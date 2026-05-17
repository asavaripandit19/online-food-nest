package com.food.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.food.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	boolean existsByUsername(String username);

	boolean existsByMobile(String mobile);

	boolean existsByEmail(String email);

	Optional<User> findByUsername(String username);

	Optional<User> findByMobile(String mobile);

	Optional<User> findByEmail(String email);
    
    
}
