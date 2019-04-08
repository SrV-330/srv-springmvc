package com.wsf.lock;

import java.util.concurrent.CountDownLatch;

public class Client {
	private static SrVLock lock=new SrVLock();
	private static int var=0;
	private static CountDownLatch cdl=new CountDownLatch(100);
	public static void main(String[] args) {
		
		for(int i=0;i<100;i++){
			new Thread(){
				@Override
				public void run(){
					
					for(int i=0;i<10000;i++){
						lock.lock();
						var++;
						lock.unlock();
					}
					cdl.countDown();
					
				}
			}.start();
		}
		
		while(cdl.getCount()!=0);
		System.out.println(var);
		

	}

}
