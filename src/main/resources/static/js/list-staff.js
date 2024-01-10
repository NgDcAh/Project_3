$('document').ready(function() {
    $('table').on('click', '#removeButton', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');
        $.get(href, function (staff, status) {
            $('#idEdit').val(staff.id);
        });
        $('#removeModal').modal();
    });
});