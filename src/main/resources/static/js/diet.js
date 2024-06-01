// Get the search parameter string from the URL
var searchParams = new URLSearchParams(window.location.search);

// Get the value of the "p" parameter
var paramValue = searchParams.get('p');

console.log(paramValue); // This will log "1233"

function mapProductToText(product) {
    return `${product.name} : ${product.calories} | ${product.proteins}, ${product.fats}, ${product.carbs}`
}

previousTime = null;

function updateTable() {
    console.log(document.getElementById('mealDate').value);
    $.get('http://localhost:8080/api/profile/meals/' + paramValue + '?date=' + document.getElementById('mealDate').value,
        function (data) {
            $('#selectedValues tbody').empty();
            data.forEach(function (item, index) {
                console.log(item);

                // Check if it's the first item or if the current time is different from the previous time
                if (index === 0 || item.time !== data[index - 1].time) {
                    // If it's the first item or the current time is different, print the time
                    $('#selectedValues tbody').append('<tr><td>' + item.time + '</td><td>' + item.product.name + '</td></tr>');
                } else {
                    // If the current time is the same as the previous time, only print the product name
                    $('#selectedValues tbody').append('<tr><td></td><td>' + item.product.name + '</td></tr>');
                }
            });
        });
}

let currentDate = new Date().toISOString().split('T')[0];

// Set the value of the mealDate input element to the current date
document.getElementById('mealDate').value = currentDate;


// Set the value of the mealTime input element to the current time
document.getElementById('mealTime').value = "00:00";


$(document).ready(function () {
    updateTable();

    $('.js-example-basic-single').select2({
        ajax: {
            url: function (params) {
                if (params.term === '') {
                    params.term = ' ';
                }
                return 'http://localhost:8080/api/products/' + params.term;
            },
            dataType: 'json',
            delay: 5, // Delay in milliseconds before sending the query
            data: function (params) {
                return {
                    q: params.term // Pass the user input as the 'q' parameter
                };
            },
            processResults: function (data) {
                // Format the data into an array of objects with 'id' and 'text' properties
                let formattedData = data.map(function (product) {
                    return {
                        id: product.id,
                        text: mapProductToText(product) // Assuming 'name' is the property representing the product name
                    };
                });

                return {
                    results: formattedData
                };
            },
            cache: true
        }
    });

    $('#displayValues').on('click', function () {
        $('#selectedValues tbody').empty();
        $('.js-example-basic-single option:selected').each(function () {
            var id = $(this).val();
            var text = $(this).text();
            $('#selectedValues tbody').append('<tr><td>' + id + '</td><td>' + text + '</td></tr>');
        });
        // Clear selected options from the dropdown
        $('.js-example-basic-single').val(null).trigger('change');
    });

    $('#submitButton').on('click', function () {

        var selectedValues = [];
        $('.js-example-basic-single option:selected').each(function () {
            var id = $(this).val();
            var text = $(this).text();
            // Create a JSON object for each selected value
            var selectedValue = {
                product: {
                    id: parseInt(id)
                },
                profile: {
                    id: paramValue
                },
                date: document.getElementById("mealDate").value,
                time: document.getElementById("mealTime").value,
                amount: 100
            };

            // Push the JSON object to the array
            selectedValues.push(selectedValue);
        });

        console.log(selectedValues)
        // Perform PUT request to backend
        $.ajax({
            url: 'http://localhost:8080/api/profile/meals/add',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(selectedValues),
            success: function (response) {
                console.log('Data sent successfully:', response);
                updateTable();
                // Handle success response here
            },
            error: function (xhr, status, error) {
                console.error('Error sending data:', error);
                // Handle error response here
            }
        });

        $('.js-example-basic-single').val('').trigger('change');
    });

});

