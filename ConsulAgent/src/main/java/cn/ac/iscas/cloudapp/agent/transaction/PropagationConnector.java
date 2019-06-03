package cn.ac.iscas.cloudapp.agent.transaction;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class PropagationConnector {
	private static Logger logger = LoggerFactory.getLogger(PropagationConnector.class);
	private TTransport transport;
	private PropagationService.Client client;
	private boolean isConnected;
	
	public static PropagationConnector getConnection(String remoteIp, int port){
		return new PropagationConnector(remoteIp, port);
	}
	
	public PropagationConnector(String remoteIp, int port){
		transport = new TSocket(remoteIp, port);
		isConnected = false;
		logger.info("construct connector with {}:{} success", remoteIp, port);
	}
	
	public void connect() throws TTransportException{
		if(isConnected) return;
		transport.open();
		TProtocol protocol = new TBinaryProtocol(transport);
		client = new PropagationService.Client(protocol);
		isConnected = true;
		logger.info("connector has been connected");
	}
	
	public void close(){
		transport.close();
		client = null;
		logger.info("connector has been closed");
	}
	
	public PropagationResult propagate(PropagationEvent event) throws TException{
		Preconditions.checkNotNull(event, "event can't be null");
		if(!isConnected) connect();
		try {
			return client.propagate(event);
		} catch (TException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}
	
	public PropagationResult prepare() throws TException{
		logger.info("prepare");
		if(!isConnected) connect();
		return client.prepare();
	}

	public PropagationResult commit(boolean onePhase) throws TException {
		if(!isConnected) connect();
		return client.commit(onePhase);
	}

	public PropagationResult rollback() throws TException {
		if(!isConnected) connect();
		return client.rollback();
	}
}
