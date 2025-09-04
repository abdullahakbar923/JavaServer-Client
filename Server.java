
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private static BufferedWriter bufferedWriter;
    private static BufferedReader bufferedReader;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(1234);
            System.out.println("Server is listening on port 1234...");

            socket = serverSocket.accept();
            System.out.println("Client connected! You can now chat.");

            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Thread to receive messages from client
            Thread receiveThread = new Thread(() -> {
                try {
                    String clientMessage;
                    while ((clientMessage = bufferedReader.readLine()) != null) {
                        System.out.println("Client: " + clientMessage);
                        
                        if (clientMessage.equalsIgnoreCase("STOP")) {
                            System.out.println("Client ended the chat.");
                            System.exit(0);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Connection lost.");
                }
            });

            // Thread to send messages to client
            Thread sendThread = new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                try {
                    while (true) {
                        String msgToSend = scanner.nextLine();
                        
                        bufferedWriter.write("Server: " + msgToSend);
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
                if (serverSocket != null) serverSocket.close();
                if (bufferedReader != null) bufferedReader.close();
                if (bufferedWriter != null) bufferedWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}