/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import Info.Communication;
import Info.Fields;
import Info.PDU;
import java.io.IOException;
import java.util.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Work
 */
public class MainMenu extends javax.swing.JFrame {

    private String user;
    private Communication s;
    
    /**
     * Creates new form NewJFrame
     */
    public MainMenu() {
        initComponents();
        this.setLocationRelativeTo(null);
    }

    MainMenu(Communication s, String user) {
        this.s = s;
        this.user = user;
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

        jPanel1 = new javax.swing.JPanel();
        accept = new javax.swing.JButton();
        rankingL = new javax.swing.JButton();
        logout = new javax.swing.JButton();
        rankingG = new javax.swing.JButton();
        list = new javax.swing.JButton();
        add = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();

        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setPreferredSize(new java.awt.Dimension(360, 333));

        accept.setText("Aceitar Desafio");
        accept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptActionPerformed(evt);
            }
        });

        rankingL.setText("Ranking Local");
        rankingL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rankingLActionPerformed(evt);
            }
        });

        logout.setText("Logout");
        logout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutActionPerformed(evt);
            }
        });

        rankingG.setText("Ranking Global");
        rankingG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rankingGActionPerformed(evt);
            }
        });

        list.setText("Listar Desafios");
        list.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listActionPerformed(evt);
            }
        });

        add.setText("Adicionar Desafio");
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(rankingL, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(add))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rankingG, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(list, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(accept, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addGap(10, 10, 10)
                        .addComponent(logout, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {accept, add, list, logout, rankingG, rankingL});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(add, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(list, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rankingL, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rankingG, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(accept, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logout, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {accept, add, list, logout, rankingG, rankingL});

        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Nº Desafio", "Data", "Inicio"
            }
        ));
        jScrollPane1.setViewportView(jTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(81, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rankingLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rankingLActionPerformed
        ArrayList<Fields> l = new ArrayList<>();
        List<Fields> campos = null;
        try {
            l.add(new Fields(0, "LOCAL"));
            byte[] r = Communication.serializa(l);
            PDU a = new PDU(13,l.size(),r.length,l);
            this.s.sendPDU(a);      

            PDU receive = this.s.receivePDU();
            campos = receive.getCampos();
        } catch (IOException ex) { new OkDialog(this).show("Ocorreu um erro!!"); }
        
        if(campos != null) {
            if(campos.get(campos.indexOf(new Fields(0,""))).getInfo().equals("OK")) {
                new OkDialog(this).show("O Ranking foi apresentado!");
                HashMap<String,Integer> r = new HashMap<>();
                
                int i = 1;
                while(i < campos.size()){
                    String al = campos.get(i).getInfo();
                    int sc = Integer.valueOf(campos.get(i+1).getInfo());
                    r.put(al,sc);
                    i += 2;
                }  
                RankingChallenge t = new RankingChallenge(r);
                t.setVisible(true);
            } else { new OkDialog(this).show("Não consegui apresentar o Ranking!"); } 
        }
    }//GEN-LAST:event_rankingLActionPerformed

    private void rankingGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rankingGActionPerformed
        ArrayList<Fields> l = new ArrayList<>();
        List<Fields> campos = null;
        try {
            l.add(new Fields(0, "GLOBAL"));
            byte[] r = Communication.serializa(l);
            PDU a = new PDU(13,l.size(),r.length,l);
            this.s.sendPDU(a);      

            PDU receive = this.s.receivePDU();
            campos = receive.getCampos();
        } catch (IOException ex) { new OkDialog(this).show("Ocorreu um erro!!"); }
        
        if(campos != null) {
            if(campos.get(campos.indexOf(new Fields(0,""))).getInfo().equals("OK")) {
                new OkDialog(this).show("O Ranking foi apresentado!");
                HashMap<String,Integer> r = new HashMap<>();
                
                int i = 1;
                while(i < campos.size()){
                    String al = campos.get(i).getInfo();
                    int sc = Integer.valueOf(campos.get(i+1).getInfo());
                    r.put(al,sc);
                    i += 2;
                }  
                RankingChallenge t = new RankingChallenge(r);
                t.setVisible(true);
            } else { new OkDialog(this).show("Não consegui apresentar o Ranking!"); } 
        }  
    }//GEN-LAST:event_rankingGActionPerformed

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
        NewChallenge nc = new NewChallenge(s,user);
        nc.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_addActionPerformed

    private void logoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutActionPerformed
        ArrayList<Fields> a = new ArrayList<>();
        a.add(new Fields(2, user));
        List<Fields> b = null;
        try {
            byte[] r = Communication.serializa(a);
            PDU l = new PDU(4, a.size(), r.length, a);
            this.s.sendPDU(l);
            PDU receive = this.s.receivePDU();
            b = receive.getCampos();
        } catch (IOException ex) { new OkDialog(this).show("Ocorreu um erro!!"); }
        if(b != null){
            if(b.get(b.indexOf(new Fields(0,""))).getInfo().equals("OK")) {
                new OkDialog(this).show("Logout efectuado!");
                System.exit(0);
            }
            else { new OkDialog(this).show("Logout não efectuado!"); }
        } 
    }//GEN-LAST:event_logoutActionPerformed
    
    private void listActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listActionPerformed
        ArrayList<Fields> l = new ArrayList<>();
        List<Fields> campos = null;
        try {
            byte[] r = Communication.serializa(l);
            PDU a = new PDU(7,l.size(),r.length,l);
            this.s.sendPDU(a);      

            PDU receive = this.s.receivePDU();
            campos = receive.getCampos();
        } catch (IOException ex) { new OkDialog(this).show("Ocorreu um erro!!"); }
        
        if(campos != null) {
            if(campos.get(campos.indexOf(new Fields(0,""))).getInfo().equals("OK")) {
                new OkDialog(this).show("Os desafios foram listados!");

                DefaultTableModel model = (DefaultTableModel) jTable.getModel();
                int size = model.getRowCount();
                int i = size-1;
                for (; i>=0; i--) { model.removeRow(i); }

                i = 1;
                while(i < campos.size()){
                    String challenge = campos.get(i).getInfo();
                    String date = campos.get(i+1).getInfo();
                    String hour = campos.get(i+2).getInfo();
                    i += 3;
                    model.addRow(new Object[]{challenge, date, hour});       
                }  
            } else { new OkDialog(this).show("Não consegui listar os desafios!"); } 
        }   
    }//GEN-LAST:event_listActionPerformed

    private void acceptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptActionPerformed
        AcceptChallenge ac = new AcceptChallenge(s,user);
        ac.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_acceptActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        ArrayList<Fields> a = new ArrayList<>();
        a.add(new Fields(2, user));
        List<Fields> b = null;
        try {
            byte[] r = Communication.serializa(a);
            PDU l = new PDU(4, a.size(), r.length, a);
            this.s.sendPDU(l);
            PDU receive = this.s.receivePDU();
            b = receive.getCampos();
        } catch (IOException ex) { new OkDialog(this).show("Ocorreu um erro!!"); }
        if(b != null){
            if(b.get(b.indexOf(new Fields(0,""))).getInfo().equals("OK")) {
                new OkDialog(this).show("Logout efectuado!");
                System.exit(0);
            }
            else { new OkDialog(this).show("Logout não efectuado!"); }
        }
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton accept;
    private javax.swing.JButton add;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable;
    private javax.swing.JButton list;
    private javax.swing.JButton logout;
    private javax.swing.JButton rankingG;
    private javax.swing.JButton rankingL;
    // End of variables declaration//GEN-END:variables
}
