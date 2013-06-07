package com.btiao.tg.datagen;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.btiao.tg.TgData;
import com.btiao.tg.TgShop;

public abstract class Gen {
	static public String DBDIR = "e:\\tgdb";
	static public String ORIGIN_TG_DIR = "originTg";
	
	static public String tgDBId = "tgdb";
	static public String tgSearchDBId = "tgschdb";
	static public String shopDBId = "shopdb";
	static public String shopSearchDBId = "shopschdb";
	
	static protected Map<String,String> dbTgs = new HashMap<String,String>();
	static protected Map<String,String> dbShops = new HashMap<String,String>();
	
	static private Connection tgCon;
	static private Connection tgShopCon;
	static private Connection tgSearchCon;
	
	static public void genAll() throws Exception {
		long t1 = System.currentTimeMillis();
		init();
		long t11 = System.currentTimeMillis();
		WoWoGen wGen = new WoWoGen("wowo.xml");
		wGen.preGen();
		wGen.toDB();
		wGen.postGen();
		long t2 = System.currentTimeMillis();
		shutdownDB(tgCon);
		shutdownDB(tgShopCon);
		shutdownDB(tgSearchCon);
		long t3 = System.currentTimeMillis();
		
		System.out.println("t1~t11: "+(t11-t1)/60000+"min");
		System.out.println("t11~t2: "+(t2-t11)/60000+"min");
		System.out.println("t2~t3: "+(t3-t2)/1000+"s");
	}
	
	static public void main(String[] args) throws Exception {
//		DBDIR = "genTest"+File.separator + "db";
//		ORIGIN_TG_DIR = "genTest"+File.separator;
		
		long t1 = System.currentTimeMillis();
		genAll();
		long t2 = System.currentTimeMillis();
		System.out.println("time: " + (t2-t1) + "ms");
		
		try {
			assert(false);
			System.out.println("pls use -ea VM argument!");
		} catch (Throwable e) {
			System.out.println("success!");
		}
	}
	
	static void shutdownDB(Connection cn) throws Exception {
		Statement s = cn.createStatement();
		s.execute("SHUTDOWN");
		s.close();
		cn.close();
	}
	
	static void clearAllDB() {
		File dir = new File(DBDIR);
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		
		for (File file : files) {
			file.delete();
		}
	}
	
	/**
	 * 初始化Gen的静态数据。
	 */
	static protected void init() throws Exception {
		clearAllDB();
		
		tgSearchCon = DriverManager.getConnection("jdbc:hsqldb:file:"+DBDIR+File.separator+tgSearchDBId, "SA", "");
		tgCon = DriverManager.getConnection("jdbc:hsqldb:file:"+DBDIR+File.separator+tgDBId, "SA", "");
		tgShopCon = DriverManager.getConnection("jdbc:hsqldb:file:"+DBDIR+File.separator+shopDBId, "SA", "");
		
		try {
			Statement s = tgSearchCon.createStatement();
			String sql = "CREATE TABLE tb_tg(" +
					"type INTEGER NOT NULL," +
					"url VARCHAR(256) NOT NULL," +
					"endTime BIGINT NOT NULL," +
					"useEndTime BIGINT NOT NULL," +
					"PRIMARY KEY(url)" +
					")";
			s.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		try {
			Statement s = tgCon.createStatement();
			String sql = "CREATE TABLE tb_tg(" +
					"type INTEGER NOT NULL," +
					"url VARCHAR(256) NOT NULL," +
					"title VARCHAR(512) NOT NULL," +
					"desc VARCHAR(512) NOT NULL," +
					"imageUrl VARCHAR(256) NOT NULL," +
					"startTime BIGINT NOT NULL," +
					"endTime BIGINT NOT NULL," +
					"useEndTime BIGINT NOT NULL," +
					"value INTEGER NOT NULL," +
					"price INTEGER NOT NULL," +
					"boughtNum INTEGER NOT NULL," +
					"PRIMARY KEY(url)" +
					")";
			s.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		try {
			Statement s = tgShopCon.createStatement();
			String sql = "CREATE TABLE tb_shop(" +
					"longitude BIGINT NOT NULL," +
					"latitude BIGINT NOT NULL," +
					"name VARCHAR(128) NOT NULL," +
					"addr VARCHAR(256) NOT NULL," +
					"tel VARCHAR(128) NOT NULL," +
					"PRIMARY KEY(longitude,latitude)" +
					")";
			s.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	static protected long tmStr2Long(String tm) {
		try {
			SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = fm.parse(tm);
			
			return date.getTime();
		} catch (Exception e) {
			return -1;
		}
	}
	
	/**
	 * 价格字符串（小数），转换成整数，放大100倍。
	 * @param str
	 * @return
	 */
	static protected Integer priceStr2Int(String str) {
		try {
			float r = Float.parseFloat(str);
			
			return (int)(r*100);
		} catch (Exception e) {
			return null;
		}
	}
	
	static protected Integer str2Int(String str) {
		try {
			int r = Integer.parseInt(str);
			
			return r;
		} catch (Exception e) {
			return null;
		}
	}
	
	static protected long doubleLatLon2Long(String d) {
		try {
			double dlong = Double.parseDouble(d);
			return (long)(dlong*1000*1000);
		} catch (Exception e) {
			return (0xffffffffffffffffL);
		}
	}
	
	public void toDB() throws Exception {
		while (genTg()) {
			if (alreadyAddedTg()) {
				continue;
			}
			
			insertTg();
			insertShop();
		}
	}
	
	protected final Document doc;
	protected TgData tgTmp;
	protected List<TgShop> shopsTmp = new ArrayList<TgShop>();
	
	protected Gen(String originTgXmlFn) throws Exception {
		SAXReader reader = new SAXReader();
		doc = reader.read(new File(ORIGIN_TG_DIR+File.separator+originTgXmlFn));
	}
	
	protected void insertTg() throws Exception {
		if (dbTgs.containsKey(tgTmp.url)) {
			return;
		}
		
		String sql = null;
		try {
			sql = genInsertTgSql(tgTmp);
			Statement s = tgCon.createStatement();
			s.execute(sql);
			s.execute("CHECKPOINT");
		} catch (Exception e) {
			System.err.println("sql="+sql);
			e.printStackTrace();
		}
		
		dbTgs.put(tgTmp.url, "");
	}
	
	private String genInsertTgSql(TgData tg) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO tb_tg ");
		sb.append("(type,url,title,desc,imageUrl,startTime,endTime,useEndTime,value,price,boughtNum) VALUES(");
		sb.append(tg.type);sb.append(",");
		sb.append(normalTxt2Sqltxt(tg.url));sb.append(",");
		sb.append(normalTxt2Sqltxt(tg.title));sb.append(",");
		if (tg.desc.length() > 120) {
			tg.desc = tg.desc.substring(0, 120);
		}
		sb.append(normalTxt2Sqltxt(tg.desc));sb.append(",");
		sb.append(normalTxt2Sqltxt(tg.imageUrl));sb.append(",");
		sb.append(tg.startTime);sb.append(",");
		sb.append(tg.endTime);sb.append(",");
		sb.append(tg.useEndTime);sb.append(",");
		sb.append(tg.value);sb.append(",");
		sb.append(tg.price);sb.append(",");
		sb.append(tg.boughtNum);
		sb.append(")");
		
		return sb.toString();
	}
	
	protected void insertShop() throws Exception {
		for (TgShop shop : shopsTmp) {
			String id = shop.longitude + "," +shop.latitude;
			if (dbShops.containsKey(id)) {
				String shopName = dbShops.get(id);
				if (!shopName.equals(shop.name)) {
					//TODO 记录便于查看
				}
				continue;
			}
			
			String sql = genInsertShopSql(shop);
			Statement s = tgShopCon.createStatement();
			s.execute(sql);
			
			dbShops.put(id, shop.name);
		}
	}
	
	private String genInsertShopSql(TgShop shop) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO tb_shop ");
		sb.append("(longitude,latitude,addr,name,tel) VALUES(");
		sb.append(shop.longitude);sb.append(",");
		sb.append(shop.latitude);sb.append(",");
		sb.append(normalTxt2Sqltxt(shop.addr));sb.append(",");
		sb.append(normalTxt2Sqltxt(shop.name));sb.append(",");
		sb.append(normalTxt2Sqltxt(shop.tel));
		sb.append(")");
		
		return sb.toString();
	}
	
	private String normalTxt2Sqltxt(String str) {
		//str = "'\\%_/<>";
		return "'" + 
			str.replace("'", "''") +
			"\'";
	}

	/**
	 * 每次调用产生一条团购信息，填写到tgTmp和shopsTmp中。
	 * @return true表示是否取到了下一条团购数据。
	 */
	protected abstract boolean genTg();
	
	protected abstract void preGen();
	protected abstract void postGen() throws Exception ;
	
	private boolean alreadyAddedTg() {
		if (dbTgs.containsKey(tgTmp.url)) {
			return true;
		}
		else {
			return false;
		}
	}
}
