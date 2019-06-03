package cn.ac.iscas.cloudapp.agent.transaction;

import java.util.Properties;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;

@Component
public class PropagationTransactionManager {
	private UserTransactionService uts;
	private CompositeTransactionManager ctm;
	private int timeout;
	
	private static class UserTransactionServiceHolder{
		static UserTransactionService holder;
		static{
			holder = new UserTransactionServiceImp();
			Properties properties = new Properties();
			properties.setProperty("com.atomikos.icatch.threaded_2pc", "true");
			holder.init(properties);
		}
	}
	
	@Autowired
	public PropagationTransactionManager(@Value("#{configs['transaction_timeout'] ?: 60}") int timeout){
		uts = UserTransactionServiceHolder.holder;
		ctm = uts.getCompositeTransactionManager();
		this.timeout = timeout;
	}
	
	public CompositeTransaction createTransaction(long timeout){
		return ctm.createCompositeTransaction(timeout);
	}
	
	public CompositeTransaction createTransaction(){
		return ctm.createCompositeTransaction(timeout * 1000);
	}
	
	public CompositeTransaction getTransaction(){
		return ctm.getCompositeTransaction();
	}
	
	@PreDestroy
	public void preDestory(){
		uts.shutdown(true);
	}
}
