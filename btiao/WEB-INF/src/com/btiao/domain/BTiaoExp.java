package com.btiao.domain;

public class BTiaoExp extends Exception {
	public BTiaoExp(int errNo, Throwable be) {
		this.errNo = errNo;
		this.be = be;
	}
	
	@Override
	public String toString() {
		return "errNo=" + errNo + "\n" + be;
	}
	
	public final int errNo;
	
	public final Throwable be;
}
