package com.nick.bpit.server;

public interface Config
{
    String PROJECT_NUMBER = "662517051362";
    String APP_SERVER_URL = "http://192.168.0.113:8080/BPITServer/RegisterUser"; //include ip address of server
    int RC_SIGN_IN = 0;
    int SIGN_IN_SUCCESS = 9001;
    String ADMIN_EMAIL = "nikwarke@gmail.com";

    //REQUEST TO SERVER
    String REQUEST_PARAMETER_EMAIL = "EMAIL";
    String REQUEST_PARAMETER_TOKEN = "TOKEN";

    String PAYLOAD_EMAIL = "EMAIL";
    String PAYLOAD_MESSAGE = "BODY";
    String ACTION = "ACTION";
    String ACTION_REGISTER = "REGISTER";
    String ACTION_BROADCAST = "BROADCAST";

    String TIMESTAMP = "TIMESTAMP";
    String EMAIL = "EMAIL";
    //MESSAGES
    String MESSAGE_TABLE = "MESSAGE";
    String MESSAGE_BODY = "BODY";

    //MEMBER
    String MEMBER_TABLE = "MEMBER";
    String MEMBER_NAME = "NAME";

    String MEMBER_TOKEN = "TOKEN"
;}
