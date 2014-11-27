package com.avocent.vm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class VirtualMedia
  implements InterfaceDiskMappingListener, InterfaceDriveActivityListener
{
  private static final String KVM_LAUNCHED = "Launched";
  private static final byte CONNECT_CANCELLED = -1;
  private static final int INPUT_BUFFER_SIZE = 32768;
  private static final String TRACE_CONTEXT = "VirtualMedia";
  static final com.avocent.lib.e.b RES = JFrameVirtualMedia.RES;
  static boolean m_bShowNetworkErrorDlg;
  private static VirtualMedia vmObject;
  private static String duplicateKey;
  OptionsFile options;
  private static final byte PROTOCOL_AVMP = 2;
  private static final int CONNECTION_TYPE_NONE = 0;
  private static final int CONNECTION_TYPE_TCP_CLEARTEXT = 1;
  private static final int CONNECTION_TYPE_TCP_RC4 = 2;
  private static final int CONNECTION_TYPE_SSL_ANONYMOUS = 4;
  private static final int CONNECTION_TYPE_SSL_CERT = 8;
  public static final int MAP_RESULT_FAIL = -1;
  private static HeartBeat m_heartBeat;
  private DataOutputStream m_dosAppliance;
  private DataInputStream m_disAppliance;
  private static ApplianceSession m_applianceSession;
  private LocalDrives m_localDrives;
  private Socket m_socket = null;
  private static abstractJFrameVM m_frame;
  private static Properties m_propertiesLaunch;
  private com.avocent.lib.d.a m_sessionStatus = null;
  private static boolean m_bPreempt;
  private static boolean m_bPreemptWait;
  private Vector<VirtualMediaListener> m_vVirtualMediaListeners = new Vector();
  boolean m_cancel = false;

  public VirtualMedia()
  {
    try
    {
      this.m_localDrives = new LocalDrives(false, false);
    }
    catch (Exception localException)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, localException.getMessage(), localException);
    }
  }

  public void setCancel(boolean paramBoolean)
  {
    this.m_cancel = paramBoolean;
  }

  public VirtualMedia(SessionParameters paramSessionParameters)
    throws com.avocent.lib.b.a
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia constructor returned from parseCommandLine, about to create LocalDrives");
    this.m_localDrives = new LocalDrives(false);
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia constructor returned from LocalDrives constructor, about to create JFrameVirtualMedia");
    String str = OptionsFile.getString("FrameMain", "com.avocent.vm.JFrameVirtualMedia");
    try
    {
      Class localClass = Class.forName(str);
      Class[] arrayOfClass = { SessionParameters.class, LocalDrives.class };
      Constructor localConstructor = localClass.getConstructor(arrayOfClass);
      Object[] arrayOfObject = { paramSessionParameters, this.m_localDrives };
      m_frame = (abstractJFrameVM)localConstructor.newInstance(arrayOfObject);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      throw new com.avocent.lib.b.a("Could not construct frame");
    }
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia constructor returned from JFrameVirtualMedia constructor");
  }

  public static void close()
  {
    if (m_applianceSession != null)
    {
      System.out.println("VirtualMedia: closing session.");
      m_applianceSession.terminate();
    }
  }

  public void addListener(VirtualMediaListener paramVirtualMediaListener)
  {
    this.m_vVirtualMediaListeners.add(paramVirtualMediaListener);
  }

  public void removeListener(VirtualMediaListener paramVirtualMediaListener)
  {
    this.m_vVirtualMediaListeners.remove(paramVirtualMediaListener);
  }

  public void listenerDiskMapping(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int j = JFrameVirtualMedia.b;
    for (int i = 0; i < this.m_vVirtualMediaListeners.size(); i++) {
      VirtualMediaListener localVirtualMediaListener = (VirtualMediaListener)this.m_vVirtualMediaListeners.get(i);
      localVirtualMediaListener.mappingChanged(paramInt2, paramBoolean);
    }
  }

  public void driveRead(int paramInt1, int paramInt2)
  {
    int j = JFrameVirtualMedia.b;
    for (int i = 0; i < this.m_vVirtualMediaListeners.size(); i++) {
      VirtualMediaListener localVirtualMediaListener = (VirtualMediaListener)this.m_vVirtualMediaListeners.get(i);
      localVirtualMediaListener.driveRead(paramInt1, paramInt2);
    }
  }

  public void driveWrite(int paramInt1, int paramInt2)
  {
    int j = JFrameVirtualMedia.b;
    for (int i = 0; i < this.m_vVirtualMediaListeners.size(); i++) {
      VirtualMediaListener localVirtualMediaListener = (VirtualMediaListener)this.m_vVirtualMediaListeners.get(i);
      localVirtualMediaListener.driveWrite(paramInt1, paramInt2);
    }
  }

  public void driveError(int paramInt1, int paramInt2)
  {
    int j = JFrameVirtualMedia.b;
    for (int i = 0; i < this.m_vVirtualMediaListeners.size(); i++) {
      VirtualMediaListener localVirtualMediaListener = (VirtualMediaListener)this.m_vVirtualMediaListeners.get(i);
      localVirtualMediaListener.driveError(paramInt1, paramInt2);
    }
  }

  public static Socket ProxySelectorOffSocket(String paramString, int paramInt)
    throws com.avocent.lib.b.a
  {
    Socket localSocket = null;
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ProxySelectorOffSocket szIpAddress=" + paramString + " nTcpPort=" + paramInt);
    localSocket = new Socket();
    InetSocketAddress localInetSocketAddress = new InetSocketAddress(paramString, paramInt);
    for (int i = 0; i < 30; i++) {
      try
      {
        localSocket.connect(localInetSocketAddress, 1000);
        break;
      }
      catch (SocketTimeoutException localSocketTimeoutException)
      {
        com.avocent.lib.a.b.a(TRACE_CONTEXT, RES.b("VirtualMedia_RECEIVING_SESSION_SETUP"));
      }
      catch (Exception localException)
      {
        throw new com.avocent.lib.b.a("Failure in setting up ProxySelectorOffSocket Exception:" + localException.getMessage());
      }
    }
    return localSocket;
  }

  private void connect(String paramString, int paramInt)
    throws Exception
  {
    this.m_socket = ProxySelectorOffSocket(paramString, paramInt);
    this.m_socket.setTcpNoDelay(true);
    this.m_socket.setTrafficClass(24);
    this.m_socket.setReceiveBufferSize(32768);
    this.m_socket.setSendBufferSize(65000);
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "Socket connected to: " + this.m_socket.getInetAddress().getHostAddress().toString());
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(this.m_socket.getOutputStream());
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(this.m_socket.getInputStream());
    this.m_dosAppliance = new DataOutputStream(localBufferedOutputStream);
    this.m_disAppliance = new DataInputStream(localBufferedInputStream);
  }

  private void sendSessionRequest(byte paramByte1, byte paramByte2, byte paramByte3, int paramInt, byte[] paramArrayOfByte)
    throws IOException
  {
    int j = JFrameVirtualMedia.b;
    if (paramArrayOfByte.length > 32)
      throw new IOException("Random number too long");
    this.m_dosAppliance.writeBytes("APCP");
    this.m_dosAppliance.writeInt(53);
    this.m_dosAppliance.writeShort(256);
    this.m_dosAppliance.writeShort(0);
    this.m_dosAppliance.writeByte(paramByte1);
    this.m_dosAppliance.writeByte(paramByte2);
    this.m_dosAppliance.writeByte(paramByte3);
    this.m_dosAppliance.writeByte(0);
    this.m_dosAppliance.writeInt(paramInt);
    this.m_dosAppliance.writeByte(paramArrayOfByte.length);
    this.m_dosAppliance.write(paramArrayOfByte);
    int i = 32 - paramArrayOfByte.length;
    do
    {
      if (i <= 0)
        break;
      this.m_dosAppliance.writeByte(0);
      i--;
    }
    while (j == 0);
    this.m_dosAppliance.flush();
  }

  private void receiveSessionSetup(SessionSetup paramSessionSetup)
    throws IOException, com.avocent.lib.b.a
  {
    int i3 = JFrameVirtualMedia.b;
    byte[] arrayOfByte = new byte[4];
    this.m_socket.setSoTimeout(2000);
    int i = 90;
    int j = 0;
    do
    {
      try
      {
        j = this.m_disAppliance.read(arrayOfByte);
      }
      catch (SocketTimeoutException localSocketTimeoutException)
      {
      }
      i--;
      if (j == 0)
        com.avocent.lib.a.b.a(TRACE_CONTEXT, RES.b("VirtualMedia_RECEIVING_SESSION_SETUP"));
    }
    while ((j == 0) && (i > 0));
    if (i == 0)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "(DSConnect DSConnectApplianceAVSP) socket timed out");
      throw new IOException("Header read timed out");
    }
    this.m_socket.setSoTimeout(0);
    if ((arrayOfByte[0] != 65) || (arrayOfByte[1] != 80) || (arrayOfByte[2] != 67) || (arrayOfByte[3] != 80))
      throw new IOException("Header incorrect");
    int k = this.m_disAppliance.readInt();
    int m = this.m_disAppliance.readShort();
    if (m != -32512)
      throw new IOException("Incorrect message type");
    int n = this.m_disAppliance.readShort();
    paramSessionSetup.m_bVersionMajor = this.m_disAppliance.readByte();
    paramSessionSetup.m_bVersionMinor = this.m_disAppliance.readByte();
    paramSessionSetup.m_nConnectionCapabilities = this.m_disAppliance.readInt();
    paramSessionSetup.m_sTcpPort = this.m_disAppliance.readShort();
    int i1 = this.m_disAppliance.readByte();
    paramSessionSetup.m_abRandomAuth = new byte[i1];
    this.m_disAppliance.read(paramSessionSetup.m_abRandomAuth);
    for (int i2 = 32 - paramSessionSetup.m_abRandomAuth.length; i2 > 0; i2--) {
      this.m_disAppliance.readByte();
    }
  }

  private void login(String paramString1, byte[] paramArrayOfByte, String paramString2, byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4)
    throws IOException
  {
    int j = JFrameVirtualMedia.b;
    this.m_dosAppliance.writeBytes("AVMP");
    this.m_dosAppliance.writeInt(153);
    this.m_dosAppliance.writeShort(256);
    this.m_dosAppliance.writeShort(0);
    byte[] arrayOfByte1 = paramString1.getBytes("UTF-8");
    this.m_dosAppliance.writeByte(arrayOfByte1.length);
    this.m_dosAppliance.write(arrayOfByte1);
    int i;
    for (i = 96 - arrayOfByte1.length; i > 0; i--)
    {
      this.m_dosAppliance.writeByte(0);
    }
    this.m_dosAppliance.write(paramArrayOfByte);
    for (i = 32 - paramArrayOfByte.length; i > 0; i--)
    {
      this.m_dosAppliance.writeByte(0);
    }
    byte[] arrayOfByte2 = new byte[8];
    this.m_dosAppliance.write(arrayOfByte2);
    this.m_dosAppliance.writeByte(paramByte1);
    this.m_dosAppliance.writeByte(paramByte2);
    this.m_dosAppliance.writeByte(paramByte3);
    this.m_dosAppliance.writeByte(paramByte4);
    this.m_dosAppliance.flush();
  }

  private LoginStatus receiveLoginStatus()
    throws IOException, com.avocent.lib.b.a
  {
    int i4 = JFrameVirtualMedia.b;
    LoginStatus localLoginStatus = new LoginStatus(null);
    byte[] arrayOfByte1 = new byte[4];
    this.m_socket.setSoTimeout(500);
    int i = 90;
    int j = 0;
    do
    {
      if (this.m_cancel == true)
      {
        localLoginStatus.m_bStatus = -1;
        if (i4 == 0)
          break;
      }
      try
      {
        j = this.m_disAppliance.read(arrayOfByte1);
      }
      catch (SocketTimeoutException localSocketTimeoutException)
      {
      }
      i--;
      if ((j == 0) && (!m_bPreempt))
        com.avocent.lib.a.b.a(TRACE_CONTEXT, RES.b("VirtualMedia_RECEIVING_LOGIN_STATUS"));
    }
    while ((j == 0) && (i > 0));
    if (i == 0)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia::receiveLoginStatus: socket timed out");
      throw new IOException("Header read timed out");
    }
    this.m_socket.setSoTimeout(0);
    if ((arrayOfByte1[0] != 65) || (arrayOfByte1[1] != 86) || (arrayOfByte1[2] != 77) || (arrayOfByte1[3] != 80))
      throw new IOException("Header incorrect");
    int k = this.m_disAppliance.readInt();
    int m = this.m_disAppliance.readShort();
    if (m != -32512)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia:receiveLoginStatus message type: " + m);
      throw new IOException("Incorrect message type");
    }
    int n = this.m_disAppliance.readShort();
    localLoginStatus.m_bStatus = this.m_disAppliance.readByte();
    int i1 = this.m_disAppliance.readByte();
    localLoginStatus.m_bCanRejectPreemption = ((i1 & 0x1) != 0);
    int i2 = this.m_disAppliance.readByte();
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "receiveLoginStatus bUsernameLength=" + i2);
    byte[] arrayOfByte2 = new byte[i2];
    if (i2 > 0)
      this.m_disAppliance.read(arrayOfByte2);
    localLoginStatus.m_szUsername = new String(arrayOfByte2, "UTF-8");
    for (int i3 = 96 - arrayOfByte2.length; i3 > 0; i3--)
    {
      this.m_disAppliance.readByte();
    }
    return localLoginStatus;
  }

  private byte[] getAuthResult(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
    throws NoSuchAlgorithmException
  {
    byte[] arrayOfByte = new byte[paramArrayOfByte1.length + paramArrayOfByte2.length + paramArrayOfByte3.length];
    System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0, paramArrayOfByte1.length);
    System.arraycopy(paramArrayOfByte2, 0, arrayOfByte, paramArrayOfByte1.length, paramArrayOfByte2.length);
    System.arraycopy(paramArrayOfByte3, 0, arrayOfByte, paramArrayOfByte1.length + paramArrayOfByte2.length, paramArrayOfByte3.length);
    return arrayOfByte;
  }

  private static void parseCommandLine(String[] paramArrayOfString, SessionParameters paramSessionParameters)
    throws com.avocent.lib.b.a
  {
    int m = JFrameVirtualMedia.b;
    String str1 = "title=";
    String str2 = "user=";
    String str3 = "password=";
    String str4 = "ip=";
    String str5 = "port=";
    String str7 = "iso=";
    String str8 = "maxkb=";
    String str9 = "timeout=";
    String str6 = "F1=";
    String str10 = "";
    m_propertiesLaunch = new Properties();
    int i;
    for (i = 0; i < paramArrayOfString.length; i++) {
      String str11 = paramArrayOfString[i];
      str10 = str10 + str11 + " ";
      String[] arrayOfString = str11.split("=", 2);
      m_propertiesLaunch.setProperty(arrayOfString[0], arrayOfString[1]);
    }
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia.parseCommandLine commandline: " + str10);
    for (i = 0; i < paramArrayOfString.length; i++) {
      int j = paramArrayOfString[i].indexOf(str1);
      if (j >= 0)
      {
        paramSessionParameters.m_szTitle = paramArrayOfString[i].substring(j + str1.length());
        if (paramSessionParameters.m_szTitle.charAt(0) == '"')
          paramSessionParameters.m_szTitle = paramSessionParameters.m_szTitle.substring(1);
        int k = paramSessionParameters.m_szTitle.lastIndexOf('"');
        if (k > 0)
        {
          paramSessionParameters.m_szTitle = paramSessionParameters.m_szTitle.substring(0, k);
          if (m == 0);
        }
      }
      else
      {
        j = paramArrayOfString[i].indexOf(str2);
        if (j >= 0)
        {
          paramSessionParameters.m_szUsername = paramArrayOfString[i].substring(j + str2.length());
          if (m == 0);
        }
        else
        {
          j = paramArrayOfString[i].indexOf(str3);
          if (j >= 0)
          {
            paramSessionParameters.m_szPassword = paramArrayOfString[i].substring(j + str3.length());
            if (m == 0);
          }
          else
          {
            j = paramArrayOfString[i].indexOf(str4);
            if (j >= 0)
            {
              paramSessionParameters.m_ip = paramArrayOfString[i].substring(j + str4.length());
              if (m == 0);
            }
            else
            {
              j = paramArrayOfString[i].indexOf(str5);
              if (j >= 0)
              {
                paramSessionParameters.m_port = new Integer(paramArrayOfString[i].substring(j + str5.length())).intValue();
                if (m == 0);
              }
              else
              {
                j = paramArrayOfString[i].indexOf(str1);
                if (j >= 0)
                {
                  paramSessionParameters.m_szTitle = paramArrayOfString[i].substring(j + str1.length());
                  if (m == 0);
                }
                else
                {
                  j = paramArrayOfString[i].indexOf(str6);
                  if (j >= 0)
                    paramSessionParameters.m_vFolder = paramArrayOfString[i].substring(j + str6.length());
                  else
                  {
                    j = paramArrayOfString[i].indexOf(str7);
                    if (j >= 0)
                      paramSessionParameters.m_szISO = paramArrayOfString[i].substring(j + str7.length());
                    else
                    {
                      j = paramArrayOfString[i].indexOf(str8);
                      if (j >= 0)
                        paramSessionParameters.m_maxkb = new Integer(paramArrayOfString[i].substring(j + str8.length())).longValue();
                      else
                      {
                        j = paramArrayOfString[i].indexOf(str9);
                        if (j >= 0)
                          paramSessionParameters.m_timeout = new Integer(paramArrayOfString[i].substring(j + str9.length())).intValue();
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private void runApplianceSession(Socket paramSocket)
  {
    int i = JFrameVirtualMedia.b;
    m_applianceSession = new ApplianceSession(this.m_disAppliance, this.m_dosAppliance, paramSocket, this.m_localDrives, this.m_sessionStatus);
    if (m_frame != null)
    {
      try
      {
        m_frame.init(m_applianceSession);
        m_frame.setAvailable(true);
      } catch (Exception e) { return; }
    }
    do
      while (true)
      {
        if (!m_applianceSession.isAlive())
          return;
        try
        {
          Thread.sleep(100L);
        }
        catch (InterruptedException localInterruptedException)
        {
        }
      }
    while (i == 0);
  }

  public LocalDrives getLocalDrives()
  {
    return this.m_localDrives;
  }

  public Socket getSocket()
  {
    return this.m_socket;
  }

  public void setSocket(Socket paramSocket)
  {
    this.m_socket = paramSocket;
  }

  public void setDos(DataOutputStream paramDataOutputStream)
  {
    this.m_dosAppliance = paramDataOutputStream;
  }

  public void setDis(DataInputStream paramDataInputStream)
  {
    this.m_disAppliance = paramDataInputStream;
  }

  public void closeDos()
    throws IOException
  {
    this.m_dosAppliance.close();
  }

  public void closeDis()
    throws IOException
  {
    this.m_disAppliance.close();
  }

  private boolean connectionBlocked(boolean paramBoolean, String paramString1, String paramString2)
  {
    boolean bool = false;
    Object[] arrayOfObject;
    String str1;
    if (paramBoolean)
    {
      arrayOfObject = new Object[] { paramString1, paramString2 };
      str1 = MessageFormat.format(RES.b("VirtualMedia_SERVER_UNAVAILABLE_CURRENTLY_BEING_VIEWED_CAN_PREEMPT"), arrayOfObject);
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia: "+ str1);
    }
    else
    {
      arrayOfObject = new Object[] { paramString1, paramString2 };
      str1 = MessageFormat.format(RES.b("VirtualMedia_SERVER_UNAVAILABLE_CURRENTLY_BEING_VIEWED"), arrayOfObject);
      String str2 = RES.b("VirtualMedia_SESSION_NOT_AVAILABLE");
      com.avocent.lib.a.b.a(TRACE_CONTEXT,"VirtualMedia: "+ str1+" "+str2);
    }
    return bool;
  }

  public static void VM_main(String[] paramArrayOfString)
  {
    int i1 = JFrameVirtualMedia.b;
    if (!com.avocent.lib.a.b.b())
      com.avocent.lib.a.b.a(null);
    String str1 = "";
    for (int i = 0; i < paramArrayOfString.length; i++) {
      str1 = str1 + paramArrayOfString[i] + " ";
    }
    com.avocent.lib.a.b.a("VirtualMedia - main", str1);
    VirtualMedia localVirtualMedia = null;
    SessionParameters localSessionParameters = new SessionParameters();
    try
    {
      parseCommandLine(paramArrayOfString, localSessionParameters);
      String str2 = localSessionParameters.m_ip;
      int j = localSessionParameters.m_port;
      String str5 = "VM_APP_" + str2 + "_" + j;
      str5 = str5.replace(' ', '_');
      if (vmObject != null)
      {
        Socket localSocket = vmObject.m_socket;
        if ((localSocket != null) && (localSocket.isConnected()) && (str5.equals(duplicateKey)))
        {
          return;
        }
      }
      localVirtualMedia = new VirtualMedia(localSessionParameters);
      vmObject = localVirtualMedia;
      int k = 0;
      if (OptionsFile.getBoolean("LoginTimeout", false))
        k = OptionsFile.getInteger("LoginTimeout", 30);
      SessionSetup localSessionSetup = new SessionSetup();
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia returned from parseCommandLine");
      String str6 = MessageFormat.format(RES.b("VirtualMedia_CONNECTING_TO"), new Object[] { localSessionParameters.m_ip });
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia returned from UpdateAndCheckProgressDlg");
      try
      {
        localVirtualMedia.connect(str2, j);
      }
      catch (Exception localException2)
      {
        com.avocent.lib.a.b.a(TRACE_CONTEXT, localException2.getMessage(), localException2);
        throw new IOException("Socket connect error");
      }
      byte[] arrayOfByte = new byte[32];
      Random localRandom = new Random();
      localRandom.nextBytes(arrayOfByte);
      com.avocent.lib.a.b.a(TRACE_CONTEXT, RES.b("VirtualMedia_SENDING_SESSION_REQUEST"));
      int m = 5;
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia sendSessionRequest with nConnectionRequestType=" + m);
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia ip=" + str2 + "p=" + j);
      localVirtualMedia.sendSessionRequest((byte)2, (byte)0, (byte)0, m, arrayOfByte);
      com.avocent.lib.a.b.a(TRACE_CONTEXT, RES.b("VirtualMedia_RECEIVING_SESSION_SETUP"));
      localVirtualMedia.receiveSessionSetup(localSessionSetup);
      com.avocent.lib.a.b.a(TRACE_CONTEXT, RES.b("VirtualMedia_GETTING_CONNECTION_CAPABILITIES"));
      Object localObject1;
      Object localObject2;
      Object localObject3;
      switch (localSessionSetup.getConnectionCapabilities())
      {
      case 1:
        com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia sessionSetup connection capability=CONNECTION_TYPE_TCP_CLEARTEXT");
        if (i1 == 0)
          break;
      case 2:
        com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia sessionSetup connection capability=CONNECTION_TYPE_TCP_RC4");
        com.avocent.lib.a.b.a(TRACE_CONTEXT, RES.b("Not ready to do RC4 encryption yet"));
        return;
      case 4:
        com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia sessionSetup connection capability=CONNECTION_TYPE_SSL_ANONYMOUS");
        localObject1 = createSSLSocket(localVirtualMedia.getSocket(), localSessionParameters.m_ip, localSessionParameters.m_port, k);
        com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia ssl socket created");
        localObject2 = new BufferedOutputStream(((Socket)localObject1).getOutputStream());
        localObject3 = new BufferedInputStream(((Socket)localObject1).getInputStream());
        localVirtualMedia.setDos(new DataOutputStream((OutputStream)localObject2));
        localVirtualMedia.setDis(new DataInputStream((InputStream)localObject3));
        localVirtualMedia.setSocket((Socket)localObject1);
      case 8:
        if ((i1 == 0) || (i1 == 0))
          break;
      case 0:
        com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia sessionSetup connection capability=CONNECTION_TYPE_NONE");
        localObject1 = RES.b("VirtualMedia_NO_CONNECTION_TYPE_IS_AVAILABLE");
        localObject2 = RES.b("VirtualMedia_CANNOT_START_VIRTUAL_MEDIA");
        com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia"+(String)localObject1+" "+(String)localObject2);
        return;
      case 3:
      case 5:
      case 6:
      case 7:
      }
      if (localSessionSetup.getConnectionCapabilities() == 8)
        throw new Exception("SSL_CERT Option not supported");
      if (!m_bPreemptWait)
        com.avocent.lib.a.b.a(TRACE_CONTEXT, RES.b("VirtualMedia_LOGGING_IN"));
      for (int n=0; n == 0; )
      {
        com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia::Main: Try Login");
        localObject2 = localVirtualMedia.getAuthResult(localSessionSetup.m_abRandomAuth, localSessionParameters.m_szUsername.getBytes(), localSessionParameters.m_szPassword.getBytes());
        com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia sessionSetup using username and password type login");
        localObject3 = "";
        localVirtualMedia.login(localSessionParameters.m_szUsername, localSessionParameters.m_szPassword.getBytes("UTF-8"), (String)localObject3, (byte)0, (byte)0, (byte)0, (byte)(m_bPreempt ? 1 : 0));
        com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia::Main: After login");
        VirtualMedia.LoginStatus localLoginStatus = localVirtualMedia.receiveLoginStatus();
        com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia::Main: After status: " + localLoginStatus.m_bStatus);

        if (localLoginStatus.m_bStatus == 0)
        {
          com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia::Main: Login Succeeded");
          n = 1;
          com.avocent.lib.a.b.a(TRACE_CONTEXT, RES.b("VirtualMedia_LOGIN_SUCCESSFUL_SENDING"));
          com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia about to set frame title to " + localSessionParameters.m_szTitle);
          if (m_heartBeat != null)
            m_heartBeat.stop();
          localVirtualMedia.runApplianceSession(localVirtualMedia.getSocket());
        }
        else
        {
          m_bShowNetworkErrorDlg = false;
          loginFailedMessage(localVirtualMedia, localSessionParameters, localLoginStatus);
          com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia::Main: Login Failed");
        }
      }
    }
    catch (IOException localIOException)
    {
      if (m_bShowNetworkErrorDlg == true)
      {
        String str4 = RES.b("VirtualMedia_NETWORK_ERROR_OCCURRED");
        String str5 = RES.b("VirtualMedia_NETWORK_ERROR");
        com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia::Main "+str4+": "+str5);
      }
      exitDueToException(localVirtualMedia, localIOException);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      String str4 = RES.b("VirtualMedia_THE_REQUESTED_ENCRYPTION_ALGORITHM");
      String str5 = RES.b("VirtualMedia_ENCRYPTION_ALGORITHM_NOT_AVAILABLE");
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia::Main "+str4+": "+str5);
      exitDueToException(localVirtualMedia, localNoSuchAlgorithmException);
    }
    catch (com.avocent.lib.b.a locala)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia::Main ExceptionRequestFailed");
      exitDueToException(localVirtualMedia, locala);
    }
    catch (Exception localException1)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia::Main Exception");
      exitDueToException(localVirtualMedia, localException1);
    }
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia::Main call system.exit");
    String str3 = m_propertiesLaunch.getProperty(KVM_LAUNCHED);
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia::Main KVM Launched  " + str3 + "]");
    if (str3 == null)
      System.exit(0);
  }

  private static void loginFailedMessage(VirtualMedia paramVirtualMedia, SessionParameters paramSessionParameters, VirtualMedia.LoginStatus paramLoginStatus)
    throws Exception
  {
    String str1 = RES.b("VirtualMedia_LOGIN_FAILED");
    abstractJFrameVM localabstractJFrameVM = m_frame;
    String str2;
    switch (paramLoginStatus.m_bStatus)
    {
    case 11:
      m_bPreempt = paramVirtualMedia.connectionBlocked(true, paramSessionParameters.m_ip, RES.b("VirtualMedia_ANOTHER_USER"));
      if (!m_bPreempt)
        throw new Exception("Login failure");
      break;
    case 6:
      m_heartBeat = new HeartBeat(paramVirtualMedia.m_dosAppliance);
      m_bPreempt = paramVirtualMedia.connectionBlocked(true, paramSessionParameters.m_ip, paramLoginStatus.m_szUsername);
      if (!m_bPreempt)
        throw new Exception("Login failure");
      break;
    case 1:
      str2 = RES.b("VirtualMedia_Invalid_UserName");
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia: "+str1+": "+str2);
      throw new Exception("Login failure");
    case 2:
      str2 = RES.b("VirtualMedia_InvalidPassword");
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia: "+str1+": "+str2);
      throw new Exception("Login failure");
    case 3:
      str2 = RES.b("VirtualMedia_Channel_Access_Denied");
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia: "+str1+": "+str2);
      throw new Exception("Login failure");
    case 4:
      str2 = RES.b("VirtualMedia_Channel_In_Use");
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia: "+str1+": "+str2);
      throw new Exception("Login failure");
    case 5:
      str2 = RES.b("VirtualMedia_Channel_Not_Found");
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia: "+str1+": "+str2);
      throw new Exception("Login failure");
    case 8:
      str2 = RES.b("VirtualMedia_All_Channels_In_Use");
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia: "+str1+": "+str2);
      throw new Exception("Login failure");
    case 22:
      str2 = RES.b("VirtualMedia_Login_Failure_Auth_Server");
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia: "+str1+": "+str2);
      throw new Exception("Login failure");
    case 23:
      com.avocent.lib.c.b.a.a(null, RES.b("VirtualMedia_Login_Failure_Invalid_Expired_Cert_Auth_Server"));
      str2 = RES.b("VirtualMedia_Login_Failure_Invalid_Expired_Cert_Auth_Server");
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia: "+str1+": "+str2);
      throw new Exception("Login failure");
    case 52:
      str2 = RES.b("ApplianceSession_DISCONNECT_PREEMPT_REFUSED");
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia: "+str1+": "+str2);
      throw new Exception("Login failure");
    default:
      str2 = RES.b("VirtualMedia_LOGIN_STATUS") + paramLoginStatus.m_bStatus;
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia: "+str1+": "+str2);
      throw new Exception("Login failure");
    }
  }

  private static void exitDueToException(VirtualMedia paramVirtualMedia, Exception paramException)
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, paramException.getMessage(), paramException);
    if (m_heartBeat != null)
      m_heartBeat.stop();
    Socket localSocket = null;
    if (paramVirtualMedia != null)
      localSocket = paramVirtualMedia.getSocket();
    if (localSocket != null)
      try
      {
        localSocket.close();
      }
      catch (IOException localIOException)
      {
      }
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia::ExitDueToException call system.exit");
  }

  public static Socket createSSLSocket(Socket paramSocket, String paramString, int paramInt1, int paramInt2)
    throws IOException, NoSuchAlgorithmException, KeyManagementException, Exception
  {
    SSLContext localSSLContext = SSLContext.getInstance("SSL");
    try
    {
      if (paramInt2 == 0)
        paramInt2 = 30;
      TrustManager[] arrayOfTrustManager = { new AX509TrustManager(paramString, paramInt2) };
      localSSLContext.init(null, arrayOfTrustManager, null);
      SSLSocketFactory localSSLSocketFactory = localSSLContext.getSocketFactory();
      paramSocket = localSSLSocketFactory.createSocket(paramSocket, paramString, paramInt1, true);
      paramSocket.setTcpNoDelay(true);
      paramSocket.setReceiveBufferSize(32768);
      String[] arrayOfString = { "SSL_DHE_DSS_WITH_DES_CBC_SHA", "SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA", "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA", "SSL_RSA_WITH_RC4_128_MD5", "SSL_RSA_WITH_RC4_128_SHA", "SSL_RSA_WITH_DES_CBC_SHA", "SSL_RSA_WITH_3DES_EDE_CBC_SHA", "SSL_RSA_EXPORT_WITH_RC4_40_MD5" };
      ((SSLSocket)paramSocket).setEnabledCipherSuites(arrayOfString);
      ((SSLSocket)paramSocket).startHandshake();
    }
    catch (Exception localException)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "VirtualMedia::createSSLSocket rethrow exception: " + localException.getMessage());
      localException.printStackTrace();
      throw localException;
    }
    return paramSocket;
  }

    private class LoginStatus
    {

        byte m_bStatus;
        String m_szUsername;
        boolean m_bCanRejectPreemption;

        private LoginStatus()
        {
        }

        LoginStatus(Object this1)
        {
            this();
        }
    }
}

/* Location:           53112a36-4903b3b6.zip
 * Qualified Name:     com.avocent.vm.VirtualMedia
 * JD-Core Version:    0.6.1
 */