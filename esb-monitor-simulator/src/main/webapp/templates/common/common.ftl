<#macro head>
<!DOCTYPE html>
<html lang="en" class="no-js">
<!--<![endif]-->
<!-- BEGIN HEAD -->
<head>
    <meta charset="utf-8" />
    <title>卫宁健康科技集团股份有限公司</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta content="width=device-width, initial-scale=1" name="viewport" />
    <meta content="" name="description" />
    <meta content="" name="author" />
    <#assign contextPath=request.contextPath>
    <link href="${contextPath}/static/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="${contextPath}/static/plugins/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css" />
    <link href="${contextPath}/static/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
    <link type="text/css" href="${contextPath}/static/plugins/layui/css/layui.css" rel="stylesheet">
    <#nested >
</head>
</#macro>

<#macro foot>
<body>
    <#assign contextPath=request.contextPath>
    <script src="${contextPath}/static/plugins/jquery/jquery-3.3.1.min.js" type="text/javascript"></script>
    <script src="${contextPath}/static/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="${contextPath}/static/plugins/bootstrap-datetimepicker/js/bootstrap-datetimepicker.min.js" type="text/javascript"></script>
    <script src="${contextPath}/static/plugins/bootstrap-datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN.js" type="text/javascript"></script>
    <script src="${contextPath}/static/plugins/layui/layui.js" type="text/javascript"></script>
    <script src="${contextPath}/static/js/common/common-function.js" type="text/javascript"></script>
    <#nested >
</body>
</#macro>