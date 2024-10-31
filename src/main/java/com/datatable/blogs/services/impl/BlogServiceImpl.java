package com.datatable.blogs.services.impl;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.datatable.blogs.modal.Blog;
import com.datatable.blogs.modal.Datatable;
import com.datatable.blogs.modal.Datatable.Order;
import com.datatable.blogs.repository.BlogRepository;
import com.datatable.blogs.services.BlogService;

@Service
public class BlogServiceImpl implements BlogService {

	@Autowired
	private BlogRepository blogRepository;

	@Override
	public void deleteBlogById(long id) {

		Blog blog = blogRepository.findById(id).get();

		blogRepository.delete(blog);
	}

	@Override
	public Blog publishBlogById(long id) {

		Blog blog = blogRepository.findById(id).get();
		blog.setPublishedAt(ZonedDateTime.now(ZoneId.of("UTC")));
		blogRepository.save(blog);

		return blog;

	}

	@Override
	public Blog getBlogById(long id) {
		Blog blog = blogRepository.findById(id).get();
		return blog;
	}

	@Override
	public Blog editBlogById(Blog blog, long id) {

		Blog blog2 = blogRepository.findById(id).get();

		blog2.setTitle(blog.getTitle());
		blog2.setSummary(blog.getSummary());
		blog2.setContent(blog.getContent());

		blogRepository.save(blog2);
		return blog2;
	}

	@Override
	public ResponseEntity<Map<String, Object>> getAllBlogs(Datatable datatable) {

		String draw = datatable.getDraw();
		int start = datatable.getStart();
		int length = datatable.getLength();
		String searchValue = datatable.getSearch() != null ? datatable.getSearch().getValue() : null;

		// Create Pageable object with sorting
		Sort sort = Sort.unsorted(); // Start with no sorting

		List<Order> orders = datatable.getOrder();

		for (Order order : orders) {
			String columnName = datatable.getColumns().get(order.getColumn()).getData();
			Sort.Direction direction = order.getDir().equalsIgnoreCase("desc") ? Sort.Direction.DESC
					: Sort.Direction.ASC;

			if (searchValue != null && !searchValue.isEmpty()) {
				System.out.println("searching");
				switch (columnName) {
				case "createdAt":
					sort = sort.and(Sort.by(direction, "created_at")); // Use the database column name
					break;
				case "publishedAt":
					sort = sort.and(Sort.by(direction, "published_at")); // Use the database column name
					break;
				default:
					System.out.println("other " + columnName);
					sort = sort.and(Sort.by(direction, columnName)); // Use as is for other columns
					break;
				}
			} else {
				System.out.println("not searching");
				sort = sort.and(Sort.by(direction, columnName));
			}
		}

		PageRequest pageable = PageRequest.of(start / length, length, sort);

		// Fetch filtered records
		Page<Blog> blogPage;
		if (searchValue != null && !searchValue.isEmpty()) {
			blogPage = blogRepository.searchBlogs(searchValue, pageable);
		} else {
			blogPage = blogRepository.findAll(pageable);
		}

		// Construct response
		Map<String, Object> response = new HashMap<>();
		response.put("draw", draw);
		response.put("recordsTotal", blogPage.getTotalElements());
		response.put("recordsFiltered", blogPage.getTotalElements());
		response.put("data", blogPage.getContent());

		return ResponseEntity.ok(response);
	}

	@Override
	public Blog createBlog(Blog blog) {

		Blog savedBlog = blogRepository.save(blog);

		return savedBlog;
	}

	@Override
	public List<Blog> getRecentBlogs() {

        var pageable = PageRequest.of(0,6); // 3 blog 
		
        return blogRepository.findByPublishedAtTrueOrderByCreatedAtDesc(pageable).getContent();
	}

	@Override
	public List<Blog> findAllBlogs() {
		return 	blogRepository.findAllByPublishedAtTrue();
	}

}
