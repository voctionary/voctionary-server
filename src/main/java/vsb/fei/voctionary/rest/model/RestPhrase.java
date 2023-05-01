package vsb.fei.voctionary.rest.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestPhrase {

	private Long id;
	private String text;
	private String definition;
	private String partOfSpeech;
	private String englishTranslationText;
	private String englishTranslationDefinition;
	private String register;
	private String semanticCategory;
	private List<RestExample> examples;
	
}
