package cn.com.database.discovery;

/**
 * Oracle数据库发现任务
 * 
 * @author Linjianyu
 * 
 */
public class OracleDiscovery extends DBDiscovery {

	public OracleDiscovery(String sourceName,String url,String user,String password) {
		super(sourceName,url,user,password);
		
		super.setDBType("ORACLE");
		super.setTargets(new String[]{ "TABLE", "COLUMN" });
	}

}
