//////////////////////////////////////////
// Session Timeout Detection functions
//////////////////////////////////////////

var globalTimoutInterval = 30000;

//============================================================
// Retrieve session time-to-live through timeoutagent-i.php
//
// Input:
//    timeoutCallbackFn - function callback when session is timed
//                        out
//============================================================
function sessionTimeoutCheck(timedoutCallbackFn)
{
    sessionCheck(timedoutCallbackFn, true);
}

//============================================================
// Do periodic session time-to-live check via timeoutagent-i.php
//
// Input:
//    timeoutCallbackFn - function callback when session is timed
//                        out
//============================================================
function sessionPeriodicTimeoutCheck(timedoutCallbackFn)
{
    sessionCheck(timedoutCallbackFn, false);
}

//============================================================
// Retrieve session time-to-live through timeoutagent-i.php
//
// Input:
//    timeoutCallbackFn - function callback when session is timed
//                        out
//    saveTinValue - indicates whether we are to save TIN
//                     cookie value or not
//============================================================
function sessionCheck(timedoutCallbackFn, saveTinValue)
{
    if (typeof(timedoutCallbackFn) != "function") {
        return;
    }

    if( ( r = getHTTPRequestObject() ) !== null ){
        r.onreadystatechange = function(rObj,timedoutCallbackFn, saveTinValue){ return function(){ timeoutHandler(rObj,timedoutCallbackFn,saveTinValue); } }(r,timedoutCallbackFn,saveTinValue);
        r.open( 'GET', '/vdesk/timeoutagent-i.php', true );
        r.send("");
    }
}

//============================================================
// Callback function when request to timeoutagent-i.php completes
//============================================================
function timeoutHandler(r,timedoutCallbackFn,saveTinValue)
{
    if(r.readyState != 4){ return; }
    if(r.status < 400 && (( match = document.cookie.match( /TIN=(\d+)[;]?/ ) ) != null) && (( expirationTimeout = parseInt( match[1] ) ) > 0) ){
        if(saveTinValue) {
            globalTimoutInterval = expirationTimeout;
        }
        window.setTimeout( function(fn,saveTinValue) { return function() { sessionCheck(fn,saveTinValue); } }(timedoutCallbackFn, saveTinValue), globalTimoutInterval );
    } else {
        timedoutCallbackFn();
    }
}

//============================================================
// Helper function to display splash msg
//
// Input:
//    elementId - id of the splash element
//============================================================
function showSplashLayer(elementId, customizedText)
{
    var d = document.getElementById(elementId);
    if (d != null) {
        try {
            d.className = 'inspectionHostDIVBig';
            customizedText = customizedText.replace(/\[SESSION_RESTART_URL\]/g, "/");
            d.innerHTML = customizedText;
            var b = document.getElementsByTagName("body");
            if (b != null) {
                disableAllElements(b[0], d);
            }
        }
        catch (e) {}
    }
}

function getHTTPRequestObject()
{
    var res = null;
    try {
        res = new XMLHttpRequest();
    }catch(e){ try {
            res = new ActiveXObject('Msxml2.XMLHTTP');
        }catch(e){ try {
                res = new ActiveXObject('Microsoft.XMLHTTP');
            }catch(e){
                res = null;
            }
        }
    }
    return res;
}

function disableAllElements(elementToDisable, elementToExclude)
{
    if (elementToDisable == null) {
        return;
    }

    if ((typeof(elementToExclude) == "undefined") || (elementToExclude == null) ||
        (elementToDisable == elementToExclude)) {
        return;
    }

    try {
        if (typeof(elementToDisable.disabled) != "undefined" && elementToDisable.tagName.toLowerCase() != "body") {
            elementToDisable.disabled = true;
        }

        if (elementToDisable.tagName.toLowerCase() == "a") {
            elementToDisable.style.cursor = "not-allowed";
            elementToDisable.onclick = function() { return false; };
        }

        if (elementToDisable.childNodes && elementToDisable.childNodes.length > 0) {
            for (var x = 0; x < elementToDisable.childNodes.length; x++) {
                disableAllElements(elementToDisable.childNodes[x], elementToExclude);
            }
        }
    }
    catch (e) {
    }
}
