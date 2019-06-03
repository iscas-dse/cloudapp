package cn.ac.iscas.cloudapp.agent.transaction;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Dictionary;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atomikos.icatch.DataSerializable;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.SysException;

public class PropagationListener implements Participant, DataSerializable{
	private static Logger logger = LoggerFactory.getLogger(PropagationListener.class);
	private String remoteAddr;
	private int remotePort;
	private transient PropagationConnector connection;
	private HeuristicMessage[] msgs;
	private String msg_;
	
	public boolean fireEvent(PropagationEvent event){
		try {
			PropagationResult result = connection.propagate(event);
			logger.info("fire event {} , get result: {}: {}", event.getEventKey(), result.getInfo(), result.getCode());
			if(result.getCode() == PropagationResultCode.EVENT_ACK) return true;
		} catch (TException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean recover() throws SysException {
		return true;
	}
	@Override
	public String getURI() {
		return this.remoteAddr + ":" + this.remotePort;
	}
	@Override
	public void setCascadeList(Dictionary allParticipants) throws SysException {
	}
	@Override
	public void setGlobalSiblingCount(int count) {
	}
	@Override
	public int prepare() throws RollbackException, HeurHazardException, HeurMixedException, SysException{
		try {
			PropagationResult result = connection.prepare();
			msgs = new HeuristicMessage[1];
			msgs[0] = new StringHeuristicMessage(result.info);
			switch(result.code){
			case ROLLBACK:
				throw new RollbackException(result.info);
			case HEUR_HAZARD:
				throw new HeurHazardException(msgs);
			case HEUR_MIXED:
				throw new HeurMixedException(msgs);
			case SYSTEM_FAILED:
				throw new SysException(result.info);
			case EVENT_ACK:
				return READ_ONLY + 1;
			}
		} catch (TException e) {
			e.printStackTrace();
			throw new SysException("connection failed", e);
		}
		throw new SysException("prepare failed");
	}
	@Override
	public HeuristicMessage[] commit(boolean onePhase)
			throws HeurRollbackException, HeurHazardException, HeurMixedException, RollbackException, SysException {
		try {
			PropagationResult result = connection.commit(onePhase);
			msgs = new HeuristicMessage[1];
			msgs[0] = new StringHeuristicMessage(result.info);
			switch(result.code){
			case ROLLBACK:
				throw new RollbackException(result.info);
			case HEUR_HAZARD:
				throw new HeurHazardException(msgs);
			case HEUR_MIXED:
				throw new HeurMixedException(msgs);
			case SYSTEM_FAILED:
				throw new SysException(result.info);
			case EVENT_ACK:
				return msgs = null;
			default:
				break;
			}
		} catch (TException e) {
			e.printStackTrace();
		}
		throw new SysException("commit failed");
	}
	@Override
	public HeuristicMessage[] rollback()
			throws HeurCommitException, HeurMixedException, HeurHazardException, SysException {
		try {
			PropagationResult result = connection.rollback();
			HeuristicMessage[] msgs = new HeuristicMessage[1];
			msgs[0] = new StringHeuristicMessage(result.info);
			switch(result.code){
			case HEUR_COMMIT:
				throw new HeurCommitException(msgs);
			case HEUR_HAZARD:
				throw new HeurHazardException(msgs);
			case HEUR_MIXED:
				throw new HeurMixedException(msgs);
			case SYSTEM_FAILED:
				throw new SysException(result.info);
			case EVENT_ACK:
				return msgs = null;
			default:
				break;
			}
		} catch (TException e) {
			e.printStackTrace();
		}
		throw new SysException("rollback failed");
	}
	@Override
	public void forget() {
	}
	@Override
	public HeuristicMessage[] getHeuristicMessages() {
		return msgs;
	}
	
	public String getRemoteAddr() {
		return remoteAddr;
	}
	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}
	public int getRemotePort() {
		return remotePort;
	}
	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}
	public PropagationListener(String remoteAddr, int remotePort) {
		this.remoteAddr = remoteAddr;
		this.remotePort = remotePort;
		connection = PropagationConnector.getConnection(remoteAddr, remotePort);
	}

	public void writeData(DataOutput out) throws IOException {
		out.writeUTF(msg_.toString());
		
	}

	public void readData(DataInput in) throws IOException {
		msg_=new String(in.readUTF());
	}

	public void close() {
		connection.close();
	}
	
}
