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
public class ExternalApiSense {
    private String id;
    private String definition;
    private String register;
    @JsonProperty(value = "semantic_category")
    private Object semanticCategoryJson;
    private String subcategory;
    private String subcategorization;
	private String see;
    @JsonProperty(value = "compositional_phrases")
    private List<ExternalApiPhrase> phrases;
    @JsonProperty(value = "translations")
    private Map<String, Object> jsonTranslations;
    private List<ExternalApiExample> examples;
    
    @JsonIgnore
    private List<ExternalApiTranslation> translations;
    
    @JsonIgnore
    private String semanticCategory;
}
