package vsb.fei.voctionary.externalApi.model.Lexicala;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalApiPhrase {
    private String text;
    private String definition;
    @JsonProperty(value = "pos")
    private String partOfSpeech;
    private String register;
    @JsonProperty(value = "semantic_category")
    private String semanticCategory;
    @JsonProperty(value = "translations")
    private Map<String, Object> jsonTranslations;
    private List<ExternalApiExample> examples;
    
    @JsonIgnore
    private List<ExternalApiTranslation> translations;

}
