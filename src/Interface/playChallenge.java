
package Interface;

import Info.Communication;
import Info.Fields;
import Info.PDU;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.*;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;
import javax.imageio.ImageIO;

/**
 *
 * @author Grupo 04
 */
class playChallenge {
    private String desafio;
    private String username;
    private String question;
    private Communication s;
    private HashMap<Integer,PDU> pacotes;
    
    public playChallenge(String desafio, String username, String question, Communication s) { 
        this.desafio = desafio; 
        this.username = username; 
        this.s = s;
        this.pacotes = new HashMap<>();
    }
    
    private int maior(Object[] a){
        int max = (int) a[0];
        for(int i = 1; i < a.length; i++){
            if(((int)a[i]) > max){
                max = (int) a[i];
            } 
        }
        return max;
    }
    
    public void prepareQuestion() throws SocketException {
        receivePDUs();
        boolean flag = true;
        int t;
        while( flag ) {
            PDU receive = null;
            t = maior(this.pacotes.keySet().toArray());
            if(this.pacotes.containsKey(t)){
                if(this.pacotes.get(t).getCampos().get(this.pacotes.get(t).getCampos().indexOf(new Fields(254,""))).getInfo().equals("1")) {
                    flag = false;
                }
                else{ 
                    boolean f = true;
                    while(f){
                        receive = askPDU(t+1,Integer.valueOf(this.question));
                        if(receive != null) f = false;
                    } 
                }
            }
            else{
                boolean f = true;
                    while(f){
                        receive = askPDU(t,Integer.valueOf(this.question));
                        if(receive != null) f = false;
                    } 
            }  
            if(flag){
                ArrayList<Fields> campos = (ArrayList<Fields>)receive.getCampos();
                int bloco = Integer.valueOf(campos.get(campos.indexOf(new Fields(17,""))).getInfo());
                this.pacotes.put(bloco,receive); 
            }
        }
        t = maior(this.pacotes.keySet().toArray());
        int i = 0;
        while( i < t ) {
            if(!pacotes.containsKey(i)){
                flag = true;
                PDU receive = null;
                while(flag){
                    receive = askPDU(i,Integer.valueOf(this.question));
                    if(receive != null) flag = false;
                } 
                ArrayList<Fields> campos = (ArrayList<Fields>)receive.getCampos();
                int bloco = Integer.valueOf(campos.get(campos.indexOf(new Fields(17,""))).getInfo());
                this.pacotes.put(bloco,receive); 
            }
            i++;
        }
        confirmPDUs();
        
        ArrayList<Fields> campos = (ArrayList<Fields>)this.pacotes.get(0).getCampos();
        this.desafio = campos.get(0).getInfo();
        this.question = campos.get(1).getInfo();
        String pergunta = campos.get(2).getInfo();
        HashMap<Integer, String> respostas = new HashMap<>();
        
        respostas.put(1,campos.get(4).getInfo());
        respostas.put(2,campos.get(6).getInfo());    
        respostas.put(3,campos.get(8).getInfo());
        
        ArrayList<Byte> file = new ArrayList<>();         
        i = 1;
        flag = true;
        while(flag){
            campos = (ArrayList<Fields>)this.pacotes.get(i).getCampos();
            boolean a = campos.get(campos.indexOf(new Fields(16,""))).getInfo().equals("Imagem");

            if(a){
                Fields aux = campos.get(campos.indexOf(new Fields(16,"")));
                for(byte e : aux.getFile()){
                    file.add(e);
                }
                i++;
            }
            else{ flag = false; }
        } 
        int n = file.size();
        byte[] imagem = new byte[n];
        for (int z = 0; z < n; z++) { imagem[z] = file.get(z); }
        
        BufferedImage img = null;
        try {
            img = ImageIO.read(new ByteArrayInputStream(imagem));
        } catch (IOException ex) { System.out.println("Erro ao converter imagem!"); }
        
        file = new ArrayList<>();         
        flag = true;
        while(flag){
            try{
                campos = (ArrayList<Fields>)this.pacotes.get(i).getCampos();
                boolean a = campos.get(campos.indexOf(new Fields(18,""))).getInfo().equals("Musica");

                if(a){
                    Fields aux = campos.get(campos.indexOf(new Fields(18,"")));
                    for(byte e : aux.getFile()){
                        file.add(e);
                    }
                    i++;
                }
                else{ flag = false; }
            } catch (NullPointerException ex) { flag = false;  }
        } 
        n = file.size();
        byte[] audio = new byte[n];
        for (int z = 0; z < n; z++) { audio[z] = file.get(z); }

        File someFile = new File("Musica.mp3");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(someFile);
            fos.write(audio);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }

        Question nq = new Question(this.username, this.desafio, this.question, pergunta, respostas, img, someFile, this.s);
        nq.setVisible(true);
    }
    
    private void receivePDUs() throws SocketException {
        boolean flag = true;
        int bloco = 0;
        while(flag){
            try {
                PDU receive = this.s.receivePDU();
                if(bloco == 0) this.s.setSoTimeout(1000);
                List<Fields> campos = receive.getCampos();
                int cont = Integer.valueOf(campos.get(campos.indexOf(new Fields(254,""))).getInfo());
                bloco = Integer.valueOf(campos.get(campos.indexOf(new Fields(17,""))).getInfo());
                this.question = campos.get(campos.indexOf(new Fields(10,""))).getInfo();
                this.pacotes.put(bloco, receive);
                if(cont == 1) flag = false;
                
            } catch (SocketTimeoutException ex) {
                flag = false;
            }
            catch (IOException ex) {
                System.out.println("Erro na recepção dos pacotes!");
            }
        }
        this.s.setSoTimeout(0); 
    } 
    
    private PDU askPDU(int bloco, int q) throws SocketException {
        PDU receive = null;
        try { 
            this.s.setSoTimeout(1000);
            ArrayList<Fields> list = new ArrayList<>();
            list.add(new Fields(7, this.desafio));
            list.add(new Fields(10, String.valueOf(q)));
            list.add(new Fields(17, String.valueOf(bloco)));

            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(12,list.size(),r.length,list);
            this.s.sendPDU(reply);
            receive = this.s.receivePDU();
        } catch (SocketTimeoutException ex) { 
                receive = null;
        } 
        catch (IOException ex) { System.out.println("Erro ao pedir pacote em falta!"); }
        this.s.setSoTimeout(0); 
        return receive;
    } 
    
    private void confirmPDUs() {
        try {
            ArrayList<Fields> list = new ArrayList<>();
            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(0,list.size(),r.length,list);
            this.s.sendPDU(reply);
            this.s.sendPDU(reply);
            this.s.sendPDU(reply);
        } catch (IOException ex) { System.out.println("Erro ao confirmar a recepção de todos os pacotes!"); }
    }     
}
            