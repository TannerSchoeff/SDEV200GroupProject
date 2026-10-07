import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Random;

public class GameServer {
    private static int secretNumber;
    private static int attempts;

    public static void main(String[] args) throws IOException {
        // Create an HTTP server listening on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Define our API endpoints
        server.createContext("/api/start", new StartHandler());
        server.createContext("/api/guess", new GuessHandler());

        server.setExecutor(null); // default executor
        System.out.println("🚀 Game Server started on port 8080...");
        server.start();
    }

    // Handles POST /api/start
    static class StartHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                secretNumber = new Random().nextInt(100) + 1; // 1 to 100
                attempts = 0;
                System.out.println("New game started. Secret number is: " + secretNumber);

                String response = "Game initialized! Guess a number between 1 and 100.";
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    // Handles POST /api/guess
    static class GuessHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                // Read the guess sent in the request body
                String body = new String(exchange.getRequestBody().readAllBytes());
                try {
                    int guess = Integer.parseInt(body.trim());
                    attempts++;

                    String response;
                    if (guess < secretNumber) {
                        response = "TOO_LOW";
                    } else if (guess > secretNumber) {
                        response = "TOO_HIGH";
                    } else {
                        response = "CORRECT. You got it in " + attempts + " attempts!";
                    }

                    sendResponse(exchange, 200, response);
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "Invalid integer format.");
                }
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    // Helper method to send plain text responses back to the client
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}