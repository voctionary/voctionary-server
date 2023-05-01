package vsb.fei.voctionary.rest.controller;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.RequiredArgsConstructor;
import vsb.fei.voctionary.email.EmailSenderService;
import vsb.fei.voctionary.email.EmailSenderServiceImpl.EmailType;
import vsb.fei.voctionary.exception.ApiExceptionHandler;
import vsb.fei.voctionary.exception.ApiRequestException;
import vsb.fei.voctionary.model.User;
import vsb.fei.voctionary.model.UserToken;
import vsb.fei.voctionary.model.enums.UserRole;
import vsb.fei.voctionary.model.enums.UserTokenType;
import vsb.fei.voctionary.rest.RestConverter;
import vsb.fei.voctionary.rest.model.RestRegistrationRequest;
import vsb.fei.voctionary.rest.model.RestUser;
import vsb.fei.voctionary.service.EmailValidator;
import vsb.fei.voctionary.service.JwtService;
import vsb.fei.voctionary.service.db.UserService;
import vsb.fei.voctionary.service.db.UserTokenService;

@RestController
@RequestMapping(path = "api/")
@RequiredArgsConstructor
public class UserController {

	Logger logger = LoggerFactory.getLogger(UserController.class);

	private final UserService userService;
	private final UserTokenService confirmationTokenService;
	private final EmailValidator emailValidator;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final EmailSenderService emailSender;
	private final JwtService jwtService;

	@GetMapping(path = "getUserInfo")
	public ResponseEntity<RestUser> getUserInfo(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException, IOException {			
		RestUser restUser = null;
		User user = jwtService.getUserFromJwtToken(request.getCookies());
		if(user == null || !user.isEnabled()) {
			return new ResponseEntity<RestUser>(restUser, HttpStatus.UNAUTHORIZED);
		}
		restUser = new RestConverter().convertUser(user);
		return new ResponseEntity<RestUser>(restUser, HttpStatus.OK);
	}
	
	@PutMapping(path = "updateUser")
	public ResponseEntity<RestUser> updateUser(@RequestBody RestRegistrationRequest restRequest, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException, IOException {
		RestUser restUser = null;
		User user = jwtService.getUserFromJwtToken(request.getCookies());
		if(user == null || !user.getEmail().equals(restRequest.getEmail())) {
			// invalid token
			ApiExceptionHandler.writeApiRequestExceptionToResponse(new ApiRequestException(HttpStatus.UNAUTHORIZED, "Unauthorized behavior. Your request cannot be executed.", true), response);
			return new ResponseEntity<RestUser>(restUser, HttpStatus.UNAUTHORIZED);
		}
		
		user.setName(restRequest.getName());
		user.setSurname(restRequest.getSurname());
		user.setReceivingEmails(restRequest.getReceivingEmails());
		if(!StringUtils.isAllBlank(restRequest.getPassword())) {
			user.setPassword(bCryptPasswordEncoder.encode(restRequest.getPassword()));
		}
		user = userService.updateUser(user);
		restUser = new RestConverter().convertUser(user);
		return new ResponseEntity<RestUser>(restUser, HttpStatus.OK);
	}

	@PostMapping(path = "register")
	public void register(@RequestBody RestRegistrationRequest restRequest) {
		User user = new RestConverter().convertRestRegistrationRequest(restRequest);
		
		if(!emailValidator.test(user.getEmail())) {
			throw new ApiRequestException(HttpStatus.BAD_REQUEST, "Email is not valid", false);
		}
		
		User dbUser = userService.findByEmail(user.getEmail());
		if(dbUser == null) {
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			user.getUserRoles().add(UserRole.ROLE_USER);
			String token = UUID.randomUUID().toString();
			final Instant now = Instant.now();
			UserToken confirmationToken = new UserToken(token, UserTokenType.CONFIRM_EMAIL, now, now.plusSeconds(15*60), user);
			user.getConfirmationTokens().add(confirmationToken);
			user = userService.createUser(user);
			emailSender.send(EmailType.CONFIRM_EMAIL, user, token);
		}
		else if(dbUser.isEnabled() ) {
			throw new ApiRequestException(HttpStatus.BAD_REQUEST, "Email already registered", false); 
		}
		else {
			dbUser.setName(user.getName());
			dbUser.setSurname(user.getSurname());
			dbUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			dbUser.setReceivingEmails(user.getReceivingEmails());
			
			final Instant now = Instant.now();
			for(UserToken t : dbUser.getConfirmationTokens()) {
				if(t.getExpiresAt().isAfter(now) && t.getType() == UserTokenType.CONFIRM_EMAIL) {
					t.setExpiresAt(now);
				}
			}
			String token = UUID.randomUUID().toString();
			UserToken confirmationToken = new UserToken(token, UserTokenType.CONFIRM_EMAIL, now, now.plusSeconds(15*60), dbUser);
			dbUser.getConfirmationTokens().add(confirmationToken);
			dbUser = userService.updateUser(dbUser);
			emailSender.send(EmailType.CONFIRM_EMAIL, dbUser, token);
		}
	}
	
	@PutMapping(path = "requestResetPassword")
	public void requestResetPassword(@RequestParam String email) throws JsonProcessingException, IOException {
		String functionName = "GET api/requestResetPassword";
		logger.info(MessageFormat.format("{0} - requested", functionName));
		
		User dbUser = userService.findByEmail(email);
		if(dbUser == null) {
			throw new ApiRequestException(HttpStatus.BAD_REQUEST, "User with given email does not exist", false);
		}
		
		final Instant now = Instant.now();
		for(UserToken t : dbUser.getConfirmationTokens()) {
			if(t.getExpiresAt().isAfter(now) && t.getType() == UserTokenType.RESET_PASSWORD) {
				t.setExpiresAt(now);
			}
		}
		String token = UUID.randomUUID().toString();
		UserToken confirmationToken = new UserToken(token, UserTokenType.RESET_PASSWORD, now, now.plusSeconds(15*60), dbUser);
		dbUser.getConfirmationTokens().add(confirmationToken);
		dbUser = userService.updateUser(dbUser);
		emailSender.send(EmailType.RESET_PASSWORD, dbUser, token);
	}
	
	@PutMapping(path = "resetPassword")
	public void resetPassword(@RequestBody RestRegistrationRequest restRequest) throws JsonProcessingException, IOException {
		UserToken confirmationToken = confirmationTokenService.findByToken(restRequest.getEmail());
		if(confirmationToken == null) {
			throw new ApiRequestException("Token not found", true);
		}

		Instant now = Instant.now();
		if(confirmationToken.getExpiresAt().isBefore(now)) {
			throw new ApiRequestException("Token is expired", false);
		}
		
		confirmationToken.setConfirmedAt(now);
		confirmationToken.getUser().setPassword(bCryptPasswordEncoder.encode(restRequest.getPassword()));
		userService.updateUser(confirmationToken.getUser());
	}
	
	@GetMapping(path = "logOut")
	public void logOut(HttpServletRequest request, HttpServletResponse response)
			throws JsonGenerationException, JsonMappingException, IOException {
		// see CustomAuthorizationFitler.class
	}

	@PostMapping(path = "confirmEmail")
	public void confirmEmail(@RequestParam("token") String token) {
		UserToken confirmationToken = confirmationTokenService.findByToken(token);
		if(confirmationToken == null) {
			throw new ApiRequestException("Token not found", true);
		}
		
		if(confirmationToken.getUser().getEnabled()) {
			throw new ApiRequestException("Email already confirmed", false);
		}

		Instant now = Instant.now();
		if(confirmationToken.getExpiresAt().isBefore(now)) {
			throw new ApiRequestException("Token is expired", false);
		}
		
		confirmationToken.setConfirmedAt(now);
		confirmationToken.getUser().setEnabled(true);
		userService.updateUser(confirmationToken.getUser());
	}

}
