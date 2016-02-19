
package Info;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Aguarda pela conexão de novos servidores
 * @author Grupo 04
 */
public class waitServers extends Thread {
    private Manager m;
    private ServerSocket server;
    private Socket newServer = null;
    private ArrayList<Socket> connectServers;
    public static int tcpPORT = 1723;
    public static ObjectOutputStream writer = null;    

    public waitServers(Manager m){ 
        this.m = m; 
        this.connectServers = new ArrayList<>();
//        try {
//            this.server = new ServerSocket(tcpPORT);
//        } catch (IOException ex) {
//            System.out.println("Ocorreu um erro na execução da Thread - waitServers");
//        }
    }
    @Override
    public void run() {
        boolean flag = true;
        while(flag){
            try {
                //this.newServer = this.server.accept();
                this.newServer = new Socket("168.192.1.3", waitServers.tcpPORT);
                System.out.println("Estou conectado!");
                this.connectServers.add(this.newServer);
                receiveServerHandler t1 = new receiveServerHandler(this.newServer,m);
                sendServerHandler t2 = new sendServerHandler(this.newServer,m);
                this.m.addServers(t2);
                t1.start();
                flag = false;
            } catch (IOException ex) {
                if(this.newServer != null) flag = false;
                //System.out.println("Ocorreu um erro na execução da Thread - ws run");
            }
        }   
    }
        
    private static BufferedReader getSocketReader(Socket s) throws IOException {
        return new BufferedReader(new InputStreamReader(s.getInputStream()));
    }
    
    private static ObjectInputStream getSocketOReader(Socket s) throws IOException {
        return new ObjectInputStream(s.getInputStream());
    }
    
    private static ObjectOutputStream getSocketWriter(Socket s) throws IOException {
        return new ObjectOutputStream(s.getOutputStream());
    }
    
    public void diffuseServer(Socket newServer) throws IOException {
        for(Socket e : connectServers){
            ArrayList<Fields> list = new ArrayList<>();
            list.add(new Fields(30, newServer.getInetAddress().getHostAddress()));
            list.add(new Fields(31, String.valueOf(newServer.getLocalPort()))); 
            PDU reply = new PDU(14,2,list.size(),list);
            sendPacket(reply,e);
        }
    }

    private static void sendPacket(PDU p, Socket s) throws IOException {
        writer = null;
        writer = getSocketWriter(s);
        writer.writeObject(p);
        writer.flush();
    }
    
    public void addServer(Socket n){
       this.connectServers.add(n);
    }
}
