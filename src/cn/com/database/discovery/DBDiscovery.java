package cn.com.database.discovery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.pinae.pumbaa.data.DataOutputor;
import org.pinae.pumbaa.data.db.SQLConstant;
import org.pinae.pumbaa.data.db.SQLExecutor;
import org.pinae.pumbaa.data.db.SQLMapper;
import org.pinae.pumbaa.data.db.SQLMetadata;
import org.pinae.pumbaa.data.file.FileTools;
import org.pinae.pumbaa.data.file.ZipFile;

import cn.com.database.config.Configure;
import cn.com.database.config.Constant;

/**
 * 数据库发现任务
 * 
 * @author Huiyugeng
 * 
 */
public class DBDiscovery{
	
	private String sourceName; // 数据源名称

	private String dbType; // 数据库类型
	private String driver; // 数据库驱动程序
	private String url; // 数据库URL地址
	private String user; // 数据库用户名
	private String password; // 数据库密码

	protected String targets[] = null; // 数据库发现目标

	/**
	 * 构造函数
	 * 
	 * @param sourceName 数据源名称
	 * @param url        数据库路径
	 * @param user       用户
	 * @param password   密码
	 */
	public DBDiscovery(String sourceName,String url,String user,String password) {
		this.sourceName = sourceName;
		this.url = url;
		this.user = user;
		this.password = password;
	}


	/**
	 * 设置数据库类型
	 * 
	 * @param dbType 数据库类型 (Oracle/MySQL)
	 */
	public void setDBType(String dbType) {
		this.dbType = dbType;
		if (StringUtils.equalsIgnoreCase(dbType, "ORACLE")) {
			this.driver = Constant.ORACLE_DRIVER;
		} else if (StringUtils.equalsIgnoreCase(dbType, "MySQL")) {
			this.driver = Constant.MYSQL_DRIVER;
		}
	}
	
	/**
	 * 设置数据库发现目标
	 * 
	 * @param targets 数据库目标，例如TABLE/COLUMN
	 */
	public void setTargets(String[] targets) {
		this.targets = targets;
	}

	public boolean dbDiscoveryExecute() {
		if (StringUtils.isNotEmpty(url) && StringUtils.isNotEmpty(user) && StringUtils.isNotEmpty(password)) {

			SQLExecutor executor = new SQLExecutor(driver, url, user, password);

			List<String> fileList = new ArrayList<String>();
			Map<String, String> sqls = getSQL(dbType);

			Map<String, Integer> dataCounter = new HashMap<String, Integer>();
			
			Set<String> sqlNameSet = sqls.keySet();
			for (String sqlName : sqlNameSet) {
				String sql = sqls.get(sqlName);

				List<Object[]> resultSet = executor.select(sql);
				String columns[] = getColumn(sql);
				if (resultSet != null && resultSet.size() > 0) {
					
					dataCounter.put(sqlName, resultSet.size());
					
					List<Map<String, Object>> table = new SQLMapper().convertTableToMapList(resultSet, columns);

					String filename = String.format("%s/%s@%s_%s.csv", Configure.REPORT_PATH, sqlName,
							sourceName, Long.toString(System.currentTimeMillis()));
					// 输出为CSV格式
					new DataOutputor().outputCSV(filename, columns, table);
					fileList.add(filename);
				}
			}

			// 关闭数据库连接
			executor.close();

			String uploadFile = null;
			// 如果输出的文件大于1个，则进行文件压缩，并仅保留压缩后的文件
			if (fileList.size() > 1) {
				try {
					// 生成压缩文件
					uploadFile = String.format("%s/%s_%s.zip", Configure.REPORT_PATH, this.sourceName,Long.toString(System.currentTimeMillis()));
					ZipFile.zip(fileList, uploadFile);

					// 清理文件被压缩的文件
					FileTools.deleteFileSet(fileList);
				} catch (IOException e) {

				}
			} else if (fileList.size() == 1) {
				uploadFile = fileList.get(0);
			}
		}

		return true;
	}

	/**
	 * 获取SQL语句列信息
	 * 
	 * @param sql SQL语句
	 * 
	 * @return SQL语句返回的列名称
	 */
	private String[] getColumn(String sql) {

		SQLMetadata metadata = new SQLMetadata(driver, url, user, password);
		List<Map<String, String>> metadataList = metadata.getMetadataBySQL(sql);

		String[] columns = new String[metadataList.size()];
		for (int i = 0; i < metadataList.size(); i++) {
			Map<String, String> metadataItem = metadataList.get(i);
			String name = metadataItem.get("NAME");

			if (name != null) {
				columns[i] = name.toLowerCase();
			}
		}
		return columns;
	}

	private Map<String, String> getSQL(String db) {
		Map<String, String> sqls = new HashMap<String, String>();

		for (String target : targets) {
			String sqlName = String.format("%s_%s", db, target);
			String sql = SQLConstant.getSQLByName(StringUtils.upperCase(sqlName));

			sqls.put(sqlName, sql);
		}

		return sqls;
	}
}
