package com.btiao.tg;

import java.util.List;

public class TgData {
	public static class TgType {
		static public final int unkown = 0; //不能识别的，若出现则需要处理
		static public final int food = 1; //美食 [1,10000)
		static public final int food_huoguo = 1000; //美食-火锅
		static public final int ktv = 10000; //KTV [10000,20000)
		static public final int film = 20000; //电影 [20000,30000)
		static public final int film_child = 21000; //电影-儿童片
	};
	
	public int type; //团购类型，取值范围见：TgType
	
	public String url;
	
	public String title;
	public String desc;
	
	public String imageUrl;
	
	//时间信息
	public long startTime;
	public long endTime;
	public long useEndTime; //最晚消费时间 
	
	//价格信息
	public int value;
	public int price;
	public int boughtNum; //当前购买人数
//	public int maxQuota; //最多购买人数
//	public int minQuota; //最少购买人数
	
	//其他信息
//	public int bitType; //第0位为是否支持投递
	
	//团购来源
//	public String nameOfOrigWebSite;
//	public String urlOfOrigWebSite;
}
