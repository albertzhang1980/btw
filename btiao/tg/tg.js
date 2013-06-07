(function(){
//namespace registor

//global variable

//function define
function refreshPos() {
	var url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js";
	$.getScript(url, function() {
		var cityName = $("#curCity");
		cityName.empty();
		cityName.append(remote_ip_info.city);
	});
}

function refreshTg() {
	$("#idTgList").empty();
	
	var url = "../GetTgs?handle=0&pgs=10&idx=0";
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
	});
}

function genTgHtml(tg) {
	var r = '<a target="_blank" href="';
	r += tg.url;
	r += '"><img class="ctgImg" src="';
	r += tg.imageUrl;
	r += '"/></a>';
	r += '<p class="ctgTitle"><a target="_blank" href="';
	r += tg.url;
	r += '">';
	r += tg.title;
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
	r += '</p><a class="cGo" target="_blank" href="';
	r += tg.url;
	r += '">去看看</a></div>'
		
	return r;
}

//global code
refreshPos();
refreshTg();

})();