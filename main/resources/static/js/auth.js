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

    // let wsSocket = null;

    // function connectUserWebSocket() {
    //     const email = sessionStorage.getItem("email");
    //
    //     if (!email) {
    //         return; // user not logged in
    //     }
    //
    //     if (wsSocket !== null && wsSocket.readyState === WebSocket.OPEN) {
    //         return; // already connected
    //     }
    //
    //     wsSocket = new WebSocket("ws://localhost:8080/session");
    //
    //     wsSocket.onopen = function () {
    //         console.log("WS connected for:", email);
    //     };
    //
    //     wsSocket.onmessage = function (event) {
    //         if (event.data === "LOGOUT") {
    //             alert("You have been logged out because your account was used elsewhere.");
    //
    //             sessionStorage.clear();
    //             localStorage.clear();
    //             window.location.href = "login.html";
    //         }
    //     };
    //
    //     wsSocket.onclose = function () {
    //         console.log("WS Closed");
    //     };
    // }

    // let wsSocket = null;
    //
    // function connectUserWebSocket() {
    //     const email = sessionStorage.getItem("email");
    //     const token = sessionStorage.getItem("token"); // token must be stored earlier
    //
    //     if (!email || !token) {
    //         console.log("connectUserWebSocket: no email or token");
    //         return; // user not logged in
    //     }
    //
    //     if (wsSocket !== null && wsSocket.readyState === WebSocket.OPEN) {
    //         console.log("WebSocket already open");
    //         return; // already connected
    //     }
    //
    //     // Build URL (ensure you're using the right protocol for your page: ws for http, wss for https)
    //     const wsUrl = `ws://localhost:8080/session?token=${encodeURIComponent(token)}`;
    //
    //     try {
    //         wsSocket = new WebSocket(wsUrl);
    //     } catch (err) {
    //         console.error("Failed to create WebSocket:", err);
    //         return;
    //     }
    //
    //     wsSocket.onopen = function () {
    //         console.log("WS connected for:", email);
    //     };
    //
    //     wsSocket.onmessage = function (event) {
    //         console.log("WS message:", event.data);
    //         if (event.data === "LOGOUT") {
    //             alert("You have been logged out because your account was used elsewhere.");
    //             sessionStorage.clear();
    //             localStorage.clear();
    //             window.location.href = "login.html";
    //         }
    //     };
    //
    //     wsSocket.onerror = function (err) {
    //         console.error("WebSocket error:", err);
    //     };
    //
    //     wsSocket.onclose = function (ev) {
    //         console.log("WS Closed:", ev.code, ev.reason);
    //     };
    // }

    // let stompClient = null;
    //
    // function connectWS(email) {
    //     const socket = new SockJS("http://localhost:8080/session?token=${encodeURIComponent(token)}");
    //     stompClient = Stomp.over(socket);
    //
    //     stompClient.connect({}, () => {
    //         stompClient.subscribe(`/topic/logout/${email}`, (msg) => {
    //
    //             if (msg.body === "FORCE_LOGOUT") {
    //                 sessionStorage.clear();
    //                 alert("You have been logged out because your account logged in elsewhere.");
    //                 window.location.href = "index.html";
    //             }
    //         });
    //     });
    // }




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
