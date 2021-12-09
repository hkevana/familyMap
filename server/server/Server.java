package server;

import com.sun.net.httpserver.*;
import handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    private static final int MAX_WAITING_CONNECTIONS = 12;

    private HttpServer server;

    private void run(String port) {
        System.out.println("Initializing HTTP Server");

        // Initialize server
        try {
            server = HttpServer.create(
                    new InetSocketAddress(Integer.parseInt(port)),
                    MAX_WAITING_CONNECTIONS);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        server.setExecutor(null);

        // define context "routes"
        System.out.println("Creating contexts");

        server.createContext("/user/register", new RegisterHandler());
        server.createContext("/user/login", new LoginHandler());
        server.createContext("/clear", new ClearHandler());
        server.createContext("/fill", new FillHandler());
        server.createContext("/load", new LoadHandler());
        server.createContext("/person", new PersonHandler());
        server.createContext("/event", new EventHandler());
        server.createContext("/", new FileHandler());

        // Start the server
        System.out.println("Starting server");
        server.start();
        System.out.printf("\nServer listening on port %s\n", port);

    }

    public static void main(String[] args) {
        // Default port = 8080 unless otherwise specified
        String port = "8080";
        if (args.length > 0) { port = args[0]; }

        new Server().run(port);
    }

}
