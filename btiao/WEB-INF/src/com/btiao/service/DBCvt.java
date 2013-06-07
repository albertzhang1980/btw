package com.btiao.service;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import com.btiao.tg.TgData;

public class DBCvt {
	static public void main(String[] args) throws Exception {
		String DBDIR = ".."+File.separator+"TgDataGen"+File.separator+"genTest"+File.separator+"db";
		String tgDBId = "tgdb";
		Connection cn = DriverManager.getConnection("jdbc:hsqldb:file:"+DBDIR+File.separator+tgDBId+";ifexist=true", "SA", "");
		Statement s = cn.createStatement();
		String sql = "select * from tb_tg";

		ResultSet rst = s.executeQuery(sql);
		while (rst.next()) {
			TgData tg = new TgData();
			row2obj(rst, tg);
		}
		
		s.close();
		cn.close();
		
	}
	
	static public void row2obj(ResultSet rst, Object o) throws Exception {
		Class<?> oClass = o.getClass();
		Field[] fields = oClass.getDeclaredFields();
		for (Field f : fields) {
			String col = f.getName();
			String v = rst.getString(col);
			
			Class<?> clz = f.getType();
			if (clz.toString().equals("int")) {
				f.set(o, Integer.parseInt(v));
			} else if (clz.toString().equals("long")) {
				f.set(o, Long.parseLong(v));
			} else if (clz.toString().equals("class java.lang.String")){
				f.set(o, v);
			} else {
				throw new Exception("row2obj: unkown type!!! type="+clz);
			}
		}
	}
}
