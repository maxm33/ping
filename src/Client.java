package src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Random;

/*
 *** non-local test ***
 * 
 * java src.Client 52.43.121.77:10001 10
 * 
 * it is an udp echo server
 */

public class Client {
    private static Integer port;
    private static int timeout = 3000; // milliseconds
    private static DatagramSocket socket;

    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            if (args.length != 2)
                throw new IndexOutOfBoundsException();
            String[] result = args[0].split(":");
            InetAddress address = InetAddress.getByName(result[0]);
            port = Integer.parseInt(result[1]);
            if (port < 0 || port > 65535)
                throw new NumberFormatException();
            int numpackets = Integer.parseInt(args[1]);

            socket = new DatagramSocket();
            socket.setSoTimeout(timeout);

            if (address.isLoopbackAddress()) {
                System.out.println("\nStarting local server on port " + port + "...");
                LocalServer server = new LocalServer(port, numpackets);
                server.start();
                ping(address, numpackets);
                server.join();
            } else
                ping(address, numpackets);
            socket.close();
        } catch (UnknownHostException e) {
            System.err.println("Error - Host provided could not be found.");
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Usage: java src.Client <hostname>:<port> <#packets>");
        } catch (NumberFormatException e) {
            if (port == null)
                System.err.println("Error - Port is an integer in range 0-65535.");
            else
                System.err.println("Error - Second argument must be an integer.");
        }
    }

    private static void ping(InetAddress address, int numpackets) throws IOException {
        long timeStart = 0, timeEnd = 0;
        int countreceived = 0, RTT = 0, RTTcumulative = 0, minRTT = 9999, maxRTT = 0;
        Random rd = new Random();

        System.out.println("\n");
        for (int i = 1; i <= numpackets; i++) {
            byte[] buf = new byte[i * 1024];
            rd.nextBytes(buf);
            DatagramPacket dp = new DatagramPacket(buf, buf.length, address, port);
            socket.send(dp);

            timeStart = System.currentTimeMillis();
            try {
                dp = new DatagramPacket(buf, buf.length);
                socket.receive(dp);
                countreceived++;
                timeEnd = System.currentTimeMillis();
                RTT = (int) (timeEnd - timeStart);
                RTTcumulative += RTT;
                if (RTT > maxRTT)
                    maxRTT = RTT;
                if (RTT < minRTT)
                    minRTT = RTT;
                System.out.println(
                        "UDP - " + buf.length + " bytes from " + address.getHostName() + ": RTT=" + RTT + "ms");
            } catch (SocketTimeoutException so) {
                System.err.println("TimeoutException: failed ping attempt");
            }
        }
        if (countreceived == 0) {
            System.err.println("Server is unreachable.");
            return;
        }
        float averageRTT = RTTcumulative / countreceived;
        float losspercent = (float) (numpackets - countreceived) / numpackets;
        losspercent *= 100;
        losspercent = Math.round(losspercent);
        System.out.println("\n\n---- " + address.getHostName() + " ping statistics ----\n" + numpackets
                + " packets transmitted, " + countreceived + " packets received, " + losspercent
                + "% packet loss\nround-trip min/avg/max = " + minRTT + "/" + (int) averageRTT + "/" + maxRTT
                + " ms\n");
    }
}