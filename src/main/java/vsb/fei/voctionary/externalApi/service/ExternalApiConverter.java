package vsb.fei.voctionary.externalApi.service;

import java.util.ArrayList;
import java.util.List;

import vsb.fei.voctionary.externalApi.model.Lexicala.ExternalApiExample;
import vsb.fei.voctionary.externalApi.model.Lexicala.ExternalApiHeadword;
import vsb.fei.voctionary.externalApi.model.Lexicala.ExternalApiPhrase;
import vsb.fei.voctionary.externalApi.model.Lexicala.ExternalApiPronunciation;
import vsb.fei.voctionary.externalApi.model.Lexicala.ExternalApiSense;
import vsb.fei.voctionary.externalApi.model.Lexicala.ExternalApiTranslation;
import vsb.fei.voctionary.externalApi.model.Lexicala.ExternalApiWord;
import vsb.fei.voctionary.model.Example;
import vsb.fei.voctionary.model.Headword;
import vsb.fei.voctionary.model.Phrase;
import vsb.fei.voctionary.model.Sense;
import vsb.fei.voctionary.model.Word;
import vsb.fei.voctionary.model.enums.Language;

public class ExternalApiConverter {
	
	private Language language = null;
	
	public List<Word> covnertExternalApiWords(List<ExternalApiWord> externalApiWords) {
		if(externalApiWords == null) {
			return null;
		}
		
		List<Word> words = new ArrayList<>();
		for(ExternalApiWord externalApiWord : externalApiWords) {
			words.add(covnertExternalApiWord(externalApiWord));
		}

		return words;
	}
	
	
	public Word covnertExternalApiWord(ExternalApiWord externalApiWord) {
		if(externalApiWord == null) {
			return null;
		}
		
		Word word = new Word();
		word.setId(externalApiWord.getId());
		language = Language.findByKey(externalApiWord.getLanguage());
		word.setLanguage(language);
		word.setHeadwords(convertExternalApiHeadwords(externalApiWord.getHeadwords()));
		if(word.getHeadwords() != null) {
			for(Headword headword : word.getHeadwords()) {
				headword.setWord(word);
				if(word.getPartOfSpeech() == null && headword.getPartOfSpeech() != null) {
					word.setPartOfSpeech(headword.getPartOfSpeech());
				}
			}
		}
		
		List<Sense> senses = new ArrayList<>();
		for(ExternalApiSense externalApiSense : externalApiWord.getSenses()) {
			Sense sense = convertExternalApiSense(externalApiSense);
			sense.setWord(word);
			senses.add(sense);
		}
		word.setSenses(senses);
		
		if(externalApiWord.getRelatedEntries() != null) {
			word.setRelatedEntries(externalApiWord.getRelatedEntries());
		}

		return word;
	}


	private Sense convertExternalApiSense(ExternalApiSense externalApiSense) {
		if(externalApiSense == null) {
			return null;
		}		
		
		Sense sense = new Sense();
		sense.setDefinition(externalApiSense.getDefinition());
		sense.setId(externalApiSense.getId());
		sense.setRegister(externalApiSense.getRegister());
		sense.setSemanticCategory(externalApiSense.getSemanticCategory());
		sense.setSubcategorization(externalApiSense.getSubcategorization());
		sense.setSubcategory(externalApiSense.getSubcategory());
		sense.setSee(externalApiSense.getSee());
		
		if(externalApiSense.getExamples() != null) {
			List<Example> examples = new ArrayList<>();
			for(ExternalApiExample externalApiExample : externalApiSense.getExamples()) {
				Example example = convertExternalApiExample(externalApiExample);
				example.setSense(sense);
				examples.add(example);
			}
			sense.setExamples(examples);
		}
		
		if(externalApiSense.getPhrases() != null) {
			List<Phrase> phrases = new ArrayList<>();
			for(ExternalApiPhrase externalApiPhrase : externalApiSense.getPhrases()) {
				Phrase phrase = convertExternalApiPhrase(externalApiPhrase);
				phrase.setSense(sense);
				phrases.add(phrase);
			}
			sense.setPhrases(phrases);
		}

		sense.setEnglishTranslationWords(convertExternalApiTranslations(externalApiSense.getTranslations()));
		
		return sense;
	}

	private Phrase convertExternalApiPhrase(ExternalApiPhrase externalApiPhrase) {
		if(externalApiPhrase == null) {
			return null;
		}		
		
		Phrase phrase = new Phrase();
		phrase.setDefinition(externalApiPhrase.getDefinition());
		phrase.setPartOfSpeech(externalApiPhrase.getPartOfSpeech());
		phrase.setRegister(externalApiPhrase.getRegister());
		phrase.setSemanticCategory(externalApiPhrase.getSemanticCategory());
		phrase.setText(externalApiPhrase.getText());

		List<String> englishTranslations = convertExternalApiTranslations(externalApiPhrase.getTranslations());
		phrase.setEnglishTranslationText((englishTranslations.isEmpty() ? null : englishTranslations.get(0)));
		
		if(externalApiPhrase.getExamples() != null) {
			List<Example> examples = new ArrayList<>();
			for(ExternalApiExample externalApiExample : externalApiPhrase.getExamples()) {
				Example example = convertExternalApiExample(externalApiExample);
				example.setPhrase(phrase);
				examples.add(example);
			}
			phrase.setExamples(examples);
		}
		
		return phrase;
	}


	private List<String> convertExternalApiTranslations(List<ExternalApiTranslation> externalApiTranslations) {
		List<String> translations = new ArrayList<>();

		if(externalApiTranslations != null && language != Language.ENGLISH) {
			for(ExternalApiTranslation externalApiTranslation : externalApiTranslations) {
				if(externalApiTranslation.getLanguage().equals(Language.ENGLISH.getKey())) {
					translations.add(externalApiTranslation.getText());
				}
			}
		}
		
		return translations;
	}


	private Example convertExternalApiExample(ExternalApiExample externalApiExample) {
		if(externalApiExample == null) {
			return null;
		}
		
		Example example = new Example();
		example.setText(externalApiExample.getText());
		List<String> englishTranslations = convertExternalApiTranslations(externalApiExample.getTranslations());
		example.setEnglishTranslation(englishTranslations.isEmpty() ? null : englishTranslations.get(0));
		
		return example;
	}


	private List<Headword> convertExternalApiHeadwords(List<ExternalApiHeadword> externalApiHeadwords) {
		List<Headword> headwords = new ArrayList<>();

		if(externalApiHeadwords != null) {
			for(ExternalApiHeadword externalApiHeadword : externalApiHeadwords) {
				Headword headword = new Headword();
				headword.setGender(externalApiHeadword.getGender());
				headword.setNumber(externalApiHeadword.getNumber());
				headword.setPartOfSpeech(externalApiHeadword.getPartOfSpeech());
				headword.setRegister(externalApiHeadword.getRegister());
				headword.setSubcategorization(externalApiHeadword.getSubcategorization());
				headword.setSubcategory(externalApiHeadword.getSubcategory());
				headword.setText(externalApiHeadword.getText());
				headword.setPronunciations(convertExternalApiPronunciations(externalApiHeadword.getPronunciations()));
				headwords.add(headword);
			}
		}

		return headwords;
	}
	
	private List<String> convertExternalApiPronunciations(List<ExternalApiPronunciation> externalApiPronunciations){
		List<String> pronunciations = new ArrayList<>();
		
		if(externalApiPronunciations != null) {
			for(ExternalApiPronunciation externalApiPronunciation : externalApiPronunciations) {
				pronunciations.add(externalApiPronunciation.getValue());
			}
		}
		
		return pronunciations;
	}
	
}
