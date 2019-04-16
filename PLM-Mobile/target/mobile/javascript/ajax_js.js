var xmlHttp;
function createXMLHttpRequest() {
	   try { return new XMLHttpRequest(); } catch(e) {}
	   try { return new ActiveXObject("Msxml2.XMLHTTP"); } catch (e) {}
	   try { return new ActiveXObject("Microsoft.XMLHTTP"); } catch (e) {}
	   alert("XMLHttpRequest not supported");
	   return null;
}

function execute(actionType, url, params ,async, callback){	
	xmlHttp = createXMLHttpRequest();
	if(actionType =="GET"){
		url = url+"&random="+Math.random();
	}
	xmlHttp.open(actionType, url, async); 
	//alert(callback);
	if((async) && (callback != null)){
		xmlHttp.onreadystatechange=callback;
	}

	if(params != null){
		xmlHttp.send(params);
	} else {
		xmlHttp.send(null);
	}
	
	if((async)) {
		//alert(xmlHttp.readyState);
		if((callback != null) && (xmlHttp.readyState == 4)){
			if(xmlHttp.responseText.indexOf("<title>LOGIN</title>") > 0) {
				location.href = "plmredirect";
				return false;
			}
		} else{
			return true;
		}
		
	} else {
		if(xmlHttp.status == 200){
			if(xmlHttp.responseText.indexOf("<title>LOGIN</title>") > 0) {
				location.href = "plmredirect";
				return false;
			} else{
				return true;
			}
		} else {
			return false;
		}
	}
}



function execute_get_callback(url, async, callback){
	return execute("GET", url, null, async, callback);
}

function execute_post_callback(url, params, async, callback){
	return execute("POST", url, params, async, callback);
}

function execute_get(url, async){
	return execute("GET", url, null, async, null);
}

function execute_post(url, params, async){
	return execute("POST", url, null, async, null);
}