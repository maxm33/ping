package src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class LocalServer extends Thread {
    private int port, numpackets;

    public LocalServer(int port, int numpackets) {
        this.port = port;
        this.numpackets = numpackets;
    }

    public void run() {
        int seed = 1000; // milliseconds
        double losschance = 0.2;
        try (DatagramSocket socket = new DatagramSocket(port)) {
            for (int i = 1; i <= numpackets; i++) {
                byte[] buf = new byte[i * 2 * 1024];
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                socket.receive(dp);

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
