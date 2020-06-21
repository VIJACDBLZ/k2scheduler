$( document ).ready(function() {
    console.log( "Starting K2Scheduler" );
    //initializing cache var
    window.K2cache = {
        jobs:{},
        jobGroups: []
    }


    var runJobEl = '<a href="#" class="runJob" data-toggle="tooltip" title="Run Now!"><i class="material-icons">launch</i></a>';
    var pauseBtnEl = '<a href="#" class="pauseJob" data-toggle="tooltip" title="Pause Trigger"><i class="material-icons">pause_circle_filled</i></a>';
    var resumeBtnEl = '<a href="#" class="resumeJob" data-toggle="tooltip" title="Resume Trigger"><i class="material-icons">play_circle_filled</i></a>';
    var editBtlEl = '<a href="#" class="editJob" data-toggle="tooltip" title="Edit Job"><i class="material-icons">create</i></a>';


    K2cache.jobsTable = $('#jobs-table').DataTable({
        "order": [ 0, 'asc' ],
        "bInfo" : false,
        "pageLength": 10,
        "lengthMenu": [ [10, -1], [10, "All"] ],
        oLanguage: {
            sLengthMenu: "_MENU_",
        },
        columnDefs: [
        {
            targets: 0,
            width: "30px"
        },
        {
            targets: 6,
            width: "50px",
            orderable:false
        },
        {
            targets: 7,
            width: "120px",
            className:"joblisting-dependsonCol"
        },
        {
            targets:-1,
            data: null,
            width:"150px",
            className: "center",
            orderable: false,
            render: function ( data, type, row ) {

                if(data[3] == "NORMAL")
                    return runJobEl+pauseBtnEl+editBtlEl;
                else if (data[3] == "PAUSED")
                    return runJobEl+resumeBtnEl+editBtlEl;
                else
                    return "INVALID STATE";

            }

        }
        ]
    });


    fetch("/quartz/getAllJobs")
        .then(function(response) {
            if (!response.ok) {
                alert("Error!, Unable to contact K2Scheduler "+response.statusText);
                throw Error(response.statusText);
            }
            return response.json();
        }).then(function(response) {

            updateCacheAndJobsListing(response, false);
            showNotification("Jobs loaded!", "success");

        }).catch(function(error) {
            console.log(error);
            alert(error);
        });


});

/*###################################### JOBS LISTING ####################################################################*/

var updateCacheAndJobsListing = function(jobs , partialupdate){

    jobs.forEach(function(job , idx){
        K2cache.jobs[job["jobId"]] = job;
        if(!!partialupdate){
            K2cache.jobsTable.row("#"+job["jobId"]).data([
               job["jobId"],
               job["jobName"],
               job["jobGroup"],
               job["jobStatus"],
               job["cronExpression"],
               job["batchType"],
               job["deploymentId"],
               job["dependsOnBatchTypes"],
               new Date(job["k2TriggerDetail"]["nextTrigger"]).toLocaleString('en-US'),
               ""
            ]).invalidate().draw();
        }
    });

    $('[data-toggle="tooltip"]').tooltip({trigger : 'hover'});

    if(!partialupdate)
        loadJobsListing(K2cache.jobs, false);
};

var loadJobsListing = function(jobs, append){

    if(!append)
        K2cache.jobsTable.clear();


    for (const [jobId, job] of Object.entries(jobs)) {
      K2cache.jobsTable.row.add([
               jobId,
               job["jobName"],
               job["jobGroup"],
               job["jobStatus"],
               job["cronExpression"],
               job["batchType"],
               job["deploymentId"],
               job["dependsOnBatchTypes"],
               new Date(job["k2TriggerDetail"]["nextTrigger"]).toLocaleString('en-US'),
               ""
      ]).node().id = jobId;


      //Add group if it not already in cache
      if(K2cache.jobGroups.indexOf(job["jobGroup"]) == -1)
        K2cache.jobGroups.push(job["jobGroup"]);

    }

    buildDropDown(K2cache.jobGroups);
    K2cache.jobsTable.draw( false );

    $('[data-toggle="tooltip"]').tooltip('hide');
    $('[data-toggle="tooltip"]').tooltip({trigger : 'hover'});

};

/*###############################################           JOB ACTIONS        ###################################################################*/


//run job once
var runJob = function(jobId){

    let job = K2cache.jobs[jobId];
    let data = {
        "jobName": job["jobName"],
        "jobGroup": job["jobGroup"]
    };

    fetch("/quartz/runJob?t=" + new Date().getTime(), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    })
    .then(response => response.json())
    .then(response => {

      if(!response.valid){
        showNotification("Error! : "+response.msg , "error");
        return;
      }

      showNotification(job.jobName+" triggered successfully!" , "success");


    })
    .catch((error) => {
      console.error('Error:', error);
      showNotification("Error! : "+error , "error");
    });

};

var pauseJob = function(jobId){

    let job = K2cache.jobs[jobId];
    let data = {
        "jobName": job["jobName"],
        "jobGroup": job["jobGroup"]
    };

    fetch("/quartz/pauseJob?t=" + new Date().getTime(), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    })
    .then(response => response.json())
    .then(response => {

      if(!response.valid){
        showNotification("Error! : "+response.msg , "error");
        return;
      }

      let jobs = [];
      jobs.push(response.data);
      updateCacheAndJobsListing(jobs, true);

      showNotification(response.data.jobName+" paused successfully!" , "success");

    })
    .catch((error) => {
      console.error('Error:', error);
      showNotification("Error! : "+error , "error");
    });

};

var resumeJob = function(jobId){

    let job = K2cache.jobs[jobId];
    let data = {
        "jobName": job["jobName"],
        "jobGroup": job["jobGroup"]
    };

    fetch("/quartz/resumeJob?t=" + new Date().getTime(), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    })
    .then(response => response.json())
    .then(response => {

        if(!response.valid){
            showNotification("Error! : "+response.msg , "error");
            return;
        }

        let jobs = [];
        jobs.push(response.data);
        updateCacheAndJobsListing(jobs, true);

        showNotification(response.data.jobName+" resumed successfully!" , "success");

    })
    .catch((error) => {
      console.error('Error:', error);
      showNotification("Error! : "+error , "error");
    });

};

var updateJob = function(jobId){

    let job = K2cache.jobs[jobId];

    job["cronExpression"] = $("#update-cron-expression").val();
    job["deploymentId"] = $("#update-deployment-id").val();
    job["dependsOnBatchTypes"] = $("#update-depends-on-batchtypes").val();

    fetch("/quartz/saveOrUpdate?t=" + new Date().getTime(), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(job),
    })
    .then(response => response.json())
    .then(response => {

        if(!response.valid){
            //$("#editJobModal").modal('hide');
            showNotification("Error! : "+response.msg , "error");
            return;
        }

        let jobs = [];
        jobs.push(response.data);
        updateCacheAndJobsListing(jobs, true);

        $("#editJobModal").modal('hide');

        showNotification(response.data.jobName+" got updated!" , "success");

    })
    .catch((error) => {
      console.error('Error:', error);
      showNotification("Error! : "+error , "error");
    });
};


var createJob = function(){
    let job = {};

    job["jobName"] =$("#create-jobname").val();
    job["jobGroup"] =$("#create-jobgroup").val();
    job["batchType"] =$("#create-batchtype").val();
    job["jobStatus"] ="NORMAL";
    job["cronExpression"] = $("#create-cronexpression").val();
    job["deploymentId"] = $("#create-deploymentid").val();
    job["dependsOnBatchTypes"] = $("#create-dependsonbatchtypes").val();

    fetch("/quartz/saveOrUpdate?t=" + new Date().getTime(), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(job),
    })
    .then(response => response.json())
    .then(response => {

        if(!response.valid){
            //$("#editJobModal").modal('hide');
            showNotification("Error! : "+response.msg , "error");
            return;
        }

        let job = response.data;
        let jobMap = {};
        jobMap[job.jobId] = job;
        //updating cache
        K2cache.jobs[job.jobId] = job;


        loadJobsListing(jobMap, true);

        $("#createJobModal").modal('hide');

        showNotification(response.data.jobName+" got created!" , "success");

    })
    .catch((error) => {
      console.error('Error:', error);
      showNotification("Error! : "+error , "error");
    });


};

//event listeners for action buttons
$("#jobs-table").on('click' , ".runJob" , function(e) {
    $('[data-toggle="tooltip"]').tooltip('hide');
    var jobId = $(this).parent().parent().attr("id");
    runJob(jobId);
});

$("#jobs-table").on('click' , ".pauseJob" , function(e) {
    $('[data-toggle="tooltip"]').tooltip('hide');
    var jobId = $(this).parent().parent().attr("id");
    pauseJob(jobId);
});

$("#jobs-table").on('click' , ".resumeJob" , function(e) {
    $('[data-toggle="tooltip"]').tooltip('hide');
    var jobId = $(this).parent().parent().attr("id");
    resumeJob(jobId);
});

$("#jobs-table").on('click' , ".editJob" , function(e) {
    $('[data-toggle="tooltip"]').tooltip('hide');
    var jobId = $(this).parent().parent().attr("id");

    let job = K2cache.jobs[jobId];

    $("#update-job-title").text(job["jobName"]);
    $("#update-cron-expression").val(job["cronExpression"]);
    $("#update-deployment-id").val(job["deploymentId"]);
    $("#update-depends-on-batchtypes").val(job["dependsOnBatchTypes"]);
    $("#updateJob").data( "jobId", jobId );
    $("#editJobModal").modal('show');

});


$("#updateJob").click(function(){
    let jobId = $("#updateJob").data("jobId");
    updateJob(jobId);
});

$("#createJobInit").click(function(){
    $("#createJobModal").modal('show');
});

$("#createJob").click(function(){
    createJob();
});


/*############################################## NOTIFICATION #####################################*/

var showNotification = function(message , state){

    let sucessClass = "alert-success";
    let errorClass = "alert-error";
    let warningClass = "alert-warning";

    $(".notification .alert").removeClass("alert-success alert-error alert-warning");

    if(state == "success")
        $(".notification .alert").addClass("alert-success");
    else if(state == "error")
        $(".notification .alert").addClass("alert-danger");
    else
        $(".notification .alert").addClass("alert-warning");

    $("#notification-content").text(message);
    $(".notification").removeClass("d-none");

    setTimeout(function() {
        $('.notification').addClass('d-none');
    }, 1500);


};





/*############################################## OVERLAY ##########################################*/
var showOverlay = function(show){
    if(!!show)
        $('#benchmark-details').css("width", "100%");
    else
        $('#benchmark-details').css("width", "0%");
};

$('#load-benchmark').on('click', function() {
   showOverlay(true);
});

$('#hide-benchmark').on('click', function() {
  showOverlay(false);
});









/*###############################################    GROUPS DROPDOWN     ###################################################################*/


//Find the input search box
let search = document.getElementById("searchGroup");

//Find every item inside the dropdown
let items = document.getElementsByClassName("dropdown-item");
var buildDropDown = function buildDropDown(values) {
    let contents = []
    for (let name of values) {
        contents.push('<input type="button" class="dropdown-item" type="button" value="' + name + '"/>');
    }
    $('#menuItems').html(contents.join(""));

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

    let selectedGroup = $(this)[0].value;
    $('#dropdown_groups').text(selectedGroup);
    $("#dropdown_groups").dropdown('toggle');

    let filteredJobs = {};

    for (const [jobId, job] of Object.entries(K2cache.jobs)) {
        if(job["jobGroup"] == selectedGroup)
            filteredJobs[jobId] = job;
    }

    loadJobsListing(filteredJobs , false);
    $("#runAll").removeClass("disabled");

});




