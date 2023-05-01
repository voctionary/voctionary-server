package vsb.fei.voctionary.rest.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestWordListItem {

	private Long id;
	private Instant createdAt;
	@JsonProperty(value = "isListed")
	private Boolean listed;
	@JsonProperty(value = "isLearned")
	private Boolean learned = false;
	private Instant lastTestedAt;
	private Integer allTests = 0;
	private Integer successfulTests = 0;
	private RestWord word;
	
}
