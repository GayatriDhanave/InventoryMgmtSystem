// let stompClient = null;
//
// function connectWS(email) {
//     const socket = new SockJS("http://localhost:8080/ws");
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

$(".menu-toggle").click(function () {
    $(".sidebar").toggleClass("show");
});

let wsSocket = null;

function connectUserWebSocket() {
    const email = sessionStorage.getItem("email");
    const token = sessionStorage.getItem("token"); // token must be stored earlier

    if (!email || !token) {
        console.log("connectUserWebSocket: no email or token");
        return; // user not logged in
    }

    if (wsSocket !== null && wsSocket.readyState === WebSocket.OPEN) {
        console.log("WebSocket already open");
        return; // already connected
    }

    // Build URL (ensure you're using the right protocol for your page: ws for http, wss for https)
    const wsUrl = `ws://localhost:8080/session?token=${encodeURIComponent(token)}`;

    try {
        wsSocket = new WebSocket(wsUrl);
    } catch (err) {
        console.error("Failed to create WebSocket:", err);
        return;
    }

    wsSocket.onopen = function () {
        console.log("WS connected for:", email);
    };

    wsSocket.onmessage = function (event) {
        console.log("WS message:", event.data);
        if (event.data === "LOGOUT") {
            alert("You have been logged out because your account was used elsewhere.");
            sessionStorage.clear();
            localStorage.clear();
            window.location.href = "login.html";
        }
    };

    wsSocket.onerror = function (err) {
        console.error("WebSocket error:", err);
    };

    wsSocket.onclose = function (ev) {
        console.log("WS Closed:", ev.code, ev.reason);
    };
}

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





// const email = sessionStorage.getItem("email");
// if (email) {
//     connectUserWebSocket();
// }
$(document).ready(function () {
    $("#dashboardPage").show();
    loadDashboardStats();

    const links = $(".nav-link[data-page]");
    const pages = $(".page");
    links.on("click", function (e) {
        e.preventDefault();

        const target = $(this).data("page");

        pages.hide();
        $("#" + target).show();

        links.removeClass("active");
        $(this).addClass("active");
    });

    $("#logoutBtn").on("click", function () {
        if (confirm("Are you sure you want to logout?")) {
            window.location.href = "http://localhost:8080/inventoryManagementSystem_war/login.html";
        }
    });

});

function loadDashboardStats() {
    $.ajax({
        url: "http://localhost:8080/inventoryManagementSystem_war/products/v1/count",
        type: "GET",
        headers: {
            email: sessionStorage.getItem('email'),
            token: sessionStorage.getItem('token')
        },
        success: function (resp) {
            $("#totalProducts").text(resp.data);
        }
    });

    $.ajax({
        url: "http://localhost:8080/inventoryManagementSystem_war/supplier/v1/count",
        type: "GET",
        headers: {
            email: sessionStorage.getItem('email'),
            token: sessionStorage.getItem('token')
        },
        success: function (resp) {
            $("#totalSuppliers").text(resp.data);
        }
    });

    const profile = document.getElementById("profile");
    const menu = document.getElementById("logoutMenu");

    let hideTimeout;


    profile.addEventListener("mouseenter", () => {
        clearTimeout(hideTimeout);
        menu.style.display = "block";
    });

    profile.addEventListener("mouseleave", () => {
        hideTimeout = setTimeout(() => {
            menu.style.display = "none";
        }, 2000);
    });

    menu.addEventListener("mouseenter", () => {
        clearTimeout(hideTimeout);
    });

    menu.addEventListener("mouseleave", () => {
        hideTimeout = setTimeout(() => {
            menu.style.display = "none";
        }, 2000);
    });

}

window.addEventListener("storage", function (event) {

    if (event.key === "email" && event.newValue) {
        console.log("Detected login from another tab → Connecting WebSocket");
        connectWS(event.newValue);
    }

    // When logout happens in another tab
    if (event.key === "email" && event.newValue === null) {
        console.log("User logged out in another tab → redirecting...");
        sessionStorage.clear();
        window.location.href = "index.html";
    }
});

$(document).on('click', '.menu-toggle', function(){ $('.sidebar').toggleClass('show'); });

// Nav links switch pages (keeps your data-page usage intact)
$(document).on('click', '.sidebar .nav-link', function(e){
    e.preventDefault();
    $('.sidebar .nav-link').removeClass('active');
    $(this).addClass('active');
    const page = $(this).data('page');
    $('.page').removeClass('active');
    $('#' + page).addClass('active');

    // if dashboard activate animated counters
    if(page === 'dashboardPage') animateCountersOnShow();
});

// Profile menu
$('#profile').on('click', function(e){ $('#logoutMenu').toggle(); });

// Dark mode toggle (persist in localStorage)
function setDarkMode(enabled){
    if(enabled){ document.documentElement.classList.add('dark-mode'); localStorage.setItem('salvation_dark','1'); $('#darkModeToggle i').removeClass('fa-sun').addClass('fa-moon'); }
    else{ document.documentElement.classList.remove('dark-mode'); localStorage.removeItem('salvation_dark'); $('#darkModeToggle i').removeClass('fa-moon').addClass('fa-sun'); }
}
$('#darkModeToggle').on('click', function(){ setDarkMode(!document.documentElement.classList.contains('dark-mode')); });
if(localStorage.getItem('salvation_dark')) setDarkMode(true);

// Small helper: show toast
function showToast(message, type='success'){
    const container = $('#toastContainer');
    const toast = $('<div>').addClass('toastBox').text(message);
    if(type==='error') toast.addClass('toast-error');
    if(type==='warning') toast.addClass('toast-warning');
    if(type==='success') toast.addClass('toast-success');
    container.append(toast);
    setTimeout(()=> toast.addClass('show'),50);
    setTimeout(()=> toast.fadeOut(300,function(){ $(this).remove(); }),4200);
}

// ----------------- Animated counters -----------------
// function animateCounter(el, end){
//     const duration = 1200; // ms
//     const start = 0;
//     const range = end - start;
//     const startTime = performance.now();
//     function step(now){
//         const elapsed = Math.min((now - startTime)/duration,1);
//         const val = Math.floor(start + (range * easeOutCubic(elapsed)));
//         el.text(val);
//         if(elapsed < 1) requestAnimationFrame(step);
//     }
//     requestAnimationFrame(step);
// }
// function easeOutCubic(t){ return 1 - Math.pow(1 - t, 3); }

// function animateCountersOnShow(){
//     // Fetch current numbers from backend or existing DOM values — here we read DOM and animate from 0 to that value
//     const tp = parseInt($('#totalProducts').data('value') || $('#totalProducts').text(resp.data) || 0,10);
//     const ts = parseInt($('#totalSuppliers').data('value') || $('#totalSuppliers').text(resp.data) || 0,10);
//     // const pu = parseInt($('#pendingUploads').data('value') || $('#pendingUploads').text() || 0,10);
//     animateCounter($('#totalProducts'), tp);
//     animateCounter($('#totalSuppliers'), ts);
//     // animateCounter($('#pendingUploads'), pu);
// }

// Trigger counter when loaded if dashboard visible
// $(document).ready(()=>{ if($('#dashboardPage').hasClass('active')) animateCountersOnShow(); });

// ----------------- Keep IDs & behavior: small integration points -----------------
// Example: when your dashboard.js fetches counts it should set the text or data-value attribute of counters
// e.g. $('#totalProducts').text(123); or $('#totalProducts').data('value', 123);
// The animateCountersOnShow function will animate the shown numbers.

// ----------------- Immediate logout across tabs (simple localStorage broadcast) -----------------
window.addEventListener('storage', function(e){
    if(e.key === 'sessionUUID'){
        // if sessionUUID differs, force logout this tab
        const myToken = sessionStorage.getItem('token');
        const newToken = localStorage.getItem('token');
        if(myToken && newToken && newToken !== myToken){
            alert('You have been logged out because your account logged in in another tab.');
            sessionStorage.clear(); localStorage.removeItem('sessionUUID'); window.location.href = 'index.html';
        }
    }
    if(e.key === 'token' && e.newValue === null){
        // other tab logged out
        alert('You have been logged out.'); sessionStorage.clear(); window.location.href = 'index.html';
    }
});

// ----------------- Initialize DataTables lightly (serverSide logic should remain in your products.js) -----------------
$(function(){
    // basic init so UI looks right before your existing JS takes over
    if($.fn.DataTable) {
        $('#productsTable').DataTable({ responsive:true, lengthMenu:[10,25,50], pageLength:10, searching:true, autoWidth:false, serverSide:false, processing:false, destroy:true });
        $('#suppliersTable').DataTable({ responsive:true, lengthMenu:[10,25,50], pageLength:10, searching:true, autoWidth:false, serverSide:false, processing:false, destroy:true });
        $('#fileUploadTable').DataTable({ responsive:true, lengthMenu:[10,25,50], pageLength:10, searching:false, autoWidth:false, serverSide:false, processing:false, destroy:true });
    }

    // wire sidebar nav to preserve active state from your existing markup
    $('.sidebar .nav-link[data-page]').on('click', function(e){ e.preventDefault(); $('.sidebar .nav-link').removeClass('active'); $(this).addClass('active'); const page=$(this).data('page'); $('.page').removeClass('active'); $('#' + page).addClass('active'); });

});

function showSwalConfirmation(message, confirmButtonText = 'Proceed', onConfirm) {
    Swal.fire({
        title: 'Are you sure?',
        text: message,
        icon: 'warning',
        showCancelButton: true,
        focusCancel: true,
        confirmButtonText: confirmButtonText,
        cancelButtonText: 'Cancel',
        reverseButtons: true,
        customClass: {
            confirmButton: 'swal-confirm-btn',
            cancelButton: 'swal-cancel-btn'
        }
    }).then((result) => {
        if (result.isConfirmed) {
            try {
                onConfirm();
            } catch (err) {
                console.error('Error in onConfirm callback:', err);
            }
        } else {
            // cancelled — do nothing
        }
    });
}

/**
 * Show a confirmation popup before performing edit/delete actions
 * @param {string} message - Confirmation message to show
 * @param {function} callback - Function executed if user clicks Proceed
 */
function showConfirmation(message, callback) {
    $("#confirmActionMessage").text(message);

    // Remove any previous click events to avoid stacking
    $("#confirmActionProceedBtn").off("click");

    // Add new click handler
    $("#confirmActionProceedBtn").on("click", function () {
        $("#confirmActionModal").modal("hide");
        callback(); // Execute the pending action
    });

    $("#confirmActionModal").modal("show");
}








