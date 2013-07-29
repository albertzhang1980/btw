package com.btiao.user.oif.restlet;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

public class ResBTUser extends ServerResource {
	@Put(value="json")
	public JsonRepresentation btiaoGut(JsonRepresentation au) {
		//TODO
		return null;
	}
	
	@Get(value="json")
	public JsonRepresentation btiaoGet() {
		//TODO
		return null;
	}
}
