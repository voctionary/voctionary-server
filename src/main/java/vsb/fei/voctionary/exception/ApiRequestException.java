package vsb.fei.voctionary.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ApiRequestException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	private final HttpStatus httpStatus;
	private final boolean isError;

	public ApiRequestException(HttpStatus httpStatus, String message,boolean isError) {
		super(message);
		this.httpStatus = httpStatus;
		this.isError = isError;
	}

	public ApiRequestException(HttpStatus httpStatus, String message, boolean isError, Throwable cause) {
		super(message, cause);
		this.httpStatus = httpStatus;
		this.isError = isError;
	}
	
	public ApiRequestException(String message, boolean isError) {
		super(message);
		httpStatus = HttpStatus.BAD_GATEWAY;
		this.isError = isError;
	}
	
}