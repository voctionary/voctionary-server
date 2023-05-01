package vsb.fei.voctionary.externalApi.model.Lexicala;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalApiResponse {
    @JsonProperty(value = "n_results")
    private int numberOfResults;
    @JsonProperty(value = "page_number")
    private int pageNumber;
    @JsonProperty(value = "results_per_page")
    private int resultsPerPage;
    @JsonProperty(value = "n_pages")
    private int numberOfPages;
    @JsonProperty(value = "available_n_pages")
    private int availableNumberOfPages;
    @JsonProperty(value = "results")
    private List<ExternalApiWord> words;
}
