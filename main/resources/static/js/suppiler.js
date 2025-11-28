    $(document).ready(function () {
    supplierTableFunction();
});

function supplierAjax() {
    const deferred = $.Deferred();

    const filters = {
        name: "",
        email: "",
        sortBy: "supId",
        order: -1,
        pageNum: 1,
        limit: 10
    };

    $.ajax({
        url: 'http://localhost:8080/inventoryManagementSystem_war/supplier/getAllSupplier',
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
            deferred.resolve(result);
        },
        error: function (xhr) {
            if (xhr.status === 401) {
                // alert("Session expired. Please login again.");
                showToast("Please login!", "error");

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

function supplierTableFunction() {
    $('#suppliersTable').DataTable({
        serverSide: true,
        processing: false,
        ajax: function (data, callback, settings) {
            supplierAjax()
                .done(function (response) {
                    callback(response);
                })
                .fail(function (err) {
                    console.error("Ajax error:", err);
                    callback({data: []});
                });
        },
        columns: [
            {
                data: null,
                render: function (data, type, row) {
                    return `<input type="checkbox" name="supplierSelect" value="${row.supId}">`;
                },
                orderable: false
            },
            {data:null},
            // {data: 'supId'},
            {data: 'name'},
            {data: 'contact'},
            {data: 'email'},

            { render: (d, t, row) =>
                    `<i class="fas fa-edit edit-icon" style="color: #1d4bc5" onclick="editSupplier('${row.supId}')"></i> &nbsp &nbsp <i class="fa-solid fa-eye" style="color: #1d4bc5;" onclick="viewSupplier('${row.supId}')"></i> &nbsp &nbsp <i class="fas fa-trash-alt delete-icon" style="color: #1d4bc5" onclick="deleteSupplier('${row.supId}')"></i>`}

        ]
    });
    applySerialNumber('#suppliersTable', 1);
}

$('#bulkDeleteSupplierBtn').off('click').on('click', function () {

    const selectedIds = $('input[name="supplierSelect"]:checked')
        .map(function () { return $(this).val(); })
        .get();

    if (selectedIds.length === 0) {
        showToast("Please select a supplier!", "warning");

        return;
    }

    if (!confirm("Are you sure you want to delete selected suppliers?")) {
        return;
    }

    $.ajax({
        url: 'http://localhost:8080/inventoryManagementSystem_war/supplier/v1/bulkDeleteSupplier?ids='
            + selectedIds.join(','),
        method: 'DELETE',
        headers: {
            email: sessionStorage.getItem('email'),
            token: sessionStorage.getItem("token")
        },
        success: function (resp) {
            // alert("Selected products deleted successfully!");
            showToast("Suppliers deleted successfully!", "success");

            supplierTableFunction();
        },
        error: function () {
            showToast("Error deleting supplier!", "error");
        }
    });
});


function editSupplier(selectedId){
    // $('#editSupplierBtn').click(function () {
    //     const selectedId = $('input[name="supplierSelect"]:checked').val();
    //     if (!selectedId) {
    //         alert("Please select a supplier to edit!");
    //         return;
    //     }

        $.ajax({
            url: 'http://localhost:8080/inventoryManagementSystem_war/supplier/getSuppiler?supplierId=' + selectedId,
            method: 'GET',
            success: function (resp) {
                const supplier = resp.data;
                $('#supplierId').val(supplier.supId);
                $('#addSupplierName').val(supplier.name);
                $('#addSupplierContact').val(supplier.contact);
                $('#addSupplierEmail').val(supplier.email);
                $('#addSupplierModal').modal('show');

            },
            error: function () {
                showToast("Failed to fetch supplier details!", "error");
                // alert('Failed to fetch supplier details.');
            }
        });
    // });

}
$.validator.addMethod("pattern", function (value, element, param) {
    if (this.optional(element)) {
        return true;
    }
    if (typeof param === "string") {
        param = new RegExp(param);
    }
    return param.test(value);
}, "Invalid format");


$('#addSupplierForm').validate({
    rules: {
        name: {
            required: true,
            minlength: 3,
            pattern: /^[A-Za-z\s]*$/
        },
        contact: {
            required: true,
            minlength: 10,
            pattern: /^[0-9]*$/
        },
        emailid: {
            required: true,
            email: true
        }
    },
    messages: {
        name: {
            required: "Enter your name",
            minlength: "Minimum 3 chars",
            pattern: "Only letters allowed"
        },
        contact: {
            required: "Enter contact",
            minlength: "Please enter 10 digits",
            pattern: "Contact number should be digits"
        },
        emailid: {
            required: "Enter email",
            email: "Enter valid email"
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
        let userData = {
            name: $("#name").val(),
            contact: $("#contact").val(),
            email: $("#emailid").val(),
            action: 'addUser'
        };
        $.ajax({
            type: "POST",
            url: "http://localhost:8080/inventoryManagementSystem_war/supplier/addSupplier",
            contentType: "application/json",
            data: JSON.stringify(userData),
            success: function (response) {
                showToast("Supplier added successfully!", "success");
                $('#addSupplierModal').modal('hide');
                supplierTableFunction();
                form.reset();
            },
            error: function (xhr) {
                showToast("Error adding supplier!", "error");
            }
        });
    }
});

$('#editSupplierForm').validate({
    rules: {
        supplierName: {
            required: true,
            minlength: 3,
            pattern: /^[A-Za-z\s]*$/
        },
        supplierContact: {
            required: true,
            minlength: 10,
            maxlength: 10,
            pattern: /^[0-9]*$/
        },
        supplierEmail: {
            required: true,
            email: true
        },
    },
    messages: {
        supplierName: {
            required: "Enter supplier name",
            minlength: "Minimum 3 chars",
            pattern: "Only letters allowed"
        },
        supplierContact: {
            required: "Enter Contact details",
            minlength: "Please enter minimum 10 digits",
            maxlength: "Please enter maximum 10 digits",
            pattern: "Price number should be digits"
        },
        supplierEmail: {
            required: "Enter email",
            pattern: "Enter valid email"
        },
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
        let supplierData = {
            supId:parseInt( $('#supplierId').val()),
            name: $('#supplierName').val(),
            contact: $('#supplierContact').val(),
            email: $('#supplierEmail').val()

        };
        $.ajax({
            method: "PUT",
            url: "http://localhost:8080/inventoryManagementSystem_war/supplier/updateSupplier",
            contentType: "application/json",
            data: JSON.stringify(supplierData),
            success: function (response) {
                showToast("Supplier aupdate successfully!", "success");
                // alert("Suppiler updated successfully!");
                $('#editSupplierModal').modal('hide');
                supplierTableFunction();
                form.reset();
            },
            error: function (xhr) {
                showToast("Error updating supplier!", "error");
            }
        });
    }
});

function deleteSupplier(selectedId){


        $.ajax({
            url: 'http://localhost:8080/inventoryManagementSystem_war/supplier/deleteSupplier?supplierId=' + selectedId,
            method: 'DELETE',
            success: function (resp){
                showToast("Supplier deleted successfully!", "success");
                supplierTableFunction();
            },
            error: function (resp){
                showToast("Error deleting supplier!", "error");
            }
        });
    // });

}


function viewSupplier(selectedId){

        $.ajax({
            url: 'http://localhost:8080/inventoryManagementSystem_war/supplier/getSuppiler?supplierId=' + selectedId,
            method: 'GET',
            headers: {
                email: sessionStorage.getItem('email'),
                Authorization: "Bearer " + sessionStorage.getItem("token")
            },
            success: function (resp) {
                const supplier = resp.data;

                // Populate supplier details in modal
                $('#sId').text(supplier.supId || '-');
                $('#sName').text(supplier.name || '-');
                $('#sEmail').text(supplier.email || '-');
                $('#sContact').text(supplier.contact || '-');

                $('#supplierDetailsModal').modal('show');
            },
            error: function (resp) {
                showToast("Error fetching suppliers!", "error");
                // alert("Error fetching supplier! Please try again.");
                console.error("Fetch supplier failed:", resp);
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


