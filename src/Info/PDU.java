package Info;

import java.util.*;
import java.io.*;

/**
 *
 * @author Grupo 04
 */
public class PDU implements Serializable {
       
    private int ver;
    private int seg; //default 0, sem segurança
    private int label;
    private int type;
    
    private int nCampos;
    private int nBytes;
    private List<Fields> campos;  

    public PDU(int ver, int seg, int label, int type, int nCampos, int nBytes, List<Fields> campos) {
        this.ver = ver;
        this.seg = seg;
        this.label = label;
        this.type = type;
        this.nCampos = nCampos;
        this.nBytes = nBytes;
        this.campos = campos;
    }

    public PDU(int type, int nCampos, int nBytes, List<Fields> campos) {
        this.ver = 0;
        this.seg = 0;
        this.label = 0;
        this.type = type;
        this.nCampos = nCampos;
        this.nBytes = nBytes;   
        this.campos = campos;
    }
    
    public PDU(int label, int type, int nCampos, int nBytes, List<Fields> campos) {
        this.ver = 0;
        this.seg = 0;
        this.label = label;
        this.type = type;
        this.nCampos = nCampos;
        this.nBytes = nBytes;   
        this.campos = campos;
    }
    
    
    public int getVer() { return ver; }
    public void setVer(int ver) { this.ver = ver; }

    public int getSeg() { return seg; }
    public void setSeg(int seg) { this.seg = seg; }

    public int getLabel() { return label; }
    public void setLabel(int label) { this.label = label; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public int getnCampos() { return nCampos; }
    public void setnCampos(int nCampos) { this.nCampos = nCampos; }

    public int getnBytes() { return nBytes; }
    public void setnBytes(int nBytes) { this.nBytes = nBytes; }

    public List<Fields> getCampos() { return campos; }
    public void setCampos(List<Fields> campos) { this.campos = campos; }
    
    @Override   
    public String toString() {
	StringBuilder s = new StringBuilder(" --- Pacote PDU --- \n");
        s.append(" - ver: ").append(this.getVer()).append("\n");
	s.append(" - seg: ").append(this.getSeg()).append("\n");
        s.append(" - label: ").append(this.getLabel()).append("\n");
	s.append(" - type: ").append(this.getType()).append("\n");
	s.append(" - nCampos: ").append(this.getnCampos()).append("\n");
	s.append(" - nBytes: ").append(this.getnBytes()).append("\n");

        for(Fields entry : this.campos){
            s.append("Campo: ").append(entry.getId());
            s.append(" - ").append(entry.getInfo()).append("\n");
        }
           
        return s.toString();
    }   
}
