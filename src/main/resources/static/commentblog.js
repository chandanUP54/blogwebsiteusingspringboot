$(document).ready(function() {



	function getCookie(cname) {
		let name = cname + "=";

		let ca = document.cookie.split(';');

		for (let i = 0; i < ca.length; i++) {
			let c = ca[i];
			while (c.charAt(0) == ' ') {
				c = c.substring(1);
			}
			if (c.indexOf(name) == 0) {
				return c.substring(name.length, c.length);
			}
		}
	}

	var jwt = getCookie("token");
	console.log("jwt:-> " + jwt)



	tinymce.init({
		selector: "textarea#comment",
		height: 300,
		plugins: [
			"advlist",
			"autolink",
			"lists",
			"link",
			"image",
			"charmap",
			"preview",
			"anchor",
			"searchreplace",
			"visualblocks",
			"code",
			"fullscreen",
			"insertdatetime",
			"media",
			"table",
			"help",
			"wordcount",
		],
		toolbar:
			"undo redo | blocks | " +
			"bold italic backcolor | alignleft aligncenter " +
			"alignright alignjustify | bullist numlist outdent indent | " +
			"removeformat | help",
		content_style:
			"body { font-family:Helvetica,Arial,sans-serif; font-size:16px }",
	});

	var editMode = false;
	var commentIdForEdit = null;
	var blog_id = null;
	const userElements = document.querySelectorAll('.user');

	userElements.forEach(element => {
		blog_id = element.getAttribute('data-id'); // Fetch the data-id attribute

	});

	blog_id = parseInt(blog_id);


	$("#commentForm").on("submit", function(event) {

		event.preventDefault();
		tinymce.triggerSave();

		const formData = {
			blog: {
				id: blog_id
			},
			comment: $("#comment").val(),
		};

		console.log("form data->", formData)

		if (editMode) {
			console.log("updating--->>>");

			$.ajax({
				url: `/blog/${blog_id}/update/${commentIdForEdit}`,
				method: 'PUT',
				headers: {
					'Authorization': `Bearer ${jwt}`
				},
				contentType: "application/json", // Set the content type to JSON
				data: JSON.stringify(formData),
				success: function(data, textStatus, xhr) {

					document.getElementById(`content-${data.commentId}`).innerHTML = data.comment

					reset()
				},
				error: function(xhr, textStatus, errorThrown) {
					console.log('Error in Operation');
				}
			});


		} else {
			$.ajax({
				url: `/blog/${blog_id}/comment`, // Use dynamic blog ID
				method: "POST",
				headers: {
					'Authorization': `Bearer ${jwt}`
				},
				contentType: "application/json", // Set the content type to JSON
				data: JSON.stringify(formData), // Stringify the formData object
				success: function(data) {

					console.log("data", data)

					const allcomments = document.getElementById("all-comments");
					// Create new div
					const newComment = document.createElement("div");

					newComment.id = `comment-${data.commentId}`;
					newComment.setAttribute("data-id", data.commentId);
					newComment.classList.add("bg-gray-50", "p-4", "rounded", "mb-2", "shadow", "hero");


					newComment.innerHTML = `<h3  class="font-semibold" id="content-${data.commentId}">${data.comment}</h3>`;

					const newDiv = document.createElement("div");

					newDiv.classList.add("flex", "justify-between", "mt-2");


					const button = document.createElement("button");

					button.id = `button-${data.commentId}`
					button.setAttribute("delete-id", data.commentId)
					button.classList.add("delete-comment", "text-red-500")

					button.textContent = "Delete"; // Set the button

					//newComment.appendChild(button);
					newDiv.appendChild(button);
					//adding update button

					const update = document.createElement("button")

					update.id = `update-${data.commentId}`
					update.setAttribute("update-id", data.commentId)
					update.classList.add("update-comment", "text-blue-500")

					update.textContent = "Edit"; // Set the button

					//newComment.appendChild(update
					newDiv.appendChild(update)

					//allcomments.appendChild(newComment);

					newComment.appendChild(newDiv);
					allcomments.appendChild(newComment);
					reset()
				},

			});
		}


	});


	function reset() {
		editMode = false;
		$("#comment").val("");
		$("#saveBtn").html("New Comment")
		$("#heading-comment").html("Add Comment")
		tinymce.get("comment").setContent("");
	}

	$("#resetBtn").on("click", function() {
		reset()
	})

	//delete functionalty
	document.addEventListener("click", function(event) {

		if (event.target.classList.contains("delete-comment")) {
			const commentId = event.target.getAttribute("delete-id");
			console.log("comment id", commentId);

			$.ajax({
				url: `/blog/${blog_id}/comment/${commentId}`,
				method: "DELETE",
				headers: {
					'Authorization': `Bearer ${jwt}`
				},
				success: function() {
					console.log("Comment deleted successfully");
					const commentElement = document.getElementById(`comment-${commentId}`)
					commentElement.remove()
					reset()
				},
				error: function() {
					console.log("Error deleting comment");
				},
			});
		}


		if (event.target.classList.contains("update-comment")) {
			const commentId = event.target.getAttribute("update-id");
			console.log("update comment id", commentId);

			editMode = true;

			$("#heading-comment").html("Edit This Comment")
			$("#saveBtn").html("Edit Comment")

			commentIdForEdit = commentId;

			const val = document.getElementById(`content-${commentId}`).innerHTML;

			tinymce.get("comment").setContent(val);

			//$("#comment").val(val);


		}
	});

});