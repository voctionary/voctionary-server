package vsb.fei.voctionary.service.db.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vsb.fei.voctionary.model.WordTest;
import vsb.fei.voctionary.repository.WordTestRepository;
import vsb.fei.voctionary.service.db.WordTestService;

@Service
@AllArgsConstructor
public class WordTestServiceImpl implements WordTestService {
	
	private final WordTestRepository wordTestRepository;

	@Override
	public WordTest getWordTest(Long id) {
		return wordTestRepository.findById(id).orElse(null);
	}
	
	@Override
	public List<WordTest> getWordTests() {
		return wordTestRepository.findAll();
	}

	@Override
	public WordTest createWordTest(WordTest wordTest) {
		wordTest.setCreatedAt(Instant.now());
		return wordTestRepository.save(wordTest);
	}

	@Override
	public WordTest finishWordTest(WordTest wordTest) {
		wordTest.setFinishedAt(Instant.now());
		return wordTestRepository.save(wordTest);
	}
	
	@Override
	public void deleteAll() {
		wordTestRepository.deleteAll();
	}
	
}
