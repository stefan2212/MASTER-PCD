package strategy;

import utils.Constants;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;

@SuppressWarnings("ALL")
public class TcpClient implements Client {

    private final static Integer PORT = 65000;
    private final static String IP = "127.0.0.1";
    private Socket clientSocket;

    public TcpClient() throws IOException {
        clientSocket = new Socket(IP, PORT);
    }

    public byte[] reafFileData() {
        File file;
        try {
            return Files.readAllBytes(Constants.FILE_PATH);
        } catch (IOException e) {
            System.out.println("Could not read data from file");
        }
        return null;
    }

    public void connect() throws Exception {
        sendMessage("Hello");
        System.out.println();
        int messageSize = Integer.parseInt(messageConverter(reciveMessage()));
        streamSendFile(messageSize, Constants.FILE_SIZE);
    }

    public void sendMessage(String message) throws IOException {
        byte[] byteMessage = message.getBytes();
        DataOutputStream stream = new DataOutputStream(clientSocket.getOutputStream());
        stream.writeInt(byteMessage.length);
        stream.write(byteMessage);
    }

    @SuppressWarnings("Duplicates")
    public byte[] reciveMessage() throws Exception {
        DataInputStream stream = new DataInputStream(clientSocket.getInputStream());
        int length = stream.readInt();
        if (length > 0) {
            byte[] message = new byte[length];
            stream.readFully(message, 0, message.length);
            return message;
        }
        return null;
    }

    private String messageConverter(byte[] message) {
        return new String(message);
    }

    private void streamSendFile(int messageBlock, int fileSize) throws IOException {
        byte[] fileData = reafFileData();
        DataOutputStream stream = new DataOutputStream(clientSocket.getOutputStream());
        int messagesSended = 0;
        int bytesSended = 0;
        int bufferIdx = messageBlock;
        long start = System.currentTimeMillis();
        while (bytesSended < fileSize) {
            System.out.println(String.format("Sending %s bytes of data", messageBlock));
            byte[] batchMessage = Arrays.copyOfRange(fileData, bufferIdx - messageBlock, bufferIdx);
            stream.write(batchMessage);
            messagesSended++;
            bytesSended += messageBlock;
            System.out.println(String.format("Remaining bytes to be send %s", fileSize - bytesSended));
        }
        System.out.println("Files was sent");
        System.out.println(String.format("Time spend %s", System.currentTimeMillis()-start));
        clientSocket.close();
        System.out.println(String.format("Number of sent messages %s", messagesSended));
        System.out.println(String.format("Number of sent bytes %s", bytesSended));

    }

    private void stopAndWait(int messageBlock, int fileSize) throws Exception {
        byte[] fileData = reafFileData();
        DataOutputStream stream = new DataOutputStream(clientSocket.getOutputStream());
        DataInputStream reciveAck = new DataInputStream(clientSocket.getInputStream());
        int messagesSended = 1;
        int bytesSended = 0;
        int bufferIdx = messageBlock;
        long start = System.currentTimeMillis();
        while (bytesSended < fileSize) {

            byte[] batchMessage = Arrays.copyOfRange(fileData, bufferIdx - messageBlock, bufferIdx);
            int ack=-1;
            do {
                System.out.println(String.format("Sending %s bytes of data", messageBlock));
                stream.write(batchMessage);
                bytesSended += messageBlock;
                System.out.println(String.format("Remaining bytes to be send %s", fileSize - bytesSended));

                // recive Ack
                Thread.sleep(Constants.TIMEOUT);
                ack = Integer.parseInt(messageConverter(reciveMessage()));
            } while(ack!=messagesSended);
            messagesSended++;
            bufferIdx+= messageBlock;
        }
        System.out.println("File was sent succesfully");
        System.out.println(String.format("Time spend %s", System.currentTimeMillis()-start));
    }


}
