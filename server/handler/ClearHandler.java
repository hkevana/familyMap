package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.*;
import response.ClearRes;
import service.ClearSer;

import java.io.*;
import java.net.HttpURLConnection;

public class ClearHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String urlPath = exchange.getRequestURI().toString();
            System.out.printf("\n%s %s ", method, urlPath);

            if (method.equals("POST")) {
                ClearSer service = new ClearSer();
                ClearRes resData = service.clear();
                System.out.print(resData.toString());

                if (resData.isSuccess()) { exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0); }
                else { exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0); }

                OutputStream resBody = exchange.getResponseBody();
                convertRes(resData, resBody);
                resBody.close();
            }
        }
        catch(IOException ex) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            ex.printStackTrace();
        }
    }
    private void convertRes(ClearRes cr, OutputStream os) throws IOException {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        OutputStreamWriter sw = new OutputStreamWriter(os);
        String json = g.toJson(cr);
        sw.write(json);
        sw.flush();
    }
}
