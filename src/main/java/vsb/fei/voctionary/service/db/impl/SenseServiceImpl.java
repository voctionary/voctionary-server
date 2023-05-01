package vsb.fei.voctionary.service.db.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vsb.fei.voctionary.VoctionaryApplication;
import vsb.fei.voctionary.model.Sense;
import vsb.fei.voctionary.repository.SenseRepository;
import vsb.fei.voctionary.service.db.SenseService;

@Service
@RequiredArgsConstructor
public class SenseServiceImpl implements SenseService {
	
	Logger logger = LoggerFactory.getLogger(VoctionaryApplication.class);
	
	private final SenseRepository senseRepository;
	
	@Override
	public Sense getSense(String id) {
		return senseRepository.findById(id).orElse(null);
	}

}
