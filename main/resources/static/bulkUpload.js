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

$('#bulkUploadForm').submit(function (e) {
    e.preventDefault();

    const inputFile = $('#bulkFile')[0];

    if (inputFile.files.length === 0) {
        $('#msg').html('<span class="text-danger">Please select a file!</span>');
        return;
    }

    const formData = new FormData();
    formData.append('file', inputFile.files[0]); // ✅ ensure .files[0] not undefined

    $.ajax({
        url: 'http://localhost:8080/inventoryManagementSystem_war/bulkUpload/uploadFile',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
            console.log("Upload response:", response);
            $('#uploadResult').html('<span class="text-success"><h3>Data uploaded: ' + response.validRecords + '</h3></span>');
            $('#bulkFile').val(''); // ✅ clear file input
        },
        error: function (xhr, status, error) {
            console.error("Upload failed:", xhr);
            $('#uploadResult').html('<span class="text-danger">' + xhr.responseText + '</span>');
        }
    });
});


$('#errorFileBtn').click(function () {

    fetch('http://localhost:8080/inventoryManagementSystem_war/bulkUpload/downloadErrorFile')
        .then(response => response.blob())
        .then(blob => {
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'ErrorFile.xlsx';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);
        })
        .catch(error => console.error('Error downloading Excel:', error));
});
