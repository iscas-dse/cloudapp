package cn.ac.iscas.cloudeploy.v2.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import io.fabric8.kubernetes.api.model.extensions.Deployment;

public class YamlHelper {

    public static void main(String[] args) throws IOException {
        String str = "D://ts//ts-deployment-part1.yml";
        Path path=Paths.get(str);
        List<Object> list = null;
        InputStream is = new FileInputStream(new File(str));
        Yaml yaml = new Yaml();
        if (is != null) {
            list = new ArrayList<Object>();
            for (Object obj : yaml.loadAll(is)) {
                list.add(obj);
                System.out.println(obj.getClass());
            }
            is.close();
        }
        //return list;
        
    }
}
