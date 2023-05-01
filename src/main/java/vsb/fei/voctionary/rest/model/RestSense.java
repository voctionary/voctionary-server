package vsb.fei.voctionary.rest.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestSense {

	private String id;
	private String definition;
	private List<String> englishTranslationWords;
	private String englishTranslationDefinition;
	private String register;
	private String subcategorization;
	private String subcategory;
	private String see;
	private List<RestExample> examples;
	private List<RestPhrase> phrases;
	
}
