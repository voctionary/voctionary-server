package vsb.fei.voctionary.service.db;

import vsb.fei.voctionary.model.UserToken;

public interface UserTokenService {
	
	UserToken findByToken(String token);
	
	UserToken createConfirmationToken(UserToken confirmationToken);
	
	UserToken updateConfirmationToken(UserToken confirmationToken);
	
	void deleteAll();

}
