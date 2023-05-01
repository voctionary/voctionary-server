package vsb.fei.voctionary.rest.model;

import java.util.LinkedHashMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestWordTestResponse {
	
	private long testId;
	private boolean success;
	private LinkedHashMap<String, String> words; // first word is correct
	
}
