// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.serverengines.mahogany;

import com.serverengines.mahogany.CConn;
import com.serverengines.resmgr.*;
import com.serverengines.kvm.*;
import java.util.*;
import java.io.IOException;

public class MahoganyViewer
    implements Runnable
{
    public MahoganyViewer(String as[])
    {
        viewOnly = new BoolParameter("ViewOnly", "Don't send any mouse or keyboard events to the server", false);
        alwaysShowServerDialog = new BoolParameter("AlwaysShowServerDialog", "Always show the server dialog even if a server has been specified in an applet parameter or on the command line", false);
        mahoganyLogging = new BoolParameter("logging", "Turn logging messages on", false);
        mahoganyServerName = new StringParameter("ipaddress", "The Pilot server <host>", as[1]);
        mahoganyServerPasswd = new StringParameter("httpdata", "The Mahogany User Passowrd", as[4]);
        mahoganyServerUserName = new StringParameter("username", "The Mahogany User Name", as[3]);
        mahoganySessionType = new StringParameter("sessiontype", "The Mahogany Session Type", "kvm");
        mahoganyStorageServerPort = new IntParameter("port", "The Mahogany storage server port number", new Integer(as[2]));
        mahoganyNonSecureKVMPort = new IntParameter("NonSecure_KVMPort", "The Mahogany non secure KVM port number", 80);
        mahoganyNonSecureKMPort = new IntParameter("NonSecure_KMPort", "The Mahogany non secure KM port number", 0);
        mahoganyNonSecureVPort = new IntParameter("NonSecure_VPort", "The Mahogany non secure V port number", 0);
        mahoganySSLVMPort = new IntParameter("SSL_VMPort", "The Mahogany SSL VM port number", 0);
        mahoganySSLKPort = new IntParameter("SSL_KPort", "The Mahogany SSL K port number", 0);

        einstellungISO = new StringParameter("iso", "Einstellung ISO image to redirect", as[0]);
        einstellungServerName = new StringParameter("selfip", "Einstellung ethernet interface listen to", as[5]);

        AppResMgr appresmgr = AppResMgr.getInstance();
        mahoganyNonSecureKVMPort.setParam(appresmgr.getResourceString("kvms.port"));
        mahoganyNonSecureKMPort.setParam(appresmgr.getResourceString("keyboard.port"));
        mahoganyNonSecureVPort.setParam(appresmgr.getResourceString("kvms.port"));
        mahoganySSLVMPort.setParam(appresmgr.getResourceString("kvms.port"));
        mahoganySSLKPort.setParam(appresmgr.getResourceString("keyboard.ssl.port"));

        if (as.length >= 6) {
            try {
                maxkb = Integer.parseInt(as[6]);
            }
            catch(NumberFormatException numberformatexception6) {
                System.err.println("Wrong arg 6");
                System.exit(2);
            }
        }

        if (as.length == 7) {
            try {
                timeout = Integer.parseInt(as[7]);
            }
            catch(NumberFormatException numberformatexception7) {
                System.err.println("Wrong arg 7");
                System.exit(2);
            }
        }

        cc = new CConn(this);
    }

    public void start()
    {
        Thread thread = new Thread(this);
        try {
            thread.start();
            while (cc.m_isRunning)
                Thread.sleep(1000);
        } catch (InterruptedException e) {}
    }

    public void run()
    {
        cc.m_isRunning = true;
        int exit = 1;
        try {
            if (cc.init(mahoganyServerName.getValue(),
                        mahoganyServerUserName.getValue(),
                        mahoganyServerPasswd.getValue(),
                        false,
                        mahoganySessionType.getValue(),
                        mahoganyStorageServerPort.getValue(),
                        mahoganyNonSecureKVMPort.getValue(),
                        mahoganyNonSecureKMPort.getValue(),
                        mahoganyNonSecureVPort.getValue(),
                        mahoganySSLVMPort.getValue(),
                        mahoganySSLKPort.getValue())) {
                cc.startReceiverThread();
                exit = 0;
                boolean mounted = false;
                boolean fullcontrol = false;
                int kb = maxkb / 2;
                int waitfor = timeout*60;
                try {
                    while (waitfor > 0 && cc.m_isRunning) {
                        Thread.sleep(1000);
                        waitfor--;
                        if (maxkb != -1 && maxkb < kb)
                            break;
                        if (MessageSender.isHandshakeComplete() && !fullcontrol) {
                            MessageSender.requestPrimaryControl();
                            fullcontrol = true;
                        } else if (MessageSender.isHandshakeComplete() && cc.isStorageEnabled() && !mounted) {
                            cc.startDeviceDiscovery();
                            Thread.sleep(3000);
                            cc.mount();
                            mounted = true;
                        }
                    }
                    if (waitfor == 0 && kb < (maxkb / 2)) {
                        /* FIXME: there is no standard way to count bytes, just exit */
                        exit = 0;
                    }
                } catch (java.lang.InterruptedException exception) { System.out.println("interrupted"); }
            }
        } catch (java.io.IOException exception) { System.out.println(exception); }
        cc.umount();
        cc.setExitProperly(true);
        cc.close();
        System.exit(exit);
    }

    static final long serialVersionUID = 0xe276e932e096fdb2L;
    public static final String PARAM_IP_ADDRESS = "ipaddress";
    public static final String PARAM_USER_NAME = "username";
    public static final String PARAM_PASSWORD = "httpdata";
    public static final String PARAM_STORAGE_PORT = "port";
    public static final String PARAM_NONSECURE_KVM_PORT = "NonSecure_KVMPort";
    public static final String PARAM_NONSECURE_KM_PORT = "NonSecure_KMPort";
    public static final String PARAM_NONSECURE_V_PORT = "NonSecure_VPort";
    public static final String PARAM_SSL_VM_PORT = "SSL_VMPort";
    public static final String PARAM_SSL_K_PORT = "SSL_KPort";
    public static final String PARAM_SESSION_TYPE = "sessiontype";
    public static final String PARAM_LOGGING = "logging";
    public static final String NONSECURE_CONNECTION = "kvm";
    public static final String SECURE_CONNECTION = "kvmssl";
    public static final long SLEEP_TIME = 500L;
    public BoolParameter viewOnly;
    public BoolParameter alwaysShowServerDialog;
    public BoolParameter mahoganyLogging;
    public StringParameter mahoganyServerName;
    public StringParameter mahoganyServerPasswd;
    public StringParameter mahoganyServerUserName;
    public StringParameter mahoganySessionType;
    public StringParameter einstellungServerName;
    public StringParameter einstellungISO;
    public IntParameter mahoganyStorageServerPort;
    public IntParameter mahoganyNonSecureKVMPort;
    public IntParameter mahoganyNonSecureKMPort;
    public IntParameter mahoganyNonSecureVPort;
    public IntParameter mahoganySSLVMPort;
    public IntParameter mahoganySSLKPort;
    public static boolean applet = false;

    private static int maxkb = -1;
    private static int timeout = 5;

    protected CConn cc;
}
