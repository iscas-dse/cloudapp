package cn.ac.iscas.cloudeploy.v2.dataview;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.Chart;
import cn.ac.iscas.cloudeploy.v2.util.ConstantsUtil;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.extensions.Deployment;

public class ChartView {
	public static class DetailedChart {
		public Long id;
		public String name;
		public String version;
		public String description;
		public Long createdAt;
		public Long updatedAt;
		public HashMap<String, String> path2File = new HashMap<>();
		public Deployment deployment;
		public Service service;
	}
	private static final ConcurrentHashMap<String, Deployment> deploymentsCache=new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<String, Service> servicesCache=new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<String, String> deploymentsFileStringCache=new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<String, String> servicesFileStringCache=new ConcurrentHashMap<>();
	// Application获取详细信息
	private static Function<Chart, DetailedChart> DTAILED_ITEM_VIEW_TRANSFORMER = new Function<Chart, DetailedChart>() {
		@Override
		public DetailedChart apply(Chart chart) {
			if (chart == null)
				return null;
			DetailedChart detailedChart = new DetailedChart();
			detailedChart.id = chart.getId();
			detailedChart.name = chart.getName();
			detailedChart.description = chart.getDescription();
			detailedChart.version = chart.getVersion();
			String comRootPath=ConstantsUtil.COM_PATH + File.separator + chart.getName();
			//System.err.println(comRootPath);
			String deploymentPath=comRootPath+File.separator+ConstantsUtil.DEPLOYMENT+File.separator+ConstantsUtil.DEPLOYMENT_YAML;
			String servicePath=comRootPath+File.separator+ConstantsUtil.SERVICE+File.separator+ConstantsUtil.SERVICE_YAML;
			
			detailedChart.createdAt = chart.getCreateTime() == null ? null : chart.getCreateTime().getTime();
			detailedChart.updatedAt = chart.getUpdateTime() == null ? null : chart.getUpdateTime().getTime();
			//service
			String path=File.separator+ConstantsUtil.SERVICE+File.separator+ConstantsUtil.SERVICE_YAML;
			String fileString=servicesFileStringCache.get(servicePath);
			if(fileString==null){
				try {
					File serviceFile=new File(servicePath);
					fileString=FileUtils.readFileToString(serviceFile);
					servicesFileStringCache.put(servicePath, fileString);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			detailedChart.path2File.put(path,fileString);
			//deployment
			path=File.separator+ConstantsUtil.DEPLOYMENT+File.separator+ConstantsUtil.DEPLOYMENT_YAML;
			fileString=deploymentsFileStringCache.get(deploymentPath);
			if(fileString==null){
				try {
					File deploymentFile=new File(deploymentPath);
					fileString=FileUtils.readFileToString(deploymentFile);
					deploymentsFileStringCache.put(deploymentPath, fileString);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			detailedChart.path2File.put(path,fileString);
			//service deployment
			Service service=servicesCache.get(servicePath);
			Deployment deployment=deploymentsCache.get(deploymentPath);
			if(deployment==null||service==null){
				Yaml yaml=new Yaml();
				try {
					File deploymentFile=new File(deploymentPath);
					File serviceFile=new File(servicePath);
					service=yaml.loadAs(new FileInputStream(serviceFile), Service.class);
					deployment=yaml.loadAs(new FileInputStream(deploymentFile), Deployment.class);
					servicesCache.put(servicePath, service);
					deploymentsCache.put(deploymentPath, deployment);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			detailedChart.service=service;
			detailedChart.deployment=deployment;
			return detailedChart;
		}
	};

	public static DetailedChart detailedViewOf(Chart input) {
		return input == null ? null : DTAILED_ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<DetailedChart> detailedViewListOf(List<Chart> input) {
		return input == null ? ImmutableList.<DetailedChart> of()
				: Lists.transform(input, DTAILED_ITEM_VIEW_TRANSFORMER);
	}
}
