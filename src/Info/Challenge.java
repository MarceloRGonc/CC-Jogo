package Info;

import java.io.Serializable;
import java.util.*;
import java.util.Map.*;

/**
 *
 * @author Grupo 04
 */

public class Challenge implements Serializable{
    private String dono;
    private String nome;
    private String data;
    private String hora;
    private String mDir;
    private String iDir;
    private Map<Integer,String> music;
    private Map<Integer,String> image;
    private Map<Integer,Question> question;
    private Map<String,Integer> jogadores;    
    private Map<String,Integer> scores;
        
    public Challenge(String dono, String nome, String data, String hora, String mDir, String iDir, 
                HashMap<Integer, String> music, HashMap<Integer, String> image, 
                HashMap<Integer, Question> question, HashMap<String, Integer> jogadores) {
        this.dono = dono;
        this.nome = nome;
        this.data = data;
        this.hora = hora;
        this.mDir = mDir;
        this.iDir = iDir;
        this.music = music;
        this.image = image; 
        this.question = question;
        this.jogadores = jogadores;
        this.scores = new HashMap<>();
    }
    
    public Challenge(Challenge f) {
        this.dono = f.getDono();
        this.nome = f.getNome();
        this.data = f.getData();
        this.hora = f.getHora();
        this.mDir = f.getMDir();
        this.iDir = f.getIDir();
        this.music = f.getMusic();
        this.image = f.getImage(); 
        this.question = f.getQuestion();
        this.jogadores = f.getJogadores();
        //this.scores = f.getScores();
    }

    public Challenge(String nome, String data, String hora) {
        this.nome = nome; 
        this.data = data;
        this.hora = hora;
    }
    
    public Challenge() {
        this.dono = null; this.nome = null; this.data = null;
        this.hora = null; this.mDir = null; this.iDir = null;
        this.music = new HashMap<>();
        this.image = new HashMap<>();
        this.question = new HashMap<>();
        this.jogadores = new HashMap<>();
        this.scores = new HashMap<>();
    }
    
    public String getDono() { return this.dono; }
    public String getNome() { return this.nome; }
    public String getData() { return this.data; }
    public String getHora() { return this.hora; }
    public String getMDir() { return this.mDir; }
    public String getIDir() { return this.iDir; }
    public Map<Integer, String> getMusic() { return this.music; }
    public Map<Integer, String> getImage() { return this.image; }
    public Map<Integer, Question> getQuestion() { return this.question; }
    public Map<String, Integer> getJogadores() { return this.jogadores; }
    
    boolean flag = false;
    int count = 0;
    boolean getFlag(){ return flag;}
    public Map<String, Integer> getScores() {
        if(this.scores.size() == this.jogadores.size()){
            count++;
            if (count == this.jogadores.size()){ flag = true;}
            return this.scores; 
        }
        else return null;
    }
    
    public void setDono(String dono) { this.dono = dono; }
    public void setNome(String nome) { this.nome = nome; }
    public void setData(String data) { this.data = data; }
    public void setHora(String hora) { this.hora = hora; }
    public void setmDir(String mDir) { this.mDir = mDir; }
    public void setiDir(String iDir) { this.iDir = iDir; }
    public void setMusic(Map<Integer, String> music) { this.music = music; }
    public void setImage(Map<Integer, String> image) { this.image = image; }
    public void setQuestion(Map<Integer, Question> question) { this.question = question; }
    public void setJogadores(Map<String, Integer> jogadores) { this.jogadores = jogadores; }
    int i = 2;
    public void insertPlayer(String u) { jogadores.put(u, i++); }
    void deletePlayer(User u) { jogadores.remove(u.getAlcunha());}
    public void insertScore(String user, int score) { this.scores.put(user, score); }

    @Override
    public Challenge clone() { return new Challenge(this); }

    @Override   
    public String toString() {
	StringBuilder s = new StringBuilder("\n --- Challenge --- \n");
        s.append(" - Nome: ").append(this.getNome()).append("\n");
        s.append(" - Data: ").append(this.getData()).append("\n");
        s.append(" - Hora: ").append(this.getHora()).append("\n");
        s.append(" - Directoria das Músicas: ").append(this.getMDir()).append("\n");
        s.append(" - Directoria das Imagens: ").append(this.getIDir()).append("\n");
        
        for(Entry<Integer,String> entry : this.music.entrySet()){
            s.append("Número da pergunta: ").append(entry.getKey()).append("\n");
            s.append("Musica: ").append(entry.getValue()).append("\n");
        }
        for(Entry<Integer,String> entry : this.image.entrySet()){
            s.append("Número da pergunta: ").append(entry.getKey()).append("\n");
            s.append("Imagem: ").append(entry.getValue()).append("\n");
        }
         for(Entry<Integer,Question> entry : this.question.entrySet()){
            s.append("Número da pergunta: ").append(entry.getKey()).append("\n");
            s.append("Pergunta: ").append(entry.getValue().getQuestion()).append("\n");
            for(Entry<Integer,String> q : entry.getValue().getOptions().entrySet()){
                s.append("Número da opção: ").append(q.getKey()).append("\n");
                s.append("Resposta: ").append(q.getValue()).append("\n");
            }
        }
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
	if ((o == null) || (o.getClass() != this.getClass()))
            return false;
	else {
            Challenge f = (Challenge) o;
            return
                this.getNome().equals(f.getNome()) &&
                this.getData().equals(f.getData()) &&
                this.getHora().equals(f.getHora()); 
	}
    }
}
