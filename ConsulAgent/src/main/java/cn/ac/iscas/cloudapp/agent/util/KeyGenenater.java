package cn.ac.iscas.cloudapp.agent.util;

public class KeyGenenater {
	public static String genenateListenerKey(String configKey){
		return "listener/" + configKey;
	}
}
