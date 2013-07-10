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
			var tgHtml = '<li class="cTgLi"><div class="cTgUnit">';
			tgHtml += genTgHtml(rst.tgs[idx]);
			tgHtml += '</div></li>';
			$("#idTgList").append(tgHtml);
		}
		
		if (rst.tgs.length < page_size) {
			hasMore = false;
			$("#more").empty();
			$("#more").append("没有团购了");
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
	r += '">去看看</a></p>';
	r += '<p><span>'+tg.dist+'&nbsp;米</span>';
	r += '<span>&nbsp;&nbsp;<a href="'+genLineUrl('北京',uLon,uLat,tg.longitude,tg.latitude)+'" target="_blank">查看路线</a></span>';
	r += '</p></div>'
		
	return r;
}
function genLineUrl(city, uLon, uLat, tgLon, tgLat) {
	return 'http://api.map.baidu.com/direction?origin=latlng:'+uLat/1000000+','+uLon/1000000+'|name:您的位置'+
		'&destination=latlng:'+tgLat/1000000+','+tgLon/1000000+'|name:团购位置'+'&output=html&mode=driving'+
		'&region='+city;
}

function scrollEvent(evt) {
	var viewportHeight = window.innerWidth;
	var verticalScroll = window.pageYOffset;

	var element = document.getElementById("more");
	var actualTop = getY(element) + 665;	
	if (viewportHeight >= (actualTop - verticalScroll)) {
		if (hasMore) {
			refreshTg();
			//alert("top="+actualTop+",hegiht="+viewportHeight+",verticalScroll="+verticalScroll)
		}
	}
}
function getY(element) {
    var y = 0;
    for(var e = element; e; e = e.offsetParent) // Iterate the offsetParents
        y += e.offsetTop;                       // Add up offsetTop values

    // Now loop up through the ancestors of the element, looking for
    // any that have scrollTop set. Subtract these scrolling values from
    // the total offset. However, we must be sure to stop the loop before
    // we reach document.body, or we'll take document scrolling into account
    // and end up converting our offset to window coordinates.
    for(e = element.parentNode; e && e != document.body; e = e.parentNode)
        if (e.scrollTop) y -= e.scrollTop;  // subtract scrollbar values

    // This is the Y coordinate with document-internal scrolling accounted for.
    return y;
}


//global code
refreshPos();
refreshTg(true);
var elm = document.getElementsByTagName("body")[0];
elm.addEventListener("scroll",scrollEvent, false);
window.onscroll = scrollEvent;
};
