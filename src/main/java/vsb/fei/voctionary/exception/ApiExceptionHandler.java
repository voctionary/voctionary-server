package vsb.fei.voctionary.exception;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@ControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(value = {ApiRequestException.class})
	public ResponseEntity<Object> handleApiRequestException(ApiRequestException e){		
		ApiException apiException = new ApiException(e.getHttpStatus(), e.getMessage(), e.isError(), LocalDateTime.now());
		return new ResponseEntity<>(apiException, e.getHttpStatus());
	}
	
	public static void writeApiRequestExceptionToResponse(ApiRequestException apiRequestException, HttpServletResponse response) throws JsonProcessingException, IOException {
		ApiException apiException = new ApiException(apiRequestException.getHttpStatus(), apiRequestException.getMessage(), apiRequestException.isError(), LocalDateTime.now());
		ObjectMapper mapper = new ObjectMapper();
	    mapper.registerModule(new JavaTimeModule());
	    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
	    response.setStatus(HttpStatus.UNAUTHORIZED.value());
	    response.getWriter().write(mapper.writeValueAsString(apiException));
	}
	
}
