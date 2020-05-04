package tools;

import java.sql.*;

enum ResultTypes{
	OFFER, ACCOUNT, ORDER, TRADE, CLOSED, HPTABLE
}

public class DBMan {
	
	private Connection conn = null;
	
	public static void CreateDatabase(String path, String UName, String Pass) throws SQLException{
		Connection c = DriverManager.getConnection("jdbc:hsqldb:file:" + path + ";ifexists=false",UName,Pass);
		Statement s = c.createStatement();
		s.executeQuery("SHUTDOWN;");
	}
	
	public DBMan(String path, String UName, String Pass) throws SQLException{
		try{
			conn = DriverManager.getConnection("jdbc:hsqldb:file:" + path + ";ifexists=true",UName,Pass);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public void ShutDown() throws SQLException{
		Statement s = conn.createStatement();
		s.executeQuery("SHUTDOWN;");
	}
	
	public void RunOrder(String SQLOrder) throws SQLException{
		Statement s = conn.createStatement();
		s.executeQuery(SQLOrder);
	}
	
	public ResultSet Query(String SQLQuery, ResultTypes rt) throws SQLException{
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery(SQLQuery);
		return rs;
	}

}
