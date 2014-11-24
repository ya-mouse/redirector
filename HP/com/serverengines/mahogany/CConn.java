// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.serverengines.mahogany;

import com.serverengines.buffer.Buffers;
import com.serverengines.buffer.LittleEndianBufferMgr;
import com.serverengines.helper.*;
import com.serverengines.keyboard.*;
import com.serverengines.kvm.*;
import com.serverengines.mahoganyprotocol.FirmwareVersion;
import com.serverengines.mahoganyprotocol.MultiUserState;
import com.serverengines.mahoganyprotocol.ServerHandshake;
import com.serverengines.mahoganyprotocol.StorageMsgRequest;
import com.serverengines.mahoganyprotocol.StorageStatus;
import com.serverengines.mahoganyprotocol.User;
import com.serverengines.mouse.MouseMgr;
import com.serverengines.nativeinterface.LUD;
import com.serverengines.rdr.JavaInStream;
import com.serverengines.rdr.JavaOutStream;
import com.serverengines.resmgr.*;
import com.serverengines.storage.DriveData;
import com.serverengines.storage.MountDialog;
import com.serverengines.storage.MountedDriveMgr;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.net.ssl.*;
import javax.swing.*;

// Referenced classes of package com.serverengines.mahogany:
//            MessageReceiverThread, ServerDialog, ServerEgninesTrustManager, DesktopWindow, 
//            MasterThread, ViewportFrame, StartDeviceDiscoveryThread, StorageDiscoveryThread, 
//            PerformanceDialog, FrameCtThread, AboutDialog, SettingsDialog, 
//            DisconnectSessionDialog, IOKCancelDlgCallback, IOKDlgCallback, MahoganyViewer, 
//            MsgBox, ModallessOKMsgDlg, MessageSender, ModallessOKCancelDlg

public class CConn extends CConnection
    implements IOKCancelDlgCallback, IOKDlgCallback
{

    public CConn(MahoganyViewer viewer_)
    {
        viewer = viewer_;
        m_permssionMode = 1;
        shuttingDown = false;
        m_alraedyExited = false;
        m_isHLevelCompressionOn = false;
        m_isEncyptionOn = false;
        m_hasFullControl = false;
        m_prevHasFullControl = false;
        m_canTakeFullControl = false;
        m_prevCanTakeFullControl = false;
        m_canDisconnectOthers = false;
        m_isStorageEnabled = false;
        m_isExeStorageSupported = false;
        m_isExeForKVM = false;
        m_canDoRemoteStorage = false;
        m_canDoStorageWrite = false;
        m_canRelinquishFullControl = false;
        m_justRelinquishFullControl = false;
        m_canChangeLMS = false;
        m_canTurnScreenOn = false;
        m_canTurnScreenOff = false;
        m_ignoreCapsLock = false;
        m_ignoreNumLock = false;
        m_ignoreScrollLock = false;
        m_receivedMultiUserMessage = false;
        m_loadedStorageSupport = false;
        m_receivedFirmwareVersion = false;
        m_exitProperly = false;
        m_isDVDRomDeviceAllowed = false;
        m_isMemoryDeviceAllowed = false;
        m_allowTimeoutThread = false;
        m_isKeyboardEnabled = false;
        m_isVideoEnabled = false;
        m_isMouseEnabled = false;
        m_isForcedHLevelState = false;
        m_isStandbyPower = false;
        m_isSSLConnection = false;
        m_isEmbeddedApplet = false;
        m_isRunning = true;
        m_isTimingDown = true;
        m_isStorageWorking = true;
        m_isLocalScreenOn = true;
        m_storagePort = viewer.mahoganyStorageServerPort.getValue();
        m_requiredKeyboardPort = 0;
        CurrentLocalMonitorSetting = 15;
        m_startWidthViewport = 0;
        m_startHeightViewport = 0;
        m_reasonForExiting = 2;
        desktop = null;
        m_sessionType = null;
        sock = null;
        m_storageDiscoverySocket = null;
        jis = null;
        jos = null;
        m_storageDiscoveryJis = null;
        m_storageDiscoveryJos = null;
        m_password = null;
        m_passwordFull = null;
        m_performanceDlg = null;
        m_aboutDlg = null;
        m_settingsDlg = null;
        m_requestDlg = null;
        m_sessionRequestDlg = null;
        m_DisconnectSessionDialog = null;
        m_masterThread = null;
        m_frameCtThread = null;
        m_storageDiscoveryThread = null;
        m_startStorageThread = null;
        m_secondaryControlDlg = null;
        m_newConnectionDlg = null;
        m_stoargeStatusDlg = null;
        m_kvmExeFailedDlg = null;
        m_msgDlg = null;
        m_currentFrameCt = 0;
        m_prevUsers = new User[2];
        m_altText = 0;
        m_userName = "";
        m_hostName = "";
        m_hostAddress = "";
        m_mainThread = Thread.currentThread();
        ResourceMgr resourcemgr = ResourceMgr.getInstance();
        m_firmwareVersion = resourcemgr.getResourceString("about.firmware.unknown");
        m_storageBaseVersion = "";
        for(int i = 0; i < m_prevUsers.length; i++)
            m_prevUsers[i] = User.getInstance();

        m_curUsers = new User[2];
        for(int j = 0; j < m_curUsers.length; j++)
            m_curUsers[j] = User.getInstance();

        m_keysPressedDownSet = new HashSet();
        m_keysTypedSet = new HashSet();
        m_asciiKeysSet = new HashSet();
        m_msgRecvThread = new MessageReceiverThread(this);
        try
        {
            m_robot = new Robot();
        }
        catch(Exception exception) { }
    }

    public DesktopWindow getDesktop()
    {
        return desktop;
    }

    public void startReceiverThread()
    {
        m_msgRecvThread.start();
    }

    public void initFrameCt()
    {
        m_currentFrameCt = 0;
        m_xFrameCtThreshold = 0x40000000;
        m_yFrameCtThreshold = 0x40000000;
        if(m_performanceDlg != null)
            m_performanceDlg.clearValues();
    }

    public String getFirmwareVersion()
    {
        return m_firmwareVersion;
    }

    public String getStorageDirectory()
    {
        return m_storageBaseVersion;
    }

    public void centerMouse()
    {
    }

    public void toggleMouseEscape()
    {
        if(hasFullControl())
        {
            MouseMgr mousemgr = MouseMgr.getInstance();
            mousemgr.sendMouseState();
        }
        if(viewport != null)
            viewport.toggleMouseEscape();
    }

    public void synchMouse()
    {
        if(viewport != null)
            viewport.onMouseSync();
    }

    public boolean isKeyboardEnabled()
    {
        return m_isKeyboardEnabled;
    }

    public boolean isVideoEnabled()
    {
        return m_isVideoEnabled;
    }

    public boolean isMouseEnabled()
    {
        return m_isMouseEnabled;
    }

    public boolean isStorageEnabled()
    {
        return m_isStorageEnabled;
    }

    public boolean isExeStorageSupported()
    {
        return m_isExeStorageSupported;
    }

    public boolean isStorageWorking()
    {
        return m_isStorageWorking;
    }

    public void setStorageWorking(boolean flag)
    {
        m_isStorageWorking = flag;
    }

    public boolean isRemoteStorageEnabled()
    {
        return m_canDoRemoteStorage;
    }

    public boolean isForcedHLevelState()
    {
        return m_isForcedHLevelState;
    }

    public boolean isStandbyPower()
    {
        return m_isStandbyPower;
    }

    public void setStandbyPower(boolean flag)
    {
        m_isStandbyPower = flag;
    }

    public boolean canChangeLMS()
    {
        return m_canChangeLMS;
    }

    public boolean canTurnScreenOn()
    {
        return m_canTurnScreenOn;
    }

    public boolean canTurnScreenOff()
    {
        return m_canTurnScreenOff;
    }

    public boolean allowTimeDown()
    {
        return m_allowTimeoutThread;
    }

    public boolean isTimingDown()
    {
        return m_isTimingDown;
    }

    public boolean canDisconnectOthers()
    {
        return m_canDisconnectOthers;
    }

    public void setTimingDown(boolean flag)
    {
        m_isTimingDown = flag;
    }

    public synchronized void updateTimeoutThread()
    {
        if(m_masterThread != null)
            m_masterThread.extendTimeout();
    }

    public boolean isEmbeddedApplet()
    {
        return m_isEmbeddedApplet;
    }

    protected void installFile(String s, String s1, boolean flag)
        throws Exception
    {
        long l = 0L;
        long l1 = 1L;
        StringBuffer stringbuffer = Helper.getUserHomeDir();
        stringbuffer.append(m_storageBaseVersion);
        Helper.makeRelativePath(stringbuffer);
        stringbuffer.append(s);
        if(vlog.isInfoLoggingEnabled())
            vlog.info("Destination " + s1 + ": '" + stringbuffer.toString() + "'");
        File file = new File(stringbuffer.toString());
        if(file.exists())
        {
            if(!file.isFile())
            {
                if(vlog.isErrorLoggingEnabled())
                    vlog.error("The destination " + s1 + " is not a file. Cannot install " + s1);
                setStorageWorking(false);
                return;
            }
            Object obj = Helper.getResourceStream(s);
            l = Helper.calculateChecksum(((java.io.InputStream) (obj)));
            obj = new FileInputStream(file);
            l1 = Helper.calculateChecksum(((java.io.InputStream) (obj)));
        }
        if(l != l1)
        {
            if(vlog.isInfoLoggingEnabled())
                vlog.info("Installing " + s1 + "...");
            java.io.InputStream inputstream = Helper.getResourceStream(s);
            Helper.copyFile(inputstream, stringbuffer);
            if(Helper.isLinux() && flag)
            {
                String as[] = new String[3];
                as[0] = "chmod";
                as[1] = "774";
                as[2] = stringbuffer.toString();
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec(as);
                process.waitFor();
            }
        }
        StringBufferPool.recycle(stringbuffer);
    }

    protected void installFileFromPilot(String s, int i, String s1, String s2, boolean flag)
        throws Exception
    {
        long l = 0L;
        long l1 = 1L;
        StringBuffer stringbuffer = StringBufferPool.getInstance();
        if(m_isSSLConnection)
            stringbuffer.append("https:/");
        else
            stringbuffer.append("http:/");
        stringbuffer.append(s);
        stringbuffer.append(':');
        stringbuffer.append(i);
        Helper.makeHTTPRelativePath(stringbuffer);
        stringbuffer.append(s1);
        URL url = new URL(stringbuffer.toString());
        StringBuffer stringbuffer1 = Helper.getUserHomeDir();
        stringbuffer1.append(m_storageBaseVersion);
        File file = new File(stringbuffer1.toString());
        if(!file.isDirectory())
        {
            if(file.exists())
            {
                if(vlog.isErrorLoggingEnabled())
                    vlog.error("The path is not a directory. Cannot install " + s2);
                setStorageWorking(false);
                return;
            }
            file.mkdir();
        }
        Helper.makeRelativePath(stringbuffer1);
        stringbuffer1.append(s1);
        if(vlog.isInfoLoggingEnabled())
        {
            vlog.info("Destination " + s2 + ": '" + stringbuffer1.toString() + "'");
            vlog.info("From " + s2 + ": '" + stringbuffer.toString() + "'");
            vlog.info("pilot_url: '" + s + "' port: '" + i + "' file_name: '" + s1 + "'");
        }
        file = new File(stringbuffer1.toString());
        if(file.exists())
        {
            if(!file.isFile())
            {
                if(vlog.isErrorLoggingEnabled())
                    vlog.error("The destination " + s2 + " is not a file. Cannot install " + s2);
                setStorageWorking(false);
                return;
            }
            Object obj = url.openStream();
            l = Helper.calculateChecksum(((java.io.InputStream) (obj)));
            obj = new FileInputStream(file);
            l1 = Helper.calculateChecksum(((java.io.InputStream) (obj)));
        }
        if(l != l1)
        {
            if(vlog.isInfoLoggingEnabled())
                vlog.info("Installing " + s2 + "...");
            java.io.InputStream inputstream = url.openStream();
            Helper.copyFile(inputstream, stringbuffer1);
            if(Helper.isLinux() && flag)
            {
                String as[] = new String[3];
                as[0] = "chmod";
                as[1] = "774";
                as[2] = stringbuffer1.toString();
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec(as);
                process.waitFor();
            }
        }
        StringBufferPool.recycle(stringbuffer);
        StringBufferPool.recycle(stringbuffer1);
    }

    protected void loadStorage()
    {
        if(m_receivedMultiUserMessage && m_receivedFirmwareVersion && !m_loadedStorageSupport)
        {
            Process process = null;
            boolean flag = false;
            boolean flag1 = false;
            ResourceMgr resourcemgr = ResourceMgr.getInstance();
            m_loadedStorageSupport = true;
            installStorageSupport();
            if(isStorageWorking() && m_isExeForKVM)
            {
                StringBuffer stringbuffer = Helper.getUserHomeDir();
                stringbuffer.append(m_storageBaseVersion);
                Helper.makeRelativePath(stringbuffer);
                stringbuffer.append(Helper.getStorageServerName());
                String as[] = new String[6];
                as[0] = stringbuffer.toString();
                as[1] = "-integratedkvm";
                as[2] = m_hostAddress;
                as[3] = String.valueOf(serverPort);
                as[4] = m_userName;
                as[5] = new String(m_password);
                StringBufferPool.recycle(stringbuffer);
                Runtime runtime = Runtime.getRuntime();
                try
                {
                    process = runtime.exec(as);
                }
                catch(Exception exception)
                {
                    flag1 = true;
                }
                do
                {
                    if(flag1)
                        break;
                    try
                    {
                        int i = process.waitFor();
                        flag1 = true;
                        if(i == 0)
                        {
                            close();
                            return;
                        }
                    }
                    catch(InterruptedException interruptedexception) { }
                    catch(Exception exception1)
                    {
                        flag1 = true;
                    }
                } while(true);
                flag = true;
            }
            if(flag)
            {
                String s;
                if(isAlt0Text())
                    s = resourcemgr.getResourceString("alt.0.err.kvm.exe.failed");
                else
                    s = resourcemgr.getResourceString("err.kvm.exe.failed");
                if(m_kvmExeFailedDlg == null)
                {
                } else
                {
                    m_kvmExeFailedDlg.setMessage(s);
                    m_kvmExeFailedDlg.pack();
                    m_kvmExeFailedDlg.requestFocus();
                }
            }
            try
            {
                if(!isStorageWorking())
                {
                    if(vlog.isDebugLoggingEnabled())
                        vlog.debug("Error copying storage plug-in JAR file.");
                    String s1;
                    if(isAlt0Text())
                        s1 = resourcemgr.getResourceString("alt.0.err.storage.failed.to.load");
                    else
                        s1 = resourcemgr.getResourceString("err.storage.failed.to.load");
                    if(m_msgDlg == null)
                    {
                    } else
                    {
                        m_msgDlg.setMessage(s1);
                        m_msgDlg.requestFocus();
                    }
                } else
                if(!isExeStorageSupported())
                {
                    LUD.getInstance(m_storageBaseVersion);
                    if(!LUD.isLoaded())
                    {
                        setStorageWorking(false);
                        String s2;
                        if(isAlt0Text())
                            s2 = resourcemgr.getResourceString("alt.0.err.storage.failed.to.load");
                        else
                            s2 = resourcemgr.getResourceString("err.storage.failed.to.load");
                        if(LUD.hasStorageFailedToLoad())
                            if(m_msgDlg == null)
                            {
                            } else
                            {
                                m_msgDlg.setMessage(s2);
                                m_msgDlg.requestFocus();
                            }
                    }
                }
            }
            catch(NoClassDefFoundError noclassdeffounderror)
            {
                setStorageWorking(false);
                if(vlog.isDebugLoggingEnabled())
                {
                    vlog.debug("Error starting storage...");
                    vlog.debug(noclassdeffounderror);
                }
                String s3;
                if(isAlt0Text())
                    s3 = resourcemgr.getResourceString("alt.0.err.storage.failed.to.load");
                else
                    s3 = resourcemgr.getResourceString("err.storage.failed.to.load");
                if(m_msgDlg == null)
                {
                } else
                {
                    m_msgDlg.setMessage(s3);
                    m_msgDlg.requestFocus();
                }
            }
        }
    }

    public void installStorageSupport()
    {
        try
        {
            if(isExeStorageSupported() || m_isExeForKVM)
            {
                installFileFromPilot(m_hostAddress, serverPort, Helper.getStorageServerName(), "StorageServer", true);
                installFileFromPilot(m_hostAddress, serverPort, "M2.R", "ResourceFile", false);
                setStorageWorking(true);
            }
        }
        catch(Throwable throwable)
        {
            if(vlog.isErrorLoggingEnabled())
            {
                vlog.error("Error installing Storage:");
                vlog.error(throwable);
            }
            setStorageWorking(false);
        }
    }

    protected void mount()
    {
        InetAddress inetaddress = null;
        byte abyte0[] = null;
        byte byte1 = -1;
        byte byte2 = -1;
        byte byte3 = -1;
        byte byte4 = -1;
        String s = "";
        String s1 = "";

        short word0 = (short)viewer.mahoganyStorageServerPort.getValue();
        byte1 = 0;
        byte3 = 0xb; /* driveType */
        s = viewer.einstellungISO.getValue();

        try {
            inetaddress = InetAddress.getByName(viewer.einstellungServerName.getValue());
        } catch (Exception exception) { System.exit(13); }

        abyte0 = inetaddress.getAddress();
        byte byte0;
        if(inetaddress instanceof Inet4Address)
            byte0 = 1;
        else
            byte0 = 2;
        MessageSender.storageClientComnnect(abyte0, word0, byte1, byte2, byte3, byte4, byte0, s, s1);
    }

    protected void umount()
    {
        if (isSocketValid())
            MessageSender.storageClientDisconnect();
    }

    protected void displayInfo()
    {
        if(vlog.isDebugLoggingEnabled())
        {
            Properties properties = System.getProperties();
            StringBuffer stringbuffer = StringBufferPool.getInstance("--- System Info ---\n");
            for(Enumeration enumeration = properties.keys(); enumeration.hasMoreElements(); stringbuffer.append("'\n"))
            {
                String s = (String)enumeration.nextElement();
                String s1 = properties.getProperty(s);
                stringbuffer.append(s);
                stringbuffer.append("='");
                stringbuffer.append(s1);
            }

            stringbuffer.append("---------------------\nRemote KVMS version: ");
            VersionResMgr versionresmgr = VersionResMgr.getInstance();
            stringbuffer.append(versionresmgr.getResourceString("release.version"));
            stringbuffer.append('.');
            stringbuffer.append(versionresmgr.getResourceString("major.version"));
            stringbuffer.append('.');
            stringbuffer.append(versionresmgr.getResourceString("minor.version"));
            stringbuffer.append('.');
            stringbuffer.append(versionresmgr.getResourceString("build.number"));
            stringbuffer.append("\n---------------------\nLog file time: ");
            Calendar calendar = CalendarPool.getInstance();
            stringbuffer.append(calendar.toString());
            CalendarPool.recycle(calendar);
            stringbuffer.append("\n---------------------");
            vlog.debug(stringbuffer.toString());
            StringBufferPool.recycle(stringbuffer);
        }
    }

    public boolean init(String s, String s1, String s2, boolean flag, String s3, int i, int j, 
            int k, int l, int i1, int j1)
        throws IOException
    {
        MessageSender.setConn(this);
        StringBuffer stringbuffer = StringBufferPool.getInstance();
        m_userName = s1;
        m_password = s2.toCharArray();
        m_sessionType = s3;
        m_storagePort = i;
        if(flag || s == null)
        {
            ServerDialog serverdialog = new ServerDialog(this, s);
            serverdialog.setVisible(true);
            if(!serverdialog.ok)
                return false;
            s = serverdialog.m_serverText.getText();
            m_userName = serverdialog.m_userName.getText();
            m_password = serverdialog.m_passwdEntry.getPassword();
            m_isEmbeddedApplet = false;
        } else
        {
            m_isEmbeddedApplet = true;
        }
        m_passwordFull = new char[128];
        System.arraycopy(m_password, 0, m_passwordFull, 0, m_password.length);
        SettingsResMgr settingsresmgr = SettingsResMgr.getInstance();
        if(viewer.mahoganyLogging.getValue() && settingsresmgr.getResourceInt("initial.enable.msgs") == 0)
            settingsresmgr.setResourceValue("initial.enable.msgs", 1);
        if(settingsresmgr.getResourceInt("initial.enable.msgs") == 0)
            LogWriter.setGlobalLoggingLevel(0);
        LogWriter.configureLogging();
        if(m_userName.length() < 16)
        {
            stringbuffer.append(m_userName);
            for(int k1 = stringbuffer.length(); k1 < 16; k1++)
                stringbuffer.append('\0');

            m_userName = stringbuffer.toString();
        } else
        if(m_userName.length() > 16)
            m_userName = m_userName.substring(0, 16);
        if(m_password.length < 20)
        {
            char ac[] = new char[20];
            System.arraycopy(m_password, 0, ac, 0, m_password.length);
            for(int i2 = m_password.length; i2 < 20; i2++)
                ac[i2] = '\0';

            m_password = ac;
        } else
        if(m_password.length > 20)
        {
            char ac1[] = new char[20];
            System.arraycopy(m_password, 0, ac1, 0, ac1.length);
            m_password = ac1;
        }
        if(m_passwordFull.length < 128)
        {
            char ac2[] = new char[128];
            System.arraycopy(m_passwordFull, 0, ac2, 0, m_passwordFull.length);
            for(int j2 = m_passwordFull.length; j2 < 128; j2++)
                ac2[j2] = '\0';

            m_passwordFull = ac2;
        } else
        if(m_passwordFull.length > 128)
        {
            char ac3[] = new char[128];
            System.arraycopy(m_passwordFull, 0, ac3, 0, ac3.length);
            m_passwordFull = ac3;
        }
        displayInfo();
        if(vlog.isInfoLoggingEnabled())
        {
            stringbuffer.setLength(0);
            stringbuffer.append("Storage Port: ");
            stringbuffer.append(i);
            vlog.info(stringbuffer.toString());
            stringbuffer.setLength(0);
            stringbuffer.append("Nonsecure KVM Port: ");
            stringbuffer.append(j);
            vlog.info(stringbuffer.toString());
            stringbuffer.setLength(0);
            stringbuffer.append("Nonsecure KM Port: ");
            stringbuffer.append(k);
            vlog.info(stringbuffer.toString());
            stringbuffer.setLength(0);
            stringbuffer.append("Nonsecure V Port: ");
            stringbuffer.append(l);
            vlog.info(stringbuffer.toString());
            stringbuffer.setLength(0);
            stringbuffer.append("SSL VM Port: ");
            stringbuffer.append(i1);
            vlog.info(stringbuffer.toString());
            stringbuffer.setLength(0);
            stringbuffer.append("SSL K Port: ");
            stringbuffer.append(j1);
            vlog.info(stringbuffer.toString());
        }
        m_hostName = s;
        serverHost = InetAddress.getByName(s);
        m_hostAddress = serverHost.toString();
        int l1 = 0;
        if(m_sessionType.equalsIgnoreCase("kvmssl"))
        {
            l1 = 1;
            m_isSSLConnection = true;
        } else
        {
            l1 = 0;
        }
        settingsresmgr.setResourceValue("connect.as.method", l1);
        if(l1 == 1)
        {
            m_requiredKeyboardSecurePort = j1;
            SSLContext sslcontext = null;
            m_isEncyptionOn = true;
            try
            {
                sslcontext = SSLContext.getInstance("SSL");
                TrustManager atrustmanager[] = new TrustManager[1];
                atrustmanager[0] = new ServerEgninesTrustManager();
                sslcontext.init(null, atrustmanager, null);
            }
            catch(Exception exception)
            {
                if(vlog.isErrorLoggingEnabled())
                    vlog.error("Error setting trust manager");
                throw new RuntimeException("Error setting trust manager", exception);
            }
            serverPort = j;
            if(vlog.isInfoLoggingEnabled())
            {
                stringbuffer.setLength(0);
                stringbuffer.append("Connecting to host via SSL ");
                stringbuffer.append(m_hostAddress);
                stringbuffer.append(" port ");
                stringbuffer.append(serverPort);
                vlog.info(stringbuffer.toString());
            }
            SSLSocketFactory sslsocketfactory = sslcontext.getSocketFactory();
            SSLSocket sslsocket = (SSLSocket)sslsocketfactory.createSocket(serverHost, serverPort);
            sslsocket.startHandshake();
            sock = sslsocket;
        } else
        {
            serverPort = j;
            if(vlog.isInfoLoggingEnabled())
            {
                stringbuffer.setLength(0);
                stringbuffer.append("Connecting to host ");
                stringbuffer.append(m_hostAddress);
                stringbuffer.append(" port ");
                stringbuffer.append(serverPort);
                vlog.info(stringbuffer.toString());
            }
            sock = new Socket(serverHost, serverPort);
        }
        StringBufferPool.recycle(stringbuffer);
        sock.setSoTimeout(20);
        jis = new JavaInStream(sock.getInputStream());
        jos = new JavaOutStream(sock.getOutputStream());
        setStreams(jis, jos);
        MessageSender.setConnection(this);
        MessageSender.sendClientNOP();
        return true;
    }

    public void startStorageSocket()
        throws Throwable
    {
        if(m_storageDiscoverySocket == null)
        {
            int i = getViewer().mahoganyStorageServerPort.getValue();
            if(vlog.isDebugLoggingEnabled())
                vlog.debug("cwd: '" + System.getProperty("user.dir", "").trim() + "'");
            Thread.sleep(250L);
            m_storageDiscoverySocket = new Socket("127.0.0.1", i);
            m_storageDiscoveryJis = new JavaInStream(m_storageDiscoverySocket.getInputStream());
            m_storageDiscoveryJos = new JavaOutStream(m_storageDiscoverySocket.getOutputStream());
        }
    }

    public void sendStorageMsg()
        throws Throwable
    {
        LittleEndianBufferMgr littleendianbuffermgr = LittleEndianBufferMgr.getInstance();
        StorageMsgRequest storagemsgrequest = StorageMsgRequest.getInstance();
        storagemsgrequest.setPortNumber((short)getViewer().mahoganyStorageServerPort.getValue());
        storagemsgrequest.writeBuffer(littleendianbuffermgr);
        m_storageDiscoveryJos.writeBytes(littleendianbuffermgr.getBuffer(), 0, littleendianbuffermgr.getOffset());
        m_storageDiscoveryJos.flush();
        storagemsgrequest.recycle();
    }

    public void readStorageResponse(StringBuffer stringbuffer)
    {
        String s = "";
        m_storageDiscoveryJis.readBytes(Buffers.m_buffer, 19);
        try
        {
            s = new String(Buffers.m_buffer, 9, 8);
        }
        catch(Exception exception) { }
        s = s.trim();
        int i = Integer.parseInt(s, 16);
        m_storageDiscoveryJis.readBytes(Buffers.m_buffer, i);
        try
        {
            s = new String(Buffers.m_buffer, 0, i);
        }
        catch(Exception exception1) { }
        stringbuffer.append(s);
    }

    public StringBuffer communicateWithStorageServer()
        throws Throwable
    {
        StringBuffer stringbuffer = StringBufferPool.getInstance();
        boolean flag = false;
        int i = 0;
        do
        {
            if(flag || i >= 3)
                break;
            try
            {
                if(vlog.isDebugLoggingEnabled())
                    vlog.debug("Starting stoarge socket...");
                startStorageSocket();
                if(vlog.isDebugLoggingEnabled())
                    vlog.debug("Sending stoarge message...");
                sendStorageMsg();
                if(vlog.isDebugLoggingEnabled())
                    vlog.debug("Reading stoarge response...");
                readStorageResponse(stringbuffer);
                if(vlog.isDebugLoggingEnabled())
                    vlog.debug("Completed Storage transcation.");
                flag = true;
                if(vlog.isDebugLoggingEnabled())
                    vlog.debug("Completed Device Discovery...");
            }
            catch(Throwable throwable)
            {
                if(vlog.isErrorLoggingEnabled())
                {
                    vlog.error("Error opening storage socket:");
                    vlog.error(throwable);
                }
                m_storageDiscoverySocket = null;
                if(++i >= 3)
                {
                    ResourceMgr resourcemgr = ResourceMgr.getInstance();
                    try
                    {
                        if(!isStorageWorking())
                        {
                            if(vlog.isDebugLoggingEnabled())
                                vlog.debug("Error copying storage plug-in JAR file.");
                            String s;
                            if(isAlt0Text())
                                s = resourcemgr.getResourceString("alt.0.err.storage.failed.to.load");
                            else
                                s = resourcemgr.getResourceString("err.storage.failed.to.load");
                            if(m_msgDlg == null)
                            {
                            } else
                            {
                                m_msgDlg.setMessage(s);
                                m_msgDlg.requestFocus();
                            }
                        } else
                        if(!isExeStorageSupported() && !LUD.isLoaded())
                        {
                            setStorageWorking(false);
                            String s1;
                            if(isAlt0Text())
                                s1 = resourcemgr.getResourceString("alt.0.err.storage.failed.to.load");
                            else
                                s1 = resourcemgr.getResourceString("err.storage.failed.to.load");
                            if(LUD.hasStorageFailedToLoad())
                                if(m_msgDlg == null)
                                {
                                } else
                                {
                                    m_msgDlg.setMessage(s1);
                                    m_msgDlg.requestFocus();
                                }
                        }
                    }
                    catch(NoClassDefFoundError noclassdeffounderror)
                    {
                        setStorageWorking(false);
                        if(vlog.isDebugLoggingEnabled())
                        {
                            vlog.debug("Error starting native interface plugin...");
                            vlog.debug(noclassdeffounderror);
                        }
                        String s2;
                        if(isAlt0Text())
                            s2 = resourcemgr.getResourceString("alt.0.err.storage.failed.to.load");
                        else
                            s2 = resourcemgr.getResourceString("err.storage.failed.to.load");
                        if(m_msgDlg == null)
                        {
                        } else
                        {
                            m_msgDlg.setMessage(s2);
                            m_msgDlg.requestFocus();
                        }
                    }
                }
            }
        } while(true);
        return stringbuffer;
    }

    void removeWindow()
    {
        if(viewport != null)
        {
            viewport.dispose();
            viewport = null;
        }
    }

    public void onOfferFullControl()
    {
        //viewport.onOfferFullControl();
    }

    public boolean isExitingProperly()
    {
        return m_exitProperly;
    }

    public void setExitProperly(boolean flag)
    {
        m_exitProperly = flag;
    }

    public boolean isHLevelCompressionOn()
    {
        return m_isHLevelCompressionOn;
    }

    public void setHLevelCompressionOn(boolean flag)
    {
        m_isHLevelCompressionOn = flag;
    }

    public void serverInit()
    {
        super.serverInit();
        recreateViewport();
        m_masterThread = new MasterThread(this);
        m_masterThread.start();
    }

    void recreateViewport()
    {
        ResourceMgr resourcemgr = ResourceMgr.getInstance();
        SettingsResMgr settingsresmgr = SettingsResMgr.getInstance();
        KeyboardMgr keyboardmgr = KeyboardMgr.getInstance();
        Keyboard keyboard = keyboardmgr.getKeyboard(settingsresmgr.getResourceInt("default.language"), this);
    }

    public void updateMountDialog(boolean flag)
    {
        if(isStorageWorking() && (isExeStorageSupported() || LUD.isLoaded()) && flag)
            startDeviceDiscovery(flag);
    }

    public void beginStorageDlgUpdate()
    {
        if(m_startStorageThread == null)
        {
            m_startStorageThread = new StartDeviceDiscoveryThread(this, m_mainThread);
            m_startStorageThread.start();
        }
    }

    public void updateStorageDialog()
    {
        if(m_startStorageThread != null)
        {
            updateMountDialog(true);
            //viewport.setStorageDlgVisible();
            m_startStorageThread = null;
        }
    }

    public Thread getMainThread()
    {
        return m_mainThread;
    }

    public StorageDiscoveryThread getDeviceDiscoveryThread()
    {
        return m_storageDiscoveryThread;
    }

    public void startDeviceDiscovery()
    {
        startDeviceDiscovery(true);
    }

    public void startDeviceDiscovery(boolean flag)
    {
        if(m_storageDiscoveryThread == null)
        {
            //viewport.discoverDevices(true);
            m_storageDiscoveryThread = new StorageDiscoveryThread(this, flag, m_mainThread);
            m_storageDiscoveryThread.start();
        }
    }

    public void onDiscoveryComplete()
    {
        m_storageDiscoveryThread = null;
        //viewport.getMountDialog().reloadDrives();
        //viewport.discoverDevices(false);
    }

    void reconfigureViewport()
    {
        if(viewport != null)
            viewport.pack();
    }

    public void close()
    {
        try
        {
            if(!m_alraedyExited)
            {
                m_alraedyExited = true;
                MouseMgr mousemgr = MouseMgr.getInstance();
                if(mousemgr.getId() == 0)
                {
                    mousemgr.useRelativeMode();
                    SettingsResMgr settingsresmgr = SettingsResMgr.getInstance();
                    settingsresmgr.setResourceValue("mouse.mode", mousemgr.getId());
                    settingsresmgr.writeResource();
                }
                m_frameCtThread = null;
                m_isRunning = false;
                shuttingDown = true;
                if(m_masterThread != null)
                {
                    m_masterThread.setRunning(false);
                    m_masterThread = null;
                }
                if(m_frameCtThread != null)
                {
                    m_frameCtThread.setRunning(false);
                    m_frameCtThread = null;
                }
                if(m_requestDlg != null)
                {
                    m_requestDlg.endDialog();
                    m_requestDlg = null;
                }
                if(m_sessionRequestDlg != null)
                {
                    m_sessionRequestDlg.endDialog();
                    m_sessionRequestDlg = null;
                }
                if(m_secondaryControlDlg != null)
                {
                    m_secondaryControlDlg.endDialog();
                    m_secondaryControlDlg = null;
                }
                if(m_newConnectionDlg != null)
                {
                    m_newConnectionDlg.endDialog();
                    m_newConnectionDlg = null;
                }
                if(m_stoargeStatusDlg != null)
                {
                    m_stoargeStatusDlg.endDialog();
                    m_stoargeStatusDlg = null;
                }
                if(m_kvmExeFailedDlg != null)
                {
                    m_kvmExeFailedDlg.endDialog();
                    m_kvmExeFailedDlg = null;
                }
                if(m_performanceDlg != null)
                {
                    m_performanceDlg.endDialog();
                    m_performanceDlg = null;
                }
                if(m_msgDlg != null)
                {
                    m_msgDlg.endDialog();
                    m_msgDlg = null;
                }
                closeAllDevices();
                if(isSocketValid())
                {
                    if(m_hasFullControl)
                        MessageSender.relinquishFullControl();
                    MessageSender.clientDisconnect(m_reasonForExiting);
                }
                closeSockets();
                KeyboardMgr.recycle();
                if(isStorageWorking() && !isExeStorageSupported())
                    LUD.clearInstance();
                LogWriter.close();
            }
        }
        catch(Throwable throwable)
        {
            if(vlog.isErrorLoggingEnabled())
                vlog.error(throwable);
        }
    }

    public MasterThread getMasterThread()
    {
        return m_masterThread;
    }

    public void closeFromTimeout()
    {
        ResourceMgr resourcemgr = ResourceMgr.getInstance();
        String s;
        if(isAlt0Text())
            s = resourcemgr.getResourceString("alt.0.err.session.timedout");
        else
            s = resourcemgr.getResourceString("err.session.timedout");
        setExitProperly(true);
        close();
    }

    public void setReasonForExiting(int i)
    {
        m_reasonForExiting = i;
    }

    public void closeSockets()
    {
        try
        {
            if(sock != null)
            {
                sock.close();
                sock = null;
            }
            closeStorageSupport();
        }
        catch(Exception exception)
        {
            if(vlog.isErrorLoggingEnabled())
                vlog.error(exception);
        }
    }

    public void closeStorageSupport()
    {
        try
        {
            if(m_storageDiscoverySocket != null)
            {
                m_storageDiscoverySocket.close();
                m_storageDiscoverySocket = null;
                m_storageDiscoveryJis = null;
                m_storageDiscoveryJos = null;
            }
            LUD lud = LUD.getInstance(m_storageBaseVersion);
            lud.unload();
        }
        catch(Exception exception)
        {
            if(vlog.isErrorLoggingEnabled())
                vlog.error(exception);
        }
    }

    protected void closeAllDevices()
    {
        if(isStorageWorking() && (isExeStorageSupported() || LUD.isLoaded()))
        {
            MountedDriveMgr mounteddrivemgr = MountedDriveMgr.getInstance();
            if(mounteddrivemgr.isSharingDevices() && mounteddrivemgr.isSharingLocalStorage())
                mounteddrivemgr.unmountDevice();
        }
    }

    public boolean isSocketValid()
    {
        return sock != null && !sock.isClosed() && sock.isConnected() && !sock.isOutputShutdown();
    }

    public void showPerformanceDialog(Frame frame)
    {
        if(m_performanceDlg == null)
        {
            m_performanceDlg = new PerformanceDialog(frame, this);
            initFrameCt();
            if(m_frameCtThread == null)
            {
                m_frameCtThread = new FrameCtThread(this);
                m_frameCtThread.start();
            }
            m_frameCtThread.setRunning(true);
        }
        m_performanceDlg.setVisible(true);
    }

    public void onPerformanceComplete()
    {
        m_performanceDlg = null;
        if(m_frameCtThread != null)
        {
            m_frameCtThread.setRunning(false);
            m_frameCtThread = null;
        }
    }

    public void incrementFrameCounter(int i, int j)
    {
        synchronized(com.serverengines.mahogany.CConn.class)
        {
            if(i <= m_xFrameCtThreshold && j <= m_yFrameCtThreshold)
            {
                m_currentFrameCt++;
                if(m_performanceDlg != null)
                    m_performanceDlg.changeCutrentCycle(m_currentFrameCt);
            }
            m_xFrameCtThreshold = i;
            m_yFrameCtThreshold = j;
        }
    }

    public void changeCycles()
    {
        synchronized(com.serverengines.mahogany.CConn.class)
        {
            if(m_performanceDlg != null)
            {
                m_performanceDlg.changeCycles();
                m_currentFrameCt = 0;
            }
        }
    }

    public void showAbout(Dialog dialog)
    {
        if(m_aboutDlg != null)
            m_aboutDlg.endDialog();
        m_aboutDlg = new AboutDialog(dialog, this);
        m_aboutDlg.setVisible(true);
    }

    public void showAbout(Frame frame)
    {
        if(m_aboutDlg != null)
            m_aboutDlg.endDialog();
        m_aboutDlg = new AboutDialog(frame, this);
        m_aboutDlg.setVisible(true);
    }

    public void onAboutComplete()
    {
        m_aboutDlg = null;
    }

    public void onPreferencesComplete()
    {
        m_settingsDlg = null;
    }

    public void showPreferencesDialog(Dialog dialog)
    {
        if(m_settingsDlg != null)
            m_settingsDlg.endDialog();
        m_settingsDlg = new SettingsDialog(dialog, this);
        m_settingsDlg.setVisible(true);
    }

    public void showPreferencesDialog(Frame frame)
    {
        if(m_settingsDlg != null)
            m_settingsDlg.endDialog();
        m_settingsDlg = new SettingsDialog(frame, this);
        m_settingsDlg.setVisible(true);
    }

    public void onDisconnectSession()
    {
    }

    public synchronized void writeKeyEvent(int i, boolean flag)
    {
        if(state() != 1) // || viewer.viewOnly.getValue())
        {
            return;
        } else
        {
            MessageSender.keyStateChange((short)i, flag, false);
            return;
        }
    }

    public synchronized void onKeyPressed(KeyEvent keyevent)
    {
        if(state() != 1)
            return;
        if(keyevent.getKeyCode() == 20 && m_ignoreCapsLock)
            return;
        if(keyevent.getKeyCode() == 144 && m_ignoreNumLock)
            return;
        if(keyevent.getKeyCode() == 145 && m_ignoreScrollLock)
            return;
        if(keyevent.getKeyCode() == 121)
            return;
        KeyboardMgr keyboardmgr = KeyboardMgr.getInstance();
        Keyboard keyboard = keyboardmgr.getKeyboard();
        KeyMappingResMgr keymappingresmgr = keyboard.getKeyMappings();
        int i = keyevent.getKeyCode();
        int j = keymappingresmgr.getHidMapping(i, keyevent.getKeyChar(), keyevent.getKeyLocation());
        Integer integer = new Integer(j);
        Integer integer1 = new Integer(230);
        boolean flag = keyevent.isAltGraphDown() && !m_keysPressedDownSet.contains(integer1);
        if(j == -1)
            return;
        if(i == 240)
        {
            writeKeyEvent(j, true);
            writeKeyEvent(j, false);
        } else
        if(!m_keysPressedDownSet.contains(integer))
        {
            Integer integer2 = new Integer(224);
            if(keyevent.isAltDown() && keyevent.getKeyLocation() == 3 && m_keysPressedDownSet.contains(integer2))
            {
                writeKeyEvent(224, false);
                m_keysPressedDownSet.remove(integer2);
                //viewport.setSpecialKey(224, false);
            }
            if(flag)
            {
                //viewport.setSpecialKey(230, true);
                writeKeyEvent(230, true);
            }
            //viewport.setSpecialKey(j, true);
            m_keysPressedDownSet.add(integer);
            writeKeyEvent(j, true);
            m_asciiKeysSet.add(new Integer(keyevent.getKeyChar()));
        }
    }

    public synchronized void onKeyReleased(KeyEvent keyevent)
    {
        if(keyevent.getKeyCode() == 20 && m_ignoreCapsLock)
        {
            m_ignoreCapsLock = false;
            return;
        }
        if(keyevent.getKeyCode() == 144 && m_ignoreNumLock)
        {
            m_ignoreNumLock = false;
            return;
        }
        if(keyevent.getKeyCode() == 145 && m_ignoreScrollLock)
        {
            m_ignoreScrollLock = false;
            return;
        }
        if(state() != 1)
            return;
        if(keyevent.getKeyCode() == 121)
            return;
        KeyboardMgr keyboardmgr = KeyboardMgr.getInstance();
        Keyboard keyboard = keyboardmgr.getKeyboard();
        KeyMappingResMgr keymappingresmgr = keyboard.getKeyMappings();
        int i = keymappingresmgr.getHidMapping(keyevent.getKeyCode(), keyevent.getKeyChar(), keyevent.getKeyLocation());
        Integer integer = new Integer(i);
        Integer integer1 = new Integer(230);
        boolean flag = keyevent.isAltGraphDown() && !m_keysPressedDownSet.contains(integer1);
        if(i == -1)
            return;
        if(m_keysTypedSet.contains(integer))
        {
            m_keysPressedDownSet.remove(integer);
            m_keysTypedSet.remove(integer);
            //viewport.clearSpecialKeys();
            return;
        }
        if(m_keysPressedDownSet.contains(integer))
        {
            writeKeyEvent(i, false);
            m_keysPressedDownSet.remove(integer);
            m_asciiKeysSet.remove(new Integer(keyevent.getKeyChar()));
            //viewport.setSpecialKey(i, false);
            if(flag)
            {
                writeKeyEvent(230, false);
                //viewport.setSpecialKey(230, false);
            }
        } else
        if(i == 224)
        {
            m_asciiKeysSet.remove(new Integer(keyevent.getKeyChar()));
            //viewport.setSpecialKey(i, false);
        } else
        {
            if(flag)
            {
                //viewport.setSpecialKey(230, true);
                writeKeyEvent(230, true);
            }
            //viewport.setSpecialKey(i, true);
            writeKeyEvent(i, true);
            writeKeyEvent(i, false);
            //viewport.setSpecialKey(i, false);
            if(flag)
            {
                writeKeyEvent(230, false);
                //viewport.setSpecialKey(230, false);
            }
        }
        //viewport.clearSpecialKeys();
    }

    public synchronized void onKeyTyped(KeyEvent keyevent)
    {
        if(state() != 1)
            return;
        if(keyevent.getKeyCode() == 121)
            return;
        KeyboardMgr keyboardmgr = KeyboardMgr.getInstance();
        Keyboard keyboard = keyboardmgr.getKeyboard();
        KeyMappingResMgr keymappingresmgr = keyboard.getKeyMappings();
        int i = keymappingresmgr.getHidMapping(keyevent.getKeyCode(), keyevent.getKeyChar(), keyevent.getKeyLocation());
        Integer integer = new Integer(i);
        if(i == -1)
            return;
        if(!m_keysPressedDownSet.contains(integer) && !m_asciiKeysSet.contains(new Integer(keyevent.getKeyChar())))
        {
            //viewport.setSpecialKey(i, true);
            writeKeyEvent(i, true);
            writeKeyEvent(i, false);
            //viewport.setSpecialKey(i, false);
            m_keysTypedSet.add(new Integer(i));
        }
    }

    public void changeKeyboardLayout()
    {
        if(viewport != null)
            viewport.changeKeyboardLayout();
    }

    public void closeKeyboardWindow()
    {
        //viewport.closeKeyboardWindow();
    }

    public void informKeyIndicators(boolean flag, boolean flag1, boolean flag2)
    {
        boolean flag3 = Helper.getLockingKeyState(20);
        boolean flag4 = Helper.getLockingKeyState(144);
        boolean flag5 = Helper.getLockingKeyState(145);
        int i = 0;
        if(flag3 != flag)
        {
            m_ignoreCapsLock = true;
            i |= 1;
            Helper.setLockingKeyState(20, flag);
        }
        if(flag4 != flag1)
        {
            m_ignoreNumLock = true;
            i |= 2;
            Helper.setLockingKeyState(144, flag1);
        }
        if(flag5 != flag2)
        {
            m_ignoreScrollLock = true;
            i |= 4;
            Helper.setLockingKeyState(145, flag2);
        }
        //viewport.informKeyIndicators(flag, flag1, flag2);
        startClearLockKeysThread(i);
    }

    public boolean hasFullControl()
    {
        return m_hasFullControl;
    }

    public boolean canTakeFullControl()
    {
        return m_canTakeFullControl;
    }

    public boolean canPrevTakeFullControl()
    {
        return m_prevCanTakeFullControl;
    }

    public boolean canRelinquishFullControl()
    {
        return m_canRelinquishFullControl;
    }

    public User[] getUsers()
    {
        return m_curUsers;
    }

    public void startDisconnecting()
    {
        ResourceMgr resourcemgr = ResourceMgr.getInstance();
        setExitProperly(true);
        setReasonForExiting(1);
        close();
    }

    public void onServerDisconnect(int i, String s)
    {
        ResourceMgr resourcemgr = ResourceMgr.getInstance();
        ArrayList arraylist = ArrayListStringBufferPool.getInstance();
        StringBuffer stringbuffer = StringBufferPool.getInstance(s);
        arraylist.add(stringbuffer);
        StringBufferPool.recycle(stringbuffer);
        ArrayListStringBufferPool.recycle(arraylist);
        setExitProperly(true);
        setReasonForExiting(1);
        close();
    }

    public void onServerHandshake(ServerHandshake serverhandshake)
    {
        if(!m_receivedMultiUserMessage)
        {
            //m_isKeyboardEnabled = serverhandshake.isKeyboardEnabled();
            //m_isVideoEnabled = serverhandshake.isVideoEnabled();
            //m_isMouseEnabled = serverhandshake.isMouseEnabled();
            m_isStorageEnabled = serverhandshake.isStorageEnabled();
            MessageSender.sendClientHandshake(m_isEmbeddedApplet, m_userName, new String(m_password), new String(m_passwordFull), 31, "");
        }
    }

    protected String fixFileSystemName(String s)
    {
        StringBuffer stringbuffer = StringBufferPool.getInstance(s);
        for(int i = 0; i < ILLEGAL_FILE_CHARS.length; i++)
            Helper.replaceString(stringbuffer, ILLEGAL_FILE_CHARS[i], "_");

        String s1 = stringbuffer.toString();
        StringBufferPool.recycle(stringbuffer);
        return s1;
    }

    public void onFirmwareVersion(FirmwareVersion firmwareversion)
    {
        m_firmwareVersion = firmwareversion.getFirmwareVersion();
        m_storageBaseVersion = fixFileSystemName(m_firmwareVersion);
        m_receivedFirmwareVersion = true;
        PrintStream printstream = null;
        try
        {
            String s = System.getProperty("user.home", "").trim();
            int i = s.indexOf(File.pathSeparatorChar);
            if(i > -1)
                s = s.substring(0, i).trim();
            if(s.length() > 0)
            {
                StringBuffer stringbuffer = StringBufferPool.getInstance(s);
                Helper.makeRelativePath(stringbuffer);
                stringbuffer.append(m_storageBaseVersion);
                File file = new File(stringbuffer.toString());
                if(file.isDirectory() || !file.exists())
                {
                    if(!file.exists())
                        file.mkdir();
                    Helper.makeRelativePath(stringbuffer);
                    stringbuffer.append("StorageServer.Port");
                    if(vlog.isDebugLoggingEnabled())
                        vlog.debug("Writing port file: '" + stringbuffer.toString() + "'");
                    printstream = new PrintStream(new FileOutputStream(stringbuffer.toString()));
                    printstream.print(getViewer().mahoganyStorageServerPort.getValue());
                }
                StringBufferPool.recycle(stringbuffer);
            }
        }
        catch(Exception exception)
        {
            if(vlog.isErrorLoggingEnabled())
            {
                vlog.error("Exception creating storeserver.port file:");
                vlog.error(exception);
            }
        }
        finally
        {
            if(printstream != null)
                printstream.close();
        }
        loadStorage();
    }

    public void onRelinquishFullControl()
    {
        MessageSender.relinquishFullControl();
        m_justRelinquishFullControl = true;
    }

    public void onRelinquishPrimaryControl()
    {
        m_hasFullControl = false;
        m_prevHasFullControl = false;
        m_justRelinquishFullControl = true;
        //viewer.viewOnly.setParam(true);
    }

    public boolean isHandshakeComplete()
    {
        return m_receivedMultiUserMessage;
    }

    public void onMultiUserState(MultiUserState multiuserstate)
    {
        boolean flag = !m_receivedMultiUserMessage;
        if(flag)
        {
            m_isExeStorageSupported = multiuserstate.isExeForStorage();
            m_isExeForKVM = multiuserstate.isExeForKVM();
            //viewport.setVisible(true);
        }
        MountedDriveMgr mounteddrivemgr = MountedDriveMgr.getInstance();
        m_receivedMultiUserMessage = true;
        User auser[] = multiuserstate.getUsers();
        boolean flag1 = m_requestDlg != null;
        boolean flag2 = m_newConnectionDlg != null;
        boolean flag3 = false;
        boolean flag4 = false;
        ResourceMgr resourcemgr = ResourceMgr.getInstance();
        if(multiuserstate.isAlt0Text())
            m_altText = 1;
        else
            m_altText = 0;
        for(int i = 0; i < m_curUsers.length; i++)
        {
            m_curUsers[i].setUserFlags(auser[i].getUserFlags());
            if(m_curUsers[i].isUserValid())
            {
                m_curUsers[i].setUserNameSize(auser[i].getUserNameSize());
                m_curUsers[i].setUserName(auser[i].getUserName());
                if(m_curUsers[i].isThisUserIdentity())
                {
                    m_userName = m_curUsers[i].getUserNameStr();
                    m_permssionMode = multiuserstate.getUserStatusMask();
                    //viewport.setUserNameInTitle(m_userName, m_hostName, m_permssionMode, m_isEncyptionOn);
                }
            } else
            {
                m_curUsers[i].setUserNameSize((byte)0);
            }
        }

        m_prevCanTakeFullControl = m_canTakeFullControl;
        m_prevHasFullControl = m_hasFullControl;
        m_allowTimeoutThread = multiuserstate.isSessionTimeoutAllowed();
        m_canTakeFullControl = multiuserstate.isAllowedToTakeFullControl();
        m_canDisconnectOthers = multiuserstate.isAllowedToDisconnectOtherUsers();
        m_canRelinquishFullControl = multiuserstate.isAllowedToRelinquishFullControl();
        m_canChangeLMS = multiuserstate.isAllowedToChangeLMS();
        m_canTurnScreenOn = multiuserstate.isAllowedToTurnScreenOn();
        m_canTurnScreenOff = multiuserstate.isAllowedToTurnScreenOff();
        m_isStorageEnabled = multiuserstate.isAllowedToDoStorage();
        m_canDoRemoteStorage = multiuserstate.isRemoteStorageAllowed();
        m_canDoStorageWrite = multiuserstate.isAllowedToDoWriteStorage();
        m_isDVDRomDeviceAllowed = multiuserstate.isDVDRomDeviceAllowed();
        m_isMemoryDeviceAllowed = multiuserstate.isMemoryDeviceAllowed();
        m_isForcedHLevelState = multiuserstate.isForceHLevelState();
        m_isLocalScreenOn = multiuserstate.isLocalScreenOn();
        mounteddrivemgr.setDevciesAllowed(m_isDVDRomDeviceAllowed, m_isMemoryDeviceAllowed);
        onTimeoutThread();
        switch(multiuserstate.getUserStatusMask())
        {
        case 1: // '\001'
            m_hasFullControl = false;
            break;

        case 0: // '\0'
            m_hasFullControl = true;
            break;

        default:
            m_hasFullControl = false;
            flag4 = true;
            break;
        }
//        if(!m_hasFullControl && m_prevHasFullControl)
//            viewport.clearAllSpecialKeys();
        //viewer.viewOnly.setParam(!m_hasFullControl);
        if(!flag4)
        {
            if(m_hasFullControl && !m_prevHasFullControl)
            {
                MessageSender.requestKeyIndicators();
            }
            if(m_canTakeFullControl && !m_justRelinquishFullControl && !m_hasFullControl && (!m_prevCanTakeFullControl || m_prevHasFullControl))
                flag1 = true;
            for(int j = 0; j < m_curUsers.length; j++)
            {
                if(!flag2 && m_curUsers[j].isUserValid() && !m_curUsers[j].isThisUserIdentity() && !m_curUsers[j].equals(m_prevUsers[j]))
                    flag2 = true;
                if(!flag3 && m_DisconnectSessionDialog != null && (m_curUsers[j].isUserValid() != m_prevUsers[j].isUserValid() || !m_curUsers[j].equals(m_prevUsers[j]) || m_curUsers[j].getUserFlags() != m_prevUsers[j].getUserFlags()))
                    flag3 = true;
            }

            if(flag3)
                m_DisconnectSessionDialog.updateSessionDialog(this);
            if(flag2 && flag1)
            {
                if(m_newConnectionDlg != null)
                {
                    m_newConnectionDlg.dispose();
                    m_newConnectionDlg = null;
                }
                if(m_requestDlg != null)
                {
                    m_requestDlg.dispose();
                    m_requestDlg = null;
                }
                if(m_sessionRequestDlg == null)
                {
                } else
                {
                    m_sessionRequestDlg.requestFocus();
                }
            } else
            if(flag2)
            {
                if(m_sessionRequestDlg == null)
                {
                    if(m_newConnectionDlg == null)
                    {
                    } else
                    {
                        m_newConnectionDlg.requestFocus();
                    }
                } else
                {
                    m_sessionRequestDlg.requestFocus();
                }
            } else
            if(flag1)
                if(m_sessionRequestDlg == null)
                {
                    if(m_requestDlg == null)
                    {
                    } else
                    {
                        m_requestDlg.requestFocus();
                    }
                } else
                {
                    m_sessionRequestDlg.requestFocus();
                }
            for(int k = 0; k < m_prevUsers.length; k++)
            {
                m_prevUsers[k].setUserFlags(m_curUsers[k].getUserFlags());
                if(m_prevUsers[k].isUserValid())
                {
                    m_prevUsers[k].setUserNameSize(m_curUsers[k].getUserNameSize());
                    m_prevUsers[k].setUserName(m_curUsers[k].getUserName());
                } else
                {
                    m_prevUsers[k].setUserNameSize((byte)0);
                }
            }

        } else
        {
            startDisconnecting();
        }
        m_justRelinquishFullControl = false;
        loadStorage();
    }

    public void onTimeoutThread()
    {
        if(m_allowTimeoutThread && m_isTimingDown)
            startTimeoutThread();
        else
            stopTimeoutThread();
    }

    protected void startTimeoutThread()
    {
        m_masterThread.setRunningTimeout(true);
    }

    public void stopTimeoutThread()
    {
        m_masterThread.setRunningTimeout(false);
    }

    public void onTakeFullControl()
    {
        if(m_requestDlg == null)
        {
            ResourceMgr resourcemgr = ResourceMgr.getInstance();
        } else
        {
            m_requestDlg.requestFocus();
        }
    }

    public void onStorageServerStatus(StorageStatus storagestatus)
    {
        if(isStorageWorking() && (isExeStorageSupported() || LUD.isLoaded()))
        {
            ResourceMgr resourcemgr = ResourceMgr.getInstance();
            ArrayList arraylist = ArrayListStringBufferPool.getInstance();
            StringBuffer stringbuffer = null;
            StringBuffer stringbuffer1 = null;
            StringBuffer stringbuffer2 = null;
            StringBuffer stringbuffer3 = null;
            Object obj = null;
            String s1 = "";
            String s2 = "";
            MountedDriveMgr mounteddrivemgr = MountedDriveMgr.getInstance();
            mounteddrivemgr.setDeviceShareStatus(storagestatus);
            if(storagestatus.getShareIndex0() != -1 && (storagestatus.getShareStatus0() == 2 || storagestatus.getShareStatus0() == 3))
            {
                stringbuffer = Helper.valueOf(storagestatus.getShareIndex0());
                arraylist.add(stringbuffer);
                stringbuffer1 = Helper.formatToHex(storagestatus.getShareStatus0());
                arraylist.add(stringbuffer1);
                stringbuffer2 = StringBufferPool.getInstance(resourcemgr.getResourceString("storage.status." + storagestatus.getShareStatus0()));
                arraylist.add(stringbuffer2);
                stringbuffer3 = StringBufferPool.getInstance(storagestatus.getSharePath0());
                arraylist.add(stringbuffer3);
                s1 = resourcemgr.getResourceString("err.status.device", arraylist);
            }
            if(storagestatus.getShareIndex1() != -1 && (storagestatus.getShareStatus1() == 2 || storagestatus.getShareStatus1() == 3))
            {
                if(stringbuffer != null)
                {
                    StringBufferPool.recycle(stringbuffer);
                    StringBufferPool.recycle(stringbuffer1);
                    StringBufferPool.recycle(stringbuffer2);
                    StringBufferPool.recycle(stringbuffer3);
                }
                arraylist.clear();
                stringbuffer = Helper.valueOf(storagestatus.getShareIndex1());
                arraylist.add(stringbuffer);
                stringbuffer1 = Helper.formatToHex(storagestatus.getShareStatus1());
                arraylist.add(stringbuffer1);
                stringbuffer2 = StringBufferPool.getInstance(resourcemgr.getResourceString("storage.status." + storagestatus.getShareStatus1()));
                arraylist.add(stringbuffer2);
                stringbuffer3 = StringBufferPool.getInstance(storagestatus.getSharePath1());
                arraylist.add(stringbuffer3);
                s2 = resourcemgr.getResourceString("err.status.device", arraylist);
            }
            if(s1.length() > 0 || s2.length() > 0)
            {
                StringBufferPool.recycle(stringbuffer);
                StringBufferPool.recycle(stringbuffer1);
                StringBufferPool.recycle(stringbuffer2);
                StringBufferPool.recycle(stringbuffer3);
                arraylist.clear();
                stringbuffer = Helper.formatIPAddress(storagestatus.getIPAddress(), storagestatus.getIPType());
                arraylist.add(stringbuffer);
                stringbuffer1 = Helper.valueOf(storagestatus.getPort());
                arraylist.add(stringbuffer1);
                stringbuffer2 = StringBufferPool.getInstance(s1);
                arraylist.add(stringbuffer2);
                stringbuffer3 = StringBufferPool.getInstance(s2);
                arraylist.add(stringbuffer3);
                String s = resourcemgr.getResourceString("err.status.device.msg", arraylist);
                if(m_stoargeStatusDlg == null)
                {
                } else
                {
                    m_stoargeStatusDlg.setMessage(s);
                    m_stoargeStatusDlg.pack();
                    m_stoargeStatusDlg.requestFocus();
                }
            }
            if(stringbuffer != null)
            {
                StringBufferPool.recycle(stringbuffer);
                StringBufferPool.recycle(stringbuffer1);
                StringBufferPool.recycle(stringbuffer2);
                StringBufferPool.recycle(stringbuffer3);
            }
            ArrayListStringBufferPool.recycle(arraylist);
            //viewport.getMountDialog().reloadDrives();
        }
    }

    public void onKeyboardCurrentLMS()
    {
        ResourceMgr resourcemgr = ResourceMgr.getInstance();
    }

    public boolean isLocalScreenOn()
    {
        return m_isLocalScreenOn;
    }

    public void setLocalScreenon(boolean flag)
    {
        m_isLocalScreenOn = flag;
    }

    public int getCurrentLocalMonitorSetting()
    {
        return CurrentLocalMonitorSetting;
    }

    public void setCurrentLocalMonitorSetting(int i)
    {
        CurrentLocalMonitorSetting = i;
    }

    public MahoganyViewer getViewer()
    {
        return viewer;
    }

    public void clearLockKeys(int i)
    {
        if((i & 1) != 0)
            m_ignoreCapsLock = false;
        if((i & 2) != 0)
            m_ignoreNumLock = false;
        if((i & 4) != 0)
            m_ignoreScrollLock = false;
    }

    protected void startClearLockKeysThread(int i)
    {
        m_masterThread.setKeysMask(i);
        m_masterThread.setRunningClearLockKeys(true);
    }

    public int getAltText()
    {
        return m_altText;
    }

    public boolean isAlt0Text()
    {
        return m_altText == 1;
    }

    public void okCancelCallback(ModallessOKCancelDlg modallessokcanceldlg, boolean flag)
    {
        if(modallessokcanceldlg.equals(m_requestDlg) || modallessokcanceldlg.equals(m_sessionRequestDlg))
        {
            if(flag)
                MessageSender.requestPrimaryControl();
            m_requestDlg = null;
            m_sessionRequestDlg = null;
        }
    }

    public void okCallback(ModallessOKMsgDlg modallessokmsgdlg)
    {
        if(modallessokmsgdlg.equals(m_secondaryControlDlg))
            m_secondaryControlDlg = null;
        else
        if(modallessokmsgdlg.equals(m_newConnectionDlg))
            m_newConnectionDlg = null;
        else
        if(modallessokmsgdlg.equals(m_stoargeStatusDlg))
            m_stoargeStatusDlg = null;
        else
        if(modallessokmsgdlg.equals(m_kvmExeFailedDlg))
            m_kvmExeFailedDlg = null;
        else
        if(modallessokmsgdlg.equals(m_msgDlg))
            m_msgDlg = null;
    }

    public static final int DEFAULT_STOARGE_PORT = 5901;
    public static final int DEFAULT_DEVICE_STORAGE_PORT = 5901;
    public static final int MAX_USER_NAME_LEN = 16;
    public static final int MAX_PWD_LEN = 20;
    public static final int MAX_FULL_PWD_LEN = 128;
    public static final int SOCKET_TIME_OUT = 20;
    public static final int INVALID_THRESHOLD_VALUE = 0x40000000;
    public static final int NO_ALT_TEXT = 0;
    public static final int ALT_0_TEXT = 1;
    public static final int INITIAL_HEADER_SIZE = 19;
    public static final int HEADER_OFFSET_BYTES_TO_READ = 9;
    public static final int BYTES_TO_READ_LEN = 8;
    public static final int DEVICE_TYPE_LEN = 3;
    public static final int HEX_RADIX = 16;
    public static final int CONNECTION_RETRIES = 3;
    public static final int INIT_STORAGE_SLEEP_TIME = 250;
    public static final String LINE_DELIMINATOR = "\n";
    public static final int CHMOD_NUM_CMD_LINE = 3;
    public static final String CHMOD_EXE_NAME = "chmod";
    public static final String CHMOD_PERMSSION = "774";
    public static final int KVM_EXE_NUM_CMD_LINE = 6;
    public static final String KVM_CMD_LINE_OPTION = "-integratedkvm";
    public static final int SUCCESS_RETURN_CODE = 0;
    public static final String HTTP_HEADER = "http:/";
    public static final String HTTPS_HEADER = "https:/";
    public static final char COLON = 58;
    public static final String ILLEGAL_FILE_CHARS[] = {
        ":", "/", "\\", "*", "?", "|", ">", "<", "&"
    };
    public static final String ILLEGAL_FILE_CHAR_TO_RPLACE = "_";
    public static final String LOCAL_HOST_ADDR = "127.0.0.1";
    private static LogWriter vlog;
    protected int CurrentLocalMonitorSetting;
    protected boolean m_isLocalScreenOn;
    protected ModallessOKMsgDlg m_secondaryControlDlg;
    protected ModallessOKMsgDlg m_newConnectionDlg;
    protected ModallessOKMsgDlg m_stoargeStatusDlg;
    protected ModallessOKMsgDlg m_kvmExeFailedDlg;
    protected ModallessOKMsgDlg m_msgDlg;
    protected boolean m_exitProperly;
    protected boolean m_alraedyExited;
    protected int m_reasonForExiting;
    protected Robot m_robot;
    protected int m_currentFrameCt;
    protected int m_xFrameCtThreshold;
    protected int m_yFrameCtThreshold;
    protected String m_hostAddress;
    InetAddress serverHost;
    int serverPort;
    protected int m_requiredKeyboardPort;
    protected int m_requiredKeyboardSecurePort;
    protected boolean m_isEmbeddedApplet;
    protected String m_userName;
    protected char m_password[];
    protected char m_passwordFull[];
    protected User m_prevUsers[];
    protected User m_curUsers[];
    protected String m_hostName;
    protected String m_firmwareVersion;
    protected String m_storageBaseVersion;
    protected Socket sock;
    protected Socket m_storageDiscoverySocket;
    protected JavaInStream jis;
    protected JavaOutStream jos;
    protected JavaInStream m_storageDiscoveryJis;
    protected JavaOutStream m_storageDiscoveryJos;
    MahoganyViewer viewer;
    DesktopWindow desktop;
    ViewportFrame viewport;
    protected boolean shuttingDown;
    boolean m_isRunning;
    protected String m_sessionType;
    protected int m_permssionMode;
    protected int m_storagePort;
    protected boolean m_isHLevelCompressionOn;
    protected boolean m_isEncyptionOn;
    protected boolean m_hasFullControl;
    protected boolean m_prevHasFullControl;
    protected boolean m_canTakeFullControl;
    protected boolean m_prevCanTakeFullControl;
    protected boolean m_canDisconnectOthers;
    protected boolean m_isStorageEnabled;
    protected boolean m_isExeStorageSupported;
    protected boolean m_isExeForKVM;
    protected boolean m_isStorageWorking;
    protected boolean m_canDoRemoteStorage;
    protected boolean m_canDoStorageWrite;
    protected boolean m_canRelinquishFullControl;
    protected boolean m_justRelinquishFullControl;
    protected boolean m_canChangeLMS;
    protected boolean m_canTurnScreenOn;
    protected boolean m_canTurnScreenOff;
    protected boolean m_isDVDRomDeviceAllowed;
    protected boolean m_isMemoryDeviceAllowed;
    protected boolean m_ignoreCapsLock;
    protected boolean m_ignoreNumLock;
    protected boolean m_ignoreScrollLock;
    protected boolean m_receivedMultiUserMessage;
    protected boolean m_loadedStorageSupport;
    protected boolean m_receivedFirmwareVersion;
    protected boolean m_allowTimeoutThread;
    protected boolean m_isTimingDown;
    protected boolean m_isKeyboardEnabled;
    protected boolean m_isVideoEnabled;
    protected boolean m_isMouseEnabled;
    protected boolean m_isForcedHLevelState;
    protected boolean m_isStandbyPower;
    protected boolean m_isSSLConnection;
    protected MessageReceiverThread m_msgRecvThread;
    protected MasterThread m_masterThread;
    protected FrameCtThread m_frameCtThread;
    protected StorageDiscoveryThread m_storageDiscoveryThread;
    protected StartDeviceDiscoveryThread m_startStorageThread;
    protected Thread m_mainThread;
    protected int m_startWidthViewport;
    protected int m_startHeightViewport;
    protected int m_altText;
    protected HashSet m_keysPressedDownSet;
    protected HashSet m_keysTypedSet;
    protected HashSet m_asciiKeysSet;
    protected PerformanceDialog m_performanceDlg;
    protected AboutDialog m_aboutDlg;
    protected SettingsDialog m_settingsDlg;
    protected DisconnectSessionDialog m_DisconnectSessionDialog;
    protected ModallessOKCancelDlg m_requestDlg;
    protected ModallessOKCancelDlg m_sessionRequestDlg;

    static 
    {
        vlog = new LogWriter((com.serverengines.mahogany.CConn.class).getName());
    }
}
