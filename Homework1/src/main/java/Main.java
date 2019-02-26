import strategy.Server;
import strategy.TcpServer;
import strategy.UdpServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args)  {

        Server server = null;
        try {
            server = new UdpServer();
            ((UdpServer) server).reciveFiles();
        } catch (Exception e) {
            System.out.println("Socket hass been closed");
        }

//        try {
//            Server server = new TcpServer();
//            ((TcpServer) server).accept();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
