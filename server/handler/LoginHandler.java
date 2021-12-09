package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.*;
import request.LoadReq;
import request.LoginReq;
import response.LoadRes;
import response.LoginRes;
import service.LoginSer;

import javax.print.attribute.standard.PresentationDirection;
import java.io.*;
import java.net.HttpURLConnection;

public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod().toUpperCase();
            String urlPath = exchange.getRequestURI().toString();
            System.out.printf("\n%s %s ", method, urlPath);

            if (method.equals("POST")) {
                // Convert Request Body into Login Request Object
                InputStream reqBody = exchange.getRequestBody();
                LoginReq reqData = convertReq(reqBody);

                // Send request to login user
                LoginSer service = new LoginSer();
                LoginRes resData = service.loginUser(reqData);
                System.out.print(resData.toString());

                if (resData.isSuccess()) { exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0); }
                else { exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0); }

                // Convert Response Data back into Json Response Body format
                OutputStream resBody = exchange.getResponseBody();
                convertRes(resData, resBody);
                resBody.close();
            }
        } catch(IOException ex) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            ex.printStackTrace();
        }

    }
    private LoginReq convertReq(InputStream body) {
        InputStreamReader sr = new InputStreamReader(body);
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        return g.fromJson(sr, LoginReq.class);
    }
    private void convertRes(LoginRes data, OutputStream body) throws IOException {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter sw = new OutputStreamWriter(body);
        String json = g.toJson(data);
        sw.write(json);
        sw.flush();
    }
}
