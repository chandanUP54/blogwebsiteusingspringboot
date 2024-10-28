document.getElementById("signinform").addEventListener("submit",function(e){
		e.preventDefault();
		 const username = document.getElementById('username').value;
	        const password = document.getElementById('password').value;

	        fetch('/signin', {
	            method: 'POST',
	            headers: {
	                'Content-Type': 'application/json'
	            },
	            body: JSON.stringify({ username, password })
	        })
	        .then(response => {
	            if (!response.ok) {
	                throw new Error('Network response was not ok');
	            }
	            return response.json();
	        })
	        .then(data => {
	            // Handle successful signup (e.g., redirect, show a message)
	            console.log('Signin successful',data.token);
	            localStorage.setItem('jwtToken', data.token); // Store token
	            window.location.href = '/';
	        })
	        .catch(error => {
	            console.error('There was a problem with the signin request:', error);
	        });
	})