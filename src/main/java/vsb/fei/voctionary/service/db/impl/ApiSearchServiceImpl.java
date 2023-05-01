package vsb.fei.voctionary.service.db.impl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vsb.fei.voctionary.model.ApiSearch;
import vsb.fei.voctionary.model.enums.Language;
import vsb.fei.voctionary.repository.ApiSearchRepository;
import vsb.fei.voctionary.service.db.ApiSearchService;

@Service
@AllArgsConstructor
public class ApiSearchServiceImpl implements ApiSearchService {
	
	private ApiSearchRepository apiSearchRepository;
	private EntityManager entityManager;

	@Override
	public ApiSearch getApiSearch(int id) {
		return apiSearchRepository.findById(id).orElse(null);
	}
	
	@Override
	public ApiSearch getApiSearch(Language language, String text) {
		TypedQuery<ApiSearch> query = entityManager.createQuery("SELECT f FROM ApiSearch f WHERE f.language = :language AND f.text = :text", ApiSearch.class);
		query.setParameter("language", language);
		query.setParameter("text", text);
		query.setMaxResults(1);
		try {
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public ApiSearch createApiSearch(ApiSearch apiSearch) {
		return apiSearchRepository.save(apiSearch);
	}

	@Override
	public void deleteAll() {
		apiSearchRepository.deleteAll();
	}
	
}
