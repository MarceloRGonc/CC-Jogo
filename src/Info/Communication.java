
package Info;

import java.io.*;
import java.net.*;

/**
 *
 * @author Grupo 04
 */
public class Communication {
  
    public int udpPORT;
    private final DatagramSocket server;
    private InetAddress IPAddress;
    
    public Communication(int udpPort, InetAddress IPaddress) throws SocketException {
        this.udpPORT = udpPort;
        this.server = new DatagramSocket();
        this.IPAddress = IPaddress; 
    }
    
    public void sendPDU(PDU p) throws IOException {
        byte[] data = Communication.serializa(p);
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, udpPORT);
        server.send(sendPacket);
    }
    
    public PDU receivePDU() throws IOException {
        byte[] incomingData = new byte[49000];
        DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);     
        server.receive(incomingPacket);
        
        IPAddress = incomingPacket.getAddress();
        udpPORT = incomingPacket.getPort();
        
        try{
            Object obj = Communication.desSerializa(incomingPacket.getData());
                
            if(obj.getClass().getName().equals(PDU.class.getName()) == true) {
                return (PDU)obj;
            } else return null;
        } catch (ClassNotFoundException e) { return null; }       
    }
    
    public static byte[] serializa(Object o) throws IOException{
	ByteArrayOutputStream baos = new ByteArrayOutputStream();						
        ObjectOutputStream oos = new ObjectOutputStream(baos);                   
        oos.writeObject(o);
	return baos.toByteArray();
    }
	   
    public static Object desSerializa(byte[] ab) throws IOException, ClassNotFoundException{		
        ByteArrayInputStream bais = new ByteArrayInputStream(ab);
	ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    } 

    public void setSoTimeout(int i) throws SocketException {
            this.server.setSoTimeout(i);
    }
}
