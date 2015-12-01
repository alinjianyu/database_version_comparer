package cn.com.database.discovery;

/**
 * MySQL数据库发现任务
 * 
 * @author Linjianyu
 * 
 */
public class MySQLDiscovery extends DBDiscovery {


	public MySQLDiscovery(String sourceName,String url,String user,String password) {
		super(sourceName,url,user,password);
		
		super.setDBType("MySQL");
		super.setTargets(new String[]{ "TABLE", "COLUMN" });
	}



}
