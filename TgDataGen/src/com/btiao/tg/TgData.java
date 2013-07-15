package com.btiao.tg;

import java.util.ArrayList;
import java.util.List;

public class TgData {
	public static class TgType {
		static public final int unkown = 0; //����ʶ��ģ�����������Ҫ����
		static public final int food = 1; //��ʳ [1,10000)
		static public final int food_huoguo = 1000; //��ʳ-���
		static public final int food_kaoyu = 1001; //��ʳ-����
		static public final int food_kaorou = 1002; //��ʳ-���� 
		static public final int food_mlxg = 1003; //��ʳ-�������
		static public final int food_xican = 1004; //��ʳ-����
		static public final int food_taicai = 1005; //��ʳ-̩��
		static public final int food_dangao = 1006; //��ʳ-����
		static public final int food_kaoya = 1007; //��ʳ-��Ѽ
		static public final int food_ice = 1008; //��ʳ-�����
		
		static public final int ktv = 10000; //KTV
		
		static public final int film = 20000; //��Ӱ
		static public final int film_child = 20001; //��Ӱ-��ͯƬ
		
		static public final int tiyu = 30000; //����
		static public final int tiyu_jianshen = 30001; //����-����
		static public final int tiyu_youyong = 30002; //����-��Ӿ
		
		static public final int yangs = 40000; //����
		static public final int yangs_spa = 40001; //����-SPA
		
		static public final int jiuba = 50000; //�ư�
		
		static public final int mei = 60000; //��������
		static public final int mei_meijia = 60000; //��������-����
		
		static public final int juyuan = 70000; //��Ժ
		static public final int juyuan_yinyue = 70001; //��Ժ-���־�
		
		static public final int sheying = 70000; //��Ӱ
		static public final int sheying_xiezhen = 70001; //��Ӱ-д��
		static public final int sheying_hunsha = 70002; //��Ӱ-��ɴ
		
	};
	
	public int type; //�Ź����ͣ�ȡֵ��Χ����TgType
	
	public String url;
	
	public int longitude;
	public int latitude;
	
	public String title;
	public String desc;
	
	public String imageUrl;
	
	//ʱ����Ϣ
	public long startTime;
	public long endTime;
	public long useEndTime; //��������ʱ�� 
	
	//�۸���Ϣ
	public int value;
	public int price;
	public int boughtNum; //��ǰ��������
//	public int maxQuota; //��๺������
//	public int minQuota; //���ٹ�������
	
	//������Ϣ
//	public int bitType; //��0λΪ�Ƿ�֧��Ͷ��
	
	//�Ź���Դ
//	public String nameOfOrigWebSite;
//	public String urlOfOrigWebSite;
	
	public int dist = 1000*1000; //���룬Ĭ�ϱ�ʾδ֪
	public String shopName = ""; //�̼�����
}
