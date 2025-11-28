$(document).ready(function () {
    productTableFunction();

});
function getAllCategories(){
    $.ajax({
        url: "http://localhost:8080/inventoryManagementSystem_war/products/getCategories",
        method: "GET",
        dataType: "json",
        headers: {
            email: localStorage.getItem('email'),
            token: localStorage.getItem("token")
        },
        success: function (response) {

            const data = response.data || [];
            const $dropdown = $('#addProductCategory');

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
            email: localStorage.getItem('email'),
            token: localStorage.getItem("token")
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

// function editProduct(selectedId){
//
//     $.ajax({
//         url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1/getProduct?productId=' + selectedId,
//         method: 'GET',
//         headers: {
//             email: localStorage.getItem('email'),
//             token: localStorage.getItem("token")
//         },
//         success: function (resp) {
//             const product = resp.data;
//             $('#productId').val(product.id);
//             $('#editProductName').val(product.productName);
//             // $('#editProductCategory').val(product.category);
//             const categoryDropdown = $('#editProductCategory');
//             categoryDropdown.empty();
//
//             if (product.category!==null) {
//                 categoryDropdown.append(
//                     `<option value="${product.categoryId}" selected>${product.category}</option>`
//                 );
//             }
//             $.ajax({
//                 url: "http://localhost:8080/inventoryManagementSystem_war/products/getCategories",
//                 method: "GET",
//                 dataType: "json",
//                 headers: {
//                     email: localStorage.getItem('email'),
//                     token: localStorage.getItem("token")
//                 },
//                 success: function (response) {
//
//                     // const data = response.data || [];
//                     // const $dropdown = $('#productCategory');
//                     //
//                     // $dropdown.empty();
//                     // $dropdown.append('<option value="">-- Select a category --</option>');
//                     //
//                     // if (data.length === 0) {
//                     //     console.warn("No categories found in data array");
//                     // }
//                     const categories = resp.data;
//
//                     // data.forEach(category => {
//                     //     $dropdown.append(`<option value="${category.id}">${category.categoryName}</option>`);
//                         categories.forEach(cat => {
//                                 if (cat.id !== product.categoryId) {
//                                     categoryDropdown.append(
//                                         `<option value="${cat.id}">${cat.categoryName}</option>`
//                                     );
//                                 }
//                         });
//                     // });
//                 },
//                 error: function (jqXHR, textStatus, errorThrown) {
//                     console.error("AJAX Error:", textStatus, errorThrown);
//                     $('#productCategory').empty().append('<option>Error loading data</option>');
//                 }
//             });
//
//
//             $('#productPrice').val(product.price);
//             $('#editProductQauntity').val(product.quantity);
//             const supplierDropdown = $('#editSupplierDropdown');
//             supplierDropdown.empty();
//
//             if (product.suppilerName!==null) {
//                 supplierDropdown.append(
//                     `<option value="${product.supId}" selected>${product.suppilerName}</option>`
//                 );
//             }
//             $.ajax({
//                 url: "http://localhost:8080/inventoryManagementSystem_war/supplier/getAllSuppliers",
//                 method: "GET",
//                 dataType: "json",
//                 headers: {
//                     email: localStorage.getItem('email'),
//                     token: localStorage.getItem("token")
//                 },
//                 success: function (response) {
//
//                     const data = response.data.data || [];
//                     const $dropdown = $('#supplierDropdown');
//
//                     // $dropdown.empty();
//                     // $dropdown.append('<option value="">-- Select a supplier --</option>');
//                     //
//                     // if (data.length === 0) {
//                     //     console.warn("No suppliers found in data array");
//                     // }
//
//                     data.forEach(supplierData => {
//                         // console.log("Adding option:", supplierData);
//                         $dropdown.append(`<option value="${supplierData.supId}">${supplierData.name}</option>`);
//                     });
//                 },
//                 error: function (jqXHR, textStatus, errorThrown) {
//                     console.error("AJAX Error:", textStatus, errorThrown);
//                     $('#supplierDropdown').empty().append('<option>Error loading data</option>');
//                 }
//             });
//             $('#editProductModal').modal('show');
//         },
//         error: function () {
//             // alert('Failed to fetch product details.');
//             showToast("Failed to fetch product details!", "error");
//         }
//     });
// }
function editProduct(selectedId) {

    $.ajax({
        url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1/getProduct?productId=' + selectedId,
        method: 'GET',
        headers: {
            email: localStorage.getItem('email'),
            token: localStorage.getItem("token")
        },
        success: function (resp) {

            const product = resp.data;

            $('#editproductId').val(product.id);
            $('#productName').val(product.productName);
            $('#productPrice').val(product.price);
            $('#productQuantity').val(product.quantity);


            const categoryDropdown = $('#productCategory');
            categoryDropdown.empty();

            if (product.category !== null) {
                categoryDropdown.append(
                    `<option value="${product.categoryId}" selected>${product.category}</option>`
                );
            }
            const supplierDropdown = $('#editsupplierDropdown');
            supplierDropdown.empty();
            if (product.suppilerName !== null) {
                supplierDropdown.append(`<option value="${product.supId}" selected>${product.suppilerName}</option>`);
            } else {
                supplierDropdown.append(`<option value="">-- Select supplier --</option>`);
            }

            // $.ajax({
            //     url: "http://localhost:8080/inventoryManagementSystem_war/supplier/getAllSuppliers",
            //     method: "GET",
            //     headers: {
            //         email: localStorage.getItem('email'),
            //         token: localStorage.getItem("token")
            //     },
            //     success: function (response) {
            //         const suppliers = response?.data?.data || [];
            //         suppliers.forEach(supplier => {
            //             if (supplier.supId !== product.supId) {
            //                 supplierDropdown.append(`<option value="${supplier.supId}">${supplier.name}</option>`);
            //             }
            //         });
            //     },
            //     error: function () {
            //         supplierDropdown.append('<option>Error loading suppliers</option>');
            //     }
            // });
            const addProductModal = new bootstrap.Modal(document.getElementById('editProductModal'));
            addProductModal.show();

            // $('#addProductModal').modal('show');
        },

        error: function () {
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


// $('#addProductForm').validate({
//     rules: {
//         productName: {
//             required: true,
//             minlength: 3,
//             pattern: /^[A-Za-z\s]*$/
//         },
//         price: {
//             required: true,
//             minlength: 2,
//             pattern: /^[0-9]*$/
//         },
//         quantity: {
//             required: true,
//             minlength: 1,
//             pattern: /^[0-9]*$/
//         },
//         category: {
//             required: true
//         },
//         supplier: {
//             required: true
//         }
//     },
//     messages: {
//         productName: {
//             required: "Enter product name",
//             minlength: "Minimum 3 chars",
//             pattern: "Only letters allowed"
//         },
//         price: {
//             required: "Enter price",
//             minlength: "Please enter 2 digits",
//             pattern: "Price number should be digits"
//         },
//         quantity: {
//             required: "Enter quantity",
//             minlength: "Please enter 1 digits",
//             pattern: "Price number should be digits"
//         },
//         category: {
//             required: "Select category"
//         },
//         supplier: {
//             required: "Select supplier"
//         }
//     },
//     errorPlacement: function (error, element) {
//         error.insertAfter(element);
//     },
//
//     highlight: function (element) {
//         // $(element).addClass("invalid-field").css({"border": "2px solid red", "text": "red"});
//         $(element).addClass("invalid-field")
//             .css("border", "2px solid red").css("color", "red");
//     },
//
//     unhighlight: function (element) {
//         $(element).removeClass("invalid-field").css("border", "");
//     },
//     submitHandler: function (form) {
//         let productData = {
//             productName: $('#productName').val(),
//             category: {id: parseInt($('#productCategory').val())},
//             price: $('#price').val(),
//             quantity: $('#quantity').val(),
//             supplier: {supId: parseInt($('#supplierDropdown').val())}
//
//         };
//         $.ajax({
//             type: "POST",
//             url: "http://localhost:8080/inventoryManagementSystem_war/products/v1",
//             contentType: "application/json",
//             data: JSON.stringify(productData),
//             headers: {
//                 email: localStorage.getItem('email'),
//                     token: localStorage.getItem("token")
//             },
//             success: function (response) {
//                 // alert("Product added successfully!");
//                 showToast("Products added successfully!", "success");
//                 $('#addProductModal').modal('hide');
//                 // productTableFunction()
//                 getProductsDataTable().ajax.reload(null, false)
//                 form.reset();
//             },
//             error: function (xhr) {
//                 // alert("Error adding supplier: " + xhr.responseText);
//                 showToast("Error adding supplier!", "error");
//             }
//         });
//     }
// });

$('#addProductForm').validate({
    rules: {
        addProductName: {
            required: true,
            minlength: 3,
            pattern: /^[A-Za-z\s]*$/
        },
        addProductPrice: {
            required: true,
            minlength: 1,
            pattern: /^[0-9]*$/
        },
        addProductQuantity: {
            required: true,
            minlength: 1,
            pattern: /^[0-9]*$/
        },
        addProductCategory: {
            required: true
        },
        supplierDropdown: {
            required: true
        }
    },
    messages: {
        addProductName: { required: "Enter product name" },
        addProductPrice: { required: "Enter price" },
        addProductQuantity: { required: "Enter quantity" },
        addProductCategory: { required: "Select category" },
        supplierDropdown: { required: "Select supplier" }
    },
    submitHandler: function (form) {

        let productData = {
            productName: $('#addProductName').val(),
            category: { id: parseInt($('#addProductCategory').val()) },
            price: $('#addProductPrice').val(),
            quantity: $('#addProductQuantity').val(),
            supplier: { supId: parseInt($('#supplierDropdown').val()) }
        };

        Swal.fire({
            title: "Add Product?",
            text: "Do you want to save this product?",
            icon: "question",
            showCancelButton: true,
            confirmButtonText: "Save",
            cancelButtonText: "Cancel"
        }).then(result => {
            if (!result.isConfirmed) return;

            $.ajax({
                type: "POST",
                url: "http://localhost:8080/inventoryManagementSystem_war/products/v1",
                contentType: "application/json",
                data: JSON.stringify(productData),
                headers: {
                    email: localStorage.getItem('email'),
                    token: localStorage.getItem("token")
                },
                success: function () {
                    showToast("Product added!", "success");
                    $('#addProductModal').modal('hide');
                    getProductsDataTable().ajax.reload(null, false);
                }
            });
        });
    }
});



function getProductsDataTable() {
    return $('#productsTable').DataTable();
}

function productTableFunction() {
    // Destroy if already initialized (prevents duplicates)
    if ($.fn.DataTable.isDataTable('#productsTable')) {
        $('#productsTable').DataTable().destroy();
    }

    $('#productsTable').DataTable({
        serverSide: true,
        processing: true,
        ajax: function (data, callback, settings) {
            // DataTables paging starts at 0; backend expects pageNum starting at 1
            const pageNum = Math.floor(data.start / data.length) + 1;
            const limit = data.length;
            const orderCol = data.order && data.order.length ? data.order[0].column : 2; // default col
            const orderDir = data.order && data.order.length ? (data.order[0].dir === 'asc' ? 1 : -1) : -1;

            // map column index to column name your backend expects (change if needed)
            const colMap = {
                2: 'productName',
                3: 'category',
                4: 'suppiler',
                5: 'price',
                6: 'quantity'
            };
            const sortBy = colMap[orderCol] || 'id';

            $.ajax({
                url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1',
                type: 'GET',
                data: {
                    productName: '',
                    category: '',
                    supplier: '',
                    sortBy: sortBy,
                    order: orderDir,
                    pageNum: pageNum,
                    limit: limit
                },
                headers: {
                    email: localStorage.getItem('email'),
                    token: localStorage.getItem('token')
                },
                success: function (resp) {
                    const inner = resp.data || {};
                    // expected format for DataTables:
                    callback({
                        data: inner.data || [],
                        recordsTotal: inner.totalRecords || 0,
                        recordsFiltered: inner.filteredRecords || inner.totalRecords || 0
                    });
                },
                error: function (xhr) {
                    if (xhr.status === 401) {
                        showToast("Session expired — please login again!", "error");
                        localStorage.clear();
                        window.location.href = "index.html";
                    } else {
                        console.error("Error fetching products:", xhr.responseText);
                        callback({ data: [], recordsTotal: 0, recordsFiltered: 0 });
                    }
                }
            });
        },
        columns: [
            {
                data: null,
                render: function (data, type, row) {
                    return `<input type="checkbox" name="productSelect" value="${row.productId}">`;
                },
                orderable: false
            },
            { data: null, orderable: false }, // serial number column
            { data: 'productName', orderable: false },
            { data: 'category', orderable: false },
            { data: 'suppiler', orderable: false },
            { data: 'price', orderable: false },
            { data: 'quantity', orderable: false },
            {
                data: null,
                orderable: false,
                render: (d, t, row) =>
                    `<i class="fas fa-edit edit-icon" style="color: #1d4bc5" onclick="editProduct('${row.productId}')"></i>
                     &nbsp;<i class="fa-solid fa-eye" style="color: #1d4bc5;" onclick="viewProduct('${row.productId}')"></i>
                     &nbsp;<i class="fas fa-trash-alt delete-icon" style="color: #1d4bc5" onclick="deleteProduct('${row.productId}')"></i>`
            }
        ],
        drawCallback: function () {
            applySerialNumber('#productsTable', 1);
        },
        lengthMenu: [10, 25, 50, 100]
    });
}

// function productAjax() {
//     const deferred = $.Deferred();
//
//     const filters = {
//         productName: "",
//         category: "",
//         supplier: "",
//         sortBy: 'id',
//         order: -1,
//         pageNum: 1,
//         limit: 10
//     };
//
//     $.ajax({
//         url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1',
//         type: 'GET',
//         data: filters,
//         headers: {
//             email: localStorage.getItem('email'),
//             token: localStorage.getItem("token")
//         },
//         dataType: 'json',
//         success: function (resp) {
//             const inner = resp.data;
//
//             const result = {
//                 data: inner.data || [],
//                 recordsTotal: inner.totalRecords || 0,
//                 recordsFiltered: inner.filteredRecords || 0
//             };
//             // $('productCount').append("Product Count<br> ${recordsTotal}");
//             deferred.resolve(result);
//         },
//         error: function (xhr) {
//             if (xhr.status === 401) {
//                 // alert("Session expired. Please login again.");
//                 showToast("Seesion expierd please login again!", "error");
//                 localStorage.clear();
//                 window.location.href = "index.html";
//             } else {
//                 console.error("Error:", xhr.responseText);
//                 deferred.reject(xhr);
//             }
//         }
//     });
//
//     return deferred.promise();
// }
//
// function productTableFunction() {
//     if ($.fn.DataTable.isDataTable('#productsTable')) {
//         $('#productsTable').DataTable().destroy();
//     }
//     $('#productsTable').DataTable({
//         serverSide: true,
//         processing: true,
//         ajax: function (data, callback, settings) {
//             productAjax()
//                 .done(function (response) {
//                     callback(response);
//                     $('productCount').append("Product Count<br> ${response.recordsTotal}");
//                 })
//                 .fail(function (err) {
//                     console.error("Ajax error:", err);
//                     callback({ data: [] });
//                 });
//         },
//         columns: [
//             {
//         data: null,
//         render: function(data, type, row) {
//             return `<input type="checkbox" name="productSelect" value="${row.productId}">`;
//         },
//         orderable: false
//     },
//             { data: null , orderable: false},
//             { data: 'productName', orderable: false },
//             { data: 'category' , orderable: false},
//             { data: 'suppiler', orderable: false },
//             { data: 'price', orderable: false },
//             { data: 'quantity' , orderable: false},
//             { data: null,
//                 render: (d, t, row) =>
//                     `<i class="fas fa-edit edit-icon" style="color: #1d4bc5" onclick="editProduct('${row.productId}')"></i> &nbsp &nbsp<i class="fa-solid fa-eye" style="color: #1d4bc5;" onclick="viewProduct('${row.productId}')"></i>  &nbsp &nbsp <i class="fas fa-trash-alt delete-icon" style="color: #1d4bc5" onclick="deleteProduct('${row.productId}')"></i>`, orderable: false}
//         ]
//     });
//     applySerialNumber('#productsTable', 1);
// }

$('#editProductBtn').click(function () {
    const selectedId = $('input[name="productSelect"]:checked').val();
    if (!selectedId) {
        // alert("Please select a product to edit!");
        showToast("Please select a product to edit!", "warning");
        return;
    }
    editProduct();


});

//editProductForm
$('#productForm').validate({
    rules: {
        productName: {
            required: true,
            minlength: 3,
            pattern: /^[A-Za-z\s]*$/
        },
        productPrice: {
            required: true,
            minlength: 2,
            pattern: /^[0-9]*$/
        },
        productQauntity: {
            required: true,
            minlength: 1,
            pattern: /^[0-9]*$/
        },
        productCategory: {
            required: true
        },
        editSupplierDropdown: {
            required: true
        }
    },
    messages: {
        productName: {
            required: "Enter product name",
            minlength: "Minimum 3 chars",
            pattern: "Only letters allowed"
        },
        productPrice: {
            required: "Enter price",
            minlength: "Please enter 2 digits",
            pattern: "Price number should be digits"
        },
        productQauntity: {
            required: "Enter quantity",
            minlength: "Please enter 1 digits",
            pattern: "Price number should be digits"
        },
        productCategory: {
            required: "Select category"
        },
        supplierDropdown: {
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
        // if (!confirm("Are you sure you want to edit selected product?")) {
        //     return;
        // }
        // showConfirmation("Do you really want to edit this Product?", function () {

        Swal.fire({
            title: "Edit Product?",
            text: "Do you want to edit this product?",
            icon: "question",
            showCancelButton: true,
            confirmButtonText: "Save",
            cancelButtonText: "Cancel"
        }).then(result => {
            if (!result.isConfirmed) return;});
            $.ajax({
                type: "POST",
                url: "http://localhost:8080/inventoryManagementSystem_war/products/v1",
                contentType: "application/json",
                data: JSON.stringify(productData),
                headers: {
                    email: localStorage.getItem('email'),
                    token: localStorage.getItem("token")
                },
                success: function (response) {
                    // alert("Product updated successfully!");
                    showToast("Products update successfully!", "success");
                    $('#editProductModal').modal('hide');
                    // productTableFunction()
                    getProductsDataTable().ajax.reload(null, false)
                    form.reset();
                },
                error: function (xhr) {
                    // alert("Error adding supplier: " + xhr.responseText);
                    showToast("Error adding product!", "error");
                }
            });

    }
});

//  function deleteProduct(selectedId){
//     // if (!confirm("Are you sure you want to delete selected products?")) {
//     //     return;
//     // }
//      showConfirmation("Do you really want to delete this Product?", function () {
//
//          $.ajax({
//              url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1/getProduct?productId=' + selectedId,
//              method: 'DELETE',
//              headers: {
//                  email: localStorage.getItem('email'),
//                  token: localStorage.getItem("token")
//              },
//              success: function (resp) {
//                  // alert("Product deleted successfully!");
//                  getProductsDataTable().ajax.reload(null, false)
//                  showToast("Products deleted successfully!", "success");
//
//              },
//              error: function (resp) {
//                  // alert("Error deleting product! Please try again");
//                  showToast("Error deleting product!", "error");
//              }
//          });
//      });
//
// }

function deleteProduct(id) {

    Swal.fire({
        title: "Delete product?",
        text: "This action cannot be undone.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Delete",
        cancelButtonText: "Cancel"
    }).then(result => {

        if (!result.isConfirmed) return;

        $.ajax({
            url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1/productId=' + id,
            method: 'DELETE',
            headers: {
                email: localStorage.getItem('email'),
                token: localStorage.getItem("token")
            },
            success: function () {
                showToast("Product deleted!", "success");
                getProductsDataTable().ajax.reload(null, false);
            },
            error: function () {
                showToast("Error deleting!", "error");
            }
        });

    });
}

 function viewProduct(selectedId){
        $.ajax({
            url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1/getProduct?productId=' + selectedId,
            method: 'GET',
            headers: {
                email: localStorage.getItem('email'),
                Authorization: "Bearer " + localStorage.getItem("token")
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

    // $('#bulkDeleteProductBtn').off('click').on('click', function () {
    //
    //     const selectedIds = $('input[name="productSelect"]:checked')
    //         .map(function () { return $(this).val(); })
    //         .get();
    //
    //     if (selectedIds.length === 0) {
    //         // alert("Please select at least one product to delete!");
    //         showToast("Select a product!", "warning");
    //
    //         return;
    //     }
    //
    //     // if (!confirm("Are you sure you want to delete selected products?")) {
    //     //     return;
    //     // }
    //     showConfirmation("Do you really want to delete this Product?", function () {
    //
    //
    //         $.ajax({
    //             url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1/bulkDeleteProduct?ids='
    //                 + selectedIds.join(','),
    //             method: 'DELETE',
    //             headers: {
    //                 email: localStorage.getItem('email'),
    //                 token: localStorage.getItem("token")
    //             },
    //             success: function (resp) {
    //                 // alert("Selected products deleted successfully!");
    //                 showToast("Products deleted successfully!", "success");
    //                 // productTableFunction();
    //                 getProductsDataTable().ajax.reload(null, false)
    //             },
    //             error: function () {
    //                 // alert("Error deleting products! Please try again");
    //                 showToast("Error in deleting product!", "error");
    //             }
    //         });
    //     });
    // });
$('#bulkDeleteProductBtn').on('click', function () {

    const ids = $('input[name="productSelect"]:checked').map(function () {
        return $(this).val();
    }).get();

    if (ids.length === 0) {
        showToast("Select products first!", "warning");
        return;
    }

    Swal.fire({
        title: "Delete selected?",
        text: "This cannot be undone.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Delete",
        cancelButtonText: "Cancel"
    }).then(result => {

        if (!result.isConfirmed) return;

        $.ajax({
            url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1/bulkDeleteProduct?ids=' + ids.join(","),
            method: 'DELETE',
            headers: {
                email: localStorage.getItem('email'),
                token: localStorage.getItem("token")
            },
            success: function () {
                showToast("Products deleted!", "success");
                getProductsDataTable().ajax.reload(null, false);
            }
        });

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
