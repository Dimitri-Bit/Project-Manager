function getReceivedInvites() {
    fetch("/invite/received", {
            method: "GET",
            credentials: "include"
        })
        .then(response => response.json())
        .then(data => {
            const receivedInvitesElement = document.getElementById("received-invites-list");

            data.reverse().forEach(invite => {
                const newListElement = document.createElement("div");
                newListElement.classList.add("list-group-item");

                newListElement.innerHTML = `
            <div class="d-flex w-100 justify-content-between">
                <h6 class="mb-1"><span class="text-muted">${invite.projectName}</span></h6>
                <span class="text-muted small">${invite.inviterEmail}</span>
                <button onclick="acceptInvite('${invite.id}')" class="btn btn-primary btn-sm"><i class="fas fa-check"></i></button>
            </div>
            `;
                receivedInvitesElement.appendChild(newListElement);
            });
        })
        .catch(error => console.error(error));
}

function getSentInvites() {
    fetch("/invite/sent", {
            method: "GET",
            credentials: "include"
        })
        .then(response => response.json())
        .then(data => {
            const sentInvitesElement = document.getElementById("sent-invites-list");

            data.reverse().forEach(invite => {
                const newListElement = document.createElement("div");
                newListElement.classList.add("list-group-item");

                newListElement.innerHTML = `
            <div class="d-flex w-100 justify-content-between">
                <h6 class="mb-1"><span class="text-muted">${invite.projectName}</span></h6>
                <span class="text-muted small">${invite.invitedEmail}</span>
                <button class="btn btn-secondary btn-sm" disabled><i class="fas fa-spinner"></i></button>
            </div>
            `;
                sentInvitesElement.appendChild(newListElement);
            });
            hideSpinner();
        })
        .catch(error => console.error(error));
}

function acceptInvite(id) {
    showSpinner();
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