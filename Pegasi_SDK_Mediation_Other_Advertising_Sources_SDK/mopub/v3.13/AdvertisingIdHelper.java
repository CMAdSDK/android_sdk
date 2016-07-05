package com.cmcm.picks.gaid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.TextUtils;

import com.cmcm.adsdk.CMAdManager;
import com.cmcm.picks.init.PicksMob;


public class AdvertisingIdHelper {
    static GooglePlayServiceConnection connection(Context paramContext) {
        try {
            PackageManager packageManager = paramContext.getPackageManager();
            packageManager.getPackageInfo("com.android.vending", 0);
        } catch (Exception e) {
            return null;
        }
        try{
            GooglePlayServiceConnection myServiceConnection = new GooglePlayServiceConnection();
            Intent localIntent = new Intent(
                    "com.google.android.gms.ads.identifier.service.START");
            localIntent.setPackage("com.google.android.gms");
            if (paramContext.bindService(localIntent, myServiceConnection, 1))
                return myServiceConnection;
        }catch (Exception e){
            return null;
        }
        return null;
    }

    private static class a implements AdvertisingIdInterface {
        private IBinder kq;

        a(IBinder paramIBinder) {
            this.kq = paramIBinder;
        }

        public IBinder asBinder() {
            return this.kq;
        }

        public String getId() throws RemoteException {
            Parcel localParcel1 = Parcel.obtain();
            Parcel localParcel2 = Parcel.obtain();
            String str;
            try {
                localParcel1
                        .writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                this.kq.transact(1, localParcel1, localParcel2, 0);
                localParcel2.readException();
                str = localParcel2.readString();
            } finally {
                localParcel2.recycle();
                localParcel1.recycle();
            }
            return str;
        }

        public boolean isLimitAdTrackingEnabled(boolean paramBoolean) throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            boolean limitAdTracking = false;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                data.writeInt(paramBoolean ? 1 : 0);
                this.kq.transact(2, data, reply, 0);
                reply.readException();
                limitAdTracking = 0 != reply.readInt();
            } catch (Exception e){

            }finally {
                reply.recycle();
                data.recycle();
            }
            return limitAdTracking;
        }

    }

    public static IInterface getIdInterface(IBinder paramIBinder) {
        if (paramIBinder == null)
            return null;
        IInterface localIInterface = paramIBinder
                .queryLocalInterface("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
        if ((localIInterface != null)
                && ((localIInterface instanceof AdvertisingIdInterface)))
            return localIInterface;
        return new a(paramIBinder);
    }

    /*	public void getId(Context context) {

            Info adInfo = null;
            try {
                adInfo = AdvertisingIdClient
                        .getAdvertisingIdInfo(context);

            } catch (Exception e) {
                // Unrecoverable error connecting to Google Play services (e.g.,
                // the old version of the service doesn't support getting
                // AdvertisingId).

            }
            final String id = adInfo.getId();
            final boolean isLAT = adInfo.isLimitAdTrackingEnabled();
            Log.e("bbc", "id2" + id);
        }
        */
    private String mGAId = "";
    private boolean mTrackFlag = false;
    public String getGAId()
    {
        return mGAId;

    }
    public boolean getTrackFlag() {
        return mTrackFlag;
    }


    public void asyncGetGAId(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Context context = CMAdManager.getContext();
                GooglePlayServiceConnection conn = connection(context);

                if(conn == null)
                    return;
                String adid = null;
                boolean track = false;
                try {
                    AdvertisingIdInterface idInterface = (AdvertisingIdInterface) getIdInterface(conn
                            .getConnectedBinder());

                    adid = idInterface.getId();
                    track = idInterface.isLimitAdTrackingEnabled(false);
                }  catch (Exception e) {

                }finally {
                    try{
                        if(null != conn){
                            context.unbindService(conn);
                        }
                    }
                    catch (Exception localIllegalArgumentException2){}
                }
                if (!TextUtils.isEmpty(adid)) {
                    synchronized(mGAId){
                        mGAId = adid;
                        mTrackFlag = track;
                    }
                }
            }
        }).start();
    }
    private static AdvertisingIdHelper instance = null;
    public static AdvertisingIdHelper getInstance(){
        if(instance == null){
            instance = new AdvertisingIdHelper();
        }
        return instance;

    }
    private AdvertisingIdHelper(){
    }
}
