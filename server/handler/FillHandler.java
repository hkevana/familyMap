package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.*;
import request.FillReq;
import response.FillRes;
import service.FillSer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class FillHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod().toUpperCase();
            String urlPath = exchange.getRequestURI().toString();
            System.out.printf("\n%s %s ", method, urlPath);

            if (method.equals("POST")) {
                String username = urlPath.substring(6);

                FillReq reqData;
                if (username.contains("/")) {
                    // Get number of generations to fill from URL Path
                    String generations = username.substring(username.indexOf("/") + 1);
                    username = username.substring(0, username.indexOf("/"));

                    reqData = new FillReq(username, Integer.parseInt(generations));
                }
                else { reqData = new FillReq(username); } // Default generations = 4

                // Send request to fill database
                FillSer service = new FillSer();
                FillRes resData = service.fill(reqData);

                if (resData.isSuccess()) { exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0); }
                else { exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0); }

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
    private void convertRes(FillRes data, OutputStream body) throws IOException {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter sw = new OutputStreamWriter(body);
        String json = g.toJson(data);
        sw.write(json);
        sw.flush();
    }
}
