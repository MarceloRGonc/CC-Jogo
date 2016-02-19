
package Info;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 *
 * @author Grupo 04
 */
class receiveServerHandler extends Thread{
    public PrintWriter writer;    
    public ObjectInputStream reader;
    private final Socket server;
    private final Manager m;
    
    public receiveServerHandler(Socket newServer, Manager m) {
        this.m = m;
        this.server = newServer;
        this.writer = null;    
        this.reader = null;
    }
    
    @Override
    public void run() {
        try{
            serve();
        } catch (IOException e) {
            System.out.println("Ocorreu um erro na execução da Thread - run");
        }
    }
    
    private void serve() throws IOException {
        boolean flag = true;
        do{
            try {
                this.reader = getSocketReader();
                this.writer = getSocketWriter();
                
                @SuppressWarnings("unchecked")
                PDU p = (PDU) this.reader.readObject();
                if(p == null){ break; }

                List<Fields> campos = p.getCampos();
                switch (p.getType()) {
                    case 6:
                        pEnd(campos);
                        break;
                    case 7:
                        getLChallenges(campos);
                        break;
                    case 9:
                        getChallenge(campos);
                        break;
                    case 13:
                        getRanking(campos);
                        break;
                    case 14:
                        receiveInfo(campos);
                        break;
                }
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("Ocorreu um erro na execução da Thread - serve");
                flag = false;
            }
        }while(flag);
        if(reader != null) reader.close();
        if(writer != null) writer.close();
    }
    
    private void sendPacket(PDU p) throws IOException {
        ObjectOutputStream w = getSocketOWriter();
        w.writeObject(p);
        w.flush();
    }
    
    private ObjectInputStream getSocketReader() throws IOException {
        return new ObjectInputStream(this.server.getInputStream());
    }
    
    private PrintWriter getSocketWriter() throws IOException {
        return new PrintWriter(new OutputStreamWriter(this.server.getOutputStream()), true);
    }
    
    private ObjectOutputStream getSocketOWriter() throws IOException {
        return new ObjectOutputStream(this.server.getOutputStream());
    }

    private void getLChallenges(List<Fields> campos) throws IOException {
        HashMap<String, Challenge> desafios = this.m.getMyChallenges();
        
        ArrayList<Fields> list = new ArrayList<>();
        list.add(0, new Fields(0, "LIST"));
        int i = 1;
        for(Map.Entry<String,Challenge> entry : desafios.entrySet()){
            list.add(i, new Fields(7, entry.getKey()));
            list.add(i+1, new Fields(4, entry.getValue().getData()));
            list.add(i+2, new Fields(5, entry.getValue().getHora()));
            i += 3;
        }  

        PDU reply = new PDU(14,list.size(),list.size(),list);
        sendPacket(reply);
    }
    
    /* Regista jogador e envia o jogo */
    private void getChallenge(List<Fields> campos) throws IOException {
        String des = campos.get(campos.indexOf(new Fields(7,""))).getInfo();
        String al = campos.get(campos.indexOf(new Fields(2,""))).getInfo();

        this.m.registerPlayer(al, des);
        Challenge aux = null;
        if(this.m.getMyChallenges().containsKey(des)){
            aux = this.m.getMyChallenges().get(des).clone();
        }

        ArrayList<Fields> list = new ArrayList<>();
        list.add(0, new Fields(0, "ACCEPT"));

        PDU reply = new PDU(14,list.size(),list.size(),list);
        sendPacket(reply);
              
        ObjectOutputStream w = getSocketOWriter();
        w.writeObject(aux);
        w.flush(); 
    }
    
    private void getRanking(List<Fields> campos) throws IOException {
        HashMap<String, Integer> ranking = this.m.getMyRanking();
        
        ArrayList<Fields> list = new ArrayList<>();
        list.add(0, new Fields(0, "RANKING"));
        int i = 1;
        for(Map.Entry<String, Integer> entry : ranking.entrySet()){
            list.add(i, new Fields(2, entry.getKey()));
            list.add(i+1, new Fields(20, String.valueOf(entry.getValue())));
            i += 2;
        }  

        PDU reply = new PDU(14,list.size(),list.size(),list);
        sendPacket(reply);
    }
    
    private void pEnd(List<Fields> campos) {
        String desafio = campos.get(campos.indexOf(new Fields(7,""))).getInfo();
        HashMap<String, Integer> scores = null;

        while(scores == null){
            scores = (HashMap<String, Integer>)this.m.getChallengesR().get(desafio).getScores();
            if( scores == null ) {
                try { ClientHandler.sleep(500); }
                catch (InterruptedException ex) { 
                    System.out.println("Thread interrompida!");
                } 
            }
        }
        ArrayList<Fields> list = new ArrayList<>();
        list.add(0, new Fields(0, "END"));
        int i = 1;
        for(Map.Entry<String,Integer> entry : scores.entrySet()){
            list.add(i, new Fields(1, entry.getKey()));
            list.add(i+1, new Fields(20, String.valueOf(entry.getValue())));
            i += 2;
        } 
        try{
            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(14,list.size(),r.length,list);
            sendPacket(reply);
        } catch (IOException ex) {
            System.out.println("Ocorreu um erro a enviar o pacote END!");
        }
        this.m.endChallenge(desafio);
    }

    private void receiveInfo(List<Fields> campos) {
        String i = campos.get(campos.indexOf(new Fields(0,""))).getInfo();
        switch (i) {
            case "SCORE":
                score(campos);
                break;
            case "END":
                end(campos);
                break;
            case "LIST":
                list(campos);
                break;
            case "RANKING":
                rank(campos);
                break;
            case "ACCEPT":
                accept(campos);
                break;
        }
    }   
    
    private void rank(List<Fields> campos) {
        HashMap<String,Integer> r = new HashMap<>();
        int i = 1;
        while(i < campos.size()){
            String al = campos.get(i).getInfo();
            int sc = Integer.valueOf(campos.get(i+1).getInfo());
            r.put(al,sc);
            i += 2;
        }  
        this.m.setRankingS(r);
    }

    private void list(List<Fields> campos) {
        HashMap<String,Challenge> r = new HashMap<>();
        int i = 1;
        while(i < campos.size()){
            String challenge = campos.get(i).getInfo();
            String date = campos.get(i+1).getInfo();
            String hour = campos.get(i+2).getInfo();
            Challenge a = new Challenge(challenge,date,hour);
            r.put(challenge,a);
            i += 3;
        }  
        this.m.setListS(r);
    }
    
    /* Recebe o jogo */
    private void accept(List<Fields> campos) {
        try{
            this.reader = getSocketReader();
            Challenge p = (Challenge)this.reader.readObject();
            this.m.setChallengeS(p);
        } catch (IOException | ClassNotFoundException ex) {
            Challenge p = null;
            this.m.setChallengeS(p);
            System.out.println("Ocorreu um erro na execução da Thread - accept");
        }
    }

    private void score(List<Fields> campos) {
        String des = campos.get(campos.indexOf(new Fields(7,""))).getInfo();
        String al = campos.get(campos.indexOf(new Fields(2,""))).getInfo();
        String score = campos.get(campos.indexOf(new Fields(20,""))).getInfo();
        this.m.insertSChallenge(des,al,Integer.valueOf(score));
    }

    private void end(List<Fields> campos) {
        int i = 1;
        HashMap<String, Integer> scores = new HashMap<>();
        while(i < campos.size()){
            scores.put(campos.get(i).getInfo(),Integer.valueOf(campos.get(i+1).getInfo()));
            i+=2;
        }
        this.m.setEndS(scores);
    }    
}
