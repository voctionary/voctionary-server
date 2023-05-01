package vsb.fei.voctionary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vsb.fei.voctionary.model.InvalidJwt;

@Repository
public interface InvalidJwtRepository extends JpaRepository<InvalidJwt, String>{

}
