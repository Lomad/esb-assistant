<#import "projectCommon/tree.ftl" as list/>
<#import "projectCommon/modal.ftl" as modal/>
<#import "integrationManage/simulationTestStep1.ftl" as step1/>
<#import "integrationManage/simulationTestStep2.ftl" as step2/>
<#import "integrationManage/simulationTestStep3.ftl" as step3/>
<#import "baseManage/svcUrlSave.ftl" as svcUrl/>
<@list.head>
    <#assign contextPath=request.contextPath>
<link href="${contextPath}/project-assets/plugins/jqueryStep/jquery.step.css" type="text/css" rel="stylesheet"/>
<style>
    .step-common {

    }
    .step-enable {
        display: block;
    }
    .step-diable {
        display: none;
    }

    .border-botton {
        border-bottom: 1px solid #f0f0f0;
    }

    .svcTestStepItemActive {
        background-color: #f5f5f5;
    }

    .badgeNormal {
        background-color: #e2e2e2;
    }
    .badgeSuccess {
        background-color: #64BD2E;
    }
    .badgeFailure {
        background-color: #CC0000;
    }

</style>
</@list.head>

<input id="loginUserid" type="hidden" value="${Session["userid"]}"/>

<div class="project-body-container">
    <div class="row">
        <div class="col-md-3">
            <div id="treeDiv" class="row common-box font-size-14" style="margin-top: 0px;min-height: 600px">
                <div class="col-md-12 col-sm-12">
                    <ul id="appTree" class="ztree"></ul>
                </div>
            </div>
        </div>
        <div class="col-md-9" style="height: inherit;min-height: 583px; padding-left: 0px;">
            <div class="row common-box" style="margin-top: 0px;min-height: 600px">
                <div class="row margin-left-right-15 margin-top-15">
                    <div id="stepSimulationTest" class="border-botton padding-bottom-15"></div>
                </div>
                <div id="step1" class="step-enable row margin-left-right-15 margin-top-15">
                <@step1.step></@step1.step>
                </div>
                <div id="step2" class="step-diable row margin-left-right-15 margin-top-15">
                <@step2.step></@step2.step>
                </div>
                <div id="step3" class="step-diable row margin-left-right-15 margin-top-15">
                <@step3.step></@step3.step>
                </div>
            <@svcUrl.svcUrlSave></@svcUrl.svcUrlSave>
            </div>
        </div>
    </div>
</div>

<@list.foot>
    <#assign contextPath=request.contextPath>
<script src="${contextPath}/project-assets/plugins/jqueryStep/jquery.step.js"></script>
<script src="${contextPath}/project-assets/js/baseManage/appZTree.js" type="text/javascript"></script>

<script src="${contextPath}/project-assets/js/integrationManage/simulationTestStep2.js" type="text/javascript"></script>
<script src="${contextPath}/project-assets/js/integrationManage/simulationTestStep3.js" type="text/javascript"></script>
<script src="${contextPath}/project-assets/js/integrationManage/simulationTest.js" type="text/javascript"></script>

</@list.foot>