package com.btiao.service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btiao.Result;
import com.btiao.domain.BTiaoExp;
import com.btiao.service.UserFilter.Item;
import com.btiao.service.gen.P2PInfoMgr;
import com.btiao.tg.TgData;

public class AllTgMgr {
	static public class FilterRst {
		public ResultSet rst;
	}
	static public void main(String[] args) throws Exception {
		UserFilter f = new UserFilter();
		f.addFilter("longitude=116276000;latitude=39957000");
		f.city = "beijing";
		f.uLatitude = 1;
		f.uLongitude = 2;
		
		String fstr = "{city=beijing;lon=2;lat=1{{longitude=116276000;latitude=39957000;},}}";
		assert (f.toString().equals(fstr));
		
		AllTgMgr.instance().genFilterDB(f);
		//System.out.println(container.size());
		
		try {
			assert(false);
			System.out.println("pls use -ea VM argument!");
		} catch (Throwable e) {
			System.out.println("success!");
		}
	}
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
	
	static public String DBDIR = "btdb";
	static public String DBFDIR = "btfdb";
	static public String tgDBId = "tg";
	
	static private volatile AllTgMgr inst = null;
	
	/**
	 * 返回handle对应的团购序列的一页数据。
	 * @param f 过滤条件
	 * @param idx 要获取的数据的起始索引
	 * @param pgs 要获取的数据的个数
	 * @return
	 */
	public List<TgData> getTg(UserFilter f, int idx, int num) 
	throws BTiaoExp {
		boolean dbExist = fltdb.get(f.toString());
		if (!dbExist) {
			genFilterDB(f);
			fltdb.put(f.toString(), true);
		}
		
		List<TgData> r = getTgBlockData(f, idx, num);
		return r;
	}
	
	private AllTgMgr() {
	}
	
	private List<TgData> getTgBlockData(UserFilter f, int idx, int num) {
		List<TgData> tgs = new ArrayList<TgData>();
		
		String dbId= genTgFilterDBOnlyFile(f, false);
		try {
			Connection cn = DriverManager.getConnection("jdbc:hsqldb:file:"+dbId, "SA", "");
			Statement s = cn.createStatement();
			String sql = "select * from tb_tg " + 
					genTgWhere(f) + genTgSort(f);
	
			ResultSet rst = s.executeQuery(sql);
			int begin = 0;
			while (rst.next()) {
				if (begin != idx) {
					++begin;
					continue;
				}
				
				if (num < 0) break;
				
				TgData tg = new TgData();
				DBCvt.row2obj(rst, tg);
				tgs.add(tg);
				
				--num;
			}
			
			s.close();
			cn.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return tgs;
	}
	
	private void genFilterDB(UserFilter f) throws BTiaoExp {
		String dbId = genTgFilterDBOnlyFile(f, true);

		try {
			Connection cn = DriverManager.getConnection("jdbc:hsqldb:file:"+DBFDIR+File.separator+dbId+";ifexist=true", "SA", "");
			
			genTgDynamicData(cn, f);
		} catch (Exception e) {
			throw new BTiaoExp(Result.TG_GEN_FDB_FAILED, e);
		}
	}
	
	private void genTgDynamicData(Connection cn, UserFilter f) 
	throws BTiaoExp {
		List<TgData> tgs = new ArrayList<TgData>();
		
		try {
			Statement s = cn.createStatement();
			String sql = "select * from tb_tg " + genTgWhere(f);

			ResultSet rst = s.executeQuery(sql);
			while (rst.next()) {
				TgData tg = new TgData();
				DBCvt.row2obj(rst, tg);
				tgs.add(tg);
			}
		} catch (Exception e) {
			throw new BTiaoExp(Result.TG_GEN_FDB_DYNAMIC_FAILED, e);
		}
		
		Connection cnp2p = null;
		try {
			cn.setAutoCommit(false);
			Statement us = cn.createStatement();
			
			cnp2p = DriverManager.getConnection("jdbc:hsqldb:file:"+P2PInfoMgr.getPosDBID(f.city), "SA", "");
			Statement s = cnp2p.createStatement();
			
			for (TgData tg : tgs) {
				String sql = "select * from tb_p2pinfo_dist " + genPosWhere(tg, f);
				ResultSet rst = s.executeQuery(sql);
				if (rst.next()) {
					tg.dist = rst.getInt("dist");
				}
				
				sql = "update tb_tg SET dist = " + tg.dist;
				us.execute(sql);
			}
			
			cn.commit();
			
			s.close();
		} catch (Exception e) {
			throw new BTiaoExp(Result.TG_GEN_FDB_DYNAMIC_FAILED, e);
		} finally {
			try {
				cnp2p.createStatement().execute("SHUTDOWN");
				if (cnp2p != null) cnp2p.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private String genPosWhere(TgData tg, UserFilter f) {
		StringBuilder sb = new StringBuilder();
		sb.append(" WHERE " +
				"p1x=" + tg.longitude + " AND " +
				"p1y=" + tg.latitude + " AND " +
				"p2x=" + f.uLongitude + " AND " +
				"p2y=" + f.uLatitude);
		return sb.toString();
	}
	
	private String genTgFilterDBOnlyFile(UserFilter f, boolean newDB) {
		String dbId = "tg."+f.toString();

		if (newDB) {
			String fromDBFileId = DBDIR + File.separator + getDBID(f.city);
			String toDBFileId = DBFDIR + File.separator + dbId;
			
			try {
				copyDBFile(fromDBFileId, toDBFileId);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return dbId;
	}
	
	private void copyDBFile(String fromDBFileId, String toDBFileId) 
	throws BTiaoExp {
		Process p1 = null;
		Process p2 = null;
		try {
			String cmd1 = "copy " + fromDBFileId + ".script" + 
					toDBFileId + ".script";
			p1 = Runtime.getRuntime().exec(cmd1);
			
			String cmd2 = "copy " + fromDBFileId + ".properties" + 
					toDBFileId + ".properties";
			p2 = Runtime.getRuntime().exec(cmd2);
		} catch (Exception e) {
			throw new BTiaoExp(Result.TG_GEN_FDB_FAILED, e);
		}
		
		do {
			try {
				int err = p1.waitFor();
				if (err != 0) {
					throw new BTiaoExp(Result.TG_GEN_FDB_FAILED, null);
				}
				err = p2.waitFor();
				if (err != 0) {
					throw new BTiaoExp(Result.TG_GEN_FDB_FAILED, null);
				}
				
				break;
			} catch (InterruptedException e) {
				continue;
			}
		} while (true);
	}
	
	private String getDBID(String city) {
		return tgDBId + "." + city;
	}

	private String genTgSort(UserFilter f) {
		return "ORDER BY dist"; //TODO 带实现其他排序
	}
	
	private String genTgWhere(UserFilter filter) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("WHERE ");
		for (int i=0; i<filter.fs.size(); ++i) {
			List<Item> f = filter.fs.get(i);
			
			if (i != 0) {
				sb.append(" OR ");
			}
			
			sb.append("(");
			for (int j=0; j<f.size(); ++j) {
				if (j == 0) {
					sb.append(" ");
				} else {
					sb.append(" AND ");
				}
				Item it = f.get(j);
				sb.append(it.n);
				sb.append(it.op);
				sb.append(it.v);
			}
			sb.append(")");
		}
		
		return sb.toString();
	}
	
	private Map<String,Boolean> fltdb = new HashMap<String,Boolean>();
}
