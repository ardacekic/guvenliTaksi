package com.example.mapsapp;

public class Constants {
    //TODO: CHANGE THIS WHEN WE WILL USE "REAL" SERVER AND DEVICE...Neden calismiyor??
    //private static final String ROOT_URL = "http://10.0.2.2/Android/v1/";
    private static final String ROOT_URL = "http://8269-78-175-228-166.ngrok.io/Android/v1/";
    public static final String URL_REGISTER = ROOT_URL + "registerUser.php";
    public static final String URL_LOGIN = ROOT_URL + "loginUser.php";
    public static final String URL_GetAllTaksiInfo = ROOT_URL + "getAllTaksi.php";
    public static final String URL_Request_data = ROOT_URL + "request_data_fromUser.php";
    public static final String URL_Does_Taksi_Accepted = ROOT_URL + "doesTaksiAccepted.php";
}
