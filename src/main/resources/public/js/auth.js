function register(event) {
    showSpinner();

    event.preventDefault();

    const form = event.target;
    const formData = new FormData(form);

    const name = formData.get("ime");
    const surname = formData.get("prezime");
    const job = formData.get("profesija");
    const email = formData.get("email");
    const password = formData.get("password");

    const requestHeaders = new Headers();
    requestHeaders.append("Content-Type", "application/json");

    const raw = JSON.stringify({
        "firstName": name,
        "lastName": surname,
        "jobTitle": job,
        "email": email,
        "password": password
    });

    const requestOptions = {
        method: "POST",
        headers: requestHeaders,
        body: raw,
        redirect: "follow"
    };

    let success = true;

    fetch("/register", requestOptions)
        .then(response => {
            if (response.ok) {
                success = true;
            }
            return response.json();
        })
        .then(data => {
            if (success) {
                successAlertRedirect(data.message);
            } else {
                errorAlert(data.message);
            }
            hideSpinner();
        });
}

function successAlertRedirect(text) {
    Swal.fire({
        icon: "success",
        title: "Uspijeh",
        text: text,
        confirmButtonColor: "#3085d6",
        confirmButtonText: "Login",
        allowOutsideClick: false,
        allowEscapeKey: false
    }).then((result) => {
        if (result.isConfirmed) {
            redirect("/login/auth");
        }
    });
}

function redirect(url) {
    window.location.replace(url);
}