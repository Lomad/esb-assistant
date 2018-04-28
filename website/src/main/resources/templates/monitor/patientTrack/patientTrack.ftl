<#import "projectCommon/listAndTree.ftl" as list/>
<#import "monitor/common/detailedTimes.ftl" as detailedTimes/>

<@list.head tree=true>
    <#assign contextPath=request.contextPath>
    <@detailedTimes.head />
</@list.head>

<!-- BEGIN PAGE -->
<div class="project-body-container">
    <div class="row">
        <div class="col-md-3">
            <div id="treeWrapper" class="row common-box font-size-14 margin-top-0 padding-bottom-15">
                <div class="col-md-12 col-sm-12">
                    <ul id="appTree" class="ztree padding-0"></ul>
                </div>
            </div>
        </div>
        <div class="col-md-9 padding-left-0">
            <@detailedTimes.list id="wrapperId2" />
        </div>
    </div>
</div>
<!-- END PAGE -->

<@list.foot tree=true laydate=true>
    <#assign contextPath=request.contextPath>
    <@detailedTimes.foot />
<script src="${contextPath}/project-assets/js/monitor/patientTrack/patientTrack.js" type="text/javascript"></script>
</@list.foot>