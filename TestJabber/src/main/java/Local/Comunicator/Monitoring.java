package Local.Comunicator;

import Local.Configuration.MainConfig;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Monitoring {
    Socket socket;
    MainConfig config;

    public Monitoring(MainConfig config) throws IOException {
        this.config = config;
        socket = new Socket(config.getServiceIP(), 3030);
    }

    public void start() throws IOException {
        socket.getOutputStream().write(config.getUpdateTime());
    }

    public Info monitor() throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        double cpuUsage = in.readDouble();
        long freeMemory = in.readLong();

        return new Info(cpuUsage, freeMemory);
    }

    public class Info {
        public double cpuUsage;
        public long freeMemory;

        public Info(double cpuUsage, long freeMemory) {
            this.cpuUsage = cpuUsage;
            this.freeMemory = freeMemory;
        }
    }
}
