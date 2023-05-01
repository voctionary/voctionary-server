package vsb.fei.voctionary.externalApi.model.Lexicala;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalApiHeadword {
    private String text;
    @JsonProperty(value = "pos")
    private String partOfSpeech;
    private String gender;
    private String number;
    private String register;
    @JsonProperty(value = "subcategory")
    private Object jsonSubcategory;
    private String subcategorization;
    private Object pronunciation;
    
    @JsonIgnore
    private List<ExternalApiPronunciation> pronunciations;;
    @JsonIgnore
    private String subcategory;
}
