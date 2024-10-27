package com.datatable.blogs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.datatable.blogs.modal.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
	
	@Query(value = "select * from users where email = ?1", nativeQuery = true)
	Optional<Users> findByEmail(String email);

}
