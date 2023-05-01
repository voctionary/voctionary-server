package vsb.fei.voctionary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vsb.fei.voctionary.model.WordList;


@Repository
public interface WordListRepository extends JpaRepository<WordList, Long>{
		
}
