package ru.mipt.farmremotecontrolapp;

import static java.util.Objects.isNull;

public class ControlMessage {
    public static final int NO_FLAG = 0;
    public static final int SET_CONFIG = 1;
    public static final int GET_CONFIG = 2;
    public static final int GET_STATS = 3;
    public static final int CHECK_FARM = 4;


    private int flag = 0;
    private int fromTime = 0;
    private int toTime = Integer.MAX_VALUE;
    public static ControlMessage getStats(Integer ft, Integer tt){
        ControlMessage controlMessage = new ControlMessage();
        controlMessage.flag = GET_STATS;
        if(!isNull(ft)){controlMessage.fromTime = ft;}
        if(!isNull(tt)){controlMessage.fromTime = tt;}
        return controlMessage;

    }

    private ControlMessage(){

    }
}
