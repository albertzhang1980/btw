var gMap;
function onLoad() {
	$("#p1x").blur(function() {
		p1.x = $("#p1x").val();
	});
	$("#p1y").blur(function() {
		p1.y = $("#p1y").val();
	});
	$("#p2x").blur(function() {
		p2.x = $("#p2x").val();
	});
	$("#p2y").blur(function() {
		p2.y = $("#p2y").val();
	});
	p1.x = 116.414;$("#p1x").val(p1.x);
	p1.y = 39.915;$("#p1y").val(p1.y);
	p2.x = p1.x;$("#p2x").val(p2.x);
	p2.y = p1.y;$("#p2y").val(p2.y);
	next_p2();

	gMap = new BMap.Map("out");
	gMap.centerAndZoom(new BMap.Point(116.414, 39.915), 15);
	gMap.enableKeyboard();
}
function drv(p1, p2) {
	var driving = new BMap.DrivingRoute(gMap, {
		renderOptions: { map: gMap, autoViewport: true }
	});
	
	//driving.search("西单","王府井");
	driving.search(new BMap.Point(p1.x, p1.y), new BMap.Point(p2.x, p2.y));
}
function printGeo(lon, lat, title) {
	var myGeo = new BMap.Geocoder();
	myGeo.getLocation(new BMap.Point(lon, lat), function(result){
		if (result){ 
			$(txtout).empty();
			$(txtout).append("lon="+lon+",lat="+lat+","+result.address);
		}
	});
	
	gMap.panTo(new BMap.Point(lon, lat), 15);
	var mk = new BMap.Marker(new BMap.Point(lon, lat));
	mk.setTitle(title);
	gMap.addOverlay(mk);
}

var cos40 = 0.766;
var lonDist = cos40*2*3.1415926*6371.004*1000/360; //alert(lonDist); 
var latDist = 2*3.1415926*6356.755*1000/360; //alert(latDist);

function judgeNm(p, dx, dy) {
	//var lonDist = 111194.9946; //2*3.1415926*6371.004*1000/360; //经度上每度多少米，111194.9946 m
	//var latDist = 110946.3026; //2*3.1415926*6356.755*1000/360; //赤道上的0维度线每经度多少米，110946.3026 m	
	var r = {x:(p.x*1000000+1000000*dx/lonDist)/1000000, y:(p.y*1000000+1000000*dy/latDist)/1000000};
	return r;
}
function next_p2() {
	p2 = judgeNm(p2, 500, 500);
	$("#p2x").val(p2.x);
	$("#p2y").val(p2.y);
}
var p1 = {x:116.414,y:39.915};
var p2 = judgeNm(p1, 1*1000, 1*1000);

	var INTERVAL = 700;
	var INTERVAL_NUM = 2000;
	var INTERVAL_FAILED = 5000;
	var PER_NUM = 100;
	
function genAll(city, p, step, pp) {
	initDB(city);
	
	var numX = (pp.x - p.x)/(step/lonDist);
	var numY = (pp.y - p.y)/(step/latDist);
	var total = numX*numY*(numX*numY-1); total = parseInt(total);
	
	var tm_start = new Date().getTime();
	var tm_end = new Date().getTime();
	
	var iFirst=0;
	var jFirst=0;
	var iSecond=0;
	var jSecond=0;
	
	var nextIJFirst = function () {
		++jFirst;
		if (jFirst>=numX) {
			jFirst = 0;
			++iFirst;
			
			if (iFirst>=numX) {
				return false;
			} else {
				return true;
			}
		}
		return true;
	}
	var nextIJSecond = function () {
		++jSecond;
		if (jSecond>=numY) {
			jSecond = 0;
			++iSecond;
			
			if (iSecond>=numY) {
				return false;
			} else {
				return true;
			}
		}
		return true;
	}

	//每隔100ms执行一次，一次执行10条
	setTimeout(function() {
		var exeNum = 0;
		
		var firstIsLeggal = true;
		do {
			var secondIsLeggal = true;
			do {
				p1 = judgeNm(p, iFirst*step, jFirst*step);
				p2 = judgeNm(p, iSecond*step, jSecond*step);
				
				if (!(p1.x == p2.x && p1.y == p2.y)) {
					getDistZJ(city, p1, p2, true);
					getDistGJ(city, p1, p2, true);
					getDistOnly(city, p1, p2);
				}
				
				++exeNum;
				secondIsLeggal = nextIJSecond();
				if (exeNum>=INTERVAL_NUM || !secondIsLeggal) break;
			} while (true);
			
			if (!secondIsLeggal) {
				iSecond = 0;
				jSecond = 0;
				firstIsLeggal = nextIJFirst();
			}
				
			if (exeNum>=INTERVAL_NUM || !firstIsLeggal) {
				break;
			}
		} while (true);

		if (firstIsLeggal) {
			setTimeout(arguments.callee, INTERVAL);
			
			tm_end = new Date().getTime();
			var time = (tm_end-tm_start)/1000;

			var eclipseNumGJ = total - genAll.okGJNum;
			var eclipseNumZJ = total - genAll.okZJNum;
			var eclipseNumDist = total - genAll.okDistOnlyNum;
			
			var spGJ = (genAll.okGJNum)/time; spGJ = parseInt(spGJ);
			var spZJ = (genAll.okZJNum)/time; spZJ = parseInt(spZJ);
			var spDist = (genAll.okDistOnlyNum)/time; spDist = parseInt(spDist);
			
			var hGJ = (eclipseNumGJ)/(spGJ*3600); hGJ = parseInt(hGJ);
			var mGJ = ((eclipseNumGJ)/(spGJ*60))%60; mGJ = parseInt(mGJ);
			var sGJ = ((eclipseNumGJ)/spGJ)%60; sGJ = parseInt(sGJ);
			
			var hZJ = (eclipseNumZJ)/(spZJ*3600); hZJ = parseInt(hZJ);
			var mZJ = ((eclipseNumZJ)/(spZJ*60))%60; mZJ = parseInt(mZJ);
			var sZJ = ((eclipseNumZJ)/spZJ)%60; sZJ = parseInt(sZJ);
			
			var hDist = (eclipseNumDist)/(spDist*3600); hDist = parseInt(hDist);
			var mDist = ((eclipseNumDist)/(spDist*60))%60; mDist = parseInt(mDist);
			var sDist = ((eclipseNumDist)/spDist)%60; sDist = parseInt(sDist);
			
			$("#genAllProcess").empty();
			$("#genAllProcess").append("<p>total: "+total+",已使用时间: "+parseInt(time/3600)+"小时,"+parseInt(time/60)%60+"分钟,"+parseInt(time)%60+"秒"+"</p>");
			$("#genAllProcess").append("<p>finished_gj: "+genAll.okGJNum+","+genAll.okGJBDNum+",速度_gj: "+spGJ+",剩余时间: "+hGJ+"小时,"+mGJ+"分钟,"+sGJ+"秒"+"</p>");
			$("#genAllProcess").append("<p>finished_zj: "+genAll.okZJNum+","+genAll.okZJBDNum+",速度_zj: "+spZJ+",剩余时间: "+hZJ+"小时,"+mZJ+"分钟,"+sZJ+"秒"+"</p>");
			$("#genAllProcess").append("<p>finished_dist: "+genAll.okDistOnlyNum+","+genAll.okDistOnlyBDNum+",速度_zj: "+spDist+",剩余时间: "+hDist+"小时,"+mDist+"分钟,"+sDist+"秒"+"</p>");
		}
	}, 0);

	//对于超时的请求，负责重新处理
	setTimeout(function() {
		var NUM = total;
		
		if ((genAll.okZJNum && genAll.okGJNum && genAll.okDistOnlyNum) &&
			!(genAll.okZJNum < NUM) && !(genAll.okGJNum < NUM) && !(genAll.okDistOnlyNum < NUM)) {
			$("#txtout").append("genAll exe all success!!!");
			closeDB(city);
			return;
		}
		
		//1次处理10个
		var exeNumZJ = 0;
		if (genAll.okZJNum < NUM && genAll.failsZJ) {
			while (genAll.failsZJ.length > 0 && exeNumZJ++ < 10) {
				var one = genAll.failsZJ.shift();
				getDistZJ(city, one.p1, one.p2);
			}
		}
		
		var exeNumGJ = 0;
		if (genAll.okGJNum < NUM && genAll.failsGJ) {
			while (genAll.failsGJ.length > 0 && exeNumGJ++ < 10) {
				var one = genAll.failsGJ.shift();
				getDistGJ(city, one.p1, one.p2);
			}
		}
		
		var exeNumDist = 0;
		if (genAll.okDistOnlyNum < NUM && genAll.failsDist) {
			while (genAll.failsDist.length > 0 && exeNumDist++ < 10) {
				var one = genAll.failsDist.shift();
				getDistOnly(city, one.p1, one.p2);
			}
		}
		
		//剩下的延迟x秒下处理
		setTimeout(arguments.callee, INTERVAL_FAILED);
	}, INTERVAL_FAILED);
}
function sendDist(d, cb) {
	var url = "../addp1p2dist";
	$.get(url, d, cb);
}
function cvtP(p) {
	var r = {x:parseInt(p.x*1000000),y:parseInt(p.y*1000000)};
	return r;
}
function getDistOnly(city, p1, p2) {
	var lineDist = gMap.getDistance(new BMap.Point(p1.x, p1.y),new BMap.Point(p2.x, p2.y));
	lineDist = parseInt(lineDist);
	var time = 0;
	var totalDist = 0;
	
	var P1 = cvtP(p1);
	var P2 = cvtP(p2);
	
	//genAll.okDistOnlyNum = genAll.okDistOnlyNum ? (genAll.okDistOnlyNum + 1) : 1;
	//return;
	genAll.okDistOnlyBDNum = genAll.okDistOnlyBDNum ? (genAll.okDistOnlyBDNum + 1) : 1;
	push2cache({city:city,p1x:P1.x,p1y:P1.y,p2x:P2.x,p2y:P2.y,dist:lineDist, zj:false, gj:false, distOnly:true});
}
function push2cache(o) {
	if (!sendDist.cache) {
		sendDist.cache = [];
	}
	sendDist.cache.push(o);

	if (sendDist.cache.length >= PER_NUM) {
		var cache = sendDist.cache;
		sendDist.cache = [];
		
		var p1x = "";
		var p1y = "";
		var p2x = "";
		var p2y = "";
		var city = "";
		var dist = "";
		var dist_zj = "";
		var dist_gj = "";
		var time = "";
		var zj = false;
		var gj = false;
		var distOnly = false;
		
		p1x = cache[0].p1x;
			p1y = cache[0].p1y;
			p2x = cache[0].p2x;
			p2y = cache[0].p2y;
			city = cache[0].city;
			dist = cache[0].dist;
			dist_zj = cache[0].dist_zj;
			dist_gj = cache[0].dist_gj;
			time_gj = cache[0].time_gj;
			time_zj = cache[0].time_zj;
			
			zj = cache[0].zj;
			gj = cache[0].gj;
			distOnly = cache[0].distOnly;
		for (var i=1; i<cache.length; ++i) {
			p1x +="," + (cache[i].p1x ? cache[i].p1x : "");
			p1y +="," + (cache[i].p1y ? cache[i].p1y : "");
			p2x +="," + (cache[i].p2x ? cache[i].p2x : "");
			p2y +="," + (cache[i].p2y ? cache[i].p2y : "");
			dist_zj +="," + (cache[i].dist_zj ? cache[i].dist_zj : "");
			dist_gj +="," + (cache[i].dist_gj ? cache[i].dist_gj : "");
			time_gj +="," + (cache[i].time_gj ? cache[i].time_gj : "");
			time_zj +="," + (cache[i].time_zj ? cache[i].time_zj : "");
			dist +="," + (cache[i].dist ? cache[i].dist : "");
		}
		
		if (gj) sendDist({city:city,p1x:p1x,p1y:p1y,p2x:p2x,p2y:p2y,dist_gj:dist_gj,time_gj:time_gj,dist:dist,multi:PER_NUM}, function(result) {
			if (result == "false") {
				failCB({p1:p1,p2:p2}, zj, gj, distOnly);
			} else {
				genAll.okGJNum = genAll.okGJNum ? (genAll.okGJNum + PER_NUM) : PER_NUM;
			}
		});
		if (zj) sendDist({city:city,p1x:p1x,p1y:p1y,p2x:p2x,p2y:p2y,dist_zj:dist_zj,time_zj:time_zj,dist:dist,multi:PER_NUM}, function(result) {
			if (result == "false") {
				failCB({p1:p1,p2:p2}, zj, gj, distOnly);
			} else {
				genAll.okZJNum = genAll.okZJNum ? (genAll.okZJNum + PER_NUM) : PER_NUM;
			}
		});
		if (distOnly) sendDist({city:city,p1x:p1x,p1y:p1y,p2x:p2x,p2y:p2y,dist:dist,multi:PER_NUM}, function(result) {
			if (result == "false") {
				failCB({p1:p1,p2:p2}, zj, gj, distOnly);
			} else {
				genAll.okDistOnlyNum = genAll.okDistOnlyNum ? (genAll.okDistOnlyNum + PER_NUM) : PER_NUM;
			}
		});
	}
}
function getDistZJ(city, p1, p2, disable) {
	if (disable) {
		genAll.okZJNum = genAll.okZJNum ? (genAll.okZJNum + 1) : 1;
		return;
	}

	//alert(p1.x);alert(p1.y);alert(p2.x);alert(p2.y);
	var options = {
		policy: BMAP_DRIVING_POLICY_LEAST_TIME,
		onSearchComplete: function(results) {
			var status = driving.getStatus();
			if (status == BMAP_STATUS_TIMEOUT) {
				failCB({p1:p1,p2:p2}, true, false, false);
			} else { 
				var totalDist = -1;//单位：米，-1表示未知
				var lineDist = -1;//单位：米，-1表示未知
				var time = -1;//单位：分钟，-1表示未知
				var P1 = cvtP(p1);
				var P2 = cvtP(p2);
	
				if (status == BMAP_STATUS_SUCCESS) {
					var plan = results.getPlan(0);
					if (0) { for (var rtIdx=0; rtIdx<plan.getNumRoutes(); ++rtIdx) {
						var route = plan.getRoute(0);
						var d = route.getDistance(false); //单位：米
						totalDist += d;
					}
					time = 60*totalDist/(50*1000); } //按照平均每小时50公里 
					
					totalDist = plan.getDistance(false);
					totalDist = parseInt(totalDist);
					time = plan.getDuration(false);
					time = parseInt(time);	
				} else {
					; //查询错误，表示未知
					$("#txtoutdetail").append("<p>zjerr="+status+",p1.x="+p1.x+",p1.y="+p1.y+",p2.x="+p2.x+",p2.y="+p2.y+"</p>");
				}
				
				lineDist = gMap.getDistance(new BMap.Point(p1.x, p1.y),new BMap.Point(p2.x, p2.y));
				lineDist = parseInt(lineDist);
				
				genAll.okZJBDNum = genAll.okZJBDNum ? (genAll.okZJBDNum + 1) : 1;
				
				push2cache({city:city,p1x:P1.x,p1y:P1.y,p2x:P2.x,p2y:P2.y,dist_zj:totalDist, time_zj:time, dist:lineDist,zj:true, gj:false, distOnly:false});
				/*
				sendDist({city:city,p1x:P1.x,p1y:P1.y,p2x:P2.x,p2y:P2.y,dist_zj:totalDist, time_zj:time, dist:lineDist}, function (result) {
					if (result == "false") {
						failCB({p1:p1,p2:p2}, true, false, false);
					} else {
						genAll.okZJNum = genAll.okZJNum ? (genAll.okZJNum + 1) : 1;
					}
				});*/
			}
		}
		//,renderOptions: { map: gMap, autoViewport: true }
	};
	
	var driving = new BMap.DrivingRoute(gMap, options);
	
	driving.search(new BMap.Point(p1.x, p1.y), new BMap.Point(p2.x, p2.y));
}
function getDistGJ(city, p1, p2, disable) {
	if (disable) {
		genAll.okGJNum = genAll.okGJNum ? (genAll.okGJNum + 1) : 1;
		return;
	}

	var P1 = cvtP(p1);
	var P2 = cvtP(p2);
	
	var transit = new BMap.TransitRoute(new BMap.Point(p1.x, p1.y), {
		policy: BMAP_TRANSIT_POLICY_LEAST_TIME
	});
	transit.setSearchCompleteCallback(function(results){
		var status = transit.getStatus();
		if (status == BMAP_STATUS_TIMEOUT){
			failCB({p1:p1,p2:p2}, false, true, false);
		} else {
			var totalDistBx = -1; //单位：米，-1表示未知
			var totalDistGj = -1; //单位：米，-1表示未知
			var totalDist = -1; //单位：米，-1表示未知
			var time = -1; //单位：分钟，-1表示未知
			var lineDist = -1; //单位：米，-1表示未知
			
			if (status == BMAP_STATUS_SUCCESS) {
				var firstPlan = results.getPlan(0);
				
				if (0) { for (var i = 0; i < firstPlan.getNumRoutes(); i ++){
					var walk = firstPlan.getRoute(i);
					var d = walk.getDistance(false);
					totalDistBx += d;
					gMap.addOverlay(new BMap.Polyline(walk.getPath(), {lineColor: "green"}));
				}
				
				for (i = 0; i < firstPlan.getNumLines(); i ++){
					var line = firstPlan.getLine(i);
					gMap.addOverlay(new BMap.Polyline(line.getPath()));
					var d = line.getDistance(false);
					//var num = line.getNumViaStops(); //途径站点数目
					totalDistGj += d;
				}
				time += 60*totalDistGj/(30*1000); //公交速度：30公里/小时
				totalDist = totalDistGj + totalDistBx; }
				
				//$(txtoutdetail).append("totalDistGJ=" + totalDist+"("+lineDist+"), ");
			
				totalDist = firstPlan.getDistance(false);
				totalDist = parseInt(totalDist); //去掉小数位
				time = firstPlan.getDuration(false)/60;
				time = parseInt(time); //去掉小数位
			} else {
				; //查询错误，表示未知
				$("#txtoutdetail").append("<p>gjerr="+status+",p1.x="+p1.x+",p1.y="+p1.y+",p2.x="+p2.x+",p2.y="+p2.y+"</p>");
			}
				
			lineDist = gMap.getDistance(new BMap.Point(p1.x, p1.y),new BMap.Point(p2.x, p2.y));
			lineDist = parseInt(lineDist); //去掉小数位
			
			var P1 = cvtP(p1);
			var P2 = cvtP(p2);
			genAll.okZJBDNum = genAll.okZJBDNum ? (genAll.okZJBDNum + 1) : 1;
			push2cache({city:city,p1x:P1.x,p1y:P1.y,p2x:P2.x,p2y:P2.y,dist_gj:totalDist, time_gj:time, dist:lineDist,zj:false, gj:true, distOnly:false});
			/*
			sendDist({city:city,p1x:P1.x,p1y:P1.y,p2x:P2.x,p2y:P2.y,dist_gj:totalDist, time_gj:time, dist:lineDist}, function (result) {
				if (result == "false") {
					failCB({p1:p1,p2:p2}, false, true, false);
				} else {
					genAll.okGJNum = genAll.okGJNum ? (genAll.okGJNum + 1) : 1;
				}
			}); */
		}
	});
	
	transit.search(new BMap.Point(p1.x, p1.y), new BMap.Point(p2.x, p2.y));
}
function failCB(p2pObj, zj, gj, dist) {
	if (zj) {
		if (!genAll.failsZJ) genAll.failsZJ = [];
		
		genAll.failsZJ.push(p2pObj);
		$("#txtoutdetail").append("<p>zj,p1.x="+p2pObj.p1.x+",p1.y="+p2pObj.p1.y+",p2.x="+p2pObj.p2.x+",p2.y="+p2pObj.p2.y+"</p>");
	}
	if (gj) {
		if (!genAll.failsGJ) genAll.failsGJ = [];
		
		genAll.failsGJ.push(p2pObj);
		$("#txtoutdetail").append("<p>gj,p1.x="+p2pObj.p1.x+",p1.y="+p2pObj.p1.y+",p2.x="+p2pObj.p2.x+",p2.y="+p2pObj.p2.y+"</p>");
	}
	if (dist) {
		if (!genAll.failsDist) genAll.failsDist = [];
		
		genAll.failsDist.push(p2pObj);
		$("#txtoutdetail").append("<p>dist,p1.x="+p2pObj.p1.x+",p1.y="+p2pObj.p1.y+",p2.x="+p2pObj.p2.x+",p2.y="+p2pObj.p2.y+"</p>");
	}
}
function initDB(city) {
	var url = "../dbop";
	$.ajax({url:url,
			dataType:"text",
			async:false,
			data:{city:city,op:"init"},
			success:function (data, txtStatus) {
				if (data == "false") {
					$(txtout).append("<p>initDB.result=false</p>");
				} else {
					$(txtout).append("<p>initDB.result=true</p>");
				}
			}});
}
function closeDB(city) {
	var url = "../dbop";
	$.ajax({url:url,
			dataType:"text",
			async:false,
			data:{city:city,op:"close"},
			success:function (data, txtStatus) {
				if (data == "false") {
					$(txtout).append("<p>closeDB.result=false</p>");
				} else {
					$(txtout).append("<p>closeDB.result=true</p>");
				}
			}});
}
function clearInc() {
	var url = "../dbop";
	$.ajax({url:url,
			dataType:"text",
			async:false,
			data:{city:city,op:"clearInc"},
			success:function (data, txtStatus) {
				$(txtout).append("<p>clearInc.</p>");
			}});
}




