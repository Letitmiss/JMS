package wusc.edu.demo.user.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import wusc.edu.demo.common.page.PageBean;
import wusc.edu.demo.user.base.BaseAction;
import wusc.edu.demo.user.biz.PmsUserBiz;
import wusc.edu.demo.user.entity.PmsUser;
import wusc.edu.demo.user.enums.UserStatusEnum;
import wusc.edu.demo.user.enums.UserTypeEnum;


/**
 * 
 * @描述: 用户信息管理 .
 * @作者: WuShuicheng .
 * @创建时间: 2015-1-25,下午9:36:46 .
 * @版本号: V1.0 .
 */
@Scope("prototype")
public class PmsUserAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5588682213578275029L;

	private static Log log = LogFactory.getLog(PmsUserAction.class);

	@Autowired
	private PmsUserBiz pmsUserBiz;

	// /////////////////////////////////// 用户管理   //////////////////////////////////////////
	/**
	 * 分页列出用户信息，并可按登录名获姓名进行查询.
	 * 
	 * @return listPmsUser or operateError .
	 * 
	 */
	public String listPmsUser() {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>(); // 业务条件查询参数
			paramMap.put("userNo", getString("userNo")); // 用户登录名（精确查询）
			paramMap.put("userName", getString("userName")); // 用户姓名（模糊查询）
			paramMap.put("status", getInteger("status")); // 状态

			PageBean pageBean = pmsUserBiz.listPage(getPageParam(), paramMap);
			this.pushData(pageBean);
			PmsUser pmsUser = getLoginedUser();// 获取当前登录用户对象
			this.putData("currUserNo", pmsUser.getUserNo());
			// 回显查询条件值
			this.pushData(paramMap);

			this.putData("UserStatusEnumList", UserStatusEnum.values());
			this.putData("UserStatusEnum", UserStatusEnum.toMap());
			this.putData("UserTypeEnumList", UserTypeEnum.values());
			this.putData("UserTypeEnum", UserTypeEnum.toMap());

			return "PmsUserList";
		} catch (Exception e) {
			log.error("== listPmsUser exception:", e);
			return operateError("获取数据失败");
		}
	}

	/**
	 * 查看用户详情.
	 * 
	 * @return .
	 */
	public String viewPmsUserUI() {
		try {
			Long userId = getLong("id");
			PmsUser pmsUser = pmsUserBiz.getById(userId);
			if (pmsUser == null) {
				return operateError("无法获取要查看的数据");
			}
			
			this.putData("UserStatusEnumList", UserStatusEnum.values());
			this.putData("UserStatusEnum", UserStatusEnum.toMap());
			this.putData("UserTypeEnumList", UserTypeEnum.values());
			this.putData("UserTypeEnum", UserTypeEnum.toMap());

			this.pushData(pmsUser);
			return "PmsUserView";
		} catch (Exception e) {
			log.error("== viewPmsUserUI exception:", e);
			return operateError("获取数据失败");
		}
	}

	/**
	 * 转到添加用户页面 .
	 * 
	 * @return addPmsUserUI or operateError .
	 */
	public String addPmsUserUI() {
		try {
			this.putData("UserStatusEnumList", UserStatusEnum.values());
			return "PmsUserAdd";
		} catch (Exception e) {
			log.error("== addPmsUserUI exception:", e);
			return operateError("获取角色列表数据失败");
		}
	}

	/**
	 * 保存一个用户
	 * 
	 */
	public String addPmsUser() {
		try {
			String userPwd = getString("userPwdss"); // 初始登录密码

			String userNo = getString("userNamess");

			PmsUser pmsUser = new PmsUser();
			pmsUser.setUserName(getString("userName")); // 姓名
			pmsUser.setUserNo(userNo); // 登录名
			pmsUser.setUserPwd(userPwd);
			pmsUser.setRemark(getString("desc")); // 描述
			pmsUser.setIsChangedPwd(false);
			pmsUser.setLastLoginTime(null);
			pmsUser.setMobileNo(getString("mobileNo")); // 手机号码
			pmsUser.setStatus(getInteger("status")); // 状态（100:'激活',101:'冻结'1）
			pmsUser.setUserType(String.valueOf(UserTypeEnum.ADMIN.getValue())); // 用户类型（1:超级管理员，2:普通管理员，3:用户主帐号，4:用户子帐号）

			// 表单数据校验
			String validateMsg = validatePmsUser(pmsUser);

			if (StringUtils.isNotBlank(validateMsg)) {
				return operateError(validateMsg); // 返回错误信息
			}

			// 校验用户登录名是否已存在
			PmsUser userNoCheck = pmsUserBiz.findUserByUserNo(userNo);
			if (userNoCheck != null) {
				return operateError("登录名【" + userNo + "】已存在");
			}

			pmsUser.setUserPwd(DigestUtils.sha1Hex(userPwd)); // 存存前对密码进行加密

			pmsUserBiz.create(pmsUser);

			return operateSuccess();
		} catch (Exception e) {
			log.error("== addPmsUser exception:", e);
			return operateError("保存用户信息失败");
		}
	}

	/**
	 * 校验Pms用户表单数据.
	 * 
	 * @param PmsUser
	 *            用户信息.
	 * @param roleUserStr
	 *            关联的角色ID串.
	 * @return
	 */
	private String validatePmsUser(PmsUser user) {
		String msg = ""; // 用于存放校验提示信息的变量
		msg += lengthValidate("真实姓名", user.getUserName(), true, 2, 15);
		msg += lengthValidate("登录名", user.getUserName(), true, 3, 50);
		
		// 登录密码
		String userPwd = user.getUserPwd();
		String userPwdMsg = lengthValidate("登录密码", userPwd, true, 6, 50);
		/*
		 * if (StringUtils.isBlank(loginPwdMsg) &&
		 * !ValidateUtils.isAlphanumeric(loginPwd)) { loginPwdMsg +=
		 * "登录密码应为字母或数字组成，"; }
		 */
		msg += userPwdMsg;

		// 手机号码
		String mobileNo = user.getMobileNo();
		String mobileNoMsg = lengthValidate("手机号", mobileNo, true, 0, 12);
		msg += mobileNoMsg;

		// 状态
		Integer status = user.getStatus();
		if (status == null) {
			msg += "请选择状态，";
		} else if (status.intValue() < 100 || status.intValue() > 101) {
			msg += "状态值不正确，";
		}

		msg += lengthValidate("描述", user.getRemark(), true, 3, 100);
		return msg;
	}

	/**
	 * 删除用户
	 * 
	 * @return
	 * */
	public String deleteUserStatus() {
		long id = getLong("id");
		pmsUserBiz.deleteUserById(id);
		return this.operateSuccess("操作成功");
	}

	/**
	 * 转到修改用户界面
	 * 
	 * @return PmsUserEdit or operateError .
	 */
	public String editPmsUserUI() {
		try {
			Long id = getLong("id");
			PmsUser pmsUser = pmsUserBiz.getById(id);
			if (pmsUser == null) {
				return operateError("无法获取要修改的数据");
			}

			// 普通用户没有修改超级管理员的权限
			if (UserTypeEnum.ADMIN.getValue().equals(this.getLoginedUser().getUserType()) && UserTypeEnum.ADMIN.getValue().equals(pmsUser.getUserType())) {
				return operateError("权限不足");
			}

			this.pushData(pmsUser);

			this.putData("UserStatusEnum", UserStatusEnum.toMap());
			this.putData("UserTypeEnum", UserTypeEnum.toMap());

			return "PmsUserEdit";
		} catch (Exception e) {
			log.error("== editPmsUserUI exception:", e);
			return operateError("获取修改数据失败");
		}
	}

	/**
	 * 保存修改后的用户信息
	 * 
	 * @return operateSuccess or operateError .
	 */
	public String editPmsUser() {
		try {
			Long id = getLong("id");

			PmsUser pmsUser = pmsUserBiz.getById(id);
			if (pmsUser == null) {
				return operateError("无法获取要修改的用户信息");
			}

			// 普通用户没有修改超级管理员的权限
			if ("0".equals(this.getLoginedUser().getUserType()) && "1".equals(pmsUser.getUserType())) {
				return operateError("权限不足");
			}

			pmsUser.setRemark(getString("remark"));
			pmsUser.setMobileNo(getString("mobileNo"));
			pmsUser.setUserName(getString("userName"));
			// 修改时不能修状态
			// pmsUser.setStatus(getInteger("status"));

			

			// 表单数据校验
			String validateMsg = validatePmsUser(pmsUser);
			if (StringUtils.isNotBlank(validateMsg)) {
				return operateError(validateMsg); // 返回错误信息
			}

			pmsUserBiz.update(pmsUser);
			return operateSuccess();
		} catch (Exception e) {
			log.error("== editPmsUser exception:", e);
			return operateError("更新用户信息失败");
		}
	}

	/**
	 * 根据ID冻结或激活用户.
	 * 
	 * @return operateSuccess or operateError .
	 */
	public String changeUserStatus() {
		try {
			Long userId = getLong("id");
			PmsUser user = pmsUserBiz.getById(userId);
			if (user == null) {
				return operateError("无法获取要操作的数据");
			}

			if (this.getLoginedUser().getId() == userId) {
				return operateError("不能修改自己账户的状态");
			}

			// 普通用户没有修改超级管理员的权限
			if ("0".equals(this.getLoginedUser().getUserType()) && "1".equals(user.getUserType())) {
				return operateError("你没有修改超级管理员的权限");
			}

			// 2014-01-02,由删除改为修改状态
			// pmsPermissionBiz.deleteUser(id);
			// 激活的变冻结，冻结的则变激活
			if (user.getStatus().intValue() == UserStatusEnum.ACTIVE.getValue()) {
				if ("1".equals(user.getUserType())) {
					return operateError("【" + user.getUserNo() + "】为超级管理员，不能冻结");
				}
				user.setStatus(UserStatusEnum.INACTIVE.getValue());
				pmsUserBiz.update(user);
			} else {
				user.setStatus(UserStatusEnum.ACTIVE.getValue());
				user.setPwdErrorCount(0);
				pmsUserBiz.update(user);
			}
			return operateSuccess();
		} catch (Exception e) {
			log.error("== changeUserStatus exception:", e);
			return operateError("删除用户失败:" + e.getMessage());
		}
	}

	/***
	 * 重置用户的密码（注意：不是修改当前登录用户自己的密码） .
	 * 
	 * @return
	 */
	public String resetUserPwdUI() {
		PmsUser user = pmsUserBiz.getById(getLong("id"));
		if (user == null) {
			return operateError("无法获取要重置的信息");
		}

		// 普通用户没有修改超级管理员的权限
		if ("0".equals(this.getLoginedUser().getUserType()) && "1".equals(user.getUserType())) {
			return operateError("你没有修改超级管理员的权限");
		}

		this.putData("userId", user.getId());
		this.pushData(user);

		return "PmsUserResetPwd";
	}

	/**
	 * 重置用户密码.
	 * 
	 * @return
	 */
	public String resetUserPwd() {
		try {
			Long userId = getLong("userId");
			PmsUser user = pmsUserBiz.getById(userId);
			if (user == null) {
				return operateError("无法获取要重置密码的用户信息");
			}

			// 普通用户没有修改超级管理员的权限
			if ("0".equals(this.getLoginedUser().getUserType()) && "1".equals(user.getUserType())) {
				return operateError("你没有修改超级管理员的权限");
			}

			String newPwd = getString("newPwd");
			String newPwd2 = getString("newPwd2");

			String validateMsg = validatePassword(newPwd, newPwd2);
			if (StringUtils.isNotBlank(validateMsg)) {
				return operateError(validateMsg); // 返回错误信息
			}

			pmsUserBiz.updateUserPwd(userId, DigestUtils.sha1Hex(newPwd), false);

			return operateSuccess();
		} catch (Exception e) {
			log.error("== resetUserPwd exception:", e);
			return operateError("密码重置出错:" + e.getMessage());
		}
	}

	/**
	 * 进入重置当前登录用户自己的密码的页面.
	 * 
	 * @return
	 */
	public String userChangeOwnPwdUI() {
		return "PmsUserChangeOwnPwd";
	}

	/**
	 * 重置当前登录用户自己的密码.
	 * 
	 * @return
	 */
	public String userChangeOwnPwd() {
		try {

			PmsUser user = this.getLoginedUser();
			if (user == null) {
				return operateError("无法从会话中获取用户信息");
			}

			// 判断旧密码是否正确
			String oldPwd = getString("oldPwd");
			if (StringUtils.isBlank(oldPwd)) {
				return operateError("请输入旧密码");
			}
			// 旧密码要判空，否则sha1Hex会出错
			if (!user.getUserPwd().equals(DigestUtils.sha1Hex(oldPwd))) {
				return operateError("旧密码不正确");
			}

			// 校验新密码
			String newPwd = getString("newPwd");
			if (oldPwd.equals(newPwd)) {
				return operateError("新密码不能与旧密码相同");
			}

			String newPwd2 = getString("newPwd2");
			String validateMsg = validatePassword(newPwd, newPwd2);
			if (StringUtils.isNotBlank(validateMsg)) {
				return operateError(validateMsg); // 返回错误信息
			}

			// 更新密码
			pmsUserBiz.updateUserPwd(user.getId(), DigestUtils.sha1Hex(newPwd), true);

			return operateSuccess("密码修改成功，请重新登录!");
		} catch (Exception e) {
			log.error("== userChangeOwnPwd exception:", e);
			return operateError("修改密码出错:" + e.getMessage());
		}
	}

	/**
	 * 当前登录的用户查看自己帐号的详细信息.
	 * 
	 * @return
	 */
	public String userViewOwnInfo() {
		try {

			PmsUser pmsUser = this.getLoginedUser();
			if (pmsUser == null) {
				return operateError("无法从会话中获取用户信息");
			}

			PmsUser user = pmsUserBiz.getById(pmsUser.getId());
			if (user == null) {
				return operateError("无法获取用户信息");
			}

			this.pushData(user);
			this.putData("UserStatusEnumList", UserStatusEnum.values());
			this.putData("UserStatusEnum", UserStatusEnum.toMap());
			this.putData("UserTypeEnumList", UserTypeEnum.values());
			this.putData("UserTypeEnum", UserTypeEnum.toMap());

			return "PmsUserViewOwnInfo";
		} catch (Exception e) {
			log.error("== editPmsUser exception:", e);
			return operateError("无法获取要修改的用户信息失败");
		}
	}


	/***
	 * 验证重置密码
	 * 
	 * @param newPwd
	 * @param newPwd2
	 * @return
	 */
	private String validatePassword(String newPwd, String newPwd2) {
		String msg = ""; // 用于存放校验提示信息的变量
		if (StringUtils.isBlank(newPwd)) {
			msg += "新密码不能为空，";
		} else if (newPwd.length() < 6) {
			msg += "新密码不能少于6位长度，";
		}

		if (!newPwd.equals(newPwd2)) {
			msg += "两次输入的密码不一致";
		}
		return msg;
	}
}
