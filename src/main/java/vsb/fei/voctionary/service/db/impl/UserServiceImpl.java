package vsb.fei.voctionary.service.db.impl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import vsb.fei.voctionary.model.User;
import vsb.fei.voctionary.repository.UserRepository;
import vsb.fei.voctionary.service.db.UserService;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	private final EntityManager entityManager;
	
	@Override
	public UserDetails loadUserByUsername(String email) {
		return findByEmail(email);
	}

	@Override
	@Transactional
	public User findByEmail(String email) {
		TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
		query.setParameter("email", email);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@Override
	public User createUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public User updateUser(User user) {
		return userRepository.save(user);
	}
	
	@Override
	public void deleteAll() {
		userRepository.deleteAll();
	}

}
