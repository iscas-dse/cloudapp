package cn.ac.iscas.cloudapp.agent.transaction;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class PropagationConnectorTest {
	@Test
	public void test(){
		PropagationServiceImpl handler = new PropagationServiceImpl();
		PropagationService.Processor<PropagationServiceImpl> processor =
				new PropagationService.Processor<PropagationServiceImpl>(handler);
		try {
			int listenPort = 9001;
			TServerTransport serverTransport = new TServerSocket(listenPort );
			TThreadPoolServer.Args serverArgs=new TThreadPoolServer.Args(serverTransport);
			serverArgs.processorFactory(new TProcessorFactory(null){
				@Override
				public TProcessor getProcessor(TTransport trans) {
					PropagationServiceImpl handler = new PropagationServiceImpl();
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
	@Test
	public void test1() throws TException{
		PropagationConnector connection1 = PropagationConnector.getConnection("localhost", 9001);
		PropagationConnector connection2 = PropagationConnector.getConnection("localhost", 9001);
		PropagationEvent event1 = new PropagationEvent("connection1", "connection1");
		PropagationEvent event2 = new PropagationEvent("connection2", "connection1");
		connection1.propagate(event1);
		connection1.prepare();
		connection2.propagate(event2);
		connection1.prepare();
		connection2.prepare();
	}
}
