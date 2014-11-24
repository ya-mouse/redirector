// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package tw.com.aten.ikvm.ui;

import java.nio.ByteBuffer;
import tw.com.aten.ikvm.util.KeyMap;

public class RemoteVideo
{
    private class CatchThread
        implements Runnable
    {
        public void run()
        {
            catchLoop();
        }

        private CatchThread()
        {
        }
    }

    private native void init(Class class1, ByteBuffer bytebuffer, ByteBuffer bytebuffer1);

    private native void destory();

    public native void keyboardAction(int i, int j, int k, int l, int i1, int j1, int k1);

    public native void doCatch(int i);

    public native void catchLoop();

    public native void stopCatch();

    private native int runImage();

    private native void hotPlug();

    private native void updateImage();

    private native void updateInfo();

    private native void getScreenUILang();

    public native void refresh();

    public RemoteVideo()
    {
        frameBuffer = ByteBuffer.allocateDirect(0x8ca000);
        cursorBuffer = ByteBuffer.allocateDirect(16404);
        init(tw.com.aten.ikvm.ui.RemoteVideo.class, frameBuffer, cursorBuffer);
        catchThread = new Thread(new CatchThread());
//        catchThread.start();
        doCatch(1);
        getScreenUILang();
        refresh();
        updateInfo(); // 7
        updateImage();
        hotPlug(); // :
    }

    public void changeResolution(int i, int j)
    {
        System.err.println("changeResolution: "+i+" x "+j);
    }

    public void privilegeCtrl(int i, int j, byte abyte0[])
    {
        System.err.println("privilegeCtrl: i="+i+" j="+j);
    }

    public void setViewerConfig(int i, byte abyte0[])
    {
        System.err.println("setViewerConfig: SESSID="+i);
        if (abyte0[1] == 0) {
            System.err.println("keyboardMouseEnabled = false");
            stop();
            System.exit(5);
        } else
            System.err.println("keyboardMouseEnabled = true");
        if (abyte0[3] == 0)
            System.err.println("vstorageEnabled = false");
        else
            System.err.println("vstorageEnabled = true");
    }

    public void genKeyEvent(int i, int j)
    {
       System.err.println("genKeyEvent: i="+i+" j="+j);
    }

    private void addClipBounds(int i, int j, int k, int l)
    {
       System.err.println("addClipBounds: i="+i+" j="+j+" k="+k+" l="+l);
    }

    public void getScreenUILangConf(int i)
    {
       System.err.println("getScreenUILangConf: i="+i);
    }

    public void getQuickCursorUpdate()
    {
       System.err.println("getQuickCursorUpdate");
    }

    public void errorHandler(int i)
    {
        System.err.println("Error: "+i);
    }

    public void sendF2()
    {
        int i = 0;
        int k = 0;
        i = KeyMap.VKtoHID(113);
        k = KeyMap.isExtendedKey(113) ? 1 : 0;
        System.out.println("i="+i+" k="+k);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
        keyboardAction(i, i, k, 1, 0, 0, 0);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
        keyboardAction(i, i, k, 0, 0, 0, 0);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
        stop();
        System.err.println("KeyPressed");
    }

    private void stop()
    {
        doCatch(0);
        stopCatch();
        try {
            if (catchThread != null)
                catchThread.join(1000);
        } catch (java.lang.InterruptedException e) { }
        destory();
    }

    private ByteBuffer frameBuffer;
    private ByteBuffer cursorBuffer;
    private Thread catchThread;
}
