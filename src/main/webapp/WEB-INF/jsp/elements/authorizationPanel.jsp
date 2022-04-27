<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div id="auth">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/3.1.2/rollups/md5.js"></script>
    <script type="text/javascript">
        function encryptPass() {
            const pass = document.getElementById('passwd');
            let input = pass.value;
            pass.value = CryptoJS.MD5(input).toString();
        }

        function setAuthPanel() {
            const auth = document.getElementById('auth');
            let visible = auth.style.display.toString() == 'block';
            if(visible)
                auth.style.display = 'none';
            else
                auth.style.display = 'block';
        }
    </script>
    <form id="login" onsubmit="encryptPass()" action="${pageContext.request.contextPath}/api/login" method="get">
        <input name="login" type="text" id="log" class="input_logpass_text in_login" style="width:180px;padding-left:21px;background:url(/img/inp_login.svg) top 1px left 3px no-repeat,#fff;" value="Имя пользователя" onblur="if (this.value=='') {this.value = 'Имя пользователя';}" onfocus="if (this.value=='Имя пользователя') {this.value = '';}"> <br>
        <div style="vertical-align:middle;">
            <input name="passwd" type="password" id="passwd" class="input_logpass_text in_pass" style="vertical-align:middle;width:150px;padding-left:21px;background:url(/img/inp_passw.svg) top 1px left 3px no-repeat,#fff;" value="Пароль" onblur="if (this.value=='') {this.value = 'Пароль';}" onfocus="if (this.value=='Пароль') {this.value = '';}">
            <input type="submit" value="" class="input_logpass_text ent_b" style="vertical-align:middle;width:25px;height:26px;margin-left:2px;background-image:url(/img/ti_enter.svg);background-position:center center; background-repeat:no-repeat;">
        </div>
    </form>
    <p class="auth-info">Авторизация предусмотрена только для зарегистрированных пользователей.</p>
</div>
