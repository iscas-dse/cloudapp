package cn.ac.iscas.cloudeploy.v2.util;

import java.util.Iterator;
import java.util.Properties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class PropertiesUtil {
	private static final String DEFAULT_PROPERTIES = "application.properties";
	private static Properties PROPERTIES;
	static {
		try {
			Resource resource = new ClassPathResource(DEFAULT_PROPERTIES);
			PROPERTIES = PropertiesLoaderUtils.loadProperties(resource);
			Iterator<String> it = PROPERTIES.stringPropertyNames().iterator();
			while (it.hasNext()) {
				String key = it.next();
				System.out.println(key + ":" + PROPERTIES.getProperty(key));
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
