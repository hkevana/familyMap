package byu.cs240.familymapclient.net;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import byu.cs240.familymapclient.req.LoginReq;
import byu.cs240.familymapclient.req.DataReq;
import byu.cs240.familymapclient.req.RegisterReq;
import byu.cs240.familymapclient.res.DataRes;
import byu.cs240.familymapclient.res.EventRes;
import byu.cs240.familymapclient.res.LoginRes;
import byu.cs240.familymapclient.res.PersonRes;
import byu.cs240.familymapclient.res.RegisterRes;

public class ServerProxy {

    private static final String TAG = "ServerProxy";

    private static ServerProxy _instance;

    public static String hostName;
    public static String portNumber;

    public static ServerProxy getInstance() {
        if (_instance == null) { _instance = new ServerProxy(); }
        return _instance;
    }

    private ServerProxy() {
        // Default IP address and port number
        hostName = "10.0.2.2";
        portNumber = "8080";
    }

    public void clear() {
        try {
            URL url = new URL("http://" + hostName + ":" + portNumber + "/clear");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(false);

            http.addRequestProperty("Accept", "application/json"); // ACCEPTS JSON RESPONSES

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) { http.getInputStream().close(); }
            else { System.out.println("ERROR: " + http.getResponseMessage()); }

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public LoginRes login(LoginReq req) {
        LoginRes resData = null;
        try {
            URL url = new URL("http://" + hostName + ":" + portNumber + "/user/login");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true); // specify whether there is a req body

            // ADD REQUEST HEADERS
//            http.addRequestProperty("Authorization", "TOKEN");
            http.addRequestProperty("Accept", "application/json"); // ACCEPTS JSON RESPONSES
            http.addRequestProperty("Content-Type", "application/json"); // req body contains json data

            http.connect();

//            Log.d("ServerProxy::Login", req.toString());
            // HOW TO SEND A REQ BODY
            OutputStream reqBody = http.getOutputStream();
            convertReq(req, reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream resBody = http.getInputStream();
                resData = (LoginRes)convertRes(resBody, "LoginRes");
                resBody.close();
            } else {
                resData = new LoginRes("ERROR: bad request");
            }
        } catch(Exception ex) { ex.printStackTrace(); }
        return resData;
    }

    public RegisterRes register(RegisterReq req) {
        RegisterRes resData = null;
        try {
            URL url = new URL("http://" + hostName + ":" + portNumber + "/user/register");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true); // specify whether there is a req body

            // ADD REQUEST HEADERS
//            http.addRequestProperty("Authorization", "TOKEN");
            http.addRequestProperty("Accept", "application/json"); // ACCEPTS JSON RESPONSES
            http.addRequestProperty("Content-Type", "application/json"); // req body contains json data

            http.connect();

//            Log.d("ServerProxy::Register", req.toString());
            // HOW TO SEND A REQ BODY
            OutputStream reqBody = http.getOutputStream();
            convertReq(req, reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream resBody = http.getInputStream();
                resData = (RegisterRes)convertRes(resBody, "RegisterRes");
                resBody.close();
            } else { resData = new RegisterRes("ERROR: Bad Request"); }

        } catch(Exception ex) { ex.printStackTrace(); }

        return resData;
    }

    public DataRes getData(DataReq req) {
//        Log.i(TAG + "::getData", req.toString());

        PersonRes resPersons = getAllPersons(req);
        EventRes resEvents = getAllEvents(req);
        if (resPersons.isSuccess() && resEvents.isSuccess()) {
            return new DataRes(resPersons.getData(), resEvents.getData(), req.getPersonID(), true, null);
        } else { return new DataRes(null, null, null, false, "Data Retrieval Failed"); }
    }

    private PersonRes getAllPersons(DataReq req) {
//        Log.i(TAG + "::getPersons", req.toString());

        PersonRes resData = null;
        try {
            URL url = new URL("http://" + hostName + ":" + portNumber + "/person");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);

//          ADD REQUEST HEADERS
            http.addRequestProperty("Authorization", req.getAuthToken());
            http.addRequestProperty("Accept", "application/json"); // ACCEPTS JSON RESPONSES

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream resBody = http.getInputStream();
                resData = (PersonRes)convertRes(resBody, "PersonRes");
                resBody.close();
            } else {
                resData = new PersonRes("ERROR: Bad Request");
            }
        } catch(Exception ex) { ex.printStackTrace(); }
        return resData;
    }

    private EventRes getAllEvents(DataReq req) {
//        Log.i(TAG + "::getEvents", req.toString());

        EventRes resData = null;
        try {
            URL url = new URL("http://" + hostName + ":" + portNumber + "/event");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);

//          ADD REQUEST HEADERS
            http.addRequestProperty("Authorization", req.getAuthToken());
            http.addRequestProperty("Accept", "application/json"); // ACCEPTS JSON RESPONSES

            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream resBody = http.getInputStream();
                resData = (EventRes) convertRes(resBody, "EventRes");
                resBody.close();
            } else {
                resData = new EventRes("ERROR: Bad Request");
            }
        } catch(Exception ex) { ex.printStackTrace(); }
        return resData;
    }


    private static Object convertRes(InputStream is, String type) throws IOException {
//        Log.i(TAG + "::convertRes", "Current class: " +  type);

        InputStreamReader sr = new InputStreamReader(is);
        Gson g = new GsonBuilder().setPrettyPrinting().create();

        switch (type) {
            case "LoginRes": return g.fromJson(sr, LoginRes.class);
            case "RegisterRes": return g.fromJson(sr, RegisterRes.class);
            case "PersonRes": return g.fromJson(sr, PersonRes.class);
            case "EventRes": return g.fromJson(sr, EventRes.class);
            default: return null;
        }
    }
    private static void convertReq(Object data, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        Gson g = new GsonBuilder().setPrettyPrinting().create();

        String json;
        String classType = data.getClass().toString();
        if (classType.contains("Login")) { json = g.toJson((LoginReq)data); }
        else if (classType.contains("Register")) { json = g.toJson((RegisterReq)data); }
        else { return; }

        sw.write(json);
        sw.flush();
    }


    public static String getHostName() { return hostName; }
    public static void setHostName(String hostName) { ServerProxy.hostName = hostName; }

    public static String getPortNumber() { return portNumber; }
    public static void setPortNumber(String portNumber) { ServerProxy.portNumber = portNumber; }
}
