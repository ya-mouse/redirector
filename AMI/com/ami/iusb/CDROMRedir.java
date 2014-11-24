// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CDROMRedir.java

package com.ami.iusb;

import com.ami.iusb.protocol.CDROMProtocol;
import com.ami.iusb.protocol.IUSBHeader;
import com.ami.iusb.protocol.IUSBSCSI;
import com.ami.iusb.protocol.PacketMaster;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// Referenced classes of package com.ami.iusb:
//            RedirProtocolException, RedirectionException

public class CDROMRedir extends Thread
{

    public CDROMRedir(boolean flag)
    {
        running = false;
        stopRunning = false;
        nativeReaderPointer = -1L;
        sourceCDROM = null;
        nBytesRedirected = 0;
        physicalDrive = flag;
        protocol = new CDROMProtocol();
        packetReadBuffer = ByteBuffer.allocateDirect(0x800000);
        packetWriteBuffer = ByteBuffer.allocateDirect(0x2003d);
    }

    private void cdromConnect(String s, int i, boolean flag)
        throws IOException
    {
        packetMaster = new PacketMaster(s, i, false, protocol, flag);
        packetMaster.setupBuffers(packetReadBuffer, packetWriteBuffer);
        packetMaster.setBufferEndianness(ByteOrder.LITTLE_ENDIAN, ByteOrder.LITTLE_ENDIAN);
        packetMaster.connect();
    }

    private void cdromDisconnect()
    {
        try
        {
            packetMaster.close();
        }
        catch(IOException ioexception)
        {
            System.err.println((new StringBuilder()).append(EN_StrTable.GetString("4_2_CDROMREDIR")).append(ioexception.getMessage()).toString());
        }
    }

    public boolean startRedirection(String s, int i, boolean flag, String s1, String s2)
        throws RedirectionException
    {
        if(running)
            return true;
        try
        {
            cdromConnect(s, i, flag);
            SendAuth_SessionToken(s1);
            //System.out.println("waiting to receive request");
            IUSBSCSI iusbscsi = recvRequest();
            //System.out.println("out of receive request");
            if(iusbscsi.opcode == 241)
            {
                if(iusbscsi.connectionStatus != 1)
                {
                    cdromDisconnect();
                    if(iusbscsi.connectionStatus == 5)
                        System.err.println(EN_StrTable.GetString("4_3_CDROMREDIR"));
                    else
                    if(iusbscsi.connectionStatus == 7)
                        System.err.println(EN_StrTable.GetString("4_10_CDROMREDIR"));
                    else
                    if(iusbscsi.connectionStatus == 3)
                        System.err.println(EN_StrTable.GetString("4_11_CDROMREDIR"));
                    else
                    if(iusbscsi.m_otherIP != null)
                        System.err.println((new StringBuilder()).append(EN_StrTable.GetString("4_4_CDROMREDIR")).append(iusbscsi.m_otherIP).toString());
                    else
                        System.err.println(EN_StrTable.GetString("4_5_CDROMREDIR"));
                    return false;
                }
            } else
            {
                cdromDisconnect();
                throw new RedirProtocolException((new StringBuilder()).append(EN_StrTable.GetString("4_6_CDROMREDIR")).append(iusbscsi.opcode).toString());
            }
        }
        catch(IOException ioexception)
        {
            System.err.println("Exception CDROMRedir:startRedirection");
            throw new RedirectionException(ioexception.getMessage());
        }
        if(nativeReaderPointer == -1L)
            newCDROMReader(physicalDrive);
        sourceCDROM = s2;
        if(!openCDROM(s2))
        {
            System.err.println(EN_StrTable.GetString("4_7_CDROMREDIR"));
            deleteCDROMReader();
            cdromDisconnect();
            return false;
        } else
        {
            nBytesRedirected = 0;
            redirThread = new Thread(this);
            redirThread.start();
            running = true;
            return true;
        }
    }

    public boolean stopRedirection()
    {
        if(running)
        {
            stopRunning = true;
            try
            {
                packetMaster.wakeup();
                redirThread.join();
            }
            catch(InterruptedException interruptedexception)
            {
                System.err.println(EN_StrTable.GetString("4_8_CDROMREDIR"));
            }
            cdromDisconnect();
            running = false;
            stopRunning = false;
            closeCDROM();
            deleteCDROMReader();
        }
        nBytesRedirected = 0;
        return true;
    }

    private IUSBSCSI recvRequest()
        throws IOException, RedirectionException
    {
        return (IUSBSCSI)packetMaster.receivePacket();
    }

    public boolean isRedirActive()
    {
        return running;
    }

    public void stopRedirectionAbnormal()
    {
        if(running)
        {
            stopRunning = true;
            cdromDisconnect();
            running = false;
            stopRunning = false;
            closeCDROM();
            deleteCDROMReader();
            System.err.println("JViewerApp.getInstance().reportCDROMAbnormal()");
        }
    }

    public void run()
    {
        int i = 0;
        while(!stopRunning) 
            try
            {
                packetWriteBuffer.rewind();
                IUSBSCSI iusbscsi = recvRequest();
                if(iusbscsi != null)
                {
                    int j = executeCDROMSCSICmd(packetReadBuffer, packetWriteBuffer);
                    packetWriteBuffer.limit(j);
                    IUSBSCSI iusbscsi1 = new IUSBSCSI(packetWriteBuffer, true);
                    packetMaster.sendPacket(iusbscsi1);
                    i += j;
                    nBytesRedirected += i / 1024;
                    i %= 1024;
                }
            }
            catch(IOException ioexception)
            {
                if(!stopRunning)
                {
                    stopRedirectionAbnormal();
                    handleError(ioexception.getMessage());
                    return;
                }
            }
            catch(RedirectionException redirectionexception)
            {
                if(!stopRunning)
                {
                    stopRedirectionAbnormal();
                    handleError(redirectionexception.getMessage());
                    return;
                }
            }
        //System.out.println("Exiting the CDROM/ISO Redirection thread");
    }

    public String[] getCDROMList()
        throws RedirectionException
    {
        if(!physicalDrive)
        {
            DisplayErrorMsg(EN_StrTable.GetString("4_9_CDROMREDIR"));
            return null;
        }
        String as[] = null;
        if(nativeReaderPointer == -1L)
        {
            newCDROMReader(true);
            as = listCDROMDrives();
            deleteCDROMReader();
        }
        return as;
    }

    public String getLIBCDROMVersion()
    {
        String s;
        if(nativeReaderPointer == -1L)
        {
            newCDROMReader(false);
            s = getVersion();
            deleteCDROMReader();
        } else
        {
            s = getVersion();
        }
        return s;
    }

    public void DisplayErrorMsg(String s)
    {
        System.err.println(s);
    }

    public void handleError(String s)
    {
        DisplayErrorMsg(s);
    }

    public boolean isPhysicalDevice()
    {
        return physicalDrive;
    }

    public String getSourceDrive()
    {
        return sourceCDROM;
    }

    public void SendAuth_SessionToken(String s)
        throws RedirectionException, IOException
    {
        char c = '\200';
        packetWriteBuffer.clear();
        packetWriteBuffer.limit(160);
        IUSBHeader iusbheader = IUSBHeader.createCDROMHeader(c);
        iusbheader.write(packetWriteBuffer);
        packetWriteBuffer.position(41);
        packetWriteBuffer.put((byte)-14);
        packetWriteBuffer.position(62);
        packetWriteBuffer.put((byte)0);
        packetWriteBuffer.put(s.getBytes());
        packetWriteBuffer.position(0);
        IUSBSCSI iusbscsi = new IUSBSCSI(packetWriteBuffer, true);
        packetMaster.sendPacket(iusbscsi);
    }

    public void sendTransferCmd()
        throws RedirectionException, IOException
    {
        byte byte0 = 30;
        packetWriteBuffer.clear();
        packetWriteBuffer.limit(62);
        IUSBHeader iusbheader = IUSBHeader.createCDROMHeader(byte0);
        iusbheader.write(packetWriteBuffer);
        packetWriteBuffer.position(41);
        packetWriteBuffer.put((byte)-13);
        packetWriteBuffer.position(62);
        packetWriteBuffer.position(0);
        IUSBSCSI iusbscsi = new IUSBSCSI(packetWriteBuffer, true);
        packetMaster.sendPacket(iusbscsi);
    }

    public int getBytesRedirected()
    {
        return nBytesRedirected;
    }

    private native String[] listCDROMDrives();

    private native void newCDROMReader(boolean flag);

    private native void deleteCDROMReader();

    private native boolean openCDROM(String s);

    private native void closeCDROM();

    private native int executeCDROMSCSICmd(ByteBuffer bytebuffer, ByteBuffer bytebuffer1);

    private native String getVersion();

    private PacketMaster packetMaster;
    private CDROMProtocol protocol;
    private ByteBuffer packetReadBuffer;
    private ByteBuffer packetWriteBuffer;
    private boolean physicalDrive;
    private boolean running;
    private boolean stopRunning;
    private long nativeReaderPointer;
    private Thread redirThread;
    private String sourceCDROM;
    private int nBytesRedirected;
    private static final int PORT = 5120;
    private static final int START_LOCAL_IMAGE_REDIRECTION = 240;
    private static final int DEVICE_REDIRECTION_ACK = 241;
    public static final int AUTH_CMD = 242;
    private static final int TRANSFER_CMD = 243;
    private static final int CONNECTION_ACCEPTED = 1;
    private static final int CONNECTION_DENIED = 2;
    private static final int LOGIN_FAILED = 3;
    private static final int CONNECTION_IN_USE = 4;
    private static final int CONNECTION_PERM_DENIED = 5;
    private static final int OTHER_ERROR = 6;
    private static final int MEDIA_IN_DETACH_STATE = 7;
    private static final int MAX_READ_SECTORS = 64;
    private static final int MAX_READ_SIZE = 0x20000;

    static 
    {
        try
        {
            System.loadLibrary("javacdromwrapper");
        }
        catch(UnsatisfiedLinkError unsatisfiedlinkerror)
        {
            System.err.println(EN_StrTable.GetString("4_1_CDROMREDIR"));
        }
    }
}
