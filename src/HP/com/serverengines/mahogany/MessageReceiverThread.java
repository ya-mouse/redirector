// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 

package com.serverengines.mahogany;

import com.serverengines.buffer.BufferMgr;
import com.serverengines.graphics.GraphicsMgr;
import com.serverengines.helper.Helper;
import com.serverengines.helper.StringBufferPool;
import com.serverengines.keyboard.Keyboard;
import com.serverengines.keyboard.KeyboardMgr;
import com.serverengines.kvm.LogWriter;
import com.serverengines.mahoganyprotocol.BitBlt;
import com.serverengines.mahoganyprotocol.Command;
import com.serverengines.mahoganyprotocol.EnhanceBitBlt;
import com.serverengines.mahoganyprotocol.FirmwareVersion;
import com.serverengines.mahoganyprotocol.GraphicsRegisterValue;
import com.serverengines.mahoganyprotocol.InformKeyIndicators;
import com.serverengines.mahoganyprotocol.InformVesaMode;
import com.serverengines.mahoganyprotocol.MahoganyProtocol;
import com.serverengines.mahoganyprotocol.MatroxGraphicsCursor;
import com.serverengines.mahoganyprotocol.MultiUserState;
import com.serverengines.mahoganyprotocol.Region;
import com.serverengines.mahoganyprotocol.ServerDisconnect;
import com.serverengines.mahoganyprotocol.ServerHandshake;
import com.serverengines.mahoganyprotocol.SetPalette;
import com.serverengines.mahoganyprotocol.SetTextCursor;
import com.serverengines.mahoganyprotocol.SpecialGraphicsBit;
import com.serverengines.mahoganyprotocol.StandbyPower;
import com.serverengines.mahoganyprotocol.StorageStatus;
import com.serverengines.rdr.BadException;
import com.serverengines.rdr.InStream;

// Referenced classes of package com.serverengines.mahogany:
//            CConn, MsgBox, MahoganyViewer

public class MessageReceiverThread extends Thread
{

    public MessageReceiverThread(CConn cconn)
    {
        s_logger = new LogWriter((com.serverengines.mahogany.MessageReceiverThread.class).getName());
        m_cconn = null;
        m_cconn = cconn;
        is = null;
        Thread thread = m_cconn.getMainThread();
        int i = thread.getPriority();
        if(i > 1 && --i > 1)
            i--;
        setPriority(i);
    }

    public void run()
    {
        String s = null;
        is = m_cconn.getInStream();
        try
        {
            while(m_cconn.m_isRunning) 
            {
                parseCommand();
                sleep(1L);
            }
        }
        catch(Throwable throwable)
        {
            if(s_logger.isInfoLoggingEnabled())
                s_logger.info(throwable);
            if(!m_cconn.isExitingProperly())
                s = throwable.toString();
        }
        m_cconn.close();
//        if(!MahoganyViewer.applet && MahoganyViewer.nViewers < 1)
//            System.exit(0);
    }

    protected void parseCommand()
    {
        Command command = Command.getInstance(is.readU8());
        switch(command.getDataType())
        {
        case 225: 
            onInformVesaMode(command, is);
            break;

        case 226: 
            onBitBlt(command, is);
            break;

        case 227: 
            onEnhanceBitBlt(command, is);
            break;

        case 230: 
            onSetPalette(command, is);
            break;

        case 234: 
            onSetTextCursor(command, is);
            break;

        case 235: 
            onSpecialGraphicsBit(command, is);
            break;

        case 236: 
            onMatroxGraphicsCursor(command, is);
            break;

        case 228: 
            onStandbyPower(command, is);
            break;

        case 213: 
            onInformKeyIndicators(command, is);
            break;

        case 197: 
            onMultiUserState(command, is);
            break;

        case 198: 
            onServerDisconnect(command, is);
            break;

        case 200: 
            onServerHandshake(command, is);
            break;

        case 201: 
            onFirmwareVersion(command, is);
            break;

        case 137: 
            onStorageServerStatus(command, is);
            break;

        default:
            onOtherCommand(command, is);
            break;
        }
        command.recycle();
    }

    protected MahoganyProtocol loadCommand(Command command, BufferMgr buffermgr)
    {
        return loadCommand(command, buffermgr, true);
    }

    protected MahoganyProtocol loadCommand(Command command, BufferMgr buffermgr, boolean flag)
    {
        MahoganyProtocol mahoganyprotocol = getProtocol(command.getDataType());
        if(mahoganyprotocol != null)
        {
            mahoganyprotocol.readBuffer(buffermgr);
            if(s_logger.isInfoLoggingEnabled() && flag)
                s_logger.info(mahoganyprotocol.toString());
        }
        return mahoganyprotocol;
    }

    protected MahoganyProtocol getProtocol(int i)
    {
        Object obj = null;
        switch(i)
        {
        case 225: 
            obj = InformVesaMode.getInstance();
            break;

        case 238: 
            obj = GraphicsRegisterValue.getInstance();
            break;

        case 226: 
            obj = BitBlt.getInstance();
            break;

        case 227: 
            obj = EnhanceBitBlt.getInstance();
            break;

        case 230: 
            obj = SetPalette.getInstance();
            break;

        case 234: 
            obj = SetTextCursor.getInstance();
            break;

        case 235: 
            obj = SpecialGraphicsBit.getInstance();
            break;

        case 236: 
            obj = MatroxGraphicsCursor.getInstance();
            break;

        case 197: 
            obj = MultiUserState.getInstance();
            break;

        case 198: 
            obj = ServerDisconnect.getInstance();
            break;

        case 200: 
            obj = ServerHandshake.getInstance();
            break;

        case 201: 
            obj = FirmwareVersion.getInstance();
            break;

        case 213: 
            obj = InformKeyIndicators.getInstance();
            break;

        case 137: 
            obj = StorageStatus.getInstance();
            break;

        case 228: 
            obj = StandbyPower.getInstance();
            break;

        default:
            StringBuffer stringbuffer = StringBufferPool.getInstance("Cannot load invalid protocol: ");
            StringBuffer stringbuffer1 = Helper.formatToHex((byte)i);
            stringbuffer.append(i);
            stringbuffer.append(" (");
            stringbuffer.append(stringbuffer1);
            stringbuffer.append(")");
            String s = stringbuffer.toString();
            if(s_logger.isErrorLoggingEnabled())
                s_logger.error(s);
            StringBufferPool.recycle(stringbuffer1);
            StringBufferPool.recycle(stringbuffer);
            throw new BadException(s);
        }
        return ((MahoganyProtocol) (obj));
    }

    protected void onInformKeyIndicators(Command command, BufferMgr buffermgr)
    {
        InformKeyIndicators informkeyindicators = (InformKeyIndicators)loadCommand(command, buffermgr, m_cconn.isKeyboardEnabled());
        if(m_cconn.isKeyboardEnabled())
        {
            Keyboard keyboard = KeyboardMgr.getInstance().getKeyboard();
            keyboard.informKeyIndicators(informkeyindicators.getCapsLockState(), informkeyindicators.getNumLockState(), informkeyindicators.getScrollLockState());
        }
        informkeyindicators.recycle();
    }

    protected void onMultiUserState(Command command, BufferMgr buffermgr)
    {
        MultiUserState multiuserstate = (MultiUserState)loadCommand(command, buffermgr);
        KeyboardMgr keyboardmgr = KeyboardMgr.getInstance();
        keyboardmgr.getCConn().onMultiUserState(multiuserstate);
        multiuserstate.recycle();
    }

    protected void onServerDisconnect(Command command, BufferMgr buffermgr)
    {
        ServerDisconnect serverdisconnect = (ServerDisconnect)loadCommand(command, buffermgr);
        KeyboardMgr keyboardmgr = KeyboardMgr.getInstance();
        keyboardmgr.getCConn().onServerDisconnect(serverdisconnect.getReason(), serverdisconnect.getMessage());
        serverdisconnect.recycle();
    }

    protected void onServerHandshake(Command command, BufferMgr buffermgr)
    {
        ServerHandshake serverhandshake = (ServerHandshake)loadCommand(command, buffermgr);
        KeyboardMgr keyboardmgr = KeyboardMgr.getInstance();
        keyboardmgr.getCConn().onServerHandshake(serverhandshake);
        serverhandshake.recycle();
    }

    protected void onFirmwareVersion(Command command, BufferMgr buffermgr)
    {
        FirmwareVersion firmwareversion = (FirmwareVersion)loadCommand(command, buffermgr);
        KeyboardMgr keyboardmgr = KeyboardMgr.getInstance();
        keyboardmgr.getCConn().onFirmwareVersion(firmwareversion);
        firmwareversion.recycle();
    }

    protected void onInformVesaMode(Command command, BufferMgr buffermgr)
    {
        InformVesaMode informvesamode = (InformVesaMode)loadCommand(command, buffermgr, m_cconn.isVideoEnabled());
        if(m_cconn.isVideoEnabled())
        {
            GraphicsMgr graphicsmgr = GraphicsMgr.getInstance();
            graphicsmgr.setVesaMode(informvesamode.getModeNumber(), informvesamode.getBpp(), informvesamode.getWidth(), informvesamode.getHeight());
        }
        informvesamode.recycle();
    }

    protected void onBitBlt(Command command, BufferMgr buffermgr)
    {
        BitBlt bitblt = (BitBlt)loadCommand(command, buffermgr, m_cconn.isVideoEnabled());
        if(m_cconn.isVideoEnabled())
        {
            GraphicsMgr graphicsmgr = GraphicsMgr.getInstance();
            Region region = bitblt.getDestinationRectangle();
            graphicsmgr.bitBlt(bitblt.getBiltType(), bitblt.getFontHeight(), bitblt.getFontWidth(), region.getX(), region.getY(), region.getWidth(), region.getHeight(), bitblt.getDataType(), bitblt.getSize());
        }
        bitblt.recycle();
    }

    protected void onEnhanceBitBlt(Command command, BufferMgr buffermgr)
    {
        EnhanceBitBlt enhancebitblt = (EnhanceBitBlt)loadCommand(command, buffermgr, m_cconn.isVideoEnabled());
        if(m_cconn.isVideoEnabled())
        {
            GraphicsMgr graphicsmgr = GraphicsMgr.getInstance();
            graphicsmgr.enhanceBitBlt(enhancebitblt.getBiltType(), enhancebitblt.getTileWidth(), enhancebitblt.getTileHeight(), enhancebitblt.getTripletCode(), enhancebitblt.getRepeatCode(), enhancebitblt.getRawSize(), enhancebitblt.getScrunchSize(), enhancebitblt.getSnoopLowMap(), enhancebitblt.getSnoopHighMap(), enhancebitblt.getDataType());
        }
        enhancebitblt.recycle();
    }

    protected void onSetPalette(Command command, BufferMgr buffermgr)
    {
        SetPalette setpalette = (SetPalette)loadCommand(command, buffermgr, m_cconn.isVideoEnabled());
        if(m_cconn.isVideoEnabled())
        {
            GraphicsMgr graphicsmgr = GraphicsMgr.getInstance();
            graphicsmgr.setPalette(setpalette.getPaletteSet(), setpalette.getAttributeSet(), setpalette.getPaletteSize(), setpalette.getAttributeSize());
        }
        setpalette.recycle();
    }

    protected void onSetTextCursor(Command command, BufferMgr buffermgr)
    {
        SetTextCursor settextcursor = (SetTextCursor)loadCommand(command, buffermgr, m_cconn.isVideoEnabled());
        if(m_cconn.isVideoEnabled())
        {
            GraphicsMgr graphicsmgr = GraphicsMgr.getInstance();
            graphicsmgr.setTextCursor(settextcursor.getCursorX(), settextcursor.getCursorY(), settextcursor.getStartLine(), settextcursor.getStopLine());
        }
        settextcursor.recycle();
    }

    protected void onSpecialGraphicsBit(Command command, BufferMgr buffermgr)
    {
        SpecialGraphicsBit specialgraphicsbit = (SpecialGraphicsBit)loadCommand(command, buffermgr, m_cconn.isVideoEnabled());
        if(m_cconn.isVideoEnabled())
        {
            GraphicsMgr graphicsmgr = GraphicsMgr.getInstance();
            graphicsmgr.blinkingText(specialgraphicsbit.isBlinkingText());
            graphicsmgr.dbeSwap(specialgraphicsbit.isSwapping(), specialgraphicsbit.getPrimarySelectOffset(), specialgraphicsbit.getSecondarySelectOffset());
            graphicsmgr.onSetBPP4ColorIndex(specialgraphicsbit.getBPP4ColorIndex());
        }
        specialgraphicsbit.recycle();
    }

    protected void onMatroxGraphicsCursor(Command command, BufferMgr buffermgr)
    {
        MatroxGraphicsCursor matroxgraphicscursor = (MatroxGraphicsCursor)loadCommand(command, buffermgr, m_cconn.isVideoEnabled());
        if(m_cconn.isVideoEnabled())
        {
            GraphicsMgr graphicsmgr = GraphicsMgr.getInstance();
            graphicsmgr.matroxGraphicsCursor(matroxgraphicscursor.getControl(), matroxgraphicscursor.getData(), matroxgraphicscursor.getCursorMode());
        }
        matroxgraphicscursor.recycle();
    }

    protected void onStandbyPower(Command command, BufferMgr buffermgr)
    {
        MahoganyProtocol mahoganyprotocol = loadCommand(command, buffermgr, m_cconn.isVideoEnabled());
        if(m_cconn.isVideoEnabled())
        {
            GraphicsMgr graphicsmgr = GraphicsMgr.getInstance();
            graphicsmgr.standbyPower();
        }
        mahoganyprotocol.recycle();
    }

    protected void onStorageServerStatus(Command command, BufferMgr buffermgr)
    {
        StorageStatus storagestatus = (StorageStatus)loadCommand(command, buffermgr, m_cconn.isStorageEnabled());
        if(m_cconn.isStorageEnabled())
        {
            KeyboardMgr keyboardmgr = KeyboardMgr.getInstance();
            keyboardmgr.getCConn().onStorageServerStatus(storagestatus);
        }
        storagestatus.recycle();
    }

    protected void onOtherCommand(Command command, BufferMgr buffermgr)
    {
        try
        {
            MahoganyProtocol mahoganyprotocol = loadCommand(command, buffermgr);
            if(mahoganyprotocol != null)
                mahoganyprotocol.recycle();
        }
        catch(Exception exception)
        {
            if(s_logger.isErrorLoggingEnabled())
            {
                StringBuffer stringbuffer = StringBufferPool.getInstance("Unknown command encountered: ");
                StringBuffer stringbuffer1 = Helper.formatToHex((byte)command.getDataType());
                stringbuffer.append(command.getDataType());
                stringbuffer.append(" (");
                stringbuffer.append(stringbuffer1);
                stringbuffer.append(')');
                s_logger.error(stringbuffer.toString());
                StringBufferPool.recycle(stringbuffer);
                StringBufferPool.recycle(stringbuffer1);
                s_logger.error(exception);
            }
        }
    }

    public static final long SLEEP_TIME = 1L;
    protected LogWriter s_logger;
    protected CConn m_cconn;
    protected InStream is;
}
