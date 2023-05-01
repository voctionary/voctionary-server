package vsb.fei.voctionary.security.filter;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import vsb.fei.voctionary.exception.ApiExceptionHandler;
import vsb.fei.voctionary.exception.ApiRequestException;
import vsb.fei.voctionary.model.User;
import vsb.fei.voctionary.service.JwtService;
import vsb.fei.voctionary.service.db.UserService;

@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private final AuthenticationManager authenticationManager;
	private final UserService userService;
	private final JwtService jwtService;
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		UserDetails userDetails = userService.loadUserByUsername(email);
		if(userDetails == null) {
			throw new UsernameNotFoundException(null);
		}
		
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
	    return authenticationManager.authenticate(authenticationToken);
	}
	
	@Override
	public void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
		if(failed instanceof UsernameNotFoundException) {
			ApiExceptionHandler.writeApiRequestExceptionToResponse(new ApiRequestException(HttpStatus.UNAUTHORIZED, "User with the given email does not exist", false), response);
			logger.info(MessageFormat.format("Unable to find a user in the database based on the given email {0}", request.getParameter("email")));
		}
		else if(failed instanceof BadCredentialsException) {
			ApiExceptionHandler.writeApiRequestExceptionToResponse(new ApiRequestException(HttpStatus.UNAUTHORIZED, "Wrong password", false), response);
			logger.info(MessageFormat.format("Unable to find a user in the database based on the given credentials - email: {0}", request.getParameter("email")));
		}
		else if(failed instanceof DisabledException) {
			ApiExceptionHandler.writeApiRequestExceptionToResponse(new ApiRequestException(HttpStatus.UNAUTHORIZED, "User has not confirmed email", false), response);
			logger.info(MessageFormat.format("Unable to find a user in the database based on the given credentials - email: {0}", request.getParameter("email")));
		}
		else {
			ApiExceptionHandler.writeApiRequestExceptionToResponse(new ApiRequestException(HttpStatus.BAD_GATEWAY, "Authentication failed", true), response);
			logger.info(MessageFormat.format(failed.getMessage() + " - Unsuccessful authentication for email {0}", request.getParameter("email")));
		}
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {
		User user = (User) authentication.getPrincipal();
		
		// generate new tokens
		for(ResponseCookie cookie : jwtService.createJWTResponseCookies(user, request.getServerName() + ":" + request.getServerPort(), request.getParameter("rememberMe").equals("true"))) {
			response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
		}
		
		logger.info(MessageFormat.format("Successful authentication for email {0}", request.getParameter("email")));
	}
	
}

