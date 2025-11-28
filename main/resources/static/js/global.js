// // function applySerialNumber(tableId, columnIndex = 0) {
// //     const table = $(tableId).DataTable();
// //
// //     table.on('draw.dt', function () {
// //         let pageInfo = table.page.info();
// //         table.column(columnIndex, { search: 'applied', order: 'applied' })
// //             .nodes()
// //             .each(function (cell, i) {
// //                 cell.innerHTML = pageInfo.start + i + 1;
// //             });
// //     });
// // }
// let stompClient = null;
//
// function connectWS(email) {
//     const socket = new SockJS("http://localhost:8080/session");
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
//
// function showToast(message, type = "success") {
//     const container = document.getElementById("toastContainer");
//
//     const toast = document.createElement("div");
//     toast.classList.add("toastBox");
//
//     // Choose color
//     if (type === "error") {
//         toast.classList.add("toast-error");
//     } else {
//         toast.classList.add("toast-success");
//     }
//
//     toast.innerText = message;
//
//     container.appendChild(toast);
//     setTimeout(() => {
//         toast.classList.add("toast-show");
//     }, 100);
//
//
//     setTimeout(() => {
//         toast.classList.remove("toast-show");
//         setTimeout(() => toast.remove(), 400);
//     }, 5000);
// }
//
//
// let socket = new WebSocket("ws://localhost:8080/session");
//
// socket.onmessage = function(event) {
//     const message = event.data;
//
//     if (message === "LOGOUT") {
//         alert("You have been logged out because your account was used in another tab.");
//
//         sessionStorage.clear();
//         window.location.href = "login.html";
//     }
// };