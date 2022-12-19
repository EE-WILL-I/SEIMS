<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%@ page import="ru.seims.application.servlet.jsp.OrganizationServlet" %>
<%@ page import="ru.seims.database.entitiy.StoredImage" %>
<%@ page import="ru.seims.utils.logging.Logger" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String orgId = (String) request.getAttribute("org_id");
  String orgDataJson = (String) request.getAttribute("org_data");
  if(orgDataJson == null || orgDataJson.isEmpty()) orgDataJson = "{}";
  String imgId;
  JSONObject webData;
  try {
    webData = (JSONObject) new JSONParser().parse(orgDataJson);
    imgId = String.valueOf(webData.get("id_img"));
  } catch (Exception e) {
    webData = new JSONObject();
    imgId = "0";
  }
  StoredImage img;
  try {
    img = new StoredImage(imgId);
  } catch (Exception e) {
    Logger.log(this, e.getMessage(), 3);
    img = new StoredImage();
  }
  String name = (String) webData.get("name");
  String description = (String) webData.get("description");
  if(name == null) name = "*ошибка базы данных*";
  if(description == null || description.isEmpty()) description = "Описание осутствует.";
  String orgURL = OrganizationServlet.getOrg.replace("{id}", orgId);
%>
<div id="org_header">
  <div id="org_img">
    <img style="width: 450px;" src="data:image/jpg;base64,<%=img.getBase64Data()%>"/>
  </div>
  <div style="padding-left: 20px;">
    <p id = "org_name"><%=name%></p>
    <br/>
    <hr/>
    <p id = "org_desc"><%=description%></p>
  </div>
</div>
<br/>
<hr/>
<p><strong>Район: </strong><%=webData.get("district")%></p>
<hr/>
<p><strong>Web-сайт: </strong><%=webData.get("web_site")%></p>
<p><strong>Контактные данные: </strong><%=webData.get("contact_data")%></p>
<hr/>
<script src="${pageContext.request.contextPath}/js/jquery-1.11.0.min.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/vrtable/VRTableScripts.js"></script>
<div style="display: flex; width: 100%; background: #367554;">
  <p class="vr_type_p">Отображаемый документ:</p>
  <a id="a_type_0" href="${pageContext.request.contextPath}<%=orgURL%>?doc=0" class="vr_type_btn">Все</a>
  <a id="a_type_2" href="${pageContext.request.contextPath}<%=orgURL%>?doc=2" class="vr_type_btn">oo1</a>
  <a id="a_type_3" href="${pageContext.request.contextPath}<%=orgURL%>?doc=3" class="vr_type_btn">oo2</a>
</div>
<hr/>