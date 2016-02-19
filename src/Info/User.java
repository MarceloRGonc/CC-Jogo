package Info;

import java.io.Serializable;
import java.net.InetAddress;

/**
 *
 * @author 
 */
public class User implements Serializable {
    private String nome;
    private String alcunha;	
    private String password;
    private int score;
    private boolean ativo;
    
    public User() {
        this.nome = "";
        this.alcunha = "";
        this.password = "";
        this.score = 0;
        this.ativo = false;
    }

    public User(String nome, String nick, String pw) {
        this.nome = nome;
        this.alcunha = nick;
	this.password = pw;
        this.score = 0;
        this.ativo = false;
    }

    public User(String nome, String nick, String pw, int score) {
        this.nome = nome;
        this.alcunha = nick;
	this.password = pw;
        this.score = 0;
        this.ativo = false;
    }
    
    public User(User c) {
        this.nome = c.getNome();
	this.alcunha = c.getAlcunha();
	this.password = c.getPassword();
        this.score = c.getScore();
        this.ativo = c.isAtivo();
    }

    public String getAlcunha() { return this.alcunha; }

    public String getPassword() { return this.password; }
    
    private int getScore() { return this.score; }
    
    public void setScore(int a) { this.score = a; } 
    
    public void incScore(int a) { this.score += a; } 
    
    public String getNome() { return this.nome; }
    
    public boolean isAtivo() { return this.ativo; }
       
    public void setAtivo(boolean a) { this.ativo = a; } 
        
    @Override
    public User clone() { return new User(this); }

    @Override   
    public String toString() {
	StringBuilder s = new StringBuilder(" --- Cliente --- \n");
        s.append(" - Nome: ").append(this.getNome()).append("\n");
        s.append(" - Alcunha: ").append(this.getAlcunha()).append("\n");
	s.append(" - Password ").append(this.getPassword()).append("\n");
        s.append(" - Score ").append(this.getScore()).append("\n");
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
	if ((o == null) || (o.getClass() != this.getClass()))
            return false;
	else {
            User c = (User) o;
            return this.getAlcunha().equals(c.getAlcunha()) 
                   && this.getNome().equals(c.getNome()) 
                   && this.getScore() == c.getScore();
	}
    }
}
