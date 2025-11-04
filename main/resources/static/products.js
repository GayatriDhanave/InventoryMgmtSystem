document.addEventListener("DOMContentLoaded", function () {

    document.getElementById("dashboardPage").style.display = "block";

    const links = document.querySelectorAll(".nav-link[data-page]");
    const pages = document.querySelectorAll(".page");

    links.forEach(link => {
        link.addEventListener("click", (e) => {
            e.preventDefault();
            const target = link.getAttribute("data-page");
            pages.forEach(page => page.style.display = "none");

            document.getElementById(target).style.display = "block";

            links.forEach(l => l.classList.remove("active"));
            link.classList.add("active");
        });
    });

    document.getElementById("logoutBtn").addEventListener("click", () => {
        alert("Are you sure to logout");
        window.location.href = "/login.html";
    });
});

$('#addProductModal').on('show.bs.modal', function () {
    $.ajax({
        url: "http://localhost:8080/inventoryManagementSystem_war/products/getCategories",
        method: "GET",
        dataType: "json",
        success: function (response) {

            const data = response.data || [];
            const $dropdown = $('#productCategory');

            $dropdown.empty();
            $dropdown.append('<option value="">-- Select a category --</option>');

            if (data.length === 0) {
                console.warn("No categories found in data array");
            }

            data.forEach(category => {
                $dropdown.append(`<option value="${category.id}">${category.categoryName}</option>`);
            });
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("AJAX Error:", textStatus, errorThrown);
            $('#productCategory').empty().append('<option>Error loading data</option>');
        }
    });

    $.ajax({
        url: "http://localhost:8080/inventoryManagementSystem_war/supplier/getAllSupplier",
        method: "GET",
        dataType: "json",
        success: function (response) {

            const data = response.data || [];
            const $dropdown = $('#supplierDropdown');

            $dropdown.empty();
            $dropdown.append('<option value="">-- Select a category --</option>');

            if (data.length === 0) {
                console.warn("No suppliers found in data array");
            }

            data.forEach(supplierData => {
                console.log("Adding option:", supplierData);
                $dropdown.append(`<option value="${supplierData.supId}">${supplierData.name}</option>`);
            });
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("AJAX Error:", textStatus, errorThrown);
            $('#supplierDropdown').empty().append('<option>Error loading data</option>');
        }
    });
});


$.validator.addMethod("pattern", function (value, element, param) {
    if (this.optional(element)) {
        return true;
    }
    if (typeof param === "string") {
        param = new RegExp(param);
    }
    return param.test(value);
}, "Invalid format");


$('#addProductForm').validate({
    rules: {
        productName: {
            required: true,
            minlength: 3,
            pattern: /^[A-Za-z\s]*$/
        },
        price: {
            required: true,
            minlength: 2,
            pattern: /^[0-9]*$/
        },
        quantity: {
            required: true,
            minlength: 1,
            pattern: /^[0-9]*$/
        },
        category: {
            required: true
        },
        supplier: {
            required: true
        }
    },
    messages: {
        productName: {
            required: "Enter product name",
            minlength: "Minimum 3 chars",
            pattern: "Only letters allowed"
        },
        price: {
            required: "Enter price",
            minlength: "Please enter 2 digits",
            pattern: "Price number should be digits"
        },
        quantity: {
            required: "Enter quantity",
            minlength: "Please enter 1 digits",
            pattern: "Price number should be digits"
        },
        category: {
            required: "Select category"
        },
        supplier: {
            required: "Select supplier"
        }
    },
    errorPlacement: function (error, element) {
        error.insertAfter(element);
    },

    highlight: function (element) {
        $(element).addClass("invalid-field").css("border", "2px solid red");
    },

    unhighlight: function (element) {
        $(element).removeClass("invalid-field").css("border", "");
    },
    submitHandler: function (form) {
        let productData = {
            productName: $('#productName').val(),
            category: {id: parseInt($('#productCategory').val())},
            price: $('#price').val(),
            quantity: $('#quantity').val(),
            supplier: {supId: parseInt($('#supplierDropdown').val())}

        };
        $.ajax({
            type: "POST",
            url: "http://localhost:8080/inventoryManagementSystem_war/products/addProduct",
            contentType: "application/json",
            data: JSON.stringify(productData),
            success: function (response) {
                alert("Supplier added successfully!");
                $('#addProductModal').modal('hide');
                table.ajax.reload();
                form.reset();
            },
            error: function (xhr) {
                alert("Error adding supplier: " + xhr.responseText);
            }
        });
    }
});


$(document).ready(function () {
    productTableFunction();


});

function productAjax() {
    const deferred = $.Deferred();

    const filters = {
        productName: "",
        category: "",
        supplier: "",
        sortBy: 'id',
        order: -1,
        pageNum: 1,
        limit: 10
    };

    $.ajax({
        url: 'http://localhost:8080/inventoryManagementSystem_war/products/getAllProducts',
        type: 'GET',
        data: filters,
        headers: {
            email: sessionStorage.getItem('email'),
            sessionId: sessionStorage.getItem('sessionId')
        },
        dataType: 'json',
        success: function (resp) {
            // const result = {
            //     data: resp.data || [],
            //     recordsTotal: resp.recordsTotal || 0,
            //     recordsFiltered: resp.recordsFiltered || 0
            // };

            const inner = resp.data;

            const result = {
                data: inner.data || [], // ✅ main table array
                recordsTotal: inner.totalRecords || 0, // ✅ correct totals
                recordsFiltered: inner.filteredRecords || 0
            };
            deferred.resolve(result); // ✅ one object, not multiple args
        },
        error: function (xhr) {
            if (xhr.status === 401) {
                alert("Session expired. Please login again.");
                sessionStorage.clear();
                window.location.href = "login.html";
            } else {
                console.error("Error:", xhr.responseText);
                deferred.reject(xhr);
            }
        }
    });

    return deferred.promise();
}

function productTableFunction() {
    $('#productsTable').DataTable({
        serverSide: true,
        processing: true,
        ajax: function (data, callback, settings) { // ✅ correct param names
            productAjax()
                .done(function (response) {
                    callback(response); // ✅ correct callback
                })
                .fail(function (err) {
                    console.error("Ajax error:", err);
                    callback({ data: [] });
                });
        },
        columns: [
            {
        data: null,
        render: function(data, type, row) {
            return `<input type="radio" name="productSelect" value="${row.productId}">`;
        },
        orderable: false
    },
            { data: 'productId' },
            { data: 'productName' },
            { data: 'category' },
            { data: 'suppiler' },
            { data: 'price' },
            { data: 'quantity' }
        ]
    });
}

$('#editProductBtn').click(function () {
    const selectedId = $('input[name="productSelect"]:checked').val();
    if (!selectedId) {
        alert("Please select a product to edit!");
        return;
    }

    $.ajax({
        url: 'http://localhost:8080/inventoryManagementSystem_war/products/getProduct?productId=' + selectedId,
        method: 'GET',
        success: function (resp) {
            const product = resp.data;
            $('#productId').val(product.id);
            $('#editProductName').val(product.productName);
            // $('#editProductCategory').val(product.category.categoryName);
            const categoryDropdown = $('#editProductCategory');
            categoryDropdown.empty();
            categoryDropdown.append('<option value="">Select one option</option>');

            if (product.supplier) {
                categoryDropdown.append(
                    `<option value="${product.category.id}" selected>${product.category.categoryName}</option>`
                );
            }
            $('#productPrice').val(product.price);
            $('#editProductQauntity').val(product.quantity);
            // $('#editSupplierDropdown').val(product.supplier.name);
            const supplierDropdown = $('#editSupplierDropdown');
            supplierDropdown.empty();
            supplierDropdown.append('<option value="">Select one option</option>');

            if (product.supplier) {
                supplierDropdown.append(
                    `<option value="${product.supplier.supId}" selected>${product.supplier.name}</option>`
                );
            }
            $('#editProductModal').modal('show');
        },
        error: function () {
            alert('Failed to fetch product details.');
        }
    });
});

// $('#editProductForm').submit(function (e) {
//     e.preventDefault();
//     const formData = $(this).serialize();
//     $.ajax({
//         url: 'http://localhost:8080/inventoryManagementSystem_war/products/update',
//         method: 'POST',
//         data: formData,
//         success: function () {
//             $('#editProductModal').modal('hide');
//             alert('Product updated successfully!');
//             location.reload(); // Refresh table
//         },
//         error: function () {
//             alert('Error updating product.');
//         }
//     });
// });
//

$('#editProductForm').validate({
    rules: {
        editProductName: {
            required: true,
            minlength: 3,
            pattern: /^[A-Za-z\s]*$/
        },
        productPrice: {
            required: true,
            minlength: 2,
            pattern: /^[0-9]*$/
        },
        editProductQauntity: {
            required: true,
            minlength: 1,
            pattern: /^[0-9]*$/
        },
        editProductCategory: {
            required: true
        },
        editSupplierDropdown: {
            required: true
        }
    },
    messages: {
        editProductName: {
            required: "Enter product name",
            minlength: "Minimum 3 chars",
            pattern: "Only letters allowed"
        },
        productPrice: {
            required: "Enter price",
            minlength: "Please enter 2 digits",
            pattern: "Price number should be digits"
        },
        editProductQauntity: {
            required: "Enter quantity",
            minlength: "Please enter 1 digits",
            pattern: "Price number should be digits"
        },
        editProductCategory: {
            required: "Select category"
        },
        editSupplierDropdown: {
            required: "Select supplier"
        }
    },
    errorPlacement: function (error, element) {
        error.insertAfter(element);
    },

    highlight: function (element) {
        $(element).addClass("invalid-field").css("border", "2px solid red");
    },

    unhighlight: function (element) {
        $(element).removeClass("invalid-field").css("border", "");
    },
    submitHandler: function (form) {
        let productData = {
            id:$('#productId').val(),
            productName: $('#editProductName').val(),
            category: {id: parseInt($('#editProductCategory').val())},
            price: $('#productPrice').val(),
            quantity: $('#editProductQauntity').val(),
            supplier: {supId: parseInt($('#editSupplierDropdown').val())}

        };
        $.ajax({
            type: "POST",
            url: "http://localhost:8080/inventoryManagementSystem_war/products/updateProduct",
            contentType: "application/json",
            data: JSON.stringify(productData),
            success: function (response) {
                alert("Product updated successfully!");
                $('#editProductModal').modal('hide');
                table.ajax.reload();
                form.reset();
            },
            error: function (xhr) {
                alert("Error adding supplier: " + xhr.responseText);
            }
        });
    }
});

$('#deleteProductBtn').click(function (){

    const selectedId = $('input[name="productSelect"]:checked').val();
    if (!selectedId) {
        alert("Please select a product to delete!");
        return;
    }

    $.ajax({
        url: 'http://localhost:8080/inventoryManagementSystem_war/products/deleteProduct?productId=' + selectedId,
        method: 'DELETE',
        success: function (resp){
            alert("Product deleted successfully!");
        },
        error:function (resp){
            alert("Error deleting product! Please try again");
        }
    });
})

