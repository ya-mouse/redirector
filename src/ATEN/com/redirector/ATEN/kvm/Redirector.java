package com.redirector.ATEN.kvm;

import tw.com.aten.bean.*;
import tw.com.aten.vstorage.VirtualStorage;
import tw.com.aten.ikvm.jni.RMConnection;
import java.lang.InterruptedException;

public class Redirector
{
    public static void main(String as[])
    {
        vs = new VirtualStorage();
        vs.main(as);
    }

    private static VirtualStorage vs;

    static
    {
        String s1 = System.getProperty("os.arch");
        try
        {
            if(s1.indexOf("64") != -1)
            {
                System.loadLibrary("iKVM".concat("64"));
                System.loadLibrary("SharedLibrary".concat("64"));
            } else
            {
                System.loadLibrary("iKVM".concat("32"));
                System.loadLibrary("SharedLibrary".concat("32"));
            }
            RMConnection.init(tw.com.aten.bean.ConnInfo.class, tw.com.aten.bean.UserInfo.class);
        }
        catch(UnsatisfiedLinkError unsatisfiedlinkerror)
        {
            System.exit(3);
        }
    }
}
