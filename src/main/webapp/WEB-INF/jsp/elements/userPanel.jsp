<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div id="user">
    <script type="text/javascript">

        function setUserPanel(user) {
            const auth = document.getElementById('user');
            let visible = auth.style.display.toString() === 'block';
            if(visible)
                auth.style.display = 'none';
            else
                auth.style.display = 'block';
            document.getElementById("user_name").innerText = user;
        }
    </script>
    <div>
        <p id="user_name"></p>
        <a href="${pageContext.request.contextPath}/logout">Выйти</a>
    </div>
</div>
