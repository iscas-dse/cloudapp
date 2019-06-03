package cn.ac.iscas.cloudeploy.v2.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class TimeUtil {
	public static final synchronized String getCurTime(){
		SimpleDateFormat df = new SimpleDateFormat("MMDDHHmm");//设置日期格式
		return df.format(new Date());
	}
	public static void main(String[] args) {
		System.err.println(getCurTime());
	}
}
