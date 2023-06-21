function showSpinner() {
    document.querySelector('.spinner-container').classList.remove('d-none');
}

function hideSpinner() {
    document.querySelector('.spinner-container').classList.add('d-none');
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
        if (result.isConfirmed) {
            location.reload();
        }
    });
}

function toggleLogoutWarning() {
    Swal.fire({
        title: 'Da li ste sigurni?',
        icon: 'warning',
        showCancelButton: true,
        cancelButtonText: 'Odustani',
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Odjavi Me'
    }).then((result) => {
        if (result.isConfirmed) {
            logOut()
        }
    })
}

function logOut() {
    fetch('logout', {
            method: 'POST'
        })
        .then(() => {
            redirect("/login/auth/");
        })
        .catch((error) => console.error(error));
}

function getUsername() {
    fetch("/util/username", {
            method: "GET",
            credentials: "include"
        })
        .then(response => response.json())
        .then(data => {
            const usernameElement = document.getElementById("username");
            usernameElement.textContent = data.message;

            if (data.picture !== "null") {
                const avatar = document.getElementById("avatar");
                avatar.setAttribute("src", data.picture);
            }
        })
        .catch(error => console.error(error));
}

function updateProfileImg(event) {
    showSpinner();

    event.preventDefault();

    const form = document.getElementById("image-form");
    const formData = new FormData(form);

    let success = false;

    fetch("/pfp/upload", {
            method: "POST",
            credentials: "include",
            body: formData
        })
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
        .catch(error => console.error(error));
}

function redirect(url) {
    window.location.replace(url);
}