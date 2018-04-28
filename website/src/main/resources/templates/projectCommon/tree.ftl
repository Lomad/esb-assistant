<#import "master/head.ftl" as commonHead/>
<#import "master/foot.ftl" as commonFoot/>

<#macro head>
    <#assign contextPath=request.contextPath>
    <@commonHead.head>
        <#--<link href="${contextPath}/project-assets/plugins/jsTree/themes/default/style.min.css" type="text/css" rel="stylesheet">-->
        <link href="${contextPath}/project-assets/plugins/ztree/css/zTreeStyle/zTreeStyle.css" type="text/css" rel="stylesheet">
        <link href="${contextPath}/project-assets/plugins/layer/skin/default/layer.css" type="text/css" rel="stylesheet"/>
        <link href="${contextPath}/project-assets/plugins/mCustomScrollbar/jquery.mCustomScrollbar.min.css" type="text/css" rel="stylesheet"/>
        <link href="${contextPath}/project-assets/css/common.css" type="text/css" rel="stylesheet" />
        <link href="${contextPath}/project-assets/css/tree.css" type="text/css" rel="stylesheet" />

        <#nested >
    </@commonHead.head>
</#macro>

<#macro foot>
    <#assign contextPath=request.contextPath>
    <@commonFoot.foot>
        <#--<script src="${contextPath}/project-assets/plugins/jsTree/jstree.min.js"></script>-->
        <script src="${contextPath}/project-assets/plugins/ztree/jquery.ztree.all.min.js"></script>
        <script src="${contextPath}/project-assets/plugins/layer/layer.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/plugins/mCustomScrollbar/jquery.mCustomScrollbar.concat.min.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/js-common/common-func.js" type="text/javascript"></script>

        <#nested >
    </@commonFoot.foot>
</#macro>