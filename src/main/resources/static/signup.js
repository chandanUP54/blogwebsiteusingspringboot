document.getElementById("signupform").addEventListener("submit",function(e){
		e.preventDefault();
		 const email = document.getElementById('email').value;
	        const password = document.getElementById('password').value;

	        fetch('/signup', {
	            method: 'POST',
	            headers: {
	                'Content-Type': 'application/json'
	            },
	            body: JSON.stringify({ email, password })
	        })
	        .then(response => {
	            if (!response.ok) {
	                throw new Error('Network response was not ok');
	            }
	            return response.json();
	        })
	        .then(data => {
	            // Handle successful signup (e.g., redirect, show a message)
	            console.log('Signup successful');
				
	        })
	        .catch(error => {
	            console.error('There was a problem with the signup request:', error);
	        });
	})