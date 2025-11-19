$(document).ready(function () {
    productTableFunction();

});
function getAllCategories(){
    $.ajax({
        url: "http://localhost:8080/inventoryManagementSystem_war/products/getCategories",
        method: "GET",
        dataType: "json",
        headers: {
            email: sessionStorage.getItem('email'),
            token: sessionStorage.getItem("token")
        },
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
}

function getAllSuppliers(){
    $.ajax({
        url: "http://localhost:8080/inventoryManagementSystem_war/supplier/getAllSuppliers",
        method: "GET",
        dataType: "json",
        headers: {
            email: sessionStorage.getItem('email'),
            token: sessionStorage.getItem("token")
        },
        success: function (response) {

            const data = response.data.data || [];
            const $dropdown = $('#supplierDropdown');

            $dropdown.empty();
            $dropdown.append('<option value="">-- Select a supplier --</option>');

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
}

function editProduct(selectedId){

    $.ajax({
        url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1/getProduct?productId=' + selectedId,
        method: 'GET',
        headers: {
            email: sessionStorage.getItem('email'),
            token: sessionStorage.getItem("token")
        },
        success: function (resp) {
            const product = resp.data;
            $('#productId').val(product.id);
            $('#editProductName').val(product.productName);
            // $('#editProductCategory').val(product.category);
            const categoryDropdown = $('#editProductCategory');
            categoryDropdown.empty();

            if (product.category!==null) {
                categoryDropdown.append(
                    `<option value="${product.categoryId}" selected>${product.category}</option>`
                );
            }
            $('#productPrice').val(product.price);
            $('#editProductQauntity').val(product.quantity);
            const supplierDropdown = $('#editSupplierDropdown');
            supplierDropdown.empty();

            if (product.suppilerName!==null) {
                supplierDropdown.append(
                    `<option value="${product.supId}" selected>${product.suppilerName}</option>`
                );
            }
            $('#editProductModal').modal('show');
        },
        error: function () {
            // alert('Failed to fetch product details.');
            showToast("Failed to fetch product details!", "error");
        }
    });
}


$('#addProductModal').on('show.bs.modal', function () {
    getAllCategories();
    getAllSuppliers();
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
            url: "http://localhost:8080/inventoryManagementSystem_war/products/v1",
            contentType: "application/json",
            data: JSON.stringify(productData),
            headers: {
                email: sessionStorage.getItem('email'),
                    token: sessionStorage.getItem("token")
            },
            success: function (response) {
                // alert("Product added successfully!");
                showToast("Products added successfully!", "success");
                $('#addProductModal').modal('hide');
                productTableFunction()
                form.reset();
            },
            error: function (xhr) {
                // alert("Error adding supplier: " + xhr.responseText);
                showToast("Error adding supplier!", "error");
            }
        });
    }
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
        url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1',
        type: 'GET',
        data: filters,
        headers: {
            email: sessionStorage.getItem('email'),
            token: sessionStorage.getItem("token")
        },
        dataType: 'json',
        success: function (resp) {
            const inner = resp.data;

            const result = {
                data: inner.data || [], 
                recordsTotal: inner.totalRecords || 0, 
                recordsFiltered: inner.filteredRecords || 0
            };
            // $('productCount').append("Product Count<br> ${recordsTotal}");
            deferred.resolve(result);
        },
        error: function (xhr) {
            if (xhr.status === 401) {
                // alert("Session expired. Please login again.");
                showToast("Seesion expierd please login again!", "error");
                sessionStorage.clear();
                window.location.href = "index.html";
            } else {
                console.error("Error:", xhr.responseText);
                deferred.reject(xhr);
            }
        }
    });

    return deferred.promise();
}

function productTableFunction() {
    if ($.fn.DataTable.isDataTable('#productsTable')) {
        $('#productsTable').DataTable().destroy();
    }
    $('#productsTable').DataTable({
        serverSide: true,
        processing: true,
        ajax: function (data, callback, settings) {
            productAjax()
                .done(function (response) {
                    callback(response);
                    $('productCount').append("Product Count<br> ${response.recordsTotal}");
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
            return `<input type="checkbox" name="productSelect" value="${row.productId}">`;
        },
        orderable: false
    },
            { data: null , orderable: false},
            { data: 'productName', orderable: false },
            { data: 'category' , orderable: false},
            { data: 'suppiler', orderable: false },
            { data: 'price', orderable: false },
            { data: 'quantity' , orderable: false},
            { data: null,
                render: (d, t, row) =>
                    `<i class="fas fa-edit edit-icon" onclick="editProduct('${row.productId}')"></i> <i class="fa-solid fa-circle-info" onclick="viewProduct('${row.productId}')"></i>  <i class="fas fa-trash-alt delete-icon" onclick="deleteProduct('${row.productId}')"></i>`, orderable: false}
        ]
    });
    applySerialNumber('#productsTable', 1);
}

$('#editProductBtn').click(function () {
    const selectedId = $('input[name="productSelect"]:checked').val();
    if (!selectedId) {
        // alert("Please select a product to edit!");
        showToast("Please select a product to edit!", "warning");
        return;
    }
    editProduct();


});


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
        if (!confirm("Are you sure you want to edit selected product?")) {
            return;
        }
        $.ajax({
            type: "POST",
            url: "http://localhost:8080/inventoryManagementSystem_war/products/v1",
            contentType: "application/json",
            data: JSON.stringify(productData),
            headers: {
                email: sessionStorage.getItem('email'),
                    token: sessionStorage.getItem("token")
            },
            success: function (response) {
                // alert("Product updated successfully!");
                showToast("Products update successfully!", "success");
                $('#editProductModal').modal('hide');
                productTableFunction()
                form.reset();
            },
            error: function (xhr) {
                // alert("Error adding supplier: " + xhr.responseText);
                showToast("Error adding product!", "error");
            }
        });
    }
});

 function deleteProduct(selectedId){
    if (!confirm("Are you sure you want to delete selected products?")) {
        return;
    }

        $.ajax({
            url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1/getProduct?productId=' + selectedId,
            method: 'DELETE',
            headers: {
                email: sessionStorage.getItem('email'),
                token: sessionStorage.getItem("token")
            },
            success: function (resp){
                // alert("Product deleted successfully!");
                showToast("Products deleted successfully!", "success");

            },
            error:function (resp){
                // alert("Error deleting product! Please try again");
                showToast("Error deleting product!", "error");
            }
        });

}
 function viewProduct(selectedId){
        $.ajax({
            url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1/getProduct?productId=' + selectedId,
            method: 'GET',
            headers: {
                email: sessionStorage.getItem('email'),
                Authorization: "Bearer " + sessionStorage.getItem("token")
            },
            success: function (resp) {
                const product = resp.data;

                $('#pName').text(product.productName || '-');
                $('#pCategory').text(product.category || '-');
                $('#pPrice').text(product.price || '-');
                $('#pQuantity').text(product.quantity || '-');
                $('#pSupplierName').text(product.suppilerName || '-');
                $('#pSupplierContact').text(product.contact || '-');
                $('#pSupplierEmail').text(product.email || '-');
                $('#pAddedDate').text(product.addedDate ? new Date(product.addedDate).toLocaleString() : '-');

                $('#pErrorRecords').text(product.errorRecords?.length > 0 ? product.errorRecords.length : 'None');


                $('#productDetailsModal').modal('show');
            },
            error: function (resp) {
                showToast("Error fetching products!", "error");
                console.error("Fetch failed:", resp);
            }
        });

}

    $('#bulkDeleteProductBtn').off('click').on('click', function () {

        const selectedIds = $('input[name="productSelect"]:checked')
            .map(function () { return $(this).val(); })
            .get();

        if (selectedIds.length === 0) {
            // alert("Please select at least one product to delete!");
            showToast("Select a product!", "warning");

            return;
        }

        if (!confirm("Are you sure you want to delete selected products?")) {
            return;
        }

        $.ajax({
            url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1/bulkDeleteProduct?ids='
                + selectedIds.join(','),
            method: 'DELETE',
            headers: {
                email: sessionStorage.getItem('email'),
                token: sessionStorage.getItem("token")
            },
            success: function (resp) {
                // alert("Selected products deleted successfully!");
                showToast("Products deleted successfully!", "success");
                productTableFunction();  // reload your table if needed
            },
            error: function () {
                // alert("Error deleting products! Please try again");
                showToast("Error in deleting product!", "error");
            }
        });
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

function applySerialNumber(tableId, columnIndex = 0) {
    const table = $(tableId).DataTable();

    table.on('draw.dt', function () {
        let pageInfo = table.page.info();
        table.column(columnIndex, { search: 'applied', order: 'applied' })
            .nodes()
            .each(function (cell, i) {
                cell.innerHTML = pageInfo.start + i + 1;
            });
    });
}
