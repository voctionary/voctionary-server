package vsb.fei.voctionary.externalApi.model.Lexicala;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalApiPronunciation {
    private String value;
}
