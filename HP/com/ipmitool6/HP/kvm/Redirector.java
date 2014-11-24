package com.ipmitool6.HP.kvm;

import com.serverengines.mahogany.MahoganyViewer;

public class Redirector
{
    public static void main(String args[])
    {
        if (args.length < 5)
            System.exit(-1);

        viewer = new MahoganyViewer(args);
        viewer.start();
    }

    private static MahoganyViewer viewer;
}
