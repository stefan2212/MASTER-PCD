package strategy;

import com.sun.tools.internal.jxc.ap.Const;
import utils.Constants;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import static utils.Constants.LENGTH_SIZE;

@SuppressWarnings("Duplicates")
public class UdpServer implements Server {

    private static final Integer MAX_MESSAGE_SIZE = 65000;

    private DatagramSocket socket;

    public UdpServer() throws SocketException {
        socket = new DatagramSocket(Constants.PORT);
    }

    public void reciveFiles() throws Exception {
        reciveMessage();
    }


    private void reciveMessage() throws Exception {
        byte[] receivedData = new byte[4];
        byte[] sendData;
        //recive length from server
        DatagramPacket recivePacket = new DatagramPacket(receivedData, LENGTH_SIZE);
        socket.receive(recivePacket);
        int packetSize = getPacketSize(recivePacket.getData());
        receivedData = new byte[packetSize];
        recivePacket = new DatagramPacket(receivedData, packetSize);
        socket.receive(recivePacket);
        DatagramPacket sendPacket = new DatagramPacket(toByteArray(MAX_MESSAGE_SIZE), Constants.LENGTH_SIZE, recivePacket.getAddress(), recivePacket.getPort());
        socket.send(sendPacket);
        reciveStream();
    }

    private void reciveStream() throws IOException {
        int messagesRecived = 0;
        int bytesRecived = 0;
        BufferedWriter br = new BufferedWriter(new FileWriter("testfile", true));
        byte[] receivedData = new byte[MAX_MESSAGE_SIZE];
        while (bytesRecived < Constants.FILE_SIZE) {
            byte[] chunckMessage = new byte[MAX_MESSAGE_SIZE];
            DatagramPacket recivePacket = new DatagramPacket(chunckMessage, LENGTH_SIZE);
            socket.receive(recivePacket);
            System.out.println(String.format("Recived bytes %s", MAX_MESSAGE_SIZE));
            bytesRecived += MAX_MESSAGE_SIZE;
            br.write(messageConverter(recivePacket.getData()));
        }
        br.close();
        System.out.println("File was recived");

    }

    private void stopAndWait() throws IOException, InterruptedException {
        int ack = 0;
        int bytesRecived = 0;
        BufferedWriter br = new BufferedWriter(new FileWriter("testfile", true));
        while (bytesRecived < Constants.FILE_SIZE) {
            byte[] chunckMessage = new byte[MAX_MESSAGE_SIZE];
            System.out.println(String.format("Recived bytes %s", MAX_MESSAGE_SIZE));
            DatagramPacket recivePacket = new DatagramPacket(chunckMessage, LENGTH_SIZE);
            socket.receive(recivePacket);
            chunckMessage = recivePacket.getData();
            bytesRecived+= MAX_MESSAGE_SIZE;
            br.write(messageConverter(chunckMessage));
            //Send ack to client
            ack++;
            System.out.println("Sending ack");

            DatagramPacket sendPacket = new DatagramPacket(toByteArray(ack), Constants.LENGTH_SIZE, recivePacket.getAddress(), recivePacket.getPort());
            socket.send(sendPacket);
            Thread.sleep(Constants.TIMEOUT);

        }
        br.close();
        socket.close();
        System.out.println("File was recived");

    }


    private Integer getPacketSize(byte[] packet) {
        return new BigInteger(packet).intValue();
    }

    private String messageConverter(byte[] message) {
        return new String(message);
    }

    private byte[] toByteArray(int value) {
        byte[] result = new byte[Integer.BYTES];
        for (int index = Integer.BYTES - 1; index >= 0; index--) {
            result[index] = (byte) value;
            value = value >> 8;
        }
        return result;
    }
}
