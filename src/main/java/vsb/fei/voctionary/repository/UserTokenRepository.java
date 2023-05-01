package vsb.fei.voctionary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vsb.fei.voctionary.model.UserToken;


@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long>{
		
}
