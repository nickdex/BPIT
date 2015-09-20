package com.nick.bpit.server;

public interface Config
{
    //Values

    String PROJECT_NUMBER = "662517051362";
    String APP_SERVER_URL = "http://192.168.0.113:8080/BPITServer/RegisterUser"; //include ip address of server
    int RC_SIGN_IN = 0;
    int SIGN_IN_SUCCESS = 9001;

    //DEBUG
    boolean DEBUG_FLAG = false;
    String ACTION_DEBUG = "DEBUG";
    String ACTION_REGISTER = "REGISTER";
    String ACTION_BROADCAST = "BROADCAST";
    String ACTION_REFRESH = "REFRESH";
    String TRUE = "T";
    String FALSE = "F";

    String SHOW_DB_MSGS = "SHOWDBMSGS";
    String SHOW_DB_MEMBERS = "SHOWDBMEMS";

    //REQUEST TO SERVER
    String REQUEST_PARAMETER_EMAIL = "EMAIL";
    String REQUEST_PARAMETER_TOKEN = "TOKEN";

    //Key
    String ACTION = "ACTION";
    String TIMESTAMP = "TIMESTAMP";
    String EMAIL = "EMAIL";
    //MESSAGES
    String MESSAGE_TABLE = "MESSAGE";
    String MESSAGE_BODY = "BODY";
    //MEMBER
    String MEMBER_TABLE = "MEMBER";
    String MEMBER_NAME = "NAME";
    String NEW_MEMBER = "NEW_MEM";
}
