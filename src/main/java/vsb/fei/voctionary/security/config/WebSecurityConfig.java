package vsb.fei.voctionary.security.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.auth0.jwt.algorithms.Algorithm;

import lombok.RequiredArgsConstructor;
import vsb.fei.voctionary.security.filter.CustomAuthenticationFilter;
import vsb.fei.voctionary.security.filter.CustomAuthorizationFitler;
import vsb.fei.voctionary.service.JwtService;
import vsb.fei.voctionary.service.db.InvalidJwtService;
import vsb.fei.voctionary.service.db.UserService;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{    
	
	public static final String CHROME_EXTENSION_ID = "hijhmbhebodagjbggpninekclkhoabjb";
		
	public static final Algorithm ALGORITHM = Algorithm.HMAC256("secret".getBytes());
	public static final String ACCESS_TOKEN_NAME = "access_token";
	public static final String REFRESH_TOKEN_NAME = "refresh_token";
	public static final String REMEMBER_ME_NAME = "remember_me";
	public static final String USER_ROLES_NAME = "roles";
	public static final String LOGIN_PATH = "/api/logIn";
	public static final String LOGOUT_PATH = "/api/logOut";
	
	private final UserService userService;
	private final JwtService jwtService;
	private final InvalidJwtService invalidJwtService;
	
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
	}
	
	  private CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "chrome-extension://" + CHROME_EXTENSION_ID));
	    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
	    config.setAllowedHeaders(Arrays.asList("*"));
	    config.setAllowCredentials(true);
	    
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);

	    return source;
	  }
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
		CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean(), userService, jwtService);
		customAuthenticationFilter.setFilterProcessesUrl(LOGIN_PATH);
		http.cors().configurationSource(corsConfigurationSource());
		http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
		http
			.authorizeRequests()
				.antMatchers(HttpMethod.PUT, "/api/updateUser").hasRole("USER")
				.antMatchers(HttpMethod.GET, "/api/getWordList/{id}").hasRole("USER")
				.antMatchers(HttpMethod.GET, "/api/getWordLists").hasRole("USER")
				.antMatchers(HttpMethod.POST, "/api/addWordToList").hasRole("USER")
				.antMatchers(HttpMethod.DELETE, "/api/removeWordFromList").hasRole("USER")
				.antMatchers(HttpMethod.PUT, "/api/learnWord").hasRole("USER")
				.antMatchers(HttpMethod.GET, "/api/startTest").hasRole("USER")
				.antMatchers(HttpMethod.GET, "/api/finishTest").hasRole("USER")

				.antMatchers(HttpMethod.POST, "/api/register").anonymous()
				.antMatchers(HttpMethod.PUT, "/api/requestResetPassword").anonymous()
				.antMatchers(HttpMethod.PUT, "/api/resetPassword").anonymous()
				.antMatchers(HttpMethod.POST, "/api/confirmEmail").anonymous()

				.antMatchers(HttpMethod.GET, "/api/getUserInfo").permitAll()
				.antMatchers(HttpMethod.GET, "/api/words").permitAll()
				.antMatchers(HttpMethod.GET, "/api/word/{id}").permitAll()
				.antMatchers(HttpMethod.GET, "/api/randomWord").permitAll()
				.antMatchers(HttpMethod.GET, "/api/randomWords").permitAll()
				.antMatchers(HttpMethod.GET, "/api/findWord").permitAll()
				.antMatchers(HttpMethod.GET, "/api/languages").permitAll()
				.antMatchers(HttpMethod.GET, "/api/tests").permitAll()
				.antMatchers(HttpMethod.GET, "/api/logOut").permitAll()

				.anyRequest().denyAll();
        
        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFitler(userService, jwtService, invalidJwtService), UsernamePasswordAuthenticationFilter.class);
    }

}
