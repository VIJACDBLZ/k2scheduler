$( document ).ready(function() {
    console.log( "Starting K2Scheduler" );


    //initializing cache var
    window.K2cache = {
        jobs:[],
        jobGroups:[],
        selectedRequestConfig:{},
        placeholderFields:[],
        benchmarkTable:'',
        benchmarkIdLoaded:''
    }

    K2cache.jobsTable = $('#jobs-table').DataTable({
        "bInfo" : false,
        oLanguage: {
            sLengthMenu: "_MENU_",
        }
    });


    fetch("/quartz/getAllJobs")
        .then(function(response) {
            if (!response.ok) {
                alert("Error!, Unable to contact K2Scheduler "+response.statusText);
                throw Error(response.statusText);
            }
            return response.json();
        }).then(function(response) {

            K2cache.jobs  = response;
            K2cache.jobGroups = K2cache.jobs.map(job => job["jobGroup"]);

            buildDropDown(K2cache.jobGroups);
        }).catch(function(error) {
            console.log(error);
        });


});

//Find the input search box
let search = document.getElementById("searchGroup");

//Find every item inside the dropdown
let items = document.getElementsByClassName("dropdown-item");
function buildDropDown(values) {
    let contents = []
    for (let name of values) {
    contents.push('<input type="button" class="dropdown-item" type="button" value="' + name + '"/>');
    }
    $('#menuItems').append(contents.join(""));

    //Hide the row that shows no items were found
    $('#empty').hide();
}

//Capture the event when user types into the search box
window.addEventListener('input', function () {
    filter(search.value.trim().toLowerCase())
})

//For every word entered by the user, check if the symbol starts with that word
//If it does show the symbol, else hide it
function filter(word) {
    let length = items.length
    let collection = []
    let hidden = 0
    for (let i = 0; i < length; i++) {
    if (items[i].value.toLowerCase().startsWith(word)) {
        $(items[i]).show()
    }
    else {
        $(items[i]).hide()
        hidden++
    }
    }

    //If all items are hidden, show the empty view
    if (hidden === length) {
    $('#empty').show()
    }
    else {
    $('#empty').hide()
    }
}

//If the user clicks on any item, set the title of the button as the text of the item
$('#menuItems').on('click', '.dropdown-item', function(){
    let selectedRequestConfigTitle = $(this)[0].value;

    $('#dropdown_requests').text(selectedRequestConfigTitle);
    $("#dropdown_requests").dropdown('toggle');

    let selectedRequestConfig = QTcache.requestConfigList.find( requestConfig => requestConfig["request-title"] == selectedRequestConfigTitle);

    console.log("Request config selected : "+ selectedRequestConfig);

    QTcache.selectedRequestConfig = selectedRequestConfig;

    $("#first-endpoint").val(selectedRequestConfig["first-endpoint"]);
    $("#second-endpoint").val(selectedRequestConfig["second-endpoint"]);
    $("#request-template").text(selectedRequestConfig["request-template"]);

})

$("#request-file").on('change', function(event){
    var files = event.target.files; // FileList object
    var reader = new FileReader();
    try{
        reader.onload = function(event)

        {
            let lines = event.target.result.split('\n');
            let placeholderFields = lines.filter(String).map( line => line.split(',').map( field => field.trim()));
            console.log(placeholderFields);

            QTcache.placeholderFields = placeholderFields;
            $('#placeholder-display').text( JSON.stringify(placeholderFields, undefined, 2));
            $("#request-file").val(null);
        };

        reader.readAsText(files[0]);

    }catch(e){
        alert("Invalid file!");
        return
    }

});


$('#start-benchmark').on('click', function(){

    let firstEndpoint = $('#first-endpoint').val();
    let secondEndpoint = $('#second-endpoint').val();
    let requestTemplate = $('#request-template').text();

    if(!QTcache.placeholderFields || QTcache.placeholderFields.length == 0){
        alert("Placedholder fields not loaded yet!");
        return;
    }

    const data = {
        firstendpoint: firstEndpoint,
        secondendpoint: secondEndpoint,
        requesttemplate: requestTemplate,
        placeholderfields: QTcache.placeholderFields,
        requestconfigid: QTcache.selectedRequestConfig["id"]
    };

    fetch('benchmark', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    })
    .then(response => response.json())
    .then(data => {
      console.log('Success:', data);
      QTcache.benchmarkTable.clear();
      data.benchmark.forEach(function (benchmark, index) {
        QTcache.benchmarkTable.row.add([
         benchmark["request"],
         benchmark["firstApiUrl"],
         benchmark["firstApiResponse"],
         benchmark["firstApiLatency"],
         benchmark["secondApiUrl"],
         benchmark["secondApiResponse"],
         benchmark["secondApiLatency"],
         benchmark["responseIdentical"],
        ]).draw( false );
      });

      QTcache.benchmarkIdLoaded = data.benchmarkId;
      $('#benchmarkId').val(QTcache.benchmarkIdLoaded);
      showOverlay(true);

    })
    .catch((error) => {
      console.error('Error:', error);
    });

});

var showOverlay = function(show){
    if(!!show)
        $('#benchmark-details').css("width", "100%");
    else
        $('#benchmark-details').css("width", "0%");
}

$('#load-benchmark').on('click', function() {

  let benchmarkId = $('#benchmarkId').val();
  if(QTcache.benchmarkIdLoaded == benchmarkId){
    showOverlay(true);
  }else{
    if(!benchmarkId){
        alert("Invalid BenchmarkId");
        return;
    }

    fetch('benchmark/'+ benchmarkId, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          }
        })
        .then(response => response.json())
        .then(data => {

            QTcache.benchmarkTable.clear();
            data.forEach(function (benchmark, index) {
                QTcache.benchmarkTable.row.add([
                 benchmark["request"],
                 benchmark["firstApiUrl"],
                 benchmark["firstApiResponse"],
                 benchmark["firstApiLatency"],
                 benchmark["secondApiUrl"],
                 benchmark["secondApiResponse"],
                 benchmark["secondApiLatency"],
                 benchmark["responseIdentical"],
                ]).draw( false );
            });
            QTcache.benchmarkIdLoaded = benchmarkId;
            showOverlay(true);

        }).catch((error) => {
           console.error('Error:', error);
        });








  }
});

$('#hide-benchmark').on('click', function() {
  showOverlay(false);
});




