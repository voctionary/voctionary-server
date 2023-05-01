package vsb.fei.voctionary.service.db.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vsb.fei.voctionary.model.UserToken;
import vsb.fei.voctionary.repository.UserTokenRepository;
import vsb.fei.voctionary.service.db.UserTokenService;

@Service
@AllArgsConstructor
public class UserTokenServiceImpl implements UserTokenService {
	
	private final UserTokenRepository userTokenRepository;

	@Override
	public UserToken findByToken(String token) {
		List<UserToken> list = userTokenRepository.findAll().stream().filter(t -> t.getToken().equals(token)).collect(Collectors.toList());
		if(list == null || list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	@Override
	public UserToken createConfirmationToken(UserToken confirmationToken) {
		return userTokenRepository.save(confirmationToken);
	}

	@Override
	public UserToken updateConfirmationToken(UserToken confirmationToken) {
		return userTokenRepository.save(confirmationToken);
	} 
	
	@Override
	public void deleteAll() {
		userTokenRepository.deleteAll();
	}
	
}
