{
    //avoid global scope

    // global page components
    let personalAlbumsList,
        communityAlbumsList,
        pageOrchestrator = new PageOrchestrator(); // controller

    var albumImages, imagesToShow = [];
    var currPage = 0;
    var displayedAlbum;
    var showAddImg = false;

    window.addEventListener(
        'load',
        () => {
            if (sessionStorage.getItem('username') == null) {
                window.location.href = 'index.html';
            } else {
                pageOrchestrator.start();
                pageOrchestrator.refresh();
            }
        },
        false
    );

    document.getElementById("idHome").addEventListener("click", (e) => {
        pageOrchestrator.refresh();
    });

    document.querySelector("a[href='Logout']").addEventListener('click', () => {
        window.sessionStorage.removeItem('username');
    });

    function PersonalAlbumsList(
        _alert,
        _personalAlbumsList,
        _personalAlbumsBody
    ) {
        this.alert = _alert;
        this.personalAlbumsList = _personalAlbumsList;
        this.personalAlbumsBody = _personalAlbumsBody;
        this.albumDetails = null;

        this.reset = () => {
            this.personalAlbumsList.style.visibility = 'hidden';
        };

        this.show = () => {
            var self = this;

            makeCall('GET', 'GetPersonalAlbums', null, (req) => {
                var message = req.responseText;
                if (req.readyState == 4) {
                    if (req.status == 200) {
                        self.update(JSON.parse(message));
                    } else if (req.status == 500) {
                        self.alert.textContent =
                            'INTERNAL SERVER ERROR WHILE RETRIVING PERSONAL ALBUMS';
                    }
                }
            });
        };

        this.update = (albumsArray) => {
            var length = albumsArray.length,
                row,
                cell,
                linkCell,
                anchor;

            if (length == 0) {
                this.alert.textContent = 'No albums to display for now';
            } else {
                var self = this;
                albumsArray.forEach((album) => {
                    row = document.createElement('tr');
                    cell = document.createElement('td');
                    cell.textContent = album.title;
                    row.appendChild(cell);
                    cell = document.createElement('td');
                    cell.textContent = album.userAlbum;
                    row.appendChild(cell);
                    cell = document.createElement('td');
                    cell.textContent = album.creationDate;
                    row.appendChild(cell);
                    linkCell = document.createElement('td');
                    anchor = document.createElement('a');
                    linkCell.appendChild(anchor);
                    let linkText = document.createTextNode('OPEN');
                    anchor.appendChild(linkText);
                    anchor.setAttribute('idAlbum', album.idAlbum);
                    anchor.className = 'open_album_btn';
                    anchor.addEventListener(
                        'click',
                        (e) => {
                            currentAlbum = e.target.getAttribute('idAlbum');
                            displayedAlbum = this;
                            //TODO album details image list
                            self.albumDetails = new AlbumDetails({
                                alert: this.alert,
                                albumContainer: document.getElementById("albumContainer"),
                                imagesContainer: document.getElementById("imagesContainer"),
                                imagesContainerBody: document.getElementById("imagesContainerBody"),
                                newImageButton: document.getElementById("newImageButton"),
                            });
                            showAddImg = true;
                            self.albumDetails.show(currentAlbum);
                        },
                        false
                    );
                    anchor.href = '#';
                    row.appendChild(linkCell);
                    self.personalAlbumsBody.appendChild(row);
                });
                self.personalAlbumsBody.appendChild(row);
                self.personalAlbumsList.style.visibility = 'visible';
            }
        };


        document.getElementById("createNewImage").addEventListener("click", (e) => {

        });
    }


    function AlbumDetails(options) {
        this.alert = options["alert"];
        this.albumContainer = options["albumContainer"];
        this.imagesContainer = options["imagesContainer"];
        this.imagesContainerBody = options["imagesContainerBody"];
        this.newImageButton = options["newImageButton"];
        this.directionalButtons = new DirectionalButtons(document.getElementById("prevButton"), document.getElementById("nextButton"));


        this.show = function (albumId) {
            console.log("inizio la show");
            var currPage = 0;
            var self = this;
            document.getElementById("personalAlbums").style.visibility = "hidden";
            document.getElementById("communityAlbums").style.visibility = "hidden";
            document.getElementById("homePage").style.visibility = "hidden";
            document.getElementById("homePage").style.height = "0";

            if (!showAddImg) {
                document.getElementById("createNewImage").style.visibility = "hidden";
            } else {
                makeCall('GET', 'PostImage?album=' + albumId, null, (req) => {
                    var message = req.responseText;
                    if (req.readyState == 4) {
                        if (req.status == 200) {
                            console.log("tutto ok post album")
                        }
                    }
                });
            }


            makeCall('GET', 'GetAlbumPage?album=' + albumId + "&page=" + currPage, null, (req) => {
                var message = req.responseText;
                if (req.readyState == 4) {
                    if (req.status == 200) {
                        albumImages = JSON.parse(message);
                        self.create(albumImages);
                        this.directionalButtons.update();
                    } else if (req.status == 500) {
                        self.alert.textContent =
                            'INTERNAL SERVER ERROR WHILE RETRIVING PERSONAL ALBUMS';
                    }
                }
            });
        };


        this.create = function (albumImages) {
            var row, cell, i = 0, self = this, length = albumImages.length;
            //svuoto contenuto
            this.imagesContainerBody.innerHTML = "";

            imagesToShow = [];

            var j = 0;
            for (let img of albumImages) {
                if (j == 5) {
                    break;
                }
                imagesToShow.push(img);
                j++;
            }

            for (let img of imagesToShow) {
                console.log(i);
                if (i == 5) {
                    break;
                }

                //console.log(img.title);
                //console.log(img.path);

                let imgPath = img.path;
                let imgId = img.idUser;
                var params = "fileName=" + albumImages[i].path + "&userId=" + albumImages[i].idUser;

                console.log(params)

                //var formData = new FormData();
                //formData.append("fileName", imgPath);
                //formData.append("userId", imgId);

                //let folderPath = "/Users/matteopancini/Documents/TIWData/u";
                //console.log(folderPath);
                //let fullPath = folderPath.concat(img.idUser, "/", imgPath)

                row = document.createElement("tr");
                cell = document.createElement("td");
                cell.textContent = img.title;
                row.appendChild(cell);
                cell = document.createElement("img");

                /*
                cell.src = makeCall('GET', 'GetImage?fileName=' + imgPath + "&userId=" + imgId, null, (req) => {
                    var message = req.responseText;
                    if (req.readyState == 4) {
                        if (req.status == 200) {
                                //albumImages = JSON.parse(message);
                                //self.create(albumImages);
                        } else if (req.status == 500) {
                            self.alert.textContent =
                                'INTERNAL SERVER ERROR WHILE RETRIVING IMAGE';
                        }
                    }
                });
                */
                makeCall('GET', 'GetImage?fileName=' + albumImages[i].path + "&userId=" + albumImages[i].idUser, null, (req) => {
                    var message = req.responseText;
                    if (req.readyState == 4) {
                        if (req.status == 200) {
                            //albumImages = JSON.parse(message);
                            //cell.src = "GetImage?fileName=" + albumImages[i].path + "&userId=" + albumImages[i].idUser;
                            //console.log(cell.src);
                        } else if (req.status == 500) {
                            self.alert.textContent =
                                'INTERNAL SERVER ERROR WHILE RETRIVING IMAGE';
                        }
                    }
                });
                let imgSrc = "GetImage?fileName=" + albumImages[i].path + "&userId=" + albumImages[i].idUser;
                cell.src = imgSrc;
                cell.setAttribute("imageID", albumImages[i].idImage);
                cell.className = "thumbnail";

                let commentList = document.getElementById("comment_list");
                let imageDetailsContainer = document.getElementById("imageDetailsForm");

                // Open ImageDetails modal window on click
                cell.addEventListener("click", (e) => {
                    let currImageID = e.target.getAttribute("imageID");
                    console.log("click image, imageID=" + currImageID);
                    makeCall('GET', 'ShowImageDetails?image=' + currImageID, null, (req) => {
                        var message = req.responseText;
                        if (req.readyState == 4) {
                            if (req.status == 200) {
                                let imageDetails = JSON.parse(message);
                                console.log(imageDetails);
                                // make modal window visible
                                imageDetailsContainer.style.visibility = "visible";

                                // creator name
                                let imageTitle = document.getElementById("imageTitle");
                                imageTitle.innerHTML = imageDetails.title;

                                // image full size
                                let fullSizeImage = document.getElementById("selected_image");
                                fullSizeImage.src = imgSrc;

                                // creator name
                                let imageCreator = document.getElementById("imageCreator");
                                imageCreator.innerHTML = imageDetails.username;

                                // date
                                let imageDate = document.getElementById("imageDate");
                                imageDate.innerHTML = imageDetails.date;

                                // description
                                let imageDescription = document.getElementById("imageDescription");
                                imageDescription.innerHTML = imageDetails.description;

                            } else if (req.status == 500) {
                                self.alert.textContent =
                                    'INTERNAL SERVER ERROR WHILE RETRIVING IMAGE DETAILS';
                            }
                        }
                    });

                    // Get the list of comments related to the selected image
                    makeCall('GET', 'GetComments?image=' + currImageID, null, (req) => {
                        var message = req.responseText;
                        if (req.readyState == 4) {
                            if (req.status == 200) {
                                let comments = [];
                                comments = JSON.parse(message);
                                console.log(comments);
                                comments.forEach(comment => {
                                    let liElement = document.createElement("li");
                                    liElement.className = "comment";

                                    let user = document.createElement("h4");
                                    user.innerHTML = comment.username;

                                    let commentText = document.createElement("p");
                                    commentText.innerHTML = comment.text;

                                    liElement.appendChild(user);
                                    liElement.appendChild(commentText);

                                    commentList.appendChild(liElement);

                                });

                            } else if (req.status == 500) {
                                self.alert.textContent =
                                    'INTERNAL SERVER ERROR WHILE RETRIVING COMMENTS';
                            }
                        }
                    });

                    // Post a new comment
                    document.getElementById("sendCommentBtn").addEventListener("click", (e) => {
                        console.log("post a new comment");

                        let commentText = document.forms["comment_form"]["comment"].value;
                        console.log(commentText);

                        if (commentText == "") {
                            alert("Field must not be empty!");
                            return;
                        }

                        console.log('PostComment?image=' + currImageID);
                        console.log(e.target.closest('form'));
                        makeCall('POST', 'PostComment?image=' + currImageID, e.target.closest('form'), (x) => {
                            if (x.readyState == XMLHttpRequest.DONE) {
                                var message = x.responseText;
                                switch (x.status) {
                                    case 200:
                                        alert('Comment added!');
                                        imageDetailsContainer.style.visibility = "hidden";
                                        commentList.replaceChildren();
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

                    // close the modal window
                    document.getElementById("closeImageDetails").addEventListener("click", (e) => {
                        imageDetailsContainer.style.visibility = "hidden";
                        commentList.replaceChildren();
                    });

                });
                //cell.src =  fullPath;
                //console.log(cell.src);

                row.appendChild(cell);
                self.imagesContainerBody.appendChild(row);
                //TODO: aggiungere l'addEventListener a seconda cell
                i++;
                self.imagesContainerBody.appendChild(row);
            }

            self.albumContainer.style.visibility = 'visible';
            //this.newImageButton.className = "createImage";

            if (imagesToShow.length < 5) {
                self.directionalButtons.reset();
            }

        }


        this.update = function () {
            console.log("Entro in updates");
            var self = this;
            self.imagesContainerBody.innerHTML = "";
            imagesToShow = [];
            //console.log("Images to show size: " + imagesToShow.length);

            //let i = currPage;
            for (i = currPage * 5; i < (currPage * 5 + 5); i++) {
                console.log(i);
                imagesToShow.push(albumImages[i]);
                console.log("Images to show size: " + imagesToShow.length);
                row = document.createElement("tr");
                cell = document.createElement("td");
                cell.textContent = albumImages[i].title;
                row.appendChild(cell);
                cell = document.createElement("img");

                makeCall('GET', 'GetImage?fileName=' + albumImages[i].path + "&userId=" + albumImages[i].idUser, null, (req) => {
                    var message = req.responseText;
                    if (req.readyState == 4) {
                        if (req.status == 200) {
                            //albumImages = JSON.parse(message);
                            cell.src = "GetImage?fileName=" + albumImages[i].path + "&userId=" + albumImages[i].idUser;
                            //console.log(cell.src);
                        } else if (req.status == 500) {
                            self.alert.textContent =
                                'INTERNAL SERVER ERROR WHILE RETRIVING IMAGE';
                        }
                    }
                });
                cell.src = "GetImage?fileName=" + albumImages[i].path + "&userId=" + albumImages[i].idUser;

                //console.log(cell.src);
                cell.className = "thumbnail";
                //cell.src =  fullPath;
                //console.log(cell.src);

                row.appendChild(cell);
                self.imagesContainerBody.appendChild(row);

                if ((i + 1) == albumImages.length) {
                    break;
                }
            }
            //this.directionalButtons.update();
        }


    }

    function CommunityAlbumsList(
        _alert,
        _communityAlbumsList,
        _communitylAlbumsBody
    ) {
        this.alert = _alert;
        this.communityAlbumsList = _communityAlbumsList;
        this.communityAlbumsBody = _communitylAlbumsBody;

        this.reset = () => {
            this.communityAlbumsList.style.visibility = 'hidden';
        };

        this.show = () => {
            var self = this;

            makeCall('GET', 'GetOtherAlbums', null, (req) => {
                var message = req.responseText;
                if (req.readyState == 4) {
                    if (req.status == 200) {
                        self.update(JSON.parse(message));
                    } else if (req.status == 500) {
                        self.alert.textContent =
                            'INTERNAL SERVER ERROR WHILE RETRIVING PERSONAL ALBUMS';
                    }
                }
            });
        };

        this.update = (albumsArray) => {
            var length = albumsArray.length,
                row,
                cell,
                linkCell,
                anchor;

            if (length == 0) {
                this.alert.textContent = 'No albums to display for now';
            } else {
                var self = this;
                albumsArray.forEach((album) => {
                    row = document.createElement('tr');
                    cell = document.createElement('td');
                    cell.textContent = album.title;
                    row.appendChild(cell);
                    cell = document.createElement('td');
                    cell.textContent = album.userAlbum;
                    row.appendChild(cell);
                    cell = document.createElement('td');
                    cell.textContent = album.creationDate;
                    row.appendChild(cell);
                    linkCell = document.createElement('td');
                    anchor = document.createElement('a');
                    linkCell.appendChild(anchor);
                    let linkText = document.createTextNode('OPEN');
                    anchor.appendChild(linkText);
                    anchor.setAttribute('idAlbum', album.idAlbum);
                    anchor.className = 'open_album_btn';
                    anchor.addEventListener(
                        'click',
                        (e) => {
                            currentAlbum = e.target.getAttribute('idAlbum');
                            displayedAlbum = this;
                            //TODO album details image list
                            self.albumDetails = new AlbumDetails({
                                alert: this.alert,
                                albumContainer: document.getElementById("albumContainer"),
                                imagesContainer: document.getElementById("imagesContainer"),
                                imagesContainerBody: document.getElementById("imagesContainerBody"),
                                newImageButton: document.getElementById("newImageButton"),
                            });
                            self.albumDetails.show(currentAlbum);
                        },
                        false
                    );
                    anchor.href = '#';
                    row.appendChild(linkCell);
                    self.communityAlbumsBody.appendChild(row);
                });
                self.communityAlbumsBody.appendChild(row);
                self.communityAlbumsList.style.visibility = 'visible';
            }
        };
    }



    function DirectionalButtons(_next, _prev) {
        this.next = _next;
        this.prev = _prev;

        this.reset = function () {
            this.next.style.visibility = "hidden";
            this.prev.style.visibility = "hidden";
        }

        this.update = function () {
            if (albumImages.length > 5) {
                //console.log("dovrei essere qui perchè > 5");
                var self = this;
                console.log("CURRPAGE" + currPage);
                console.log("imageToShow: " + imagesToShow.length + " albumImages: " + albumImages.length + " currPage:" + currPage);


                document.getElementById("prevButton").style.visibility = "hidden";
                //console.log(this.next.style.visibility);
                document.getElementById("nextButton").style.visibility = "hidden";
                //console.log(this.prev.style.visibility);
                if (currPage > 0) {
                    //console.log("NON DEVO ESSRCI ENTRATO");
                    console.log("first IF")
                    document.getElementById("arrows").classList.add("arrowsDiv");
                    document.getElementById("prevButton").style.visibility = "visible";
                    document.getElementById("prevButton").classList.add("leftArrow");


                    /*
                    document.getElementById("prevButton").addEventListener("click", function () {
                        console.log("Click prev")
                        if (currPage === 0) {
                            return;
                        }
                        currPage--;
                        self.update();
                        displayedAlbum.albumDetails.update();
                        
                        //personalAlbumsList.albumDetails.update();
                        //communityAlbumsList.albumDetails.update();
                    });
                    */
                    document.getElementById("prevButton").onclick = function () {
                        console.log("Click prev")
                        if (currPage === 0) {
                            return;
                        }
                        currPage--;

                        displayedAlbum.albumDetails.update();
                        self.update();
                        //personalAlbumsList.albumDetails.update();
                        //communityAlbumsList.albumDetails.update();
                    };

                    if (albumImages.length - (currPage + 1) * 5 <= 0) {
                        document.getElementById("nextButton").style.visibility = "hidden";
                    }
                }

                if (imagesToShow.length == 5 && albumImages.length - (currPage + 1) * 5 > 0) {
                    //console.log("dovrei essere arrivato qui perchè > 5");
                    console.log("second IF")

                    document.getElementById("arrows").classList.add("arrowsDiv");
                    document.getElementById("nextButton").style.visibility = "visible";
                    document.getElementById("nextButton").classList.add("rightArrow");
                    //console.log(this.next.style.visibility);

                    /*
                    document.getElementById("nextButton").addEventListener("click", function () {
                        if ((currPage + 1) * 5 > albumImages.length) {
                            return;
                        }
                        console.log("Click next")

                        currPage++;
                        self.update();
                        displayedAlbum.albumDetails.update();
                        
                        //personalAlbumsList.albumDetails.update();
                        //communityAlbumsList.albumDetails.update();
                    });
                    */
                    document.getElementById("nextButton").onclick = function () {
                        if ((currPage + 1) * 5 > albumImages.length) {
                            return;
                        }
                        console.log("Click next")

                        currPage++;

                        displayedAlbum.albumDetails.update();
                        self.update();
                        //personalAlbumsList.albumDetails.update();
                        //communityAlbumsList.albumDetails.update();
                    };

                }
            }
        }
    }

    function PageOrchestrator() {
        this.start = () => {
            personalAlbumsList = new PersonalAlbumsList(
                document.getElementById('alert'),
                document.getElementById('personalAlbums'),
                document.getElementById('personalAlbumsBody')
            );

            communityAlbumsList = new CommunityAlbumsList(
                document.getElementById('alert'),
                document.getElementById('communityAlbums'),
                document.getElementById('communityAlbumsBody')
            );

            document.getElementById("albumContainer").style.visibility = "hidden";
        };

        this.refresh = () => {
            personalAlbumsList.reset();
            communityAlbumsList.reset();
            personalAlbumsList.show();
            communityAlbumsList.show();
            showAddImg = false;
        };
    }
}
