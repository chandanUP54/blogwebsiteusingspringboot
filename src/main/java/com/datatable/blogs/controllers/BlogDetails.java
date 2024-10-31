package com.datatable.blogs.controllers;

import java.security.Principal;
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
import org.springframework.web.bind.annotation.RequestHeader;

import com.datatable.blogs.exception.UserException;
import com.datatable.blogs.modal.Blog;
import com.datatable.blogs.modal.Comment;
import com.datatable.blogs.modal.Users;
import com.datatable.blogs.services.BlogComment;
import com.datatable.blogs.services.BlogService;
import com.datatable.blogs.userservices.AuthenticationService;

@Controller
public class BlogDetails {

	@Autowired
	private BlogService blogService;

	@Autowired
	private BlogComment blogComment;

	@Autowired
	private AuthenticationService authenticationService;

	// use templating here
	@GetMapping("/blog/{id}")
	public String blogDetailById(@PathVariable long id, Model model, Principal p) {

		Blog blog = blogService.getBlogById(id);
		List<Comment> comments = blogComment.findAllCommentForABlog(id);

		if (blog.getPublishedAt() != null) {
			model.addAttribute("blog", blog);
		}

		model.addAttribute("comments", comments);

		String users;
		if (p != null) {
			users = p.getName();
			String email = users.substring(users.indexOf("email=") + 6, users.indexOf(",", users.indexOf("email=")));

			model.addAttribute("currentemail", email);
		}
		return "blogpage";

	}

	@PostMapping("/blog/{blogId}/comment")
	public ResponseEntity<Comment> postingComment(@RequestHeader("Authorization") String jwt,
			@RequestBody Comment commentRequest, @PathVariable long blogId) {
		jwt = jwt.substring(7);
		try {

			Comment comment = new Comment();
			comment.setComment(commentRequest.getComment());

			Comment savedComment = blogComment.createComment(comment, blogId, jwt);
			return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
		} catch (Exception e) {
			e.printStackTrace(); // Print the stack trace for debugging
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@DeleteMapping("/blog/{blogID}/comment/{commentID}")
	public ResponseEntity<?> deleteComment(@RequestHeader("Authorization") String jwt, @PathVariable long blogID,
			@PathVariable long commentID) {

		jwt = jwt.substring(7);

		try {
			blogComment.deleteComment(blogID, commentID, jwt);
			return ResponseEntity.ok("Comment Deleted");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("COMMENT NOT DELETED ");
		}

	}

	@PutMapping("/blog/{blogID}/update/{commentID}")
	public ResponseEntity<Comment> editComment(@RequestHeader("Authorization") String jwt,
			@RequestBody Comment commentReq, @PathVariable long blogID, @PathVariable long commentID)
			throws UserException {

		jwt = jwt.substring(7);
		Comment comment = blogComment.updateComment(commentReq, commentID, blogID, jwt);
		if (comment != null) {
			return ResponseEntity.status(HttpStatus.OK).body(comment);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

	}
}
