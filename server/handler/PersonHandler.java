package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.*;
import model.AuthToken;
import request.EventReq;
import request.PersonReq;
import response.EventRes;
import response.PersonRes;
import service.AuthTokenSer;
import service.PersonSer;

import java.io.*;
import java.net.HttpURLConnection;

public class PersonHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        try {
            String method = exchange.getRequestMethod().toUpperCase();
            String urlPath = exchange.getRequestURI().toString();
            System.out.printf("\n%s %s ", method, urlPath);

            PersonRes resData = null;
            if (method.equals("GET")) {
                Headers reqHeaders = exchange.getRequestHeaders();

                // Validate request authentication token
                if (reqHeaders.containsKey("Authorization")) {
                    String authToken = reqHeaders.getFirst("Authorization");
                    AuthTokenSer serviceAT = new AuthTokenSer();

                    if (serviceAT.validateToken(authToken) != null) {
                        // Send request to find Person to server
                        PersonSer serviceP = new PersonSer();
                        if (urlPath.equals("/person")) { resData = serviceP.findPerson(authToken); }
                        else {
                            // Get Requested Person ID from URL Path
                            String personID = urlPath.substring((urlPath.indexOf("/", 7) + 1));
                            PersonReq reqData = new PersonReq(personID, serviceAT.findToken(authToken).getUserID());

                            resData = serviceP.findPerson(reqData);
                        }

                        // Log response data
                        System.out.print(resData.toString());

                        if (resData.isSuccess()) { exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0); }
                        else { exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0); }

                        // Convert Response Data back into Json Response Body Format
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
                resData = new PersonRes("error: bad request");
                convertRes(resData, resBody);
                resBody.close();
            }
        } catch(IOException ex) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            ex.printStackTrace();
        }
    }
    private void convertRes(PersonRes data, OutputStream body) throws IOException {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter sw = new OutputStreamWriter(body);
        String json = g.toJson(data);
        sw.write(json);
        sw.flush();
    }
}
