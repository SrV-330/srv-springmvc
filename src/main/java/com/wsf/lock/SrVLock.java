package com.wsf.lock;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

public class SrVLock implements Lock{

	private AtomicReference<Thread> ar=new AtomicReference<Thread>(null);
	private LinkedBlockingQueue<Thread> lbq=new LinkedBlockingQueue<Thread>();
	public SrVLock(){
		
	}
	
	public void lock() {
		while(!ar.compareAndSet(null,Thread.currentThread())){
			
			lbq.add(Thread.currentThread());
			LockSupport.park();
			lbq.remove(Thread.currentThread());
		}
		
		
	}
	public void unlock() {
		if(ar.compareAndSet(Thread.currentThread(),null)){
			Object[] threads=lbq.toArray();
			for(Object t:threads){
				LockSupport.unpark((Thread)t);
			}
		}
		
	}
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	public boolean tryLock() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	

	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}

}
