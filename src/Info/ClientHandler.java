package Info;

import java.io.*;
import java.net.*;
import java.time.*;
import static java.time.LocalDateTime.*;
import java.util.Map.*;
import java.time.format.*;
import java.util.*;

/**
 *
 * @author Grupo 04
 */
class ClientHandler extends Thread {
    public int udpPORT;
    private DatagramSocket client;
    private final Manager dM;
    private final InetAddress IPAddress;
    private final int port;
    private User u;
    private int id;
    private int score;
    
    public ClientHandler(InetAddress IPAddress, int port, Manager m, int udpPORT) throws SocketException{
        this.udpPORT = udpPORT;
        this.client = new DatagramSocket(udpPORT);
        this.dM = m;
        this.IPAddress = IPAddress;
        this.port = port;
        this.u = null;
    }
    
    @Override
    public void run() {
        try{
            serve();
        } catch (IOException e) {
            System.out.println("Ocorreu um erro na execução da Thread - ch run");
        }
    }
    
    private void serve() throws IOException {
        ArrayList<Fields> list = new ArrayList<>();
        list.add(new Fields(0, "OK"));
        list.add(new Fields(30, String.valueOf(udpPORT))); 
        byte[] r = Communication.serializa(list);
        PDU reply = new PDU(0,2,r.length,list);
        sendPDU(reply);
        
        do{
            try {
                PDU receive = receivePDU();
                
                List<Fields> campos = receive.getCampos();
                switch (receive.getType()) {
                    case 2:
                        register(campos);
                        break;
                    case 3:
                        autenticate(campos);
                        break;
                    case 4:
                        logout(campos);
                        break;   
                    case 7:
                        listChallenge(campos);
                        break;    
                    case 8:
                        makeChallenge(campos);
                        break; 
                    case 9:
                        acceptChallenge(campos);
                        break; 
                    case 10:
                        deleteChallenge(campos);
                        break; 
                    case 13:
                        Ranking(campos);
                        break; 
                }
            } catch (IOException ex) {
                System.out.println("Ocorreu um erro na execução da Thread - ch serve");
            }
        }while(true);
    }
    
    private void register(List<Fields> campos) throws IOException {
	String nome = campos.get(campos.indexOf(new Fields(1,""))).getInfo();
        String alcunha = campos.get(campos.indexOf(new Fields(2,""))).getInfo();
	String pw = campos.get(campos.indexOf(new Fields(3,""))).getInfo();

        boolean existe = this.dM.registerUser(nome, alcunha, pw);
					
        if(existe) {
            ArrayList<Fields> list = new ArrayList<>();
            list.add(new Fields(0, "OK"));
            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(0,1,r.length,list);
            sendPDU(reply);
        }
        else {
            ArrayList<Fields> list = new ArrayList<>();
            list.add(new Fields(0, "NO"));
            list.add(new Fields(255, "Utilizador já existe!"));
            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(0,22,r.length,list);
            sendPDU(reply);
        }
    }
    
    private void autenticate(List<Fields> campos) throws IOException {
        String alcunha = campos.get(campos.indexOf(new Fields(2,""))).getInfo();
	String pw = campos.get(campos.indexOf(new Fields(3,""))).getInfo();
	
        String nome;
        
	boolean inSession = false;
	if(dM.getUsers().containsKey(alcunha)){
            inSession = dM.getUsers().get(alcunha).isAtivo();
        }
	boolean existe = dM.validateUser(alcunha, pw);
						
        if(existe && !inSession) {
            ArrayList<Fields> list = new ArrayList<>();
            list.add(new Fields(0, "OK"));
            list.add(new Fields(2, alcunha));
            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(0,2,r.length,list);
            sendPDU(reply);
            u = dM.getUsers().get(alcunha);
        }
        else {
            ArrayList<Fields> list = new ArrayList<>();
            list.add(new Fields(0, "NO"));
            list.add(new Fields(255, "Não foi possível efetuar o login!"));
            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(0,2,r.length,list);
            sendPDU(reply);
        }    
    }

    private void logout(List<Fields> campos) throws IOException {        
        String alcunha = campos.get(campos.indexOf(new Fields(2,""))).getInfo();
        dM.logout(alcunha);
        ArrayList<Fields> list = new ArrayList<>();
        list.add(new Fields(0, "OK"));
        byte[] r = Communication.serializa(list);
        PDU reply = new PDU(0,1,r.length,list);
        sendPDU(reply);   
    }
    
    private void makeChallenge(List<Fields> campos) throws IOException {
        String nome = campos.get(campos.indexOf(new Fields(7,""))).getInfo();
	String data = campos.get(campos.indexOf(new Fields(4,""))).getInfo();
	String hora = campos.get(campos.indexOf(new Fields(5,""))).getInfo();

        Challenge read = readFile(nome,data,hora);
        read.insertPlayer(u.getAlcunha());
        boolean existe = this.dM.registerChallenge(nome, read);
        
	if(existe){
            LocalDateTime agora = now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyMMdd");
            LocalDate d = LocalDate.parse(data, format);

            format = DateTimeFormatter.ofPattern("HHmmss");
            LocalTime h = LocalTime.parse(hora, format);

            LocalDateTime comeca = of(d,h);
            long a = agora.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long c = comeca.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            try {
                if((c-a) > 0){
                    ArrayList<Fields> list = new ArrayList<>();
                    list.add(new Fields(0, "OK"));
                    list.add(new Fields(7, nome));
                    list.add(new Fields(4, data));
                    list.add(new Fields(5, hora));
                    byte[] r = Communication.serializa(list);
                    PDU reply = new PDU(0,4,r.length,list);
                    sendPDU(reply);
                    ClientHandler.sleep(c-a);
                    beginChallenge(nome,1);
                }
                else {
                    this.dM.deleteChallenge(nome,u.getAlcunha());
                    ArrayList<Fields> list = new ArrayList<>();
                    list.add(new Fields(0, "NO"));
                    list.add(new Fields(255, "Não foi possível registar o desafio!"));
                    byte[] r = Communication.serializa(list);
                    PDU reply = new PDU(0,2,r.length,list);
                    sendPDU(reply);
                }   
            } catch (InterruptedException ex) {
                System.out.println("Thread interrompida!");
            }
        }
        else {
            ArrayList<Fields> list = new ArrayList<>();
            list.add(new Fields(0, "NO"));
            list.add(new Fields(255, "Não foi possível registar o desafio!"));
            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(0,2,r.length,list);
            sendPDU(reply);
        }    
    }
    
    Challenge challenge;
    boolean out;
    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }
    public void beginChallenge(String desafio, int u) {
        this.dM.beginChallenge(desafio);
        
        if(u == 1 && !out ) {
            challenge = this.dM.getChallengesR().get(desafio);
        } 
        
        HashMap<Integer,PDU> pacotes = new HashMap<>();
        
        int np = u;
        
        this.id = 0;

        ArrayList<Fields> list = new ArrayList<>();
        list.add(0,new Fields(7, challenge.getNome()));
        list.add(1,new Fields(10, String.valueOf(np)));
        list.add(2,new Fields(11, challenge.getQuestion().get(np-1).getQuestion()));
        list.add(3,new Fields(12, "1"));
        list.add(4,new Fields(13, challenge.getQuestion().get(np-1).getOptions().get(1)));
        list.add(5,new Fields(12, "2"));
        list.add(6,new Fields(13, challenge.getQuestion().get(np-1).getOptions().get(2)));
        list.add(7,new Fields(12, "3"));
        list.add(8,new Fields(13, challenge.getQuestion().get(np-1).getOptions().get(3)));
        list.add(9,new Fields(17, String.valueOf(this.id)));
        list.add(10,new Fields(254,"0"));

        byte[] r;
        try { 
            r = Communication.serializa(list);
            pacotes.put(id,new PDU(0,list.size(),r.length,list));
        }
        catch (IOException ex) { System.out.println("Erro a serializar o pacote!"); }
        this.id++;
        HashMap<Integer,PDU> aux;
        try {
            StringBuilder sp = new StringBuilder();
            sp.append("/Users/Marcelo/NetBeansProjects/[CC] TP2/src/").append(challenge.getIDir()).append("/").append(challenge.getImage().get(np-1));
            aux = getPIMG(sp.toString(),np-1);
            for(Entry<Integer,PDU> entry : aux.entrySet()){
                pacotes.put(entry.getKey(),entry.getValue());
            }
        } catch (IOException ex) { System.out.println("Erro no tratamento da imagem!"); }

        try {
            StringBuilder sp = new StringBuilder();
            sp.append("/Users/Marcelo/NetBeansProjects/[CC] TP2/src/").append(challenge.getMDir()).append("/").append(challenge.getMusic().get(np-1));
            aux = getPMUS(sp.toString(),np-1);
            for(Entry<Integer,PDU> entry : aux.entrySet()){
                pacotes.put(entry.getKey(),entry.getValue());
            }
        } catch (IOException ex) { System.out.println("Erro no tratamento da musica!"); }
        sendPDUs(pacotes);
        servePlay(desafio,u);
    }
    
    public HashMap<Integer,PDU> getPIMG(String iDir, int nr) throws IOException {  
        File myFile = new File (iDir); 
        HashMap<Integer,PDU> pacotes = new HashMap<>();
        byte [] mybytearray  = new byte [(int)myFile.length()];  
        
        FileInputStream fis = new FileInputStream(myFile);
        fis.read(mybytearray);
        
        byte [] aux; int i = 0;
        int p = 0;
        while(i < mybytearray.length){
            aux = java.util.Arrays.copyOfRange(mybytearray, i, i+48000);
            
            ArrayList<Fields> list = new ArrayList<>();
            list.add(new Fields(10, String.valueOf(nr)));
            list.add(new Fields(16, "Imagem",aux));
            list.add(new Fields(17, String.valueOf(this.id)));
            list.add(new Fields(254,"0"));
        
            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(0,list.size(),r.length,list);
            pacotes.put(id,reply);
            this.id++;
            
            i += 48000;
        }     
        return pacotes;
    }  

    public HashMap<Integer,PDU> getPMUS(String dir, int nr) throws IOException {
        File file = new File(dir);
       
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum); 
            }
        } catch (IOException ex) {
        }
        byte[] bytes = bos.toByteArray();
 
        HashMap<Integer,PDU> pacotes = new HashMap<>();
        byte [] aux; int i = 0;
        while(i < bytes.length){
            aux = java.util.Arrays.copyOfRange(bytes, i, i+48000);
            
            ArrayList<Fields> list = new ArrayList<>();
            list.add(new Fields(10, String.valueOf(nr)));
            if(i==0) list.add(new Fields(16, "", aux));
            list.add(new Fields(18, "Musica", aux));
            list.add(new Fields(17, String.valueOf(this.id)));
            
            if( i+48000 < bytes.length) { list.add(new Fields(254,"0")); }
            else{ list.add(new Fields(254,"1")); }
        
            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(0,list.size(),r.length,list);
            pacotes.put(id,reply);
            this.id++;
            i += 48000;
        } 
        
        return pacotes;
    }

    private void sendPDUs(HashMap<Integer,PDU> pacotes) {
        for(PDU p : pacotes.values()){
            try { sendPDU(p); }
            catch (IOException ex) { System.out.println("Erro a enviar um pacote!"); }
        }
        boolean f = true;
        while(f){
            try {
                PDU receive = receivePDU();
                List<Fields> campos = receive.getCampos();
                
                switch (receive.getType()) {
                    case 0:
                        f = false;
                        break;
                    case 12:
                        String desafio = campos.get(campos.indexOf(new Fields(7,""))).getInfo();
                        int quest = Integer.valueOf(campos.get(campos.indexOf(new Fields(10,""))).getInfo());
                        int bloco = Integer.valueOf(campos.get(campos.indexOf(new Fields(17,""))).getInfo());
                        sendPDU(pacotes.get(bloco));
                        break;
                }
            } catch (IOException ex) { System.out.println("Erro ao confirmar pacotes recebidos!"); }
        }
    }  
    
    boolean flag;
    private void servePlay(String desafio, int n) {
       this.flag = true;
       do{
            try {
                PDU receive = receivePDU();
                List<Fields> campos = receive.getCampos();
                switch (receive.getType()) {
                    case 4: //Logout - desitiu sai do programa
                        String alcunha = campos.get(campos.indexOf(new Fields(2,""))).getInfo();
                        this.dM.insertScoreChallenge(desafio, alcunha, 0);
                        this.dM.logout(alcunha);
                        ArrayList<Fields> list = new ArrayList<>();
                        list.add(new Fields(0, "OK"));
                        byte[] r = Communication.serializa(list);
                        PDU reply = new PDU(0,list.size(),r.length,list);
                        sendPDU(reply);  
                        break;
                    case 5: //Quit - desistiu volta ao menu
                        alcunha = campos.get(campos.indexOf(new Fields(2,""))).getInfo();
                        this.dM.insertScoreChallenge(desafio, alcunha, 0);
                        list = new ArrayList<>();
                        list.add(new Fields(0, "OK"));
                        r = Communication.serializa(list);
                        reply = new PDU(0,list.size(),r.length,list);
                        sendPDU(reply);
                        break;
                    case 6: //End - finalizar o desafio
                        pEnd(campos);
                        this.flag = false;
                        break;   
                    case 11: //Resposta
                        pAnswer(campos);
                        if( n != 10 ) beginChallenge(desafio, n+1);
                        else servePlay(desafio,n);
                        break;    
                }
            } catch (IOException ex) {
                System.out.println("Ocorreu um erro na execução do jogo!");
            }
        } while(flag);
    }
    
    private void acceptChallenge(List<Fields> campos) throws IOException {
        String nome = campos.get(campos.indexOf(new Fields(7,""))).getInfo();

        boolean existe = this.dM.registerPlayer(u.getAlcunha(), nome);
        boolean nserver = false;
        if(!existe) {
            nserver = this.dM.registerPlayerS(u.getAlcunha(), nome);
        }
        
	if(existe){
            ArrayList<Fields> list = new ArrayList<>();
            list.add(new Fields(0, "OK"));
            list.add(new Fields(7, nome));
            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(0,1,r.length,list);

            LocalDateTime agora = now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyMMdd");
            LocalDate d = LocalDate.parse(this.dM.getChallenges().get(nome).getData(), format);

            format = DateTimeFormatter.ofPattern("HHmmss");
            LocalTime h = LocalTime.parse(this.dM.getChallenges().get(nome).getHora(), format);

            LocalDateTime comeca = of(d,h);
            long a = agora.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long c = comeca.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            try {
                if((c-a) > 0){
                    sendPDU(reply);
                    ClientHandler.sleep(c-a);
                    beginChallenge(nome,1);
                }
                else {
                    this.dM.deletePlayer(u.getAlcunha(), nome);
                    list = new ArrayList<>();
                    list.add(new Fields(0, "NO"));
                    list.add(new Fields(255, "O desafio não pode ser aceite!"));
                    r = Communication.serializa(list);
                    reply = new PDU(0,2,r.length,list);
                    sendPDU(reply);
                }   
            } catch (InterruptedException ex) {
                System.out.println("Thread interrompida!");
            }
        }else if(nserver){
            ArrayList<Fields> list = new ArrayList<>();
            list.add(new Fields(0, "OK"));
            list.add(new Fields(7, nome));
            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(0,1,r.length,list);

            this.challenge = this.dM.getChallenge().clone();
            out = true;
            
            LocalDateTime agora = now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyMMdd");
            LocalDate d = LocalDate.parse(this.challenge.getData(), format);

            format = DateTimeFormatter.ofPattern("HHmmss");
            LocalTime h = LocalTime.parse(this.challenge.getHora(), format);

            LocalDateTime comeca = of(d,h);
            long a = agora.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long c = comeca.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            try {
                if((c-a) > 0){
                    sendPDU(reply);
                    ClientHandler.sleep(c-a);
                    beginChallenge(nome,1);
                }
                else {
                    sendPDU(reply);
                    beginChallenge(nome,1);
                }   
            } catch (InterruptedException ex) {
                System.out.println("Thread interrompida!");
            }
        } else {
            ArrayList<Fields> list = new ArrayList<>();
            list.add(new Fields(0, "NO"));
            list.add(new Fields(255, "O desafio não pode ser aceite!"));
            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(0,2,r.length,list);
            sendPDU(reply);
        }    
    }
    
    private void listChallenge(List<Fields> campos) throws IOException {
        HashMap<String, Challenge> desafios = this.dM.getChallenges();
        
	if(!desafios.isEmpty()){
            ArrayList<Fields> list = new ArrayList<>();
            list.add(0, new Fields(0, "OK"));
            int i = 1;
            for(Map.Entry<String,Challenge> entry : desafios.entrySet()){
                list.add(i, new Fields(7, entry.getKey()));
                list.add(i+1, new Fields(4, entry.getValue().getData()));
                list.add(i+2, new Fields(5, entry.getValue().getHora()));
                i += 3;
            }  
            
            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(0,list.size(),r.length,list);
            sendPDU(reply);
        }
        else {
            ArrayList<Fields> list = new ArrayList<>();
            list.add(new Fields(0, "NO"));
            list.add(new Fields(255, "Ocorreu um erro a listar os desafios!"));
            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(0,list.size(),r.length,list);
            sendPDU(reply);
        }    
    }
    
    private void deleteChallenge(List<Fields> campos) throws IOException {
        String nome = campos.get(campos.indexOf(new Fields(7,""))).getInfo();
        
        HashMap<String, Challenge> desafios = this.dM.getChallenges();
        
	if(!desafios.isEmpty()){
            ArrayList<Fields> list = new ArrayList<>();
            list.add(0, new Fields(0, "OK"));
            int i = 1;
            for(Map.Entry<String,Challenge> entry : desafios.entrySet()){
                list.add(i, new Fields(7, entry.getKey()));
                list.add(i+1, new Fields(4, entry.getValue().getData()));
                list.add(i+2, new Fields(5, entry.getValue().getHora()));
                i += 3;
            }  
            
            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(0,list.size(),r.length,list);
            sendPDU(reply);
        }
        else {
            ArrayList<Fields> list = new ArrayList<>();
            list.add(new Fields(0, "NO"));
            list.add(new Fields(255, "Ocorreu um erro a listar os desafios!"));
            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(0,list.size(),r.length,list);
            sendPDU(reply);
        }    
    }
    
    public Challenge readFile(String nome, String data, String hora){
        String nameFile = "../[CC] TP2/desafio-000001.txt";
        Challenge aux = null;
        HashMap<Integer,String> music = new HashMap<>();
        HashMap<Integer,String> image = new HashMap<>();
        HashMap<Integer,Question> question = new HashMap<>();
        
        try{
            FileReader ficheiro = new FileReader(nameFile);
            BufferedReader parser = new BufferedReader(ficheiro); 
            String a, mDir, iDir;            

            a = parser.readLine();
            String[] tokens = a.split("=");
            mDir = tokens[1];
            
            a = parser.readLine();
            tokens = a.split("=");
            iDir = tokens[1];
            
            parser.readLine();

            int i = 0;
            while( (a = parser.readLine()) != null ){
                tokens = a.split(",");
                music.put(i,tokens[0]);
                image.put(i,tokens[1]);
                HashMap<Integer,String> options = new HashMap<>();
                options.put(1,tokens[3]);
                options.put(2,tokens[4]);
                options.put(3,tokens[5]);
                Question r = new Question(tokens[2],options,Integer.parseInt(tokens[6]));
                question.put(i,r);
                i++;
            }
            HashMap<String, Integer> j = new HashMap<>();
            j.put(u.getAlcunha(), 1);
            aux = new Challenge(u.getAlcunha(),nome,data,hora,mDir,iDir,music,image,question,j);
        }catch (IOException e){
            System.out.println("Erro na leitura do ficheiro ...");
        }
        return aux;
    }

    private void sendPDU(PDU p) throws IOException {
        byte[] data = Communication.serializa(p);
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, this.IPAddress, this.port);
        this.client.send(sendPacket);
        System.out.println("Packet send!!");
    }
    
    private PDU receivePDU() throws IOException {
        byte[] incomingData = new byte[1024];
        DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);     
        this.client.receive(incomingPacket);
        
        try{
            Object obj = Communication.desSerializa(incomingPacket.getData());
                
            if(obj.getClass().getName().equals(PDU.class.getName()) == true) {
                System.out.println("Packet receive!!");
                return (PDU)obj;
            } else return null;
        } catch (ClassNotFoundException e) { return null; } 
    }

    private void pEnd(List<Fields> campos) {
	String desafio = campos.get(campos.indexOf(new Fields(7,""))).getInfo();
        HashMap<String, Integer> scores = null;

        while(scores == null){

            if(!out){
                scores = (HashMap<String, Integer>)this.dM.getChallengesR().get(desafio).getScores();
                if( scores == null ) {
                    try { ClientHandler.sleep(500); }
                    catch (InterruptedException ex) { 
                        System.out.println("Thread interrompida!");
                    } 
                }
            } else{
                scores = this.dM.pEnd(desafio);
            } 
        }
        ArrayList<Fields> list = new ArrayList<>();
        list.add(0, new Fields(0, "OK"));
        int i = 1;
        for(Entry<String,Integer> entry : scores.entrySet()){
            list.add(i, new Fields(1, entry.getKey()));
            list.add(i+1, new Fields(20, String.valueOf(entry.getValue())));
            i += 2;
        } 
        try{
            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(0,list.size(),r.length,list);
            sendPDU(reply);
        } catch (IOException ex) {
            System.out.println("Ocorreu um erro a enviar o pacote END!");
        }
        if(!out) this.dM.endChallenge(desafio);
    }

    private void pAnswer(List<Fields> campos) {
        int escolha = Integer.valueOf(campos.get(campos.indexOf(new Fields(6,""))).getInfo());
	String desafio = campos.get(campos.indexOf(new Fields(7,""))).getInfo();
	int questao = Integer.valueOf(campos.get(campos.indexOf(new Fields(10,""))).getInfo());
        int resp;
        if(!out) {
           resp = this.dM.getChallengesR().get(desafio).getQuestion().get(questao-1).getAnswer();
        } else {
           resp =this.challenge.getQuestion().get(questao-1).getAnswer();
        }
        
        ArrayList<Fields> list = new ArrayList<>();
        if( resp == escolha ){ 
            list.add(0, new Fields(14, "OK"));
            list.add(0, new Fields(15, "2"));
            this.score+=2; 
        }
        else { 
            list.add(0, new Fields(14, "NO"));
            list.add(0, new Fields(15, "-1"));
            this.score-=1; 
        }

        if((questao) == 10){ 
            if(!out){
                this.dM.insertScoreChallenge(desafio, this.u.getAlcunha(), this.score);
            } else{
                this.dM.insertScoreChallengeS(desafio, this.u.getAlcunha(), score);
            } 
        }
        
        try{
            byte[] r = Communication.serializa(list);
            PDU reply = new PDU(0,list.size(),r.length,list);
            sendPDU(reply);
        } catch (IOException ex) {
            System.out.println("Ocorreu um erro a enviar o resultado da resposta!");
        }
    }

    private void Ranking(List<Fields> campos) throws IOException {
        String op = campos.get(campos.indexOf(new Fields(0,""))).getInfo();
        
        switch (op) {
            case "LOCAL":{
                HashMap<String, Integer> rank = this.dM.getMyRanking();
                if(!rank.isEmpty()){
                    ArrayList<Fields> list = new ArrayList<>();
                    list.add(0, new Fields(0, "OK"));
                    int i = 1;  

                    for(Map.Entry<String,Integer> entry : rank.entrySet()){
                        list.add(i, new Fields(7, entry.getKey()));
                        list.add(i+1, new Fields(20, String.valueOf(entry.getValue())));
                        i += 2;
                    }

                    byte[] r = Communication.serializa(list);
                    PDU reply = new PDU(0,list.size(),r.length,list);
                    sendPDU(reply);
                }
                else {
                    ArrayList<Fields> list = new ArrayList<>();
                    list.add(new Fields(0, "NO"));
                    list.add(new Fields(255, "Ocorreu um erro a listar os desafios!"));
                    byte[] r = Communication.serializa(list);
                    PDU reply = new PDU(0,list.size(),r.length,list);
                    sendPDU(reply);
                }       break;
            }
            case "GLOBAL": {
                HashMap<String, Integer> rank = this.dM.getRanking();
                if(!rank.isEmpty()){
                    ArrayList<Fields> list = new ArrayList<>();
                    list.add(0, new Fields(0, "OK"));
                    int i = 1;
                    
                    for(Map.Entry<String,Integer> entry : rank.entrySet()){
                        list.add(i, new Fields(7, entry.getKey()));
                        list.add(i+1, new Fields(20, String.valueOf(entry.getValue())));
                        i += 2;
                    }
                    
                    byte[] r = Communication.serializa(list);
                    PDU reply = new PDU(0,list.size(),r.length,list);
                    sendPDU(reply);
                }
                else {
                    ArrayList<Fields> list = new ArrayList<>();
                    list.add(new Fields(0, "NO"));
                    list.add(new Fields(255, "Ocorreu um erro a listar os desafios!"));
                    byte[] r = Communication.serializa(list);
                    PDU reply = new PDU(0,list.size(),r.length,list);
                    sendPDU(reply);
                }       break;
            }
            default:
                ArrayList<Fields> list = new ArrayList<>();
                list.add(new Fields(0, "NO"));
                list.add(new Fields(255, "Ocorreu um erro a listar os desafios!"));
                byte[] r = Communication.serializa(list);
                PDU reply = new PDU(0,list.size(),r.length,list);
                sendPDU(reply);
                break;    
        }
    }	
}
