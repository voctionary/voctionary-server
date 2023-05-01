package vsb.fei.voctionary.service.db.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vsb.fei.voctionary.VoctionaryApplication;
import vsb.fei.voctionary.exception.ApiRequestException;
import vsb.fei.voctionary.model.Sense;
import vsb.fei.voctionary.model.Word;
import vsb.fei.voctionary.model.enums.Language;
import vsb.fei.voctionary.repository.WordRepository;
import vsb.fei.voctionary.service.db.WordService;

@Service
@RequiredArgsConstructor
public class WordServiceImpl implements WordService {
	
	Logger logger = LoggerFactory.getLogger(VoctionaryApplication.class);
	
	private final WordRepository wordRepository;
	private final EntityManager entityManager;
	
	@Override
	public long getCount() {
		return wordRepository.count();
	}
	
	@Override
	public Word getWord(String id) {
		return wordRepository.findById(id).orElse(null);
	}

	@Override
	public Word getWordWithRelated(String id) {
		Word word = wordRepository.findById(id).orElse(null);
		if(word != null) {
			List<Word> relatedWords = new ArrayList<>();
			for(String relatedEntry : word.getRelatedEntries()) {
				relatedWords.add(wordRepository.findById(relatedEntry).orElse(null));
			}
			word.setRelatedWords(relatedWords);
		}

		return word;
	}

	@Override
	public List<Word> getWords() {
		return wordRepository.findAll();
	}

	@Override
	synchronized public Word createWord(Word word) {
		Word wordDb = getWord(word.getId());
		if(wordDb != null) {
			logger.error("Word with ID '" + word.getId() + "' does already exist.");
			return wordDb;
		}
		word = wordRepository.save(word);
	
		logger.info(MessageFormat.format("Word with ID {0} created into the database", word.getId()));
		
		return word;
	}

	@Override
	public List<Word> createWords(List<Word> words) {
		List<Word> returnWords = new ArrayList<>();
		for (Word word : words) {
			returnWords.add(createWord(word));
		}

		return returnWords;
	}

	@Override
	public void deleteWord(String id) {
		Word word = getWord(id);
		if(word == null) {
			throw new ApiRequestException("Word with ID '" + id + "' does not exist.", true);
		}
		
		wordRepository.deleteById(id);
	}
	
	@Override
	public boolean exists(String id) {
		return wordRepository.existsById(id);
	}

	@Override
	public List<Sense> getRandomTestableSenses(Word paramWord) {
		Sense sense1;
		Sense sense2;
		
		TypedQuery<Sense> query = entityManager.createQuery("SELECT s FROM Sense s WHERE s.definition IS NOT NULL AND s.word.language = :language AND s.word.partOfSpeech = :partOfSpeech AND s.word.id != :testedWordId ORDER BY RAND()", Sense.class);
		query.setParameter("language", paramWord.getLanguage());
		query.setParameter("partOfSpeech", paramWord.getPartOfSpeech());
		query.setParameter("testedWordId", paramWord.getId());
		query.setMaxResults(1);
		List<Sense> senses = query.getResultList();
		if(senses.isEmpty()) {
			TypedQuery<Sense> secondQuery = entityManager.createQuery("SELECT s FROM Sense s WHERE s.definition IS NOT NULL AND s.word.language = :language AND s.word.id != :testedWordId ORDER BY RAND()", Sense.class);
			secondQuery.setParameter("language", paramWord.getLanguage());
			secondQuery.setParameter("testedWordId", paramWord.getId());
			secondQuery.setMaxResults(1);
			senses = secondQuery.getResultList();
			if(senses.isEmpty()) {
				return null;
			}
			
			sense1 = senses.get(0);
			
			secondQuery = entityManager.createQuery("SELECT s FROM Sense s WHERE s.definition IS NOT NULL AND s.word.language = :language AND s.word.id != :testedWordId AND s.word.id != :wordId ORDER BY RAND()", Sense.class);
			secondQuery.setParameter("language", paramWord.getLanguage());
			secondQuery.setParameter("testedWordId", paramWord.getId());
			secondQuery.setParameter("wordId", sense1.getWord().getId());
			secondQuery.setMaxResults(1);
			senses = secondQuery.getResultList();
			if(senses.isEmpty()) {
				return null;
			}
			
			sense2 = senses.get(0);
		}
		else {
			sense1 = senses.get(0);
			query = entityManager.createQuery("SELECT s FROM Sense s WHERE s.definition IS NOT NULL AND s.word.language = :language AND s.word.partOfSpeech = :partOfSpeech AND s.word.id != :testedWordId AND s.word.id != :wordId ORDER BY RAND()", Sense.class);
			query.setParameter("language", paramWord.getLanguage());
			query.setParameter("partOfSpeech", paramWord.getPartOfSpeech());
			query.setParameter("testedWordId", paramWord.getId());
			query.setParameter("wordId", sense1.getWord().getId());
			query.setMaxResults(1);
			senses = query.getResultList();
			
			if(senses.isEmpty()) {
				TypedQuery<Sense> secondQuery = entityManager.createQuery("SELECT s FROM Sense s WHERE s.definition IS NOT NULL AND s.word.language = :language AND s.word.id != :testedWordId AND s.word.id != :wordId ORDER BY RAND()", Sense.class);
				secondQuery.setParameter("language", paramWord.getLanguage());
				secondQuery.setParameter("testedWordId", paramWord.getId());
				secondQuery.setParameter("wordId", sense1.getWord().getId());
				secondQuery.setMaxResults(1);
				senses = secondQuery.getResultList();
				if(senses.isEmpty()) {
					return null;
				}
			}
			
			sense2 = senses.get(0);
		}
		
		return List.of(sense1, sense2);
	}
	
	@Override
	public void deleteAll() {
		wordRepository.deleteAll();
	}

	@Override
	public Word getRandomWord(Language language) {
		TypedQuery<Word> query = entityManager.createQuery("SELECT w FROM Word w WHERE w.language = :language AND w.partOfSpeech IS NOT NULL ORDER BY RAND()", Word.class);
		query.setParameter("language", language);
		query.setMaxResults(1);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
