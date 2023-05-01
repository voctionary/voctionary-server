package vsb.fei.voctionary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import vsb.fei.voctionary.model.ApiSearch;

@Component
public interface ApiSearchRepository extends JpaRepository<ApiSearch, Integer> {

}
