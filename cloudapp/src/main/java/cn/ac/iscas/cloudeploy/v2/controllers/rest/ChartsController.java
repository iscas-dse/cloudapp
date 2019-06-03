package cn.ac.iscas.cloudeploy.v2.controllers.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import cn.ac.iscas.cloudeploy.v2.dataview.ApplicationView;
import cn.ac.iscas.cloudeploy.v2.dataview.ChartView;
import cn.ac.iscas.cloudeploy.v2.dataview.ChartView.DetailedChart;
import cn.ac.iscas.cloudeploy.v2.model.dao.ApplicationDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.ChartDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.ContainerDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Chart;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Container;
import cn.ac.iscas.cloudeploy.v2.model.service.application.ApplicationService;
import cn.ac.iscas.cloudeploy.v2.util.ConstantsUtil;
import cn.ac.iscas.cloudeploy.v2.util.K8sUtil;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
/**
 * 
 * 模板管理的rest接口
 */
@Controller
@RequestMapping(value = "v2/charts")
public class ChartsController {
	private static final Logger logger=Logger.getLogger(ChartsController.class);
	@Autowired
	private ApplicationService appService;
	@Autowired
	private ChartDAO chartDao;
	@Autowired
	private ApplicationDAO applicationDAO;
	@Autowired
	private ContainerDAO containerDAO;
	private static List<DetailedChart> allDetailedCharts;
	private static List<Chart> allCharts;
	/**
	 * 获取应用简要信息列表
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET,value = { "/listDetailedcharts" })
	@ResponseBody
		public List<DetailedChart> listDetailedcharts() {
			if(allDetailedCharts==null){
				logger.info("get all charts");
				List<Chart> charts=new ArrayList<>();
				for(Chart chart: chartDao.findAll()){
					charts.add(chart);
				}
				allDetailedCharts=ChartView.detailedViewListOf(charts);
			}
			return allDetailedCharts;
		}
	@RequestMapping(method = RequestMethod.GET,value = {"/listcharts"})
	@ResponseBody
	public List<Chart> listCharts() {
		if(allCharts==null||allCharts.isEmpty()){
			allCharts=new ArrayList<>();
			for(Chart chart: chartDao.findAll()){
				allCharts.add(chart);
				}
			}
		return allCharts;
	}
	/**
	 * 一键部署一个Chart组件
	 * @param deployItemInfo
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST,value={ "/deploy" })
	@ResponseBody
	public Chart deployChart( @RequestBody DeployItemInfo deployItemInfo) {
		Chart chart=chartDao.findOne((long) deployItemInfo.id);
		Application application=K8sUtil.deployComByName(chart.getName(),deployItemInfo.name,chart);
		if(application!=null){
			applicationDAO.save(application);
			for(Container container:application.getContainers()){
				containerDAO.save(container);
			}
		}else{
			logger.error("部署失败！");
		}
		return chart;
	}
	/**
	 * 获取组件详细
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = { "/get/{chartId}" }, method = RequestMethod.GET)
	@ResponseBody
	public ChartView.DetailedChart getChart(
			@PathVariable("chartId") Long id) {
		Chart chart = chartDao.findOne(id);
		return ChartView.detailedViewOf(chart);
	}
	/**
	 * 获取组件详细
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = { "/delete/{chartId}" }, method = RequestMethod.DELETE)
	@ResponseBody
	public Iterable<Chart>  deleteChart(
			@PathVariable("chartId") Long id) {
		Chart chart=chartDao.findOne(id);
		final String comName=chart.getName();
		logger.info("delete the chart:"+comName);
		chartDao.delete(id);
		return chartDao.findAll();
	}
	/**
	 * 上传组件代码
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.POST ,value={"/upload"})
	@ResponseBody
	public List<Chart> uploadFile(HttpServletRequest request) throws Exception {
		FileOutputStream fos = null;
		InputStream in = null;
		List<Chart> charts = new ArrayList<>();
		request.setCharacterEncoding("utf-8");
		logger.info("组件保存位置:" + ConstantsUtil.COM_PATH);
		try {
			// 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
					request.getSession().getServletContext());
			// 检查form中是否有enctype="multipart/form-data"
			if (multipartResolver.isMultipart(request)) {
				// 将request变成多部分request
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				// 获取multiRequest 中所有的文件名
				Iterator iterator = multiRequest.getFileNames();
				String fileName = null;
				File file = null;
				final String comName = request.getParameter("comName");
				while (iterator.hasNext()) {
					MultipartFile multipartFile = multiRequest.getFile(iterator.next().toString());
					logger.info("文件名:" + multipartFile.getOriginalFilename() + ":" + multipartFile.getSize());
					in = multipartFile.getInputStream();
					fileName = multipartFile.getOriginalFilename();
					if (fileName.equalsIgnoreCase("deployment.yaml")) {
						file = new File(ConstantsUtil.COM_PATH + File.separator + comName + File.separator
								+ "deployment" + File.separator + multipartFile.getOriginalFilename());
					} else {
						file = new File(ConstantsUtil.COM_PATH + File.separator + comName + File.separator + "service"
								+ File.separator + multipartFile.getOriginalFilename());
					}
					if (!file.getParentFile().exists())
						file.getParentFile().mkdirs();
					if (!file.exists()) {// 如果文件不存在，则创建该文件
						file.createNewFile();
					}
					logger.info(file.getAbsolutePath());
					fos = new FileOutputStream(file);
					byte[] buff = new byte[1024];
					int len = 0;
					while ((len = in.read(buff)) > 0) {
						fos.write(buff, 0, len);
					}
				}
				logger.info("comName:" + comName);
				final String comVersion = request.getParameter("comVersion");
				logger.info("comVersion:" + comVersion);
				final String comDes = request.getParameter("comDes");
				logger.info("comDes:" + comDes);
				boolean has = false;
				for (Chart chart : chartDao.findAll()) {
					if (chart.getName().equals(comName)) {
						logger.info(chart.getName() + "重复组件");
						has = true;
					} else {
						charts.add(chart);
					}
				}
				if (has){
					return charts;
				}
				else {
					Chart chart = new Chart();
					chart.setName(comName);
					chart.setDescription(comDes);
					chart.setVersion(comVersion);
					chartDao.save(chart);
					for (Chart item : chartDao.findAll()) {
						if (item.getName().equals(comName)) {
							logger.info(item.getName() + "重复组件");
							has = true;
						} else {
							charts.add(item);
						}
					}
					return charts;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return charts;
	}

	/**
	 * 部署Chart
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = { "/deployById/{chartId}" }, method = RequestMethod.POST)
	@ResponseBody
	//@Transactional
	public ApplicationView.DetailedItem deployChart(
			@PathVariable("chartId") Long id) {
		return ApplicationView.detailedViewOf(appService.deployApplication(id));
	}
	/**
	 * 初始化组件数据,用于临时测试使用
	 * @param id
	 * @return
	 */
	@RequestMapping(value = { "/init" }, method = RequestMethod.GET)
	@ResponseBody
	public Iterable<Chart> initData() {
		//Chart mysql=new Chart("mysql","0.3.0","基础数据库组件，快速，可靠，可伸缩，基于开源");
		/*ServiceList serviceList=K8sUtil.getAllServices("default");
		for(Service service: serviceList.getItems()){
			Chart tomcat=new Chart(service.getMetadata().getName(),
					service.getApiVersion(),
					"火车票销售系统子服务:"+service.getMetadata().getName(), "应用软件");
			chartDao.save(tomcat);
		}*/
		return chartDao.findAll();
	}
}

