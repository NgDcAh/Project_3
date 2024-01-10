$(document).ready(function() {
    $('table').on('click', '#importButton', function(event) {
        event.preventDefault();
        var href = $(this).attr('href');

        $.get(href, function(product, status) {
            $('#idEdit').val(product.id);
            $('#nameEdit').val(product.name);
        });

        $('#editModal').modal();
    });
});