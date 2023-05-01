package vsb.fei.voctionary.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.RequiredArgsConstructor;
import vsb.fei.voctionary.model.User;
import vsb.fei.voctionary.model.enums.TokenType;
import vsb.fei.voctionary.security.config.WebSecurityConfig;
import vsb.fei.voctionary.service.db.UserService;

@RequiredArgsConstructor
@Service
public class JwtService {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private final UserService userService;
		
	public ResponseCookie createAccessJWTResponseCookie(User user, String issuer, boolean rememberMe) {
		TokenType tokenType = rememberMe ? TokenType.LONG_ACCESS_TOKEN : TokenType.SHORT_ACCESS_TOKEN;
		return createJWTResponseCookie(generateJwtToken(user, issuer, tokenType), tokenType);
	}
	
	public List<ResponseCookie> createJWTResponseCookies(User user, String issuer, boolean rememberMe) {
		List<ResponseCookie> cookies = new ArrayList<>();
		if(rememberMe) {
			cookies.add(createJWTResponseCookie(generateJwtToken(user, issuer, TokenType.LONG_ACCESS_TOKEN), TokenType.LONG_ACCESS_TOKEN));
			cookies.add(createJWTResponseCookie(generateJwtToken(user, issuer, TokenType.LONG_REFRESH_TOKEN), TokenType.LONG_REFRESH_TOKEN));
			
			
			final ResponseCookie rememberMeCookie = ResponseCookie
			        .from(WebSecurityConfig.REMEMBER_ME_NAME, "true")
			        .secure(true)
			        .httpOnly(true)
			        .path("/")
			        .maxAge( (int) TokenType.LONG_REFRESH_TOKEN.getDurationInSeconds())
			        .sameSite("None")
			        .build();
			cookies.add(rememberMeCookie);
		}
		else {
			cookies.add(createJWTResponseCookie(generateJwtToken(user, issuer, TokenType.SHORT_ACCESS_TOKEN), TokenType.SHORT_ACCESS_TOKEN));
			cookies.add(createJWTResponseCookie(generateJwtToken(user, issuer, TokenType.SHORT_REFRESH_TOKEN), TokenType.SHORT_REFRESH_TOKEN));
		}
		
		return cookies;
	}
	
	private ResponseCookie createJWTResponseCookie(String token, TokenType tokenType) {
		final ResponseCookie responseCookie = ResponseCookie
		        .from(tokenType.getCookieName(),  token)
		        .secure(true)
		        .httpOnly(true)
		        .path("/")
		        .maxAge( (int) tokenType.getDurationInSeconds())
		        .sameSite("None")
		        .build();
		return responseCookie;
	}
	
	public User getUserFromJwtToken(Cookie[] cookies) {
		if(cookies != null) {
			Cookie cookie = Stream.of(cookies).filter(c -> c.getName().equals(WebSecurityConfig.ACCESS_TOKEN_NAME)).findFirst().orElse(null);
			
			if(cookie == null) {
				logger.info("There is no access token cookie");
				cookie = Stream.of(cookies).filter(c -> c.getName().equals(WebSecurityConfig.REFRESH_TOKEN_NAME)).findFirst().orElse(null);
			}
			
			if(cookie == null) {
				logger.info("There is no refresh token cookie");
				return null;
			}			
			
			JWTVerifier verifier = JWT.require(WebSecurityConfig.ALGORITHM).build();
			DecodedJWT decodedJWT = null;
			try {
				decodedJWT = verifier.verify(cookie.getValue());
			} catch (Exception e) {
				return null;
			}
			
			return userService.findByEmail(decodedJWT.getSubject());
		}
		
		logger.info("There are no cookies");
		return null;
	}
	
	
	public String generateJwtToken(User user, String issuer, TokenType tokenType){
		String token = JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + (tokenType.getDurationInSeconds() * 1000)))
				.withIssuer(issuer)
				.withClaim(WebSecurityConfig.USER_ROLES_NAME, user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.sign(WebSecurityConfig.ALGORITHM);
		
		return token;
	}
	
}
