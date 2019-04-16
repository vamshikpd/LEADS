function DetectBoolean(terms) {
	var tokens = terms.split(/\s+/);
	var isBoolean = false;
	for(var i=0; i<tokens.length; i++) {
		if (tokens[i].search(/^AND$/i)>-1 ||
			tokens[i].search(/^OR$/i)>-1  ||
			tokens[i].search(/^NOT$/i)>-1 ||
			tokens[i].search(/^O?NEAR/i)>-1 ||
			tokens[i].search(/:$/)>-1 ||
			tokens[i].search(/^\(/)>-1 ||
			tokens[i].search(/\)$/)>-1) {
					
			isBoolean = true;
			break;
		}
	}
	return isBoolean;
}

function getMode(terms) {
	if(DetectBoolean(terms))
		return "mode matchboolean";
	else
		return "<%= UI_Props.getInstance().getValue(UI_Props.MATCHMODE) %>";
}

function Search() {
 	var terms = document.searchForm.keywords.value;
	var property = "<%= UI_Props.getInstance().getValue(UI_Props.SEARCHINTERFACE) %>";
	var mode = getMode(terms);
	var within = false;
	var curTerms = "<%= request.getParameter("Ntt")!=null?request.getParameter("Ntt"):"" %>";
	var curProps = "<%= request.getParameter("Ntk")!=null?request.getParameter("Ntk"):"" %>";
	var curModes = "<%= request.getParameter("Ntx")!=null?request.getParameter("Ntx"):"" %>";
	var numSearches = curTerms.split("|").length;
			
	// split still returns 1 if no Ntt exists in query string
	if(curTerms == "")
		numSearches = 0;
	if(numSearches>0) {
		curTerms+="|";
		curProps+="|";
		curModes+="|";
	}
	
	// Advanced search parameters
	if(document.searchForm.advsearch.value == "true") {
		property = document.searchForm.SearchProp[document.searchForm.SearchProp.selectedIndex].value;
		mode = document.searchForm.MatchMode[document.searchForm.MatchMode.selectedIndex].value;
		if(document.searchForm.RelevancyList.value != "") {
			mode += " rel " + document.searchForm.RelevancyList.value;
		}
	}
	
	if(document.searchForm.withinResults
		&& document.searchForm.withinResults.checked == true) {
		within = true;
	}

	var removeterms;
	var addterms;
	if(within) {

		//******************* START BUG WORKAROUND ***************
		// Merge queries being sent to same search interface to workaround
		// bug in 4.8.2 where ESearchReports get overwritten
		if(curProps.indexOf(property)>-1) {
			// remove extra | from end of the each param
			curTerms = curTerms.substring(0, curTerms.length-1);
			curProps = curProps.substring(0, curProps.length-1);
			curModes = curModes.substring(0, curModes.length-1);
			
			// get position of current property 
			var propsArray = curProps.split("|");
			var pos = 0;
			while(propsArray[pos] != property && pos<propsArray.length)
				pos++;
			
			// add the new terms to the old terms for the current property
			var termsArray = curTerms.split("|");
			termsArray[pos] += " "+terms;
			// get updated mode for complete list of terms
			mode = getMode(termsArray[pos]);
			
			curTerms = "";
			for(var i=0; i<termsArray.length; i++)
				curTerms += termsArray[i]+"|";
			curTerms = curTerms.substring(0, curTerms.length-1);
			
			// overwrite the mode
			var modeArray = curModes.split("|");
			if(modeArray[pos] != mode) {
				modeArray[pos] = mode;
				curModes = "";
				for(var i=0; i<modeArray.length; i++)
					curModes += modeArray[i]+"|";
				curModes = curModes.substring(0, curModes.length-1);
			}
						
			terms = "";
			property = "";
			mode = "";
		}
		//******************* END BUG WORKAROUND ****************
			
		removeterms = ["Ne","No","Nao","Nty","Dn","Ntt","Ntk","Ntx","D","Dx"];
		addterms = ["Ntk="+curProps+property,"Ntt="+encodeURIComponent(curTerms+terms),"Nty=1","D="+curTerms+terms,"Ntx="+curModes+mode,"Dx="+curModes+mode];
	}else {
		removeterms = ["N","Nf","Nn","Ne","No","Nao","Ntk","Ntt","D","Nty","Ntx","Dx","Dn"];
		addterms = ["N=<%= UI_Props.getInstance().getValue(UI_Props.ENE_ROOT) %>","Ntk="+property,"Ntt="+encodeURIComponent(terms),"Nty=1","D="+terms,"Ntx="+mode,"Dx="+mode];
	}
	var newurl = ConstructURL("CURRENTURL", removeterms, addterms);
	location.href = newurl;
}