package vsb.fei.voctionary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vsb.fei.voctionary.model.WordTest;


@Repository
public interface WordTestRepository extends JpaRepository<WordTest, Long>{
		
}
