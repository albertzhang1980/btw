function onload() {
//namespace registor

//global variable
var uLon = 116425000;
var uLat = 40052000;
var btGlobal = {};

var nextPageIdx = 0;
var page_size = 10;
var hasMore = true;

//function define
function refreshPos() {
	var url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js";
	$.getScript(url, function() {
		var cityName = $("#curCity");
		cityName.empty();
		cityName.append(remote_ip_info.city);
		
		if (remote_ip_info.city == "北京") {
			btGlobal.curTgCity = "beijing";
		}
	});
}

function refreshTg(wantEmpty) {
	if (wantEmpty) {
		$("#idTgList").empty();
	}
	
	if (!btGlobal.curTgCity || btGlobal.curTgCity == "") {
		setTimeout(refreshTg, 200);
		return;
	}
	
	var url = "../GetTgs?pgs="+page_size+"&idx="+(nextPageIdx++)*page_size+"&city="+btGlobal.curTgCity+
			"&uLon="+uLon+"&uLat="+uLat;
	
	$.getScript(url, function() {
		var result = rst.result;
		if (result != 0) {
			return;
		}
		
		for (var idx=0; idx<rst.tgs.length; ++idx) {
			var tgHtml = '<li class="cTgLi"><div class="'+(idx%2==0?'cTgUnitLeft':'cTgUnitRight')+'">';
			tgHtml += genTgHtml(rst.tgs[idx]);
			tgHtml += '</div></li>';
			$("#idTgList").append(tgHtml);
		}
		
		if (rst.tgs.length < page_size) {
			hasMore = false;
			
			$("#more").empty();
			$("#more").append("《您要的信息均已呈现》");
		} else {
			$("#more").empty();
			$("#more").append("还有更多...");
		}
	});
}

btGlobal.genTgHtml = genTgHtml;
function genTgHtml(tg) {
	var r = '<a target="_blank" href="';
	r += tg.url;
	r += '"><img class="ctgImg" src="';
	r += tg.imageUrl;
	r += '"/></a>';
	r += '<p class="ctgTitle" title="'+tg.title+'"><a target="_blank" href="';
	r += tg.url;
	r += '">';
	var endIdx = tg.title.length > 60 ? 60 : tg.title.length;
	r += tg.title.substring(0, endIdx);
	r += '</a></p>';
	r += '<div class="cPriceLine"><p>';
	r += '<span class="cMoneySign">¥</span>';
	r += '<span class="cPrice">';
	r += tg.price/100;
	r += '</span>';
	r += '&nbsp;&nbsp;<span class="cValue">&nbsp;&nbsp;原价&nbsp;</span>';
	r += '<span class="cValue">¥';
	r += tg.value/100;
	r += '&nbsp;&nbsp;</span>';
	r += '<a class="cGo" target="_blank" href="';
	r += tg.url;
	r += '" title="'+tg.shopName+'">去看看</a></p>';
	var dist = distMode(tg.dist);
	if (dist == 0) {
		r += '<p class="cDist"><span>500&nbsp;米以内</span>';
	} else {
		r += '<p class="cDist"><span>大约'+distMode(tg.dist)+'&nbsp;米</span>';
	}
	r += '<span>&nbsp;|&nbsp;&nbsp;<a href="'+genLineUrl('北京',uLon,uLat,tg.longitude,tg.latitude,tg.shopName)+'" target="_blank" title="'+tg.shopName+'">查看大概路线</a></span>';
	r += '</p></div>'
		
	return r;
}
function distMode(dist) {
	return parseInt(dist/500)*500;
}
function genLineUrl(city, uLon, uLat, tgLon, tgLat, shopName) {
	return 'http://api.map.baidu.com/direction?origin=latlng:'+uLat/1000000+','+uLon/1000000+'|name:您的位置'+
		'&destination=latlng:'+tgLat/1000000+','+tgLon/1000000+'|name:'+shopName+'&output=html&mode=driving'+
		'&region='+city;
}

function scrollEvent(evt) {
	var viewportHeight = window.innerWidth;
	var verticalScroll = window.pageYOffset;

	var element = document.getElementById("more");
	var actualTop = getY(element) + 665;
	
	if (viewportHeight >= (actualTop - verticalScroll)) {
		if (scrollEvent.lastMoreState == "display") {
			return;
		}
		
		scrollEvent.lastMoreState = "display";
		
		if (hasMore) {
			var second = 3;
			setTimeout(function() {
				$("#more").empty();
				$("#more").append("马上查询下一页... 倒计时（"+(second)+"）秒");
				if (second == 0) {
					refreshTg();
					$("#more").empty();
					$("#more").append("正在查询下一页... ");
				} else {
					setTimeout(arguments.callee, 1000);
				}
				
				-- second;
			}, 500);

			//alert("top="+actualTop+",hegiht="+viewportHeight+",verticalScroll="+verticalScroll)
		}
	} else {
		scrollEvent.lastMoreState = "hide";
	}
}
function getY(element) {
    var y = 0;
    for(var e = element; e; e = e.offsetParent) // Iterate the offsetParents
        y += e.offsetTop;                       // Add up offsetTop values

    for(e = element.parentNode; e && e != document.body; e = e.parentNode)
        if (e.scrollTop) y -= e.scrollTop;  // subtract scrollbar values
    return y;
}


//global code
refreshPos();
refreshTg(true);
var elm = document.getElementsByTagName("body")[0];
elm.addEventListener("scroll",scrollEvent, false);
window.onscroll = scrollEvent;
};
