function paroleedata(thisElem, searchby) {
	if(searchby == '1')
		refineTabID = 1;
	else if(searchby == '2')
		refineTabID = 2;
	else if(searchby = '3')
		refineTabID = 3;
	
	var thisli = thisElem.parentNode;
	var searchbydiv = document.getElementById('searchby'+searchby);

	for (a=0;a<thisli.parentNode.getElementsByTagName('li').length;a++){
		thisli.parentNode.getElementsByTagName('li')[a].className='';
		document.getElementById("searchby"+(a+1)).className='hide';
	}
	searchbydiv.className='show';
	thisli.className='selected';
}

function show(thisElem, searchdiv) {
	var thisli = thisElem.parentNode;
	var searchbydiv = document.getElementById(searchdiv);
	var maindiv = document.getElementById('paroleeresultslistresult');

	for (a=0;a<thisli.parentNode.getElementsByTagName('li').length;a++){
		thisli.parentNode.getElementsByTagName('li')[a].className='';
		document.getElementById("paroleeresultslist"+(a+1)).className='hide';
	}

	searchbydiv.className='show';
	thisli.className='selected';
}