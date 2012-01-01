package com.imps.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectDB {
	
	private String username = "liwenhaosuper";
	private String pwd = "liwenhaosuper";
	private String db = "imps";
	private String dbDriver ="com.mysql.jdbc.Driver";
	private String dbConnect = "jdbc:mysql://localhost:3306/"+db;
	private Connection conn = null;
	public Statement stmt = null;
	public ResultSet rs = null;
	
	public ConnectDB() throws SQLException
	{
		try{
			Class.forName(dbDriver);
			conn = DriverManager.getConnection(dbConnect,username,pwd);
			stmt = conn.createStatement();
			
		}catch(ClassNotFoundException e)
		{
			System.err.println("jdbcDriver Error!");
		}
	}
	
	public ResultSet executeSQL(String script) throws SQLException
	{
		ResultSet res = null;
		try{
			res = stmt.executeQuery(script) ;
		}catch(Exception e)
		{
			e.printStackTrace();
		}

		return res;
	}
	
	public int executeUpdate(String script) throws SQLException
	{
		int res = 0;
		res = stmt.executeUpdate(script);
		return res;
	}
	

}
