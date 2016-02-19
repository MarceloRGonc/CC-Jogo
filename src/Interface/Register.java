package Interface;

import Info.Communication;
import Info.Fields;
import Info.PDU;
import java.io.*;
import java.util.*;

/**
 * Uma classe que cria uma frame para log in na aplicação.
 *
 * @author Marcelo Gonçalves, Ricardo Silva
 * @version 2015.04.07
 */

public class Register extends javax.swing.JFrame {

    // Variáveis de instância
    private String name;   
    private String username;
    private String password;
    private Communication s;

    public Register(Communication s) {
        this.s = s;
        initComponents();
        this.setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nameT = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        registerButton = new javax.swing.JButton();
        passwordField = new javax.swing.JPasswordField();
        cancelButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        userT = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Log in");
        setResizable(false);

        jLabel1.setText("Nome");

        jLabel2.setText("Password");

        registerButton.setText("Register");
        registerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onClickLogInButton(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtononClickLogInButton(evt);
            }
        });

        jLabel3.setText("Alcunha");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(97, 97, 97)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(userT, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(nameT, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                        .addComponent(passwordField)))
                .addGap(97, 97, 97))
            .addGroup(layout.createSequentialGroup()
                .addGap(136, 136, 136)
                .addComponent(registerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(136, 136, 136))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {nameT, passwordField});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, registerButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameT, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userT, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(registerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(45, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2, nameT, passwordField});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cancelButton, registerButton});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel3, userT});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onClickLogInButton(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onClickLogInButton
        this.name = nameT.getText();
        this.username = userT.getText();
        this.password = new String(passwordField.getPassword());         
                        
        ArrayList<Fields> list = new ArrayList<>();
        list.add(new Fields(1, name));
        list.add(new Fields(2, username));
        list.add(new Fields(3, password));
        byte[] r;
        PDU receive;
        List<Fields> campos = null;
        
        try {
            r = Communication.serializa(list);
            PDU register = new PDU(2,list.size(),r.length,list);
            this.s.sendPDU(register);      
            receive = this.s.receivePDU();
            campos = receive.getCampos();
        } catch (IOException ex) {
                new OkDialog(this).show("Ocorreu um Erro!!");  
        }
        
        if(campos != null){
            if(campos.get(campos.indexOf(new Fields(0,""))).getInfo().equals("OK")) {
                new OkDialog(this).show("Registo efetuado com sucesso!");
                Main m = new Main(s);
                m.setVisible(true);
                this.dispose();
            } else { new OkDialog(this).show("Registo não efectuado!"); }
        }      
    }//GEN-LAST:event_onClickLogInButton

    private void cancelButtononClickLogInButton(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtononClickLogInButton
        Main m = new Main(s);
        m.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_cancelButtononClickLogInButton

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField nameT;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JButton registerButton;
    private javax.swing.JTextField userT;
    // End of variables declaration//GEN-END:variables
}