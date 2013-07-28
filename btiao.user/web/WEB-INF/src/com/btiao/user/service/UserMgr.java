package com.btiao.user.service;

import com.btiao.user.domain.BTUser;
import com.btiao.user.domain.BTUserLoginState;
import com.btiao.user.domain.CmdContext;

public class UserMgr {
	static public synchronized UserMgr instance() {
		if (inst== null) {
			inst = new UserMgr();
		}
		return inst;
	}
	
	static private UserMgr inst;
	static public long TIMEOUT_INTERVAL = 60*10;
	
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
	
	public boolean auth(CmdContext ctx) {
		BTUserLoginState state = getLoginState(ctx);
		if (state == null) {
			return false; //未登录或之前登录已超时
		}
		
		if (isTimeout(ctx, state)) {
			return false;
		}
		
		updateLastOpTime(ctx, state);
		
		return false;
	}
	
	private void updateLastOpTime(CmdContext ctx, BTUserLoginState state) {
		state.lastOpTime = System.currentTimeMillis();
		state.update();
	}
	private boolean isTimeout(CmdContext ctx, BTUserLoginState state) {
		long curTime = System.currentTimeMillis();
		if ((curTime-state.lastOpTime) >= TIMEOUT_INTERVAL) {
			disableLogin(ctx);
			return true;
		} else {
			return false;
		}
	}
	private boolean disableLogin(CmdContext ctx) {
		//TODO
		return false;
	}
	private BTUserLoginState getLoginState(CmdContext ctx) {
		//TODO
		return null;
	}
	private BTUser getUser(CmdContext ctx) {
		//TODO
		return null;
	}
}
