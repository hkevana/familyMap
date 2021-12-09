package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.*;
import request.RegisterReq;
import response.RegisterRes;
import service.RegisterSer;

import java.io.*;
import java.net.HttpURLConnection;

public class RegisterHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            // Log request
            String method = exchange.getRequestMethod().toUpperCase();
            String urlPath = exchange.getRequestURI().toString();
            System.out.printf("\n%s %s ", method, urlPath);

            RegisterRes resData = null;
            if (method.equals("POST")) {
                // Convert Json Request Body into Register Request Object
                InputStream reqBody = exchange.getRequestBody();
                RegisterReq reqData = convertReq(reqBody);
                // Send Request to register new user to server - Log response
                RegisterSer service = new RegisterSer();
                resData = service.registerUser(reqData);
                System.out.print(resData.toString());

                if (resData.isSuccess()) { exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0); }
                else { exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0); }

                // Covert Response Data back into Json Response Body Format
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
    private RegisterReq convertReq(InputStream is) {
        InputStreamReader sr = new InputStreamReader(is);
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        return g.fromJson(sr, RegisterReq.class);
    }
    private void convertRes(RegisterRes data, OutputStream body) throws IOException {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter sw = new OutputStreamWriter(body);
        String json = g.toJson(data);
        sw.write(json);
        sw.flush();
    }
}
