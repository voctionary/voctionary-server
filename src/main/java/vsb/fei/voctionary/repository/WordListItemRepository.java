package vsb.fei.voctionary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vsb.fei.voctionary.model.WordListItem;


@Repository
public interface WordListItemRepository extends JpaRepository<WordListItem, Long>{
		
}
