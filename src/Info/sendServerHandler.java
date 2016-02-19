
package Info;

import java.io.*;
import java.net.*;

/**
 *
 * @author Grupo 04
 */
class sendServerHandler{
    public static ObjectOutputStream writer = null;    
    public static ObjectInputStream reader = null;
    private Socket client = null;
    private Manager m;
    
    public sendServerHandler(Socket newServer, Manager m) {
        this.m = m;
        this.client = newServer;
    }
    
    private BufferedReader getSocketReader() throws IOException {
        return new BufferedReader(new InputStreamReader(client.getInputStream()));
    }
    
    private ObjectInputStream getSocketOReader() throws IOException {
        return new ObjectInputStream(client.getInputStream());
    }
    
    private ObjectOutputStream getSocketWriter() throws IOException {
        return new ObjectOutputStream(client.getOutputStream());
    }
    
    public void sendPacket(PDU p) throws IOException {
        writer = null;
        writer = getSocketWriter();
        writer.writeObject(p);
        writer.flush();
    }
    
}
