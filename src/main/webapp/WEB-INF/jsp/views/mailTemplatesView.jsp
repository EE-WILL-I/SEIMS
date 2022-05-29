<%@ page import="ru.seims.mailservice.MessageTemplateBean" %>
<%@ page import="ru.seims.localization.LocalizationManager" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    MessageTemplateBean[] templates = (MessageTemplateBean[]) pageContext.getRequest().getAttribute("templates");
    String tempId = (String) pageContext.getRequest().getAttribute("currentTemplate");
    MessageTemplateBean selectedTemplate = templates[0];
    for(MessageTemplateBean templateBean : templates)
        if(String.valueOf(templateBean.getId()).equals(tempId))
            selectedTemplate = templateBean;
%>
<html>
<head>
    <title>Templates</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
</head>
<jsp:include page="../elements/header.jsp"/>
<jsp:include page="../elements/popup.jsp"/>
<body>
    <div class="content_holder">
        <jsp:include page="../elements/mailServiceTab.jsp"/>
        <div class="q_div" style="justify-content: start">
            <nav class="q_tab">
                <%for(MessageTemplateBean template : templates) {%>
                <a href="${pageContext.request.contextPath}/mail/templates/get/<%=template.getId()%>"><%=template.getSubject()%></a>
                <%}%>
            </nav>
            <form id="mail_form" action="${pageContext.request.contextPath}/mail/templates/save/<%=selectedTemplate.getId()%>" method="post">
                <label for="subject"><%=LocalizationManager.getString("mailService.subject")%></label>
                <input id="subject" name="subject" type="text" value="<%=selectedTemplate.getSubject()%>">
                <br/>
                <label for="message"><%=LocalizationManager.getString("mailService.content")%></label>
                <input id="message" name="message" type="text" value="<%=selectedTemplate.getContent()%>">
                <br/>
                <label for="signature"><%=LocalizationManager.getString("mailService.templates.signature")%></label>
                <input id="signature" name="signature" type="text" value="<%=selectedTemplate.getSignature()%>">
                <br/>
                <button type="submit" value="Send"><%=LocalizationManager.getString("mailService.templates.save")%></button>
            </form>
        </div>
        <form action="${pageContext.request.contextPath}/mail/templates/new" method="post">
            <button type="submit">New</button>
        </form>
        <form action="${pageContext.request.contextPath}/mail/templates/delete/<%=selectedTemplate.getId()%>" method="post">
            <button type="submit">Delete</button>
        </form>
    </div>
</body>
<jsp:include page="../elements/footer.jsp" />
</html>
