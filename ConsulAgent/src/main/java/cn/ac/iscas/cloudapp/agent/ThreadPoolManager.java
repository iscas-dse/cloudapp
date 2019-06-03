package cn.ac.iscas.cloudapp.agent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

@Component
public class ThreadPoolManager {
	private static final int THREADNUM = 10;
	private ExecutorService pool;
	
	public ThreadPoolManager(){
		pool = Executors.newFixedThreadPool(THREADNUM);
	}
	
	public void shutdown(){
		pool.shutdown();
	}

	public ExecutorService getPool() {
		return pool;
	}

	public void setPool(ExecutorService pool) {
		this.pool = pool;
	}	
	
}
