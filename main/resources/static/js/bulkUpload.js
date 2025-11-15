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

// $('#bulkUploadForm').submit(function (e){
//     e.preventDefault();
//     const inputFile=$('#bulkFile')[0];
//     if(inputFile.files.length===0){
//         $('#msg').html('<span class="text-danger">Please select a file!</span>');
//         return;
//     }
//     var formData=new FormData();
//     formData.append('file',inputFile[0]);
//     $.ajax({
//         url: 'http://localhost:8080/inventoryManagementSystem_war/bulkUpload/uploadFile',
//         type:'POST',
//         data:formData,
//         processData:false,
//         contentType:false,
//         success:function (response){
//             console.log(response);
//             alert(response);
//             $('#msg').html('<span class="text-success"><h3>Data uploaded: '+response.validRecords+' </h3></span>');
//             $('#bulkFile').val('');
//
//         },
//         error:function (xhr, status, error){
//             $('#msg').html('<span class="text-danger">'+xhr.responseText+'</span>');
//         }
//
//     });
// });

// $('#bulkUploadForm').submit(function (e) {
//     e.preventDefault();
//
//     const inputFile = $('#bulkFile')[0];
//
//     if (inputFile.files.length === 0) {
//         $('#msg').html('<span class="text-danger">Please select a file!</span>');
//         return;
//     }
//
//     const formData = new FormData();
//     formData.append('file', inputFile.files[0]);
//
//     $.ajax({
//         url: 'http://localhost:8080/inventoryManagementSystem_war/bulkUpload/uploadFile',
//         type: 'POST',
//         data: formData,
//         processData: false,
//         contentType: false,
//         success: function (response) {
//             console.log("Upload response:", response);
//             $('#uploadResult').html('<span class="text-success"><h3>Data uploaded: ' + response.validRecords + '</h3></span>');
//             $('#bulkFile').val('');
//         },
//         error: function (xhr, status, error) {
//             console.error("Upload failed:", xhr);
//             $('#uploadResult').html('<span class="text-danger">' + xhr.responseText + '</span>');
//         }
//     });
// });
//

// $('#errorFileBtn').click(function () {
//
//     fetch('http://localhost:8080/inventoryManagementSystem_war/bulkUpload/downloadErrorFile')
//         .then(response => response.blob())
//         .then(blob => {
//             const url = URL.createObjectURL(blob);
//             const a = document.createElement('a');
//             a.href = url;
//             a.download = 'ErrorFile.xlsx';
//             document.body.appendChild(a);
//             a.click();
//             document.body.removeChild(a);
//             URL.revokeObjectURL(url);
//         })
//         .catch(error => console.error('Error downloading Excel:', error));
// });

$(document).ready( function (){
    fileUploadTableFunction();
});

// $('#errorFileBtn').click(function () {
//     const token = sessionStorage.getItem("token");
//
//     $.ajax({
//         url: 'http://localhost:8080/inventoryManagementSystem_war/bulkUpload/downloadErrorFile',
//         method: 'GET',
//         xhrFields: {
//             responseType: 'blob'  // ensures we get binary data (the Excel file)
//         },
//         // beforeSend: function (xhr) {
//         //     if (token) {
//         //         xhr.setRequestHeader("Authorization", "Bearer " + token);
//         //     }
//         // },
//         headers: {
//             email: sessionStorage.getItem('email'),
//             // sessionId: sessionStorage.getItem('sessionId'),
//             token: sessionStorage.getItem("token")
//         },
//         success: function (data, status, xhr) {
//             const blob = new Blob([data], { type: xhr.getResponseHeader('Content-Type') });
//             const url = window.URL.createObjectURL(blob);
//             const a = document.createElement('a');
//             a.href = url;
//             a.download = 'ErrorFile.xlsx';
//             document.body.appendChild(a);
//             a.click();
//             document.body.removeChild(a);
//             window.URL.revokeObjectURL(url);
//         },
//         error: function (xhr, status, error) {
//             console.error('Error downloading Excel:', error);
//             alert('Failed to download error file.');
//         }
//     });
// });

$('#errorFileBtn').click(function () {
    $.ajax({
        url: 'http://localhost:8080/inventoryManagementSystem_war/bulkUpload/downloadErrorFile',
        method: 'GET',
        xhrFields: { responseType: 'blob' },
        headers: {
            email: sessionStorage.getItem('email'),
            token: sessionStorage.getItem("token")
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
            alert("Download failed: " + xhr.status + " " + xhr.statusText);
        }
    });
});


$('#bulkUploadBtn').click(async function (e) {
    e.preventDefault();

    const inputFile = $('#bulkFile')[0];

    if (inputFile.files.length === 0) {
        $('#msg').html('<span class="text-danger">Please select a file!</span>');
        return;
    }
    const active = await checkActiveUpload();
    if (active.status === 'BLOCKED') {
        alert("Previous upload not completed. Please wait before uploading another.");
        return;
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
            email: sessionStorage.getItem('email'),
            token: sessionStorage.getItem("token")
        },
        success: function (response, status, xhr) {
            $('#uploadResult').html('<span class="text-danger"><h4>File uploaded successfully.</h4></span>');
            if(response.data.invalidRecords>0){
                $('#msg').html('<span class="text-danger"><h4>File uploaded successfully. Your file contains error. Please click on the download error file button.</h4></span>');
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

// bulkResult fileUploadTable

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

function fileUploadTableFunction() {
    $('#fileUploadTable').DataTable({
        serverSide: true,
        processing: true,
        ajax: function (data, callback, settings) {
            fileAjax()
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
                // data: null,
                // render: function(data, type, row) {
                //     return `<input type="checkbox" name="productSelect" value="${row.productId}">`;
                // },
                // orderable: false
            },
            { data: 'id' },
            { data: 'data.fileName' },
            { data: 'data.uploadDate' },
            { data: 'data.status' }
            // { data: 'price' },
            // { data: 'quantity' },
            // {"data": null,
            //     "defaultContent": '<i class="fas fa-edit edit-icon"></i> <i class="fas fa-trash-alt delete-icon"></i> <i class="fa-solid fa-circle-info"></i>',
            //     "render": function (data, type, row) {
            //
            //         let icons = '<button id="editProductBtn" onclick="editProduct(${"#row.productId"})"> <i class="fas fa-edit edit-icon" data-id="' + row.id + '"></i> </button>';
            //         icons += '<button id="deleteProductBtn"> <i class="fas fa-trash-alt delete-icon" data-id="' + row.id + '"></i> </button>';
            //         icons += '<button id="viewProductBtn"> <i class="fa-solid fa-circle-info" data-id="' + row.id + '"></i> </button>';
            //         return icons;}
            // }
        ]
    });
}

function checkActiveUpload() {
    return $.ajax({
        url: 'http://localhost:8080/inventoryManagementSystem_war/bulkUpload/checkActiveUpload',
        method: 'GET',
        headers: {
            email: sessionStorage.getItem('email'),
            token: sessionStorage.getItem("token")
        }
    });
}
//
// $('#bulkUploadBtn').click(async function (e) {
//     e.preventDefault();
//
//     const email = sessionStorage.getItem('email');
//     const token=sessionStorage.getItem('token');
//     const inputFile = $('#bulkFile')[0];
//
//
// });

