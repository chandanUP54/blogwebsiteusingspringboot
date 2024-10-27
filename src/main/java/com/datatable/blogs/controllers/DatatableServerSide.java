package com.datatable.blogs.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.datatable.blogs.modal.Blog;
import com.datatable.blogs.modal.Datatable;
import com.datatable.blogs.modal.Datatable.Order;

@Controller
@RequestMapping("/blogs")
public class DatatableServerSide {

	private static final String DB_URL = "jdbc:mysql://localhost:3306/blogs";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "admin";

	@PostMapping("/all")
	public ResponseEntity<Map<String, Object>> getAllBlogs(@RequestBody Datatable datatable) {
		String draw = datatable.getDraw();
		int start = datatable.getStart();
		int length = datatable.getLength();
		String searchValue = datatable.getSearch() != null ? datatable.getSearch().getValue() : null;

		List<Blog> blogs = new ArrayList<>();
		String baseQuery = "SELECT id, title, summary, content, created_at, published_at FROM blog";
		String countQuery = "SELECT COUNT(*) FROM blog";
		String searchQuery = "";
		String limitQuery = " LIMIT ? OFFSET ?";

		// Build search query if search value is provided
		if (searchValue != null && !searchValue.isEmpty()) {
			System.out.println("Inside search logic");
			searchQuery = " WHERE (title LIKE ? OR summary LIKE ? OR content LIKE ? OR DATE_FORMAT(created_at, '%d %b %Y %H:%i') LIKE ?)";
		}

		// ordering
		String query1 = " ";
		List<Order> orders = datatable.getOrder();
		if (orders != null && !orders.isEmpty()) {
			for (Order order : orders) {
				int columnIndex = order.getColumn();
				String direction = order.getDir().equalsIgnoreCase("desc") ? "DESC" : "ASC";
				String columnName1 = datatable.getColumns().get(columnIndex).getData();
				query1 += (" ORDER BY " + columnName1 + " " + direction);
			}
		}

		// Fetch filtered records
		String query = baseQuery + searchQuery + query1 + limitQuery;

		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				PreparedStatement stmt = connection.prepareStatement(query)) {

			// searching logic
			if (searchValue != null && !searchValue.isEmpty()) {
				String searchPattern = "%" + searchValue + "%";
				stmt.setString(1, searchPattern);
				stmt.setString(2, searchPattern);
				stmt.setString(3, searchPattern);
				stmt.setString(4, searchPattern);

			}

			// Set pagination parameters,1,2 for setInt
			stmt.setInt(searchValue != null && !searchValue.isEmpty() ? 5 : 1, length);
			stmt.setInt(searchValue != null && !searchValue.isEmpty() ? 6 : 2, start);

//			System.out.println("Overall Query: " + stmt);

			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				blogs.add(mapResultSetToBlog(resultSet));
			}
		} catch (SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Database not fetched: " + e.getMessage()));
		}

		// calculating total record
		int totalRecords;
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				PreparedStatement countStmt = connection.prepareStatement(countQuery)) {

			ResultSet countResult = countStmt.executeQuery();
			countResult.next();
			totalRecords = countResult.getInt(1);
		} catch (SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Error fetching total records: " + e.getMessage()));
		}

		// Construct response
		Map<String, Object> response = Map.of("draw", draw, "recordsTotal", totalRecords, "recordsFiltered",
				totalRecords, "data", blogs);

		System.out.println("blogs-->>" + blogs);

		return ResponseEntity.ok(response);
	}

	private Blog mapResultSetToBlog(ResultSet resultSet) throws SQLException {
		Blog blog = new Blog();
		blog.setId(resultSet.getLong("id"));
		blog.setTitle(resultSet.getString("title"));
		blog.setSummary(resultSet.getString("summary"));
		blog.setContent(resultSet.getString("content"));

		System.out.println("result->" + resultSet.getTimestamp("created_at"));

		Timestamp createdAt = resultSet.getTimestamp("created_at");
		blog.setCreatedAt(createdAt != null ? createdAt.toInstant().atZone(ZoneId.of("UTC")) : null);

		Timestamp publishedAt = resultSet.getTimestamp("published_at");
		blog.setPublishedAt(publishedAt != null ? publishedAt.toInstant().atZone(ZoneId.of("Asia/Kolkata")) : null);
		return blog;
	}
}
