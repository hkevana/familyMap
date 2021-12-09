package handler;

import com.sun.net.httpserver.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.nio.file.Files;

public class FileHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (exchange.getRequestMethod().toUpperCase().equals("GET")) {
                // Get requested url
                String urlPath = exchange.getRequestURI().toString();
                System.out.print("\nGET " + urlPath);

                if (urlPath == null || urlPath.equals("") || urlPath.equals("/")) { urlPath = "/index.html"; }

                // convert file path to File if exists and send response
                String filePath = "web" + urlPath;
                File location = new File(filePath);
                if (location.exists()) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream resBody = exchange.getResponseBody();
                    Files.copy(location.toPath(), resBody);

                    resBody.close();
                } else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                    OutputStream resBody = exchange.getResponseBody();
                    Files.copy(new File("web/HTML/404.html").toPath(), resBody);
                    resBody.close();
                }
            }
        } catch(IOException ex) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
            exchange.getResponseBody().close();
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
