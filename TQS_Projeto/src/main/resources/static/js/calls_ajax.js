GET: $(document).ready(
    function () {  // REQUEST FROM SEARCH BOX
        $("#getCityInfo").click(function (event) {
            event.preventDefault();
            ajaxGet();
        });

        // DO GET
        function ajaxGet() {
            $.ajax({
                type: "GET",
                url: "city?city_name=" + $('#city').val() + chosenDate() ,
                success: function (caq) {
                    console.log("Success: ", caq);
                    buildCard(caq);
                },
                error: function (e) {
                    $("#getResultDiv").html("<strong>Failed to Load Rooms</strong>");
                    console.log("ERROR: ", e);
                }
            });
        }
    });


GET: $(document).ready(
    function () {  // REQUEST FROM SELECT BOX
        $("#getCityInfo2").click(function (event) {
            event.preventDefault();
            ajaxGet();
        });

        // DO GET
        function ajaxGet() {
            $.ajax({
                type: "GET",
                url: "city?city_name=" + $('#city2').val()  + chosenDate(),
                success: function (caq) {
                    console.log("Success: ", caq);
                     buildCard(caq);
                },
                error: function (e) {
                    $("#getResultDiv").html("<strong>Failed to Load Rooms</strong>");
                    console.log("ERROR: ", e);
                }
            });
        }
    });


GET: $(document).ready(
    function () {  // REQUEST FROM LATITUDE & LONGITUDE
        $("#coordsSearch").click(function (event) {
            event.preventDefault();
            ajaxGet();
        });

        // DO GET
        function ajaxGet() {
            $.ajax({
                type: "GET",
                url: "coords?latitude=" + $('#latitude').val() + "&longitude=" + $('#longitude').val() + chosenDate(),
                success: function (caq) {
                    console.log("Success: ", caq);
                    buildCard(caq);
                },
                error: function (e) {
                    $("#getResultDiv").html("<strong>Failed to Load Rooms</strong>");
                    console.log("ERROR: ", e);
                }
            });
        }
    });





$(document).ready(
    function (){
        var day = new Date();
        var dd;
        var mm;
        var yyyy;
        var buttons = '';

        for(let i=0;i<5;i++){
            dd = day.getDate();
            mm = day.getMonth() +1 ;
            yyyy = day.getFullYear();
            if(dd<10) {dd='0'+dd;}
            if(mm<10) {mm='0'+mm;}

            buttons += '<button style="margin-bottom: 5px; margin-top: 1px;" type="button" class="btn btn-light datebutton">' +
                + yyyy + '-' + mm + '-' + dd +
                '</button><br>';
            day.setDate(day.getDate() + 1);
        }
        $('#days').append(buttons);

        $(".datebutton").click(function(){
            // ALL
            $(".datebutton").removeClass('btn btn-info');
            $(".datebutton").removeClass('dayclicked');
            $(".datebutton").addClass('btn btn-light');

            //BUTTON CLICKED
            $(this).removeClass('btn btn-light');
            $(this).addClass('btn btn-info');
            $(this).addClass('dayclicked');
        });
    }
);

$(document).ready(
    function (){
        $('#clearResults').click(function(){
            const myNode = document.getElementById("results");
            while (myNode.firstChild) {
                myNode.removeChild(myNode.lastChild);
            }
        });
    }
);

function chosenDate(){
    var chosenDate = $(".dayclicked");
    if(chosenDate.text()!=""){
        return "&date=" + chosenDate.text()
    }
    return "";
}
function buildCard(caq){
    var components = '';
    var elem;
    for(let i=0;i<caq.components.length;i++){
        elem = caq.components[i];
        components += "|" +  elem['name'] + " - " + elem['dayMap'][caq.date]['avg'] + "|  ";
    }
    const new_card = `<div class="card text-center bgwhite2" style="margin-bottom: 20px" > 
    <div class="delete_icon">
     <i  style="color: red" class="fas fa-times-circle fa-lg"></i>
  </div>
        <div class="card-header">
            ${caq.city}
        </div>
        <div class="card-body">
            <p class="card-title">Air Quality Indice: ${caq.date} </p>
            <p class="card-title">Air Quality Indice: ${caq.aqi} </p>
            <p class="card-text">Latitude: ${ Math.round(caq.latitude * 100) / 100}</p>
            <p class="card-text">Longitude: ${Math.round(caq.longitude * 100) / 100}</p>
            <p class="card-text">Components: ${components} </p>
        </div>
        <div class="card-footer text-muted">

        </div>
    </div>`;
    $('#results').prepend(new_card);

    $(".delete_icon").click(function(){
        console.log("Inside function!")
        this.parentElement.remove();
    });


}