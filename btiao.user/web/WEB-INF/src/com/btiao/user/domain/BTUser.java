package com.btiao.user.domain;

import com.btiao.model.BTMO;

public class BTUser extends BTMO {
	/**
	 * 以下划线开头的id为保留ID
	 */
	public String id;
	
	/**
	 * 用户昵称
	 */
	public String desc;
	
	/**
	 * 用户密码
	 */
	public String passwd;
}
