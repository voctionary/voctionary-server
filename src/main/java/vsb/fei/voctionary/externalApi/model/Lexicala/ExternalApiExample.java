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
public class ExternalApiExample {
	private String text;
    @JsonProperty(value = "translations")
    private Map<String, Object> jsonTranslations;
    
    @JsonIgnore
    private List<ExternalApiTranslation> translations;
}
