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
            $('#productName').val(product.productName);
            // $('#editProductCategory').val(product.category.categoryName);
            const categoryDropdown = $('#productCategory');
            categoryDropdown.empty();
            categoryDropdown.append('<option value="">Select one option</option>');

            if (product.supplierName!==null) {
                categoryDropdown.append(
                    `<option value="${product.categoryId}" selected>${product.category}</option>`
                );
            }
            $('#price').val(product.price);
            $('#quantity').val(product.quantity);
            // $('#editSupplierDropdown').val(product.supplier.name);
            const supplierDropdown = $('#supplierDropdown');
            supplierDropdown.empty();
            supplierDropdown.append('<option value="">Select one option</option>');

            if (product.suppilerName!==null) {
                supplierDropdown.append(
                    `<option value="${product.supId}" selected>${product.suppilerName}</option>`
                );
            }
            $('#addProductModal').modal('show');
        },
        error: function () {
            alert('Failed to fetch product details.');
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
                alert("Product added successfully!");
                $('#addProductModal').modal('hide');
                productTableFunction()
                form.reset();
            },
            error: function (xhr) {
                alert("Error adding supplier: " + xhr.responseText);
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
            { data: 'productId' },
            { data: 'productName' },
            { data: 'category' },
            { data: 'suppiler' },
            { data: 'price' },
            { data: 'quantity' },
            {"data": null,
                "defaultContent": '<i class="fas fa-edit edit-icon"></i> <i class="fas fa-trash-alt delete-icon"></i> <i class="fa-solid fa-circle-info"></i>',
                "render": function (data, type, row) {

                    let icons = '<button id="editProductBtn" onclick="editProduct(${"#row.productId"})"> <i class="fas fa-edit edit-icon" data-id="' + row.id + '"></i> </button>';
                    icons += '<button id="deleteProductBtn"> <i class="fas fa-trash-alt delete-icon" data-id="' + row.id + '"></i> </button>';
                    icons += '<button id="viewProductBtn"> <i class="fa-solid fa-circle-info" data-id="' + row.id + '"></i> </button>';
                    return icons;}
            }
        ]
    });
}

$('#editProductBtn').click(function () {
    const selectedId = $('input[name="productSelect"]:checked').val();
    if (!selectedId) {
        alert("Please select a product to edit!");
        return;
    }
    editProduct();


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
            url: "http://localhost:8080/inventoryManagementSystem_war/products/v1",
            contentType: "application/json",
            data: JSON.stringify(productData),
            headers: {
                email: sessionStorage.getItem('email'),
                    token: sessionStorage.getItem("token")
            },
            success: function (response) {
                alert("Product updated successfully!");
                $('#editProductModal').modal('hide');
                productTableFunction()
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
        url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1?productId=' + selectedId,
        method: 'DELETE',
        headers: {
            email: sessionStorage.getItem('email'),
            token: sessionStorage.getItem("token")
        },
        success: function (resp){
            alert("Product deleted successfully!");
        },
        error:function (resp){
            alert("Error deleting product! Please try again");
        }
    });
});

$('#viewProductBtn').click(function (){
    const selectedId = $('input[name="productSelect"]:checked').val();
    if (!selectedId) {
        alert("Please select a product to view!");
        return;
    }
    $.ajax({
        url: 'http://localhost:8080/inventoryManagementSystem_war/products/getProduct?productId=' + selectedId,
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
            alert("Error fetching product! Please try again");
            console.error("Fetch failed:", resp);
        }
    });
});

