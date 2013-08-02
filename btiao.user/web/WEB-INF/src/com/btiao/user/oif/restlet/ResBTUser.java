package com.btiao.user.oif.restlet;

import java.util.logging.Logger;

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
		String uId = (String)this.getContext().getAttributes().get("userId");
		if (!uId.equals(u.id)) {
			log.warning("userid isn't the same what is in the URI!");
			return null;
		}
		
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

	Logger log = Logger.getLogger("btiao");
}
