package com.btiao.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.btiao.Result;
import com.btiao.service.AllTgMgr;
import com.btiao.tg.TgData;

public class GetTgs extends HttpServlet {
	/**
	 * 输入：
	 * handle=123&pgs=123;
	 * 返回格式
	 * {result=123;desc="";more:true/false; tgs:[{tgObj}];}
	 */
	public void doGet (HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
		res.setContentType("text/html;charset=UTF-8");
		
		int result = 0;
		
		Map<String,String[]> args = req.getParameterMap();
		
		int idx = 0;
		String[] idxArg = args.get("idx");
		if (idxArg != null && idxArg.length > 0) {
			try {
				idx = Integer.parseInt(idxArg[0]);
			} catch (Exception e) {
				result = Result.ARG_ERROR;
			}
		} else {
			result = Result.ARG_ERROR;
		}
		
		int pgs = 0;
		String[] pgsArg = args.get("pgs");
		if (pgsArg != null && pgsArg.length > 0) {
			try {
				pgs = Integer.parseInt(pgsArg[0]);
			} catch (Exception e) {
				result = Result.ARG_ERROR;
			}
		} else {
			result = Result.ARG_ERROR;
		}
		
		int handle = 0;
		String[] handleArg = args.get("handle");
		if (handleArg != null && handleArg.length > 0) {
			try {
				handle = Integer.parseInt(handleArg[0]);
			} catch (Exception e) {
				result = Result.ARG_ERROR;
			}
		} else {
			result = Result.ARG_ERROR;
		}
		
		if (!AllTgMgr.instance().canUse()) {
			result = Result.TG_ERROR;
		}
		
		if (result != Result.SUCCESS) {
			PrintWriter out = res.getWriter();
			out.print("var rst = {result:"+result+",desc:\""+Result.desc(result)+"\",tgs:[]}");
			out.close();
			return;
		}
		
		PrintWriter out = res.getWriter();

		List<TgData> tgs = AllTgMgr.instance().getTg(handle, idx, pgs);
		
		out.print("var rst = {result:0,tgs:[");
		
		StringBuilder sb = new StringBuilder();
		int num = tgs.size();
		if (num > 0) {
			JsonCvt.obj(tgs.get(0), sb);
			for (int i=1; i<num; ++i) {
				sb.append(",");
				JsonCvt.obj(tgs.get(i), sb);
		    }
		}
	    
		out.print(sb);
	    out.print("]}");
	    
	    out.close(); 
	}
}
