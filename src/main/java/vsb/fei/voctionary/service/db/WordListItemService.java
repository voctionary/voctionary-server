package vsb.fei.voctionary.service.db;

import vsb.fei.voctionary.model.WordListItem;

public interface WordListItemService {
	
	WordListItem createWordListItem(WordListItem wordListItem);
	
	WordListItem listWordListItem(WordListItem wordListItem, boolean isListed);

	WordListItem learnWordListItem(WordListItem wordListItem, boolean isLearned);
	
	void deleteAll();

}