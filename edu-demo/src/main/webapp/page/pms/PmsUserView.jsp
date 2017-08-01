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
	<form>
		<div class="pageFormContent" layoutH="60">
			
			<p style="width:99%">
				<label>用户姓名：</label>
				<s:textfield name="userName" readonly="true" size="30" />
			</p>
			<p style="width:99%">
				<label>用户登录名：</label>
				<s:textfield name="userNo" readonly="true" size="30" />
			</p>
			<p style="width:99%">
				<label>创建时间：</label>
				<fmt:formatDate value="${createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
			</p>
			<p style="width:99%">
				<label>手机号码：</label>
				<s:textfield name="mobileNo" readonly="true" size="30" />
			</p>
			<p style="width:99%">
				<label>状态：</label>
				<c:choose>
					<c:when test="${status eq 100 }">激活</c:when>
					<c:when test="${status eq 101 }">冻结</c:when>
					<c:otherwise>--</c:otherwise>
				</c:choose>
			</p>
			<p style="width:99%">
				<label>类型：</label>
				<c:forEach items="${UserTypeEnumList}" var="userTypeEnum">
					<c:if test="${userType ne null and userType eq userTypeEnum.value}">${userTypeEnum.desc}</c:if>
				</c:forEach>
			</p>
			<p style="width:99%;height:50px;">
				<label>描述：</label>
				<s:textarea name="remark" rows="3" cols="50" readonly="true"></s:textarea>
			</p>
			<p style="width:99%">
				<label>输错密码次数：</label>
				<s:textfield name="pwdErrorCount" readonly="true" size="30" />
			</p>
			<p style="width:99%">
				<label>最后输错密码时间：</label>
				<fmt:formatDate value="${pwdErrorTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
			</p>
			<p style="width:99%">
				<label>是否已更改过密码：</label>
				<c:if test="${isChangedPwd eq true}">是</c:if>
				<c:if test="${isChangedPwd eq false}">否</c:if>
			</p>
			
		</div>
		<div class="formBar">
			<ul>
				<li><div class="button"><div class="buttonContent"><button type="button" class="close">关闭</button></div></div></li>
			</ul>
		</div>
	</form>
</div>