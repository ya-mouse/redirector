package com.ipmitool6.HUAWEI.kvm;

import com.huawei.vm.console.management.ConsoleControllers;
import com.huawei.vm.console.storage.impl.CDROMDriver;

import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.ActionEvent;
import java.lang.InterruptedException;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class Redirector
{
    public static void main(String args[])
    {
        if (args.length < 3)
            System.exit(-1);

        int code_key = -1;
        try {
            long tempLong = 0L;
            long i = (long)Long.valueOf("4294967296").intValue();
            tempLong = Long.valueOf(args[2]).longValue();
            if (tempLong > 0x7fffffffL)
                code_key = (int)(0L - (i - tempLong));
            else
                code_key = Integer.parseInt(args[2]);
        }
        catch(NumberFormatException numberformatexception3) {
            System.err.println("Wrong arg 2: "+args[2]);
            return;
        }

        int maxkb = -1;
        if (args.length >= 4) {
            try {
                maxkb = Integer.parseInt(args[3]);
            }
            catch(NumberFormatException numberformatexception3) {
                System.err.println("Wrong arg 3");
                return;
            }
        }

        int timeout = 5;
        if (args.length == 5) {
            try {
                timeout = Integer.parseInt(args[4]);
            }
            catch(NumberFormatException numberformatexception3) {
                System.err.println("Wrong arg 4");
                return;
            }
        }

        console = new ConsoleControllers();

        createVMLink(cdrom, args[0], args[1] /* IPA */, 8208, code_key /* verifyValue */);

        int result = console.changeCdromImage(args[0]);
        if (result != 0) {
            System.err.println(console.getStatement(result));
            System.exit(1);
        }

        int exit = 0;
        try {
            int kb = maxkb / 2;
            int waitfor = timeout*60;
            while (waitfor > 0) {
                Thread.sleep(1000);
                waitfor--;
                if (maxkb != -1 && maxkb < kb)
                    break;
            }
            if (waitfor == 0 && kb < (maxkb / 2)) {
                /* FIXME: there is no standard way to count bytes, just exit */
                exit = 0;
            }
        } catch (InterruptedException e) {}

        result = console.changeCdromImage("");
        if (result != 0)
            System.err.println(console.getStatement(result));
        disconnectVMLink(cdrom);

        System.exit(exit);
    }

    private static void createVMLink(int deviceType, String path, String serverIP, int serverPort, int certifyID)
    {
        if (cdrom == deviceType) {
            console.creatVMLink(deviceType, path, serverIP, serverPort, certifyID, false /* isFWP */);
            cdVMlink = new Timer("cdrom createVMLink");
            TimerTask task = new TimerTask() {

                public void run()
                {
                    checkCdromVMConsole();
                    cdVMlink.cancel();
                    cdVMlink = null;
                }

            };
            cdVMlink.schedule(task, 7000L);
        }
        if (floppy == deviceType) {
            console.creatVMLink(deviceType, path, serverIP, serverPort, certifyID, false /* isFWP */);
            flpVmlink = new Timer("floppy createVMLink");
            TimerTask task = new TimerTask() {

                public void run()
                {
//                    checkFloppyVMConsole();
                    flpVmlink.cancel();
                    flpVmlink = null;
                }

            };
            flpVmlink.schedule(task, 7000L);
        }
    }

    private static void checkCdromVMConsole()
    {
        if (!console.isConsoleOK(cdrom)) {
            if (console.isVMLinkCrt(cdrom)) {
                Timer date = new Timer("cdrom checkVMConsole");
                TimerTask task = new TimerTask() {

                    public void run()
                    {
                        cdRomVMlink = new javax.swing.Timer(1000, doCdRomVMlink());
                        cdRomVMlink.start();
                    }

                };
                date.schedule(task, 800L);
            } else {
                console.destroyVMLink(cdrom);
            }
        } else {
            int state = console.getConsoleState(cdrom);
            if (state != 0) {
                System.err.println(console.getConsoleStatement(cdrom));
                System.exit(2);
            }
        }
    }

    private static Action doCdRomVMlink()
    {
        Action action = new AbstractAction() {

            public void actionPerformed(ActionEvent e)
            {
                if (!console.isVMLinkCrt(cdrom))
                {
                    storageDevice = null;
                    cdRomVMlink.stop();
                    cdRomVMlink = null;
                    disconnectVMLink(cdrom);
                }
                if (storageDevice == null)
                    storageDevice = console.getConsole().getCdrom();
                if (storageDevice != null && storageDevice.isIsoDiskChanged)
                {
                    int result = console.changeCdromImage("");
                    if (result != 0)
                        System.err.println(console.getStatement(result));
                    storageDevice.isIsoDiskChanged = false;
                }
            }

        };
        return action;
    }

    public static void disconnectVMLink(int deviceType)
    {
        if (deviceType == cdrom)
            storageDevice = null;
        console.destroyVMLink(deviceType);
    }

    private static final int cdrom  = 2;
    private static final int floppy = 1;
    private static final int common = 0;

    private static Timer cdVMlink = null;
    private static Timer flpVmlink = null;
    private static javax.swing.Timer cdRomVMlink;
    private static javax.swing.Timer floppyVmlink;

    private static CDROMDriver storageDevice;
    private static ConsoleControllers console;
}
