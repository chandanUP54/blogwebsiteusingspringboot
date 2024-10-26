package com.datatable.blogs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.datatable.blogs.modal.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
	 List<Comment> findByBlogId(long blogId);
}
