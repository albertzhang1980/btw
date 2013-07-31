package com.btiao.user.domain;

import com.btiao.base.model.BTMO;

public class BTUserLoginState extends BTMO {
	/**
	 * �����û��ĵ�¼״̬��������ֶ����
	 */
	public String accessKey;
	
	/**
	 * ��¼ʱ�䣬�����1970��1��1��0ʱ0��0��
	 */
	public long loginTime;
	
	/**
	 * �ϴβ���ʱ��
	 * ����N����û�в�����ϵͳ�Զ��˳���N��UserMgr����
	 */
	public long lastOpTime;
}
