package com.plm.dataextract;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceUtils;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;

public class DBTest {
	
	public static void main(String args[]) {
		//DataSourceUtils.getConnection(ds);
		
		Resource xmlResource = new FileSystemResource("D:/CDCR/PROJ9/PLM-DataExtractor/target/classes/dbConfig.xml");
	    BeanFactory factory = new XmlBeanFactory(xmlResource);

	    //DataSourceTransactionManager dstm = (DataSourceTransactionManager)factory.getBean("calparoleDB");
	    
	    //Connection conn = DataSourceUtils.getConnection(dstm.getDataSource());
	    Connection conn = DataSourceUtils.getConnection((DataSource)factory.getBean("dataSource"));
	    //System.out.println(dstm.getDataSource().getClass().getName());
		try{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM PAROLEE WHERE ROWNUM = 1");
			//ResultSetMetaData rsm = rs.getMetaData();
			//int colCount = rsm.getColumnCount();
			if(rs.next()) {
				System.out.println("Record : >" + rs.getString(1));
			}else{
				System.out.println(" NO RECORD FOUND....");
			}
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
	    finally {
	    	try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	}
}
