$(document).ready(function() {

	const table = $("#example").DataTable({
		serverSide: true,
		ajax: {
			url: '/blogs/allx',
			type: 'POST',
			contentType: 'application/json',
			data: function(d) {
				//console.log(d);
				return JSON.stringify(d);
			},
			dataSrc: function(json) {
				//console.log(json.data);
				return json.data;
			}
		},
		columns: [
			{ data: "id" },
			{ data: "title" },
			{ data: "summary" },
			{ data: "createdAt" },
			{ data: "publishedAt" },
		],
		rowCallback: function(row, data) {
			//if published
			if (data.publishedAt) {
				$("td:eq(1)", row).html(
					` <a href="/blog/${data.id}" class="text-blue-600">${data.title}</a>`
				);
			}
		},

		columnDefs: [
			{
				targets: [3, 4],
				render: function(data) {
					// console.log(data);

					if (data == null) return "";

					const date = new Date(data);

					const fullYear = date.getFullYear();
					const month = date.getMonth();
					const day = date.getDate().toString().padStart("2", 0);

					const hours = date.getHours().toString().padStart("2", 0);
					const minutes = date.getMinutes().toString().padStart("2", 0);
					const monthNames = [
						"Jan",
						"Feb",
						"Mar",
						"Apr",
						"May",
						"Jun",
						"Jul",
						"Aug",
						"Sep",
						"Oct",
						"Nov",
						"Dec",
					];

					const monthName = monthNames[month];

					const formatted = `${day} ${monthName} ${fullYear} ${hours}:${minutes}`;

					return formatted;
				},
			},
			{
				targets: [2], render: function(data) {

					if (!data) return ""; // Handle null or undefined data

					const withoutHtml = unescapeHtml(data);
					const plainText = stripHtml(withoutHtml); // Strip HTML tags
					console.log("Plain text:", plainText); // Log the plain text

					if (plainText.length >= 150) {
						return plainText.substring(0, 150) + "....."
					}

					return plainText; // Re
				}
			}
		],
	});

	function unescapeHtml(html) {
		const txt = document.createElement("textarea");
		txt.innerHTML = html;
		return txt.value;
	}
	function stripHtml(html) {
		const txt = document.createElement("div");
		txt.innerHTML = html;
		return txt.innerText || txt.textContent; // Use innerText or textContent for plain text
	}

	const tableComment = $("#comments").DataTable({
		serverSide: true,
		ajax: {
			url: "/allComments/0",
			dataSrc: function(json) {
				return json.data;
			},
		},

		columns: [
			{ data: "comment_id" },
			{ data: "content" },
			{ data: "created_at" },
		],
	});


	var isEditMode = false; // Track whether we are adding or editing
	var editRowId = null; // To store the row being edited


	const modal = document.getElementById("modal");
	const closeModalBtn = document.getElementById("closeModalBtn");

	// Show form for adding a new row
	$("#addNewBtn").on("click", function() {

		modal.classList.remove("hidden");
		$("#formTitle").text("Add New Blog");
		$("#blogform")[0].reset();
		isEditMode = false; // Switching to add mode

	});

	closeModalBtn.addEventListener("click", function() {
		modal.classList.add("hidden");
	});

	modal.addEventListener("click", function(e) {
		if (e.target === modal) {
			modal.classList.add("hidden");
		}
	});


	table.on("click", "tbody tr", (e) => {
		let classList = e.currentTarget.classList;

		if (classList.contains("selected")) {
			classList.remove("selected");

			tableComment.ajax.reload();

			console.log("unclicked");

			tableComment.ajax.url(`/allComments/0`).load();
		} else {
			table
				.rows(".selected")
				.nodes()
				.each((row) => row.classList.remove("selected"));
			classList.add("selected");

			console.log("row clicked");

			const tablesel = table.row(".selected");
			const tableData = tablesel.data();

			console.log("table data->", tableData)

			const blog_id = tableData.id;

			tableComment.ajax.url(`/allComments/${blog_id}`).load();
		}
	});




	document.querySelector("#editButton").addEventListener("click", function() {

		const tablesel = table.row(".selected");
		const tableData = tablesel.data();

		console.log("<--tablex->", tableData)

		if (tableData == undefined) {
			alert("please selected row");
		} else {
			$("#title").val(tableData.title);

			tinymce.get("summary").setContent(tableData.summary); //only where tiny mce applied
			tinymce.get("content").setContent(tableData.content); //only where tiny mce applied

			// Switch to edit mode
			isEditMode = true;
			editRowId = tableData.id;
			modal.classList.remove("hidden");

			$("#formTitle").text("Edit This Blog");

		}


	});

	console.log("is edit", isEditMode);

	$("#blogform").on("submit", function(e) {
		console.log("submit is Edit", isEditMode);

		e.preventDefault();
		tinymce.triggerSave();

		if (isEditMode) {
			console.log("editing");

			console.log("id", editRowId);

			var title = $("#title").val();
			var summary = $("#summary").val();
			var content = $("#content").val();

			const formData = {
				title: title,
				summary: summary,
				content: content,
			};

			// console.log("form data", formData);

			$.ajax({
				url: `/blogs/edit/${editRowId}`,
				method: "PUT",
				contentType: 'application/json',
				data: JSON.stringify(formData),
				success: function() {
					table.ajax.reload(null, false);
					modal.classList.add("hidden");
					$("#blogform")[0].reset();
				},
			});
		} else {
			var title = $("#title").val();
			var summary = $("#summary").val();
			var content = $("#content").val();

			const formData = {
				title: title,
				summary: summary,
				content: content,
			};

			console.log("form data", formData);

			$.ajax({
				url: "/blogs/post",
				method: "POST",
				contentType: 'application/json',
				data: JSON.stringify(formData),

				success: function(response) {
					table.ajax.reload(null, false);
					modal.classList.add("hidden");
					$("#blogform")[0].reset();



				},
				error: function(xhr, status, error) {
					console.error("Error adding row:", error);
				},
			});
		}

		// Get form values
	});

	$("#cancelBtn").on("click", function() {
		$("#addRowForm").hide();
	});

	//-->> delete and publish

	document
		.getElementById("deleteButton")
		.addEventListener("click", function() {
			const tablesel = table.row(".selected");
			const tableData = tablesel.data();

			if (tableData == undefined) {
				alert("please selected row");
			} else {
				const id = tableData.id;

				let confirmDelete = confirm("Do You Want to Delete?");

				if (confirmDelete) {

					$.ajax({
						url: `/blogs/delete/${id}`,
						method: "DELETE",
						success: function() {
							table.ajax.reload(null, false);
							tableComment.ajax.reload();
						},
					});
				}
			}
		});

	document
		.getElementById("publishButton")
		.addEventListener("click", function() {
			const tablesel = table.row(".selected");
			const tableData = tablesel.data();

			if (tableData == undefined) {
				alert("please selected row");
			} else {

				if (tableData.publishedAt == null) {
					const id = tableData.id;

					let confirmPublish = confirm("Do You Want to Publish?");

					if (confirmPublish) {

						$.ajax({
							url: `/blogs/publish/${id}`,
							method: "POST",
							success: function() {
								table.ajax.reload(null, false);
								tableComment.ajax.reload();
							},
						});
					}
				} else {
					alert("already published");
				}
			}
		});



	tinymce.init({
		selector: "textarea#content",
		height: 200,
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

	tinymce.init({
		selector: "textarea#summary",
		height: 200,
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


});
