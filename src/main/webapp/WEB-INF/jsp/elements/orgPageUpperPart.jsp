<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%@ page import="ru.seims.application.servlet.jsp.OrganizationServlet" %>
<%@ page import="ru.seims.database.entitiy.StoredImage" %>
<%@ page import="ru.seims.utils.logging.Logger" %>
<%@ page import="ru.seims.utils.properties.PropertyReader" %>
<%@ page import="ru.seims.utils.properties.PropertyType" %>
<%@ page import="java.io.File" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String orgId = (String) request.getAttribute("org_id");
  StringBuilder imagePath = new StringBuilder("/img/").append(PropertyReader.getPropertyValue(PropertyType.SERVER, "app.emptyImageFileName"));
  String imageName = (String) request.getAttribute("image_filename");
  if(imageName != null && !imageName.isEmpty()) {
    imagePath = new StringBuilder(PropertyReader.getPropertyValue(PropertyType.SERVER, "app.uploadPath")).append("/")
            .append(orgId).append("/").append(imageName);
  }
  StoredImage imageData = null;
  JSONObject webData;
  try {
    File imageFile = new File(imagePath.toString());
    if (imageFile.exists()) {
      imageData = new StoredImage(imageFile);
    }
    webData = (JSONObject) request.getAttribute("org_data");
  } catch (Exception e) {
    webData = new JSONObject();
    imageData = null;
  }
  try {
  } catch (Exception e) {
    Logger.log(this, e.getMessage(), 3);
  }
  String name = (String) webData.get("name");
  String description = (String) webData.get("description");
  if(name == null) name = "*ошибка базы данных*";
  if(description == null || description.isEmpty()) description = "Описание осутствует.";
  String orgURL = OrganizationServlet.getOrg.replace("{id}", orgId);
%>
<div id="org_header">
  <div id="org_img">
    <%if(imageData != null) {%>
    <img id="org_preview" src="data:image/jpg;base64,<%=imageData.getBase64Data()%>"/>
    <%} else {%>
    <img id="org_preview" src="<%=imagePath%>"/>
    <%}%>
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
<p class="org_info"><strong>Район: </strong><%=webData.get("region")%></p>
<hr/>
<p class="org_info"><strong>Web-сайт: </strong><%=webData.get("web_site")%></p>
<p class="org_info"><strong>Контактные данные: </strong><%=webData.get("contact_data")%></p>
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