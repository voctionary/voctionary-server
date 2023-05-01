package vsb.fei.voctionary.service.db;

import vsb.fei.voctionary.model.InvalidJwt;

public interface InvalidJwtService {
	
	public InvalidJwt findByToken(String token);		
		
	public InvalidJwt createInvalidJwt(InvalidJwt authorizationToken);
	
}
