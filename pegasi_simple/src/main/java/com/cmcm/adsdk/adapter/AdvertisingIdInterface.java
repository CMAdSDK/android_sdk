package com.cmcm.adsdk.adapter;

import android.os.IInterface;
import android.os.RemoteException;

public abstract interface AdvertisingIdInterface extends IInterface
{
	
	public abstract String getId()
		    throws RemoteException;
    public abstract boolean isLimitAdTrackingEnabled(boolean paramBoolean)
            throws RemoteException;
}