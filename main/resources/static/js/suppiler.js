// $('#suppliers').ready(function () {
//     let table2 = $('#suppliersTable').DataTable({
//         serverSide: true,
//         processing: true,
//         ajax: function (data, callback) {
//             const email = sessionStorage.getItem("email");
//             const sessionId = sessionStorage.getItem("sessionId");
//             var currentPage = 0;
//             const filters = {
//                 name: "",
//                 email: "",
//                 sortBy: data.columns[data.order[0].column].data,
//                 order: data.order[0].dir === 'asc' ? 1 : -1,
//                 pageNum: currentPage + 1,
//                 limit: 10
//             };
//             $.ajax({
//                 url: 'http://localhost:8080/inventoryManagementSystem_war/supplier/getAllSupplier',
//                 type: 'GET',
//                 data: filters,
//                 headers: {email: email, sessionId: sessionId},
//                 dataType: 'json',
//                 success: function (resp) {
//                     currentPage++;
//                     if (resp.data) {
//                         callback({
//                             data: resp.data,
//                             recordsTotal: resp.recordsTotal,
//                             recordsFiltered: resp.recordsFiltered
//                         });
//                     } else {
//                         callback({data: []});
//                     }
//                 },
//                 error: function (xhr) {
//                     if (xhr.status === 401) {
//                         alert("Session expired. Please login again.");
//                         sessionStorage.clear();
//                         window.location.href = "login.html";
//                     } else {
//                         console.error("Error:", xhr.responseText);
//                     }
//                 }
//             });
//         },
//         columns: [
//             {data: 'supId'},
//             {data: 'name'},
//             {data: 'email'},
//             {data: 'contact'},
//             // {data: 'city'},
//             // {data: 'country'}
//         ]
//     });
//
// });

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
            // sessionId: sessionStorage.getItem('sessionId'),
            token: sessionStorage.getItem("token")

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
                data: inner.data || [],
                recordsTotal: inner.totalRecords || 0,
                recordsFiltered: inner.filteredRecords || 0
            };
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
                    return `<input type="radio" name="supplierSelect" value="${row.supId}">`;
                },
                orderable: false
            },
            {data: 'supId'},
            {data: 'name'},
            {data: 'contact'},
            {data: 'email'},
            {"data": null,
                "defaultContent": '<i class="fas fa-edit edit-icon"></i> <i class="fas fa-trash-alt delete-icon"></i> <i class="fa-solid fa-circle-info"></i>',
                "render": function (data, type, row) {

                    let icons = '<button id="editSupplierBtn" onclick="editProduct(${"#row.productId"})"> <i class="fas fa-edit edit-icon" data-id="' + row.id + '"></i> </button>';
                    icons += '<button id="deleteSupplierBtn"> <i class="fas fa-trash-alt delete-icon" data-id="' + row.id + '"></i> </button>';
                    icons += '<button id="viewSupplierBtn"> <i class="fa-solid fa-circle-info" data-id="' + row.id + '"></i> </button>';
                    return icons;},
                orderable: false
            }

        ]
    });
}


$('#editSupplierBtn').click(function () {
    const selectedId = $('input[name="supplierSelect"]:checked').val();
    if (!selectedId) {
        alert("Please select a supplier to edit!");
        return;
    }

    $.ajax({
        url: 'http://localhost:8080/inventoryManagementSystem_war/supplier/getSuppiler?supplierId=' + selectedId,
        method: 'GET',
        success: function (resp) {
            const supplier = resp.data;
            $('#supplierId').val(supplier.supId);
            $('#supplierName').val(supplier.name);
            $('#supplierContact').val(supplier.contact);
            $('#supplierEmail').val(supplier.email);
            $('#editSupplierModal').modal('show');
        },
        error: function () {
            alert('Failed to fetch supplier details.');
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
                alert("Supplier added successfully!");
                $('#addSupplierModal').modal('hide');
                table2.ajax.reload();
                form.reset();
            },
            error: function (xhr) {
                alert("Error adding supplier: " + xhr.responseText);
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
                alert("Suppiler updated successfully!");
                $('#editSupplierModal').modal('hide');
                table.ajax.reload();
                form.reset();
            },
            error: function (xhr) {
                alert("Error adding supplier: " + xhr.responseText);
            }
        });
    }
});

$('#deleteSupplierBtn').click(function (){

    const selectedId = $('input[name="supplierSelect"]:checked').val();
    if (!selectedId) {
        alert("Please select a supplier to delete!");
        return;
    }

    $.ajax({
        url: 'http://localhost:8080/inventoryManagementSystem_war/supplier/deleteSupplier?supplierId=' + selectedId,
        method: 'DELETE',
        success: function (resp){
            alert("Supplier deleted successfully!");
        },
        error: function (resp){
            alert("Error deleting supplier! Please try again.");
        }
    });
});



$('#viewSupplierBtn').click(function () {
    const selectedId = $('input[name="supplierSelect"]:checked').val();
    if (!selectedId) {
        alert("Please select a supplier to view!");
        return;
    }

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
            alert("Error fetching supplier! Please try again.");
            console.error("Fetch supplier failed:", resp);
        }
    });
});
