package com.nick.bpit;

public interface Config
{
    String PROJECT_NUMBER = "662517051362";
    String SEPARATOR = "$@$";
    String APP_SERVER_URL = "http://192.168.0.113:8080/BPITServer/RegisterUser"; //include ip address of server
    int RC_SIGN_IN = 0;
    int SIGN_IN_SUCCESS = 9001;

    //MESSAGES
    String MESSAGE_TABLE = "MESSAGE";
    String MESSAGE_TIME = "TIMESTAMP";
    String MESSAGE_BODY = "BODY";

    //MEMBER
    String MEMBER_TABLE = "MEMBER";
    String MEMBER_NAME = "NAME";
    String MEMBER_EMAIL = "EMAIL";
}
