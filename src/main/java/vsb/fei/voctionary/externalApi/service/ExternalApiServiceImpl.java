package vsb.fei.voctionary.externalApi.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import vsb.fei.voctionary.exception.ApiRequestException;
import vsb.fei.voctionary.externalApi.model.Lexicala.ExternalApiResponse;
import vsb.fei.voctionary.externalApi.model.Lexicala.ExternalApiWord;
import vsb.fei.voctionary.externalApi.model.Translate.ExternalApiTranslateRequest;
import vsb.fei.voctionary.model.Example;
import vsb.fei.voctionary.model.Phrase;
import vsb.fei.voctionary.model.Sense;
import vsb.fei.voctionary.model.Word;
import vsb.fei.voctionary.model.enums.Language;

@Service
public class ExternalApiServiceImpl implements ExternalApiService {
	
	Logger logger = LoggerFactory.getLogger(ExternalApiServiceImpl.class);
	
	private String apiKey = "a6dcb17201msh1098997804663e9p1fef8ejsnc40b173dc2ab";
	private String apiKey2 = "39df4ef784msh761ca55ca1fe87fp186d46jsnfc19f4732df8";
	
	private String usingLexicalaKey = apiKey;
	private String usingLexicalaKey2 = apiKey2;
	private String usingTranslatorKey = apiKey;
	private String usingTranslatorKey2 = apiKey2;

	/** Function to find words from external API Lexicala
	 * 
	 * @param wordId
	 * @return list of words
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	public Word getWord(String wordId) throws IOException, InterruptedException {
		if( wordId == null || (wordId = StringUtils.deleteWhitespace(wordId)).isEmpty()) {
			return null;
		}

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://lexicala1.p.rapidapi.com/entries/" + wordId))
				.header("X-RapidAPI-Key", usingLexicalaKey)
				.header("X-RapidAPI-Host", "lexicala1.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();

		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		logger.info("Request of fetching the specific word with ID " + wordId + " from the External API.");
		if(response.statusCode() == 200) {
			logger.info("Fetching the specific word from the External API succeed.");
			List<Word> words = new ExternalApiConverter().covnertExternalApiWords(ExternalApiParser.parseJsonString(ExternalApiWord.class, response.body()).getWords());
			if(words == null) {
				logger.info("The word with ID " + wordId + " does not exist in the External API.");
			}
			else if(words.size() > 1) {
				logger.error("Many words.");
			}
			return words.get(0);
		}
		else{
			request = HttpRequest.newBuilder()
					.uri(URI.create("https://lexicala1.p.rapidapi.com/entries/" + wordId))
					.header("X-RapidAPI-Key", usingLexicalaKey2)
					.header("X-RapidAPI-Host", "lexicala1.p.rapidapi.com")
					.method("GET", HttpRequest.BodyPublishers.noBody())
					.build();

			response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			logger.info("Request of fetching the specific word with ID " + wordId + " from the External API.");
			if(response.statusCode() == 200) {
				logger.info("Fetching the specific word from the External API succeed.");
				List<Word> words = new ExternalApiConverter().covnertExternalApiWords(ExternalApiParser.parseJsonString(ExternalApiWord.class, response.body()).getWords());
				if(words == null) {
					logger.info("The word with ID " + wordId + " does not exist in the External API.");
				}
				else if(words.size() > 1) {
					logger.error("Many words.");
				}
				return words.get(0);
			}
			
			ObjectMapper mapper = new ObjectMapper();
			String msgError = mapper.readTree(response.body()).findPath("message").asText("");
			logger.error(MessageFormat.format("Fetching the specific word from the External API failed with code {0}. Message from server: {1}", response.statusCode(), msgError));
			throw new ApiRequestException(msgError, true);
		}
	}
		
	/** Function to find words from external API Lexicala
	 * 
	 * @param language
	 * @param text
	 * @return list of words
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	public List<Word> findWord(Language language, String text) throws IOException, InterruptedException {
		if(language == null || (text = StringUtils.trimToNull(text)) == null){
			return null;
		}
		text = URLEncoder.encode(text, "UTF-8").replaceAll("\\+", "%20");
		
		int page = 1;
		int pageLength = 30;
		int sample = 30;
		boolean morph = true;

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://lexicala1.p.rapidapi.com/search-entries?source=global&language=" + language.getKey()
						+ "&morph=" + morph + "&text=" + text + "&sample=" + sample + "&page=" + page + "&page-length="
						+ pageLength + "&analyzed=false"))
				.header("X-RapidAPI-Key", usingLexicalaKey)
				.header("X-RapidAPI-Host", "lexicala1.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();

		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		logger.info("Request of fetching words from the External API.");
		if(response.statusCode() == 200) {
			logger.info("Fetching words from the External API succeed.");
			List<Word> words = new ExternalApiConverter().covnertExternalApiWords(ExternalApiParser.parseJsonString(ExternalApiResponse.class, response.body()).getWords());
			return words;
		}
		else{
			request = HttpRequest.newBuilder()
					.uri(URI.create("https://lexicala1.p.rapidapi.com/search-entries?source=global&language=" + language.getKey()
							+ "&morph=" + morph + "&text=" + text + "&sample=" + sample + "&page=" + page + "&page-length="
							+ pageLength + "&analyzed=false"))
					.header("X-RapidAPI-Key", usingLexicalaKey2)
					.header("X-RapidAPI-Host", "lexicala1.p.rapidapi.com")
					.method("GET", HttpRequest.BodyPublishers.noBody())
					.build();

			response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			logger.info("Request of fetching words from the External API.");
			if(response.statusCode() == 200) {
				logger.info("Fetching words from the External API succeed.");
				List<Word> words = new ExternalApiConverter().covnertExternalApiWords(ExternalApiParser.parseJsonString(ExternalApiResponse.class, response.body()).getWords());
				return words;
			}
			
			ObjectMapper mapper = new ObjectMapper();
			String msgError = mapper.readTree(response.body()).findPath("message").asText("");
			logger.error(MessageFormat.format("Fetching words from the External API failed with code {0}. Message from server: {1}", response.statusCode(), msgError));
			throw new ApiRequestException(msgError, true);
		}
	}

	/** Function to get random words from external API Lexicala
	 * 
	 * @param language
	 * @param number of random words
	 * @return list of random words
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	public  List<Word> getRandomWords(Language language, int number) 	throws IOException, InterruptedException {
		if(language == null || number < 1) {
			return null;
		}
		
		int page = 1;
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://lexicala1.p.rapidapi.com/search-entries?source=global&language=" + language.getKey()
						+ "&sample=" + number + "&page=" + page + "&page-length=" + number
						+ "&analyzed=false"))
				.header("X-RapidAPI-Key", usingLexicalaKey)
				.header("X-RapidAPI-Host", "lexicala1.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		logger.info("Request of fetching random words from the External API.");
		if(response.statusCode() == 200) {
			logger.info("Fetching random words from the External API succeed.");
			List<Word> words = new ExternalApiConverter().covnertExternalApiWords(ExternalApiParser.parseJsonString(ExternalApiResponse.class, response.body()).getWords());
			return words;
		}
		else{
			request = HttpRequest.newBuilder()
					.uri(URI.create("https://lexicala1.p.rapidapi.com/search-entries?source=global&language=" + language.getKey()
							+ "&sample=" + number + "&page=" + page + "&page-length=" + number
							+ "&analyzed=false"))
					.header("X-RapidAPI-Key", usingLexicalaKey2)
					.header("X-RapidAPI-Host", "lexicala1.p.rapidapi.com")
					.method("GET", HttpRequest.BodyPublishers.noBody())
					.build();
			response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			logger.info("Request of fetching random words from the External API.");
			if(response.statusCode() == 200) {
				logger.info("Fetching random words from the External API succeed.");
				List<Word> words = new ExternalApiConverter().covnertExternalApiWords(ExternalApiParser.parseJsonString(ExternalApiResponse.class, response.body()).getWords());
				return words;
			}
			
			ObjectMapper mapper = new ObjectMapper();
			String msgError = mapper.readTree(response.body()).findPath("message").asText("");
			logger.error(MessageFormat.format("Fetching random words from the External API failed with code {0}. Message from server: {1}", response.statusCode(), msgError));
			throw new ApiRequestException(msgError, true);
		}
	}
	
	/** Function to get random word from external API Lexicala
	 * 
	 * @param language
	 * @param number of random words
	 * @return random word
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	public Word getRandomWord(Language language) throws IOException, InterruptedException {
		return getRandomWords(language, 1).get(0);
	}
	
	/** Function to translate text from the specific language to the other language
	 * 
	 * @param from language
	 * @param to language
	 * @param text to translate
	 * @return translated text
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	public String translate(Language from, Language to, String text) throws IOException, InterruptedException {
		return translate(from, to, Arrays.asList(text)).get(0);		
	}
	
	
	/** Function to translate texts from the specific language to the other language
	 * 
	 * @param from language
	 * @param to language
	 * @param texts to translate
	 * @return translated texts
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	public List<String> translate(Language from, Language to, List<String> texts) throws IOException, InterruptedException {
		if(from == null || to == null || texts == null || texts.isEmpty() || texts.contains(null)) {
			return new ArrayList<>();
		}
		
		ExternalApiTranslateRequest translateRequest = new ExternalApiTranslateRequest();
		translateRequest.setLanguageFrom(from.getAlternateKey());
		translateRequest.setLanguageTo(to.getAlternateKey());
		translateRequest.setTexts(texts);
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(translateRequest);
		
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://rapid-translate-multi-traduction.p.rapidapi.com/t"))
				.header("content-type", "application/json")
				.header("X-RapidAPI-Key", usingTranslatorKey)
				.header("X-RapidAPI-Host", "rapid-translate-multi-traduction.p.rapidapi.com")
				.method("POST", HttpRequest.BodyPublishers.ofString(json))
				.build();
						
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		logger.info("Request of translating text by the External API.");
		if(response.statusCode() == 200) {
			String[] translatedTexts = mapper.readValue(response.body(), String[].class);
			if(translatedTexts == null || translatedTexts.length == 0) {
				logger.error("Translating text by the External API failed. There is no translated result in the response");
				return null;
			}
			logger.info("Translating text by the External API succeed.");
			return List.of(translatedTexts);
		}
		else {
			request = HttpRequest.newBuilder()
					.uri(URI.create("https://rapid-translate-multi-traduction.p.rapidapi.com/t"))
					.header("content-type", "application/json")
					.header("X-RapidAPI-Key", usingTranslatorKey2)
					.header("X-RapidAPI-Host", "rapid-translate-multi-traduction.p.rapidapi.com")
					.method("POST", HttpRequest.BodyPublishers.ofString(json))
					.build();
							
			response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			logger.info("Request of translating text by the External API.");
			if(response.statusCode() == 200) {
				String[] translatedTexts = mapper.readValue(response.body(), String[].class);
				if(translatedTexts == null || translatedTexts.length == 0) {
					logger.error("Translating text by the External API failed. There is no translated result in the response");
					return null;
				}
				logger.info("Translating text by the External API succeed.");
				return List.of(translatedTexts);
			}
			
			String msgError = mapper.readTree(response.body()).findPath("message").asText("");
			logger.error(MessageFormat.format("Translating text by the External API failed with code {0}. Message from server: {1}", response.statusCode(), msgError));
			throw new ApiRequestException(msgError, true);
		}
	}

	
	/** Function to translate all necessary Word attributes, because some attributes provided by the External API are not in the desired English language (for example Italian word has only examples in Italian language)
	 * 
	 * @param word whose attributes will be update
	 * @return word with translated attributed
	 */
	@Override
	public Word translateWordValues(Word word) throws IOException, InterruptedException {
		if(word.getLanguage() == Language.ENGLISH) {
			return word;
		}
		
		List<String> texts = new ArrayList<>();
		if(word.getSenses() != null) {
			for(Sense sense : word.getSenses()) {
				if(sense.getDefinition() != null) {
					texts.add(sense.getDefinition());
				}
				
				if(sense.getExamples() != null) {
					for(Example example : sense.getExamples()) {
						if(example.getEnglishTranslation() == null 	&& example.getText() != null) {
							texts.add(example.getText());
						}
					}
				}
					
				if(sense.getPhrases() != null) {
					for(Phrase phrase : sense.getPhrases()) {
						if(phrase.getEnglishTranslationText() == null && phrase.getText() != null) {
							texts.add(phrase.getText());
						}
						
						if(phrase.getDefinition() != null) {
							texts.add(phrase.getDefinition());
						}
						
						if(phrase.getExamples() != null) {
							for(Example example : phrase.getExamples()) {
								if(example.getEnglishTranslation() == null	&& example.getText() != null) {
									texts.add(example.getText());
								}
							}
						}
					}
				}
			}
		}
		
		texts = checkDotComplication(translate(word.getLanguage(), Language.ENGLISH, texts));
		if(texts != null && word.getSenses() != null) {
			for(Sense sense : word.getSenses()) {
				if(sense.getDefinition() != null) {
					sense.setEnglishTranslationDefinition(texts.remove(0));
				}
				
				if(sense.getExamples() != null) {
					for(Example example : sense.getExamples()) {
						if(example.getEnglishTranslation() == null 	&& example.getText() != null) {
							example.setEnglishTranslation(texts.remove(0));
						}
					}
				}
					
				if(sense.getPhrases() != null) {
					for(Phrase phrase : sense.getPhrases()) {
						if(phrase.getEnglishTranslationText() == null && phrase.getText() != null) {
							phrase.setEnglishTranslationText(texts.remove(0));
						}
						
						if(phrase.getDefinition() != null) {
							phrase.setEnglishTranslationDefinition(texts.remove(0));
						}
						
						if(phrase.getExamples() != null) {
							for(Example example : phrase.getExamples()) {
								if(example.getEnglishTranslation() == null	&& example.getText() != null) {
									example.setEnglishTranslation(texts.remove(0));
								}
							}
						}
					}
				}
			}
		}
		return word;
	}

	private LinkedList<String> checkDotComplication(final List<String> translatedTexts) {
		LinkedList<String> returnList = new LinkedList<>();
		for(String text : translatedTexts) {
			if(text.contains("<i>") && text.contains("<b>")) {
				// Match and extract the content between <b> tags
				Pattern pattern = Pattern.compile("<b>(.*?)</b>");
				Matcher matcher = pattern.matcher(text);
				StringBuilder string = new StringBuilder();
				while (matcher.find()) {
				    string.append(matcher.group(1)).append(" ");
				}
				returnList.add(string.toString());
			}
			else {
				returnList.add(text);
			}
		}
		
		return returnList;
	}

}
