<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%String popupType = (String)pageContext.getRequest().getAttribute("show_popup");%>
<script>
    function hidePopup() {
        document.getElementById("popup").style.display = "none";
    }
</script>
<div id="popup" class="popup"
     style="background-color: navajowhite;
             width: 300px;
             height: auto;
             position: fixed;
             left: auto;
             right: 10px;
             border-radius: 5px;
             border: black;
             border-bottom: outset;
             display: <%if(popupType != null) {%>inline-flex<%} else {%>none<%}%>"
>
    <div style="background: none">
        <p class="popup_message" style="padding: 0 10px 0 10px;
        color: <%if(popupType != null && popupType.equals("error")) {%>red;<%}
        else if(popupType != null && popupType.equals("message")) {%>white;<%}%>"
        ><%=pageContext.getRequest().getAttribute("popup_message")%></p>
    </div>
    <div style="background: none">
        <button type="button" onclick="hidePopup()">x</button>
    </div>
</div>

