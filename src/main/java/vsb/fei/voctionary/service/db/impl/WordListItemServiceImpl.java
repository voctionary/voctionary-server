package vsb.fei.voctionary.service.db.impl;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vsb.fei.voctionary.model.WordListItem;
import vsb.fei.voctionary.repository.WordListItemRepository;
import vsb.fei.voctionary.service.db.WordListItemService;

@Service
@AllArgsConstructor
public class WordListItemServiceImpl implements WordListItemService {

	private final WordListItemRepository wordListItemRepository;

	@Override
	public WordListItem createWordListItem(WordListItem wordListItem) {
		return  wordListItemRepository.save(wordListItem);
	}

	@Override
	public WordListItem listWordListItem(WordListItem wordListItem, boolean isListed) {
		wordListItem.setListed(isListed);
		return wordListItemRepository.save(wordListItem);
	}

	@Override
	public WordListItem learnWordListItem(WordListItem wordListItem, boolean isLearned) {
		wordListItem.setLearned(isLearned);
		return wordListItemRepository.save(wordListItem);
	}
	
	@Override
	public void deleteAll() {
		wordListItemRepository.deleteAll();
	}

}
