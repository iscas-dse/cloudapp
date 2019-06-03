package cn.ac.iscas.cloudapp.agent.transaction;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.orbitz.consul.Consul;

public class PropagationServiceImpl implements PropagationService.Iface{
	private static Logger logger = LoggerFactory.getLogger(PropagationServiceImpl.class);
	
	private Consul consul;
	private PropagationEvent event;
	
	public PropagationServiceImpl(Consul consul) {
		this.consul = consul;
	}
	
	public PropagationServiceImpl() {}

	@Override
	public PropagationResult propagate(PropagationEvent event) throws TException {
		logger.info("propagate method called event{} from thead {}", event, Thread.currentThread());
		this.event = event;
		PropagationResult result = new PropagationResult(PropagationResultCode.EVENT_ACK, "success");
		return result;
	}

	@Override
	public PropagationResult prepare() throws TException {
		// TODO Auto-generated method stub
		logger.info("prepare method called event {} from thead {}", event, Thread.currentThread());
		PropagationResult result = new PropagationResult(PropagationResultCode.EVENT_ACK, "success");
//		PropagationResult result = new PropagationResult(PropagationResultCode.ROLLBACK,"rollback");
		return result;
	}

	@Override
	public PropagationResult commit(boolean onePhase) throws TException {
		// TODO Auto-generated method stub
		logger.info("commit method called event {} from thead {}", event, Thread.currentThread());
		consul.keyValueClient().putValue(event.eventKey, event.eventValue);
		PropagationResult result = new PropagationResult(PropagationResultCode.EVENT_ACK, "success");
		return result;
	}

	@Override
	public PropagationResult rollback() throws TException {
		logger.info("rollback method called event {} from thead {}", event, Thread.currentThread());
		PropagationResult result = new PropagationResult(PropagationResultCode.EVENT_ACK, "success");
		return result;
	}
	
	
}