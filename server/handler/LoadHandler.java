package handler;

import com.google.gson.*;
import com.sun.net.httpserver.*;
import request.LoadReq;
import response.LoadRes;
import service.LoadSer;

import java.io.*;
import java.net.HttpURLConnection;

public class LoadHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod().toUpperCase();
            String urlPath = exchange.getRequestURI().toString();
            System.out.printf("\n%s %s ", method, urlPath);

            if (method.equals("POST")) {
                // convert Request Body into Load Request Object
                InputStream reqBody = exchange.getRequestBody();
                LoadReq reqData = convertReq(reqBody);

                // Send request to load
                LoadSer service = new LoadSer();
                LoadRes resData = service.load(reqData);
                System.out.print(resData.toString());

                if (resData.isSuccess()) { exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0); }
                else { exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0); }

                // Convert Response Data back into Json Response Body
                OutputStream resBody = exchange.getResponseBody();
                convertRes(resData, resBody);
                resBody.close();
            }
        } catch (IOException ex) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            ex.printStackTrace();
        }
    }

    private LoadReq convertReq(InputStream body) {
        InputStreamReader sr = new InputStreamReader(body);
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        return g.fromJson(sr, LoadReq.class);
    }
    private void convertRes(LoadRes data, OutputStream body) throws IOException {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter sw = new OutputStreamWriter(body);
        String json = g.toJson(data);
        sw.write(json);
        sw.flush();
    }
}
