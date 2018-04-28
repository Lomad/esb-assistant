<%@ page language="java" pageEncoding="UTF-8" %>
<%
  String path = request.getContextPath();
  String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
  String restUrl = basePath + "monitor/RESTLogging";
  String wsUrl = basePath + "services/MonitorDeal?wsdl";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <base href="<%=basePath%>">
</head>
<body>
<div style="margin-top: 50px; font-size: 20px; text-align: center; font-weight: bold">
  监控服务<span style="color: green; font-weight: bolder">【启动成功】</span>
</div>
<div style="margin-top: 10px; text-align: center; font-weight: bold">
  Restful地址：<a href="<%=restUrl%>" target="_blank"><%=restUrl%></a>
</div>
<div style="margin-top: 10px; text-align: center; font-weight: bold">
  Web服务地址：<a href="<%=wsUrl%>" target="_blank"><%=wsUrl%></a>
</div>
</body>

</html>
