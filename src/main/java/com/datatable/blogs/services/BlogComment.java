package com.datatable.blogs.services;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.datatable.blogs.exception.UserException;
import com.datatable.blogs.modal.Comment;

public interface BlogComment {

	Comment createComment(Comment comment, long blogId, String jwt);

	List<Comment> findAllCommentForABlog(long id);

	ResponseEntity<?> deleteComment(long blogID, long commentID, String jwt) throws UserException;

	Comment updateComment(Comment commentReq, long commentID, long blogID, String jwt) throws UserException;

}
