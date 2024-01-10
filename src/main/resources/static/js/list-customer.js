$('document').ready(function() {
    $('table').on('click','#removeButton', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');
        $.get(href, function (customer, status) {
            $('#idEdit').val(customer.id);
        });
        $('#removeModal').modal();
    });
    $('table').on('click', '#detailButton' , function (event) {
        event.preventDefault();
        var href = $(this).attr('href');
        $.get(href, function (detail, status) {
            $('#totalPrice').val(detail[1]);
            $('#totalQuantity').val(detail[2]);
            $('#totalOrder').val(detail[3])
        });
        $('#detailModal').modal();
    });
});