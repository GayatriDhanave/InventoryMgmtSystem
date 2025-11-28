$('#downloadBtn').click(function () {

    fetch('http://localhost:8080/inventoryManagementSystem_war/bulkUpload/downloadTemplate')
        .then(response => response.blob())
        .then(blob => {
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'Product.xlsx';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);
        })
        .catch(error => console.error('Error downloading Excel:', error));
});



$(document).ready( function (){
    fileUploadTableFunction();
    checkActiveUpload();
    // fileUploadTableFunction();

});


$('#valid-tab').on('shown.bs.tab', function () {
    validTableFunction();
});

$('#invalid-tab').on('shown.bs.tab', function () {
    invalidTableFunction();
});

function downloadErrorFile(selectedId){
    // $('#errorFileBtn').click(function () {
        $.ajax({
            url: 'http://localhost:8080/inventoryManagementSystem_war/bulkUpload/downloadErrorFile',
            method: 'GET',
            xhrFields: { responseType: 'blob' },
            headers: {
                email: localStorage.getItem('email'),
                token: localStorage.getItem("token")
            },
            success: function (data, status, xhr) {
                const blob = new Blob([data], { type: xhr.getResponseHeader('Content-Type') });
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'ErrorFile.xlsx';
                a.click();
                window.URL.revokeObjectURL(url);
            },
            error: function (xhr, status, error) {
                console.error("Download failed:", status, error, xhr.responseText);
                showToast("Download Failed!", "error");
            }
        });
    // });
}


$('#bulkUploadBtn').click(async function (e) {
    e.preventDefault();

    const inputFile = $('#bulkFile')[0];
    // TODO file extension check and after uploading wrong file it should return invalid file msg currently redicrts to login


    if (inputFile.files.length === 0) {
        showToast("Please select a file!", "error");
        return;
    }
    const active = await checkActiveUpload();
    if (active.status === 'BLOCKED') {
        if (inputFile.files[0].name !== 'ErrorFile.xlsx'){
            showToast("Previous upload not completed. Please wait before uploading another!", "warning");
            return;
        }

    }

    const formData = new FormData();
    formData.append('file', inputFile.files[0]);

    $.ajax({
        url: 'http://localhost:8080/inventoryManagementSystem_war/bulkUpload/uploadFile',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        headers: {
            email: localStorage.getItem('email'),
            token: localStorage.getItem("token")
        },
        success: function (response, status, xhr) {
            showToast("File uploaded successfully!", "success");
            if(response.data.invalidRecords>0){
                showToast("Your file contains error. Please click on the download error file button!", "error");
                // $('#msg').html('<span class="text-danger"><h4>File uploaded successfully. Your file contains error. Please click on the download error file button.</h4></span>');
                $('#errorFileBtn').show();
            }
                $('#uploadResult').html('<span class="text-danger"><h4>Error file downloaded. Please correct and re-upload.</h4></span>');

        },
        error: function (xhr, status, error) {
            console.error("Upload failed:", xhr);
            $('#uploadResult').html('<span class="text-danger">Upload failed. Please try again.</span>');
        }
    });
});

function fileAjax() {
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
        url: 'http://localhost:8080/inventoryManagementSystem_war/bulkUpload/getFileUploadHistory',
        type: 'GET',
        data: filters,
        headers: {
            email: localStorage.getItem('email'),
            token: localStorage.getItem("token")
        },
        dataType: 'json',
        success: function (resp) {
            // showToast("File uploaded successfully!", "success");
            const inner = resp.data;

            const result = {
                data: inner || [],
                recordsTotal: inner.totalRecords || 0,
                recordsFiltered: inner.filteredRecords || 0
            };
            deferred.resolve(result);
        },
        error: function (xhr) {
            if (xhr.status === 401) {
                // alert("Session expired. Please login again.");
                showToast("Please login again!", "error");
                localStorage.clear();
                window.location.href = "index.html";
            } else {
                showToast("Error getting file upload history!", "error");
                console.error("Error:", xhr.responseText);
                deferred.reject(xhr);
            }
        }
    });

    return deferred.promise();
}

function fileUploadTableFunction() {
    $('#fileUploadTable').DataTable({
        serverSide: true,
        processing: true,
        ajax: function (data, callback, settings) {
            fileAjax()
                .done(function (response) {
                    callback(response);
                })
                .fail(function (err) {
                    console.error("Ajax error:", err);
                    callback({ data: [] });
                });
        },
        columns: [

            {data: null, orderable: false},
            {data: 'fileName', orderable: false},
            {data: 'uploadedDate', orderable: false},
            {data: 'fileStatus', orderable: false},
            // {
            //     data: null, /*displayDwldBtn('${row.productId}')*/
            //     render: function(d, t, row) {
            //         if(row.fileStatus==="ERROR_IN_RECORDS"){
            //             `<i class="fa-solid fa-download" style="color: #6b8bc2;" onclick="downloadErrorFile('${row.productId}')"></i> `
            //         } else {
            //             `<i class="fa-solid fa-download" style="color: #6b8bc2;"></i> `
            //         }
            //         }
            // }
            {
                data: null,
                orderable: false,
                render: function (d, t, row) {
                    if (row.fileStatus === "ERROR_IN_RECORDS") {
                        return `<i class="fa-solid fa-download" style="color:#1d4bc5;" 
                                    onclick="downloadErrorFile('${row.productId}')"></i>`;
                    }
                    return `<i class="fa-solid fa-download" style="color:#6b8bc2;"></i>`;
                }
            }
        ]
    });
    applySerialNumber('#fileUploadTable', 0);
}

function validAjax() {
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
        url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1/getProductWithoutErrors',
        type: 'GET',
        data: filters,
        headers: {
            email: localStorage.getItem('email'),
            token: localStorage.getItem("token")
        },
        dataType: 'json',
        success: function (resp) {
            const inner = resp.data.data;

            const result = {
                data: inner || [],
                recordsTotal: inner.totalRecords || 0,
                recordsFiltered: inner.filteredRecords || 0
            };
            deferred.resolve(result);
        },
        error: function (xhr) {
            if (xhr.status === 401) {
                // alert("Session expired. Please login again.");
                localStorage.clear();
                window.location.href = "index.html";
            } else {
                showToast("Error fetching products!", "error");
                console.error("Error:", xhr.responseText);
                deferred.reject(xhr);
            }
        }
    });

    return deferred.promise();
}

function validTableFunction() {
    $('#validRecordsTable').DataTable({
        serverSide: false,
        processing: true,
        destroy: true,
        ajax: function (data, callback, settings) {
            validAjax()
                .done(function (response) {
                    callback(response);
                    // $('productCount').append("Product Count<br> ${response.recordsTotal}");
                })
                .fail(function (err) {
                    console.error("Ajax error:", err);
                    callback({ data: [] });
                });
        },
        columns: [

            { data: null,  orderable: false },
            { data: 'productName' ,  orderable: false},
            { data: 'category',  orderable: false },
            { data: 'suppiler',  orderable: false },
            { data: 'price',  orderable: false },
            { data: 'quantity',  orderable: false }
        ]
    });

    applySerialNumber('#validRecordsTable', 0);
}

function invalidAjax() {
    return $.ajax({
        url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1/getProductWithErrors',
        type: 'GET',
        headers: {
            email: localStorage.getItem('email'),
            token: localStorage.getItem('token')
        },
        dataType: 'json'
    }).then(resp => {

        const rows = resp?.data?.data ||[] ;

        return { data: rows };
    });
}
function invalidTableFunction() {

    $('#invalidRecordsTable').DataTable({
        serverSide: false,
        processing: true,
        destroy: true,

        ajax: function (data, callback) {
            invalidAjax()
                .then(response => callback(response))
                .catch(err => {
                    console.error("Ajax error:", err);
                    callback({ data: [] });
                });
        },

        columns: [

            {
                data: null,
                orderable: false,
                render: (data, type, row, meta) => meta.row + 1
            },

            {
                data: "productName",
                render: (data, type, row) =>
                    highlightIfError("productName", data, row.errorRecordsList)
            },

            {
                data: "category",
                render: (data, type, row) =>
                    highlightIfError("category", data, row.errorRecordsList)
            },

            {
                data: "suppiler",
                render: (data, type, row) =>
                    highlightIfError("suppiler", data, row.errorRecordsList)
            },

            {
                data: "price",
                render: (data, type, row) =>
                    highlightIfError("price", data, row.errorRecordsList)
            },

            {
                data: "quantity",
                render: (data, type, row) =>
                    highlightIfError("quantity", data, row.errorRecordsList)
            },

            { data: null,
                render: (d, t, row) =>
                    `<i class="fas fa-edit edit-icon" style="color: #1d4bc5" onclick="editProduct('${row.productId}')"></i> &nbsp <i class="fa-solid fa-eye" style="color: #1d4bc5" onclick="viewProduct('${row.productId}')"></i> &nbsp <i class="fas fa-trash-alt delete-icon" style="color: #1d4bc5" onclick="deleteProduct('${row.productId}')"></i>`}

        ]
    });
}
function highlightIfError(fieldName, value, errorList) {

    if (!errorList || errorList.length === 0) {
        return value ?? "";
    }

    const hasError = errorList.some(err => err.errorField === fieldName);

    return hasError
        ? `<span style="color:red; font-weight:bold;">${value ?? ""}</span>`
        : value ?? "";
}

function checkActiveUpload() {
    return $.ajax({
        url: 'http://localhost:8080/inventoryManagementSystem_war/bulkUpload/checkActiveUpload',
        method: 'GET',
        headers: {
            email: localStorage.getItem('email'),
            token: localStorage.getItem("token")
        }
    });
}

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
        $('#generalPopup').html("Are you sure you want to edit the product?")
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
                email: localStorage.getItem('email'),
                token: localStorage.getItem("token")
            },
            success: function (response) {
                // alert("Product updated successfully!");
                showToast("Products updated successfully!", "success");
                $('#editProductModal').modal('hide');
                invalidTableFunction();
                form.reset();
            },
            error: function (xhr) {
                // alert("Error adding supplier: " + xhr.responseText);
                showToast("Error adding product!", "error");
            }
        });
    }
});

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
            // alert("Error fetching product! Please try again");
            showToast("Error fetching products!", "error");
            console.error("Fetch failed:", resp);
        }
    });

}
function deleteProduct(selectedId){

    if (!confirm("Are you sure you want to delete selected products?")) {
        return;
    }

    $.ajax({
        url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1/getProduct?productId=' + selectedId,
        method: 'DELETE',
        headers: {
            email: localStorage.getItem('email'),
            token: localStorage.getItem("token")
        },
        success: function (resp){
            showToast("Products deleted successfully!", "success");

        },
        error:function (resp){
            showToast("Error deleting product!", "error");
        }
    });

}

function editProduct(selectedId){
    if (!confirm("Are you sure you want to edit selected product?")) {
        return;
    }
    $.ajax({
        url: 'http://localhost:8080/inventoryManagementSystem_war/products/v1/getProduct?productId=' + selectedId,
        method: 'GET',
        headers: {
            email: localStorage.getItem('email'),
            token: localStorage.getItem("token")
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
            showToast("Failed to fetch product details!", "error");
        }
    });
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

