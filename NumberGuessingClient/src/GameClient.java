import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class GameClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // 1. Tell the server to start/reset the game
            HttpRequest startRequest = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/start"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> startResponse = client.send(startRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("Server says: " + startResponse.body());

            // 2. Core Game Loop
            boolean gameWon = false;
            while (!gameWon) {
                System.out.print("Enter your guess: ");
                String userGuess = scanner.nextLine();

                // Package the user's string guess directly into the request body
                HttpRequest guessRequest = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/guess"))
                        .POST(HttpRequest.BodyPublishers.ofString(userGuess))
                        .build();

                HttpResponse<String> guessResponse = client.send(guessRequest, HttpResponse.BodyHandlers.ofString());
                String result = guessResponse.body();

                // 3. Process the server's evaluation
                if (result.startsWith("CORRECT")) {
                    System.out.println("Right" + result);
                    gameWon = true;
                } else if ("TOO_HIGH".equals(result)) {
                    System.out.println("Too high! Try a lower number.");
                } else if ("TOO_LOW".equals(result)) {
                    System.out.println("Too low! Try a higher number.");
                } else {
                    System.out.println("Error: " + result);
                }
            }

        } catch (Exception e) {
            System.err.println("Could not connect to the server. Make sure GameServer is running!");
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
