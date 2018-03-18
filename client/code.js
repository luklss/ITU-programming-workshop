
$(document).ready(function() {
    var baseUrl = "http://localhost:8080";
    $("#searchbutton").click(function() {
        console.log("Sending request to server.");
        $.ajax({
            method: "GET",
            url: baseUrl + "/search",
            data: {query: $('#searchbox').val()}
        }).success( function (data) { 
            console.log("Received response " + data);
            $("#responsesize").html("<p>"+ data.length + " matching websites were found:</p>");
            buffer = "<ul class='fa-ul'>\n";
            $.each(data, function(index, value) { 
                buffer += "<li><i class='fa-li fa fa-caret-right'></i><a href=\"" + value + "\">" + value + "</a></li>\n";
            });
            buffer += "</p>";
            $("#urllist").html(buffer);
        });
    });
    //Triggers the searchbutton.click function when pressing enter on the searchbox
    $('#searchbox').keypress(function(e){
        if(e.keyCode==13)
        $("#searchbutton").click();
    });
});

