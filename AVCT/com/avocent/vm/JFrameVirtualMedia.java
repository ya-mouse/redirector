package com.avocent.vm;

import java.io.File;

public class JFrameVirtualMedia extends abstractJFrameVM
{
  static final String TRACE_CONTEXT = "JFrameVirtualMedia";
  static final com.avocent.lib.e.b RES = com.avocent.lib.e.b.a(System.getProperty("ResourceFile", "com.avocent.vm.Res"));
  public static int b = 0;

  public JFrameVirtualMedia(SessionParameters paramSessionParameters, LocalDrives paramLocalDrives)
  {
    this.m_sessionParameters = paramSessionParameters;
    this.m_localDrives = paramLocalDrives;
  }

  public void init(ApplianceSession paramApplianceSession)
    throws Exception
  {
    int i = b;
    this.m_applianceSession = paramApplianceSession;
    String str = RES.b("JFrameVirtualMedia_VIRTUAL_MEDIA_SESSION");
    if (this.m_sessionParameters.m_szTitle.length() > 0)
    {
      str = str + "-";
      str = str + this.m_sessionParameters.m_szTitle;
    }
    com.avocent.lib.a.b.a(TRACE_CONTEXT, "JFrame");
    if (m_applianceSession.isSessionRunning()) {
      com.avocent.lib.a.b.a(TRACE_CONTEXT, "JFrame: isRunning");
      int k = 0;
      while (!m_applianceSession.hasSessionBeenInitialized() && k < 10) {
        try {
          Thread.sleep(500L);
        } catch (Exception e) { }
        k++;
      }
      actionPerformedAddImage();
      LocalDrive localDrive = this.m_localDrives.getLocalDriveByIndex(this.m_localDrives.getNumberLocalDrives()-1);
      i = localDrive.getDriveNumber();
      if (!m_applianceSession.mapDrive(i, true)) {
        this.m_applianceSession.terminate();
        System.err.println("MAP FAILED!");
        System.exit(-2);
        throw new Exception("Map drive has failed");
      }
      int ticks = 0;
      while (m_applianceSession.isSessionRunning()) {
        for (int j=0; j < m_applianceSession.getNumberRemoteDrives(); j++) {
          long rd = m_applianceSession.getBytesRead(j);
          if (rd >= this.m_sessionParameters.m_maxkb) {
            /* All done, go back */
            this.m_applianceSession.terminate();
            return;
          }
        }
        try {
          Thread.sleep(1000L);
          ticks++;
        } catch (Exception e) { }
        if (ticks >= this.m_sessionParameters.m_timeout) {
          this.m_applianceSession.terminate();
          System.err.println("TIMEOUT!");
          return;
        }
      }
    }
  }

  private void actionPerformedAddImage()
    throws Exception
  {
      if ("".equals(this.m_sessionParameters.m_szISO)) {
        this.m_applianceSession.terminate();
        System.exit(-3);
        throw new Exception("ISO IMAGE not specified");
      }
      File localFile =  new File(this.m_sessionParameters.m_szISO);
      int j = -1;
      int k = 512;
      String str1;
      String str2;
/*
      if (localFile.getName().toLowerCase().endsWith(RES.b("JFrameVirtualMedia_DOT_ISO")))
      {
        j = 5;
        k = 2048;
        if (m == 0);
      }
      else if (localFile.getName().toLowerCase().endsWith(RES.b("JFrameVirtualMedia_DOT_IMG")))
      {
        j = 6;
        k = 512;
        if (m == 0);
      }
*/
      j = 5;
      k = 2048;
      try
      {
        this.m_localDrives.addDriveFromImageFile(j, localFile.getAbsolutePath(), k, (int)(localFile.length() / k));
      }
      catch (com.avocent.lib.b.a locala)
      {
        str2 = RES.b("JFrameVirtualMedia_UNKNOWN_FILE_TYPE");
        return;
      }
  }

  public void setReadOnly(int paramInt) { }

  public void setReadWrite(int paramInt) { }

  public void listenerLocalDriveAdded() { }

  public void listenerLocalDriveRemoved() { }

  public void listenerLocalDriveRemoved(int paramInt, boolean paramBoolean) { }

  public void listenerLocalDriveChanged() { }

  public void listenerDiskMapping(int paramInt1, int paramInt2, boolean paramBoolean) { }

  public boolean closeWindow() { this.m_applianceSession.terminate(); return true; }
}

/* Location:           53112a36-4903b3b6.zip
 * Qualified Name:     com.avocent.vm.JFrameVirtualMedia
 * JD-Core Version:    0.6.1
 */