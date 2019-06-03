package cn.ac.iscas.cloudapp.agent.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import com.google.common.base.Charsets;


public class YamlUtils {
	public static <T> String dumper(T object){
		Constructor constructor = new Constructor(object.getClass());
		TypeDescription typeDescription = new TypeDescription(object.getClass());
		constructor.addTypeDescription(typeDescription);
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yaml = new Yaml(constructor, new NullRepresenter(),options);
		yaml.setBeanAccess(BeanAccess.FIELD);
		return yaml.dump(object);
	}
	
	public static <T> T loadAs(String content, Class<T> clazz){
		Yaml yaml = new Yaml();
		return yaml.loadAs(content, clazz);
	}
	
	public static <T> T loadAs(Path path, Class<T> clazz) throws IOException{
		Yaml yaml = new Yaml();
		try(BufferedReader input = Files.newBufferedReader(path, Charsets.UTF_8)){
			T t = yaml.loadAs(input, clazz);
			return t;
		}
	}
	
	private static class NullRepresenter extends Representer{
		@Override
		protected NodeTuple representJavaBeanProperty(Object javaBean,
				Property property, Object propertyValue, Tag customTag) {
			if(propertyValue == null){
				return null;
			}else{
				return super.representJavaBeanProperty(javaBean, property, propertyValue,
						customTag);
			}
		}
	}
}


