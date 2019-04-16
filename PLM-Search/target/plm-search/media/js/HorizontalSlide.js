// Copyright 2006-2007 javascript-array.com

var timeout	= 500;
var closetimer	= 0;
var ddmenuitem	= 0;

// open hidden layer
function mopen(id)
{	
//alert('mopen');
	// cancel close timer
	mcancelclosetime();

	// close old layer
	if(ddmenuitem) ddmenuitem.style.display = 'none';

	// get new layer and show it
	ddmenuitem = document.getElementById(id);
	ddmenuitem.style.display = 'block';

}
// close showed layer
function mclose()
{
	//alert('mclose');
	if(ddmenuitem) ddmenuitem.style.display = 'none';
}

// go close timer
function mclosetime()
{
		//alert('mclosetime');

	closetimer = window.setTimeout(mclose, timeout);
}

// cancel close timer
function mcancelclosetime()
{
			//alert('mcancelclosetime');
			window.clearTimeout(closetimer);
		closetimer = null;
	/*alert(closetimer);
	if(closetimer)
	{
		window.clearTimeout(closetimer);
		closetimer = null;
		//alert('null...'+closetimer);
	}
	*/
}

// close layer when click-out
//document.onclick = mclose;