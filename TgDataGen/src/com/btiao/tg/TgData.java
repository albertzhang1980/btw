package com.btiao.tg;

import java.util.List;

public class TgData {
	public static class TgType {
		static public final int unkown = 0; //����ʶ��ģ�����������Ҫ����
		static public final int food = 1; //��ʳ [1,10000)
		static public final int food_huoguo = 1000; //��ʳ-���
		static public final int ktv = 10000; //KTV [10000,20000)
		static public final int film = 20000; //��Ӱ [20000,30000)
		static public final int film_child = 21000; //��Ӱ-��ͯƬ
	};
	
	public int type; //�Ź����ͣ�ȡֵ��Χ����TgType
	
	public String url;
	
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
}
