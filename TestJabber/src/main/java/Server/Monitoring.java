package Server;

import Common.Commands;
import com.sun.management.OperatingSystemMXBean;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Monitoring {
    private static Logger log = Logger.getLogger(Monitoring.class.getName());

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(3030, 1);
            while (true) {
                Socket clientSocket = server.accept();
                log.info("Connect new client");
                monitor(clientSocket);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Socket error", e);
        }
    }

    private static void monitor(Socket socket) throws IOException {
        InputStream in;
        DataOutputStream out;
        try {
            in = socket.getInputStream();
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Create stream error", e);
            socket.close();
            return;
        }

        while (true) {
            if (in.available() > 0) {
                in.read();
                OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
                        OperatingSystemMXBean.class);

                double cpuUsage = osBean.getSystemCpuLoad();
                long freeMemory = osBean.getFreePhysicalMemorySize();

                out.writeDouble(cpuUsage);
                out.writeLong(freeMemory);
            }
        }
    }
}
