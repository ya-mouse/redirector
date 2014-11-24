package com.avocent.vm;

import com.avocent.lib.e.b;

public abstract class abstractJFrameVM
{
  static final b RES = JFrameVirtualMedia.RES;
  protected SessionParameters m_sessionParameters = null;
  protected ApplianceSession m_applianceSession = null;
  protected LocalDrives m_localDrives;
  protected boolean m_enabled = true;
  public static int a;

  public abstract void init(ApplianceSession paramApplianceSession) throws Exception;

  public abstract void setReadOnly(int paramInt);

  public abstract void setReadWrite(int paramInt);

  public abstract void listenerLocalDriveAdded();

  public abstract void listenerLocalDriveRemoved();

  public abstract void listenerLocalDriveRemoved(int paramInt, boolean paramBoolean);

  public abstract void listenerLocalDriveChanged();

  public abstract void listenerDiskMapping(int paramInt1, int paramInt2, boolean paramBoolean);

  public abstract boolean closeWindow();

  public void setAvailable(boolean paramBoolean)
  {
    this.m_enabled = paramBoolean;
  }
}

/* Location:           53112a36-4903b3b6.zip
 * Qualified Name:     com.avocent.vm.abstractJFrameVM
 * JD-Core Version:    0.6.1
 */