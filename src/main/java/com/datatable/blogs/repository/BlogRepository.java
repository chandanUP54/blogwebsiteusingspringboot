package com.datatable.blogs.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.datatable.blogs.modal.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

	@Query(value = "SELECT * FROM blog WHERE " + "LOWER(title) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR "
			+ "LOWER(summary) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR "
			+ "LOWER(content) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR "
			+ "DATE_FORMAT(created_at, '%d %b %Y %H:%i') LIKE LOWER(CONCAT('%', :searchValue, '%')) OR "
			+ "DATE_FORMAT(published_at, '%d %b %Y %H:%i') LIKE LOWER(CONCAT('%', :searchValue, '%'))", nativeQuery = true)
	Page<Blog> searchBlogs(String searchValue, Pageable pageable);

	Page<Blog> findByPublishedAtTrueOrderByCreatedAtDesc(Pageable pageable);
	
	List<Blog> findAllByPublishedAtTrue();

}
