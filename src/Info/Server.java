package Info;

import java.io.*;
import java.net.*;
import java.util.HashMap;

/**
 *
 * @author 
 */
public class Server {
    
    public static int tcpPORT = 9999;
    public static int udpPORT = 9876;
    private Manager m;

    public Server() {
    }
    public static void main(String[] args) {
        try {
            new Server().start();
        } catch (IOException ex) {
            System.out.println("IOException!\n");
        }
    }
    
    public void start() throws IOException {
        this.m = new Manager();
        
//        User u = new User("Marcelo", "Marcelo", "123", 55);
//        this.m.registerU(u);
//        u = new User("Ricardo", "Ricardo", "123", 53);
//        this.m.registerU(u);
//        u = new User("Miguel", "Miguel", "123", 55);
//        this.m.registerU(u);
        User u = new User("Joao", "Joao", "123", 35);
        this.m.registerU(u);
        u = new User("Rui", "Rui", "123", 50);
        this.m.registerU(u);
        u = new User("Marco", "Marco", "123", 47);
        this.m.registerU(u);
        HashMap<String, Integer> r = new HashMap<>();
//        r.put("Marcelo",55);
//        r.put("Ricardo",53);
        //r.put("Miguel",55);
        r.put("Joao",35);
        r.put("Rui",50);
        r.put("Marco",47);
        this.m.setMyRanking(r);
        
        waitServers server = new waitServers(m);
        server.start();
        DatagramSocket udpSocket = new DatagramSocket(udpPORT);
        byte[] incomingData = new byte[1024];
        int i = 1;
        try {
            while(true){
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                udpSocket.receive(incomingPacket);
                
                byte[] data = incomingPacket.getData();
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                ObjectInputStream is = new ObjectInputStream(in);
                
                try {
                    PDU receive = (PDU) is.readObject();
                    System.out.println("HELLO packet received = " + receive.toString());
                } catch (ClassNotFoundException e) { System.out.println("Object not recognised!"); }
                
                InetAddress IPAddress = incomingPacket.getAddress();
                int port = incomingPacket.getPort();
                
                ClientHandler thread = new ClientHandler(IPAddress, port, m, udpPORT+i);
                thread.start();
                i++;
            }
        } finally { udpSocket.close(); }
    }
}