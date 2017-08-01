<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script type="text/javascript">
	(function() {
		alert("登录超时，请重新登录！");
		logout();
	})();

	function logout() {
		top.location.href = "login_logout.action";
	}
</script>