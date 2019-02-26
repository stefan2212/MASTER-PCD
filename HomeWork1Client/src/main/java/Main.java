import strategy.Client;
import strategy.TcpClient;
import strategy.UdpClient;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Client client = new UdpClient();
        try {
            ((UdpClient) client).connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Client client = new TcpClient();
//        try {
//            ((TcpClient) client).connect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
