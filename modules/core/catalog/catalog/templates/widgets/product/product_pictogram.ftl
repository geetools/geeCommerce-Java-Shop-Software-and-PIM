<#if (pictogram_url??)>
<div id="detailed_measurementsContainer" class="panel panel-default margin-bottom10">
    <div class="panel-heading collapsed" role="tab" id="detailedMeasurementsd" data-toggle="collapse"
         data-parent="#accordion" href="#detailed_measurements"
         aria-expanded="false" aria-controls="detailedMeasurements">
        <h2 class="panel-title">
            <a class="collapsed" data-toggle="collapse" data-parent="#accordion" href="#detailed_measurements"
               aria-expanded="false" aria-controls="detailedMeasurements">
            <@message text="Detailed Measurements" lang="en" text2="Maße im Detail" lang2="de" />
            </a>
        </h2>
    </div>
    <div id="detailed_measurements" class="panel-collapse collapse" role="tabpanel"
         aria-labelledby="detailedMeasurementsd">
        <div class="panel-body">
            <img class="img-responsive" src="${pictogram_url}">
        </div>
    </div>
</div>
</#if>