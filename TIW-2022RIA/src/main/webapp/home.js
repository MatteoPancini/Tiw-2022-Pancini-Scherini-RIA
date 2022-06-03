{
    //avoid global scope

    // global page components
    let personalAlbumsList,
        communityAlbumsList,
        pageOrchestrator = new PageOrchestrator(); // controller

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

    function PersonalAlbumsList(
        _alert,
        _personalAlbumsList,
        _personalAlbumsBody
    ) {
        this.alert = _alert;
        this.personalAlbumsList = _personalAlbumsList;
        this.personalAlbumsBody = _personalAlbumsBody;

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
                            //TODO album details image list
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
                            //TODO album details image list
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
        };

        this.refresh = () => {
            personalAlbumsList.reset();
            communityAlbumsList.reset();
            personalAlbumsList.show();
            communityAlbumsList.show();
        };
    }
}
