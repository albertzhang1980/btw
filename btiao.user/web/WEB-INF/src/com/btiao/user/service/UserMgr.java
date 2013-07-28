package com.btiao.user.service;

import com.btiao.user.domain.CmdContext;

public class UserMgr {
	static public synchronized UserMgr instance() {
		if (inst== null) {
			inst = new UserMgr();
		}
		return inst;
	}
	
	static private UserMgr inst;
	
	public boolean login(String id, String passwd) {
		//TODO
		return false;
	}
	
	public boolean addUser(CmdContext ctx) {
		//TODO
		return false;
	}
	
	public boolean delUser(CmdContext ctx) {
		//TODO
		return false;
	}
	
	public boolean mdfUser(CmdContext ctx) {
		//TODO
		return false;
	}
}
