package Info;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.*;

/**
 *
 * @author Grupo 04
 */

public class Manager implements Serializable {
    private sendServerHandler sServers;
    private final HashMap<String, User> users;
    private final HashMap<String, Challenge> desafios;
    private final HashMap<String, Integer> ranking;
    private final HashMap<String, Challenge> desDecorrer;
    private Condition cc; 
    private Condition dd; 
    private Condition rr; 
    private Condition ee; 

    private Lock lock;

    private Challenge c;
    private HashMap<String, Challenge> d;
    private HashMap<String, Integer> r;
    private HashMap<String, Integer> e;

    public Manager() {		
        this.users = new HashMap<>();
        this.desafios = new HashMap<>();
        this.ranking = new HashMap<>();
        this.desDecorrer = new HashMap<>();
        this.d = new HashMap<>();
        this.c = null;
        this.r = new HashMap<>();
        this.sServers = null;
        this.lock = new ReentrantLock();
        this.cc = lock.newCondition();
        this.dd = lock.newCondition();
        this.rr = lock.newCondition();
        this.ee = lock.newCondition();
    }

    public void addServers(sendServerHandler a) { 
        lock.lock();
	try{ 
            this.sServers = a; 
        } finally { 
            lock.unlock(); 
        }        
    }
    
    public void sendPDUServer(PDU p) throws IOException {
        lock.lock();
	try{ 
            this.sServers.sendPacket(p); 
        } finally { 
            lock.unlock(); 
        }   
    }
    
    /* Retorna um Map com os utilizadores existentes no sistema */
    public HashMap<String, User> getUsers() { 
        HashMap<String,User> r = new HashMap<>();
        for(Map.Entry<String,User> entry : users.entrySet()){
            r.put(entry.getKey(),entry.getValue());
        }  
        return r; 
    }
    
    /* Retorna um Map com os desfios existentes no sistema */
    public Challenge getChallenge() {  return this.c; }
    public HashMap<String, Challenge> getMyChallenges() { return this.desafios; }
    
    public HashMap<String, Challenge> getChallenges() {
        lock.lock();	

        try {
            if(sServers != null){
                ArrayList<Fields> l = new ArrayList<>();
                PDU a = new PDU(7,l.size(),l.size(),l);
                try {   
                    this.sServers.sendPacket(a);
                    awaitPD();
                } catch (IOException | InterruptedException ex) {
                    System.out.println("Ocorreu um erro a contactar o outro servidor!");
                }
            }

            HashMap<String, Challenge> aux = new HashMap<>();
            aux.putAll(this.desafios);
            aux.putAll(this.d);

            return aux; 
        } finally { lock.unlock(); }
    }
    
    public HashMap<String, Challenge> getChallengesR() {  return this.desDecorrer; }
        
    /* Retorna um Map com o ranking dos users existentes no sistema */
    public HashMap<String, Integer> getMyRanking() { return this.ranking; }
    
    public void setMyRanking(HashMap<String, Integer> e) {
        for(Map.Entry<String,Integer> entry : e.entrySet()){
            this.ranking.put(entry.getKey(),entry.getValue());
        }
    }

    public HashMap<String, Integer> getRanking() {
        lock.lock();	

         try {
            if(sServers != null){
                ArrayList<Fields> l = new ArrayList<>();
                PDU a = new PDU(13,l.size(),l.size(),l);
                try {   
                    this.sServers.sendPacket(a);
                    awaitPR();
                } catch (IOException | InterruptedException ex) {
                    System.out.println("Ocorreu um erro a contactar o outro servidor!");
                }
            }
        
            HashMap<String, Integer> aux = new HashMap<>();
            aux.putAll(this.ranking);
            aux.putAll(this.r);
            return aux; 
        } finally { lock.unlock(); }
    }
    
    /* Regista um novo utilizador no sistema */
    public boolean registerUser(String nome, String alcunha, String pass) {
        boolean res = false;
	if(alcunha.equals("") || (pass.equals(""))) return res;
        
        lock.lock();
	try {
            if (!this.users.containsKey(alcunha)) {
                User u = new User(nome, alcunha, pass);
		this.users.put(alcunha, u);
                res = true;
            }
        } finally { lock.unlock(); }
        return res;
    }
    
    /* Regista um novo utilizador no sistema */
    public boolean registerU(User r) {
        boolean res = false;
	if(r.getAlcunha().equals("") || (r.getPassword().equals(""))) return res;
        
        lock.lock();
	try {
            if (!this.users.containsKey(r.getAlcunha())) {
		this.users.put(r.getAlcunha(), r);
                res = true;
            }
        } finally { lock.unlock(); }
        return res;
    }
    
    /* Regista um novo desafio no sistema */
    public boolean registerChallenge(String nome, Challenge novo) {
        boolean res = false;
	if(nome.equals("")) return res;
        
        lock.lock();
	try {
            if (!this.desafios.containsKey(nome)) {
		this.desafios.put(nome, novo);
                res = true;
            }
        } finally { lock.unlock(); }
        return res;
    }

    /* Valida a existencia e os dados de um Utilizador */
    public boolean validateUser(String alcunha, String pass) {
        boolean res = false;
	
        lock.lock();	
        try {
            if (this.users.containsKey(alcunha)) {
                if (this.users.get(alcunha).getPassword().equals(pass)) {
                    this.users.get(alcunha).setAtivo(true);
                    res = true;
		}
            }
        } finally { lock.unlock(); }
	return res;
    }

    /* Termina a sessão de um utilizador */
    public void logout(String alcunha) {	
        lock.lock();
	try {
            this.users.get(alcunha).setAtivo(false);
	} finally { lock.unlock(); }
    }	

    /* Regista um novo utilizador no jogo */
    public boolean registerPlayer(String alcunha, String jogo) {
        boolean res = false;
	if(alcunha.equals("")) return res;
        
        lock.lock();
	try {
            if (this.desafios.containsKey(jogo)) {
                if(!this.desafios.get(jogo).getJogadores().containsKey(alcunha)){
                    this.desafios.get(jogo).insertPlayer(alcunha);
                    res = true;
                }
            }
        } finally { lock.unlock(); }
        return res;
    }
    
    public boolean deleteChallenge(String des, String alcunha) { 
        boolean res = false;
	
        lock.lock();	
        try {
            if (this.desafios.containsKey(des)) {
                if (this.desafios.get(des).getDono().equals(alcunha)) {
                    this.desafios.remove(des);
                    res = true;
		}
            }
        } finally { lock.unlock(); }
	return res;
    }
    
    public boolean beginChallenge(String des) { 
        boolean res = false;
	
        lock.lock();	
        try {
            if(!this.desDecorrer.containsKey(des)){
                if (this.desafios.containsKey(des)) {
                    this.desDecorrer.put(des,this.desafios.get(des));
                    this.desafios.remove(des);
                    res = true;
                }
            }
        } finally { lock.unlock(); }
	return res;
    }
    
    public void insertSChallenge(String des, String user, int score){	
        lock.lock();	
        try {
            if (this.desDecorrer.containsKey(des)) {
                if (this.desDecorrer.get(des).getJogadores().containsKey(user)) {
                    this.desDecorrer.get(des).insertScore(user,score);
		}
            }
        } finally { lock.unlock(); }
    }
        
    public boolean insertScoreChallenge(String des, String user, int score) { 
        boolean res = false;
        lock.lock();	
        try {
            if (this.desDecorrer.containsKey(des)) {
                if (this.desDecorrer.get(des).getJogadores().containsKey(user)) {
                    this.desDecorrer.get(des).insertScore(user,score);
                    int sc = 0;
                    if(this.ranking.containsKey(user)){
                        sc = this.ranking.get(user);
                    }
                    sc+=score;
                    this.ranking.remove(user);
                    this.ranking.put(user,sc);
                    res = true;
		}
            }
        } finally { lock.unlock(); }
	return res;
    }
    public boolean insertScoreChallengeS(String des, String user, int score) { 
        boolean res = false;
	
        lock.lock();	
        try {
            if (this.sServers != null) {
                try {   
                    ArrayList<Fields> l = new ArrayList<>();
                    l.add(new Fields(0, "SCORE"));
                    l.add(new Fields(7, des));
                    l.add(new Fields(2, user));
                    l.add(new Fields(20, String.valueOf(score)));

                    PDU a = new PDU(14,l.size(),l.size(),l);
                    
                    this.sServers.sendPacket(a);
                    
                    int sc = 0;
                    if(this.ranking.containsKey(user)){
                        sc = this.ranking.get(user);
                    }
                    sc+=score;
                    this.ranking.remove(user);
                    this.ranking.put(user,sc);
                } catch (IOException ex) {
                    System.out.println("Ocorreu um erro a contactar o outro servidor!");
                } 
            }
        } finally { lock.unlock(); }
	return res;
    }
    
    public Map<String,Integer> getScoresChallenge(String des) { 
        Map<String,Integer> res = null;
	
        lock.lock();	
        try {
            if (this.desDecorrer.containsKey(des)) {
                res = this.desDecorrer.get(des).getScores();
            }
        } finally { lock.unlock(); }
	return res;
    }
    
    public boolean endChallenge(String des) { 
        boolean res = false;
	
        lock.lock();	
        try {
            if(this.desDecorrer.containsKey(des) && this.desDecorrer.get(des).getFlag()){
                this.desDecorrer.remove(des);
                res = true;
            }
        } finally { lock.unlock(); }
	return res;
    }
    
    public boolean deletePlayer(String alcunha, String des) {
        boolean res = false;        
        lock.lock();
	try {
            if (this.desafios.containsKey(des)) {
                if(!this.desafios.get(des).getJogadores().containsKey(alcunha)){
                    User u = this.users.get(alcunha);
                    this.desafios.get(des).deletePlayer(u);
                    res = true;
                }
            }
        } finally { lock.unlock(); }
        return res;
    }
    
    public void signalPC(){ this.cc.signalAll(); }
    public void awaitPC() throws InterruptedException { this.cc.await(); }
    
    public void signalPD(){ this.dd.signalAll(); }
    public void awaitPD() throws InterruptedException { this.dd.await(); }
    
    public void signalPR(){ this.rr.signalAll(); }
    public void awaitPR() throws InterruptedException { this.rr.await(); }
    
    public void signalPE(){ this.ee.signalAll(); }
    public void awaitPE() throws InterruptedException { this.ee.await(); }
    
    
    public void setRankingS(HashMap<String, Integer> a) { 
        lock.lock();
	try{ 
            this.r = a; 
            signalPR();
        } finally { 
            lock.unlock(); 
        }
    }
    
    public void setChallengeS(Challenge a) {  
        lock.lock();
	try{ 
            this.c = a; 
            signalPC();
        } finally { 
            lock.unlock(); 
        }
    }
    
    public void setListS(HashMap<String, Challenge> a) {         
        lock.lock();
	try{ 
            this.d = a; 
            signalPD();
        } finally { 
            lock.unlock(); 
        } 
    }
    
    public void setEndS(HashMap<String, Integer> a) {
        lock.lock();
	try{ 
            this.e = a; 
            signalPE();
        } finally { 
            lock.unlock(); 
        }
    }

    /* 
      * Registar utilizador de jogo de outro servidor 
      * Trazer Informações do jogo
    */
    boolean registerPlayerS(String alcunha, String nome) {
        boolean res = false;
	if(alcunha.equals("")) return res;
        
        lock.lock();
	try {
            if(this.sServers != null){
                try {   
                    ArrayList<Fields> l = new ArrayList<>();
                    l.add(new Fields(7, nome));
                    l.add(new Fields(2, alcunha));

                    PDU a = new PDU(9,l.size(),l.size(),l);
                    
                    this.sServers.sendPacket(a);
                    this.c = null;
                    awaitPC();
                    if ((this.c != null)) res = true; 
                } catch (InterruptedException | IOException ex) {
                    System.out.println("Ocorreu um erro a contactar o outro servidor!");
                }
            }                  
        } finally { lock.unlock(); }
        return res;
    }    

    public HashMap<String, Integer> pEnd(String des) {
        HashMap<String, Integer> res = null;
        lock.lock();
	try {
            if(this.sServers != null){
                try {   
                    ArrayList<Fields> l = new ArrayList<>();
                    l.add(new Fields(7, des));
                    PDU a = new PDU(6,l.size(),l.size(),l);
                    this.sServers.sendPacket(a);
                    this.e = null;
                    awaitPE();
                    if ((this.c != null)) res = this.e; 
                } catch (InterruptedException | IOException ex) {
                    System.out.println("Ocorreu um erro a contactar o outro servidor!");
                }
            }                  
        } finally { lock.unlock(); }
        return res;
    }
}