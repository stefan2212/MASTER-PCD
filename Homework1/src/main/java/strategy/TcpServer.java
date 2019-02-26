package strategy;


import utils.Constants;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings("Duplicates")
public class TcpServer implements Server {


    private static final Integer MAX_MESSAGE_SIZE = 65000;
    private ServerSocket serverSocket;
    private Socket socket;

    public TcpServer() throws IOException {
        serverSocket = new ServerSocket(Constants.PORT);
    }

    public void accept() throws IOException {
        System.out.println(String.format("Server is listening on port %s", Constants.PORT));
        while(true) {
            socket = serverSocket.accept();
            new Thread(serve()).start();
        }

    }

    public void sendMessage(String message) throws IOException {
        byte[] byteMessage = message.getBytes();
        DataOutputStream stream = new DataOutputStream(socket.getOutputStream());
        stream.writeInt(byteMessage.length);
        stream.write(byteMessage);
    }

    public byte[] reciveMessage() throws Exception {
        DataInputStream stream = new DataInputStream(socket.getInputStream());
        int length = stream.readInt();
        if (length > 0) {
            byte[] message = new byte[length];
            stream.readFully(message, 0, length);
            return message;
        }
        return null;
    }

    private Runnable serve() {
        return () -> {
            try {
                System.out.println(messageConverter(reciveMessage()));
                sendMessage(String.valueOf(MAX_MESSAGE_SIZE));
                reciveStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private String messageConverter(byte[] message) {
        return new String(message);
    }

    private void reciveStream() throws IOException {
        int messagesRecived = 0;
        int bytesRecived = 0;
        BufferedWriter br = new BufferedWriter(new FileWriter("testfile", true));
        DataInputStream stream = new DataInputStream(socket.getInputStream());
        while (bytesRecived < Constants.FILE_SIZE) {
            byte[] chunckMessage = new byte[MAX_MESSAGE_SIZE];
            stream.readFully(chunckMessage, 0, MAX_MESSAGE_SIZE);
            System.out.println(String.format("Recived bytes %s", MAX_MESSAGE_SIZE));
            bytesRecived += MAX_MESSAGE_SIZE;
            br.write(messageConverter(chunckMessage));
        }
        br.close();
        System.out.println("File was recived");
        serverSocket.close();

    }

    private void stopAndWait() throws IOException, InterruptedException {
        int ack = 0;
        int bytesRecived = 0;
        BufferedWriter br = new BufferedWriter(new FileWriter("testfile", true));
        DataInputStream stream = new DataInputStream(socket.getInputStream());
        while (bytesRecived < Constants.FILE_SIZE) {
            byte[] chunckMessage = new byte[MAX_MESSAGE_SIZE];
            Thread.sleep(Constants.TIMEOUT);
            stream.readFully(chunckMessage, 0, MAX_MESSAGE_SIZE);
            System.out.println(String.format("Recived bytes %s", MAX_MESSAGE_SIZE));
            bytesRecived+= MAX_MESSAGE_SIZE;
            br.write(messageConverter(chunckMessage));

            //Send ack to client
            ack++;
            System.out.println("Sending ack");
            sendMessage(String.valueOf(ack));
        }
        br.close();
        System.out.println("File was recived");

    }


}
