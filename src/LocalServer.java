package src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class LocalServer extends Thread {
    private int numpackets;

    public LocalServer(int numpackets) {
        this.numpackets = numpackets;
    }

    public void run() {
        int port = 10001; // usual UDP echo port is 7
        int seed = 1000; // milliseconds
        double losschance = 0.2;
        try (DatagramSocket socket = new DatagramSocket(port)) {
            for (int i = 1; i <= numpackets; i++) {
                byte[] buf = new byte[i * 2 * 1024];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                socket.receive(dp);

                // get ip address
                int j = 4;
                StringBuilder ipAddress = new StringBuilder();
                for (byte raw : dp.getAddress().getAddress()) {
                    ipAddress.append(raw & 0xFF);
                    if (--j > 0)
                        ipAddress.append(".");
                }

                if (Math.random() > losschance) {
                    int timewait = (int) (Math.random() * seed);
                    Thread.sleep(timewait);
                    socket.send(dp);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}