<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.eclipse.jdt.internal.compiler.ast.CastExpression" %>
<%@ page import="ru.seims.utils.logging.Logger" %>
<%@ page import="ru.seims.application.servlet.jsp.OrganizationServlet" %>
<%
  String orgId = (String) request.getAttribute("org_id");
  JSONArray appsData = (JSONArray) request.getAttribute("app_data");
  int appNum = appsData.size();
  boolean hasApps = false;
  ArrayList<JSONObject> apps = new ArrayList<>(appsData.size());
  if (appNum > 0) {
    hasApps = true;
    for (int i = 0; i < appsData.size(); i++) {
      apps.add(((JSONObject)appsData.get(i)));
    }
  }
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<p style="font-size: 20px; text-align: center; padding: 0px">Приложения</p>
<div id="org_app_wrapper">
  <div id="org_app_content" <%if(!hasApps) {%>style="display: block" <%}%>>
    <%if(!hasApps) {%>
    <p style="text-align: center">Приложений нет.</p>
    <%} else { for(JSONObject app : apps) {
      String icon = "file.png";
      String format = app.get("format").toString();
      switch (format) {
        case ".xls":
        case ".xlsx": {
          icon = "excel.png";
          break;
        }
        case ".png":
        case ".jpeg":
        case ".jpg": {
          icon = "png.png";
          break;
        }
      }
      int bytes = 0;
      try {
        bytes = (Integer) app.get("size");
        bytes /= 1024;
      } catch (ClassCastException e) {
        Logger.log(this, e.getMessage(), 3);
      }%>
    <a class="org_img_wrapper" href="<%=OrganizationServlet.downloadApplication.replace("{id}", orgId).replace("{appId}", String.valueOf(app.get("id")))%>">
      <img class="org_img" src="${pageContext.request.contextPath}/img/<%=icon%>">
      <p class="app_p"><%=app.get("path")%><%=app.get("format")%></p>
      <span class="app_span"><%=bytes%> КБ.  <%=app.get("upd_date")%></span>
    </a>
    <%}}%>
  </div>
</div>