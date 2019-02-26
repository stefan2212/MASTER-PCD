package strategy;

import utils.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.*;
import java.nio.file.Files;
import java.util.Arrays;

import static utils.Constants.LENGTH_SIZE;

@SuppressWarnings("ALL")
public class UdpClient implements Client {

    DatagramSocket socket;
    InetAddress IPAddress;

    public byte[] reafFileData() {
        File file;
        try {
            return Files.readAllBytes(Constants.FILE_PATH);
        } catch (IOException e) {
            System.out.println("Could not read data from file");
        }
        return null;
    }

    public UdpClient() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        IPAddress = InetAddress.getByName(Constants.IP_ADDRESS);
    }

    public void connect() throws Exception {
        byte[] sendData;
        byte[] receivedData;
        String firstMessage = "hello";
        //sending message dimension
        DatagramPacket sendPacket = new DatagramPacket(toByteArray(firstMessage.length()), Constants.LENGTH_SIZE, IPAddress, Constants.PORT);
        socket.send(sendPacket);
        //sending the actual message
        sendData = firstMessage.getBytes();
        sendPacket = new DatagramPacket(sendData, firstMessage.length(), IPAddress, Constants.PORT);
        socket.send(sendPacket);
        int packetSize = reciveMaxMessageSize();
        streamSendFile(packetSize, Constants.FILE_SIZE);
        socket.close();
    }

    private int reciveMaxMessageSize() throws IOException {
        byte[] receivedData = new byte[4];
        DatagramPacket recivePacket = new DatagramPacket(receivedData, LENGTH_SIZE);
        socket.receive(recivePacket);
        int packetSize = getPacketSize(recivePacket.getData());
        return packetSize;
    }

    private Integer getPacketSize(byte[] packet) {
        return new BigInteger(packet).intValue();
    }

    private void streamSendFile(int messageBlock, int fileSize) throws  IOException {
        byte[] fileData = reafFileData();
        int messagesSended = 0;
        int bytesSended = 0;
        int bufferIdx = messageBlock;
        long start = System.currentTimeMillis();
        while (bytesSended < fileSize) {
            System.out.println(String.format("Sending %s bytes of data", messageBlock));
            byte[] batchMessage = Arrays.copyOfRange(fileData, bufferIdx - messageBlock, bufferIdx);
            DatagramPacket sendPacket = new DatagramPacket(batchMessage, batchMessage.length, IPAddress, Constants.PORT);
            socket.send(sendPacket);
            messagesSended++;
            bytesSended += messageBlock;
            System.out.println(String.format("Remaining bytes to be send %s", fileSize - bytesSended));
        }
        System.out.println("Files was sent");
        System.out.println(String.format("Time spend %s", System.currentTimeMillis()-start));
        System.out.println(String.format("Number of sent messages %s", messagesSended));
        System.out.println(String.format("Number of sent bytes %s", bytesSended));

    }


    private void stopAndWait(int messageBlock, int fileSize) throws Exception {
        byte[] fileData = reafFileData();
        int messagesSended = 1;
        int bytesSended = 0;
        int bufferIdx = messageBlock;
        long start = System.currentTimeMillis();
        int ack=-1;
        while (bytesSended < fileSize) {

            byte[] batchMessage = Arrays.copyOfRange(fileData, bufferIdx - messageBlock, bufferIdx);

            do {
                System.out.println(String.format("Sending %s bytes of data", messageBlock));
                DatagramPacket sendPacket = new DatagramPacket(batchMessage, batchMessage.length, IPAddress, Constants.PORT);
                socket.send(sendPacket);
                bytesSended += messageBlock;
                System.out.println(String.format("Remaining bytes to be send %s", fileSize - bytesSended));
                // recive Ack
                byte[] receivedData = new byte[4];
                DatagramPacket recivePacket = new DatagramPacket(receivedData, LENGTH_SIZE);
                socket.receive(recivePacket);
                ack = getPacketSize(recivePacket.getData());
                System.out.println(String.format("Recived ack", ack));
                Thread.sleep(Constants.TIMEOUT);
            } while(ack!=messagesSended);
            messagesSended++;
            bufferIdx+=messageBlock;
        }
        System.out.println("File was sent succesfully");
        System.out.println(String.format("Time spend %s", System.currentTimeMillis()-start));
    }

    private byte[] toByteArray(int value) {
        byte[] result = new byte[Integer.BYTES];
        for (int index = Integer.BYTES - 1; index >= 0; index--) {
            result[index] = (byte) value;
            value = value >> 8;
        }
        return result;
    }

    private String messageConverter(byte[] message) {
        return new String(message);
    }
}
