const url = window.location.href;
let id = url.substring(url.lastIndexOf('/') + 1);

if (/[?#/]$/.test(id)) { // Check if the last character is ?, #, or /
    id = id.slice(0, -1); // Remove the last character
}

function getProjectMembers() {
    fetch(`/project/members/${id}`, {
            method: 'GET',
            credentials: 'include'
        })
        .then(response => response.json())
        .then(data => {
            const usersList = document.getElementById('users-list');
            data.forEach(user => {
                console.log(`test1 ${user.image}`);
                if (typeof user.image === 'undefined') {
                    user.image = "/public/images/avatar.png";
                    console.log(`test ${user.image}`);
                }
                const newUser = document.createElement('li');
                newUser.classList.add('list-group-item', 'd-flex', 'justify-content-between', 'align-items-center');
                newUser.innerHTML = `
        <div class="d-flex align-items-center">
          <img src="${user.image}" alt="User Image" class="rounded-circle mr-3 avatar-image">
          <div>
            <div>${user.name}</div>
            <div class="text-muted small">${user.username}</div>
          </div>
        </div>
      `;
                usersList.appendChild(newUser);
            });
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function getGroups() {
    fetch(`/group/${id}`, {
            method: 'GET',
            credentials: 'include'
        })
        .then(response => response.json())
        .then(data => {
            const groupList = document.getElementById('group-list');
            data.forEach(group => {
                const newGroup = document.createElement('div');
                newGroup.classList.add('col-sm-6');
                newGroup.classList.add('col-md-4');
                newGroup.classList.add('col-lg-4');
                newGroup.classList.add('mb-4');

                newGroup.innerHTML = `
          <div class="card border-secondary rounded-lg">
            <div class="card-body" style="height: 150px; overflow: auto;">
              <h5 class="card-title">${group.name}</h5>
              <p class="card-text">${group.description}</p>
            </div>
            <div class="card-footer bg-transparent border-0 d-flex justify-content-between align-items-center">
              <div class="text-muted">${group.creatorEmail}</div>
              <div>
                <a href="/project/task/${group.id}" class="btn btn-primary">
                  <i class="fas fa-location-arrow"></i>
                </a>
                <a class="btn btn-danger" onclick="deleteGroupConfirm('${group.id}')">
                <i class="fas fa-trash" style="color: #ffffff;"></i>
                </a>
              </div>
            </div>
          </div>
        `;
                groupList.appendChild(newGroup);
            });
            hideSpinner();
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function sendInvite() {
    showSpinner();
    const email = document.getElementById("invite-email-address").value;

    const requestHeaders = new Headers();
    requestHeaders.append("Content-Type", "application/json");

    const raw = JSON.stringify({
        "projectId": id,
        "invitedEmail": email
    });

    const requestOptions = {
        method: 'POST',
        credentials: 'include',
        headers: requestHeaders,
        body: raw,
        redirect: 'follow'
    };

    let success = false;

    fetch("/invite/new", requestOptions)
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

function createGroup() {
    showSpinner();
    const groupName = document.getElementById("group-name").value;
    const groupDescription = document.getElementById("group-description").value;

    const requestHeaders = new Headers();
    requestHeaders.append("Content-Type", "application/json");

    const raw = JSON.stringify({
        "name": groupName,
        "description": groupDescription,
        "projectId": id
    });

    const requestOptions = {
        method: 'POST',
        credentials: 'include',
        headers: requestHeaders,
        body: raw,
        redirect: 'follow'
    };

    let success = false;

    fetch("/group/new", requestOptions)
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

function deleteGroupConfirm(tId) {
    Swal.fire({
        title: 'Da li ste sigurni da želite izbrisati ovu kategoriju zadataka?',
        icon: 'warning',
        showCancelButton: true,
        cancelButtonText: 'Odustani',
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Izbriši'
    }).then((result) => {
        if (result.isConfirmed) {
            deleteGroup(tId);
        }
    })
}

function deleteGroup(tId) {
    showSpinner();

    const requestHeaders = new Headers();
    requestHeaders.append("Content-Type", "application/json");

    const raw = JSON.stringify({
        "groupId": tId
    });

    const requestOptions = {
        method: 'POST',
        credentials: 'include',
        headers: requestHeaders,
        body: raw,
        redirect: 'follow'
    };

    let success = false;

    fetch("/group/delete", requestOptions)
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