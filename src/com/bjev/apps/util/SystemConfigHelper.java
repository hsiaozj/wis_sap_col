package com.bjev.apps.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SystemConfigHelper {

	private static final Log log=LogFactory.getLog(SystemConfigHelper.class);
	
	private static Properties config;
	
	static{
		try{
			System.out.println("==================================config.properties loading....==================================");
			config = new Properties();
			InputStream ins=Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("com/bjev/apps/util/config.properties");
			InputStreamReader in = new InputStreamReader(ins, "UTF-8");
			config.load(in);
			System.out.println("==================================load config.properties done==================================");

		}catch(Exception e){
			log.error(e);
			System.exit(0);
		}
	}
	
	public static String getString(String key){
		return config.getProperty(key);
	}
	
	public static String getString(String key, String defaultString){
		String str = config.getProperty(key);
		if(StringUtils.isNotBlank(str)){
			return str;
		}else{
			return defaultString;
		}
	}
	
	public static int getInt(String key){
		return Integer.parseInt(config.getProperty(key));
	}
	
	public static int getInt(String key, int defaultValue){
		String str = config.getProperty(key);
		if(StringUtils.isNotBlank(str) && StringUtils.isNumeric(str)){
			return Integer.parseInt(config.getProperty(key));
		}else{
			return defaultValue;
		}
	}
	
	
}
