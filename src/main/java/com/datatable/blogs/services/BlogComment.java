package com.datatable.blogs.services;

import java.util.List;

import com.datatable.blogs.modal.Comment;

public interface BlogComment {

	Comment createComment(Comment comment, long blogId);

	List<Comment> findAllCommentForABlog(long id);

	void deleteComment(long blogID, long commentID);

	Comment updateComment(Comment commentReq, long commentID, long blogID);

}
