<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.eclipse.jdt.internal.compiler.ast.CastExpression" %>
<%@ page import="ru.seims.utils.logging.Logger" %>
<%@ page import="ru.seims.application.servlet.jsp.OrganizationServlet" %>
<%@ page import="ru.seims.application.servlet.jsp.DatabaseServlet" %>
<%
  String orgId = (String) request.getAttribute("org_id");
  JSONArray appsData = (JSONArray) request.getAttribute("app_data");
  int appNum = appsData.size();
  boolean hasApps = false;
  boolean edit = request.getAttribute("edit") != null;
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
      String icon;
      String format = app.get("type_id").toString();
      switch (format) {
        case "1": {
          icon = "png.png";
          break;
        }
        case "2": {
          icon = "excel.png";
          break;
        }
        case "3" : {
         icon = "file.png";
         break;
        }
        default: {
          icon = "file.png";
        }
      }
      int bytes = 0;
      try {
        bytes = (Integer) app.get("size");
        bytes /= 1024;
      } catch (ClassCastException e) {
        Logger.log(this, e.getMessage(), 3);
      }%>
    <div style="display: flex;flex-direction: column;align-items: center;">
      <div>
        <a class="org_img_wrapper" href="<%=DatabaseServlet.downloadApplication.replace("{id}", orgId).replace("{appId}", String.valueOf(app.get("id")))%>">
          <img class="org_img" src="${pageContext.request.contextPath}/img/<%=icon%>">
          <p class="app_p"><%=app.get("path")%><%=app.get("format")%></p>
          <span class="app_span"><%=bytes%> КБ.  <%=app.get("upd_date")%></span>
        </a>
      </div>
      <%if(edit) {%>
      <div>
        <form action="<%=DatabaseServlet.deleteApplication.replace("{id}", orgId).replace("{appId}", app.get("id").toString())%>" method="post">
          <button class="apps_btn apps_delete" type="submit">Удалить</button>
        </form>
      </div>
    <%}%>
    </div>
    <%}}%>
  </div>
</div>