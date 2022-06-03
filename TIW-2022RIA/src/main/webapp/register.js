(function () {
    // avoid variables ending up in the global scope

    document.getElementById('submitButton').addEventListener('click', (e) => {
        //controlli lato client
        var email = document.forms['register']['email'].value;
        var username = document.forms['register']['username'].value;
        var pwd = document.forms['register']['pwd'].value;
        var confirmed_pwd = document.forms['register']['confirmed_pwd'].value;
        var errorMessage = document.getElementById('errorMessage');

        if (email == '' || username == '' || pwd == '' || confirmed_pwd == '') {
            //alert("Fields must not be empty.");
            return;
        } else if (pwd != confirmed_pwd) {
            //alert("Passwords don't match!.");
            errorMessage.textContent = "Passwords don't match!";
            console.log('Scrivo invalid passw');

            return;
        } else if (
            !/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(
                register.email.value
            )
        ) {
            //alert("Invalid email format!.");
            errorMessage.textContent = 'Invalid email format!';
            console.log('Scrivo invalid email');
            return;
        }

        makeCall('POST', 'CreateAccount', e.target.closest('form'), (x) => {
            if (x.readyState == XMLHttpRequest.DONE) {
                var message = x.responseText;
                switch (x.status) {
                    case 200:
                        alert(
                            'Registration process has been completed. You can now login!'
                        );

                        window.location.href = 'index.html';
                        break;
                    case 400: // bad request
                        document.getElementById('errorMessage').innerText =
                            message;
                        break;
                    case 401: // unauthorized
                        document.getElementById('errorMessage').innerText =
                            message;
                        break;
                    case 500: // server error
                        document.getElementById('errorMessage').innerText =
                            message;
                        break;
                }
            }
        });
    });
})();
