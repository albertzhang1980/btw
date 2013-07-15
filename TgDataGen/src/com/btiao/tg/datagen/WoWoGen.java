package com.btiao.tg.datagen;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dom4j.Node;

import com.btiao.tg.TgData;
import com.btiao.tg.TgShop;

public class WoWoGen extends Gen {
	static public void main(String[] args) throws Exception {
		Gen.ORIGIN_TG_DIR = "genTest" + File.separator;
		WoWoGen wo = new WoWoGen();
		
		wo.preGen();
		
		boolean r = wo.genTg();
		assert(r);
		assert(wo.shopsTmp.size() == 1);
		
		SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(wo.tgTmp.endTime);
		String tmStr = fm.format(date);
		//System.out.println(wo.tgTmp.endTime + " " + tmStr);
		assert(tmStr.equals("2013-08-31 23:59:00"));
		assert(wo.tgTmp.price == 7800);
		
		assert(wo.shopsTmp.get(0).latitude == 40036000);
		assert(wo.shopsTmp.get(0).longitude == 116311000);
		
		wo.postGen();
		try {
			assert(false);
			System.out.println("pls use -ea VM argument!");
		} catch (Throwable e) {
			System.out.println("success!");
		}
	}

	@Override
	protected boolean genTg() {
		try {
		
		if (index >= tgNodes.size()) {
			return false;
		}
		
		Node tgNode = tgNodes.get(index++);
		
		tgTmp = new TgData();
		tgTmp.url = ((Node)tgNode.selectNodes("loc").get(0)).getText();
		tgTmp.title = ((Node)tgNode.selectNodes("data/display/title").get(0)).getText();
		tgTmp.imageUrl = ((Node)tgNode.selectNodes("data/display/image").get(0)).getText();
		tgTmp.startTime = tmStr2Long(((Node)tgNode.selectNodes("data/display/startTime").get(0)).getText());
		tgTmp.endTime = tmStr2Long(((Node)tgNode.selectNodes("data/display/endTime").get(0)).getText());
		String type = ((Node)tgNode.selectNodes("data/display/catName").get(0)).getText();
		tgTmp.type = convertType(type);
		tgTmp.value = priceStr2Int(((Node)tgNode.selectNodes("data/display/value").get(0)).getText());
		tgTmp.price = priceStr2Int(((Node)tgNode.selectNodes("data/display/price").get(0)).getText());
		tgTmp.boughtNum = str2Int(((Node)tgNode.selectNodes("data/display/bought").get(0)).getText());
		tgTmp.desc = ((Node)tgNode.selectNodes("data/display/tip").get(0)).getText();
		tgTmp.useEndTime = tmStr2Long(((Node)tgNode.selectNodes("data/display/quanEndTime").get(0)).getText());
		
		shopsTmp.clear();
		
		@SuppressWarnings("unchecked")
		List<Node> shopNodes = (List<Node>)tgNode.selectNodes("data/shops/shop");
		for (Node shopNode : shopNodes) {
			TgShop shop = new TgShop();
			shopsTmp.add(shop);
			
			shop.name = ((Node)shopNode.selectNodes("name").get(0)).getText();
			shop.tel = ((Node)shopNode.selectNodes("tel").get(0)).getText();
			shop.addr = ((Node)shopNode.selectNodes("addr").get(0)).getText();
			shop.shopArea = ((Node)shopNode.selectNodes("area").get(0)).getText();
			shop.longitude = doubleLatLon2Long(((Node)shopNode.selectNodes("longitude").get(0)).getText());
			shop.latitude = doubleLatLon2Long(((Node)shopNode.selectNodes("latitude").get(0)).getText());
			
			shop.longitude += 116428301 - 116422000;
			shop.latitude += 40048130 - 40042000;
		}
		
		return true;
		
		} catch (Exception e) {
			e.printStackTrace();
			
			return genTg();
		}
	}
	
	@Override
	protected String getName() {
		return "wowo";
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void preGen() {
		tgNodes = (List<Node>)doc.selectNodes("/urlset/url");
		unkownTypeStrs.clear();
	}

	@Override
	protected void postGen() throws Exception {
		FileOutputStream fout = new FileOutputStream("wowo_unkowntype.txt");
		try {
			Set<Entry<String,String>> entrys = unkownTypeStrs.entrySet();
			for (Entry<String,String> entry : entrys) {
				fout.write((entry.getKey()+"\n").getBytes());
			}
		}finally {
			fout.close();
		}
	}
	
	private int convertType(String type) {
		if (type.equals("其他火锅")) {
			return TgData.TgType.food_huoguo;
		} else if (type.equals("特色餐厅")) {
			return TgData.TgType.food;
		}
		else {
			unkownTypeStrs.put(type, "");
			return TgData.TgType.unkown;
		}
	}
	
	private List<Node> tgNodes = null;
	private int index = 0;
	
	private Map<String,String> unkownTypeStrs = new HashMap<String,String>();
	
	private String city; 
}
