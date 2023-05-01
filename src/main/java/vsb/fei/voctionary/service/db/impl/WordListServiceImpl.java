package vsb.fei.voctionary.service.db.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vsb.fei.voctionary.model.WordList;
import vsb.fei.voctionary.model.WordListItem;
import vsb.fei.voctionary.model.enums.Language;
import vsb.fei.voctionary.repository.WordListRepository;
import vsb.fei.voctionary.service.db.WordListItemService;
import vsb.fei.voctionary.service.db.WordListService;

@Service
@AllArgsConstructor
public class WordListServiceImpl implements WordListService {

	private final WordListRepository wordListRepository;
	private final WordListItemService wordListItemService;
	private final EntityManager entityManager;

	@Override
	public WordList getWordList(Long id) {
		WordList list = wordListRepository.findById(id).orElse(null);
		if(list == null) {
			return null;
		}
		return list;
	}

	@Override
	public WordList createWordList(WordList wordList) {
		return wordListRepository.save(wordList);
	}

	@Override
	public WordList updateWordList(WordList wordList) {
		return wordListRepository.save(wordList);	
	}

	@Override
	public List<WordList> findWordListsByUserEmail(String email) {
		TypedQuery<WordList> query = entityManager.createQuery("SELECT w FROM WordList w WHERE w.user.email = :email", WordList.class);
		query.setParameter("email", email);
		List<WordList> list = query.getResultList();
		return list;
	}
	
	@Override
	public WordList findWordList(String email, Language language) {
		TypedQuery<WordList> query = entityManager.createQuery("SELECT w FROM WordList w WHERE w.user.email = :email AND w.language = :language", WordList.class);
		query.setParameter("email", email);
		query.setParameter("language", language);
		query.setMaxResults(1);
		
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public WordList addWordToList(WordListItem wordListItem) {
		wordListItemService.createWordListItem(wordListItem);
		wordListItem.getWordList().setCount(wordListItem.getWordList().getCount() + 1);
		return updateWordList(wordListItem.getWordList());
	}

	@Override
	public WordList listWord(WordListItem wordListItem, boolean isListed) {
		wordListItemService.listWordListItem(wordListItem, isListed);
		wordListItem.getWordList().setCount(isListed ? wordListItem.getWordList().getCount() + 1 : wordListItem.getWordList().getCount() - 1);
		return updateWordList(wordListItem.getWordList());
	}
	
	@Override
	public WordList learnWord(WordListItem wordListItem, boolean isLearned) {
		wordListItemService.learnWordListItem(wordListItem, isLearned);
		return updateWordList(wordListItem.getWordList());
	}
	
	@Override
	public void deleteAll() {
		wordListRepository.deleteAll();
	}
	
}
