package vsb.fei.voctionary.externalApi.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import vsb.fei.voctionary.externalApi.model.Lexicala.ExternalApiExample;
import vsb.fei.voctionary.externalApi.model.Lexicala.ExternalApiHeadword;
import vsb.fei.voctionary.externalApi.model.Lexicala.ExternalApiPhrase;
import vsb.fei.voctionary.externalApi.model.Lexicala.ExternalApiPronunciation;
import vsb.fei.voctionary.externalApi.model.Lexicala.ExternalApiResponse;
import vsb.fei.voctionary.externalApi.model.Lexicala.ExternalApiSense;
import vsb.fei.voctionary.externalApi.model.Lexicala.ExternalApiTranslation;
import vsb.fei.voctionary.externalApi.model.Lexicala.ExternalApiWord;

public class ExternalApiParser {

	private static final ObjectMapper mapper = new ObjectMapper();

	public static ExternalApiResponse parseJsonString(Class<?> clazz, String jsonString) throws IOException, InterruptedException {
		if(jsonString == null) {
			return null;
		}
		
		ExternalApiResponse response;
		if(clazz == ExternalApiResponse.class) {
			response = mapper.readValue(jsonString, ExternalApiResponse.class);
		}
		else if(clazz == ExternalApiWord.class) {
			response = new ExternalApiResponse();
			ExternalApiWord word = mapper.readValue(jsonString, ExternalApiWord.class);
			response.setWords(Arrays.asList(word));
		}
		else {
			return null;
		}
		
		if (response.getWords() != null) {
			for (ExternalApiWord word : response.getWords()) {
				word.setHeadwords(parseHeadwordsFromObject(word.getHeadword()));

				if (word.getSenses() != null) {
					for (ExternalApiSense sense : word.getSenses()) {
						sense.setTranslations(parseTranslationsFromMap(sense.getJsonTranslations()));
						
						if(sense.getSemanticCategoryJson() instanceof List) {
							sense.setSemanticCategory(mapper.convertValue(((List<?>) sense.getSemanticCategoryJson()).get(0), String.class));
						}
						else if(sense.getSemanticCategoryJson() instanceof String){
							sense.setSemanticCategory(mapper.convertValue(sense.getSemanticCategoryJson(), String.class));
						}

						if (sense.getExamples() != null) {
							for (ExternalApiExample example : sense.getExamples()) {
								example.setTranslations(parseTranslationsFromMap(example.getJsonTranslations()));
							}
						}

						if (sense.getPhrases() != null) {
							for (ExternalApiPhrase phrase : sense.getPhrases()) {
								phrase.setTranslations(parseTranslationsFromMap(phrase.getJsonTranslations()));

								if (phrase.getExamples() != null) {
									for (ExternalApiExample example : phrase.getExamples()) {
										example.setTranslations(
												parseTranslationsFromMap(example.getJsonTranslations()));
									}
								}
							}
						}
					}
				}
			}
		}

		return response;
	}

	private static List<ExternalApiHeadword> parseHeadwordsFromObject(Object object) {
		if (object == null) {
			return null;
		}

		List<ExternalApiHeadword> headwords = new ArrayList<>();
		if (object instanceof List) {
			for (Object obj : (List<?>) object) {
				ExternalApiHeadword headword = mapper.convertValue(obj, ExternalApiHeadword.class);
				headword.setSubcategory(parseSubcategoryFromObject(headword.getJsonSubcategory()));
				headword.setPronunciations(parsePronunciationsFromObject(headword.getPronunciation()));
				headwords.add(headword);
			}
		} else {
			ExternalApiHeadword headword = mapper.convertValue(object, ExternalApiHeadword.class);
			headword.setPronunciations(parsePronunciationsFromObject(headword.getPronunciation()));
			headwords.add(headword);
		}
		return headwords;
	}

	private static List<ExternalApiPronunciation> parsePronunciationsFromObject(Object object) {
		if (object == null) {
			return null;
		}

		List<ExternalApiPronunciation> pronunciations = new ArrayList<>();
		if (object instanceof List) {
			for (Object obj : (List<?>) object) {
				ExternalApiPronunciation pronunciation = mapper.convertValue(obj, ExternalApiPronunciation.class);
				pronunciations.add(pronunciation);
			}
		} else {
			ExternalApiPronunciation pronunciation = mapper.convertValue(object, ExternalApiPronunciation.class);
			pronunciations.add(pronunciation);
		}
		return pronunciations;
	}

	private static String parseSubcategoryFromObject(Object object) {
		if (object == null) {
			return null;
		}

		if (object instanceof List) {
			for (Object obj : (List<?>) object) {
				return mapper.convertValue(obj, String.class);
			}
		} else {
			return mapper.convertValue(object, String.class);
		}
		
		return null;
	}
	
	private static List<ExternalApiTranslation> parseTranslationsFromMap(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		List<ExternalApiTranslation> translations = new ArrayList<>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof List) {
				for (Object obj : (List<?>) value) {
					ExternalApiTranslation translation = mapper.convertValue(obj, ExternalApiTranslation.class);
					translation.setLanguage(key);
					translations.add(translation);
				}
			} else {
				ExternalApiTranslation translation = mapper.convertValue(value, ExternalApiTranslation.class);
				translation.setLanguage(key);
				translations.add(translation);
			}
		}
		return translations;
	}
}
