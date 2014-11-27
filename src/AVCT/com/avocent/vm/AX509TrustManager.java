package com.avocent.vm;

import com.avocent.lib.a.b;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

class AX509TrustManager
  implements X509TrustManager
{
  private static final String TRACE_CONTEXT = "AX509TrustManager";
  private static final String CERT_DEFAULT_KEYSTORE_PASSWORD = "changeit";
  KeyStore m_ks;
  String m_kspass;
  String m_ksFileName;
  String m_host;
  String m_certTo;
  String m_cacertFileName;
  Thread messageThread = null;
  protected int m_seconds;
  protected int m_result;
  boolean m_timedOut;
  boolean m_runTimer;
  boolean keystoreFileFound = true;
  X509TrustManager sunJSSEX509TrustManager;

  AX509TrustManager(String paramString, int paramInt)
    throws Exception
  {
    this.m_host = paramString;
    this.m_ks = KeyStore.getInstance("JKS");
    this.m_kspass = "";
    this.m_seconds = paramInt;
    this.m_ksFileName = (System.getProperty("user.home") + "/trusted.certs");
    File localFile = new File(this.m_ksFileName);
    if (!localFile.exists())
    {
      this.m_cacertFileName = (System.getProperty("java.home") + "/lib/security/cacerts");
      copyFile(this.m_cacertFileName, this.m_ksFileName);
    }
    b.a(TRACE_CONTEXT, "TrustManager:: keyStore: " + this.m_ksFileName);
    try
    {
      FileInputStream localFileInputStream1 = new FileInputStream(new File(this.m_ksFileName));
      this.m_ks.load(localFileInputStream1, this.m_kspass.toCharArray());
    }
    catch (IOException localIOException)
    {
      this.keystoreFileFound = false;
      Object localObject = localIOException.getMessage();
      if (((String)localObject).equals("Keystore was tampered with, or password was incorrect"))
      {
        this.keystoreFileFound = true;
        this.m_kspass = CERT_DEFAULT_KEYSTORE_PASSWORD;
        FileInputStream localFileInputStream2 = new FileInputStream(new File(this.m_ksFileName));
        this.m_ks.load(localFileInputStream2, this.m_kspass.toCharArray());
      }
      else
      {
        localIOException.printStackTrace();
        b.a(TRACE_CONTEXT, "TrustManager:: throwing: " + (String)localObject);
        throw localIOException;
      }
    }
    TrustManagerFactory localTrustManagerFactory = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
    localTrustManagerFactory.init(this.m_ks);
    Object[] localObject = localTrustManagerFactory.getTrustManagers();
    for (int i = 0; i < localObject.length; i++) {
      if ((localObject[i] instanceof X509TrustManager))
      {
        this.sunJSSEX509TrustManager = ((X509TrustManager)localObject[i]);
        return;
      }
    }
    throw new Exception("Unable to Initialize MyX509TrustManager");
  }

  public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
    throws CertificateException
  {
    try
    {
      this.sunJSSEX509TrustManager.checkClientTrusted(paramArrayOfX509Certificate, paramString);
    }
    catch (CertificateException localCertificateException)
    {
      throw localCertificateException;
    }
  }

  public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
    throws CertificateException
  {
    try
    {
      b.a(TRACE_CONTEXT, "checkServertrusted");
      this.sunJSSEX509TrustManager.checkServerTrusted(paramArrayOfX509Certificate, paramString);
    }
    catch (CertificateException localCertificateException)
    {
      b.a(TRACE_CONTEXT, "checkServertrusted Certificate exception caught : " + localCertificateException.getMessage());
      String str = localCertificateException.getMessage();
      if (str.equals("No trusted certificate found"))
      {
      }
      else
      {
        b.a(TRACE_CONTEXT, "checkServertrusted rethrow excpetion: " + localCertificateException.getMessage());
        throw localCertificateException;
      }
    }
  }

  public X509Certificate[] getAcceptedIssuers()
  {
    return this.sunJSSEX509TrustManager.getAcceptedIssuers();
  }

  private void runTimer()
  {
    int j = JFrameVirtualMedia.b;
    int i = 0;
    this.m_runTimer = true;
    do
      while (true)
      {
        if (!this.m_runTimer)
          return;
        i++;
        if (i >= this.m_seconds)
        {
          b.a(TRACE_CONTEXT, "TrustManager::runTimer timeout ocurred");
          this.m_timedOut = true;
          if (this.messageThread == null)
            return;
          this.messageThread.interrupt();
          b.a(TRACE_CONTEXT, "TrustManager::runTimer close cert dialog");
          if (j == 0)
            return;
        }
        try
        {
          Thread.sleep(1000L);
        }
        catch (InterruptedException localInterruptedException)
        {
          b.a(TRACE_CONTEXT, "TrustManager::runTimer interrupted");
        }
      }
    while (j == 0);
  }

  private void copyFile(String paramString1, String paramString2)
  {
    int j = JFrameVirtualMedia.b;
    try
    {
      File localFile1 = new File(paramString1);
      File localFile2 = new File(paramString2);
      FileInputStream localFileInputStream = new FileInputStream(localFile1);
      FileOutputStream localFileOutputStream = new FileOutputStream(localFile2);
      byte[] arrayOfByte = new byte[1024];
      do
      {
        int i;
        if ((i = localFileInputStream.read(arrayOfByte)) <= 0)
          break;
        localFileOutputStream.write(arrayOfByte, 0, i);
      }
      while (j == 0);
      localFileInputStream.close();
      localFileOutputStream.close();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      b.a(TRACE_CONTEXT, "TrustManager::copyFile Error::" + localException.toString());
    }
  }

    private class _cls1
        implements Runnable
    {

        public void run()
        {
            b.a("Unable to Initialize MyX509TrustManager", "java.home");
            m_runTimer = false;
            messageThread = null;
        }

    }
}

/* Location:           53112a36-4903b3b6.zip
 * Qualified Name:     com.avocent.vm.AX509TrustManager
 * JD-Core Version:    0.6.1
 */