package vsb.fei.voctionary.rest;

import java.util.ArrayList;
import java.util.List;

import vsb.fei.voctionary.model.Example;
import vsb.fei.voctionary.model.Headword;
import vsb.fei.voctionary.model.Phrase;
import vsb.fei.voctionary.model.Sense;
import vsb.fei.voctionary.model.User;
import vsb.fei.voctionary.model.Word;
import vsb.fei.voctionary.model.WordList;
import vsb.fei.voctionary.model.WordListItem;
import vsb.fei.voctionary.rest.model.RestExample;
import vsb.fei.voctionary.rest.model.RestHeadword;
import vsb.fei.voctionary.rest.model.RestPhrase;
import vsb.fei.voctionary.rest.model.RestRegistrationRequest;
import vsb.fei.voctionary.rest.model.RestSense;
import vsb.fei.voctionary.rest.model.RestUser;
import vsb.fei.voctionary.rest.model.RestWord;
import vsb.fei.voctionary.rest.model.RestWordList;
import vsb.fei.voctionary.rest.model.RestWordListItem;

public class RestConverter {
	
	public RestWord convertWord(Word word, boolean sensesIncluded, boolean relatedWordsIncluded) {
		if(word == null) {
			return null;
		}
		
		RestWord restWord  = new RestWord();
		restWord.setId(word.getId());
		restWord.setLanguage(word.getLanguage());
		restWord.setPartOfSpeech(word.getPartOfSpeech());
		restWord.setHeadwords(convertHeadwordList(word.getHeadwords()));
		
		if(sensesIncluded) {
			restWord.setSenses(convertSenseList(word.getSenses()));
		}
		if(relatedWordsIncluded) {
				restWord.setRelatedWords(convertWordList(word.getRelatedWords(), false, false));
		}
		
		return restWord;
	}
	
	public List<RestWord> convertWordList(List<Word> words, boolean sensesIncluded, boolean relatedWordsIncluded){
		if(words == null) {
			return null;
		}
		
		List<RestWord> restWords = new ArrayList<>();
		for (Word word : words) {
			restWords.add(convertWord(word, sensesIncluded, relatedWordsIncluded));
		}
		
		return restWords;
	}
	
	public RestHeadword convertHeadword(Headword headword) {
		if(headword == null) {
			return null;
		}
		
		RestHeadword restHeadword  = new RestHeadword();
		restHeadword.setId(headword.getId());
		restHeadword.setText(headword.getText());
		restHeadword.setPartOfSpeech(headword.getPartOfSpeech());
		restHeadword.setGender(headword.getGender());
		restHeadword.setNumber(headword.getNumber());
		restHeadword.setSubcategorization(headword.getSubcategorization());
		restHeadword.setSubcategory(headword.getSubcategory());
		restHeadword.setRegister(headword.getRegister());
		restHeadword.setPronunciations(headword.getPronunciations());
		return restHeadword;
	}
	
	public List<RestHeadword> convertHeadwordList(List<Headword> headwords){
		if(headwords == null) {
			return null;
		}
		
		List<RestHeadword> restHeadwords = new ArrayList<>();
		for (Headword headword : headwords) {
			restHeadwords.add(convertHeadword(headword));
		}
		
		return restHeadwords;
	}
	
	public RestSense convertSense(Sense sense) {
		if(sense == null) {
			return null;
		}
		
		RestSense restSense  = new RestSense();
		restSense.setId(sense.getId());
		restSense.setDefinition(sense.getDefinition());
		restSense.setEnglishTranslationWords(sense.getEnglishTranslationWords());
		restSense.setEnglishTranslationDefinition(sense.getEnglishTranslationDefinition());
		restSense.setRegister(sense.getRegister());
		restSense.setSubcategorization(sense.getSubcategorization());
		restSense.setSubcategory(sense.getSubcategory());
		restSense.setSee(sense.getSee());
		restSense.setExamples(convertExampleList(sense.getExamples()));
		restSense.setPhrases(convertPhraseList(sense.getPhrases()));
		
		return restSense;
	}
	
	public List<RestSense> convertSenseList(List<Sense> senses){
		if(senses == null) {
			return null;
		}
		
		List<RestSense> restSenses = new ArrayList<>();
		for (Sense sense : senses) {
			restSenses.add(convertSense(sense));
		}
		
		return restSenses;
	}

	public RestExample convertExample(Example example) {
		if(example == null) {
			return null;
		}
		
		RestExample restExample = new RestExample();
		restExample.setId(example.getId());
		restExample.setText(example.getText());
		restExample.setEnglishTranslation(example.getEnglishTranslation());
		
		return restExample;
	}
	
	public List<RestExample> convertExampleList(List<Example> examples){
		if(examples == null) {
			return null;
		}
		
		List<RestExample> restExamples = new ArrayList<>();
		for (Example example : examples) {
			restExamples.add(convertExample(example));
		}
		
		return restExamples;
	}
	
	public RestPhrase convertPhrase(Phrase phrase) {
		if(phrase == null) {
			return null;
		}
		
		RestPhrase restPhrase = new RestPhrase();
		restPhrase.setId(phrase.getId());
		restPhrase.setText(phrase.getText());
		restPhrase.setDefinition(phrase.getDefinition());
		restPhrase.setPartOfSpeech(phrase.getPartOfSpeech());
		restPhrase.setEnglishTranslationText(phrase.getEnglishTranslationText());
		restPhrase.setEnglishTranslationDefinition(phrase.getEnglishTranslationDefinition());
		restPhrase.setRegister(phrase.getRegister());
		restPhrase.setSemanticCategory(phrase.getSemanticCategory());
		restPhrase.setExamples(convertExampleList(phrase.getExamples()));
		
		return restPhrase;
	}
	
	public List<RestPhrase> convertPhraseList(List<Phrase> phrases){
		if(phrases == null) {
			return null;
		}
		
		List<RestPhrase> restPhrases = new ArrayList<>();
		for (Phrase phrase : phrases) {
			restPhrases.add(convertPhrase(phrase));
		}
		
		return restPhrases;
	}
	
	public User convertRestRegistrationRequest(RestRegistrationRequest restRegistrationRequest) {
		if(restRegistrationRequest == null) {
			return null;
		}
		
		User user = new User();
		user.setName(restRegistrationRequest.getName());
		user.setSurname(restRegistrationRequest.getSurname());
		user.setEmail(restRegistrationRequest.getEmail());
		user.setPassword(restRegistrationRequest.getPassword());
		user.setReceivingEmails(restRegistrationRequest.getReceivingEmails());
		
		return user;
	}
	
	public RestUser convertUser(User user) {
		if(user == null) {
			return null;
		}
		
		RestUser restUser = new RestUser();
		restUser.setName(user.getName());
		restUser.setSurname(user.getSurname());
		restUser.setEmail(user.getEmail());
		restUser.setReceivingEmails(user.getReceivingEmails());
		
		return restUser;
	}	
	
	public RestWordList convertWordListEntity(WordList wordList, boolean listItemsIncluded) {
		if(wordList == null) {
			return null;
		}
		
		RestWordList restWordList = new RestWordList();
		restWordList.setId(wordList.getId());
		restWordList.setCreatedAt(wordList.getCreatedAt());
		restWordList.setUpdatedAt(wordList.getUpdatedAt());
		restWordList.setUser(convertUser(wordList.getUser()));
		restWordList.setCount(wordList.getCount());
		restWordList.setLanguage(wordList.getLanguage());

		if(listItemsIncluded) {
			restWordList.setWordListItems(convertWordListItems(wordList.getWordListItems()));
		}

		return restWordList;
	}
	
	public List<RestWordList> convertWordListEntities(List<WordList> wordLists, boolean listItemsIncluded){
		if(wordLists == null) {
			return null;
		}
		
		List<RestWordList> restWordLists = new ArrayList<>();
		for (WordList wordList : wordLists) {
			restWordLists.add(convertWordListEntity(wordList, listItemsIncluded));
		}
		
		return restWordLists;
	}
	
	public RestWordListItem convertWordListItem(WordListItem wordListItem) {
		if(wordListItem == null) {
			return null;
		}
		
		RestWordListItem restWordListItem = new RestWordListItem();
		restWordListItem.setId(wordListItem.getId());
		restWordListItem.setCreatedAt(wordListItem.getCreatedAt());
		restWordListItem.setListed(wordListItem.getListed());
		restWordListItem.setLearned(wordListItem.getLearned());
		restWordListItem.setLastTestedAt(wordListItem.getLastTestedAt());
		restWordListItem.setAllTests(wordListItem.getAllTests());
		restWordListItem.setSuccessfulTests((wordListItem.getSuccessfulTests()));
		restWordListItem.setWord(convertWord(wordListItem.getWord(), false, false));
		
		return restWordListItem;
	}
	
	public List<RestWordListItem> convertWordListItems(List<WordListItem> wordListItems){
		if(wordListItems == null) {
			return null;
		}
		
		List<RestWordListItem> restWordListItems = new ArrayList<>();
		for (WordListItem wordListItem : wordListItems) {
			restWordListItems.add(convertWordListItem(wordListItem));
		}
		
		return restWordListItems;
	}

}
