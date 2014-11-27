package com.ipmitool6.AMI.kvm;

import com.ami.iusb.IUSBRedirSession;
import java.lang.InterruptedException;

public class Redirector
{
    public static void main(String args[])
    {
        if(args.length < 13)
        {
            System.err.println("Wrong arguments count");
            return;
        }
        int m_ServerPort = 7578;
        int m_HIDPort = 5678;
        try
        {
            m_ServerPort = Integer.parseInt(args[1]);
            m_HIDPort = Integer.parseInt(args[8]);
        }
        catch(NumberFormatException numberformatexception)
        {
            System.err.println("Wrong args 1 & 8");
            return;
        }
        int k = 0;
        int l = 0;
        int i1 = 0;
        int j1 = 0;
        try
        {
            k = Integer.parseInt(args[3]);
            l = Integer.parseInt(args[4]);
            i1 = Integer.parseInt(args[7]);
            j1 = Integer.parseInt(args[9]);
        }
        catch(NumberFormatException numberformatexception1)
        {
            System.err.println("Wrong args 3, 4, 7 & 9");
            return;
        }
        if(k != 0 && k != 1 || l != 0 && l != 1)
        {
            System.err.println("Wrong args 3, 4, 7 & 9");
            return;
        }
        boolean m_bUseSSL = k == 1;
        boolean m_bVMUseSSL = l == 1;
        int m_cdPort = 0;
        try
        {
            m_cdPort = Integer.parseInt(args[5]);
        }
        catch(NumberFormatException numberformatexception2)
        {
            System.err.println("Wrong arg 5");
            return;
        }
        int m_fdPort = 0;

        String m_session_token = args[2];
        String m_webSession_token = args[11];

        IUSBRedirSession m_IUSBSession = new IUSBRedirSession();

        boolean flag = m_IUSBSession.StartISORedir(args[0], m_cdPort,
                                                   m_bVMUseSSL,
                                                   m_session_token, args[12]);

        if (!flag)
            System.exit(1);

        int maxkb = -1;
        if (args.length >= 14) {
            try {
                maxkb = Integer.parseInt(args[13]);
            }
            catch(NumberFormatException numberformatexception3) {
                System.err.println("Wrong arg 14");
                return;
            }
        }

        int timeout = 5;
        if (args.length == 15) {
            try {
                timeout = Integer.parseInt(args[14]);
            }
            catch(NumberFormatException numberformatexception3) {
                System.err.println("Wrong arg 15");
                return;
            }
        }

        try {
            int i;
            int kb = 0;
            int waitfor = timeout*60;
            while (waitfor > 0) {
                kb = m_IUSBSession.getCDROMReadBytes();
                Thread.sleep(1000);
                waitfor--;
                if (maxkb != -1 && maxkb < kb)
                    break;
            }
            if (waitfor == 0 && kb < (maxkb / 2))
                System.exit(2);
        } catch (InterruptedException e) {}

        m_IUSBSession.StopCDROMRedir();
    }
}
