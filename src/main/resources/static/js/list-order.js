$('document').ready(function() {
    $('table').on('click', '#removeButton', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');
        $.get(href, function (order, status) {
            $('#idEdit').val(order.id);
        });
        $('#removeModal').modal();
    });
});