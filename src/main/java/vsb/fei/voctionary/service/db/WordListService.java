package vsb.fei.voctionary.service.db;

import java.util.List;

import vsb.fei.voctionary.model.WordList;
import vsb.fei.voctionary.model.WordListItem;
import vsb.fei.voctionary.model.enums.Language;

public interface WordListService {
		
	WordList getWordList(Long id);
	
	WordList createWordList(WordList wordList);
	
	WordList updateWordList(WordList wordList);
	
	List<WordList> findWordListsByUserEmail(String email);
	
	WordList findWordList(String email, Language language);
	
	WordList addWordToList(WordListItem wordListItem);

	WordList listWord(WordListItem wordListItem, boolean isListed);
	
	WordList learnWord(WordListItem wordListItem, boolean isListed);
		
	void deleteAll();

}