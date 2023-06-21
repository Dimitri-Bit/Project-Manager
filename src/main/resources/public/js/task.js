const url = window.location.href;
let id = url.substring(url.lastIndexOf('/') + 1);

if (/[?#/]$/.test(id)) { // Check if the last character is ?, #, or /
    id = id.slice(0, -1); // Remove the last character
}

function getTasks() {
    fetch(`/task/${id}`, {
            method: 'GET',
            credentials: 'include'
        })
        .then(response => response.json())
        .then(data => {
            data.reverse().forEach(task => {
                let assignedTo = task.assignedEmails;
                let description = task.description;
                let name = task.name;
                createTask(name, description, assignedTo, task.finished, task.id);
                console.log(`test: ${task.finished}`)
            })
        })
        .catch(error => {
            console.error('Error:', error);
        })
}

function getProjectMembers() {
    fetch(`/group/members/${id}`, {
            method: 'GET',
            credentials: 'include'
        })
        .then(response => response.json())
        .then(data => {
            const selectList = document.getElementById('assigneeSelect');
            data.forEach(user => {
                const option = document.createElement('option');
                option.text = `${user.username}`;
                selectList.appendChild(option);
            });
            hideSpinner();
        })
        .catch(error => {
            console.error('Error:', error);
        })
}

function createNewTask() {
    showSpinner();
    const requestHeaders = new Headers();
    requestHeaders.append("Content-Type", "application/json");

    const formData = getCreateTaskFormData();

    const raw = JSON.stringify({
        "taskGroupId": id,
        "name": formData.taskName,
        "description": formData.taskDescription,
        "assignedEmails": formData.assignees
    });

    const requestOptions = {
        method: 'POST',
        credentials: 'include',
        headers: requestHeaders,
        body: raw,
        redirect: 'follow'
    };

    let success = false;

    fetch("/task/new", requestOptions)
        .then(response => {
            if (response.ok) {
                success = true;
            }
            return response.json();
        })
        .then(data => {
            if (success) {
                successAlert(data.message)
            } else {
                errorAlert(data.message) //
            }
            hideSpinner();
        });
}

function createTask(name, description, assignedTo, status, id) {
    // create task card
    let taskCard = document.createElement("div");
    taskCard.classList.add("col-sm-12", "col-md-6", "col-lg-4", "mb-3");
    let badgeColor = "primary";
    let badgeText = "Izrada";
    let buttonHTML = `
    <a href="#" onclick="finishTask('${id}')" class="card-link">Završi</a>
  `;

    if (status) {
        badgeColor = "success";
        badgeText = "Gotovo";
        buttonHTML = `
      <a href="#" onclick="openTask('${id}')" class="card-link">Otvori</a>
    `;
    }

    taskCard.innerHTML = `
    <div class="card">
      <div class="card-body">
        <h5 class="card-title">${name}</h5>
        <h6 class="card-subtitle mb-2 text-muted">Dodjeljen:</h6>
        <ul class="list-unstyled mb-3">
          ${assignedTo.map(user => `<li>${user}</li>`).join("")}
        </ul>
        <p class="card-text" style="max-height: 100px; overflow-y: auto;">${description}</p>
        ${buttonHTML}
        <a href="#" class="card-link" onclick="deleteTask('${id}')">Obriši</a>
        <a href="#" class="card-link float-right"><span class="badge badge-${badgeColor}">${badgeText}</span></a>
      </div>
    </div>
  `;

    // add task card to container
    let container = document.getElementById("tasks-container");
    container.appendChild(taskCard);
}

function getCreateTaskFormData() {
    // Get the task name input value
    const taskNameInput = document.getElementById("taskNameInput");
    const taskName = taskNameInput.value;

    // Get the task description input value
    const taskDescriptionInput = document.getElementById("taskDescriptionInput");
    const taskDescription = taskDescriptionInput.value;

    // Get the assignee select value
    const assigneeSelect = document.getElementById("assigneeSelect");
    const assignees = [];
    for (let i = 0; i < assigneeSelect.options.length; i++) {
        if (assigneeSelect.options[i].selected) {
            assignees.push(assigneeSelect.options[i].text);
        }
    }

    console.log(`name ${taskName} description ${taskDescription} assigned ${assignees}`);

    // Return an object with the form data
    return {
        taskName: taskName,
        taskDescription: taskDescription,
        assignees: assignees
    };
}

function finishTask(id) {
    showSpinner();
    const requestHeaders = new Headers();
    requestHeaders.append("Content-Type", "application/json");

    const raw = JSON.stringify({
        "taskId": id
    });

    const requestOptions = {
        method: 'POST',
        credentials: 'include',
        headers: requestHeaders,
        body: raw,
        redirect: 'follow'
    };

    let success = false;

    fetch("/task/finish", requestOptions)
        .then(response => {
            if (response.ok) {
                success = true;
            }
            return response.json();
        })
        .then(data => {
            if (success) {
                successAlert(data.message);
                console.log('test');
            } else {
                errorAlert(data.message);
            }
            hideSpinner();
        });
}

function openTask(id) {
    showSpinner();
    const requestHeaders = new Headers();
    requestHeaders.append("Content-Type", "application/json");

    const raw = JSON.stringify({
        "taskId": id
    });

    const requestOptions = {
        method: 'POST',
        headers: requestHeaders,
        credentials: 'include',
        body: raw,
        redirect: 'follow'
    };

    let success = false;
    fetch("/task/open", requestOptions)
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

function deleteTask(id) {
    showSpinner();
    const requestHeaders = new Headers();
    requestHeaders.append("Content-Type", "application/json");

    const raw = JSON.stringify({
        "taskId": id
    });

    const requestOptions = {
        method: 'POST',
        headers: requestHeaders,
        body: raw,
        redirect: 'follow'
    };

    let success = false;
    fetch("/task/delete", requestOptions)
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

async function recommendUsers() {
    showSpinner();

    const requestHeaders = new Headers();
    requestHeaders.append("Content-Type", "application/json");

    const formData = getCreateTaskFormData();

    if (!checkStringLength(formData.taskDescription)) {
        errorAlert("Deskripcija zadatka nije dovoljno duga");
    } else {

        try {
            const membersResponse = await fetch(`/group/members/${id}`, {
                method: 'GET',
                credentials: 'include'
            });
            const membersData = await membersResponse.json();

            const users = membersData.map(user => user.username);

            const raw = JSON.stringify({
                "description": formData.taskDescription,
                "users": users
            });

            const requestOptions = {
                method: 'POST',
                credentials: 'include',
                headers: requestHeaders,
                body: raw,
                redirect: 'follow'
            };

            let success = false;

            const recommendResponse = await fetch("/task/recommend", requestOptions);
            if (recommendResponse.ok) {
                success = true;
            }
            const recommendData = await recommendResponse.json();
            if (success) {
                const modalContent = document.getElementById('ai-modal-content');
                modalContent.innerHTML = `
                <div class="modal-header">
                    <h5 class="modal-title">AI Odgovor</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label for="chosen-users">Odabrani Ćlan/ovi:</label>
                        <p class="text-muted">${recommendData.message.join(', ')}</p>
                    </div>
                    <div class="form-group">
                        <label for="reasoning">Razlog:</label>
                        <p class="text-muted">${recommendData.explanation}</p>
                    </div>
                </div>
                <div class = "modal-footer">
                </div>
                `;

                $('#ai-response').modal('show');
            } else {
                errorAlert(recommendData.message) //
            }

        } catch (error) {
            console.error(error);
        }
    }
    hideSpinner();
}

function checkStringLength(myString) {
    if (myString.length >= 20) {
      return true;
    } else {
      return false;
    }
  }
