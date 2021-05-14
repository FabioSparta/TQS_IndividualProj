function temporaryglow() {
    $(".caq_info").addClass('temporary_glow');
    setTimeout(function(){
        $(".caq_info").removeClass('temporary_glow');
    }, 1100);
}

GET: $(document).ready(
    function () {  // REQUEST FROM SEARCH BOX
        $("#getCityInfo").click(function (event) {
            event.preventDefault();
            ajaxGet();
            $(".xerror").text("");
        });

        // DO GET
        function ajaxGet() {
            $.ajax({
                type: "GET",
                url: "city?city_name=" + $('#city').val() + chosenDate() ,
                success: function (caq) {
                    console.log("Success: ", caq);
                    buildCard(caq);
                    temporaryglow();
                },
                error: function (e) {
                    $("#searchbox_err").text(e.responseText);
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
            $(".xerror").text("");
        });

        // DO GET
        function ajaxGet() {
            $.ajax({
                type: "GET",
                url: "city?city_name=" + $('#city2').val()  + chosenDate(),
                success: function (caq) {
                    console.log("Success: ", caq);
                     buildCard(caq);
                    temporaryglow();
                },
                error: function (e) {
                    $("#select_err").text(e.responseText);
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
            $(".xerror").text("");
        });

        // DO GET
        function ajaxGet() {
            $.ajax({
                type: "GET",
                url: "coords?latitude=" + $('#latitude').val() + "&longitude=" + $('#longitude').val() + chosenDate(),
                success: function (caq) {
                    console.log("Success: ", caq);
                    buildCard(caq);
                    temporaryglow();
                },
                error: function (e) {
                    $("#coords_err").text(e.responseText);
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

        if(Object.keys(elem['dayMap']).length>0  ){
            components += "|" +  elem['name'] + " - " + elem['dayMap'][caq.date]['avg'] + "|  ";
        }
    }
    if(components == ''){
        components += "Unknown";
    }
    const new_card = `<div class="card text-center caq_info bgwhite" style=" margin:auto ; margin-bottom: 20px ;max-width: 700px" > 
    <div class="delete_icon">
     <i  style="color: red" class="fas fa-times-circle fa-lg"></i>
  </div>
        <div class="card-header">
            ${caq.city}
        </div>
        <div class="card-body">
            <p class="card-title">Date: ${caq.date} </p>
            <p class="card-title">Air Quality Indice: ${caq.aqi != "" ? caq.aqi : "Unknown" } </p>
            <p class="card-title">Latitude: ${ Math.round(caq.latitude * 100) / 100}</p>
            <p class="card-title">Longitude: ${Math.round(caq.longitude * 100) / 100}</p>
            <p class="card-title">Components: ${components} </p>
             <p class="card-title">More Info: <a href="${caq.link}"  target="_blank"> Here </a> </p>
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