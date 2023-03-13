<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%@ page import="ru.seims.application.servlet.jsp.OrganizationServlet" %>
<%@ page import="ru.seims.database.entitiy.StoredImage" %>
<%@ page import="ru.seims.utils.logging.Logger" %>
<%@ page import="ru.seims.utils.properties.PropertyReader" %>
<%@ page import="ru.seims.utils.properties.PropertyType" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.IOException" %>
<%@ page import="ru.seims.application.servlet.jsp.DatabaseServlet" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String orgId = (String) request.getAttribute("org_id");
  StringBuilder defImagePath = new StringBuilder("/img/")
          .append(PropertyReader.getPropertyValue(PropertyType.SERVER, "app.emptyImageFileName"));
  StringBuilder imagePath = defImagePath;
  String imageName = (String) request.getAttribute("image_filename");
  boolean edit = request.getAttribute("edit") != null;
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
    } else {
      imagePath = defImagePath;
    }
    webData = (JSONObject) request.getAttribute("org_data");
  } catch (Exception e) {
    Logger.log(this, "Cannot load image for org: " + orgId);
    webData = new JSONObject();
    imageData = null;
    imagePath = defImagePath;
  }
  String name = (String) webData.get("name");
  String description = (String) webData.get("description");
  if(name == null) name = "*ошибка базы данных*";
  if(description == null || description.isEmpty()) description = "Описание осутствует.";
  String orgURL = OrganizationServlet.getOrg.replace("{id}", orgId);
  JSONArray regionArray = (JSONArray) request.getAttribute("regions_array");
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
    <%if(edit) {%>
    <div id="org_desc" class="interactive_cell" onclick="showCellInput(this)">
      <p><%=description%></p>
      <textarea onchange="updateFieldValue(this, this.value, 'upd_desc')" rows="10"
                onblur="onInputBlur(this)" style="display: none; width: 90%; height: 300px;">
        <%=description%>
      </textarea>
    </div>
  </div>
</div>
<br/>
<hr/>
<%if(regionArray != null) {%>
<p class="org_info"><strong>Район: </strong></p>
<div class="interactive_cell">
  <select id="new_region" name="new_region" onchange="setRegion(this.value)">
    <%for(Object region : regionArray) {%>
    <option value="<%=((JSONObject)region).get("id")%>"><%=((JSONObject)region).get("name")%></option>
    <%}%>
  </select>
</div>
<%} else {%>
<p class="org_info"><strong>Район: </strong><%=webData.get("region")%></p>
<%}%>
<hr/>
<p class="org_info"><strong>Web-сайт: </strong></p>
<div class="interactive_cell" onclick="showCellInput(this)">
  <p><%=webData.get("web_site")%></p>
  <input class="row_input" onchange="updateFieldValue(this, this.value, 'upd_web')"
         onblur="onInputBlur(this)" value="<%=webData.get("web_site")%>" style="display: none;"/>
</div>
<p class="org_info"><strong>Контактные данные: </strong></p>
<div class="interactive_cell" onclick="showCellInput(this)">
  <p><%=webData.get("contact_data")%></p>
  <input class="row_input" onchange="updateFieldValue(this, this.value, 'upd_cont')"
         onblur="onInputBlur(this)" value="<%=webData.get("contact_data")%>" style="display: none;"/>
</div>
</div>
<form id="info_form" action="<%=OrganizationServlet.updateOrgInfo.replace("{id}", orgId)%>" method="post">
  <input type="hidden" id="info_data" name="info_data">
  <button class="submit_btn" type="button" onclick="saveUpdatedFields()">Сохранить изменения</button>
</form>
<hr/>
<%} else {%>
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
<%}%>
<script src="${pageContext.request.contextPath}/js/jquery-1.11.0.min.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/js/vrtable/VRTableScripts.js"></script>
<div style="display: flex; width: 100%; background: #367554;">
  <p class="vr_type_p">Отображаемый документ:</p>
  <a id="a_type_0" href="${pageContext.request.contextPath}<%=orgURL%>?doc=0" class="vr_type_btn">Все</a>
  <a id="a_type_2" href="${pageContext.request.contextPath}<%=orgURL%>?doc=2" class="vr_type_btn">oo1</a>
  <a id="a_type_3" href="${pageContext.request.contextPath}<%=orgURL%>?doc=3" class="vr_type_btn">oo2</a>
</div>
<hr/>