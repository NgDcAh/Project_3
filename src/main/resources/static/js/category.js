$('document').ready(function() {
    $('table').on('click','#editButton', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');
        $.get(href, function (category, status) {
            $('#idEdit').val(category.id);
            $('#nameEdit').val(category.name);
        });
        $('#editModal').modal();
    });
});