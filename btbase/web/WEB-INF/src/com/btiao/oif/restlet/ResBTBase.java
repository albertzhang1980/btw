package com.btiao.oif.restlet;

import java.lang.reflect.Field;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.btiao.exp.BTiaoExp;
import com.btiao.exp.ErrCode;

public abstract class ResBTBase extends ServerResource {
	static private enum OP {
		Post,Put,Del,Get
	};
	
	static public void main(String[] args) throws Exception {
		JSONObject jo = new JSONObject();
		jo.put("a", 1);
		jo.put("a", 2);
		System.out.println(jo.get("a"));
	}
	
	public Logger logger = LogManager.getRootLogger();
	
	@Get(value="json")
	public final JsonRepresentation btiaoGet() {
		return commonPPD(null, OP.Get);
	}
	
	@Put(value="json:json")
	public JsonRepresentation btiaoPut(JsonRepresentation arg) {
		return commonPPD(arg, OP.Put);
	}
	
	@Delete(value="json:json")
	public JsonRepresentation btiaoDel(JsonRepresentation arg) {
		return commonPPD(arg, OP.Del);
	}
	
	@Post(value="json:json")
	public JsonRepresentation btiaoPost(JsonRepresentation arg) {
		return commonPPD(arg, OP.Post);
	}
	
	/**
	 * get process.
	 * @param form URI argument, not null
	 * @return the return object will be converted to the 'content'<br>
	 *         attribute of the return JSON object.
	 * @throws BTiaoExp if process failed, must throw the excepion with <br>
	 *         an error code assigned in the exception object.
	 */
	protected abstract Object get(Form form) throws BTiaoExp;
	
	protected abstract Object put(JSONObject jao) throws BTiaoExp;
	
	protected abstract Object post(JSONObject jao) throws BTiaoExp;
	
	protected abstract Object del(JSONObject jao) throws BTiaoExp;
	
	private JSONObject setContentOfJRO(JSONObject jro, Object contentRet) {
		if (contentRet == null) {
			return jro;
		}
		
		Field[] fields = contentRet.getClass().getFields();
		for (Field f : fields) {
			String name = f.getName();
			try {
				Object value = f.get(contentRet);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			
		}
		return jro;
	}
	
	private JsonRepresentation commonPPD(JsonRepresentation arg, OP op) {
		JSONObject jro = new JSONObject(); //return object
		setRstOfJRO(jro, ErrCode.SUCCESS);
		
		try {
			Object contentRet = null;
			
			try {
				if (op == OP.Get) {
					Form form = this.getReference().getQueryAsForm();
					contentRet = get(form);
				} else {
					JSONObject jao = arg.getJsonObject();
					if (op == OP.Del) {
						contentRet = del(jao);
					} else if (op == OP.Post) {
						contentRet = post(jao);
					} else if (op == OP.Put) {
						contentRet = put(jao);
					}
				}
			} catch (BTiaoExp e) {
				setRstOfJRO(jro, e.errNo);
			} catch (Throwable e) {
				e.printStackTrace();
				setRstOfJRO(jro, ErrCode.UNKOWN_ERR);
			}
			
			setContentOfJRO(jro, contentRet);
		} catch (Exception e) {
			e.printStackTrace();
			setRstOfJRO(jro, ErrCode.WRONG_PARAM);
		}
		
		return new JsonRepresentation(jro);
	}
	
	private JSONObject setRstOfJRO(JSONObject jro, int code) {
		try {
			jro.put("errCode", code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return jro;
	}
	
	

}
