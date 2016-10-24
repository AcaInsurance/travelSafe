package com.aca.travelsafe.Interface;

/**
 * Created by marsel on 23/5/2016.
 */
public interface SyncListener {
    public void syncSucceed(boolean status);
    public void syncFailed(String message);
}
