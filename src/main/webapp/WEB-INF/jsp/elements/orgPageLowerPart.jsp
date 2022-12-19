<%@ page import="java.util.ArrayList" %>
<%@ page import="ru.seims.database.entitiy.StoredImage" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="ru.seims.utils.logging.Logger" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String appDataJson = (String) request.getAttribute("app_data");
    JSONArray appsData;
    try {
        appsData = (JSONArray) new JSONParser().parse(appDataJson);
    } catch (Exception e) {
        appsData = new JSONArray();
    }
    boolean hasApps = appsData.size() > 0;
    ArrayList<StoredImage> apps = new ArrayList<>(appsData.size());
    appsData.forEach((value) ->
    {
        try {
            String id = String.valueOf(((JSONObject)value).get("id"));
            apps.add(new StoredImage(id));
        } catch (Exception e) {
            Logger.log(this, e.getMessage(), 3);
            apps.add(StoredImage.loadDefaultImage());
        }
    });
%>
<p style="font-size: 20px; text-align: center; padding: 0px">Приложения</p>
<div id="org_app_wrapper">
    <div id="org_app_content" <%if(!hasApps) {%>style="display: block" <%}%>>
        <%if(!hasApps) {%>
        <p style="text-align: center">Приложений нет.</p>
        <%} else { for(StoredImage app : apps) {%>
        <div class="org_img_wrapper">
            <img class="org_img" src="data:image/jpg;base64,<%=app.getBase64Data()%>"/>
        </div>
        <%}}%>
    </div>
</div>
<hr/>
<p style="font-size: 20px; text-align: center; padding: 0px">Статистика</p>
<div style="display: flex;">
    <div class="chart-div">
        <canvas id="myChart" class="chart"></canvas>
    </div>
    <div class="chart-div">
        <canvas id="myChart1" class="chart"></canvas>
    </div>
</div>
