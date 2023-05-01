package vsb.fei.voctionary.rest.controller;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vsb.fei.voctionary.exception.ApiRequestException;
import vsb.fei.voctionary.externalApi.service.ExternalApiService;
import vsb.fei.voctionary.model.ApiSearch;
import vsb.fei.voctionary.model.Headword;
import vsb.fei.voctionary.model.Sense;
import vsb.fei.voctionary.model.User;
import vsb.fei.voctionary.model.Word;
import vsb.fei.voctionary.model.WordList;
import vsb.fei.voctionary.model.WordListItem;
import vsb.fei.voctionary.model.WordTest;
import vsb.fei.voctionary.model.enums.Language;
import vsb.fei.voctionary.model.enums.TestType;
import vsb.fei.voctionary.rest.RestConverter;
import vsb.fei.voctionary.rest.model.RestWord;
import vsb.fei.voctionary.rest.model.RestWordList;
import vsb.fei.voctionary.rest.model.RestWordTestQuestion;
import vsb.fei.voctionary.rest.model.RestWordTestResponse;
import vsb.fei.voctionary.service.JwtService;
import vsb.fei.voctionary.service.db.ApiSearchService;
import vsb.fei.voctionary.service.db.SenseService;
import vsb.fei.voctionary.service.db.WordListService;
import vsb.fei.voctionary.service.db.WordService;
import vsb.fei.voctionary.service.db.WordTestService;

@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
public class WordController {
	
	Logger logger = LoggerFactory.getLogger(WordController.class);

	private final WordService wordService;
	private final WordListService wordListService;
	private final SenseService senseService;
	private final ApiSearchService apiSearchService;
	private final WordTestService wordTestService;
	private final ExternalApiService externalApiService;
	private final JwtService jwtService;

	@GetMapping("words")
	public ResponseEntity<List<RestWord>> getWords() {
		return new ResponseEntity<List<RestWord>>(new RestConverter().convertWordList(wordService.getWords(), true, true), HttpStatus.OK);
	}

	@GetMapping("word/{id}")
	public ResponseEntity<RestWord> getWord(@PathVariable("id") String id, HttpServletRequest request) {
		Word word = wordService.getWord(id);
		if (word == null) {
			throw new ApiRequestException(MessageFormat.format("Requested word with ID {0} does not exist", id), false);
		}
		RestWord restWord = new RestConverter().convertWord(word, true, true);
		User user = jwtService.getUserFromJwtToken(request.getCookies());
		if(user != null) {
			WordList wordList = user.getWordLists().stream().filter(list -> list.getLanguage()== word.getLanguage()).findFirst().orElse(null);
			if (wordList != null && wordList.getWordListItems().stream().anyMatch(i -> i.getWord().getId().equals(word.getId()) && i.getListed())) {
				restWord.setIsListed(true);
			}
		}
		
		return new ResponseEntity<RestWord>(restWord, HttpStatus.OK);
	}
	
	@GetMapping("randomWord")
	public ResponseEntity<RestWord> getRandomWord(@RequestParam("language") String languageKey, HttpServletRequest request, HttpServletResponse response)
			throws IOException, InterruptedException {
		Language language = Language.findByKey(languageKey);
		if(language == null) {
			throw new ApiRequestException(MessageFormat.format("Language with given key {0} does not exist or is not supported", languageKey), true);
		}
		Word randomWord = wordService.getRandomWord(language);
		Set<String> nestedRelatedEntries = new HashSet<>();
		if(randomWord == null) {
			List<Word> words = getRandomWordsNonHttp(language, 5);
			randomWord = words.get(0);
		}
		final RestWord restWord = new RestConverter().convertWord(randomWord, true, true);
		User user = jwtService.getUserFromJwtToken(request.getCookies());
		if(user != null) {
			WordList wordList = user.getWordLists().stream().filter(list -> list.getLanguage() == language).findFirst().orElse(null);
			if (wordList != null && wordList.getWordListItems().stream().anyMatch(i -> i.getWord().getId().equals(restWord.getId()) && i.getListed())) {
				restWord.setIsListed(true);
			}
		}
				
		createNestedRelatedWordsByThread(nestedRelatedEntries);
		
		return new ResponseEntity<RestWord>(restWord, HttpStatus.OK);
	}

	@GetMapping("randomWords")
	public ResponseEntity<List<RestWord>> getRandomWords(@RequestParam("language") String languageKey,
			@RequestParam("number") int number) throws IOException, InterruptedException {
		List<Word> words = getRandomWordsNonHttp(Language.findByKey(languageKey), number);

		List<RestWord> restWords = new RestConverter().convertWordList(words, true, true);

		return new ResponseEntity<List<RestWord>>(restWords, HttpStatus.OK);
	}

	@GetMapping("findWord")
	public ResponseEntity<List<RestWord>> findWord(@RequestParam("language") String languageKey,
			@RequestParam("word") String text, HttpServletRequest request) throws IOException, InterruptedException {
		Language language = Language.findByKey(languageKey);
		if(language == null) {
			throw new ApiRequestException(MessageFormat.format("Language with given key {0} does not exist or is not supported.", languageKey), true);
		}

		ApiSearch apiSearch = apiSearchService.getApiSearch(language, text);
		List<Word> words = new ArrayList<>();
		Set<String> nestedRelatedEntries = new HashSet<>();
		if (apiSearch == null) {
			words = externalApiService.findWord(language, text.toLowerCase());
			apiSearch = new ApiSearch();
			apiSearch.setLanguage(language);
			apiSearch.setText(text);
			if (words == null) {
				return null;
			}
			
			// firstly creating main words
			for(Word word : words) {
				Word wordDb = wordService.getWord(word.getId());
				if (wordDb == null) {
					wordDb = wordService.createWord(externalApiService.translateWordValues(word));
					nestedRelatedEntries.addAll(wordDb.getRelatedEntries());
				}
				apiSearch.getWords().add(wordDb);
			}
			
			apiSearch = apiSearchService.createApiSearch(apiSearch);
		}
		words = apiSearch.getWords();

		List<RestWord> restWords = new RestConverter().convertWordList(words, false, false);
		User user = jwtService.getUserFromJwtToken(request.getCookies());
		if(user != null) {
			WordList wordList = user.getWordLists().stream().filter(list -> list.getLanguage()== language).findFirst().orElse(null);
			for(RestWord restWord : restWords) {
				if (wordList != null && wordList.getWordListItems().stream().anyMatch(i -> i.getWord().getId().equals(restWord.getId()) && i.getListed())) {
					restWord.setIsListed(true);
				}
			}
		}

		// after that creating nested related words (they are not necessary immediately ---> using new Thread)
		createNestedRelatedWordsByThread(nestedRelatedEntries);
		
		return new ResponseEntity<List<RestWord>>(restWords, HttpStatus.OK);
	}
	
	@GetMapping("languages")
	public ResponseEntity<HashMap<String, List<Language>>> getLanguages(HttpServletResponse response) {
		HashMap<String, List<Language>> hashMap = new HashMap<>();
		hashMap.put("to", Arrays.asList(Language.ENGLISH));
		hashMap.put("from", Arrays.asList(Language.values()));
	
		return new ResponseEntity<HashMap<String, List<Language>>>(hashMap, HttpStatus.OK);
	}
	
	private void createNestedRelatedWordsByThread(Set<String> nestedRelatedEntries){
		if(nestedRelatedEntries == null || nestedRelatedEntries.isEmpty()) {
			return;
		}
		
		Thread thread = new Thread(() -> {
			try {
				createNestedRelatedWords(nestedRelatedEntries);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
	
	private synchronized Word createWordWithRelatedWords(Word word) throws IOException, InterruptedException {
		word = wordService.createWord(externalApiService.translateWordValues(word));
		
		if(word.getRelatedEntries() != null) {
			List<Word> relatedWords  = new ArrayList<>();
			for(String relatedEntry : word.getRelatedEntries() ) {
				Word wordDb = wordService.getWord(relatedEntry);
				if(wordDb == null) {
					wordDb = wordService.createWord(externalApiService.translateWordValues(externalApiService.getWord(relatedEntry)));
				}
				relatedWords.add(wordDb);
			}
			word.setRelatedWords(relatedWords);
		}
		
		return word;
	}
	
	private synchronized void createNestedRelatedWords(Set<String> relatedEntries) throws IOException, InterruptedException {
		if(relatedEntries == null) {
			return;
		}
		
		for(String relatedEntry : relatedEntries) {
			if(!wordService.exists(relatedEntry)) {
				Word word = createWordWithRelatedWords(externalApiService.getWord(relatedEntry));
				createNestedRelatedWords(new HashSet<>(word.getRelatedEntries()));
			}
		}
	}


	@GetMapping("getWordLists")
	public ResponseEntity<List<RestWordList>> getWordLists(HttpServletRequest request)
			throws IOException, InterruptedException {
		
		User user = jwtService.getUserFromJwtToken(request.getCookies());
		if(user == null) {
			return new ResponseEntity<List<RestWordList>>(new ArrayList<>(), HttpStatus.OK);
		}
		List<WordList> wordLists = user.getWordLists();

		List<RestWordList> restWordLists = (new RestConverter().convertWordListEntities(wordLists, false));

		return new ResponseEntity<List<RestWordList>>(restWordLists, HttpStatus.OK);
	}
	
	@GetMapping("getWordList/{id}")
	public ResponseEntity<RestWordList> getWordList(@PathVariable("id") Long id, HttpServletRequest request)
			throws IOException, InterruptedException {
		User user = jwtService.getUserFromJwtToken(request.getCookies());
		if(user == null) {
			return new ResponseEntity<RestWordList>(new RestWordList(), HttpStatus.OK);
		}
		WordList wordList = user.getWordLists().stream().filter(w -> w.getId() == id).findFirst().orElse(null);

		if (wordList == null) {
			throw new ApiRequestException("List does not exist", true);
		}
		
		List<WordListItem> toRemove = new ArrayList<>();
		wordList.getWordListItems().forEach(w -> {
			if(!w.getListed()) {
				toRemove.add(w);
			}
		});
		wordList.getWordListItems().removeAll(toRemove);

		RestWordList restWordList = (new RestConverter().convertWordListEntity(wordList, true));

		return new ResponseEntity<RestWordList>(restWordList, HttpStatus.OK);
	}
	
	
	@PostMapping("addWordToList")
	public void addWordToList(@RequestParam String wordId, HttpServletRequest request) throws IOException, InterruptedException {
		Word word = wordService.getWord(wordId);
		if (word == null) {
			throw new ApiRequestException(MessageFormat.format("Failed to add the word {0} to the list because such word does not exist.", wordId), true);
		}
		
		User user = jwtService.getUserFromJwtToken(request.getCookies());
		if(user == null) {
			throw new ApiRequestException("You have not access to this wordlist", true);
		}
		WordList wordList = user.getWordLists().stream().filter(w -> w.getLanguage() == word.getLanguage()).findFirst().orElse(null);
		if (wordList == null) {
			wordList = new WordList(user, word.getLanguage());
			wordListService.createWordList(wordList);
			wordListService.addWordToList(new WordListItem(word, wordList));
		} else {
			WordListItem wordListItem = wordList.getWordListItems().stream().filter(l -> l.getWord().getId().equals(word.getId())).findFirst().orElse(null);
			if(wordListItem == null) {
				wordListService.addWordToList(new WordListItem(word, wordList));
			}
			else {
				wordListService.listWord(wordListItem, true);
			}
		}
	}

	
	@DeleteMapping("removeWordFromList")
	public ResponseEntity<RestWordList> removeWordFromList(@RequestParam String wordId, HttpServletRequest request) throws IOException, InterruptedException {
		Word word = wordService.getWord(wordId);
		if (word == null) {
			throw new ApiRequestException("Word does not exist", true);
		}
		
		User user = jwtService.getUserFromJwtToken(request.getCookies());
		if(user == null) {
			throw new ApiRequestException("You have not access to this wordlist", true);
		}
		
		WordList wordList = user.getWordLists().stream().filter(w -> w.getLanguage() == word.getLanguage()).findFirst().orElse(null);
		if (wordList == null) {
			throw new ApiRequestException("Wordlist does not exist", true);
		}
		
		WordListItem wordListItem = wordList.getWordListItems().stream().filter(l -> l.getWord().getId().equals(word.getId())).findFirst().orElse(null);
		if(wordListItem == null) {
			throw new ApiRequestException("The wordlist does not contains the word", true);
		}
		
		wordList = wordListService.listWord(wordListItem, false);
		
		wordList.setWordListItems(wordList.getWordListItems().stream().filter(i -> i.getListed()).collect(Collectors.toList()));
		
		RestWordList restWordList= (new RestConverter().convertWordListEntity(wordList, true));
		
		return new ResponseEntity<RestWordList>(restWordList, HttpStatus.OK);
	}

	
	@PutMapping("learnWord")
	public ResponseEntity<RestWordList> learnWord(@RequestParam String wordId, @RequestParam boolean isLearned, HttpServletRequest request) throws IOException, InterruptedException {
		Word word = wordService.getWord(wordId);
		if (word == null) {
			throw new ApiRequestException("Word does not exist", true);
		}
		
		User user = jwtService.getUserFromJwtToken(request.getCookies());
		if(user == null) {
			throw new ApiRequestException("You have not access to this wordlist", true);
		}
		
		WordList wordList = user.getWordLists().stream().filter(w -> w.getLanguage() == word.getLanguage()).findFirst().orElse(null);
		if (wordList == null) {
			throw new ApiRequestException("Wordlist does not exist", true);
		} 
		
		WordListItem wordListItem = wordList.getWordListItems().stream().filter(l -> l.getWord().getId().equals(word.getId())).findFirst().orElse(null);
		if(wordListItem == null) {
			throw new ApiRequestException("The wordlist does not contains the word", true);
		}
		wordList = wordListService.learnWord(wordListItem, isLearned);
		
		wordList.setWordListItems(wordList.getWordListItems().stream().filter(i -> i.getListed()).collect(Collectors.toList()));
		
		RestWordList restWordList= (new RestConverter().convertWordListEntity(wordList, true));
		
		return new ResponseEntity<RestWordList>(restWordList, HttpStatus.OK);
	}
	
	
	@GetMapping("startTest")
	public ResponseEntity<RestWordTestQuestion> startTest(@RequestParam("listId") long listId,
			@RequestParam("testType") String testType, HttpServletRequest request)
			throws IOException, InterruptedException {		
		User user = jwtService.getUserFromJwtToken(request.getCookies());
		if(user == null) {
			throw new ApiRequestException("User does not exist", true);
		}
		
		TestType wordTestType = TestType.valueOf(testType);
		if(wordTestType == null) {
			throw new ApiRequestException("Wrong WordTestType", true);
		}
		
		WordList wordList = user.getWordLists().stream().filter(l -> l.getId() == listId).findFirst().orElse(null);
		if(wordList == null) {
			throw new ApiRequestException("Wordlist does not exist", true);
		}

		if(wordList.getWordListItems().isEmpty()) {
			throw new ApiRequestException("List has no words", false);
		}
		
		WordListItem testedWordListItem = getRandomTestableWordListItem(wordList.getWordListItems());
		if(testedWordListItem == null) {
			throw new ApiRequestException("There is no testable word in the list", false);
		}
		Collections.shuffle(testedWordListItem.getWord().getSenses());
		Sense testedSense = testedWordListItem.getWord().getSenses().stream().filter(s -> s.getDefinition() != null).findFirst().orElse(null);
		
		List<Sense> senses = wordService.getRandomTestableSenses(testedWordListItem.getWord());
		if(senses == null) {
			throw new ApiRequestException("No words", true);
		}
		
		WordTest wordTest = null;
		LinkedHashMap<String, String> responses = new LinkedHashMap<>();
		StringBuffer question= new StringBuffer();  
		
		if(wordTestType == TestType.HEADWORD) {
			
			if(testedSense.getWord().getLanguage() == Language.ENGLISH) {
				question.append(testedSense.getDefinition());
			}
			else {
				question.append(testedSense.getEnglishTranslationDefinition() + " (" + testedSense.getEnglishTranslationWords().get(0) + ")");
			}
			
			String correctResponse;
			List<String> wrongResponses = new ArrayList<>(2);
			
			StringBuffer str = new StringBuffer();
			for (Headword headword : testedSense.getWord().getHeadwords()) {
				str.append(headword.getText()+ " ");
			}
			str.deleteCharAt(str.length()-1);  
			responses.put(testedSense.getId(), str.toString());
			correctResponse = testedSense.getId();
			
			for(Sense sense : senses) {
				str = new StringBuffer();
				for (Headword headword : sense.getWord().getHeadwords()) {
					str.append(headword.getText()+ " ");
				}
				str.deleteCharAt(str.length()-1);  
				responses.put(sense.getId(), str.toString());
				wrongResponses.add(sense.getId());
			}
			
			wordTest = new WordTest(testedWordListItem, wordTestType, correctResponse, wrongResponses.get(0), wrongResponses.get(1));
		}
		else if(wordTestType == TestType.SENSE) {
			for (Headword headword : testedSense.getWord().getHeadwords()) {
				question.append(headword.getText()+ " ");
			}
			question.deleteCharAt(question.length()-1);
			
			String correctResponse = testedSense.getId();
			List<String> wrongResponses = new ArrayList<>(2);
			
			if(testedSense.getWord().getLanguage() == Language.ENGLISH) {
				responses.put(testedSense.getId(), testedSense.getDefinition()); 
				senses.forEach(s -> {
					responses.put(s.getId(), s.getDefinition());
					wrongResponses.add(s.getId());
				}); 
			}
			else {
				responses.put(testedSense.getId(), testedSense.getEnglishTranslationDefinition() + " (" + testedSense.getEnglishTranslationWords().get(0) + ")");
				senses.forEach(s ->{
					responses.put(s.getId(), s.getEnglishTranslationDefinition() + " (" + s.getEnglishTranslationWords().get(0) + ")");
					wrongResponses.add(s.getId());
				});
			}
						
			wordTest = new WordTest(testedWordListItem, wordTestType, correctResponse, wrongResponses.get(0), wrongResponses.get(1));
		}
		else {
			question.append(testedSense.getDefinition());
			StringBuffer str = new StringBuffer();
			for (Headword headword : testedSense.getWord().getHeadwords()) {
				str.append(headword.getText()+ " ");
			}
			str.deleteCharAt(str.length()-1);  
			responses.put(testedSense.getWord().getId(), str.toString());
			
			Object[] keys = responses.keySet().toArray();
			
			wordTest = new WordTest(testedWordListItem, wordTestType, (String) keys[0], null, null);
		}
		
		wordTest = wordTestService.createWordTest(wordTest);
		
		// Responses shuffling
        // convert the map to a list of Map.Entry objects
        List<Map.Entry<String, String>> list = new ArrayList<>(responses.entrySet());
        // shuffle the list
        Collections.shuffle(list);
        // create a new LinkedHashMap to store the shuffled entries
        LinkedHashMap<String, String> shuffledResponses = new LinkedHashMap<>();
        // add the shuffled entries to the new map
        for (Map.Entry<String, String> entry : list) {
        	shuffledResponses.put(entry.getKey(), entry.getValue());
        }

		RestWordTestQuestion restWordTestQuestion = new RestWordTestQuestion();
		restWordTestQuestion.setQuestion(question.toString());
		restWordTestQuestion.setPartOfSpeech(testedSense.getWord().getPartOfSpeech());
		restWordTestQuestion.setResponses(shuffledResponses);		
		restWordTestQuestion.setTestId(wordTest.getId());
		
		return new ResponseEntity<RestWordTestQuestion>(restWordTestQuestion, HttpStatus.OK);
	}
	
	@GetMapping("finishTest")
	public ResponseEntity<RestWordTestResponse> finishTest(@RequestParam("testId") long testId, @RequestParam("response") String response, HttpServletRequest request)
			throws IOException, InterruptedException {		
		User user = jwtService.getUserFromJwtToken(request.getCookies());
		if(user == null) {
			throw new ApiRequestException("User does not exist", true);
		}
				
		WordTest wordTest = wordTestService.getWordTest(testId);
		if (wordTest == null) {
			throw new ApiRequestException("Test does not exist", true);
		}
		if (wordTest.getFinishedAt() != null) {
			throw new ApiRequestException("Test is finished", true);
		}
		if (!wordTest.getWordListItem().getWordList().getUser().getEmail().equals(user.getEmail())) {
			throw new ApiRequestException("You are not authorized", true);
		}
		
		if(response.equals(wordTest.getResponseOne())) {
			wordTest.setSuccess(true);
		}		

		wordTest = wordTestService.finishWordTest(wordTest);
		
		RestWordTestResponse restResponse = new RestWordTestResponse();
		restResponse.setTestId(wordTest.getId());
		restResponse.setSuccess(wordTest.getSuccess());
		LinkedHashMap<String, String> responses = new LinkedHashMap<>();
		Sense sense = senseService.getSense(wordTest.getResponseOne());
		responses.put(sense.getId(), sense.getWord().getId());
		sense = senseService.getSense(wordTest.getResponseTwo());
		responses.put(sense.getId(), sense.getWord().getId());
		sense = senseService.getSense(wordTest.getResponseThree());
		responses.put(sense.getId(), sense.getWord().getId());
		restResponse.setWords(responses);
		
		return new ResponseEntity<RestWordTestResponse>(restResponse, HttpStatus.OK);
	}
	
	private WordListItem getRandomTestableWordListItem(List<WordListItem> wordListItems) {
		Collections.shuffle(wordListItems);
		for(WordListItem wordListItem : wordListItems) {
			if(wordListItem.getListed() && !wordListItem.getLearned()) {
				if(!wordListItem.getWord().getSenses().isEmpty()) {
					for(Sense sense : wordListItem.getWord().getSenses()) {
						if(sense.getDefinition() != null) {
							return wordListItem;
						}
					}
				}
			}
		}
		return null;
	}

	/** Function to get random words from the External API Lexicala. Words will be stored in the DB.
	 * 
	 * @param language
	 * @param number of words
	 * @return list of random words
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public List<Word> getRandomWordsNonHttp(Language language, int number) throws IOException, InterruptedException {
		List<Word> words = externalApiService.getRandomWords(language, number);
		
		Set<String> nestedRelatedEntries = new HashSet<>();
		for(Word word : words) {
			Word wordDb = wordService.getWord(word.getId());
			if(wordDb == null) {
				// firsly creating main word with related words
				word = createWordWithRelatedWords(externalApiService.translateWordValues(word));
				// after that creating nested related words (they are not necessary immediately ---> using new Thread, at the end of the method)
				for(Word relatedWord : word.getRelatedWords()) {
					nestedRelatedEntries.addAll(relatedWord.getRelatedEntries());
				}
			}
			else {
				word = wordDb;
			}
		}	
		
		createNestedRelatedWordsByThread(nestedRelatedEntries);

		return words;
	}
	
	@GetMapping("tests")
	public ResponseEntity<List<WordTest>> getTests(HttpServletResponse response) {
		List<WordTest> tests = wordTestService.getWordTests();
		
		for(WordTest wordTest : tests) {
			wordTest.setWordListItem(null);
		}
	
		return new ResponseEntity<List<WordTest>>(tests, HttpStatus.OK);
	}
	
}
