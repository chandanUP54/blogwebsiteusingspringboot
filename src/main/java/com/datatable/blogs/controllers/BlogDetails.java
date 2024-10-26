package com.datatable.blogs.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.datatable.blogs.modal.Blog;
import com.datatable.blogs.modal.Comment;
import com.datatable.blogs.services.BlogComment;
import com.datatable.blogs.services.BlogService;

@Controller
public class BlogDetails {

	@Autowired
	private BlogService blogService;

	@Autowired
	private BlogComment blogComment;

	// use templating here
	@GetMapping("/blog/{id}")
	public String blogDetailById(@PathVariable long id, Model model) {

		Blog blog = blogService.getBlogById(id);
		List<Comment> comments = blogComment.findAllCommentForABlog(id);

		model.addAttribute("blog", blog);
		model.addAttribute("comments", comments);

		return "blogpage";

	}
	
	
	
//	@GetMapping("/all/{blogId}")
//	public ResponseEntity<List<Comment>> blogCommentById(@PathVariable long blogId){
//		List<Comment> comments = blogComment.findAllCommentForABlog(blogId);
//
//		return ResponseEntity.status(HttpStatus.OK).body(comments);
//	}
     
	@PostMapping("/blog/{blogId}/comment")
	public ResponseEntity<Comment> postingComment(@RequestBody Comment commentRequest, @PathVariable long blogId) {
	    try {
	        
	        Comment comment = new Comment();
	        comment.setComment(commentRequest.getComment());

	        Comment savedComment = blogComment.createComment(comment, blogId);
	        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
	    } catch (Exception e) {
	        e.printStackTrace(); // Print the stack trace for debugging
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	    }
	}


	@DeleteMapping("/blog/{blogID}/comment/{commentID}")
	public ResponseEntity<String> deleteComment(@PathVariable long blogID, @PathVariable long commentID) {
		System.out.println("insdie delete");

		try {
			blogComment.deleteComment(blogID, commentID);

			return ResponseEntity.ok("Comment Deleted");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("COMMENT NOT DELETED ");
		}

	}

	@PutMapping("/blog/{blogID}/update/{commentID}")
	public ResponseEntity<Comment> editComment(@RequestBody Comment commentReq, @PathVariable long blogID,
			@PathVariable long commentID) {


		try {
			Comment comment=blogComment.updateComment(commentReq,commentID,blogID);
			return ResponseEntity.status(HttpStatus.OK).body(comment);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		
	}
}
