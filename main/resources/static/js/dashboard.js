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
}


