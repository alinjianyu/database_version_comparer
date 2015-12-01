package cn.com.database.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.pinae.pumbaa.util.ClassLoaderUtils;

/**
 * 配置文件
 * 
 * @author Linjianyu
 */
public class Configure {
	private static Logger log = Logger.getLogger(Configure.class);

	private static String CONFIGURE_FILE = "service.properties";

	private Properties properties = new Properties();

	private static Configure CONFIGURE;

	// 服务命名空间
	public static final String SERVICE_NAMESPACE = "http://service.task.cirm.letour.com.cn/";

	// 报告输出地址
	public static String REPORT_PATH = get("REPORT_PATH", ClassLoaderUtils.getResourcePath(""));
	
	// 源数据源
	public static String SRC_TYPE = get("SRC_TYPE", ClassLoaderUtils.getResourcePath(""));
	// 源数据源
	public static String SRC_URL = get("SRC_URL", ClassLoaderUtils.getResourcePath(""));
	// 源数据源
	public static String SRC_USER = get("SRC_USER", ClassLoaderUtils.getResourcePath(""));
	// 源数据源
	public static String SRC_PASSWORD = get("SRC_PASSWORD", ClassLoaderUtils.getResourcePath(""));

	// 目的数据源
	public static String DEST_TYPE = get("DEST_TYPE", ClassLoaderUtils.getResourcePath(""));
	// 目的数据源
	public static String DEST_URL = get("DEST_URL", ClassLoaderUtils.getResourcePath(""));
	// 目的数据源
	public static String DEST_USER = get("DEST_USER", ClassLoaderUtils.getResourcePath(""));
	// 目的数据源
	public static String DEST_PASSWORD = get("DEST_PASSWORD", ClassLoaderUtils.getResourcePath(""));


	/*
	 * 构造函数，载入配置文件
	 */
	protected Configure() {
		InputStream in = Configure.class.getClassLoader().getResourceAsStream(CONFIGURE_FILE);
		try {
			properties.load(in);
		} catch (IOException e) {
			log.error(String.format("Configure Exception:exception=%s", e.getMessage()));
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				log.error(String.format("Configure Exception:exception=%s", e.getMessage()));
			}
		}
	}

	// 获得配置文件实例
	private static synchronized Configure getInstance() {
		if (CONFIGURE == null) {
			CONFIGURE = new Configure();
		}
		return CONFIGURE;
	}

	/**
	 * 根据关键字获取键值
	 * 
	 * @param key 关键字
	 * 
	 * @return 键值
	 */
	public static String get(String key, String defaultValue) {
		if (getInstance().properties.containsKey(key)) {
			return (String) getInstance().properties.get(key);
		}
		return defaultValue;
	}

	/**
	 * 根据关键字获取整型键值
	 * 
	 * @param key 关键字
	 * 
	 * @return 键值
	 */
	public static int getInteger(String key, int defaultValue) {
		String value = get(key, "0");
		if (StringUtils.isNumeric(value)) {
			return Integer.parseInt(value);
		} else {
			return defaultValue;
		}
	}

	/**
	 * 根据关键字获取长整型键值
	 * 
	 * @param key 关键字
	 * 
	 * @return 键值
	 */
	public static long getLong(String key, long defaultValue) {
		String value = get(key, "0");
		if (StringUtils.isNumeric(value)) {
			return Long.parseLong(value);
		} else {
			return defaultValue;
		}
	}

	/**
	 * 根据关键字获取布尔值键值
	 * 
	 * @param key 关键字
	 * 
	 * @return 键值
	 */
	public static boolean getBoolean(String key, boolean defaultValue) {
		String value = get(key, "false");
		if (value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))) {
			return Boolean.parseBoolean(value);
		} else {
			return defaultValue;
		}
	}

	/**
	 * 判断关键字是否存在且值不为空
	 * 
	 * @param key 关键字
	 * 
	 * @return 键值结果
	 */
	public static boolean hasKey(String key) {
		if (getInstance().properties.containsKey(key)) {
			String value = (String) getInstance().properties.get(key);
			if (StringUtils.isNotBlank(value)) {
				return true;
			}
		}
		return false;
	}

	public static synchronized void reload() {
		CONFIGURE = new Configure();
	}

}
