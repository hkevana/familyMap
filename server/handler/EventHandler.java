package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.*;
import request.EventReq;
import request.LoadReq;
import response.EventRes;
import response.LoadRes;
import service.AuthTokenSer;
import service.EventSer;

import java.io.*;
import java.net.HttpURLConnection;

public class EventHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        try {
            String method = exchange.getRequestMethod().toUpperCase();
            String urlPath = exchange.getRequestURI().toString();
            System.out.printf("\n%s %s ", method, urlPath);

            EventRes resData;
            if (method.equals("GET")) {
                Headers reqHeaders = exchange.getRequestHeaders();
                if (reqHeaders.containsKey("Authorization")) {

                    // Validate and get auth token from database
                    String authToken = reqHeaders.getFirst("Authorization");
                    AuthTokenSer serviceAuthToken = new AuthTokenSer();
                    if (serviceAuthToken.validateToken(authToken) != null) {

                        EventSer serviceEvent = new EventSer();

                        // Get Response Data
                        // If /event - return all events associated with user
                        // Else /event/{ID} - return specified event associated with ID
                        if (urlPath.equals("/event")) { resData = serviceEvent.event(authToken); }
                        else {
                            // Get Requested ID
                            String eventID = urlPath.substring((urlPath.indexOf("/", 6) + 1));
                            EventReq reqData = new EventReq(eventID, authToken);

                            resData = serviceEvent.event(reqData);
                        }

                        // Log response data
                        System.out.print(resData.toString());

                        if (resData.isSuccess()) { exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0); }
                        else { exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0); }

                        OutputStream resBody = exchange.getResponseBody();
                        convertRes(resData, resBody);

                        resBody.close();
                        success = true;
                    }
                }
            }
            if (!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                OutputStream resBody = exchange.getResponseBody();
                resData = new EventRes("error: bad request");
                convertRes(resData, resBody);
                resBody.close();
            }
        } catch (IOException ex) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            ex.printStackTrace();
        }
    }
    private void convertRes(EventRes data, OutputStream body) throws IOException {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter sw = new OutputStreamWriter(body);
        String json = g.toJson(data);
        sw.write(json);
        sw.flush();
    }
}
