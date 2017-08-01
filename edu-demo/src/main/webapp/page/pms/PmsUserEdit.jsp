<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/page/inc/taglib.jsp"%>
<style>
<!--
.pageFormContent fieldset label{
	width: 200px;
}
-->
</style>
<div class="pageContent">
	<form id="form" method="post" action="pms_editPmsUser.action" class="pageForm required-validate" onsubmit="return validateCallback(this, dialogAjaxDone);">
		<div class="pageFormContent" layoutH="60">
		    <input type="hidden" name="navTabId" value="listPmsUser">
			<input type="hidden" name="callbackType" value="closeCurrent">
			<input type="hidden" name="forwardUrl" value="">
			
			<s:hidden id="userId" name="id" />
			<p style="width:99%">
				<label>用户姓名：</label>
				<s:textfield name="userName" cssClass="required" minlength="2" maxlength="15" size="30" />
			</p>
			<p style="width:99%">
				<label>用户登录名：</label>
				<s:textfield name="userNo" cssClass="required" readonly="true" minlength="3" maxlength="30" size="30" />
			</p>
			<p style="width:99%">
				<label>手机号码：</label>
				<s:textfield name="mobileNo" cssClass="required mobile"  maxlength="12" size="30" />
			</p>
			<p style="width:99%">
				<label>状态：</label>
				<c:choose>
					<c:when test="${status eq UserStatusEnum.ACTIVE.value}">激活</c:when>
					<c:when test="${status eq UserStatusEnum.INACTIVE.value}">冻结</c:when>
					<c:otherwise>--</c:otherwise>
				</c:choose>
			</p>
			<p style="width:99%">
				<label>用户类型：</label>
				<c:choose>
					<c:when test="${type eq UserTypeEnum.USER.value }">普通用户</c:when>
					<c:when test="${type eq UserTypeEnum.ADMIN.value }">超级管理员</c:when>
					<c:otherwise>--</c:otherwise>
				</c:choose>
			</p>
			<p style="width:99%;height:50px;">
				<label>描述：</label>
				<s:textarea name="remark" maxlength="100" rows="3" cols="30"></s:textarea>
			</p>
			
		</div>
		<div class="formBar">
			<ul>
				<li><div class="buttonActive"><div class="buttonContent"><button type="button" onclick="submitForm()">保存</button></div></div></li>
				<li><div class="button"><div class="buttonContent"><button type="button" class="close">取消</button></div></div></li>
			</ul>
		</div>
	</form>
</div>
<script type="text/javascript">
	function submitForm() {
		$("#form").submit();
	}
	
</script>