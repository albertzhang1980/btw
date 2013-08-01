package com.btiao.user.oif.restlet;

import org.json.JSONObject;
import org.restlet.data.Form;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.oif.restlet.JsonCvtInfo;
import com.btiao.base.oif.restlet.ResBTBase;
import com.btiao.user.domain.BTUser;

public class ResBTUser extends ResBTBase {

	@Override
	protected Object get(Form form) throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.user.domain.BTUser")
	protected Object put(Object obj) throws BTiaoExp {
		BTUser u = (BTUser)obj;
		return null;
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.user.domain.BTUser")
	protected Object post(Object obj) throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.user.domain.BTUser")
	protected Object del(Object obj) throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

}
