function tableSort() {

    console.log("start");
    //const draggables = document.querySelectorAll('.draggable');
    const draggables = document.querySelectorAll(".draggable");
    const container = document.getElementById('imagesContainerBody');

    console.log(draggables);
    console.log(container);


    draggables.forEach(draggable => {
        draggable.addEventListener('dragstart', () => {
            console.log("Starting dragging")
            draggable.classList.add('dragging')
        })

        draggable.addEventListener('dragend', () => {
            console.log("Ending dragging")

            draggable.classList.remove('dragging')
            document.getElementById("saveOrder").classList.add("show");
        })
    })

    container.addEventListener('dragover', e => {
        e.preventDefault()
        const afterElement = getDragAfterElement(container, e.clientY)
        const draggable = document.querySelector('.dragging')
        if (afterElement == null) {
            container.appendChild(draggable)
        } else {
            container.insertBefore(draggable, afterElement)
        }
    })

    function getDragAfterElement(container, y) {
        const draggableElements = [...container.querySelectorAll('.draggable:not(.dragging)')]


        return draggableElements.reduce((closest, child) => {
            const box = child.getBoundingClientRect()
            const offset = y - box.top - box.height / 2
            if (offset < 0 && offset > closest.offset) {
                return { offset: offset, element: child }
            } else {
                return closest
            }

        }, { offset: Number.NEGATIVE_INFINITY }).element
    }

    document.getElementById("saveOrder").onclick = function () {
        var jsonReq = {};


        for (var i = 0; i < container.rows.length; i++) {
            var imgId = container.rows[i].querySelector(".thumbnail").getAttribute("imageID");
            var imgOrder = i;
            jsonReq[imgId] = imgOrder;
        }

        var request = JSON.stringify(jsonReq);

        console.log(request);

        var xhr = new XMLHttpRequest();

        xhr.open('POST', 'ChangeImagesOrder', true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.send(request);


        document.getElementById("saveOrder").style.visibility = "hidden";

    };



}