function showContent(vThis) {
	var vParent = vThis.parentNode;
	var vSibling = vParent.nextSibling;
	var TabNames = vThis.childNodes ;
	var TabID = TabNames[0];
	if(TabID=='[object Text]'){
		var	TabID1 = TabNames[1];
	} else if (TabID=='[object HTMLDivElement]') {
		var	TabID1 = TabNames[0];
	} else if(TabID=='[object]'){
		var	TabID1 = TabID;
	}
	while (vSibling.nodeType==3) { // Fix for Mozilla/FireFox Empty Space becomes a TextNode or Something
		vSibling = vSibling.nextSibling;
	};
	if(vSibling.className == "hide") {
		TabID1.getElementsByTagName('img')[0].src="media/images/global/arrow_up_blue.gif";
		TabID1.getElementsByTagName('img')[0].alt = "Collapse";
		vSibling.className = "show";
		parent.resizeIframe('maincntnt');
	} else {
		vSibling.className = "hide";
		TabID1.getElementsByTagName('img')[0].src="media/images/global/arrow_down_blue.gif";
		TabID1.getElementsByTagName('img')[0].alt = "Expand";
		parent.resizeIframe('maincntnt');
	}
	return;
}

function showallContent(){
	var parolledetailsdivcontent = document.getElementsByTagName('div');
	var showallinput = document.getElementById('showall');
	var showallimg = document.getElementById('showallimg');
	var arrowimg = document.getElementsByTagName('img');
	var avav = showallimg.src;
	if(showallinput.innerHTML=='Expand All'){
	for(a=0; a < parolledetailsdivcontent.length; a++){
		if (parolledetailsdivcontent[a].id=='parolledetailsdivcontent') {
			if(parolledetailsdivcontent[a].className=='hide'){
				parolledetailsdivcontent[a].className='show';
				
			}
		}
		}
		showallinput.innerHTML='Collapse All';
		showallimg.src="media/images/global/arrow_up_blue_all.gif";
		showallimg.alt='Collapse All';
		for (c=0; c < arrowimg.length; c++){
			var data = arrowimg[c].src;
	var e=/((http|ftp):\/)?\/?([^:\/\s]+)((\/\w+)*\/)([\w\-\.]+\.[^#?\s]+)(#[\w\-]+)?/;
    if (data.match(e)) {
        var url= RegExp['$&'];
                var protocol= RegExp.$2;
                var host=RegExp.$3;
                var path=RegExp.$4;
                var file=RegExp.$6;
                var hash=RegExp.$7;
				
    }
			if(url=='/'+host+path+'arrow_down_blue.gif'){
				arrowimg[c].src='media/images/global/arrow_up_blue.gif';
				arrowimg[c].alt='Collapse';
			}
		}
	}
	else if(showallinput.innerHTML=='Collapse All'){
		for(b=0; b < parolledetailsdivcontent.length; b++){
		if (parolledetailsdivcontent[b].id=='parolledetailsdivcontent') {
			if(parolledetailsdivcontent[b].className=='show'){
				parolledetailsdivcontent[b].className='hide';
			}
		}
		}
		showallinput.innerHTML='Expand All';
		showallimg.src="media/images/global/arrow_down_blue_all.gif";
		showallimg.alt='Expand All';
		for (c=0; c < arrowimg.length; c++){
			var data = arrowimg[c].src;
	var e=/((http|ftp):\/)?\/?([^:\/\s]+)((\/\w+)*\/)([\w\-\.]+\.[^#?\s]+)(#[\w\-]+)?/;
    if (data.match(e)) {
        var url= RegExp['$&'];
                var protocol= RegExp.$2;
                var host=RegExp.$3;
                var path=RegExp.$4;
                var file=RegExp.$6;
                var hash=RegExp.$7;
				
    }
			if(url=='/'+host+path+'arrow_up_blue.gif'){
				arrowimg[c].src='media/images/global/arrow_down_blue.gif';
				arrowimg[c].alt='Expand';
			}
		}
	}
	parent.resizeIframe('maincntnt');
}