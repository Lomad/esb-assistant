<%--
  Created by IntelliJ IDEA.
  User: Lemod
  Date: 2016/11/26
  Time: 20:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>统一监控——埋点服务</title>
  </head>
  <body>
  <p style="color: green;font-size: 16px;">埋点服务已经启动</p>
  <p style="color: green;font-size: 16px;">服务地址：</p>
<%
    String path = request.getRequestURL().toString();
    String servletPath = request.getRequestURI();
    if(!"/".equals(servletPath)){
      int endIndex = path.indexOf(servletPath);
      if(endIndex>0){
        path = path.substring(0,endIndex+1);
      }
    }
    path = path+"services/MonitorDeal";
%>
  <p style="color: green;font-size: 16px;"><a href="<%=path%>?wsdl" style="color: green;"><%=path%></a></p>
  </body>
</html>
