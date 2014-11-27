// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   IUSBRedirSession.java

package com.ami.iusb;

import java.io.File;
import java.io.IOException;

// Referenced classes of package com.ami.iusb:
//            CDROMRedir, RedirectionException, FloppyRedir

public class IUSBRedirSession
{
    public IUSBRedirSession()
    {
        cdromRedirStatus = 0;
        floppyRedirStatus = 0;
        host = null;
        cdromSession = null;
        floppySession = null;
    }

    public boolean StartCDROMRedir(String s, int i, boolean flag, String s1, String s2)
    {
        if(s == null)
        {
            System.err.println(EN_StrTable.GetString("6_1_IUSBREDIR"));
            return false;
        }
        if(s2.length() == 0)
        {
            System.err.println(EN_StrTable.GetString("6_2_IUSBREDIR"));
            return false;
        }
        try
        {
            if(cdromSession != null)
            {
                if(cdromSession.isRedirActive())
                {
                    System.err.println(EN_StrTable.GetString("6_3_IUSBREDIR"));
                    return false;
                }
                cdromSession = null;
                System.gc();
            }
            cdromSession = new CDROMRedir(true);
            System.out.println("CDROMRedir object created");
            if(!cdromSession.startRedirection(s, i, flag, s1, s2))
            {
                System.err.println(EN_StrTable.GetString("6_4_IUSBREDIR"));
                StopCDROMRedir();
                return false;
            }
        }
        catch(UnsatisfiedLinkError unsatisfiedlinkerror)
        {
            System.err.println(EN_StrTable.GetString("6_5_IUSBREDIR"));
            StopCDROMRedir();
            return false;
        }
        catch(RedirectionException redirectionexception)
        {
            System.err.println((new StringBuilder()).append(EN_StrTable.GetString("6_6_IUSBREDIR")).append(redirectionexception.getMessage()).toString());
            StopCDROMRedir();
            return false;
        }
        cdromRedirStatus = 1;
        return true;
    }

    public boolean StopCDROMRedir()
    {
        if(cdromSession != null)
        {
            if(!cdromSession.stopRedirection())
            {
                System.err.println(EN_StrTable.GetString("6_7_IUSBREDIR"));
                return false;
            }
            cdromSession = null;
            System.gc();
        }
        cdromRedirStatus = 0;
        return true;
    }

    public boolean StartISORedir(String s, int i, boolean flag, String s1, String s2)
    {
        if(s == null)
        {
            System.err.println(EN_StrTable.GetString("6_8_IUSBREDIR"));
            return false;
        }
        if(s2.length() == 0)
        {
            System.err.println(EN_StrTable.GetString("6_9_IUSBREDIR"));
            return false;
        }
        try
        {
            if(cdromSession != null)
            {
                if(cdromSession.isRedirActive())
                {
                    System.err.println(EN_StrTable.GetString("6_10_IUSBREDIR"));
                    return false;
                }
                cdromSession = null;
                System.gc();
            }
            cdromSession = new CDROMRedir(false);
            if(!cdromSession.startRedirection(s, i, flag, s1, s2))
            {
                System.err.println(EN_StrTable.GetString("6_11_IUSBREDIR"));
                StopISORedir();
                return false;
            }
        }
        catch(UnsatisfiedLinkError unsatisfiedlinkerror)
        {
            System.err.println(EN_StrTable.GetString("6_12_IUSBREDIR"));
            StopISORedir();
            return false;
        }
        catch(RedirectionException redirectionexception)
        {
            System.err.println((new StringBuilder()).append(EN_StrTable.GetString("6_13_IUSBREDIR")).append(redirectionexception.getMessage()).toString());
            StopISORedir();
            return false;
        }
        cdromRedirStatus = 1;
        return true;
    }

    public boolean StopISORedir()
    {
        return StopCDROMRedir();
    }

    public boolean StartFloppyRedir(String s, int i, boolean flag, String s1, String s2)
    {
        if(s == null)
        {
            System.err.println(EN_StrTable.GetString("6_14_IUSBREDIR"));
            return false;
        }
        if(s2.length() == 0)
        {
            System.err.println(EN_StrTable.GetString("6_15_IUSBREDIR"));
            return false;
        }
        try
        {
            if(floppySession != null)
            {
                if(floppySession.isRedirActive())
                {
                    System.err.println(EN_StrTable.GetString("6_16_IUSBREDIR"));
                    return false;
                }
                floppySession = null;
                System.gc();
            }
            floppySession = new FloppyRedir(true);
            if(!floppySession.startRedirection(s, i, flag, s1, s2))
            {
                System.err.println(EN_StrTable.GetString("6_17_IUSBREDIR"));
                StopFloppyRedir();
                return false;
            }
        }
        catch(UnsatisfiedLinkError unsatisfiedlinkerror)
        {
            System.err.println(EN_StrTable.GetString("6_18_IUSBREDIR"));
            StopFloppyRedir();
            return false;
        }
        catch(RedirectionException redirectionexception)
        {
            System.err.println((new StringBuilder()).append(EN_StrTable.GetString("6_19_IUSBREDIR")).append(redirectionexception.getMessage()).toString());
            StopFloppyRedir();
            return false;
        }
        floppyRedirStatus = 1;
        return true;
    }

    public boolean StopFloppyRedir()
    {
        if(floppySession != null)
        {
            if(!floppySession.stopRedirection())
            {
                System.err.println(EN_StrTable.GetString("6_20_IUSBREDIR"));
                return false;
            }
            floppySession = null;
            System.gc();
        }
        floppyRedirStatus = 0;
        return true;
    }

    public boolean sendTransferCmdCDROM()
    {
        if(cdromSession == null)
            return false;
        try
        {
            cdromSession.sendTransferCmd();
        }
        catch(RedirectionException redirectionexception)
        {
            System.out.println("Exception while sending Transfer cmd to cdserver");
            return false;
        }
        catch(IOException ioexception)
        {
            System.out.println("Exception while sending Transfer cmd to cdserver");
            return false;
        }
        cdromRedirStatus = 2;
        return true;
    }

    public boolean sendTransferCmdFloppy()
    {
        if(floppySession == null)
            return false;
        try
        {
            floppySession.sendTransferCmd();
        }
        catch(RedirectionException redirectionexception)
        {
            System.out.println("Exception while sending Transfer cmd to fdserver");
            return false;
        }
        catch(IOException ioexception)
        {
            System.out.println("Exception while sending Transfer cmd to fdserver");
            return false;
        }
        floppyRedirStatus = 2;
        return true;
    }

    public boolean StartFloppyImageRedir(String s, int i, boolean flag, String s1, String s2)
    {
        if(s == null)
        {
            System.err.println(EN_StrTable.GetString("6_21_IUSBREDIR"));
            return false;
        }
        if(s2.length() == 0)
        {
            System.err.println(EN_StrTable.GetString("6_22_IUSBREDIR"));
            return false;
        }
        try
        {
            if(floppySession != null)
            {
                if(floppySession.isRedirActive())
                {
                    System.err.println(EN_StrTable.GetString("6_23_IUSBREDIR"));
                    return false;
                }
                floppySession = null;
                System.gc();
            }
            floppySession = new FloppyRedir(false);
            if(!floppySession.startRedirection(s, i, flag, s1, s2))
            {
                System.err.println(EN_StrTable.GetString("6_24_IUSBREDIR"));
                StopFloppyImageRedir();
                return false;
            }
        }
        catch(UnsatisfiedLinkError unsatisfiedlinkerror)
        {
            System.err.println(EN_StrTable.GetString("6_25_IUSBREDIR"));
            StopFloppyImageRedir();
            return false;
        }
        catch(RedirectionException redirectionexception)
        {
            System.err.println((new StringBuilder()).append(EN_StrTable.GetString("6_26_IUSBREDIR")).append(redirectionexception.getMessage()).toString());
            StopFloppyImageRedir();
            return false;
        }
        floppyRedirStatus = 1;
        return true;
    }

    public boolean StopFloppyImageRedir()
    {
        return StopFloppyRedir();
    }

    public String getLIBCDROMVersion()
    {
        String s;
        try
        {
            if(cdromSession == null)
            {
                cdromSession = new CDROMRedir(false);
                s = cdromSession.getLIBCDROMVersion();
                cdromSession = null;
                System.gc();
            } else
            {
                s = cdromSession.getLIBCDROMVersion();
            }
        }
        catch(UnsatisfiedLinkError unsatisfiedlinkerror)
        {
            s = "Not present";
        }
        return s;
    }

    public String getLIBFLOPPYVersion()
    {
        String s;
        try
        {
            if(floppySession == null)
            {
                floppySession = new FloppyRedir(false);
                s = floppySession.getLIBFLOPPYVersion();
                floppySession = null;
                System.gc();
            } else
            {
                s = floppySession.getLIBFLOPPYVersion();
            }
        }
        catch(UnsatisfiedLinkError unsatisfiedlinkerror)
        {
            s = "Not present";
        }
        return s;
    }

    public int getCDROMRedirStatus()
    {
        return cdromRedirStatus;
    }

    public int getFloppyRedirStatus()
    {
        return floppyRedirStatus;
    }

    public String getCDROMSource()
    {
        if(cdromSession != null)
            return cdromSession.getSourceDrive();
        else
            return new String("");
    }

    public String getFloppySource()
    {
        if(floppySession != null)
            return floppySession.getSourceDrive();
        else
            return new String("");
    }

    public void stopCDROMAbnormal()
    {
        cdromSession = null;
        System.gc();
        cdromRedirStatus = 0;
    }

    public void stopFloppyAbnormal()
    {
        floppySession = null;
        System.gc();
        floppyRedirStatus = 0;
    }

    public int getCDROMReadBytes()
    {
        if(cdromSession != null)
            return cdromSession.getBytesRedirected();
        else
            return 0;
    }

    public int getFloppyReadBytes()
    {
        if(floppySession != null)
            return floppySession.getBytesRedirected();
        else
            return 0;
    }

    private String host;
    public static final byte AUTH_SESS_TOKEN_PKT_SIZE = 18;
    public static final byte AUTH_USER_PKT_SIZE = 98;
    public static final byte AUTH_PKT_MAX_SIZE = 98;
    public CDROMRedir cdromSession;
    public FloppyRedir floppySession;
    public static final int DEVICE_REDIR_STATUS_IDLE = 0;
    public static final int DEVICE_REDIR_STATUS_CONNECTED = 1;
    public static final int DEVICE_REDIR_STATUS_TRANSFERING = 2;
    private int cdromRedirStatus;
    private int floppyRedirStatus;
}
