<#import "projectCommon/listAndTree.ftl" as list/>
<#import "monitor/common/detailedTimes.ftl" as detailedTimes/>
<#import "projectCommon/modal.ftl" as modal/>

<@list.head tree=false>
    <#assign contextPath=request.contextPath>
    <@detailedTimes.head />
</@list.head>

<!-- BEGIN PAGE -->
<div class="project-body-container">
    <div class="row">
        <div class="col-md-12">
            <@detailedTimes.list id="wrapperId2" />
        </div>
    </div>
</div>
<!-- END PAGE -->

<@list.foot tree=false laydate=true>
    <#assign contextPath=request.contextPath>
    <@detailedTimes.foot />
<script src="${contextPath}/project-assets/js/monitor/detailedRealtime.js" type="text/javascript"></script>
</@list.foot>