package com.cmcm.adsdk.adapter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;



public class GooglePlayServiceConnection implements ServiceConnection {

	boolean CQ = false;
	private final BlockingQueue<IBinder> CR = new LinkedBlockingQueue();
	  
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		 try
		    {
		      this.CR.put(service);
		    }
		    catch (InterruptedException localInterruptedException)
		    {
		    }
		
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		
	}
	public IBinder getConnectedBinder()
		    throws InterruptedException
		  {
		    if (this.CQ)
		      throw new IllegalStateException();
		    this.CQ = true;
		    return (IBinder)this.CR.take();
		  }
	
}