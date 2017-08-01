package wusc.edu.demo.user.action;

import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import wusc.edu.demo.common.web.constant.SessionConstant;
import wusc.edu.demo.user.base.BaseAction;
import wusc.edu.demo.user.biz.PmsUserBiz;
import wusc.edu.demo.user.entity.PmsUser;
import wusc.edu.demo.user.enums.UserStatusEnum;
import wusc.edu.demo.user.enums.UserTypeEnum;

/**
 * 
 * @描述: 用户登录  .
 * @作者: WuShuicheng .
 * @创建时间: 2015-1-25,下午7:50:22 .
 * @版本号: V1.0 .
 */
@Scope("prototype")
public class PmsLoginAction extends BaseAction {
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(PmsLoginAction.class);
	@Autowired
	private PmsUserBiz pmsUserBiz;


	/**
	 * 进入登录页面.
	 * 
	 * @return
	 */
	public String loginPage() {
		return "login";
	}

	/**
	 * 登录验证Action
	 * 
	 * @return
	 * @throws Exception
	 */
	public String userLogin() {
		try {
			
			// 明文用户名
			String userNo = getString("userNo");
			if (StringUtils.isBlank(userNo)) {
				this.putData("userNoMsg", "用户名不能为空");
				return "input";
			}
			this.putData("userNo", userNo);
			PmsUser user = pmsUserBiz.findUserByUserNo(userNo);
			if (user == null) {
				log.warn("== no such user");
				this.putData("userNoMsg", "用户名或密码不正确");
				return "input";
			}

			if (user.getStatus().intValue() == UserStatusEnum.INACTIVE.getValue()) {
				log.warn("== 帐号【" + userNo + "】已被冻结");
				this.putData("userNoMsg", "该帐号已被冻结");
				return "input";
			}
			String pwd = getString("userPwd");
			if (StringUtils.isBlank(pwd)) {
				this.putData("userPwdMsg", "密码不能为空");
				return "input";
			}
			// 加密明文密码
			// 验证密码
			if (user.getUserPwd().equals(DigestUtils.sha1Hex(pwd))) {
				// 用户信息，包括登录信息和权限
				this.getSessionMap().put(SessionConstant.USER_SESSION_KEY, user);

				// 将主帐号ID放入Session 
				if (UserTypeEnum.MAIN_USER.getValue().equals(user.getUserType())) {
					this.getSessionMap().put(SessionConstant.MAIN_USER_ID_SESSION_KEY, user.getId());
				} else if (UserTypeEnum.SUB_USER.getValue().equals(user.getUserType())) {
					this.getSessionMap().put(SessionConstant.MAIN_USER_ID_SESSION_KEY, user.getMainUserId());
				} else {
					// 其它类型用户的主帐号ID默认为0
					this.getSessionMap().put(SessionConstant.MAIN_USER_ID_SESSION_KEY, 0L);
				}

				this.putData("userNo", userNo);
				this.putData("lastLoginTime", user.getLastLoginTime());

				try {
					// 更新登录数据
					user.setLastLoginTime(new Date());
					user.setPwdErrorCount(0); // 错误次数设为0
					pmsUserBiz.update(user);

				} catch (Exception e) {
					this.putData("errorMsg", e.getMessage());
					return "input";
				}

				// 判断用户是否重置了密码，如果重置，弹出强制修改密码页面； TODO
				this.putData("isChangePwd", user.getIsChangedPwd());

				return "main";

			} else {
				// 密码错误
				log.warn("== wrongPassword");
				// 错误次数加1
				Integer pwdErrorCount = user.getPwdErrorCount();
				if (pwdErrorCount == null) {
					pwdErrorCount = 0;
				}
				user.setPwdErrorCount(pwdErrorCount + 1);
				user.setPwdErrorTime(new Date()); // 设为当前时间
				String msg = "";
				if (user.getPwdErrorCount().intValue() >= SessionConstant.WEB_PWD_INPUT_ERROR_LIMIT) {
					// 超5次就冻结帐号
					user.setStatus(UserStatusEnum.INACTIVE.getValue());
					msg += "<br/>密码已连续输错【" + SessionConstant.WEB_PWD_INPUT_ERROR_LIMIT + "】次，帐号已被冻结";
				} else {
					msg += "<br/>密码错误，再输错【" + (SessionConstant.WEB_PWD_INPUT_ERROR_LIMIT - user.getPwdErrorCount().intValue()) + "】次将冻结帐号";
				}
				pmsUserBiz.update(user);
				this.putData("userPwdMsg", msg);
				return "input";
			}

		} catch (RuntimeException e) {
			log.error("login exception:", e);
			this.putData("errorMsg", "登录出错");
			return "input";
		} catch (Exception e) {
			log.error("login exception:", e);
			this.putData("errorMsg", "登录出错");
			return "input";
		}
	}

	/**
	 * 跳转到退出确认页面.
	 * 
	 * @return LogOutConfirm.
	 */
	public String logoutConfirm() {
		log.info("== logoutConfirm");
		return "logoutConfirm";
	}

	/**
	 * 退出登录
	 * 
	 * @return
	 * @throws Exception
	 */
	public String logout() throws Exception {
		this.getSessionMap().clear();
		log.info("== logout");
		return "logout";
	}

	/**
	 * 跳转到登录超时确认页面.
	 * 
	 * @return LogOutConfirm.
	 * @throws Exception
	 */
	public String timeoutConfirm() throws Exception {
		this.getSessionMap().clear();
		return "timeoutConfirm";
	}

}
