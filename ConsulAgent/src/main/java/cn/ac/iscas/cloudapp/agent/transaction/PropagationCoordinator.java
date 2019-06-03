package cn.ac.iscas.cloudapp.agent.transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.google.common.base.Preconditions;
import com.orbitz.consul.Consul;

import cn.ac.iscas.cloudapp.agent.util.ForEachHelper;

@Component
public class PropagationCoordinator{
	
	private int listenPort;
	
	private transient Runnable serverThread;
	
	@Autowired
	private PropagationTransactionManager ptm;
	
	@Autowired
	private PropagationListenerFactory factory;
	
	@Autowired
	private Consul consul;
	
	private static Logger logger = LoggerFactory.getLogger(PropagationCoordinator.class);
	
	private transient ExecutorService pool = Executors.newCachedThreadPool();
	
	@Autowired
	public PropagationCoordinator(@Value("#{configs['coordinator_port'] ?: 9002}") int port){
		this.listenPort = port;
	}
	
	@PostConstruct
	private void startCoordinator(){
		this.start();
	}
	
	public boolean firePropagationEvent(final PropagationEvent event) throws IOException{
		Preconditions.checkNotNull(event, "can't fire a null event");
		List<PropagationListener> listeners = factory.getListeners(event);
		listeners.add(new PropagationListener("localhost", listenPort));
		CompositeTransaction tx = ptm.createTransaction();
		for(PropagationListener listener : ForEachHelper.of(listeners)){
			tx.addParticipant(listener);
			logger.info("add participant to transaction: {}", listener.getURI());
		}
		List<Callable<Boolean>> todo = addTasks(event, listeners);
		boolean err = executeTasks(todo);
		
		if(!err){
			try {
				tx.commit();
				return true;
			} catch (SysException | SecurityException | HeurRollbackException | HeurMixedException | HeurHazardException
					| RollbackException e) {
				e.printStackTrace();
			}finally{
				closeListeners(listeners);
			}
		}else{
			tx.rollback();
			closeListeners(listeners);
		}
		return false;
	}

	private void closeListeners(List<PropagationListener> listeners) {
		for(PropagationListener listener : ForEachHelper.of(listeners)){
			listener.close();
		}
	}

	private boolean executeTasks(List<Callable<Boolean>> todo) {
		boolean allVotes = true;
		try {
			List<Future<Boolean>> answers = pool.invokeAll(todo);
			for(Future<Boolean> future : answers){
				allVotes = allVotes && future.get();
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return !allVotes;
	}

	private List<Callable<Boolean>> addTasks(final PropagationEvent event, List<PropagationListener> listeners) {
		List<Callable<Boolean>> todo = new ArrayList<>(listeners.size());
		for(final PropagationListener listener : ForEachHelper.of(listeners)){
			Callable<Boolean> callable = new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return listener.fireEvent(event);
				}
			};
			todo.add(callable);
		}
		return todo;
	}
	
	
	public void start(){
		if(serverThread == null){
			serverThread = new CoordinatorListenerThread();
			pool.submit(serverThread);
		}
	}
	
	public void shutdown(){
		pool.shutdown();
	}
	
	private class CoordinatorListenerThread implements Runnable{
		@Override
		public void run() {
			PropagationServiceImpl handler = new PropagationServiceImpl();
			PropagationService.Processor<PropagationServiceImpl> processor =
					new PropagationService.Processor<PropagationServiceImpl>(handler);
			try {
				TServerTransport serverTransport = new TServerSocket(listenPort);
				TThreadPoolServer.Args serverArgs=new TThreadPoolServer.Args(serverTransport);
				serverArgs.processorFactory(new TProcessorFactory(null){
					@Override
					public TProcessor getProcessor(TTransport trans) {
						PropagationServiceImpl handler = new PropagationServiceImpl(consul);
						return new PropagationService.Processor<PropagationServiceImpl>(handler);
					}
				});
				serverArgs.protocolFactory(new TBinaryProtocol.Factory());
				TServer server = new TThreadPoolServer(serverArgs);
				System.out.println("服务启动,端口号为" + listenPort);
				server.serve();
			} catch (TTransportException e) {
				e.printStackTrace();
			} 
		}
	}
}
