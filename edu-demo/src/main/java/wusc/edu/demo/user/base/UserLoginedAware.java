package wusc.edu.demo.user.base;

import wusc.edu.demo.user.entity.PmsUser;



public interface UserLoginedAware {

	/**
	 * 取得登录的用户
	 * 
	 * @return
	 */
	public PmsUser getLoginedUser();
}
