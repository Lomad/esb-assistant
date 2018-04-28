<#import "master/head.ftl" as commonHead/>
<#import "master/foot.ftl" as commonFoot/>

<#macro head
tree=true
select2=false
layui=false>
    <#assign contextPath=request.contextPath>
    <@commonHead.head>
    <!-- 集成ztree插件 -->
        <#if tree=true>
        <link href="${contextPath}/project-assets/plugins/ztree/css/zTreeStyle/zTreeStyle.css" type="text/css" rel="stylesheet">
        <link href="${contextPath}/project-assets/css/tree.css" type="text/css" rel="stylesheet" />
        </#if>
    <!-- 集成select2插件 -->
        <#if select2=true>
        <link href="${contextPath}/project-assets/plugins/select2/css/select2.min.css" type="text/css" rel="stylesheet">
        </#if>
    <!-- 集成layui插件【其中layer必须放在layui之前，否则会报错】 -->
        <link href="${contextPath}/project-assets/plugins/layer/skin/default/layer.css" type="text/css" rel="stylesheet"/>
        <#if layui=true>
        <link type="text/css" href="${contextPath}/project-assets/plugins/layui/css/layui.css" rel="stylesheet">
        </#if>
        <link href="${contextPath}/project-assets/plugins/dataTables/css/jquery.dataTables.min.css" type="text/css" rel="stylesheet"/>
        <link href="${contextPath}/project-assets/plugins/dataTables/css/dataTables.bootstrap.min.css" type="text/css" rel="stylesheet"/>
        <link href="${contextPath}/project-assets/plugins/mCustomScrollbar/jquery.mCustomScrollbar.min.css" type="text/css" rel="stylesheet"/>
        <link href="${contextPath}/project-assets/css/common.css" type="text/css" rel="stylesheet" />
        <link rel="stylesheet" type="text/css" href="${contextPath}/project-assets/plugins/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css" />

        <#nested >
    </@commonHead.head>
</#macro>

<#macro foot
tree=true
select2=false
layui=false
echart=false
laydate=false>
    <#assign contextPath=request.contextPath>
    <@commonFoot.foot>
    <!-- 集成ztree插件 -->
        <#if tree=true>
        <script src="${contextPath}/project-assets/plugins/ztree/jquery.ztree.all.min.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/js/baseManage/appZTree.js" type="text/javascript"></script>
        </#if>
    <!-- 集成select2插件 -->
        <#if select2=true>
        <script src="${contextPath}/project-assets/plugins/select2/js/select2.full.min.js" type="text/javascript"></script>
        </#if>
    <!-- 集成layui插件【其中layer必须放在layui之前，否则会报错】 -->
        <script src="${contextPath}/project-assets/plugins/layer/layer.js" type="text/javascript"></script>
        <#if laydate=true>
        <script src="${contextPath}/project-assets/plugins/laydate/laydate.js" type="text/javascript"></script>
        </#if>
        <#if layui=true>
        <script src="${contextPath}/project-assets/plugins/layui/layui.js" type="text/javascript"></script>
        </#if>
    <!-- 集成echart插件 -->
        <#if echart=true>
        <script src="${contextPath}/project-assets/plugins/echarts/echarts.min.js" type="text/javascript"></script>
        </#if>
        <script src="${contextPath}/project-assets/plugins/dataTables/js/jquery.dataTables.min.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/plugins/dataTables/js/render/ellipsis.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/plugins/dataTables/js/render/cutoff.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/plugins/dataTables/js/dataTables.bootstrap.min.js" type="text/javascript"></script>

        <script src="${contextPath}/project-assets/plugins/mCustomScrollbar/jquery.mCustomScrollbar.concat.min.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/plugins/other/jquery.cookie.min.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/js-common/common-func.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/js-common/common-table.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/js-common/common-method.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/js-common/date-utils.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/js-common/biz-stable.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/plugins/other/dateformat-min.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/plugins/bootstrap-datetimepicker/js/bootstrap-datetimepicker.min.js" type="text/javascript"></script>
        <script src="${contextPath}/project-assets/plugins/bootstrap-datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN.js" type="text/javascript"></script>

        <#nested >
    </@commonFoot.foot>
</#macro>