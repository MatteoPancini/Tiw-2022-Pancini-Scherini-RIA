(function () {
    // avoid variables ending up in the global scope
    let albumForm = document.getElementById("modalAlbumForm");

    console.log(document.getElementById("createNewAlbum"));

    document.getElementById("createNewAlbum").addEventListener("click", (e) => {
        albumForm.classList.add("show");
    });

    document.getElementById("closeForm").addEventListener("click", (e) => {
        albumForm.classList.remove("show");
    });


    document.getElementById("submitAlbum").addEventListener("click", (e) => {

        //controlli lato client
        var albumTitle = document.forms["newAlbumForm"]["albumTitle"].value;

        console.log(albumTitle);

        if (albumTitle == "") {
            alert("Title must not be empty!");
            return;
        }


        makeCall('POST', 'PostAlbum', e.target.closest('form'), (x) => {
            if (x.readyState == XMLHttpRequest.DONE) {
                var message = x.responseText;
                switch (x.status) {
                    case 200:
                        alert(
                            'Album added to your library! Enjoy'
                        );
                        window.location.href = 'home.html';
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


    let imageForm = document.getElementById("modalImageForm");

    console.log(document.getElementById("createNewImage"));

    document.getElementById("createNewImage").addEventListener("click", (e) => {
        imageForm.classList.add("show");
    });

    document.getElementById("closeIMGForm").addEventListener("click", (e) => {
        imageForm.classList.remove("show");
    });


    document.getElementById("submitImage").addEventListener("click", (e) => {

        //controlli lato client
        let imageTitle = document.forms["newImageForm"]["imageTitle"].value;
        console.log(imageTitle);

        let imageDescr = document.forms["newImageForm"]["description"].value;
        console.log(imageDescr);

        let imagePath = document.forms["newImageForm"]["file"].value;
        console.log(imagePath);



        if (imageTitle == "" || imageDescr == "" || imagePath == "") {
            alert("Fields must not be empty!");
            return;
        }


        makeCall('POST', 'PostImage', e.target.closest('form'), (x) => {
            if (x.readyState == XMLHttpRequest.DONE) {
                var message = x.responseText;
                switch (x.status) {
                    case 200:
                        alert(
                            'Image added to your library! Enjoy'
                        );
                        window.location.href = 'home.html';
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
