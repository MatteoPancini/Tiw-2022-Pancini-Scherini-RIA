/**
 * Login management RIA
 */

(function () {
    // avoid variables ending up in the global scope

    document.getElementById('loginButton').addEventListener('click', (e) => {
        //console.log("Parte js");
        var form = e.target.closest('form');
        if (form.checkValidity()) {
            makeCall('POST', 'CheckLogin', e.target.closest('form'), (x) => {
                if (x.readyState == XMLHttpRequest.DONE) {
                    var message = x.responseText;
                    switch (x.status) {
                        case 200:
                            sessionStorage.setItem('username', message);
                            window.location.href = 'home.html';
                            break;
                        case 400: // bad request
                            //alert("sendError");
                            document.getElementById(
                                'errorMessage'
                            ).textContent = message;
                            break;
                        case 401: // unauthorized
                            //alert("sendError");

                            document.getElementById(
                                'errorMessage'
                            ).textContent = message;
                            break;
                        case 500: // server error
                            //alert("sendError");

                            document.getElementById(
                                'errorMessage'
                            ).textContent = message;
                            break;
                    }
                }
            });
        } else {
            form.reportValidity();
        }
    });
})();
