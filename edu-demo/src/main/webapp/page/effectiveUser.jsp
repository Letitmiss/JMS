<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<script type="text/javascript">
	(function() {
		alert("您的账号于${time}在其它地方登录，请及时修改密码！");
		logout();
	})();

	function logout() {
		top.location.href = "login_logout.action";
	}
</script>