package com.avocent.vm;

import java.util.Vector;

public class LocalDrives
  implements InterfacePercentageUpdate
{
  private static final String TRACE_CONTEXT = "LocalDrives";
  static final com.avocent.lib.e.b RES = JFrameVirtualMedia.RES;
  private static final int MAX_DRIVES = 110;
  public static final int DRIVE_IS_UNKNOWN_TYPE = -1;
  public static final int DRIVE_IS_FLOPPY = 4;
  public static final int DRIVE_IS_CD = 2;
  public static final int DRIVE_IS_HARDDISK = 3;
  public static final int DRIVE_IS_CD_IMAGE = 5;
  public static final int DRIVE_IS_FLOPPY_IMAGE = 6;
  Vector<LocalDrive> m_vLocalDrives = new Vector();
  static InterfaceNativeLibrary m_nativeLibrary;
  private boolean m_bReadOnly = false;

  public LocalDrives(boolean paramBoolean)
    throws com.avocent.lib.b.a
  {
    this(paramBoolean, true);
  }

  public LocalDrives(boolean paramBoolean1, boolean paramBoolean2)
    throws com.avocent.lib.b.a
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "LocalDrives constructor, about to load native library");
    this.m_bReadOnly = paramBoolean1;
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "LocalDrives constructor, OS.isWindows=" + com.avocent.lib.e.a.b() + "OS.isLinux=" + com.avocent.lib.e.a.d() + "OS.isSolaris=" + com.avocent.lib.e.a.c() + "OS.isMacIntosh=" + com.avocent.lib.e.a.e());
    try
    {
      if (com.avocent.lib.e.a.b())
      {
      }
      else if (com.avocent.lib.e.a.d())
      {
        m_nativeLibrary = new avmLinuxLibrary();
      }
      else if (com.avocent.lib.e.a.e())
      {
      }
      else if (com.avocent.lib.e.a.c())
      {
        m_nativeLibrary = new avmLinuxLibrary();
      }
      if (m_nativeLibrary != null) {
        com.avocent.lib.a.b.a(TRACE_CONTEXT, "LocalDrives constructor, returned from loading native lib, about to call nativeLibraray.initLib");
        m_nativeLibrary.initLib();
      }
    }
    catch (com.avocent.lib.b.a locala)
    {
      com.avocent.lib.c.b.a.a(null, RES.b("VirtualMedia_ERROR"), RES.b("LocalDrives_CANNOT_LOAD_NATIVE_LIBRARY"), "");
      throw locala;
    }
    String[] arrayOfString1 = new String[110];
    String[] arrayOfString2 = new String[110];
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "LocalDrives constructor, returned from nativeLibraray.initLib, about to call nativeLibrary.findDrives");
  }

  public void findDriveChanges(Vector paramVector)
  {
  }

  public void addDriveFromImageFile(int paramInt1, String paramString, int paramInt2, long paramLong)
    throws com.avocent.lib.b.a
  {
    int j = JFrameVirtualMedia.b;
    String str;
    if (paramInt1 == 6)
    {
      str = "Floppy";
      if (j == 0);
    }
    else if (paramInt1 == 5)
    {
      str = "CdRom";
      if (j == 0);
    }
    else
    {
      throw new com.avocent.lib.b.a("Unknown drive type for image file");
    }
    if (isFileAlreadyInList(paramString) == true)
      return;
    int i = getHighestLocalDriveNumber() + 1;
    LocalDrive localLocalDrive = new LocalDrive(i, paramString, str, paramInt1, paramInt2, paramLong, this.m_bReadOnly);
    this.m_vLocalDrives.add(localLocalDrive);
  }

  public void removeDrive(LocalDrive paramLocalDrive)
  {
    this.m_vLocalDrives.remove(paramLocalDrive);
  }

  public int addDrive(int paramInt, String paramString)
    throws com.avocent.lib.b.a
  {
    int j = JFrameVirtualMedia.b;
    int i = 0;
    do
    {
      if (i >= this.m_vLocalDrives.size())
        break;
      LocalDrive localLocalDrive = (LocalDrive)this.m_vLocalDrives.get(i);
      if (localLocalDrive.getName().equals(paramString))
        return localLocalDrive.getDriveNumber();
      i++;
    }
    while (j == 0);
    String str;
    if ((paramInt == 2) || (paramInt == 5))
    {
      str = "CdRom";
      if (j == 0);
    }
    else if ((paramInt == 4) || (paramInt == 6))
    {
      str = "Floppy";
      if (j == 0);
    }
    else if (paramInt == 3)
    {
      str = "Harddisk";
      if (j == 0);
    }
    else
    {
      return -1;
    }
    i = getHighestLocalDriveNumber() + 1;
    LocalDrive localLocalDrive = new LocalDrive(i, paramString, str, paramInt, this.m_bReadOnly);
    if (paramInt == 2)
    {
      localLocalDrive.queryCdCapacity();
      if (j == 0);
    }
    else if ((paramInt == 4) || (paramInt == 3))
    {
      localLocalDrive.queryFloppyCapacity();
    }
    this.m_vLocalDrives.add(localLocalDrive);
    return i;
  }

  public int getNumberLocalDrives()
  {
    return this.m_vLocalDrives.size();
  }

  public LocalDrive getRemoteDriveToLocalDriveMapping(int paramInt)
  {
    int j = JFrameVirtualMedia.b;
    int i = 0;
    do
    {
      if (i >= this.m_vLocalDrives.size())
        break;
      try
      {
        LocalDrive localLocalDrive = (LocalDrive)this.m_vLocalDrives.get(i);
        if (localLocalDrive.getMapping() == paramInt)
          return localLocalDrive;
      }
      catch (Exception localException)
      {
        com.avocent.lib.a.b.a(TRACE_CONTEXT, localException.getMessage(), localException);
      }
      i++;
    }
    while (j == 0);
    return null;
  }

  public int getNumberOfMappedDrives()
  {
    int k = JFrameVirtualMedia.b;
    int i = 0;
    int j = 0;
    do
    {
      if (j >= this.m_vLocalDrives.size())
        break;
      try
      {
        LocalDrive localLocalDrive = (LocalDrive)this.m_vLocalDrives.get(j);
        if (localLocalDrive.getMapping() >= 0)
          i++;
      }
      catch (Exception localException)
      {
        com.avocent.lib.a.b.a(TRACE_CONTEXT, localException.getMessage(), localException);
      }
      j++;
    }
    while (k == 0);
    return i;
  }

  public void setLocalDriveToRemoteDriveMapping(int paramInt1, int paramInt2)
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "LocalDrives.setLocalDriveToRemoteDriveMapping nLocalDriveNumber=" + paramInt1 + "nRemoteDriveNumber=" + paramInt2);
    try
    {
      LocalDrive localLocalDrive = getLocalDrive(paramInt1);
      localLocalDrive.setMapping(paramInt2);
    }
    catch (Exception localException)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, localException.getMessage(), localException);
    }
  }

  public boolean storageCheckVerify(int paramInt)
    throws com.avocent.lib.b.a
  {
    boolean bool = true;
/*
    try
    {
      LocalDrive localLocalDrive = getLocalDrive(paramInt);
      LocalDrives.StorageCheckVerify localStorageCheckVerify = new LocalDrives.StorageCheckVerify(this, null);
      bool = LocalDrives.StorageCheckVerify.access$300(localStorageCheckVerify, localLocalDrive.getName());
    }
    catch (Exception localException)
    {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, localException.getMessage(), localException);
    }
*/
    return bool;
  }

  public boolean checkFloppyWriteable(LocalDrive paramLocalDrive)
    throws com.avocent.lib.b.a
  {
    boolean bool = false;
    return bool;
  }

  public boolean hasAccessRights(int paramInt)
    throws com.avocent.lib.b.a
  {
    boolean bool = true;
    return bool;
  }

  public void lockCd(int paramInt)
    throws com.avocent.lib.b.a
  {
    if (paramInt == -1)
      return;
    m_nativeLibrary.lockCd(paramInt);
  }

  public void unlockCd(int paramInt)
    throws com.avocent.lib.b.a
  {
    if (paramInt == -1)
      return;
    m_nativeLibrary.unlockCd(paramInt);
  }

  public void closeDrive(int paramInt, boolean paramBoolean)
    throws com.avocent.lib.b.a
  {
    if (paramInt == -1)
      return;
    m_nativeLibrary.closeDrive(paramInt, paramBoolean);
  }

  public static String getLocalDriveTypeString(int paramInt)
  {
    int i = JFrameVirtualMedia.b;
    String str;
    switch (paramInt)
    {
    case 4:
      str = RES.b("LocalDrives_FLOPPY");
      if (i == 0)
        break;
    case 2:
      str = RES.b("LocalDrives_CD_DVD");
      if (i == 0)
        break;
    case 3:
      str = RES.b("LocalDrives_HARD_DISK");
      if (i == 0)
        break;
    case 5:
      str = RES.b("LocalDrives_CD_IMAGE");
      if (i == 0)
        break;
    case 6:
      str = RES.b("LocalDrives_FLOPPY_IMAGE");
      if (i == 0)
        break;
    case -1:
    case 0:
    case 1:
    default:
      str = RES.b("LocalDrives_UNKNOWN");
    }
    return str;
  }

  public LocalDrive getLocalDrive(int paramInt)
  {
    int j = JFrameVirtualMedia.b;
    int i = 0;
    do
    {
      if (i >= this.m_vLocalDrives.size())
        break;
      try
      {
        LocalDrive localLocalDrive = (LocalDrive)this.m_vLocalDrives.get(i);
        if (localLocalDrive.getDriveNumber() == paramInt)
          return localLocalDrive;
      }
      catch (Exception localException)
      {
        com.avocent.lib.a.b.a(TRACE_CONTEXT, localException.getMessage(), localException);
      }
      i++;
    }
    while (j == 0);
    return null;
  }

  public LocalDrive getLocalDriveByIndex(int paramInt)
  {
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "LocalDrives.getLocalDriveByIndex:" + paramInt + "Vector Size: " + this.m_vLocalDrives.size());
    if (paramInt >= this.m_vLocalDrives.size())
      return null;
    return (LocalDrive)this.m_vLocalDrives.get(paramInt);
  }

  public int getIndexOfLocalDrive(LocalDrive paramLocalDrive)
  {
    int j = JFrameVirtualMedia.b;
    int i = 0;
    do
    {
      if (i >= this.m_vLocalDrives.size())
        break;
      try
      {
        if (((LocalDrive)this.m_vLocalDrives.get(i)).getDriveNumber() == paramLocalDrive.getDriveNumber())
          return i;
      }
      catch (Exception localException)
      {
        com.avocent.lib.a.b.a(TRACE_CONTEXT, localException.getMessage(), localException);
      }
      i++;
    }
    while (j == 0);
    return -1;
  }

  private int getHighestLocalDriveNumber()
  {
    int k = JFrameVirtualMedia.b;
    int i = -1;
    int j = 0;
    do
    {
      if (j >= this.m_vLocalDrives.size())
        break;
      try
      {
        LocalDrive localLocalDrive = (LocalDrive)this.m_vLocalDrives.get(j);
        if (localLocalDrive.getDriveNumber() > i)
          i = localLocalDrive.getDriveNumber();
      }
      catch (Exception localException)
      {
        com.avocent.lib.a.b.a(TRACE_CONTEXT, localException.getMessage(), localException);
      }
      j++;
    }
    while (k == 0);
    return i;
  }

  public void setReadOnly(boolean paramBoolean, Vector paramVector)
  {
    int j = JFrameVirtualMedia.b;
    setReadOnly(paramBoolean);
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "LocalDrives.setReadOnly notifying listeners drive status changed");
    int i = 0;
    do
    {
      if (i >= paramVector.size())
        break;
      InterfaceLocalDriveChangeListener localInterfaceLocalDriveChangeListener = (InterfaceLocalDriveChangeListener)paramVector.get(i);
      localInterfaceLocalDriveChangeListener.listenerLocalDriveChanged();
      i++;
    }
    while (j == 0);
  }

  public void setReadOnly(boolean paramBoolean)
  {
    int j = JFrameVirtualMedia.b;
    this.m_bReadOnly = paramBoolean;
    int i = 0;
    do
    {
      if (i >= this.m_vLocalDrives.size())
        break;
      try
      {
        LocalDrive localLocalDrive = (LocalDrive)this.m_vLocalDrives.get(i);
        localLocalDrive.setReadOnly(this.m_bReadOnly);
      }
      catch (Exception localException)
      {
        com.avocent.lib.a.b.a(TRACE_CONTEXT, localException.getMessage(), localException);
      }
      i++;
    }
    while (j == 0);
  }

  public boolean getReadOnly()
  {
    return this.m_bReadOnly;
  }

  public boolean isFileAlreadyInList(String paramString)
  {
    int j = JFrameVirtualMedia.b;
    boolean bool = false;
    int i = 0;
    do
    {
      if (i >= this.m_vLocalDrives.size())
        break;
      try
      {
        LocalDrive localLocalDrive = (LocalDrive)this.m_vLocalDrives.get(i);
        if ((localLocalDrive.isImgOrIsoFile()) && (localLocalDrive.getName().equalsIgnoreCase(paramString)))
        {
          bool = true;
          if (j == 0)
            break;
        }
      }
      catch (Exception localException)
      {
        com.avocent.lib.a.b.a(TRACE_CONTEXT, localException.getMessage(), localException);
      }
      i++;
    }
    while (j == 0);
    return bool;
  }

  static InterfaceNativeLibrary getNativeLibrary()
  {
    return m_nativeLibrary;
  }

  public byte[] getCdTocFromImage()
    throws com.avocent.lib.b.a
  {
    byte[] arrayOfByte = new byte[20];
    arrayOfByte[0] = 0;
    arrayOfByte[1] = 18;
    arrayOfByte[2] = 1;
    arrayOfByte[3] = 1;
    arrayOfByte[4] = 0;
    arrayOfByte[5] = 20;
    arrayOfByte[6] = 1;
    arrayOfByte[7] = 0;
    arrayOfByte[8] = 0;
    arrayOfByte[9] = 0;
    arrayOfByte[10] = 0;
    arrayOfByte[11] = 0;
    arrayOfByte[12] = 0;
    arrayOfByte[13] = 20;
    arrayOfByte[14] = -86;
    arrayOfByte[15] = 0;
    arrayOfByte[16] = 0;
    arrayOfByte[17] = 17;
    arrayOfByte[18] = 118;
    arrayOfByte[19] = -32;
    return arrayOfByte;
  }

  public boolean isPathToCD(String paramString)
  {
    int k = JFrameVirtualMedia.b;
    int i = this.m_vLocalDrives.size();
    if ((com.avocent.lib.e.a.d()) && (paramString.startsWith("/media/")))
      return false;
    if ((com.avocent.lib.e.a.e()) && (paramString.startsWith("/Volumes/")))
      return false;
    int j = 0;
    do
    {
      if (j >= i)
        break;
      LocalDrive localLocalDrive = (LocalDrive)this.m_vLocalDrives.get(j);
      String str = localLocalDrive.getName();
      if (str.equals(paramString))
        return localLocalDrive.getType() == 2;
      if (com.avocent.lib.e.a.b())
      {
        str = str + "\\";
        if (str.equals(paramString))
          return localLocalDrive.getType() == 2;
      }
      j++;
    }
    while (k == 0);
    return false;
  }

  public void updateMessagePercent(int paramInt)
  {
  }

}

/* Location:           53112a36-4903b3b6.zip
 * Qualified Name:     com.avocent.vm.LocalDrives
 * JD-Core Version:    0.6.1
 */