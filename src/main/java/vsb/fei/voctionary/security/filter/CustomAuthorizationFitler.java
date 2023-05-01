package vsb.fei.voctionary.security.filter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import vsb.fei.voctionary.model.InvalidJwt;
import vsb.fei.voctionary.model.User;
import vsb.fei.voctionary.security.config.WebSecurityConfig;
import vsb.fei.voctionary.service.JwtService;
import vsb.fei.voctionary.service.db.InvalidJwtService;
import vsb.fei.voctionary.service.db.UserService;

@RequiredArgsConstructor
public class CustomAuthorizationFitler extends OncePerRequestFilter {
	
	Logger logger = LoggerFactory.getLogger(CustomAuthorizationFitler.class);

	private final UserService userService;
	private final JwtService jwtService;
	private final InvalidJwtService invalidJwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if(request.getRequestURI().equals(WebSecurityConfig.LOGOUT_PATH)) {
			removeJWTCookies(request, response);
			filterChain.doFilter(request, response);
			return;
		}
		
		Cookie cookies[] = request.getCookies();
		if(cookies == null) {
			filterChain.doFilter(request, response);
			return;
		}
		Cookie accessTokenCookie = null;
		Cookie refreshTokenCookie = null;
		boolean isRememberMeActive = false;
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(WebSecurityConfig.ACCESS_TOKEN_NAME)) {
				// inactive because localhost client is not using HTTPS, only HTTP
				/*if(!cookie.isHttpOnly()) {
					ApiExceptionHandler.writeApiRequestExceptionToResponse(new ApiRequestException(HttpStatus.UNAUTHORIZED, "The cookie containing the access token is not HttpOnly", true), response);
					return;
				}
				else if(!cookie.getSecure()) {
					ApiExceptionHandler.writeApiRequestExceptionToResponse(new ApiRequestException(HttpStatus.UNAUTHORIZED, "The cookie containing the access token is not secure", true), response);
					return;
				}*/
				accessTokenCookie = cookie;
			}
			if (cookie.getName().equals(WebSecurityConfig.REFRESH_TOKEN_NAME)) {
				/*if(!cookie.isHttpOnly()) {
					ApiExceptionHandler.writeApiRequestExceptionToResponse(new ApiRequestException(HttpStatus.UNAUTHORIZED, "The cookie containing the refresh token is not HttpOnly", true), response);
					return;
				}
				else if(!cookie.getSecure()) {
					ApiExceptionHandler.writeApiRequestExceptionToResponse(new ApiRequestException(HttpStatus.UNAUTHORIZED, "The cookie containing the refresh token is not secure", true), response);
					return;
				}*/
				refreshTokenCookie = cookie;
			}
			if (cookie.getName().equals(WebSecurityConfig.REMEMBER_ME_NAME)) {
				/*if(!cookie.isHttpOnly()) {
					ApiExceptionHandler.writeApiRequestExceptionToResponse(new ApiRequestException(HttpStatus.UNAUTHORIZED, "The cookie containing the rememberMe value is not HttpOnly", true), response);
					return;
				}
				else if(!cookie.getSecure()) {
					ApiExceptionHandler.writeApiRequestExceptionToResponse(new ApiRequestException(HttpStatus.UNAUTHORIZED, "The cookie containing the rememberMe value is not secure", true), response);
					return;
				}*/
				isRememberMeActive = true;
			}
		}
		
		// If access token is present,  validate and set authentication
		if(accessTokenCookie != null) {
			JWTVerifier verifier = JWT.require(WebSecurityConfig.ALGORITHM).build();
			DecodedJWT decodedJWT = null;
			try {
				decodedJWT = verifier.verify(accessTokenCookie.getValue());
			} catch (TokenExpiredException e) {
				// expired access token ---> try refresh token
				if(refreshTokenCookie != null) {
					try {
						decodedJWT = verifier.verify(refreshTokenCookie.getValue());
					} catch (TokenExpiredException e2) {
						// expired refresh token ---> need to login
						removeJWTCookies(request, response);
						return;
					} catch (Exception e2) {
						// invalid refresh token
						removeJWTCookies(request, response);
						return;
					}
					
					verifyToken(request, response, filterChain, decodedJWT, isRememberMeActive, true);
					return;
				}
			} catch (Exception e) {
				// invalid access token
				removeJWTCookies(request, response);
				return;
			} 
			verifyToken(request, response, filterChain, decodedJWT, isRememberMeActive, false);
			return;
		}
			
        // If access token is not present, check for refresh token
		if(accessTokenCookie == null) {
            // If refresh token is present, generate new access token and update cookies
			if(refreshTokenCookie != null) {
				JWTVerifier verifier = JWT.require(WebSecurityConfig.ALGORITHM).build();
				DecodedJWT decodedJWT = null;
				try {
					decodedJWT =  verifier.verify(refreshTokenCookie.getValue());
				} catch (TokenExpiredException e) {
					// expired refresh token ---> need to login
					removeJWTCookies(request, response);
					return;
				} catch (Exception e) {
					// invalid refresh token
					removeJWTCookies(request, response);
					return;
				}
				
				verifyToken(request, response, filterChain, decodedJWT, isRememberMeActive, true);
				return;
			}
		}
		
		filterChain.doFilter(request, response);
		return;
	}
	
	private void removeJWTCookies(HttpServletRequest request, HttpServletResponse response) {
		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals(WebSecurityConfig.ACCESS_TOKEN_NAME)) {
				response.addHeader(HttpHeaders.SET_COOKIE, getResponseCookieForRemove(cookie).toString());
				invalidJwtService.createInvalidJwt(new InvalidJwt(cookie.getValue(), Instant.now().plusSeconds(cookie.getMaxAge()))); 
			}
			else if (cookie.getName().equals(WebSecurityConfig.REFRESH_TOKEN_NAME)) {
				response.addHeader(HttpHeaders.SET_COOKIE, getResponseCookieForRemove(cookie).toString());
				invalidJwtService.createInvalidJwt(new InvalidJwt(cookie.getValue(), Instant.now().plusSeconds(cookie.getMaxAge()))); 
			}
			else if (cookie.getName().equals(WebSecurityConfig.REMEMBER_ME_NAME)) {
				response.addHeader(HttpHeaders.SET_COOKIE, getResponseCookieForRemove(cookie).toString());
			}
		}
	}
	
	private ResponseCookie getResponseCookieForRemove(Cookie cookie){
		ResponseCookie responseCookie = ResponseCookie.from(cookie.getName(), cookie.getValue())
			    .path("/")
			    .maxAge(0)
			    .secure(true)
			    .httpOnly(true)
			    .sameSite("None")
			    .build();
		return responseCookie;
	}

	private void verifyToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, DecodedJWT decodedJWT, boolean isRememberMeActive, boolean createNewAccessToken) throws JsonProcessingException, IOException, ServletException {
		User user = userService.findByEmail(decodedJWT.getSubject());
		if (user == null) {
			// invalid access_token
			removeJWTCookies(request, response);
			return;
		}
		if( invalidJwtService.findByToken(decodedJWT.getToken()) != null) {
			// invalid JWT
			removeJWTCookies(request, response);
			return;
		}
		if(!(request.getServerName() + ":" + request.getServerPort()).equals(decodedJWT.getIssuer())) {
			// different URL
			removeJWTCookies(request, response);
			return;
		}
		
		List<String> roles = decodedJWT.getClaim(WebSecurityConfig.USER_ROLES_NAME).asList(String.class);
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null,
				roles.stream().map(r -> new SimpleGrantedAuthority(r)).collect(Collectors.toList()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		if(createNewAccessToken) {
			response.addHeader(HttpHeaders.SET_COOKIE, jwtService.createAccessJWTResponseCookie(user, request.getServerName() + ":" + request.getServerPort(), isRememberMeActive).toString());
		}
		filterChain.doFilter(request, response);
	}

}
