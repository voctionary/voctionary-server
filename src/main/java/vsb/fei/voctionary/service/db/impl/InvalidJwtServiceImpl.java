package vsb.fei.voctionary.service.db.impl;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vsb.fei.voctionary.model.InvalidJwt;
import vsb.fei.voctionary.repository.InvalidJwtRepository;
import vsb.fei.voctionary.service.db.InvalidJwtService;

@Service
@AllArgsConstructor
public class InvalidJwtServiceImpl implements InvalidJwtService {
	
	private final InvalidJwtRepository invalidJwtRepository;

	@Override
	public InvalidJwt findByToken(String token) {
		return invalidJwtRepository.findById(token).orElse(null);
	}
	
	@Override
	public InvalidJwt createInvalidJwt(InvalidJwt invalidJwt) {
		return invalidJwtRepository.save(invalidJwt);
	}


}
