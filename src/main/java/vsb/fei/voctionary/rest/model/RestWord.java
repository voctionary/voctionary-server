package vsb.fei.voctionary.rest.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vsb.fei.voctionary.model.enums.Language;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestWord {

	private String id;
	private Language language;
	private String partOfSpeech;
	private List<RestWord> relatedWords;
	private List<RestHeadword> headwords;
	private List<RestSense> senses;
	private Boolean isListed = false;
	
}