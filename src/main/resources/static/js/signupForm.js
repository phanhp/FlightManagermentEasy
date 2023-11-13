$(document).ready(function () {
    $('#profile-image').on('change', function () {
        let fileInput = this;
        let imageOption1 = $('#profile-image-part1');
        let imageOption2 = $('#profile-image-part2');
        let formData = new FormData();
        formData.append('file', fileInput.files[0]);

        $.ajax({
            url: '/ajax/uploadAccountImage',
            type: 'POST',
            data: formData,
            cache: false,
            contentType: false,
            processData: false,
            success: function (data) {
                imageOption1.empty();
                imageOption1.append('<img class="profile-image" src="data:image/png;base64,' + data + '" alt="image">');
                imageOption2.empty();
                imageOption2.append('<img class="profile-image" src="data:image/png;base64,' + data + '" alt="image">');
            },
            error: function (){
                alert("error");
            }
        });
    });
});