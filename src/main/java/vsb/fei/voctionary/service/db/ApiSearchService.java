package vsb.fei.voctionary.service.db;

import vsb.fei.voctionary.model.ApiSearch;
import vsb.fei.voctionary.model.enums.Language;

public interface ApiSearchService {
		
	ApiSearch getApiSearch(int id);
	
	ApiSearch getApiSearch(Language language, String text);
		
	ApiSearch createApiSearch(ApiSearch apiSearch);
			
	void deleteAll();

}
