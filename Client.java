// Enhanced Client.java - Copy this into your Client.java file
import java.io.*;
import java.util.Scanner;
import java.net.Socket;

public class Client {
    private static BufferedWriter bufferedWriter;
    private static BufferedReader bufferedReader;

    public static void main(String[] args) {
        Socket socket = null;

        try {
            socket = new Socket("localhost", 1234);
            System.out.println("Connected to server! You can now chat.");

            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Thread to receive messages from server
            Thread receiveThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = bufferedReader.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Connection lost.");
                }
            });

            // Thread to send messages to server
            Thread sendThread = new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                try {
                    while (true) {
                        String msgToSend = scanner.nextLine();
                        
                        bufferedWriter.write("Client: " + msgToSend);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        
                        if (msgToSend.equalsIgnoreCase("STOP")) {
                            System.out.println("You ended the chat.");
                            System.exit(0);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error sending message.");
                }
            });

            receiveThread.start();
            sendThread.start();

            // Wait for threads to complete
            receiveThread.join();
            sendThread.join();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) socket.close();
                if (bufferedReader != null) bufferedReader.close();
                if (bufferedWriter != null) bufferedWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}