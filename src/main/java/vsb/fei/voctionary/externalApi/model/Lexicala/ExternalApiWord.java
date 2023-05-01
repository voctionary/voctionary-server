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
public class ExternalApiWord {
    private String id;
    private String language;
    @JsonProperty(value = "related_entries")
    private List<String> relatedEntries;
    private Object headword;
    private List<ExternalApiSense> senses;
    
    @JsonIgnore
    private List<ExternalApiHeadword> headwords;
}
