<%@ page import="ru.seims.application.servlet.rest.MapRestServlet" %>
<%@ page import="ru.seims.application.servlet.jsp.OrganizationServlet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%boolean showCities = false;%>
<div style="display: flex">
    <script src="${pageContext.request.contextPath}/js/jquery-1.11.0.min.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/map/raphael.min.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/customScripts.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/map/mapScripts.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/map/mapInit.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/map/mapPaths.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/map/statePaths.js" type="text/javascript"></script>
    <div id="map" class="map" style="width: 600px; height: min-content; background-color: #555c64">
        <%if(showCities) {%><jsp:include page="../elements/cities.jsp"/><%}%>
    </div>
    <div id="city_map" class="map">
        <button id="btn_map_back" style="top:-100px" onclick="setMap('map', 'city_map');">Назад</button>
    </div>
    <div class="load_wrapper" id="map_data_wrapper">
        <div class="wrapped_div" id="map_data"></div>
    </div>
    <script type="text/javascript">
        getDistrictURL = '<%=MapRestServlet.getDistrictAPI%>';
        orgLink = '<%=OrganizationServlet.org%>';
        setPaths('map', paths);
        setPaths('city_map', statePaths);
    </script>
    <script src="${pageContext.request.contextPath}/js/scripts.min.js"></script>
</div>
