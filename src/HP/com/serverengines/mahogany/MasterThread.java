// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.serverengines.mahogany;

import com.serverengines.resmgr.SettingsResMgr;

// Referenced classes of package com.serverengines.mahogany:
//            DesktopWindow, CConn

public class MasterThread extends Thread
{

    public MasterThread(CConn cconn)
    {
        m_cconn = cconn;
        m_isRunning = false;
        m_isRunningTimeout = false;
        m_isRunningBlinkingText = false;
        m_isRunningClearLockKeys = false;
        m_timeoutThreshold = 0L;
        m_keysMask = 0;
    }

    public void extendTimeout()
    {
        SettingsResMgr settingsresmgr = SettingsResMgr.getInstance();
        long l = settingsresmgr.getResourceLong("session.timeout");
        l *= 60000L;
        m_timeoutThreshold = l + System.currentTimeMillis();
    }

    public boolean isRunning()
    {
        return m_isRunning;
    }

    public void setRunning(boolean flag)
    {
        m_isRunning = flag;
        if(!m_isRunning)
        {
            m_isRunningTimeout = false;
            m_isRunningBlinkingText = false;
            m_isRunningClearLockKeys = false;
        }
    }

    public boolean isRunningTimeout()
    {
        return m_isRunningTimeout;
    }

    public void setRunningTimeout(boolean flag)
    {
        m_isRunningTimeout = flag;
        if(m_isRunningTimeout)
            extendTimeout();
    }

    public int getKeysMask()
    {
        return m_keysMask;
    }

    public void setKeysMask(int i)
    {
        m_keysMask = i;
    }

    public boolean isRunningBlinkingText()
    {
        return m_isRunningBlinkingText;
    }

    public void setRunningBlinkingText(boolean flag)
    {
        m_isRunningBlinkingText = flag;
    }

    public boolean isRunningClearLockKeys()
    {
        return m_isRunningClearLockKeys;
    }

    public void setRunningClearLockKeys(boolean flag)
    {
        m_isRunningClearLockKeys = flag;
    }

    public void run()
    {
        m_isRunning = true;
        do
        {
            if(!m_isRunning)
                break;
            try
            {
                sleep(500L);
            }
            catch(InterruptedException interruptedexception) { }
            if(m_isRunningClearLockKeys)
            {
                m_cconn.clearLockKeys(m_keysMask);
                m_isRunningClearLockKeys = false;
            }
            if(m_isRunningTimeout)
            {
                long l = System.currentTimeMillis();
                if(l > m_timeoutThreshold)
                {
                    m_cconn.closeFromTimeout();
                    m_isRunningTimeout = false;
                }
            }
        } while(true);
    }

    public static final long SLEEP_TIME = 500L;
    public static final long MILI_SECONDS_PER_SECONDS = 1000L;
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final int NO_MASK = 0;
    public static final int CAPS_LOCK_MASK = 1;
    public static final int NUM_LOCK_MASK = 2;
    public static final int SCROLL_LOCK_MASK = 4;
    protected boolean m_isRunning;
    protected boolean m_isRunningTimeout;
    protected boolean m_isRunningBlinkingText;
    protected boolean m_isRunningClearLockKeys;
    protected CConn m_cconn;
    protected long m_timeoutThreshold;
    protected int m_keysMask;
}
