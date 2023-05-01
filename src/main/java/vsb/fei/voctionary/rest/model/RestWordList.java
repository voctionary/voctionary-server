package vsb.fei.voctionary.rest.model;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vsb.fei.voctionary.model.enums.Language;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestWordList {

	private Long id;
	private Instant updatedAt;
	private Instant createdAt;
    private RestUser user;
	private List<RestWordListItem> wordListItems;
	private int count = 0;
	private Language language;
	
}