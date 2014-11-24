// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FloppyRedir.java

package com.ami.iusb;

import com.ami.iusb.protocol.FloppyProtocol;
import com.ami.iusb.protocol.IUSBHeader;
import com.ami.iusb.protocol.IUSBSCSI;
import com.ami.iusb.protocol.PacketMaster;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// Referenced classes of package com.ami.iusb:
//            RedirectionException

public class FloppyRedir extends Thread
{

    public FloppyRedir(boolean flag)
    {
        running = false;
        stopRunning = false;
        nativeReaderPointer = -1L;
        sourceFloppy = null;
        nBytesRedirected = 0;
        physicalDevice = flag;
        protocol = new FloppyProtocol();
        packetReadBuffer = ByteBuffer.allocateDirect(0x20000);
        packetWriteBuffer = ByteBuffer.allocateDirect(0x20000);
    }

    private void floppyConnect(String s, int i, boolean flag)
        throws IOException
    {
        packetMaster = new PacketMaster(s, i, false, protocol, flag);
        packetMaster.setupBuffers(packetReadBuffer, packetWriteBuffer);
        packetMaster.setBufferEndianness(ByteOrder.LITTLE_ENDIAN, ByteOrder.LITTLE_ENDIAN);
        packetMaster.connect();
    }

    private void floppyDisconnect()
    {
        try
        {
            packetMaster.close();
        }
        catch(IOException ioexception)
        {
            System.err.println((new StringBuilder()).append(EN_StrTable.GetString("5_2_FLOPPYREDIR")).append(ioexception.getMessage()).toString());
        }
    }

    public boolean startRedirection(String s, int i, boolean flag, String s1, String s2)
        throws RedirectionException
    {
        if(running)
            return true;
        try
        {
            floppyConnect(s, i, flag);
            SendAuth_SessionToken(s1);
            IUSBSCSI iusbscsi = recvRequest();
            if(iusbscsi.opcode == 241)
            {
                if(iusbscsi.connectionStatus != 1)
                {
                    floppyDisconnect();
                    if(iusbscsi.connectionStatus == 5)
                        System.err.println(EN_StrTable.GetString("5_3_FLOPPYREDIR"));
                    else
                    if(iusbscsi.connectionStatus == 7)
                        System.err.println(EN_StrTable.GetString("5_10_FLOPPYREDIR"));
                    else
                    if(iusbscsi.connectionStatus == 3)
                        System.err.println(EN_StrTable.GetString("5_11_FLOPPYREDIR"));
                    else
                    if(iusbscsi.m_otherIP != null)
                        System.err.println((new StringBuilder()).append(EN_StrTable.GetString("5_4_FLOPPYREDIR")).append(iusbscsi.m_otherIP).toString());
                    else
                        System.err.println(EN_StrTable.GetString("5_5_FLOPPYREDIR"));
                    return false;
                }
            } else
            {
                floppyDisconnect();
                throw new RedirectionException((new StringBuilder()).append(EN_StrTable.GetString("5_6_FLOPPYREDIR")).append(iusbscsi.opcode).toString());
            }
        }
        catch(IOException ioexception)
        {
            throw new RedirectionException(ioexception.getMessage());
        }
        if(nativeReaderPointer == -1L)
            newFloppyReader(physicalDevice);
        sourceFloppy = s2;
        if(!openFloppy(s2))
        {
            System.err.println(EN_StrTable.GetString("5_7_FLOPPYREDIR"));
            deleteFloppyReader();
            floppyDisconnect();
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
                System.err.println(EN_StrTable.GetString("5_8_FLOPPYREDIR"));
            }
            floppyDisconnect();
            running = false;
            stopRunning = false;
            closeFloppy();
            deleteFloppyReader();
        }
        nBytesRedirected = 0;
        return true;
    }

    public void stopRedirectionAbnormal()
    {
        if(running)
        {
            stopRunning = true;
            floppyDisconnect();
            running = false;
            stopRunning = false;
            closeFloppy();
            deleteFloppyReader();
            System.err.println("JViewerApp.getInstance().reportFloppyAbnormal()");
        }
    }

    private IUSBSCSI recvRequest()
        throws IOException, RedirectionException
    {
        return (IUSBSCSI)packetMaster.receivePacket(true);
    }

    public boolean isRedirActive()
    {
        return running;
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
                    int j = executeFloppySCSICmd(packetReadBuffer, packetWriteBuffer);
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
    }

    public String[] getFloppyList()
    {
        if(!physicalDevice)
        {
            DisplayErrorMsg(EN_StrTable.GetString("5_9_FLOPPYREDIR"));
            return null;
        }
        String as[] = null;
        if(nativeReaderPointer == -1L)
        {
            newFloppyReader(true);
            as = listFloppyDrives();
            deleteFloppyReader();
        }
        return as;
    }

    public String getLIBFLOPPYVersion()
    {
        String s;
        if(nativeReaderPointer == -1L)
        {
            newFloppyReader(false);
            s = getVersion();
            deleteFloppyReader();
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
        return physicalDevice;
    }

    public String getSourceDrive()
    {
        return sourceFloppy;
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

    public byte ReadKeybdLEDStatus()
    {
        return GetLEDStatus();
    }

    private native String[] listFloppyDrives();

    private native void newFloppyReader(boolean flag);

    private native void deleteFloppyReader();

    private native boolean openFloppy(String s);

    private native void closeFloppy();

    private native int executeFloppySCSICmd(ByteBuffer bytebuffer, ByteBuffer bytebuffer1);

    private native String getVersion();

    private native byte GetLEDStatus();

    private PacketMaster packetMaster;
    private FloppyProtocol protocol;
    private ByteBuffer packetReadBuffer;
    private ByteBuffer packetWriteBuffer;
    private boolean running;
    private boolean stopRunning;
    private long nativeReaderPointer;
    private Thread redirThread;
    private boolean physicalDevice;
    private String sourceFloppy;
    private int nBytesRedirected;
    private static final int PORT = 5123;
    private static final int START_LOCAL_IMAGE_REDIRECTION = 240;
    private static final int DEVICE_REDIRECTION_ACK = 241;
    public static final int AUTH_CMD = 242;
    public static final int TRANSFER_CMD = 243;
    private static final int CONNECTION_ACCEPTED = 1;
    private static final int CONNECTION_DENIED = 2;
    private static final int LOGIN_FAILED = 3;
    private static final int CONNECTION_IN_USE = 4;
    private static final int CONNECTION_PERM_DENIED = 5;
    private static final int OTHER_ERROR = 6;
    private static final int MEDIA_IN_DETACH_STATE = 7;
    private static final int MAX_READ_SECTORS = 256;
    private static final int MAX_READ_SIZE = 0x20000;

    static 
    {
        try
        {
            System.loadLibrary("javafloppywrapper");
        }
        catch(UnsatisfiedLinkError unsatisfiedlinkerror)
        {
            System.err.println(EN_StrTable.GetString("5_1_FLOPPYREDIR"));
        }
    }
}
