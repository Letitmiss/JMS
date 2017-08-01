<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/page/inc/taglib.jsp"%>
<%
String path = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>样例管理后台</title>
<jsp:include page="inc/dwz.jsp" />
</head>
<body scroll="no">
	<div id="layout">
		<div id="header">
		    <!-- navMenu begin -->
			<div class="headerNav">
				<img alt="" src="<%=path %>/images/logo.png" height="50" />
				<ul class="nav">
					<li style="color:black;">欢迎您（${userNo }）！&nbsp;上次登录：<s:date name="lastLoginTime" format="yyyy年MM月dd日  HH时mm分ss秒" /> </li>
					<li><a href="javascript:" style="color:black;">${userNo}</a></li>
					<li><a href="pms_userViewOwnInfo.action" target="dialog" width="500" height="400" style="color:#fff;">帐号信息</a></li>
					<li><a href="pms_userChangeOwnPwdUI.action" target="dialog" width="550" height="300" style="color:#fff;">修改密码</a></li>
					<li>
						<a href="login_logoutConfirm.action" title="退出登录确认" target="dialog" width="300" height="200" style="color:#fff;">退出</a>
					</li>
				</ul>
				<ul class="themeList" id="themeList">
					<li theme="default"><div class="selected">蓝色</div></li>
					<li theme="green"><div>绿色</div></li>
					<li theme="purple"><div>紫色</div></li>
					<li theme="silver"><div>银色</div></li>
					<li theme="azure"><div>天蓝</div></li>
				</ul>
			</div>
			<!-- navMenu end -->
			
		</div>

		<div id="leftside">
			<div id="sidebar_s">
				<div class="collapse">
					<div class="toggleCollapse"><div></div></div>
				</div>
			</div>
			<div id="sidebar">
				<div class="toggleCollapse"><h2>主菜单</h2><div>收缩</div></div>

				<div class="accordion" fillSpace="sidebar">
					<div class="accordionHeader">
						<h2><span>Folder</span>用户管理</h2>
					</div>
					<div class="accordionContent">
						<ul class="tree treeFolder">
							<li><a href="pms_listPmsUser.action" target="navTab" rel="listPmsUser" fresh="true">用户信息管理</a></li>
						</ul>
					</div>
					
				</div>
			</div>
		</div>
		<div id="container">
			<div id="navTab" class="tabsPage">
				<div class="tabsPageHeader">
					<div class="tabsPageHeaderContent">
						<ul class="navTab-tab">
							<li tabid="main" class="main"><a href="javascript:;"><span><span class="home_icon">主页</span></span></a></li>
						</ul>
					</div>
					<div class="tabsLeft">left</div>
					<div class="tabsRight">right</div>
					<div class="tabsMore">more</div>
				</div>
				<ul class="tabsMoreList">
					<li><a href="javascript:;">主页</a></li>
				</ul>
				<div class="navTab-panel tabsPageContent layoutBox">
					<div class="page unitBox">
						<div class="accountInfo">
							<p><span>XX管理后台</span></p>
						</div>
						<div class="pageFormContent" layoutH="60" style="margin-right:230px">
						
						</div>
					</div>
					
				</div>
			</div>
		</div>

	</div>

	<div id="footer">Copyright &copy; 2015 WuShuicheng</div>

</body>
</html>