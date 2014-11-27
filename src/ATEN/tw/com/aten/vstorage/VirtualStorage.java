package tw.com.aten.vstorage;

import tw.com.aten.bean.*;
import tw.com.aten.ikvm.jni.RMConnection;
import tw.com.aten.ikvm.ui.RemoteVideo;
import java.lang.InterruptedException;

public class VirtualStorage
{
    private static final int DEVICE1 = 0;
    private static final int DEVICE2 = 1;
    private static final int DEVICE3 = 2;

    private class C_DevExistThread
        implements Runnable
    {

        public void run()
        {
            StartCheckDevExistThread();
        }

        private C_DevExistThread()
        {
        }

    }

    private class Dev1Thread
        implements Runnable
    {

        public void run()
        {
            StartDev1Thread();
        }

        private Dev1Thread()
        {
        }

    }

    private class Dev2Thread
        implements Runnable
    {

        public void run()
        {
            StartDev2Thread();
        }

        private Dev2Thread()
        {
        }

    }

    private class Dev3Thread
        implements Runnable
    {

        public void run()
        {
            StartDev3Thread();
        }

        private Dev3Thread()
        {
        }

    }

    public VirtualStorage()
    {
    }

    public void NFFillVMVersion(String s)
    {
    }

    public void NFGetVMCount(int i)
    {
    }

    public void NFChangeGUIObject(int i)
    {
    }

    public void NFGetMsgID(int i)
    {
    }

    public void main(String as[])
    {
        if (as.length < 7)
            System.exit(1);

        int iso_idx = -1;
        String iso_file    = as[0];
        String expected_kb = as[1];
        String timeout_s   = as[2];

        int rev = 1;
        if (as[3].equals("r2"))
            rev = 2;

        int i, j;
        String s, s1, s2, s3, s4, s5, s6;
        InfoRepository infoRepository = null;

        switch (rev) {
        case 1:
            s  = as[4];
            s1 = as[5];
            s2 = as[6];
            s3 = as[7];
            s4 = as.length <= 8 ? "ENG" : as[8];
            i = as.length <= 9 ? 0 : Integer.parseInt(as[9]);
            j = as.length <= 10 ? 0 : Integer.parseInt(as[10]);
            infoRepository = new InfoRepository();
            infoRepository.getConnInfo().setIp(s);
            infoRepository.getConnInfo().setPort(Integer.parseInt(s3));
            infoRepository.getUserInfo().setUserName(s1);
            infoRepository.getUserInfo().setPassword(s2);
            infoRepository.getUserInfo().setLanguage(s4);
            infoRepository.getVirtualUsbInfo().setPort(0, 0);
            infoRepository.getVirtualUsbInfo().setPort(1, 0);
            infoRepository.getVirtualUsbInfo().setPort(2, 0);
            infoRepository.getPlatformInfo().setCompanyId(i);
            infoRepository.getPlatformInfo().setBoardId(j);
            break;

        case 2: // 2.36+
            s  = as[4];
            s1 = as[5];
            s2 = as[6];
            s3 = as[7];
            s4 = as[8];
            s5 = as.length <= 9 ? "623" : as[9];
            i = as.length <= 10 ? 0 : Integer.parseInt(as[10]);
            j = as.length <= 11 ? 0 : Integer.parseInt(as[11]);
            s6 = as.length <= 12 ? "ENG" : as[12];
            infoRepository = new InfoRepository();
            infoRepository.getConnInfo().setIp(s);
            infoRepository.getConnInfo().setPort(Integer.parseInt(s4));
            infoRepository.getConnInfo().setHostName(s3);
            infoRepository.getUserInfo().setUserName(s1);
            infoRepository.getUserInfo().setPassword(s2);
            infoRepository.getUserInfo().setLanguage(s6);
            infoRepository.getVirtualUsbInfo().setPort(Integer.parseInt(s5));
            infoRepository.getPlatformInfo().setCompanyId(i);
            infoRepository.getPlatformInfo().setBoardId(j);
            break;
        }

        if(!RMConnection.keepActive(infoRepository.getConnInfo()))
        {
            System.err.println("Connection failed");
            System.exit(12);
        } else if(!RMConnection.checkValidUser(infoRepository.getUserInfo()))
        {
            System.err.println("Authentication failed");
            System.exit(13);
        }

//        RemoteVideo rv = new RemoteVideo();   
//        rv.sendF2();
//        System.exit(0);

        PlatformInfo platformInfo = infoRepository.getPlatformInfo();
        JASWInit(platformInfo.getPlatformVer(), platformInfo.getCompanyId(), platformInfo.getBoardId());

        Thread CheckDevExistThread = new Thread(new C_DevExistThread());
        CheckDevExistThread.start();
        String as1[] = new String[30];
        String as2[] = new String[30];
        String as3[] = new String[30];
        RefreshDevCBContents(DEVICE1, 1, as1);
        RefreshDevCBContents(DEVICE2, 1, as2);
        RefreshDevCBContents(DEVICE3, 1, as3);

        dev1thread = new Thread(new Dev1Thread());
        dev1thread.start();
        dev2thread = new Thread(new Dev2Thread());
        dev2thread.start();
        dev3thread = new Thread(new Dev3Thread());
        dev3thread.start();

        for (i=0; i<as2.length && iso_idx == -1; i++) {
            if (as2[i].equals("ISO File"))
                iso_idx = i;
        }

        if (iso_idx == -1)
            System.exit(4);

        JASetUNamePwdIPPort(infoRepository.getUserInfo().getUserName(),
                            infoRepository.getUserInfo().getPassword(),
                            infoRepository.getConnInfo().getIp(),
                            infoRepository.getConnInfo().getPort());

        VUSPlugIn(DEVICE2, iso_file, iso_idx, "ISO File");

        int maxkb = -1;
        try {
            maxkb = Integer.parseInt(expected_kb);
        }
        catch(NumberFormatException numberformatexception) {
            System.err.println("Wrong arg 1");
            return;
        }

        int timeout = -1;
        try {
            timeout = Integer.parseInt(timeout_s);
        }
        catch(NumberFormatException numberformatexception1) {
            System.err.println("Wrong arg 2");
            return;
        }

        int exit = 0;
        try {
            int kb = maxkb / 2;
            int waitfor = timeout*60;
            while (waitfor > 0) {
                Thread.sleep(1000);
                waitfor--;
                if (maxkb != -1 && maxkb < kb)
                    break;
            }
            if (waitfor == 0 && kb < (maxkb / 2)) {
                /* FIXME: there is no standard way to count bytes, just exit */
                exit = 0;
            }
        } catch (InterruptedException e) {}

        VUSPlugOut(DEVICE2, iso_idx);

        JThterminate();

        System.exit(exit);
    }

    private Thread dev1thread;

    private Thread dev2thread;

    private Thread dev3thread;

    public native void RefreshDevCBContents(int i, int j, String as[]);

    public native void VUSPlugIn(int i, String s, int j, String s1);

    public native void VUSPlugOut(int i, int j);

    public native void ChangeCBSelect(int i, int j);

    public native void JThterminate();

    public native void JASWInit(int i, int j, int k);

    public native void JASetUNamePwdIPPort(String s, String s1, String s2, int i);

    public native void StartDev1Thread();

    public native void StartDev2Thread();

    public native void StartDev3Thread();

    public native void StartShowMsgThread();

    public native void StartChangeGUIObjectThread();

    public native void StartCheckDevExistThread();

    public native void StartGetDevHealthStatusThread();

    public native void ResetLANDisconnect();
}
