package com.datatable.blogs.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datatable.blogs.modal.Blog;
import com.datatable.blogs.modal.Datatable;
import com.datatable.blogs.repository.BlogRepository;
import com.datatable.blogs.services.BlogService;

@RestController
@RequestMapping("/blogs")
public class DatatableORM {

	
	@Autowired
	private BlogService blogService;
	
	@Autowired
	private BlogRepository blogRepository; 
	
	@PostMapping("/post")
	public ResponseEntity<Blog> createBlog(@RequestBody Blog blog) {
	    
	    System.out.println("Submitting blog: " + blog);

	    try {
//	        Blog savedBlog = blogRepository.save(blog);
	    	 Blog savedBlog = blogService.createBlog(blog);
	        
	        return ResponseEntity.status(HttpStatus.CREATED).body(savedBlog);
	        
	    } catch (Exception e) {	        
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	    }
	}

	@PutMapping("/edit/{id}")
	public ResponseEntity<Blog> editblogById(@RequestBody Blog blog, @PathVariable long id) {

		try {
			Blog blog1 = blogService.editBlogById(blog, id);

			return ResponseEntity.ok(blog1);
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

	}

	@PostMapping("/publish/{id}")
	public ResponseEntity<Blog> publishBlog(@PathVariable long id) {

		try {
			Blog publishedBlog = blogService.publishBlogById(id);
			return ResponseEntity.ok(publishedBlog);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteBlogById(@PathVariable long id) {

		try {
			blogService.deleteBlogById(id);
			return ResponseEntity.ok("Blog deleted successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Blog not found.");
		}

	}

	@PostMapping("/allx")
	public ResponseEntity<Map<String, Object>> getAllBlogs(@RequestBody Datatable datatable) {
		
		return blogService.getAllBlogs(datatable);
	}

}