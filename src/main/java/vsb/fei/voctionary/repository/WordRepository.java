package vsb.fei.voctionary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vsb.fei.voctionary.model.Word;


@Repository
public interface WordRepository extends JpaRepository<Word, String>{
		
}
