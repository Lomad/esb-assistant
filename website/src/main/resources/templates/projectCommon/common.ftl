<#import "master/head.ftl" as commonHead/>
<#import "master/foot.ftl" as commonFoot/>

<#macro head>
    <#assign contextPath=request.contextPath>
    <@commonHead.head>
        <link type="text/css" href="${contextPath}/project-assets/plugins/layer/skin/default/layer.css" rel="stylesheet">
        <link type="text/css" href="${contextPath}/project-assets/css/common.css" rel="stylesheet" />
        <#nested >
    </@commonHead.head>
</#macro>

<#macro foot>
    <#assign contextPath=request.contextPath>
    <@commonFoot.foot>
        <script src="${contextPath}/project-assets/plugins/layer/layer.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/js-common/common-func.js" type="text/javascript"></script>
        <#nested >
    </@commonFoot.foot>
</#macro>