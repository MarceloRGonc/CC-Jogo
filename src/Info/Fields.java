package Info;

import java.io.*;

/**
 *
 * @author Grupo 04
 */
public class Fields implements Serializable {
    private int id;
    private String info;
    private byte[] file;
    
    public Fields(int i, String info) { this.id = i; this.info = info; }
    
    public Fields(int i, String info, byte[] file) { this.id = i; this.info = info; this.file = file; }

    public Fields(int i) { this.id = i; this.info = ""; }
    
    public Fields(Fields i) { this.id = i.getId(); this.info = i.getInfo(); this.file = i.getFile();}

    public int getId() { return id; }
    public String getInfo() { return info; }

    public void setId(int id) { this.id = id; }
    public void setInfo(String info) { this.info = info; }

    public byte[] getFile() { return file; }
    public void setFile(byte[] file) { this.file = file; }
    
    @Override
    public Fields clone() { return new Fields(this); }

    @Override   
    public String toString() {
	StringBuilder s = new StringBuilder(" --- Fields --- \n");
        s.append(" - Id: ").append(this.getId()).append("\n");
        s.append(" - Info: ").append(this.getInfo()).append("\n");
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
	if ((o == null) || (o.getClass() != this.getClass()))
            return false;
	else {
            Fields c = (Fields) o;
            return this.getId() == c.getId(); 
	}
    }
    
}
