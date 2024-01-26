import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    private final int port = 8989;
    private HashMap<String, ArrayList<Integer>> cookies = new HashMap<>(); // Fix: Use Integer instead of int

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            System.out.println("Listening on port: " + this.port);

            while (true) {

                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                    String line;
                    while ((line = in.readLine()) != null) { // read
                        System.out.println(" <<< " + line); // log

                        if (line.matches("GET\\s+.*")) {
                            // Generate a new random number
                            int randomNumber = (int) (Math.random() * 100);
                            
                            // Create a new cookie
                            String cookie = UUID.randomUUID().toString();
                            
                            // Initialize a guess count
                            int guessCount = 0;
                        
                            // Add information to the cookie
                            ArrayList<Integer> cookieInfo = new ArrayList<>();
                            cookieInfo.add(randomNumber);
                            cookieInfo.add(guessCount);
                            cookies.put(cookie, cookieInfo);

                           
                            String htmlContent = "<!DOCTYPE html>\n" +
                            "<html lang=\"en\">\n" +
                            "<head>\n" +
                            "    <meta charset=\"UTF-8\">\n" +
                            "    <title>Number Guess Game</title>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "    Welcome to the Number Guess Game. <br>\n" +
                            "    Guess a number between 1 and 100\n" +
                            "    <form name=\"guessform\" action=\"/\" method=\"POST\">\n" +
                            "        <input type=\"text\" name=\"gissadeTalet\">\n" +
                            "        <input type=\"submit\" value=\"Guess\">\n" +
                            "    </form>\n" +
                            "</body>\n" +
                            "</html>\n";

                            out.write("HTTP/1.1 200 OK\r\n");
                            out.write("Set-Cookie: " + cookie + "\r\n");
                            out.write("Content-Type: text/html\r\n");
                            out.write("Content-Length: " + htmlContent.length() + "\r\n");
                            out.write("\r\n");
                            out.write(htmlContent);
                            out.flush();
                            socket.close(); // close the connection explicitly

                            // Process the GET request
                        } else if (line.matches("POST\\s+.*")) {
                            // Get the cookie
                            String cookie = line.substring(line.indexOf("Cookie: ") + 8, line.indexOf("Cookie: ") + 44);

                            // Get the guess from the POST request
                            String guess = line.substring(line.indexOf("gissadeTalet=") + 13, line.indexOf("gissadeTalet=") + 15); 

                            // Get the cookie information
                            ArrayList<Integer> cookieInfo = cookies.get(cookie);

                            // Get the random number
                            int randomNumber = cookieInfo.get(0);

                            // Update and get the guess count
                            cookieInfo.set(1, cookieInfo.get(1) + 1);
                            int guessCount = cookieInfo.get(1);

                            // Check if the guess is correct
                            if (Integer.parseInt(guess) == randomNumber) {
                                String htmlContent = "<!DOCTYPE html>\n" +
                                "<html lang=\"en\">\n" +
                                "<head>\n" +
                                "    <meta charset=\"UTF-8\">\n" +
                                "    <title>Number Guess Game</title>\n" +
                                "</head>\n" +
                                "<body>\n" +
                                "    You guessed the correct number! <br>\n" +
                                "    It took you " + guessCount + " guesses.\n" +
                                "</body>\n" +
                                "</html>\n";

                                out.write("HTTP/1.1 200 OK\r\n");
                                out.write("Content-Type: text/html\r\n");
                                out.write("Content-Length: " + htmlContent.length() + "\r\n");
                                out.write("\r\n");
                                out.write(htmlContent);
                                out.flush();
                            } else {
                                String htmlContent = "<!DOCTYPE html>\n" +
                                "<html lang=\"en\">\n" +
                                "<head>\n" +
                                "    <meta charset=\"UTF-8\">\n" +
                                "    <title>Number Guess Game</title>\n" +
                                "</head>\n" +
                                "<body>\n" +
                                "    You guessed wrong! <br>\n" +
                                "    Guess a number between 1 and 100\n" +
                                "    <form name=\"guessform\" action=\"/\" method=\"POST\">\n" +
                                "        <input type=\"text\" name=\"gissadeTalet\">\n" +
                                "        <input type=\"submit\" value=\"Guess\">\n" +
                                "    </form>\n" +
                                "</body>\n" +
                                "</html>\n";

                                out.write("HTTP/1.1 200 OK\r\n");
                                out.write("Content-Type: text/html\r\n");
                                out.write("Content-Length: " + htmlContent.length() + "\r\n");
                                out.write("\r\n");
                                out.write(htmlContent);
                                out.flush();
                            }

                            // process the POST request
                        }
                    }

                    System.out.println(" >>> " + "HTTP RESPONSE"); // log
                    out.write("HTTP RESPONSE"); // write
                    out.flush(); // flush

                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }

            }
            
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Could not listen on port: " + this.port);
            System.exit(1);
        }
    }
}
