package cn.ac.iscas.cloudeploy.v2.util;
import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
@Log4j
public final class ConstantsUtil {
	public static String WEB_INF_PATH = "";
	
	private static ConstantsUtil constantsUtil;
	
	private ConstantsUtil(){
		
	}
	public final static ConstantsUtil getConstant(){
		return constantsUtil;
	}
	public static String COM_PATH = "";
	public static  String APP_PATH = "APP";
	
	public static  String SEP = "/";
	//public static  String TILLER_SERVER = "133.133.134.118";
	//public static  int TILLER_PORT = 32143;
	
	public static  String SERVER_USER = "root";
	public static  String SERVER_PW = "oncedi1012";
	
	public static  String HELM_WIN_HOME = "";
	public static  String COMPONENTS = "components";
	
	public static  String DEPLOYMENT = "deployment";
	public static  String DEPLOYMENT_YAML = "deployment.yaml";
	
	public static  String SERVICE = "service";
	public static  String SERVICE_YAML = "service.yaml";
	
	// -----------------k8s-------------
	public static  String CONTAINER_VERSION = "K8S";
	public static  String K8S_DEFAULT_NAMESPACE = "production";
	public static  String K8S_DASHBOARD_URL;
	public static  String K8S_API_SERVER ;
	//------------------consul-----------
	public static  String CONSUL_URL ;
	public static  String WEAVE_URL;
	//------------------docker------------
	public static  String DOCKER_URL ;
	//默认properties
	private static  String DEFAULT_PROPERTIES = "application.properties";

	static {
		init();
	}

	private static void init() {
		 constantsUtil = new ConstantsUtil();

		if (WEB_INF_PATH.isEmpty()) {
			File file = new File(constantsUtil.getClass().getClassLoader().getResource("").getFile());
			WEB_INF_PATH = file.getParent();
			COM_PATH = WEB_INF_PATH + SEP + COMPONENTS;
			log.info("WEB INF文件保存地址：" + WEB_INF_PATH);
			log.info("组件文件保存地址:" + COM_PATH);
		}
		try {
			Resource resource = new ClassPathResource(DEFAULT_PROPERTIES);
			Properties  properties= PropertiesLoaderUtils.loadProperties(resource);
			Iterator<String> it = properties.stringPropertyNames().iterator();
			String key=null;
			String value=null;
			while (it.hasNext()) {
				key = it.next();
				value=properties.getProperty(key);
				//k8s
				constantsUtil.K8S_API_SERVER=properties.getProperty("K8S_API_SERVER");
				constantsUtil.K8S_DEFAULT_NAMESPACE=properties.getProperty("K8S_DEFAULT_NAMESPACE");
				constantsUtil.K8S_DASHBOARD_URL=properties.getProperty("K8S_DASHBOARD_URL");
				//consul
				constantsUtil.CONSUL_URL=properties.getProperty("CONSUL_URL");
				//docker ui
				constantsUtil.DOCKER_URL=properties.getProperty("DOCKER_URL");
				//weave ui
				constantsUtil.WEAVE_URL=properties.getProperty("WEAVE_URL");
				log.info("初始化常量："+key + ":" + value);	
			}
		} catch (Exception e) {
			log.info(e);
		}
		
	}
	

}
