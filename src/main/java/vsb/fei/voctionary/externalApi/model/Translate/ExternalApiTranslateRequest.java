package vsb.fei.voctionary.externalApi.model.Translate;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalApiTranslateRequest {

    @JsonProperty(value = "from")
    private String languageFrom;
    @JsonProperty(value = "to")
    private String languageTo;
    @JsonProperty(value = "e")
    private final String e = "";
    @JsonProperty(value = "q")
    private List<String> texts;
}
