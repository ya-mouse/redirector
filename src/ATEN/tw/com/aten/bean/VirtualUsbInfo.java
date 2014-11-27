// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package tw.com.aten.bean;

import java.util.Arrays;

public class VirtualUsbInfo
{

    public VirtualUsbInfo(int i)
    {
        ports = new int[i];
        Arrays.fill(ports, 0);
    }

    public int getNumDev()
    {
        return ports.length;
    }

    public int[] getPorts()
    {
        return ports;
    }

    public void setPort(int i, int j)
    {
        ports[i] = j;
    }

    public int getPort()
    {
        return main_port;
    }

    public void setPort(int i)
    {
        main_port = i;
    }

    private int ports[];

    private int main_port;
}
