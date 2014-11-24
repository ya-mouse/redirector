package com.avocent.vm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.InterruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ApplianceSession extends Thread
  implements InterfaceLocalDriveChangeListener
{
  private static final String TRACE_CONTEXT = "ApplianceSession";
  static final com.avocent.lib.e.b RES = JFrameVirtualMedia.RES;
  public static final int DRIVE_STATUS_IDLE = 0;
  public static final int DRIVE_STATUS_ATTACHED = 1;
  public static final int DRIVE_STATUS_DISABLED = 2;
  public static final int DRIVE_STATUS_BROKEN = 3;
  public static final int DEVICE_STATUS_OK = 0;
  public static final int DEVICE_STATUS_COMMAND_NOT_SUPPORTED = 1;
  public static final int DEVICE_STATUS_READ_FAILED = 2;
  public static final int DEVICE_STATUS_WRITE_FAILED = 3;
  public static final int DEVICE_STATUS_USB_NOT_CONNECTED = 4;
  public static final int DEVICE_STATUS_DRIVE_ALREADY_ATTACHED = 5;
  public static final int DEVICE_STATUS_DRIVE_DISABLED = 6;
  public static final int DEVICE_STATUS_UNKNOWN_CONFIGURATION_OPTION = 7;
  public static final int DEVICE_STATUS_VDISK_REQUEST_FAILED = 8;
  public static final int DEVICE_ERROR_UNKNOWN_CAUSE = -1;
  public static final int EVENT_SESSON_STARTED = 0;
  public static final int EVENT_SESSON_STOPPED = 1;
  public static final int EVENT_SESSON_TERMINATED = 2;
  public static final int EVENT_SESSON_PREEMPTED = 3;
  public static final int EVENT_SESSON_FAILED_START = 4;
  public static final int EVENT_SESSON_SHARED = 5;
  public static final int EVENT_SESSON_SHARE_REFUSED = 6;
  public static final int EVENT_SESSON_SHARE_TIMED_OUT = 7;
  public static final int DEVICE_CONFIG_OPTION_READ_ONLY = 1;
  public static final int DEVICE_CONFIG_OPTION_LOCKED = 2;
  public static final int DEVICE_CONFIG_OPTION_RESERVE_AVAILABLE = 3;
  DataInputStream m_disAppliance;
  DataOutputStream m_dosAppliance;
  Queue m_queueAvailable;
  Socket m_socket;
  Queue m_queueInput;
  Queue m_queueOutput;
  HeartBeat m_heartBeat;
  private boolean m_bRunIOQueue;
  private Object m_syncRunIOQueue = new Object();
  private Object m_syncDuration = new Object();
  private Object m_syncBytesRead = new Object();
  private Object m_syncBytesWritten = new Object();
  private boolean m_bRunSession;
  private boolean m_bDidNetworkMessage;
  private Object m_syncRunSession = new Object();
  private VDiskInfo m_vDiskInfo = null;
  private DriveInfo[] m_aDriveInfo = null;
  private int m_sNumRemoteDrives = 0;
  private Vector<InterfaceVDiskInfoListener> m_vVDiskInfoListener = new Vector();
  private Vector<InterfaceDiskMappingListener> m_vDiskMappingListeners = new Vector();
  private Vector<InterfaceLocalDriveChangeListener> m_vLocalDriveChangeListeners = new Vector();
  private Vector<InterfaceDriveActivityListener> m_vDriveActivityListeners = new Vector();
  ApplianceSession.FileStuff[] m_fileMapped;
  long[] m_alBytesRead;
  long[] m_alBytesWritten;
  long[] m_beginMapTime;
  byte[] m_baReadData = new byte[16384];
  private LocalDrives m_localDrives;
  private InterfaceNativeLibrary m_nativeLibrary;
  private boolean m_bWaitingForUseResponse = false;
  private final Lock m_checkDriveLock = new ReentrantLock();

  public ApplianceSession(DataInputStream paramDataInputStream, DataOutputStream paramDataOutputStream, Socket paramSocket, LocalDrives paramLocalDrives, com.avocent.lib.d.a parama)
  {
    super("ApplianceSession");
    if (com.avocent.lib.e.a.b())
    {
    }
    else if (com.avocent.lib.e.a.d())
    {
      this.m_nativeLibrary = new avmLinuxLibrary();
    }
    else if (com.avocent.lib.e.a.e())
    {
    }
    else if (com.avocent.lib.e.a.c())
    {
      this.m_nativeLibrary = new avmLinuxLibrary();
    }
    this.m_disAppliance = paramDataInputStream;
    this.m_dosAppliance = paramDataOutputStream;
    this.m_socket = paramSocket;
    this.m_localDrives = paramLocalDrives;
    setRunSession(true);
    DSData[] arrayOfDSData = new DSData[10];
    for (int i = 0; i < arrayOfDSData.length; i++) {
      arrayOfDSData[i] = new DSData(i, 10, 4);
    }
    try {
      registerLocalDriveChangeListener(this);
      initQueues();
      start();
    }
    catch (Exception localException)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, localException.getMessage(), localException);
    }
  }

  public ApplianceSession(DataInputStream paramDataInputStream, DataOutputStream paramDataOutputStream, Socket paramSocket, LocalDrives paramLocalDrives, com.avocent.lib.d.a parama, boolean paramBoolean)
  {
    super("ApplianceSession");
    if (com.avocent.lib.e.a.b())
    {
    }
    else if (com.avocent.lib.e.a.d())
    {
      this.m_nativeLibrary = new avmLinuxLibrary();
    }
    else if (com.avocent.lib.e.a.e())
    {
    }
    else if (com.avocent.lib.e.a.c())
    {
      this.m_nativeLibrary = new avmLinuxLibrary();
    }
    this.m_disAppliance = paramDataInputStream;
    this.m_dosAppliance = paramDataOutputStream;
    this.m_socket = paramSocket;
    this.m_localDrives = paramLocalDrives;
    setRunSession(true);
    DSData[] arrayOfDSData = new DSData[10];
    for (int i = 0; i < arrayOfDSData.length; i++) {
      arrayOfDSData[i] = new DSData(i, 10, 4);
    }
    try
    {
      if (paramBoolean)
      {
        registerLocalDriveChangeListener(this);
      }
      initQueues();
      start();
    }
    catch (Exception localException)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, localException.getMessage(), localException);
    }
  }

  public int getNumberRemoteDrives()
  {
    return this.m_sNumRemoteDrives;
  }

  public boolean isLocalDriveMapped(int paramInt)
  {
    return this.m_localDrives.getLocalDrive(paramInt).isMapped();
  }

  public int getRemoteDriveType(int paramInt)
  {
    return this.m_aDriveInfo[paramInt].getDriveType();
  }

  public void setDrivesReadOnly(boolean paramBoolean)
  {
    this.m_localDrives.setReadOnly(paramBoolean, this.m_vLocalDriveChangeListeners);
  }

  public int getRemoteDriveStatus(int paramInt)
  {
    return this.m_aDriveInfo[paramInt].getDriveStatus();
  }

  public long getBytesRead(int paramInt)
  {
    long l;
    synchronized (this.m_syncBytesRead)
    {
      l = this.m_alBytesRead[paramInt];
    }
    return l;
  }

  public long getBytesWritten(int paramInt)
  {
    long l;
    synchronized (this.m_syncBytesWritten)
    {
      l = this.m_alBytesWritten[paramInt];
    }
    return l;
  }

  public int getDuration(int paramInt)
  {
    int i;
    synchronized (this.m_syncDuration)
    {
      long l1 = System.currentTimeMillis();
      long l2 = this.m_beginMapTime[paramInt];
      if (l2 == -1L)
      {
        i = 0;
      }
      else
      {
        float f = (float)(l1 - l2) / 1000.0F;
        i = (int)f;
      }
    }
    return i;
  }

  public void terminate()
  {
    unmapAllDrives();
    setRunSession(false);
  }


  private boolean getVDiskInfoAll()
    throws IOException
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.getVDiskInfoAll enter");
    this.m_vDiskInfo = null;
    cmdGetVDiskInfo((short)-1);
    int i = OptionsFile.getInteger("VdiskInfoTimeout", 30) * 10;
      while (true)
      {
        if ((i-- <= 0) || (this.m_vDiskInfo != null))
          break;
        try
        {
          Thread.sleep(100L);
        }
        catch (InterruptedException localInterruptedException)
        {
        }
      }
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.getVDiskInfoAll exit");
    return this.m_vDiskInfo != null;
  }

  public boolean mapDrive(int paramInt, boolean paramBoolean)
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.mapDrive" + (paramBoolean ? "R/O=TRUE" : "R/O=FALSE"));
    int i = -1;
    try
    {
      LocalDrive localLocalDrive;
        boolean bool1;
          boolean bool2;
            if (!getVDiskInfoAll())
            {
              com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.mapDrive getVDiskInfo returned false, telling the user drive could not be mapped");
              return false;
            }
            localLocalDrive = this.m_localDrives.getLocalDrive(paramInt);
            localLocalDrive.setUnmapFromGUI(false);
            bool2 = localLocalDrive.isMounted();
            switch (localLocalDrive.getType())
            {
            case 4:
            case 3:
            case 6:
            case 2:
                break;
            case 5:

      Object localObject2;
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.mapDrive DRIVE_IS_CD_IMAGE");
      if (localLocalDrive.getFileSize() == 0L)
      {
        localObject2 = RES.b("ApplianceSession_ZERO_BYTE_FILE");
        String str9 = RES.b("ApplianceSession_MAP_ERROR");
        com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.mapDrive "+(String)localObject2 + str9);
        return false;
      }
      localObject2 = this.m_localDrives.getCdTocFromImage();
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.mapDrive returned from getCdToc");
      if (localObject2 == null)
      {
        com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.mapDrive getCdToc FAILED!");
        clientFailureMessage();
        return false;
      }
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.mapDrive abTocData != null");
      i = remoteDeviceAvailable(2);
      if (i < 0)
      {
        driveNotAvailableMessage();
        return false;
      }
      localLocalDrive.setWriteable(false);
      localLocalDrive.setReadOnly(true);
//      ((abstractJFrameVM)this.m_frameParent).setReadOnly(paramInt);
      paramBoolean = true;
      resetCounts(i);
      this.m_localDrives.setLocalDriveToRemoteDriveMapping(paramInt, i);
      this.m_fileMapped[i] = new FileStuff(this, 5);
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.mapDrive opening file");
      boolean bool11 = true;
      if (paramBoolean == true)
        bool11 = false;
      boolean bool7 = this.m_fileMapped[i].openFile(localLocalDrive.getName(), false, bool11);
      if (!bool7)
      {
        bool7 = this.m_fileMapped[i].openFile(localLocalDrive.getName(), false, false);
        if (!bool7)
        {
          unmapDrive(paramInt);
          throw new com.avocent.lib.b.a("Could not open file " + localLocalDrive.getName());
        }
      }
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession cmdMapCdDrive");
      cmdMapCdDrive((short)i, localLocalDrive.getBlockSize(), localLocalDrive.getBlocks(), (byte[])localObject2);
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession nRemoteDriveID=" + i);
      notifyDiskMappingListeners(i, paramInt, true);
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession notifyDiskMappingListeners Done");

         break;
      }
    }
    catch (Exception localException)
    {
      unmapDrive(paramInt);
      String str1 = localException.getMessage();
      if (str1.equals("INVALID_BOOT_SECTOR"))
      {
        bootSectorMessage();
      }
      else
      {
        com.avocent.lib.a.b.a(TRACE_CONTEXT, str1, localException);
        clientFailureMessage();
      }
      return false;
    }
    this.m_beginMapTime[i] = System.currentTimeMillis();
    return true;
  }

  int remoteDeviceAvailable(int paramInt)
  {
    int i = -1;
    for (int j = 0; j < this.m_aDriveInfo.length; j++) {
      if ((this.m_aDriveInfo[j].getDriveType() == paramInt) && (this.m_aDriveInfo[j].getDriveStatus() == 0))
      {
        i = j;
      }
    }
    return i;
  }

  void driveNotAvailableMessage()
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.mapDrive tell user no drive available on target");
    String str1 = RES.b("ApplianceSession_NO_DRIVE_AVAILABLE_ON_TARGET");
    String str2 = RES.b("ApplianceSession_DRIVE_COULD_NOT_BE_MAPPED");
    com.avocent.lib.a.b.a(TRACE_CONTEXT, str1+": "+str2);
  }

  void mountedMessage()
  {
    String str1;
    String str2;
    if (com.avocent.lib.e.a.e())
    {
      str1 = RES.b("JFrameVirtualMedia_DRIVE_IS_MOUNTED");
      str2 = RES.b("JFrameVirtualMedia_DRIVE_MOUNTED_MAC_MESSAGE");
      com.avocent.lib.a.b.a(TRACE_CONTEXT, str1+": "+str2);
    }
    else
    {
      str1 = RES.b("ApplianceSession_DRIVE_IS_MOUNTED");
      str2 = RES.b("ApplianceSession_Warning");
      com.avocent.lib.a.b.a(TRACE_CONTEXT, str1+": "+str2);
    }
  }

  void noAccessMessage()
  {
    String str1 = RES.b("ApplianceSession_MAPPING_FAILED_NO_ACCESS_RIGHTS");
    String str2 = RES.b("ApplianceSession_DRIVE_COULD_NOT_BE_MAPPED");
    com.avocent.lib.a.b.a(TRACE_CONTEXT, str1+": "+str2);
  }

  boolean unableToLockMessage(String paramString)
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.unableToLockMessage");
    com.avocent.lib.a.b.a(TRACE_CONTEXT, MessageFormat.format(RES.b("ApplianceSession_DRIVE_COULD_NOT_BE_LOCKED"), new Object[] { paramString }));
    return true;
  }

  boolean readOnlyMessage(String paramString)
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, MessageFormat.format(RES.b("ApplianceSession_WriteWarning"), new Object[] { paramString }));
    return true;
  }

  void bootSectorMessage()
  {
    String str1 = RES.b("ApplianceSession_BOOTSECTOR_ERROR");
    String str2 = RES.b("ApplianceSession_DRIVE_COULD_NOT_BE_MAPPED");
    com.avocent.lib.a.b.a(TRACE_CONTEXT, str1+": "+str2);
  }

  void clientFailureMessage()
  {
    String str1 = RES.b("ApplianceSession_FAILURE_ON_CLIENT");
    String str2 = RES.b("ApplianceSession_DRIVE_COULD_NOT_BE_MAPPED");
    com.avocent.lib.a.b.a(TRACE_CONTEXT, str1+": "+str2);
  }

  void resetCounts(int paramInt)
  {
    this.m_alBytesRead[paramInt] = 0L;
    this.m_alBytesWritten[paramInt] = 0L;
    this.m_beginMapTime[paramInt] = System.currentTimeMillis();
  }

  public void unmapAllDrives()
  {
    for (int i = 0; i < this.m_localDrives.getNumberLocalDrives(); i++) {
      unmapDrive(i);
    }
  }

  public boolean unmapDrive(int paramInt)
  {
    return unmapDrive(paramInt, true);
  }

  public boolean unmapDrive(int paramInt, boolean paramBoolean)
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.unmapDrive LocalID: " + paramInt);
    try
    {
      LocalDrive localLocalDrive = this.m_localDrives.getLocalDriveByIndex(paramInt);
      if (localLocalDrive == null)
      {
        com.avocent.lib.a.b.b(TRACE_CONTEXT, "ApplianceSession.unmapDrive ILLEGAL LocalDriveIndex " + paramInt);
        return false;
      }
      int i = localLocalDrive.getMapping();
      if (i >= 0)
      {
        if (paramBoolean)
        {
          int j = this.m_fileMapped[i].getFileHandle();
          if (localLocalDrive.getType() == 2)
          {
            this.m_localDrives.unlockCd(j);
            this.m_localDrives.closeDrive(j, false);
          }
          else
          {
            this.m_localDrives.closeDrive(j, !localLocalDrive.isReadOnly());
          }
          this.m_fileMapped[i].setFileHandle(-1);
        }
        resetCounts(i);
        notifyDiskMappingListeners(i, localLocalDrive.getDriveNumber(), false);
        this.m_localDrives.setLocalDriveToRemoteDriveMapping(localLocalDrive.getDriveNumber(), -1);
        cmdUnmapDrive((short)i);
        return true;
      }
    }
    catch (Exception localException)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, localException.getMessage(), localException);
    }
    return false;
  }

  public void unmapDriveFromMenu(int paramInt1, int paramInt2)
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.unmapDrive LocalID: " + paramInt1);
    try
    {
      LocalDrive localLocalDrive = this.m_localDrives.getLocalDriveByIndex(paramInt1);
      if (localLocalDrive == null)
      {
        com.avocent.lib.a.b.b(TRACE_CONTEXT, "ApplianceSession.unmapDrive ILLEGAL LocalDriveIndex " + paramInt1);
        return;
      }
      if (paramInt2 >= 0)
      {
        int i = this.m_fileMapped[paramInt2].getFileHandle();
        if (localLocalDrive.getType() == 2)
        {
          this.m_localDrives.unlockCd(i);
          this.m_localDrives.closeDrive(i, false);
        }
        else
        {
          this.m_localDrives.closeDrive(i, false);
        }
        this.m_fileMapped[paramInt2].setFileHandle(-1);
      }
      resetCounts(paramInt2);
      notifyDiskMappingListeners(paramInt2, localLocalDrive.getDriveNumber(), false);
      this.m_localDrives.setLocalDriveToRemoteDriveMapping(localLocalDrive.getDriveNumber(), -1);
      cmdUnmapDrive((short)paramInt2);
      return;
    }
    catch (Exception localException)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, localException.getMessage(), localException);
    }
  }

  public boolean hasSessionBeenInitialized()
  {
    return this.m_vDiskInfo != null;
  }

  public VDiskInfo getVDiskInfo()
  {
    return this.m_vDiskInfo;
  }

  private void notifyDiskMappingListeners(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    for (int i = 0; i < this.m_vDiskMappingListeners.size(); i++) {
      InterfaceDiskMappingListener localInterfaceDiskMappingListener = (InterfaceDiskMappingListener)this.m_vDiskMappingListeners.get(i);
      localInterfaceDiskMappingListener.listenerDiskMapping(paramInt1, paramInt2, paramBoolean);
    }
  }

  private void notifyActivityListeners_read(int paramInt1, int paramInt2)
  {
    for (int i = 0; i < this.m_vDriveActivityListeners.size(); i++) {
      InterfaceDriveActivityListener localInterfaceDriveActivityListener = (InterfaceDriveActivityListener)this.m_vDriveActivityListeners.get(i);
      localInterfaceDriveActivityListener.driveRead(paramInt1, paramInt2);
    }
  }

  private void notifyActivityListeners_write(int paramInt1, int paramInt2)
  {
    for (int i = 0; i < this.m_vDriveActivityListeners.size(); i++) {
      InterfaceDriveActivityListener localInterfaceDriveActivityListener = (InterfaceDriveActivityListener)this.m_vDriveActivityListeners.get(i);
      localInterfaceDriveActivityListener.driveWrite(paramInt1, paramInt2);
    }
  }

  private void notifyActivityListeners_error(int paramInt1, int paramInt2)
  {
    for (int i = 0; i < this.m_vDriveActivityListeners.size(); i++) {
      InterfaceDriveActivityListener localInterfaceDriveActivityListener = (InterfaceDriveActivityListener)this.m_vDriveActivityListeners.get(i);
      localInterfaceDriveActivityListener.driveError(paramInt1, paramInt2);
    }
  }

  public void cmdGetVDiskInfo(short paramShort)
    throws IOException
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.cmdGetVDiskInfo sDiskId=" + paramShort);
    DSData localDSData = this.m_queueAvailable.getData();
    PacketAVMP localPacketAVMP = new PacketAVMP(localDSData);
    localPacketAVMP.setMessageType(512, 12);
    localPacketAVMP.setVDiskDiskID(paramShort);
    this.m_queueOutput.put(localPacketAVMP.getData());
  }

  private void cmdMapFloppyDrive(short paramShort, LocalDrive paramLocalDrive)
    throws IOException
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.cmdMapFloppyDrive sDiskId=" + paramShort + "local drive description=" + paramLocalDrive.getDescription());
    DSData localDSData = this.m_queueAvailable.getData();
    PacketAVMP localPacketAVMP = new PacketAVMP(localDSData);
    paramLocalDrive.setFloppyMode(this.m_aDriveInfo[paramShort].getDriveType() == 4);
    localPacketAVMP.setMessageType(529, 26);
    localPacketAVMP.setVDiskRequest2FieldsForFloppy(paramShort, paramLocalDrive.getBlockSize(), (int)paramLocalDrive.getBlocks(), paramLocalDrive.isReadOnly(), (int)paramLocalDrive.getCylinders(), paramLocalDrive.getHeads(), paramLocalDrive.getSectors());
    this.m_queueOutput.put(localPacketAVMP.getData());
  }

  private void cmdMapCdDrive(short paramShort, int paramInt, long paramLong, byte[] paramArrayOfByte)
    throws IOException
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.cmdMapCdDrive sDiskId=" + paramShort + "nBlockSize=" + paramInt + "nNumberOfBlocks=" + paramLong + "baTocData.length=" + paramArrayOfByte.length);
    DSData localDSData = this.m_queueAvailable.getData();
    PacketAVMP localPacketAVMP = new PacketAVMP(localDSData);
    localPacketAVMP.setMessageType(528, 24 + paramArrayOfByte.length);
    localPacketAVMP.setVDiskRequestFieldsForCd(paramShort, paramInt, (int)paramLong, paramArrayOfByte.length, paramArrayOfByte);
    this.m_queueOutput.put(localPacketAVMP.getData());
  }

  private void cmdUnmapDrive(short paramShort)
    throws IOException
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.cmdUnmapDrive sDiskId=" + paramShort);
    DSData localDSData = this.m_queueAvailable.getData();
    PacketAVMP localPacketAVMP = new PacketAVMP(localDSData);
    localPacketAVMP.setMessageType(544, 12);
    localPacketAVMP.setVDiskDiskID(paramShort);
    this.m_queueOutput.put(localPacketAVMP.getData());
  }

  private boolean cmdSendReadData(short paramShort, int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    int i7 = JFrameVirtualMedia.b;
    boolean bool = false;
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.cmdSendReadData sDiskId=" + paramShort + "nStartBlock=" + paramInt1 + "nNumBlocks=" + paramInt2);
    LocalDrive localLocalDrive = this.m_localDrives.getRemoteDriveToLocalDriveMapping(paramShort);
    if (localLocalDrive == null)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.cmdSendReadData ***** Could not get local drive ID from remote drive ID");
      return false;
    }
    int i = localLocalDrive.getBlockSize();
    int j = paramInt1;
    int k = paramInt2 * i;
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.cmdSendReadData nBlockSize=" + i + "ReadSizeInBytes=" + k);
    int m = 0;
    do
    {
      if (k <= 0)
        break;
      int n = k;
      if (n > 32768)
        n = 32768;
      paramInt2 = n / i;
      paramInt1 = j;
      if (this.m_baReadData.length < n)
        this.m_baReadData = new byte[n];
      int i1 = localLocalDrive.getType();
      if (com.avocent.lib.e.a.b())
      {
        if ((i1 == 2) || (i1 == 3))
        {
          com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.cmdSendReadData read WINDOWS: CD/USB");
          m = this.m_fileMapped[paramShort].readSectors(this.m_baReadData, paramInt1, paramInt2, i);
          if (i7 == 0);
        }
        else
        {
          com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.cmdSendReadData read WINDOWS: Files/Floppy Drives");
          m = this.m_fileMapped[paramShort].readDriveEx(this.m_baReadData, paramInt1, paramInt2, i);
          if (i7 == 0);
        }
      }
      else
      {
        com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.cmdSendReadData Linux-Mac read any");
        m = this.m_fileMapped[paramShort].readDriveEx(this.m_baReadData, paramInt1, paramInt2, i);
      }
      bool = true;
      if (m < 1)
      {
        com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.cmdSendReadData nNumRead < 1!!!!, failure nNumRead=" + m);
        m = 0;
        bool = false;
        notifyActivityListeners_error(localLocalDrive.getDriveNumber(), 1);
        if (i7 == 0)
          break;
      }
      int i2 = paramInt2;
      int i3 = 0;
      int i4 = 0;
      int i5 = 0;
      do
      {
        if (i2 <= 0)
          break;
        i4 = i2 > paramInt3 ? paramInt3 : i2;
        com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.responseReadData nBlocksToSend=" + i4 + "Blocks remaining: " + i2);
        i5 = i4 * i;
        DSData localDSData = this.m_queueAvailable.getData();
        PacketAVMP localPacketAVMP = new PacketAVMP(localDSData);
        localPacketAVMP.setMessageType(768, 20 + i5);
        localPacketAVMP.setVDiskDiskID(paramShort);
        localPacketAVMP.setVDiskReadDataFields(paramInt1, i4, i5, this.m_baReadData, i3);
        this.m_queueOutput.put(localPacketAVMP.getData());
        i2 -= i4;
        paramInt1 += i4;
        i3 += i5;
      }
      while (i7 == 0);
      this.m_alBytesRead[paramShort] += m;
      notifyActivityListeners_read(localLocalDrive.getDriveNumber(), m);
      k -= m;
      if (k < 1)
        break;
      int i6 = m / i;
      j += i6;
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.responseReadData bytes remaining to read: " + k);
    }
    while (i7 == 0);
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.responseReadData READ COMPLETED: " + bool);
    return bool;
  }

  private void cmdClientStatus(short paramShort, int paramInt)
    throws IOException
  {
    DSData localDSData = this.m_queueAvailable.getData();
    PacketAVMP localPacketAVMP = new PacketAVMP(localDSData);
    localPacketAVMP.setMessageType(1040, 16);
    localPacketAVMP.setVDiskDiskID(paramShort);
    localPacketAVMP.setClientStatus(paramInt);
    this.m_queueOutput.put(localPacketAVMP.getData());
  }

  public void cmdUsbReset(short paramShort)
    throws IOException
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.cmdUsbReset sDiskId=" + paramShort);
    DSData localDSData = this.m_queueAvailable.getData();
    PacketAVMP localPacketAVMP = new PacketAVMP(localDSData);
    localPacketAVMP.setMessageType(1056, 12);
    localPacketAVMP.setVDiskDiskID(paramShort);
    this.m_queueOutput.put(localPacketAVMP.getData());
  }

  public void cmdPreemptResponse(byte paramByte)
    throws IOException
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.cmdPreemptResponse okCancel=" + paramByte);
    DSData localDSData = this.m_queueAvailable.getData();
    PacketAVMP localPacketAVMP = new PacketAVMP(localDSData);
    localPacketAVMP.setMessageType(288, 13);
    localPacketAVMP.setDisconnectReason(paramByte);
    this.m_queueOutput.put(localPacketAVMP.getData());
  }

  public void run()
  {
    int k = com.avocent.vm.JFrameVirtualMedia.b;
    try
    {
      cmdGetVDiskInfo((short)-1);
      do {
        if (!isSessionRunning())
          break;

        do {
          if (!this.m_queueInput.hasData())
            break;
          DSData localDSData = this.m_queueInput.getData();
          PacketAVMP localPacketAVMP = new PacketAVMP(localDSData);
          switch (localPacketAVMP.getMessageType())
          {
          case 33280:
            responseVDiskInfo(localPacketAVMP);
            if(k == 0)
                continue;

          case 33808:
            responseDeviceStatus(localPacketAVMP);
            if(k == 0)
                continue;

          case 33536:
            responseReadData(localPacketAVMP);
            if(k == 0)
                continue;

          case 33552:
            responseWriteData(localPacketAVMP);
            if(k == 0)
                continue;

          case 33040:
            responseDisconnect(localPacketAVMP);
            if(k == 0)
                continue;

          case 33344:
            responseReleaseMapping(localPacketAVMP);
            if(k == 0)
                continue;

          case 33840:
            responseDeviceConfigOption(localPacketAVMP);
            if(k == 0)
                continue;

          default:
            com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession unknown packet received messageType=" + localPacketAVMP.getMessageType());
            this.m_queueAvailable.put(localPacketAVMP.getData());
            break;
          }
        } while (k == 0);
        this.m_queueInput.waitForData();
     } while (k == 0);
    }
    catch (Exception localException1)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.run:: Exception Caught");
      com.avocent.lib.a.b.a(TRACE_CONTEXT, localException1.getMessage(), localException1);
    }
    finally
    {
      this.m_heartBeat.stop();
      if (this.m_localDrives != null)
      {
        for (int i = 0; i < this.m_localDrives.getNumberLocalDrives(); i++) {
          try
          {
            if (this.m_localDrives.getLocalDrive(i).isMapped())
            {
              com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.run.finally: unmapDrive(nCnt)=" + i);
              unmapDrive(i);
              LocalDrive localLocalDrive = this.m_localDrives.getLocalDrive(i);
            }
          }
          catch (Exception localException2)
          {
          }
        }
      }
      setRunIOQueue(false);
      try
      {
        this.m_socket.close();
      }
      catch (IOException localIOException)
      {
      }
      int j = 3;
        while (true)
        {
          if (j-- < 0)
            break;
          try
          {
            Thread.sleep(100L);
          }
          catch (InterruptedException localInterruptedException1)
          {
          }
        }
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession run about to wait for user response m_bWaitingForUseResponse=" + this.m_bWaitingForUseResponse);
        while (true)
        {
          if (!this.m_bWaitingForUseResponse)
            break;
          try
          {
            Thread.sleep(50L);
          }
          catch (InterruptedException localInterruptedException2)
          {
          }
        }
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession run end waiting for user response m_bWaitingForUseResponse=" + this.m_bWaitingForUseResponse);
    }
  }

  private void responseVDiskInfo(PacketAVMP paramPacketAVMP)
    throws IOException
  {
    int j = JFrameVirtualMedia.b;
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.responseVDiskInfo");
    this.m_vDiskInfo = new VDiskInfo(paramPacketAVMP.getData());
    if (this.m_vDiskInfo != null)
    {
      this.m_aDriveInfo = this.m_vDiskInfo.getDriveInfo();
      this.m_sNumRemoteDrives = this.m_aDriveInfo.length;
    }
    if ((this.m_fileMapped == null) || (this.m_sNumRemoteDrives > this.m_fileMapped.length))
      this.m_fileMapped = new FileStuff[this.m_sNumRemoteDrives];
    if ((this.m_alBytesRead == null) || (this.m_sNumRemoteDrives > this.m_alBytesRead.length))
      this.m_alBytesRead = new long[this.m_sNumRemoteDrives];
    if ((this.m_alBytesWritten == null) || (this.m_sNumRemoteDrives > this.m_alBytesWritten.length))
      this.m_alBytesWritten = new long[this.m_sNumRemoteDrives];
    if ((this.m_beginMapTime == null) || (this.m_sNumRemoteDrives > this.m_beginMapTime.length))
      this.m_beginMapTime = new long[this.m_sNumRemoteDrives];
    for (int i = 0; i < this.m_vVDiskInfoListener.size(); i++) {
      InterfaceVDiskInfoListener localInterfaceVDiskInfoListener = (InterfaceVDiskInfoListener)this.m_vVDiskInfoListener.get(i);
      localInterfaceVDiskInfoListener.listenerVDiskInfo();
    }
    this.m_queueAvailable.put(paramPacketAVMP.getData());
  }

  private void responseDeviceStatus(PacketAVMP paramPacketAVMP)
    throws IOException
  {
    short s = paramPacketAVMP.getDiskId();
    int i = paramPacketAVMP.getDeviceStatus();
    this.m_queueAvailable.put(paramPacketAVMP.getData());
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.responseDeviceStatus sDiskID=" + s + "nDeviceStatus=" + i);
  }

  private void responseDisconnect(PacketAVMP paramPacketAVMP)
    throws IOException
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.responseDisconnect");
    byte b1 = paramPacketAVMP.getDisconnectReason();
    int i = paramPacketAVMP.getDisconnectStatus();
    byte b2 = paramPacketAVMP.getDisconnectTimeout();
    String str1 = paramPacketAVMP.getUserName();
    this.m_queueAvailable.put(paramPacketAVMP.getData());
    if (b1 == 4)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession::responseDisconnect: user " + str1);
      Object localObject = RES.b("VirtualMedia_Title");
      String str2 = MessageFormat.format(RES.b("ApplianceSession_DISCONNECT_PREEMPT_REQUEST"), new Object[] { str1 });
      int j = 0;
      int k = 0;
        cmdPreemptResponse((byte)0);
        unmapAllDrives();
        setRunSession(false);
        return;

//      cmdPreemptResponse((byte)52);
//      return;
    }
    setRunSession(false);
  }

  public void responseDeviceConfigOption(PacketAVMP paramPacketAVMP)
    throws IOException
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession responseDeviceConfigOption");
    int i = paramPacketAVMP.getDeviceConfigOptionID();
    int j = paramPacketAVMP.getDeviceConfigValueByte();
    this.m_queueAvailable.put(paramPacketAVMP.getData());
    switch (i)
    {
    case 1:
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession responseDeviceConfigOption nConfigOptionID=DEVICE_CONFIG_OPTION_READ_ONLY value=" + j);
      setDrivesReadOnly(j == 1);
        break;
    default:
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession responseDeviceConfigOption nConfigOptionID=UNKNOWN" + i + "value=" + j);
    }
  }

  public void responseReadData(PacketAVMP paramPacketAVMP)
    throws IOException
  {
    short s = paramPacketAVMP.getDiskId();
    int i = paramPacketAVMP.getDiskStartBlock();
    int j = paramPacketAVMP.getDiskNumberOfBlocks();
    int k = paramPacketAVMP.getDiskBlockingFactor();
    this.m_queueAvailable.put(paramPacketAVMP.getData());
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.responseReadData sDiskId=" + s + "nStartingBlock=" + i + "nNumBlocks=" + j + "nBlockingFactor=" + k);
    try
    {
      boolean bool = true;
      bool = cmdSendReadData(s, i, j, k);
      if (bool)
      {
        cmdClientStatus(s, 0);
      }
      else
      {
        com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.responseReadData sDiskId=" + s + "read failed");
        cmdClientStatus(s, 2);
      }
    }
    catch (IOException localIOException)
    {
      LocalDrive localLocalDrive = this.m_localDrives.getRemoteDriveToLocalDriveMapping(s);
      if (localLocalDrive != null)
      {
        com.avocent.lib.a.b.a(TRACE_CONTEXT, localIOException.getMessage(), localIOException);
        String str1 = RES.b("ApplianceSession_READ_ERROR_OCCURRED");
        String str2 = RES.b("ApplianceSession_READ_ERROR");
        com.avocent.lib.a.b.a(TRACE_CONTEXT, str1+": "+str2);
        unmapDrive(localLocalDrive.getDriveNumber());
      }
    }
  }

  public void responseWriteData(PacketAVMP paramPacketAVMP)
    throws IOException
  {
    short s = paramPacketAVMP.getDiskId();
    int i = paramPacketAVMP.getDiskStartBlock();
    int j = paramPacketAVMP.getDiskNumberOfBlocks();
    int k = 0;
    LocalDrive localLocalDrive = this.m_localDrives.getRemoteDriveToLocalDriveMapping(s);
    if (localLocalDrive == null)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.responseWriteData getRemoteDriveToLocalDriveMapping returned null, write failed");
      this.m_queueAvailable.put(paramPacketAVMP.getData());
      cmdClientStatus(s, 3);
      return;
    }
    int m = localLocalDrive.getBlockSize();
    byte[] arrayOfByte = paramPacketAVMP.getWriteData(m);
    this.m_queueAvailable.put(paramPacketAVMP.getData());
    try
    {
      k = this.m_fileMapped[s].writeDevice(i, j, m, arrayOfByte);
      cmdClientStatus(s, 0);
      this.m_alBytesWritten[s] += k;
      notifyActivityListeners_write(localLocalDrive.getDriveNumber(), j * m);
    }
    catch (Exception localException)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, localException.getMessage(), localException);
      cmdClientStatus(s, 3);
      notifyActivityListeners_error(localLocalDrive.getDriveNumber(), 2);
    }
  }

  private void responseReleaseMapping(PacketAVMP paramPacketAVMP)
    throws IOException
  {
    int i = paramPacketAVMP.getDiskId();
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.responseReleaseMapping diskId: " + i);
    this.m_queueAvailable.put(paramPacketAVMP.getData());
    LocalDrive localLocalDrive = this.m_localDrives.getRemoteDriveToLocalDriveMapping(i);
    if (localLocalDrive != null)
    {
      if (!localLocalDrive.isMapped())
        return;
      int j = this.m_localDrives.getIndexOfLocalDrive(localLocalDrive);
      if (j == -1)
      {
        com.avocent.lib.a.b.b(TRACE_CONTEXT, "ApplianceSession.responseReleaseMapping Unable to get local drive index for remote disk " + i);
        return;
      }
      int k = j;
      passDriveIndex = i;
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.responseReleaseMapping Local disk index: " + j);
      Thread localThread = new Thread(new _cls4());
      localThread.start();
    }
  }
  public void registerVDiskInfoListener(InterfaceVDiskInfoListener paramInterfaceVDiskInfoListener)
  {
    this.m_vVDiskInfoListener.add(paramInterfaceVDiskInfoListener);
  }

  public void unregisterVDiskInfoListener(InterfaceVDiskInfoListener paramInterfaceVDiskInfoListener)
  {
    this.m_vVDiskInfoListener.remove(paramInterfaceVDiskInfoListener);
  }

  public void registerDiskMappingListener(InterfaceDiskMappingListener paramInterfaceDiskMappingListener)
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "(VirtualMedia ApplianceSession.registerDiskMappingListener)");
    this.m_vDiskMappingListeners.add(paramInterfaceDiskMappingListener);
  }

  public void unregisterDiskMappingListener(InterfaceDiskMappingListener paramInterfaceDiskMappingListener)
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "(VirtualMedia ApplianceSession.unregisterDiskMappingListener)");
    this.m_vDiskMappingListeners.remove(paramInterfaceDiskMappingListener);
  }

  public void registerLocalDriveChangeListener(InterfaceLocalDriveChangeListener paramInterfaceLocalDriveChangeListener)
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "(VirtualMedia ApplianceSession.registerLocalDriveChangeListener)");
    this.m_vLocalDriveChangeListeners.add(paramInterfaceLocalDriveChangeListener);
    paramInterfaceLocalDriveChangeListener.listenerLocalDriveChanged();
  }

  public void unregisterLocalDriveChangeListener(InterfaceLocalDriveChangeListener paramInterfaceLocalDriveChangeListener)
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "(VirtualMedia ApplianceSession.unregisterLocalDriveChangeListener)");
    this.m_vLocalDriveChangeListeners.remove(paramInterfaceLocalDriveChangeListener);
  }

  public void registerLocalDriveActivityListener(InterfaceDriveActivityListener paramInterfaceDriveActivityListener)
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "(VirtualMedia ApplianceSession.registerDriveActivityListener)");
    this.m_vDriveActivityListeners.add(paramInterfaceDriveActivityListener);
  }

  public void unregisterLocalDriveActivityListener(InterfaceDriveActivityListener paramInterfaceDriveActivityListener)
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "(VirtualMedia ApplianceSession.unregisterDriveActivityListener)");
    this.m_vDriveActivityListeners.remove(paramInterfaceDriveActivityListener);
  }

  public void listenerLocalDriveAdded()
  {
  }

  public void listenerLocalDriveRemoved()
  {
  }

  public void listenerLocalDriveRemoved(int paramInt, boolean paramBoolean)
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.listenerLocalDriveRemoved nLocalDriveNumber=" + paramInt + "isMapped=" + paramBoolean);
    if (paramBoolean)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.listenerLocalDriveRemoved **** MAPPED DRIVE REMOVED **** ");
      unmapDrive(paramInt, false);
    }
  }

  public void listenerLocalDriveChanged()
  {
  }

  private void initQueues()
    throws IOException
  {
    int j = JFrameVirtualMedia.b;
    this.m_queueInput = new Queue(20);
    this.m_queueOutput = new Queue(10);
    this.m_queueAvailable = new Queue(this.m_queueInput.getSize() + this.m_queueOutput.getSize() + 10);
    for (int i = 0; i < this.m_queueAvailable.getSize(); i++) {
      this.m_queueAvailable.put(new DSData(10, 4));
    }
    setRunSession(true);
    setRunIOQueue(true);
    DSPacketIO localDSPacketIO = new DSPacketIO(this.m_disAppliance, this.m_dosAppliance, this.m_queueAvailable, null);
    ThreadDSDataInput localThreadDSDataInput = new ThreadDSDataInput(this.m_queueInput, localDSPacketIO);
    ThreadDSDataOutput localThreadDSDataOutput = new ThreadDSDataOutput(this.m_queueOutput, localDSPacketIO);
    this.m_heartBeat = new HeartBeat(this.m_queueOutput, this.m_queueAvailable);
//    ThreadCheckMappedDrives localThreadCheckMappedDrives = new ThreadCheckMappedDrives();
  }

  private void setRunSession(boolean paramBoolean)
  {
    synchronized (this.m_syncRunSession)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "(VirtualMedia ApplianceSession.setRunSession) bRunSession: " + paramBoolean);
      this.m_bRunSession = paramBoolean;
    }
  }

  public final boolean isSessionRunning()
  {
    synchronized (this.m_syncRunSession)
    {
      return this.m_bRunSession;
    }
  }

  private void setRunIOQueue(boolean paramBoolean)
  {
    synchronized (this.m_syncRunIOQueue)
    {
      this.m_bRunIOQueue = paramBoolean;
    }
  }

  private boolean isRunIOQueueSet()
  {
    synchronized (this.m_syncRunIOQueue)
    {
      return this.m_bRunIOQueue;
    }
  }

  private void displayNetworkErrorTerminationMessage()
  {
    if (this.m_bDidNetworkMessage)
      return;
    this.m_bWaitingForUseResponse = true;
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.mapDrive tell user NETWORK ERROR");
    String str1 = RES.b("VirtualMedia_ConnectionDropped");
    String str2 = RES.b("VirtualMedia_NETWORK_ERROR");
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.mapDrive "+str1+": "+str2);
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "ApplianceSession.mapDrive return from NETWORK ERROR dialog");
    this.m_bWaitingForUseResponse = false;
    this.m_bDidNetworkMessage = true;
  }

    private class _cls4
        implements Runnable
    {

        public void run()
        {
            unmapDrive(passDriveIndex);
        }

        _cls4()
        {
        }
    }


    private class DSPacketIO
    {

        protected DSData readDSData()
            throws IOException, Exception
        {
    int m = JFrameVirtualMedia.b;
    DSData localDSData = this.m_queueAvailable.getData();
    byte[] localObject = localDSData.getData();
    int i = 0;
    if (isRunPacketIOSet())
      try
      {
        int j = 0;
        int k = 0;
        while (j < 10)
        {
          k = this.m_dis.read(localObject, j, 10 - j);
          if (k >= 0)
          {
            j += k;
            if (m == 0)
              break;
          }
          else
          {
            this.m_queueAvailable.put(localDSData);
            return null;
          }
        }
        if (localDSData.getPacketLength() > localObject.length)
        {
          byte[] arrayOfByte = new byte[localDSData.getPacketLength()];
          System.arraycopy(localObject, 0, arrayOfByte, 0, 10);
          localDSData.setData(arrayOfByte);
          localObject = arrayOfByte;
        }
        i = localDSData.getPacketLength() - 10;
        j = 0;
        while (j < i)
        {
          k = this.m_dis.read(localObject, 10 + j, i - j);
          if (k >= 0)
          {
            j += k;
            if (m == 0)
              break;
          }
          else
          {
            com.avocent.lib.a.b.a("VirtualMedia", "(ApplianceSession readDSData) read error:" + k);
            this.m_queueAvailable.put(localDSData);
            return null;
          }
        }
        localDSData.setUsedLength(10 + i);
        return localDSData;
      }
      catch (IOException localIOException)
      {
        com.avocent.lib.a.b.a("VirtualMedia", "(DSData readDSData)IOException: " + localIOException.getMessage() + "Packet data: " + localObject[0] + " " + localObject[1] + " " + localObject[2] + " " + localObject[3] + " " + localObject[4] + " " + localObject[5] + " " + localObject[6] + " " + localObject[7] + "Packet length: " + i, localIOException);
        throw localIOException;
      }
      catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
      {
        com.avocent.lib.a.b.a("VirtualMedia", "(DSData readDSData)IndexOutOfBoundsException: " + localIndexOutOfBoundsException.getMessage() + "Packet data: " + localObject[0] + " " + localObject[1] + " " + localObject[2] + " " + localObject[3] + " " + localObject[4] + " " + localObject[5] + " " + localObject[6] + " " + localObject[7] + "Packet length: " + i, localIndexOutOfBoundsException);
        throw localIndexOutOfBoundsException;
      }
      catch (Exception localException)
      {
        com.avocent.lib.a.b.a("VirtualMedia", "(DSData readDSData)Exception: " + localException.getMessage() + "Packet data: " + localObject[0] + " " + localObject[1] + " " + localObject[2] + " " + localObject[3] + " " + localObject[4] + " " + localObject[5] + " " + localObject[6] + " " + localObject[7] + "Packet length: " + i, localException);
        throw localException;
      }
    return null;
        }

        private void writeDSData(DSData dsdata)
            throws IOException, SocketException
        {
            m_dos.write(dsdata.getData(), 0, dsdata.getUsedLength());
            m_dos.flush();
            m_queueAvailable.put(dsdata);
        }

        protected void setRunPacketIO(boolean flag)
        {
            synchronized(m_syncPacketIO)
            {
                m_bRunPacketIO = flag;
            }
        }

        private boolean isRunPacketIOSet()
        {
            synchronized(m_syncPacketIO)
            {
                return m_bRunPacketIO;
            }
        }

        private DataInputStream m_dis;
        private DataOutputStream m_dos;
        private Queue m_queueAvailable;
        private boolean m_bRunPacketIO;
        private Object m_syncPacketIO;

        private DSPacketIO(DataInputStream datainputstream, DataOutputStream dataoutputstream, Queue queue)
            throws IOException
        {
            m_syncPacketIO = new Object();
            m_dis = datainputstream;
            m_dos = dataoutputstream;
            m_queueAvailable = queue;
            m_bRunPacketIO = true;
        }

        DSPacketIO(DataInputStream datainputstream, DataOutputStream dataoutputstream, Queue queue, Object cls1)
            throws IOException
        {
            this(datainputstream, dataoutputstream, queue);
        }
    }


    private class ThreadDSDataInput extends Thread
    {
        public void run()
        {

    while (true) {
      if (isRunIOQueueSet())
        try
        {
          DSData localDSData = this.m_dsDataPacketIO.readDSData();
          if (localDSData != null)
          {
            this.m_queueDataInput.put(localDSData);
            localDSData = null;
          }
          else
          {
            try
            {
              sleep(10L);
            }
            catch (InterruptedException localInterruptedException)
            {
            }
          }
        }
        catch (Exception localException)
        {
          if (m_bRunSession)
          {
            com.avocent.lib.a.b.a("VirtualMedia", "(ThreadDSDataInput run) " + localException.getMessage(), localException);
            setRunSession(false);
            displayNetworkErrorTerminationMessage();
          }
        }
        if (m_nativeLibrary != null)
          m_nativeLibrary.cleanup();
        }
        }

        Queue m_queueDataInput;
        DSPacketIO m_dsDataPacketIO;

        ThreadDSDataInput(Queue queue, DSPacketIO dspacketio)
        {
            super("(VirtualMedia ApplianceSession.setRunSession) bRunSession: ");
            m_queueDataInput = queue;
            m_dsDataPacketIO = dspacketio;
            setPriority(10);
            start();
        }
    }


    private class ThreadDSDataOutput extends Thread
    {

        public void run()
        {
    int i = JFrameVirtualMedia.b;
    while (true)
      if (isRunIOQueueSet())
        try
        {
          do
          {
            if (!this.m_queueDataOutput.hasData())
              break;
            DSData localDSData = this.m_queueDataOutput.getData();
            m_dsDataPacketIO.writeDSData(localDSData);
            localDSData = null;
            m_heartBeat.updateSendTime();
          }
          while (i == 0);
          this.m_queueDataOutput.waitForData();
        }
        catch (Exception localException)
        {
          com.avocent.lib.a.b.a("VirtualMedia", "(ThreadDSDataOutput run) " + localException.getMessage(), localException);
          setRunSession(false);
          displayNetworkErrorTerminationMessage();
        }
        }

        Queue m_queueDataOutput;
        DSPacketIO m_dsDataPacketIO;

        ThreadDSDataOutput(Queue queue, DSPacketIO dspacketio)
        {
            super("ApplianceSession_DRIVE_COULD_NOT_BE_MAPPED");
            m_queueDataOutput = queue;
            m_dsDataPacketIO = dspacketio;
            setPriority(10);
            start();
        }
    }

    private class FileStuff
    {
  private static final int MAXLOCKWAITTIME = 10000;
  private static final int LOCKSLEEPTIME = 200;
  private int m_hFile = -1;
  private boolean m_bLocked = false;
  protected int m_deviceType;
  long m_lastIOTime = -1L;

  public FileStuff(ApplianceSession paramApplianceSession, int paramInt)
  {
    this.m_deviceType = paramInt;
  }

  public boolean openFile(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    com.avocent.lib.a.b.a("VirtualMedia", "ApplianceSession.FileStuff.openFile szPath=" + paramString + "Write=" + paramBoolean1 + "LockDrive=" + paramBoolean2);
    try
    {
      this.m_hFile = m_nativeLibrary.openDrive(paramString, paramBoolean1, paramBoolean2);
      com.avocent.lib.a.b.a("VirtualMedia", "ApplianceSession.FileStuff.openFile Returned m_hFile:" + this.m_hFile);
      if (this.m_hFile == -1)
        return false;
      this.m_bLocked = paramBoolean2;
      return true;
    }
    catch (com.avocent.lib.b.a locala)
    {
      throw new IOException(locala.getMessage());
    }
  }

  public int readDriveEx(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    if (this.m_hFile == -1)
      throw new IOException("Invalid handle");
    com.avocent.lib.a.b.a("VirtualMedia", "ApplianceSession.FileStuff.readDriveEx Sectors:" + paramInt2 + "SectorSize:" + paramInt3);
    acquireLock();
    int i = 0;
    try
    {
      i = m_nativeLibrary.readDriveEx(this.m_hFile, paramInt1, paramInt2, paramInt3, paramArrayOfByte);
    }
    catch (com.avocent.lib.b.a locala)
    {
      releaseLock();
      throw new IOException(locala.getMessage());
    }
    this.m_lastIOTime = System.currentTimeMillis();
    releaseLock();
    return i;
  }

  public int readSectors(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    if (this.m_hFile == -1)
      throw new IOException("Invalid handle");
    com.avocent.lib.a.b.a("VirtualMedia", "ApplianceSession.FileStuff.readSectors Sector:" + paramInt1 + "num Sectors:" + paramInt2 + "SectorSize:" + paramInt3);
    acquireLock();
    int i = 0;
    try
    {
      i = m_nativeLibrary.readSectors(this.m_hFile, paramInt1, paramInt2, paramInt3, paramArrayOfByte);
    }
    catch (com.avocent.lib.b.a locala)
    {
      releaseLock();
      throw new IOException(locala.getMessage());
    }
    this.m_lastIOTime = System.currentTimeMillis();
    releaseLock();
    return i;
  }

  public int writeDevice(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
    throws IOException, com.avocent.lib.b.a
  {
    com.avocent.lib.a.b.a("VirtualMedia", "ApplianceSession.writeDevice");
    int i = 0;
    acquireLock();
    if ((this.m_deviceType == 3) && (com.avocent.lib.e.a.b()))
    {
      i = m_nativeLibrary.writeSectors(this.m_hFile, paramInt1, paramInt3, paramArrayOfByte.length, paramArrayOfByte);
    }
    else
    {
      i = m_nativeLibrary.writeDriveEx(this.m_hFile, paramInt1, paramInt2, paramInt3, paramArrayOfByte);
    }
    if (i < 0)
    {
      releaseLock();
      throw new com.avocent.lib.b.a("Cannot write to device");
    }
    releaseLock();
    this.m_lastIOTime = System.currentTimeMillis();
    return i;
  }

  public void closeFile()
    throws IOException
  {
    com.avocent.lib.a.b.a("VirtualMedia", "ApplianceSession.FileStuff.closeFile m_hFile=" + this.m_hFile);
    this.m_lastIOTime = -1L;
    if (this.m_hFile == -1)
      return;
    boolean bool = this.m_bLocked;
    if ((this.m_deviceType == 2) || (this.m_deviceType == 6) || (this.m_deviceType == 5))
      bool = false;
    try
    {
      m_localDrives.closeDrive(this.m_hFile, bool);
    }
    catch (com.avocent.lib.b.a locala)
    {
      this.m_hFile = -1;
      throw new IOException(locala.getMessage());
    }
    com.avocent.lib.a.b.a("VirtualMedia", "ApplianceSession.FileStuff.closeFile: FILE CLOSED");
    this.m_hFile = -1;
  }

  public int getFileHandle()
  {
    return this.m_hFile;
  }

  public void setFileHandle(int paramInt)
  {
    this.m_hFile = paramInt;
    if (this.m_hFile == -1)
      this.m_lastIOTime = -1L;
  }

  public long getLastIOTime()
  {
    return this.m_lastIOTime;
  }

  private void acquireLock()
  {
    int j = JFrameVirtualMedia.b;
    boolean bool = false;
    int i = 0;
    do
      while (true)
      {
        if (bool)
          return;
        try
        {
          bool = m_checkDriveLock.tryLock();
          if (bool == true)
            if (j == 0)
              return;
        }
        catch (Exception localException1)
        {
          com.avocent.lib.a.b.a("VirtualMedia", "ApplianceSession.FileStuff lock failed " + localException1.getMessage());
        }
        i += 200;
        if (i > 10000)
          return;
        try
        {
          Thread.sleep(200L);
        }
        catch (Exception localException2)
        {
        }
      }
    while (j == 0);
  }

  private void releaseLock()
  {
    m_checkDriveLock.unlock();
  }
    }


    int  passDriveIndex ;
}

/* Location:           53112a36-4903b3b6.zip
 * Qualified Name:     com.avocent.vm.ApplianceSession
 * JD-Core Version:    0.6.1
 */