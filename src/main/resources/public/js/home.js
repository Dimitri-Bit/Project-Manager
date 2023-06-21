function getProjectsOwner() {
    fetch("/project/owner", {
            method: "GET",
            credentials: "include"
        })
        .then(response => response.json())
        .then(data => {
            const ownerProjectsElement = document.getElementById("owner-projects");

            data.reverse().forEach(project => {
                const newCardElement = document.createElement("div");
                newCardElement.classList.add("col-md-4");

                newCardElement.innerHTML = `
            <div class="card mb-4">
                <div class="card-body">
                    <h5 class="card-title">${project.name}</h5>
                    <a href="/project/view/${project.id}" class="btn btn-primary"><i class="fas fa-location-arrow"></i> Pogledaj</a>
                    <a onclick="deleteProjectConfirm('${project.id}')" class="btn btn-danger"><i class="fas fa-trash" style="color: #ffffff;"></i></a>
                </div>
            </div>
            `;

                ownerProjectsElement.append(newCardElement);
            });
        })
        .catch(error => console.error(error));
}

function getProjectsMember() {
    fetch("/project/member", {
            method: "GET",
            credentials: "include"
        })
        .then(response => response.json())
        .then(data => {
            const memberProjects = document.getElementById("member-projects");

            data.reverse().forEach(project => {
                const newCardElement = document.createElement("div");
                newCardElement.classList.add("col-md-4");

                newCardElement.innerHTML = `
            <div class="card mb-4">
                <div class="card-body">
                    <h5 class="card-title">${project.name}</h5>
                    <a href="/project/view/${project.id}" class="btn btn-primary"><i class="fas fa-location-arrow"></i> Pogledaj</a>
                </div>
            </div>
            `;

                memberProjects.append(newCardElement);
            });
            hideSpinner();
        })
        .catch(error => console.error(error));
}

function createProject() {
    showSpinner();
    const projectName = document.getElementById("project-name").value;

    const requestHeaders = new Headers();
    requestHeaders.append("Content-Type", "application/json");

    const raw = JSON.stringify({
        "name": projectName
    });

    const requestOptions = {
        method: 'POST',
        credentials: 'include',
        headers: requestHeaders,
        body: raw,
        redirect: 'follow'
    };

    let success = false;

    fetch("/project/new", requestOptions)
        .then(response => {
            if (response.ok) {
                success = true;
            }
            return response.json();
        })
        .then(data => {
            if (success) {
                successAlert(data.message);
            } else {
                errorAlert(data.message);
            }
            hideSpinner();
        });
}

function deleteProjectConfirm(pId) {
    Swal.fire({
        title: 'Da li ste sigurni da želite izbrisati ovaj projekat?',
        icon: 'warning',
        showCancelButton: true,
        cancelButtonText: 'Odustani',
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Izbriši'
    }).then((result) => {
        if (result.isConfirmed) {
            deleteProject(pId);
        }
    })
}


function deleteProject(pId) {
    showSpinner();

    const requestHeaders = new Headers();
    requestHeaders.append("Content-Type", "application/json");

    const raw = JSON.stringify({
        "projectId": pId
    });

    const requestOptions = {
        method: 'POST',
        credentials: 'include',
        headers: requestHeaders,
        body: raw,
        redirect: 'follow'
    };

    let success = false;

    fetch("/project/delete", requestOptions)
        .then(response => {
            if (response.ok) {
                success = true;
            }
            return response.json();
        })
        .then(data => {
            if (success) {
                successAlert(data.message);
            } else {
                errorAlert(data.message);
            }
            hideSpinner();
        })
        .catch(error => {
            console.error('Error:', error);
        });
}