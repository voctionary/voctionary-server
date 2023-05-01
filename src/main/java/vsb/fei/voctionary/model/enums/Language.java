package vsb.fei.voctionary.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Language {
    CHINESE("zh", "zh-CN","Chinese"),
    DANISH("dk", "da", "Danish"),
    DUTCH("nl", "nl", "Dutch"), 
    ENGLISH("en", "en", "English"), 
    FRENCH("fr", "fr", "French"), 
    GERMAN("de", "de", "German"), 
    ITALIAN("it", "it", "Italian"), 
    JAPANESE("ja", "ja", "Japanese"), 
    NORWEGIAN("no", "no", "Norwegian"),
    POLISH("pl", "pl","Polish"), 
    PORTUGUESE("br", "pt", "Portuguese"),
    SPANISH("es", "es", "Spanish"), 
    SWEDISH("sv", "sv", "Swedish");
	
    private final String key;
    private final String alternateKey; // used for Translator API
    private final String title;
    
    Language(String key, String alternateKey, String title) {
        this.key = key;
        this.alternateKey = alternateKey;
        this.title = title;
    }

	public static Language findByKey(String key) {
		for(Language language : Language.values()) {
			if(language.key.equals(key)) {
				return language;
			}
		}
		return null;
	}
	
}
