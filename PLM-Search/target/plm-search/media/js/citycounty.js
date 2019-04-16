	var resp;	
	function getResponse(){
		try {
			if(resp == true){	
				var recElm = xmlHttp.responseXML.documentElement.getElementsByTagName("Records");
	            var number = recElm.item(0).getAttribute("TotalNumERecs");
	            if (number == "Error") {
	                alert('No records found.');               
	                return;
	            } else {
	                // This implementation for image line up
					var recordList = recElm.item(0).getElementsByTagName("Record");				
					var totalRecords = recordList.length;
					var innerHTMLStr = '';
					selectField = document.getElementById('City');
					selectField.options.length = 0;				
					selectField.options[selectField.length] = new Option('City Name', 'city');
					for(var i =0; i <= totalRecords; i++){			
						var rec = recordList[i];
						var city = rec.getAttribute("city");
						var name="";
						var value="";
						var idx = 0;
						idx=city.indexOf("|");					  							
  						name=city.substring(0,idx);
  						value=city.substring(idx+1);	
						selectField.options[selectField.length] = new Option(name, value);					
					}					
					xmlHttp = null;
	                return;
	            }
			}
		} catch(exc) {}
	}

	function getCities(value){
		var url = "getCities?county="+value;		
		resp = execute_get(url, false);
		getResponse();
	}