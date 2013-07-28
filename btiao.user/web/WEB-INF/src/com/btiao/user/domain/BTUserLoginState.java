package com.btiao.user.domain;

import com.btiao.model.BTMO;

public class BTUserLoginState extends BTMO {
	/**
	 * 所有用户的登录状态都有这个字段区分
	 */
	public String accessKey;
	
	/**
	 * 登录时间，相对于1970年1月1日0时0分0秒
	 */
	public long loginTime;
	
	/**
	 * 上次操作时间
	 * 连续N秒内没有操作则系统自动退出，N由UserMgr定义
	 */
	public long lastOpTime;
}
