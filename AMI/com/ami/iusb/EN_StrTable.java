// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EN_StringTable.java

package com.ami.iusb;


public class EN_StrTable
{

    public EN_StrTable()
    {
    }

    public static final String GetString(String key) {
        for (int i=0; i<StringTable.length; i++) {
            if (StringTable[i][0].equals(key))
                return StringTable[i][1];
        }
        return null;
    }

    public static final String StringTable[][] = {
        {
            "A_1_GLOBAL", "Redirection Viewer"
        }, {
            "1_1_JVIEWER", "Incorrect parameters"
        }, {
            "1_2_OK", "OK"
        }, {
            "1_3_Cancel", "Cancel"
        }, {
            "2_1_KVMCLIENT", "Error while initializing SSL"
        }, {
            "2_2_KVMCLIENT", "Error opening video socket"
        }, {
            "2_3_KVMCLIENT", "Maximum number of allowable sessions reached. Please close other sessions and try again"
        }, {
            "2_4_KVMCLIENT", "VKVM service has rejected this connection"
        }, {
            "2_5_KVMCLIENT", "Error while initializing SSL for HID socket"
        }, {
            "2_6_KVMCLIENT", "Error opening HID socket"
        }, {
            "3_1_SSLWRAPPER", "No such algorithm: "
        }, {
            "3_2_SSLWRAPPER", "Key management exception: "
        }, {
            "3_3_SSLWRAPPER", "Setup Buffers failed\n"
        }, {
            "3_4_SSLWRAPPER", "Unable to find certificate in Default Keystore for validation.\nPlease upload the certificate using the Java Control Panel and try again.\nJava Control Panel can be found at the following locations\nWindows - Start->Programs->Control Panel->Java\nLinux - Menu->Settings->Java"
        }, {
            "3_5_SSLWRAPPER", "Unwrap error"
        }, {
            "4_1_CDROMREDIR", "Cannot find CD wrapper library"
        }, {
            "4_2_CDROMREDIR", "Error closing CD connection: "
        }, {
            "4_3_CDROMREDIR", "Insufficient user privileges to run Virtual Media redirection"
        }, {
            "4_4_CDROMREDIR", "Virtual Media redirection is already in use.\nIt may take up to 3 minutes for the device to disconnect from the previous connection and\naccept a new one.\nVirtual Media redirection is being used by the client with the following IP: "
        }, {
            "4_5_CDROMREDIR", "Unable to start CD redirection"
        }, {
            "4_6_CDROMREDIR", "Unexpected opcode: "
        }, {
            "4_7_CDROMREDIR", "Cannot open CD"
        }, {
            "4_8_CDROMREDIR", "Interrupted while joining CD thread"
        }, {
            "4_9_CDROMREDIR", "CD drive list cannot be found"
        }, {
            "4_10_CDROMREDIR", "Virtual Media devices are currently in DETACH state. Please use the configuration web page to change the status and try again."
        }, {
            "4_11_CDROMREDIR", "Authentication Failed."
        }, {
            "5_1_FLOPPYREDIR", "Cannot find Floppy wrapper library"
        }, {
            "5_2_FLOPPYREDIR", "Error closing floppy connection: "
        }, {
            "5_3_FLOPPYREDIR", "Insufficient user privileges to run Virtual Media redirection"
        }, {
            "5_4_FLOPPYREDIR", "Virtual Media redirection is already in use.\nIt may take up to 3 minutes for the device to disconnect from the previous connection and\naccept a new one.\nVirtual Media redirection is being used by the client with the following IP : "
        }, {
            "5_5_FLOPPYREDIR", "Unable to start floppy redirection"
        }, {
            "5_6_FLOPPYREDIR", "Unexpected code: "
        }, {
            "5_7_FLOPPYREDIR", "Cannot open Floppy/USB Key. The Floppy/USB Key is already in use by some other applicaiton.\nIf the media contents are opened in an explorer window, please close the window\nor if the media is already mounted, please unmount it and try again"
        }, {
            "5_8_FLOPPYREDIR", "Interrupted while joining floppy thread"
        }, {
            "5_9_FLOPPYREDIR", "Floppy drive list cannot be found"
        }, {
            "5_10_FLOPPYREDIR", "Virtual Media devices are currently in DETACH state. Please use the configuration web page to change the status and try again."
        }, {
            "5_11_FLOPPYREDIR", "Authentication Failed."
        }, {
            "6_1_IUSBREDIR", "Invalid IP address: Cannot redirect CD"
        }, {
            "6_2_IUSBREDIR", "Invalid source drive: Cannot redirect CD"
        }, {
            "6_3_IUSBREDIR", "Previous CD/ISO redirection session failed to close properly"
        }, {
            "6_4_IUSBREDIR", "CD redirection failed to start"
        }, {
            "6_5_IUSBREDIR", "CD redirection is not supported on this platform"
        }, {
            "6_6_IUSBREDIR", "Cannot redirect CD"
        }, {
            "6_7_IUSBREDIR", "Unable to cancel CD/ISO redirection. Please try again."
        }, {
            "6_8_IUSBREDIR", "Invalid IP Address: Cannot redirect CD Image"
        }, {
            "6_9_IUSBREDIR", "Invalid image file: Cannot redirect CD Image"
        }, {
            "6_10_IUSBREDIR", "Previous CD/ISO redirection session failed to close properly"
        }, {
            "6_11_IUSBREDIR", "CD image redirection failed to start"
        }, {
            "6_12_IUSBREDIR", "CD image redirection is not supported on this platform"
        }, {
            "6_13_IUSBREDIR", "Cannot redirect CD image "
        }, {
            "6_14_IUSBREDIR", "Invalid IP address. Cannot redirect floppy"
        }, {
            "6_15_IUSBREDIR", "Invalid source drive. Cannot redirect floppy"
        }, {
            "6_16_IUSBREDIR", "Previous floppy/floppy image redirection session failed to close properly"
        }, {
            "6_17_IUSBREDIR", "Floppy redirection failed to start"
        }, {
            "6_18_IUSBREDIR", "Floppy redirection is not supported on this platform"
        }, {
            "6_19_IUSBREDIR", "Cannot redirect floppy"
        }, {
            "6_20_IUSBREDIR", "Unable to cancel floppy/floppy image redirection. Please try again."
        }, {
            "6_21_IUSBREDIR", "Invalid IP address: Cannot redirect floppy image"
        }, {
            "6_22_IUSBREDIR", "Invalid image file: Cannot redirect floppy image"
        }, {
            "6_23_IUSBREDIR", "Previous floppy/floppy image redirection session failed to close properly"
        }, {
            "6_24_IUSBREDIR", "Floppy image redirection failed to start"
        }, {
            "6_25_IUSBREDIR", "Floppy image redirection is not supported on this platform"
        }, {
            "6_26_IUSBREDIR", "Cannot redirect floppy image "
        }, {
            "6_27_IUSBREDIR", "Choose a CD drive to redirect"
        }, {
            "6_28_IUSBREDIR", "CD selection"
        }, {
            "6_29_IUSBREDIR", "Choose a floppy drive to redirect"
        }, {
            "6_30_IUSBREDIR", "Floppy selection"
        }, {
            "6_31_IUSBREDIR", "Or manually enter the floppy drive letter"
        }, {
            "7_1_JLIST", "Cancel this operation and exit window"
        }, {
            "7_2_JLIST", "Approve current selection and exit window"
        }, {
            "7_3_JLIST", "Manually enter a selection for this window"
        }, {
            "7_4_JLIST", "List window"
        }, {
            "8_1_PACKETMAST", "Cannot resolve host name: "
        }, {
            "8_2_PACKETMAST", "No such algorithm: "
        }, {
            "8_3_PACKETMAST", "Key management exception: "
        }, {
            "8_4_PACKETMAST", "SSL Exception received: "
        }, {
            "8_5_PACKETMAST", "Virtual Media service closed the connection"
        }, {
            "8_6_PACKETMAST", "Timed out while receiving data"
        }, {
            "9_1_IUSBREDIR", "Bad encryption key"
        }, {
            "9_1_BW", "Detecting bandwidth. Please wait..."
        }, {
            "9_2_BW", "Detecting bandwidth. Please wait..."
        }, {
            "9_3_BW", "Changing the bandwidth settings. Please wait..."
        }, {
            "9_4_BW", "Done"
        }, {
            "A_1_DP", "Browse"
        }, {
            "B_1_FMB", "Auto Hide"
        }, {
            "B_2_FMB", "Menu"
        }, {
            "B_3_FMB", "Not connected"
        }, {
            "B_4_FMB", "Minimize"
        }, {
            "B_5_FMB", "Toggle to frame window"
        }, {
            "B_6_FMB", "Close"
        }, {
            "C_1_JVF", "Not connected"
        }, {
            "C_2_JVF", "<html>Virtual Media redirection is in use. Closing this window will cancel Virtual Media redirection.<br>\nDo you want to continue?</html>"
        }, {
            "C_3_JVF", "Confirm"
        }, {
            "D_1_JVAPP", "Host power is off"
        }, {
            "D_2_JVAPP", "Connection failed"
        }, {
            "D_3_JVAPP", "File already exists. Do you want to replace it?"
        }, {
            "D_4_JVAPP", "Confirm"
        }, {
            "D_5_JVAPP", "Unable to save the image at this location "
        }, {
            "D_6_JVAPP", "Detected bandwidth"
        }, {
            "D_7_JVAPP", "Version"
        }, {
            "D_8_JVAPP", "Copyright (c) American Megatrends Inc., 2007. All rights reserved."
        }, {
            "D_9_JVAPP", "About Redirection Viewer"
        }, {
            "D_10_JVAPP", "Keyboard/mouse encryption is enabled by other sessions."
        }, {
            "D_11_JVAPP", "Enabling encryption for this session"
        }, {
            "D_12_JVAPP", "Maximum number of sessions reached. Unable to execute the request.\nPlease close other active sessions and try again."
        }, {
            "D_13_JVAPP", "Redirection configuration has been changed. Redirection engine will restart.\nCurrent session has been closed by redirection service. Please connect again."
        }, {
            "D_14_JVAPP", "Redirection is disabled. Please change the configuration and try again."
        }, {
            "D_15_JVAPP", "You are trying to connect to a port that is not secure. Please try connecting to a secure port."
        }, {
            "D_16_JVAPP", "Authentication failed. Token issued for this session is expired. Please close this session and try again"
        }, {
            "D_17_JVAPP", "Insufficient user privileges. Unable to continue VKVM redirection"
        }, {
            "D_18_JVAPP", "Current session has been closed by redirection service. Please connect again."
        }, {
            "D_19_JVAPP", "Invalid session token. Authentication failed."
        }, {
            "D_20_JVAPP", "VKVM redirection has been disabled. Do you want to continue using Virtual Media redirection?"
        }, {
            "D_21_JVAPP", "Due to Web Session Logout.Closing the Viewer... "
        }, {
            "E_1_JVIEW", "Unable to continue mouse redirection in Linux mouse mode"
        }, {
            "E_2_JVIEW", "Invalid mouse mode. Mouse mode response packet\nhas not been received from VKVM service.\nPlease wait and try again."
        }, {
            "E_3_JVIEW", "Exception occurred while starting mouse redirection in Linux mouse mode.\nPlease close this session and try again.\nIf the problem persists, please switch to Absolute mouse mode and try again."
        }, {
            "E_4_JVIEW", "Mouse redirection in Linux mouse mode requires the redirection window size to be a minimum of"
        }, {
            "E_5_JVIEW", "Current window size is "
        }, {
            "E_6_JVIEW", "Please resize the window and try again."
        }, {
            "E_7_JVIEW", "More than half of your redirection window has exceeded the boundaries of the screen.\nAt least half of the redirection window must be visible when using mouse redirection in Linux mouse mode.\nIt is recommended that the entire window be completely visible when in this mode.\nPlease move or resize the window and try again"
        }, {
            "E_8_JVIEW", " This color model is unsupported in the current version of the Java viewer. Please install a newer version."
        }, {
            "F_1_JVM", "Video"
        }, {
            "F_2_JVM", "Pause"
        }, {
            "F_3_JVM", "Resume"
        }, {
            "F_4_JVM", "Refresh"
        }, {
            "F_5_JVM", "Capture Current Screen"
        }, {
            "F_6_JVM", "Full Screen"
        }, {
            "F_7_JVM", "Exit"
        }, {
            "F_8_JVM", "Keyboard"
        }, {
            "F_9_JVM", "Hold Right Alt Key"
        }, {
            "F_10_JVM", "Hold Left Alt Key"
        }, {
            "F_11_JVM", "Left Windows Key"
        }, {
            "F_12_JVM", "Hold Down"
        }, {
            "F_13_JVM", "Press and Release"
        }, {
            "F_14_JVM", "Right Windows Key"
        }, {
            "F_15_JVM", "Macros"
        }, {
            "F_16_JVM", "Ctrl+Alt+Del"
        }, {
            "F_17_JVM", "Alt+Tab"
        }, {
            "F_18_JVM", "Alt+Esc"
        }, {
            "F_19_JVM", "Ctrl+Esc"
        }, {
            "F_20_JVM", "Alt+Space"
        }, {
            "F_21_JVM", "Alt+Enter"
        }, {
            "F_22_JVM", "Alt+Hyphen"
        }, {
            "F_23_JVM", "Alt+F4"
        }, {
            "F_24_JVM", "Alt+PrntScrn"
        }, {
            "F_25_JVM", "PrntScrn"
        }, {
            "F_26_JVM", "F1"
        }, {
            "F_27_JVM", "Pause"
        }, {
            "F_28_JVM", "Keyboard pass-through"
        }, {
            "F_29_JVM", "Mouse"
        }, {
            "F_30_JVM", "Synchronize Mouse Cursor"
        }, {
            "F_31_JVM", "Options"
        }, {
            "F_32_JVM", "Color Mode"
        }, {
            "F_33_JVM", "15-bit"
        }, {
            "F_34_JVM", "7-bit"
        }, {
            "F_35_JVM", "4-bit color"
        }, {
            "F_36_JVM", "4-bit grey"
        }, {
            "F_37_JVM", "3-bit grey"
        }, {
            "F_38_JVM", "Media"
        }, {
            "F_39_JVM", "Virtual Media Wizard..."
        }, {
            "F_40_JVM", "Help"
        }, {
            "F_41_JVM", "About"
        }, {
            "F_42_JVM", "Video commands"
        }, {
            "F_43_JVM", "Keyboard commands"
        }, {
            "F_44_JVM", "Mouse commands"
        }, {
            "F_45_JVM", "Options setting"
        }, {
            "F_46_JVM", "Help commands"
        }, {
            "F_47_JVM", "Change to full screen or regular mode"
        }, {
            "F_48_JVM", "Pause VKVM redirection"
        }, {
            "F_49_JVM", "Resume VKVM redirection"
        }, {
            "F_50_JVM", "Refresh VKVM redirection"
        }, {
            "F_51_JVM", "Captures the current screen"
        }, {
            "F_52_JVM", "Hold down right alt key"
        }, {
            "F_53_JVM", "Hold down left alt key"
        }, {
            "F_54_JVM", "Hold down left windows key"
        }, {
            "F_55_JVM", "Press and release left windows key"
        }, {
            "F_56_JVM", "Hold down right windows key"
        }, {
            "F_57_JVM", "Press and release right windows key"
        }, {
            "F_58_JVM", "Press Alt+Ctrl+Del key combination"
        }, {
            "F_59_JVM", "Press Alt+Tab key combination"
        }, {
            "F_60_JVM", "Press Alt+Esc key combination"
        }, {
            "F_61_JVM", "Press Ctrl+Esc key combination"
        }, {
            "F_62_JVM", "Press Alt+Space key combination"
        }, {
            "F_63_JVM", "Press Alt+Enter key combination"
        }, {
            "F_64_JVM", "Press Alt+Hyphen key combination"
        }, {
            "F_65_JVM", "Press Alt+F4 key combination"
        }, {
            "F_66_JVM", "Press Alt+PrntScrn key combination"
        }, {
            "F_67_JVM", "Press PrntScrn key"
        }, {
            "F_68_JVM", "Press F1 key"
        }, {
            "F_69_JVM", "Press Pause key"
        }, {
            "F_70_JVM", "Enable/disable keyboard pass-through"
        }, {
            "F_71_JVM", "Synchronize mouse cursor"
        }, {
            "F_72_JVM", "Auto detect bandwidth"
        }, {
            "F_73_JVM", "Select 256 Kbps bandwidth"
        }, {
            "F_74_JVM", "Select 512 Kbps bandwidth"
        }, {
            "F_75_JVM", "Select 1 Mbps bandwidth"
        }, {
            "F_76_JVM", "Select 10 Mbps bandwidth"
        }, {
            "F_77_JVM", "Select 100 Mbps bandwidth"
        }, {
            "F_78_JVM", "Keyboard, Video and Mouse redirection"
        }, {
            "F_79_JVM", "Virtual Media access"
        }, {
            "F_80_JVM", "Displays Virtual Media Wizard"
        }, {
            "F_81_JVM", "Supported color modes"
        }, {
            "F_82_JVM", "15-bit color"
        }, {
            "F_83_JVM", "7-bit color"
        }, {
            "F_84_JVM", "4-bit color"
        }, {
            "F_85_JVM", "4-bit grey"
        }, {
            "F_86_JVM", "3-bit grey"
        }, {
            "F_87_JVM", "Keyboard macros"
        }, {
            "F_88_JVM", "Alt+F1"
        }, {
            "F_89_JVM", "Press Alt+F1 keys"
        }, {
            "G_1_VMD", "Virtual Media"
        }, {
            "G_2_VMD", "Floppy/USB Key Media"
        }, {
            "G_3_VMD", "CD Media"
        }, {
            "G_4_VMD", "Status"
        }, {
            "G_5_VMD", "Virtual Floppy"
        }, {
            "G_6_VMD", "Not connected"
        }, {
            "G_7_VMD", "n/a"
        }, {
            "G_8_VMD", "Virtual CD"
        }, {
            "G_9_VMD", "Target Drive"
        }, {
            "G_10_VMD", "Connected To"
        }, {
            "G_11_VMD", "Read Bytes"
        }, {
            "G_12_VMD", "Connect CD/DVD"
        }, {
            "G_13_VMD", "Connect Floppy"
        }, {
            "G_14_VMD", "Help"
        }, {
            "G_15_VMD", "Close"
        }, {
            "G_16_VMD", "Exit"
        }, {
            "G_17_VMD", "Virtual Media redirection is in use. Closing this window will cancel Virtual Media redirection.\nDo you want to continue?"
        }, {
            "G_18_VMD", "Disconnect"
        }, {
            "H_1_HR", "An existing connection was forcibly closed by the remote host. Please try again after sometime."
        }, {
            "Z_1_MISC", "started"
        }
    };

}
