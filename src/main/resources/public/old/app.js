const url = window.location.href;
let id = url.substring(url.lastIndexOf('/') + 1);

if (/[?#/]$/.test(id)) { // Check if the last character is ?, #, or /
  id = id.slice(0, -1); // Remove the last character
}

function getUsername() {
  fetch('/util/username', {
    method: 'GET',
    credentials: 'include'
  })
  .then(response => response.json())
  .then(data => {
    const usernameElement = document.getElementById('username');
    usernameElement.textContent = data.message;

    if(data.picture !== "null") {
      const avatar = document.getElementById('avatar');
      avatar.setAttribute("src", data.picture);
    }
  })
  .catch(error => console.error(error));
}

function getReceivedInvites() {
  fetch('/invite/received', {
    method: 'GET',
    credentials: 'include'
  })
  .then(response => response.json())
  .then(data => {
    // loop through the received invites and create new list items
    const receivedInvitesList = document.getElementById('received-invites-list');
    data.reverse().forEach(invite => {
      const newListItem = document.createElement('div');
      newListItem.classList.add('list-group-item');
      newListItem.innerHTML = `
        <div class="d-flex w-100 justify-content-between">
          <h6 class="mb-1"><span class="text-muted">${invite.projectName}</span></h6>
          <span class="text-muted small">${invite.inviterEmail}</span>
          <button onclick="acceptInvite('${invite.id}')" class="btn btn-primary btn-sm"><i class="fas fa-check"></i></button>
          </div>
      `;
      receivedInvitesList.appendChild(newListItem);
    });
  })
  .catch(error => {
    console.error('Error:', error);
  });
}

function getSentInvites() {
  fetch('/invite/sent', {
    method: 'GET',
    credentials: 'include'
  })
  .then(response => response.json())
  .then(data => {
    // loop through the received invites and create new list items
    const receivedInvitesList = document.getElementById('sent-invites-list');
    data.reverse().forEach(invite => {
      const newListItem = document.createElement('div');
      newListItem.classList.add('list-group-item');
      newListItem.innerHTML = `
        <div class="d-flex w-100 justify-content-between">
          <h6 class="mb-1"><span class="text-muted">${invite.projectName}</span></h6>
          <span class="text-muted small">${invite.invitedEmail}</span>
          <button class="btn btn-secondary btn-sm" disabled><i class="fas fa-spinner"></i></button>
        </div>
      `;
      receivedInvitesList.appendChild(newListItem);
    });
  })
  .catch(error => {
    console.error('Error:', error);
  });
}

function getOwnerProjects() {
  fetch('/project/owner', {
    method: 'GET',
    credentials: 'include'
  })
  .then(response => response.json())
  .then(data => {
    const ownerProjects = document.getElementById('owner-projects');
    data.reverse().forEach(project => {
      const newCard = document.createElement('div');
      newCard.classList.add('col-md-4');
      newCard.innerHTML = `
        <div class="card mb-4">
          <div class="card-body">
            <h5 class="card-title">${project.name}</h5>
            <a href="/project/view/${project.id}" class="btn btn-primary"><i class="fas fa-location-arrow"></i> Pogledaj</a>
          </div>
        </div>
      `;
      ownerProjects.appendChild(newCard);
    });
  })
  .catch(error => {
    console.error('Error:', error);
  });
}

function getMemberProjects() {
  fetch('/project/member', {
    method: 'GET',
    credentials: 'include'
  })
  .then(response => response.json())
  .then(data => {
    const ownerProjects = document.getElementById('member-projects');
    data.reverse().forEach(project => {
      const newCard = document.createElement('div');
      newCard.classList.add('col-md-4');
      newCard.innerHTML = `
        <div class="card mb-4">
          <div class="card-body">
            <h5 class="card-title">${project.name}</h5>
            <a href="/project/view/${project.id}" class="btn btn-primary"><i class="fas fa-location-arrow"></i> Pogledaj</a>
          </div>
        </div>
      `;
      ownerProjects.appendChild(newCard);
    });
  })
  .catch(error => {
    console.error('Error:', error);
  });
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
      const newUser = document.createElement('li');
      newUser.classList.add('list-group-item');
      newUser.classList.add('d-flex');
      newUser.classList.add('justify-content-between');
      newUser.classList.add('align-items-center');
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
  })
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
            <a href="/project/task/${group.id}" class="btn btn-primary"><i class="fas fa-location-arrow"></i> Pogledaj</a>
          </div>
        </div>
      </div>
      `;
      groupList.appendChild(newGroup);
    });
  })
  .catch(error => {
    console.error('Error:', error);
  })
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

function createProject() {
  const projectName = document.getElementById("project-name").value;

  const myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  const raw = JSON.stringify({
    "name": projectName
  });

  const requestOptions = {
    method: 'POST',
    credentials: 'include',
    headers: myHeaders,
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
        successAlert(data.message)
      } else {
        errorAlert(data.message) //
      }
    });
}

function getProjectMembersForTaskAssign() {
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
    })
  })
  .catch(error => {
    console.error('Error:', error);
  })
}

function createNewTask() {
  const myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

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
    headers: myHeaders,
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
  });
}

async function recommendUsers() {
  const myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  const formData = getCreateTaskFormData();

  try {
    const membersResponse = await fetch(`/group/members/${id}`, {
      method: 'GET',
      credentials: 'include'
    });
    const membersData = await membersResponse.json();

    const users = membersData.map(user => user.username);
    console.log(users); // Output: ['John Doe', 'Jane Smith', ...]

    const raw = JSON.stringify({
      "description": formData.taskDescription,
      "users": users
    });

    const requestOptions = {
      method: 'POST',
      credentials: 'include',
      headers: myHeaders,
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
      const modalContent = document.getElementById('modal-content');
      modalContent.innerHTML = `
        <div class="modal-body">
          <p><strong>Message:</strong> ${recommendData.message[0]}</p>
          <p><strong>Explanation:</strong> ${recommendData.explanation}</p>
        </div>
      `;
      $('#myModal').modal('show');
    } else {
      errorAlert(recommendData.message) //
    }

  } catch (error) {
    console.error(error);
  }
}

function createGroup() {
  const groupName = document.getElementById("group-name").value;
  const groupDescription = document.getElementById("group-description").value;

  const myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  const raw = JSON.stringify({
    "name": groupName,
    "description": groupDescription,
    "projectId": id
  });

  const requestOptions = {
    method: 'POST',
    credentials: 'include',
    headers: myHeaders,
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
        successAlert(data.message)
      } else {
        errorAlert(data.message) //
      }
    });

}

function sendInvite() {
  const email = document.getElementById("invite-email-address").value;

  const myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  const raw = JSON.stringify({
    "projectId": id,
    "invitedEmail": email
  });

  const requestOptions = {
    method: 'POST',
    credentials: 'include',
    headers: myHeaders,
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
        successAlert(data.message)
      } else {
        errorAlert(data.message) //
      }
    });
}

function acceptInvite(id) {
  const myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  const raw = JSON.stringify({
    "inviteId": id
  });

  const requestOptions = {
    method: 'POST',
    headers: myHeaders,
    body: raw,
    redirect: 'follow'
  };

  let success = false;

  fetch("/invite/accept", requestOptions)
    .then(response => {
      if(response.ok) {
        success = true;
      }
      return response.json();
    })
    .then(data => {
      if(success) {
        successAlert(data.message);
      } else {
        errorAlert(data.message);
      }
      // console.log(data.message);
    });
}

function finishTask(id) {
  const myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  const raw = JSON.stringify({
    "taskId": id
  });

  const requestOptions = {
    method: 'POST',
    credentials: 'include',
    headers: myHeaders,
    body: raw,
    redirect: 'follow'
  };

  let success = false;

  fetch("/task/finish", requestOptions)
  .then(response => {
    if(response.ok) {
      success = true;
    }
    return response.json();
  })
  .then(data => {
    if(success) {
      successAlert(data.message);
      console.log('test');
    } else {
      errorAlert(data.message);
    }
  });
}

function openTask(id) {
  const myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  const raw = JSON.stringify({
    "taskId": id
  });

  const requestOptions = {
    method: 'POST',
    headers: myHeaders,
    credentials: 'include',
    body: raw,
    redirect: 'follow'
  };

  let success = false;
  fetch("/task/open", requestOptions)
  .then(response => {
    if(response.ok) {
      success = true;
    }
    return response.json();
  })
  .then(data => {
    if(success) {
      successAlert(data.message);
    } else {
      errorAlert(data.message);
    }
  });
}

function deleteTask(id) {
  const myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  const raw = JSON.stringify({
    "taskId": id
  });

  const requestOptions = {
    method: 'POST',
    headers: myHeaders,
    body: raw,
    redirect: 'follow'
  };

  let success = false;
  fetch("/task/delete", requestOptions)
  .then(response => {
    if(response.ok) {
      success = true;
    }
    return response.json();
  })
  .then(data => {
    if(success) {
      successAlert(data.message);
    } else {
      errorAlert(data.message);
    }
  });
}

function updateProfileImg(event) {
  event.preventDefault();

  const form = document.getElementById('image-form');
  const formData = new FormData(form);

  let success = false;

  fetch("/pfp/upload", {
    method: 'POST',
    credentials: 'include',
    body: formData
  })
  .then(response => {
    if(response.ok) {
      success = true;
    }
    return response.json();
  })
  .then(data => {
    if(success) {
      successAlert(data.message);
    } else {
      errorAlert(data.message);
    }
  });
}

function errorAlert(text) {
  Swal.fire({
      icon: 'error',
      title: "Neuspijeh",
      text: text,
      confirmButtonColor: '#3085d6',
      confirmButtonText: 'Ok',
      allowOutsideClick: false,
      allowEscapeKey: false
  });
}

function successAlert(text) {
  Swal.fire({
      icon: 'success',
      title: "Uspijeh",
      text: text,
      confirmButtonColor: '#3085d6',
      confirmButtonText: 'Ok',
      allowOutsideClick: false,
      allowEscapeKey: false
  }).then((result) => {
    if(result.isConfirmed) {
      location.reload();
    }
  });
}

function redirect(url) {
  window.location.replace(url);
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
