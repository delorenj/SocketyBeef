package SocketyBeef;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

class BeefyWorker implements Runnable {
  private Socket client;
  private JTextArea textArea;
  
  BeefyWorker(Socket client, JTextArea textArea) {
   this.client = client;
   this.textArea = textArea;   
  }

  public void run(){
    String line;
    BufferedReader in = null;
    PrintWriter out = null;
    try{
      in = new BufferedReader(new InputStreamReader(client.getInputStream()));
      out = new PrintWriter(client.getOutputStream(), true);
    } catch (IOException e) {
      System.out.println("in or out failed");
      System.exit(-1);
    }

    while(true){
      try{
        line = in.readLine();
//Send data back to client
         out.println(line);
         textArea.append(line);
       } catch (IOException e) {
         System.out.println("Read failed");
         System.exit(-1);
       }
    }
  }
}

class BeefyServer extends JFrame{
/**
	 * 
	 */
	private static final long serialVersionUID = -7530847271659170546L;
JLabel label = new JLabel("Text received over socket:");
   JPanel panel;
   JTextArea textArea = new JTextArea();
   ServerSocket server = null;

   BeefyServer(){ //Begin Constructor
     panel = new JPanel();
     panel.setLayout(new BorderLayout());
     panel.setBackground(Color.white);
     getContentPane().add(panel);
     panel.add("North", label);
     panel.add("Center", textArea);
   } //End Constructor

  public void listenSocket(){
    try{
      server = new ServerSocket(6969); 
    } catch (IOException e) {
      System.out.println("Could not listen on port 6969");
      System.exit(-1);
    }
    while(true){
      BeefyWorker w;
      try{
        w = new BeefyWorker(server.accept(), textArea);
        Thread t = new Thread(w);
        t.start();
      } catch (IOException e) {
        System.out.println("Accept failed: 6969");
        System.exit(-1);
      }
    }
  }

  protected void finalize(){
//Objects created in run method are finalized when 
//program terminates and thread exits
     try{
        server.close();
    } catch (IOException e) {
        System.out.println("Could not close socket");
        System.exit(-1);
    }
  }

  public static void main(String[] args){
    BeefyServer frame = new BeefyServer();
	frame.setTitle("Beefy Server");
        WindowListener l = new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                        System.exit(0);
                }
        };
        frame.addWindowListener(l);
        frame.pack();
        frame.setVisible(true);
        frame.listenSocket();
  }
}