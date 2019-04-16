package com.plm.util;

import java.io.File;

import com.plm.oam.apps.LDAPInactiveUsersReport;

public class GenerateInactiveUsersReport {
	
	public static File generateReport(String user) {
		return LDAPInactiveUsersReport.generateReport(user);
	}

	

}
