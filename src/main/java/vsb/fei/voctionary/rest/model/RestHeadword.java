package vsb.fei.voctionary.rest.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestHeadword {

	private Long id;
	private String text;
	private String partOfSpeech;
	private String gender;
	private String number;
	private String subcategorization;
	private String subcategory;
	private String register;
	private List<String> pronunciations;
	
}
