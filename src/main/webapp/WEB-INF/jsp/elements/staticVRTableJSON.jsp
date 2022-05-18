<%@ page import="ru.seims.database.entitiy.DataTable" %>
<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="${pageContext.request.contextPath}/JS/jquery-1.11.0.min.js" type="text/javascript"></script>
<script type="text/javascript">
    var tables = JSON.parse('[{"columnCount":0,"rowCount":0,"name":"Раздел 4. Распределение воспитанников по возрасту, человек","sysName":"doo_vr4","columnLabels":["id_buildokud","name","0 лет","1 год","2 года","3 года","4 года","5 лет","6 лет","7 лет и старше","Всего"],"dataRows":[{"7 лет и старше":"8","5 лет":"6","3 года":"4","name":"Всего воспитанников","6 лет":"7","Всего":"36","id_buildokud":"2","0 лет":"1","1 год":"2","2 года":"3","4 года":"5"},{"7 лет и старше":"16","5 лет":"14","3 года":"12","name":"Всего девочек","6 лет":"15","Всего":"100","id_buildokud":"2","0 лет":"9","1 год":"10","2 года":"11","4 года":"13"},{"7 лет и старше":"24","5 лет":"22","3 года":"20","name":"Всего дети-ивалиды","6 лет":"23","Всего":"164","id_buildokud":"2","0 лет":"17","1 год":"18","2 года":"19","4 года":"21"},{"7 лет и старше":"32","5 лет":"30","3 года":"28","name":"Всего девочек-инвалидов","6 лет":"31","Всего":"228","id_buildokud":"2","0 лет":"25","1 год":"26","2 года":"27","4 года":"29"}]}]');
    for(var i = 0; i < tables.length; i++) {
        var $div = $('#content');
        var $input = $('<input type="hidden" name="table_name" id="table_name_'+tables[i].get("sysName")+'">');
        var $tab = $('<table class="data_table"></table>');
        var $header = $('<h2 id="table_'+tables[i].get("sysName")+'>'+tables[i].get("name")+'</h2>');
        var $tr = $('#<tr></tr>');
        for(var lbl in tables[i].get("columnLabels")) {
            var $th = $('<th class="row_label" id="col_'+1+'_'+tables[i].get("SysName")+'><p>'+lbl+'</p></th>')
            $tr.append($th);
        }
        $tab.append($tr);
        $tab.append($header);
        $div.append($tab);
    }
</script>
<div id="content">

</div>
