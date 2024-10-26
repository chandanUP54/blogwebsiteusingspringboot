package com.datatable.blogs.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.datatable.blogs.modal.Blog;
import com.datatable.blogs.modal.Datatable;

public interface BlogService {

	void deleteBlogById(long id);

	Blog publishBlogById(long id);

	Blog getBlogById(long id);

	Blog editBlogById(Blog blog, long id);

	ResponseEntity<Map<String, Object>> getAllBlogs(Datatable datatable);

	Blog createBlog(Blog blog);

	List<Blog> getRecentBlogs();

}
