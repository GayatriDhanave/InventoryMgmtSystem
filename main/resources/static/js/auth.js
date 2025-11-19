$(document).ajaxSend(function(event, jqxhr, settings) {
    const token = sessionStorage.getItem("token");
    if (token) {
        jqxhr.setRequestHeader("Authorization", "Bearer " + token);
    }
});
//
$(document).ajaxError(function(event, jqxhr, settings) {
    if (jqxhr.status === 401) {
        // alert("Session expired or unauthorized! Please login again.");
        showToast("Session expired or unauthorized! Please login again!", "error");
        sessionStorage.clear();
        window.location.href = "index.html";
    } else if (jqxhr.status === 500){
        showToast("Something went wrong! Please try again!", "error");
    }
});

function showToast(message, type = "success") {
    const container = document.getElementById("toastContainer");

    const toast = document.createElement("div");
    toast.classList.add("toastBox");

    if (type === "error") {
        toast.classList.add("toast-error");
    } else if(type=="warning"){
        toast.classList.add("toast-warning");
    }
    else {
        toast.classList.add("toast-success");
    }

    toast.innerText = message;

    container.appendChild(toast);
    setTimeout(() => {
        toast.classList.add("toast-show");
    }, 100);


    setTimeout(() => {
        toast.classList.remove("toast-show");
        setTimeout(() => toast.remove(), 400);
    }, 5000);
}
