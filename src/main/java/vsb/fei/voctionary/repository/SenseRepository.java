package vsb.fei.voctionary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vsb.fei.voctionary.model.Sense;


@Repository
public interface SenseRepository extends JpaRepository<Sense, String>{
		
}
