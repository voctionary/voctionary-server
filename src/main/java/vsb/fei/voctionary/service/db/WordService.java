package vsb.fei.voctionary.service.db;

import java.util.List;

import vsb.fei.voctionary.model.Sense;
import vsb.fei.voctionary.model.Word;
import vsb.fei.voctionary.model.enums.Language;

public interface WordService {
	
	long getCount();
	
	Word getWord(String id);
	
	Word getWordWithRelated(String id);
	
	List<Word> getWords();
				
	Word createWord(Word word);
	
	List<Word> createWords(List<Word> words);
		
	void deleteWord(String id);
		
	boolean exists(String id);
			
	List<Sense> getRandomTestableSenses(Word word);
	
	void deleteAll();

	Word getRandomWord(Language language);
	
}
