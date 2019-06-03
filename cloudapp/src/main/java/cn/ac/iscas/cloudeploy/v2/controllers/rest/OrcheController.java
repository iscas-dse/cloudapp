package cn.ac.iscas.cloudeploy.v2.controllers.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.ac.iscas.cloudeploy.v2.model.entity.task.TaskEdge.Relation;


@Controller
@RequestMapping(value = "v2/orche")
public class OrcheController {
	
	class OrcheRelations{
	
		public String src;
		public String des;
		public double score;
		public OrcheRelations(String src, String des, double score) {
			this.src=src;
			this.des=des;
			if(org.apache.commons.lang3.StringUtils.equalsIgnoreCase(src, des)){
				this.score=0.0123;
			}else{
				this.score=score;
			}
		}
	}
	double getRandom(double scores){
		Random random=new Random();
		if(random.nextBoolean()){
			scores+=random.nextDouble()*random.nextInt(8);
		}else{
			scores+=random.nextDouble()*random.nextInt(8)*-1.0;
		}
		if(scores<0){
			scores*=-1.0;
		}
		while(scores>100.0){
			scores-=random.nextDouble()*random.nextInt(10);
		}
		return scores;
	}
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<OrcheRelations> getScores() {
		List<OrcheRelations> orcheRelations=new ArrayList<>();
		
		orcheRelations.add(new OrcheRelations("front-end", "carts", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("front-end", "catalogue", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("front-end", "user", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("front-end", "orders", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("front-end", "payment", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("front-end", "shipping", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("catalogue", "catalogue-db", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("user", "user-db", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("carts", "carts-db", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("orders", "orders-db", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("shipping", "rabbitmq", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("rabbitmq", "queue-master", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("tomcat", "mysql", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("tale", "mysql", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("schoolapp", "mysql", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("mysql", "mysql", getRandom(00.23) ));
		//-----------------------------------------
		orcheRelations.add(new OrcheRelations( "carts","front-end", getRandom(10.23)));
		orcheRelations.add(new OrcheRelations( "catalogue","front-end", getRandom(10.23)));
		orcheRelations.add(new OrcheRelations("user","front-end",  getRandom(10.23)));
		orcheRelations.add(new OrcheRelations("orders","front-end",  getRandom(10.23)));
		orcheRelations.add(new OrcheRelations("payment","front-end",  getRandom(10.23)));
		orcheRelations.add(new OrcheRelations( "shipping","front-end", getRandom(10.23)));
		orcheRelations.add(new OrcheRelations("catalogue-db","catalogue",  getRandom(10.23)));
		orcheRelations.add(new OrcheRelations("user-db","user",  getRandom(10.23)));
		orcheRelations.add(new OrcheRelations( "orders-db","orders", getRandom(10.23)));
		orcheRelations.add(new OrcheRelations( "rabbitmq","shipping", getRandom(10.23)));
		orcheRelations.add(new OrcheRelations("queue-master","rabbitmq",  getRandom(10.23)));
		orcheRelations.add(new OrcheRelations("mysql", "tomcat", getRandom(10.23)));
		orcheRelations.add(new OrcheRelations("mysql", "tale", getRandom(10.23)));
		orcheRelations.add(new OrcheRelations("mysql","schoolapp",  getRandom(10.23)));
		//----------------------------------------------trainticket

		
		//确定----------------------------------------------------------------------------
		orcheRelations.add(new OrcheRelations("ts-ticketinfo-service", "ts-basic-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-ticketinfo-service", "ts-price-service", getRandom(80.23)));
		
		orcheRelations.add(new OrcheRelations("ts-login-service", "ts-account-mongo", getRandom(80.23)));
		
		
		orcheRelations.add(new OrcheRelations("ts-seat-service", "ts-travel-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-seat-service", "ts-order-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-seat-service", "ts-order-other-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-seat-service", "ts-config-service", getRandom(80.23)));
		
		orcheRelations.add(new OrcheRelations("ts-route-plan-service", "ts-route-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-route-plan-service", "ts-travel-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-route-plan-service", "ts-travel2-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-route-plan-service", "ts-station-service", getRandom(80.23)));
		
		orcheRelations.add(new OrcheRelations("ts-register-service", "ts-verification-code-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-register-service", "ts-sso-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-register-service", "ts-inside-payment-service", getRandom(80.23)));
		
		orcheRelations.add(new OrcheRelations("ts-rebook-service", "ts-seat-service", getRandom(80.23)));
		
		orcheRelations.add(new OrcheRelations("ts-preserve-other-service", "ts-ticketinfo-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-other-service", "ts-seat-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-other-service", "ts-notification-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-other-service", "ts-sso-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-other-service", "ts-assurance-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-other-service", "ts-station-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-other-service", "ts-travel2-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-other-service", "ts-contacts-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-other-service", "ts-order-other-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-other-service", "ts-food-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-other-service", "ts-consign-service", getRandom(80.23)));
		
		
		
		
		orcheRelations.add(new OrcheRelations("ts-preserve-service", "ts-ticketinfo-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-service", "ts-seat-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-service", "ts-notification-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-service", "ts-sso-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-service", "ts-assurance-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-service", "ts-station-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-service", "ts-security-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-service", "ts-travel-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-service", "ts-contacts-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-service", "ts-order-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-service", "ts-food-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-preserve-service", "ts-consign-service", getRandom(80.23)));
		
		
		
		orcheRelations.add(new OrcheRelations("ts-login-service", "ts-sso-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-login-service", "ts-verification-code-service", getRandom(80.23)));
		
		
		orcheRelations.add(new OrcheRelations("ts-login-service", "ts-verification-code-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-login-service", "ts-sso-service", getRandom(80.23)));
		
		orcheRelations.add(new OrcheRelations("ts-execute-service", "ts-order-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-execute-service", "ts-order-other-service", getRandom(80.23)));
		
		orcheRelations.add(new OrcheRelations("ts-consign-service", "ts-consign-price-service", getRandom(80.23)));
		
		orcheRelations.add(new OrcheRelations("ts-cancel-service", "ts-notification-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-cancel-service", "ts-sso-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-cancel-service", "ts-order-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-cancel-service", "ts-order-other-service", getRandom(80.23)));
		
		
		orcheRelations.add(new OrcheRelations("ts-basic-service", "ts-station-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-basic-service", "ts-route-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-basic-service", "ts-price-service", getRandom(80.23)));
		
		orcheRelations.add(new OrcheRelations("ts-admin-user-service", "ts-sso-service", getRandom(80.23)));
		
		orcheRelations.add(new OrcheRelations("ts-admin-travel-service", "ts-travel-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-admin-travel-service", "ts-travel2-service", getRandom(80.23)));
		
		orcheRelations.add(new OrcheRelations("ts-admin-route-service", "ts-route-service", getRandom(80.23)));
		
		
		orcheRelations.add(new OrcheRelations("ts-admin-order-service", "ts-order-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-admin-order-service", "ts-order-other-service", getRandom(80.23)));
		
		
		orcheRelations.add(new OrcheRelations("ts-admin-basic-info-service", "ts-contacts-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-admin-basic-info-service", "ts-station-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-admin-basic-info-service", "ts-train-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-admin-basic-info-service", "ts-config-service", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-admin-basic-info-service", "ts-price-service", getRandom(80.23)));
		
		orcheRelations.add(new OrcheRelations("ts-inside-payment-service", "ts-inside-payment-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-contacts-service", "ts-contacts-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-food-map-service", "ts-food-map-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-food-service", "ts-food-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-assurance-service", "ts-assurance-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-consign-price-service", "ts-consign-price-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-order-service", "ts-order-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-order-other-service", "ts-order-other-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-payment-service", "ts-payment-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-price-service", "ts-price-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-ticket-office-service", "ts-ticket-office-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-security-service", "ts-security-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-station-service", "ts-station-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-train-service", "ts-train-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-config-service", "ts-config-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-route-service", "ts-route-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-travel-plan-service", "ts-travel-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-travel-service", "ts-travel2-service", getRandom(80.23)));
			orcheRelations.add(new OrcheRelations("ts-travel2-service", "ts-travel2-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-voucher-service", "ts-voucher-mysql", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-voucher-service", "ts-voucher-mysql", getRandom(80.23)));
		//入口
		orcheRelations.add(new OrcheRelations("ts-verification-code-service", "ts-verification-code-mongo", getRandom(80.23)));
		orcheRelations.add(new OrcheRelations("ts-ui-dashboard", "redis", getRandom(80.23)));
		

		
		return orcheRelations;
	}
}
