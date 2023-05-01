package vsb.fei.voctionary.model.enums;

import lombok.Getter;
import vsb.fei.voctionary.security.config.WebSecurityConfig;

@Getter
public enum TokenType {
	
	SHORT_ACCESS_TOKEN("short_" + WebSecurityConfig.ACCESS_TOKEN_NAME, WebSecurityConfig.ACCESS_TOKEN_NAME, 900L),
	LONG_ACCESS_TOKEN("long_" + WebSecurityConfig.ACCESS_TOKEN_NAME, WebSecurityConfig.ACCESS_TOKEN_NAME, 1800L),
	SHORT_REFRESH_TOKEN("short_" + WebSecurityConfig.REFRESH_TOKEN_NAME, WebSecurityConfig.REFRESH_TOKEN_NAME, 43200L),
	LONG_REFRESH_TOKEN("long_" + WebSecurityConfig.REFRESH_TOKEN_NAME, WebSecurityConfig.REFRESH_TOKEN_NAME, 2592000L);
		
	TokenType(String name, String cookieName, long durationInSeconds) {
		this.name = name;
		this.cookieName = cookieName;
		this.durationInSeconds = durationInSeconds;
	}

	private final String name;
	private final String cookieName;
	private final long durationInSeconds;
	
	public static TokenType find(String name) {
		for(TokenType type : TokenType.values()) {
			if(type.getName().equals(name)) {
				return type;
			}
		}
		return null;
	}
	
}