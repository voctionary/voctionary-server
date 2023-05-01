package vsb.fei.voctionary.service.db;

import java.util.List;

import vsb.fei.voctionary.model.WordTest;

public interface WordTestService {
		
	WordTest getWordTest(Long id);
	
	List<WordTest> getWordTests();
						
	WordTest createWordTest(WordTest wordTest);
		
	WordTest finishWordTest(WordTest wordTest);
	
	void deleteAll();

}
