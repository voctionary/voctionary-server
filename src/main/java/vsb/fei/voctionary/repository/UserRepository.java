package vsb.fei.voctionary.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vsb.fei.voctionary.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
		
}
