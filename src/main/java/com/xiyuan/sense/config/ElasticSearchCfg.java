package com.xiyuan.sense.config;

import java.util.Properties;

public class ElasticSearchCfg {

	private static final Properties properties;

	static {
		properties = new Properties();
		try {
			properties.load(ElasticSearchCfg.class.getClassLoader().getResourceAsStream("property/elasticSearchCfg.properties"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final String root = properties.getProperty("root");

	public static final int wait_after_modify = Integer.parseInt(properties.getProperty("wait_after_modify"));

}