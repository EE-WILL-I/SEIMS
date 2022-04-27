<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page import="ru.seims.MailService.MessageTemplateBean" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    MessageTemplateBean[] templates = (MessageTemplateBean[]) pageContext.getRequest().getAttribute("templates");
    MessageTemplateBean appliedTemplate = (MessageTemplateBean) pageContext.getRequest().getAttribute("appliedTemplate");
    String subject = "", content = "", signature = "";
    if(appliedTemplate != null) {
        subject = appliedTemplate.getSubject();
        content = appliedTemplate.getContent();
        signature = appliedTemplate.getSignature();
    }
%>
<html>
<head>
    <title>Mail Service</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
    <link rel="stylesheet" href="http://ajax.aspnetcdn.com/ajax/jquery.ui/1.10.3/themes/sunny/jquery-ui.css">
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
    <script src="//ajax.aspnetcdn.com/ajax/jquery.ui/1.10.3/jquery-ui.min.js"></script>
</head>
<%
    String recipients = (String) pageContext.getRequest().getAttribute("recipients");
    if(recipients == null)
        recipients = "";
%>
<script type="text/javascript">
    function setTemplate(sub, con, sig) {
        document.getElementById('subject').value = sub;
        document.getElementById('message').value = con + '\t    ' + sig;
    }
</script>
<jsp:include page="../elements/header.jsp"/>
<jsp:include page="../elements/popup.jsp"/>
<div class="content_holder">
    <jsp:include page="../elements/mailServiceTab.jsp"/>
    <h2><%=LocalizationManager.getString("mailService.header")%></h2>
    <div class="q_div" style="justify-content: start">
        <nav class="q_tab">
            <%if(templates != null)
                for(MessageTemplateBean template : templates) {%>
            <button class="mail_temp" onclick="setTemplate('<%=template.getSubject()%>', '<%=template.getContent()%>', '<%=template.getSignature()%>')"><%=template.getSubject()%></button>
            <%}%>
        </nav>
        <form id="mail_form" action="${pageContext.request.contextPath}/mail/send" method="post">
            <label for="recipients"><%=LocalizationManager.getString("mailService.recipients")%></label>
            <input id="recipients" name="recipients" type="text" value="<%=recipients%>">
            <br/>
            <label for="subject"><%=LocalizationManager.getString("mailService.subject")%></label>
            <input id="subject" name="subject" type="text" value="<%=subject%>">
            <br/>
            <label for="message"><%=LocalizationManager.getString("mailService.content")%></label>
            <input id="message" name="message" type="text" value="<%=content + '\n' + signature%>">
            <br/>
            <button type="submit" value="Send"><%=LocalizationManager.getString("mailService.send")%></button>
        </form>
    </div>

    <!--Update account credentials-->
    <div id="dialog_acc" title="update credentials" style="display:none;">
            <form id="update_acc_form" action="${pageContext.request.contextPath}/mail/updateAccount" method="post">
                <fieldset id="field_set">
                    <label for="new_login">Login:</label>
                    <br/>
                    <input type="text" name="new_login" id="new_login" class="text">

                    <label for="new_pass">Password:</label>
                    <br/>
                    <input type="password" name="new_pass" id="new_pass" class="text">
                    <button type="submit">Update</button>
                </fieldset>
            </form>
        </div>
        <button onclick='showUpdateAccDialog()'><%=LocalizationManager.getString("mailService.update_acc")%></button>
</div>
<jsp:include page="../elements/footer.jsp" />
</body>
<script type="text/javascript">
    function setNewUser(val) {
        document.getElementById("new_user").value = val;
    }
    function setNewPass(val) {
        document.getElementById("new_pass").value = val;
    }
    function showUpdateAccDialog() {
        $("#dialog_acc").dialog();
    }
</script>
</html>
