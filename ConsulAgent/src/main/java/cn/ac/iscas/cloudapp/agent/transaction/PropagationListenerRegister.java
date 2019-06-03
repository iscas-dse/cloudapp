package cn.ac.iscas.cloudapp.agent.transaction;
/**
 * register a propagationListener of a propagation event
 * @author RichardLcc
 */
public class PropagationListenerRegister {
	public static class Entity{
		private String service;
		private String serviceId;
		private String tag;
		public String getTag() {
			return tag;
		}
		public void setTag(String tag) {
			this.tag = tag;
		}
		public String getService() {
			return service;
		}
		public void setService(String service) {
			this.service = service;
		}
		public String getServiceId() {
			return serviceId;
		}
		public void setServiceId(String serviceId) {
			this.serviceId = serviceId;
		}
	}
}
