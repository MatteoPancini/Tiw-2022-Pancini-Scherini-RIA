# Image Gallery Web App - RIA Version

The project consists of a web app that manages an image gallery.
Every user can register, login, create a new album, upload an image on an album, write a comment below an image, see other's user images.
The RIA version adds (in addition to client-side controls and modal pages) the possibility to reorder personal images.

The project was part of the final evaluation of the course Technology for Web Applications.

## 👤 Team
+ Matteo Pancini
+ Samuele Scherini

Prof. Piero Fraternali

## 🔨 Tools and Languages
+ Front End Programming Languages: ![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white) ![CSS3](https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white) ![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E)
+ Back End Programming Language: ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)

## 🐞 Bugs
During the final evaluation has been discovered a bug in the PostImage servlet that denies the possibility to use the web app in parallel on more than one different sessions open (while posting a new image).
This problem has been reported but not fixed.
The table sorting's servlet doesn't implement the auto-commit, which should complete the app in a better way.
