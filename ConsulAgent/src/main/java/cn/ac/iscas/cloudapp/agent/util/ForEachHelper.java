package cn.ac.iscas.cloudapp.agent.util;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ForEachHelper {
	public static <T> List<T> of(List<T> list){
		if(list == null) return Collections.emptyList();
		else return list;
	}
	public static <T> Set<T> of(Set<T> set){
		if(set == null) return Collections.emptySet();
		else return set;
	}
}
