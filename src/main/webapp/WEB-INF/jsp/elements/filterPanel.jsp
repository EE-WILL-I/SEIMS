<%@ page import="ru.seims.application.servlet.rest.FilterRestServlet" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div>
  <script src="${pageContext.request.contextPath}/js/jquery-1.11.0.min.js" type="text/javascript"></script>
  <script src="${pageContext.request.contextPath}/js/customScripts.js" type="text/javascript"></script>
  <script src="${pageContext.request.contextPath}/js/filter/filterInit.js" type="text/javascript"></script>
  <div style="display:flex">
    <div class="filter_div">
      <div id="tab_filter">
          <div style="display: flex">
           <p class="filter_p">Раздел:</p>
           <select id="doc_type" name="doc_type" onchange="initTabs(this.value)">
             <option value="2">OO-1</option>
             <option value="3">OO-2</option>
           </select>
         </div>
         <input class="org_filter_input" onchange="showTables(this.value)">
      </div>
      <div class="load_wrapper" id="filter_data_wrapper">
        <div class="wrapped_div filter_data" id="filter_table_data"></div>
      </div>
    </div>

    <div class="filter_div">
      <div id="row_filter">
      <p class="filter_p">Строка:</p>
      <input class="org_filter_input" onchange="showRows(this.value)">
      </div>
      <div class="load_wrapper" id="filter_row_wrapper">
        <div class="wrapped_div filter_data" id="filter_row_data"></div>
      </div>
    </div>

    <div class="filter_div">
      <div id="col_filter">
        <p class="filter_p">Графа:</p>
        <input class="org_filter_input" onchange="showCols(this.value)">
      </div>
      <div class="load_wrapper" id="filter_col_wrapper">
        <div class="wrapped_div filter_data" id="filter_col_data"></div>
      </div>
    </div>

    <div class="filter_div">
      <div>
        <div id="reg_filter">
          <p class="filter_p">Район:</p>
          <input class="org_filter_input" onchange="showRegions(this.value)">
        </div>
        <div class="load_wrapper" id="filter_reg_wrapper" style="height: 180px">
          <div class="wrapped_div filter_data" id="filter_reg_data" style="height: 180px"></div>
        </div>
      </div>
      <div>
        <div id="org_filter">
          <p class="filter_p">Организация:</p>
          <input class="org_filter_input" onchange="showOrgs(this.value)">
        </div>
        <div class="load_wrapper" id="filter_org_wrapper" style="height: 180px">
          <div class="wrapped_div filter_data" id="filter_org_data" style="height: 180px"></div>
        </div>
      </div>
    </div>
  </div>
  <div><p>Результат:</p></div>
  <script type="text/javascript">
    getTablesAPI = '<%=FilterRestServlet.getTablesAPI%>';
    getRowsAPI = '<%=FilterRestServlet.getRowsAPI%>';
    getColsAPI = '<%=FilterRestServlet.getColsAPI%>';
    getRegionsAPI = '<%=FilterRestServlet.getRegionsAPI%>';
    getOrgsAPI = '<%=FilterRestServlet.getOrgsAPI%>';
    initTabs(document.getElementById("doc_type").value);
  </script>
</div>
