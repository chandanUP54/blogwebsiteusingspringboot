package com.datatable.blogs.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datatable.blogs.modal.Blog;
import com.datatable.blogs.modal.Comment;
import com.datatable.blogs.repository.BlogRepository;
import com.datatable.blogs.repository.CommentRepository;
import com.datatable.blogs.services.BlogComment;

import jakarta.persistence.EntityNotFoundException;

@Service
public class BlogCommentImpl implements BlogComment {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private BlogRepository blogRepository;

	@Override
	public Comment createComment(Comment comment, long blogId) {
		Blog blog = blogRepository.findById(blogId).orElseThrow(() -> new EntityNotFoundException("Blog not found"));

		// Set the blog reference for the comment
		comment.setBlog(blog);

		// Save the comment to the repository
		return commentRepository.save(comment); // Make sure this is returning the saved comment
	}

	@Override
	public List<Comment> findAllCommentForABlog(long id) {
		List<Comment> comments = commentRepository.findByBlogId(id);
		return comments;
	}

	@Override
	public void deleteComment(long blogID, long commentID) {

		Blog blog = blogRepository.findById(blogID).get();

		System.out.println("blog" + blog);

		Comment comment = commentRepository.findById(commentID).get();

		commentRepository.delete(comment);

		// TODO Auto-generated method stub

	}

	@Override
	public Comment updateComment(Comment commentReq, long commentID, long blogID) {

		Comment comment = commentRepository.findById(commentID)
				.orElseThrow(() -> new EntityNotFoundException("Comment not found"));

		if (comment.getBlog().getId() == blogID) {
			comment.setComment(commentReq.getComment());
			commentRepository.save(comment);
		}
		return comment;
	}

}
