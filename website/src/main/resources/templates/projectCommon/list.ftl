<#import "master/head.ftl" as commonHead/>
<#import "master/foot.ftl" as commonFoot/>

<#macro head>
    <#assign contextPath=request.contextPath>
    <@commonHead.head>
        <link type="text/css" href="${contextPath}/project-assets/plugins/dataTables/css/jquery.dataTables.min.css" rel="stylesheet"/>
        <link type="text/css" href="${contextPath}/project-assets/plugins/dataTables/css/dataTables.bootstrap.min.css" rel="stylesheet"/>
        <link type="text/css" href="${contextPath}/project-assets/plugins/layer/skin/default/layer.css" rel="stylesheet"/>
        <link type="text/css" href="${contextPath}/project-assets/css/common.css" rel="stylesheet" />

        <#nested >
    </@commonHead.head>
</#macro>

<#macro foot>
    <#assign contextPath=request.contextPath>
    <@commonFoot.foot>
        <script src="${contextPath}/project-assets/plugins/dataTables/js/jquery.dataTables.min.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/plugins/dataTables/js/dataTables.bootstrap.min.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/plugins/dataTables/js/render/ellipsis.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/plugins/layer/layer.js" type="text/javascript"></script>
        <#--<script src="${contextPath}/project-assets/plugins/other/jquery.cookie.min.js" type="text/javascript"></script>-->
        <script src="${contextPath}/project-assets/js-common/common-func.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/js-common/common-table.js" type="text/javascript"></script>
        <#--<script src="${contextPath}/project-assets/js-common/winning-table.js" type="text/javascript"></script>-->
        <#--<script src="${contextPath}/project-assets/js-common/common-method.js" type="text/javascript"></script>-->

        <#nested >
    </@commonFoot.foot>
</#macro>