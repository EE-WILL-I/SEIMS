<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%boolean showCities = false;%>
<!-- start CONTENT -->
<div style="display: flex">
    <script src="${pageContext.request.contextPath}/JS/jquery-1.11.0.min.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/JS/map/raphael.min.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/JS/customScripts.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/JS/map/mapScripts.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/JS/map/mapInit.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/JS/map/mapPaths.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/JS/map/statePaths.js" type="text/javascript"></script>
    <div id="map" class="map" style="width: 600px; height: min-content; background-color: #555c64">
        <%if(showCities) {%><jsp:include page="../elements/cities.jsp"/><%}%>
    </div>
    <div id="city_map" class="map" style="width: 600px; height: 600px; background-color: #555c64; display: none;">
        <button id="btn_map_back" style="top:-100px" onclick="setMap('map', 'city_map');">Назад</button>
    </div>
    <div class="load_wrapper" id="map_data_wrapper">
        <div class="wrapped_div" id="map_data">
    </div>
    </div>
    <script type="text/javascript">
        setPaths('map', paths);
        setPaths('city_map', statePaths);
    </script>
</div>
<!-- end CONTENT -->
<!-- start SCRIPTS -->
<script src="${pageContext.request.contextPath}/JS/scripts.min.js"></script>
<!-- end SCRIPTS -->
