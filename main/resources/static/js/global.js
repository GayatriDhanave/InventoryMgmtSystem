// function applySerialNumber(tableId, columnIndex = 0) {
//     const table = $(tableId).DataTable();
//
//     table.on('draw.dt', function () {
//         let pageInfo = table.page.info();
//         table.column(columnIndex, { search: 'applied', order: 'applied' })
//             .nodes()
//             .each(function (cell, i) {
//                 cell.innerHTML = pageInfo.start + i + 1;
//             });
//     });
// }

function showToast(message, type = "success") {
    const container = document.getElementById("toastContainer");

    const toast = document.createElement("div");
    toast.classList.add("toastBox");

    // Choose color
    if (type === "error") {
        toast.classList.add("toast-error");
    } else {
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
