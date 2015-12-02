/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientgomoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Asus
 */
public class ClientGomoku {    
    private static class ServerListener implements Runnable {
        Socket socket = null;
        BufferedReader in = null;
        public ServerListener(Socket sock, BufferedReader input) {
            socket = sock;
            in = input;
        }
        
        @Override
        public void run() {
            try {
                String input;
                while ((input = in.readLine()) != null) {
                    System.out.println("Received : "+input);
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientGomoku.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        if (args.length != 2) {
            System.out.println("Usage : java ClientGomoku <host name> <port number>");
            System.exit(1);
        }
        String hostName = args[0];
        int portNum = Integer.parseInt(args[1]);
        try {
            // TODO code application logic here
            Socket sock = new Socket(hostName, portNum);
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            
            ServerListener listener = new ServerListener(sock, in);
            new Thread(listener).start();
            
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("echo: " + userInput);
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientGomoku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
