package vsb.fei.voctionary.rest.model;

import java.util.LinkedHashMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestWordTestQuestion {
	
	private long testId;
	private String question;
	private String partOfSpeech;
	private LinkedHashMap<String, String> responses;
	
}
