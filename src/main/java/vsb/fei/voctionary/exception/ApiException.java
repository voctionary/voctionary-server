package vsb.fei.voctionary.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class ApiException {

	private final int httpStatusCode;
	private final HttpStatus httpStatus;
	private final String message;
	@JsonProperty(value = "isError")
    private final boolean error;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private final LocalDateTime dateTime;
	
	public ApiException(HttpStatus httpStatus, String message, boolean error, LocalDateTime dateTime) {
		this.httpStatus = httpStatus;
		this.message = message;
		this.error = error;
		this.dateTime = dateTime;
		if(httpStatus != null) {
			httpStatusCode = httpStatus.value();
		}
		else {
			httpStatusCode = 0;
		}
	}
	
}
