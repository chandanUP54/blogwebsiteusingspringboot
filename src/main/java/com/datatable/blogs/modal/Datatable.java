package com.datatable.blogs.modal;

import java.util.List;

import lombok.Data;

@Data
public class Datatable {
	private String draw;
	private int start;
	private int length;
	private Search search;
	private List<Order> order;
	private List<Column> columns;

	@Data
	public static class Column {
		private String data;
		private String name;
		private boolean searchable;
		private boolean orderable;
		private Search search;
	}

	@Data
	public static class Order {
		private int column;
		private String dir;
	}

	@Data
	public static class Search {
		private String value;
		private boolean regex;
	}

}


