package com.btiao.service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.btiao.tg.TgData;

public class AllTgMgr {
	static public String DBDIR = "btdb";
	static public String tgDBId = "tgdb";
	
	static private AllTgMgr inst = null;
	static synchronized public AllTgMgr instance() {
		if (inst == null) {
			inst = new AllTgMgr();
		}
		return inst;
	}
	static {
		try { 
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 返回handle对应的团购序列的一页数据。
	 * @param handle 标识一个团购序列，为0时表示全部的团购序列。
	 * @param idx 要获取的数据的起始索引
	 * @param pgs 要获取的数据的个数
	 * @return
	 */
	public List<TgData> getTg(int handle, int idx, int num) {
		List<TgData> r = new ArrayList<TgData>();
		
		if (handle == 0) {
			int allTgNum = tgs.size();
			for (int i=idx; i<num && i<allTgNum; ++i) {
				r.add(tgs.get(i));
			}
			return r;
		}
		
		int end = idx + num;
		for (int i=idx; i<end; ++i) {
			r.add(tgs.get(i));
		}
		
		return r;
	}
	
	public boolean canUse() {
		return successInited;
	}
	
	private AllTgMgr() {
		initTg2Mem();
	}
	
	private void initTg2Mem() {
		try {
			Connection cn = DriverManager.getConnection("jdbc:hsqldb:file:"+DBDIR+File.separator+tgDBId+";ifexist=true", "SA", "");
			Statement s = cn.createStatement();
			String sql = "select * from tb_tg";
	
			ResultSet rst = s.executeQuery(sql);
			while (rst.next()) {
				TgData tg = new TgData();
				DBCvt.row2obj(rst, tg);
				tgs.add(tg);
			}
			
			s.close();
			cn.close();
		
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		successInited = true;
	}
	
	private List<TgData> tgs = new ArrayList<TgData>();
	private boolean successInited = false;
}
