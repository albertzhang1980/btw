package com.btiao.base.utils;

import java.util.logging.Logger;

public class Log {
	static Logger get() {
		return Logger.getLogger("btiao");
	}
	
	static Logger get(String name) {
		return Logger.getLogger(name);
	}
}
