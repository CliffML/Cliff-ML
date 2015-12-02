/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servergomoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Asus
 */
public class ServerGomoku {
    
    private static ArrayList<ConnectionHandler> clientList = new ArrayList<>();
    private static class ConnectionHandler implements Runnable {
        Socket socket = null;
        
        public ConnectionHandler(Socket sock) {
            socket = sock;
        }
        
        public void write(String line) {
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(line);
            } catch (IOException ex) {
                Logger.getLogger(ServerGomoku.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    sendToAll(inputLine);
                    System.out.println(inputLine);
                }
            } catch (IOException ex) {
                Logger.getLogger(ServerGomoku.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public static void sendToAll(String line) {
        clientList.stream().forEach((client) -> {
            client.write(line);
        });
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage : java ServerGomoku <port number>");
            System.exit(1);
        }
        int portNum = Integer.parseInt(args[0]);
        try {
            ServerSocket servSock = new ServerSocket(portNum);
            for (;;) {
                Socket clientSock = servSock.accept();
                ConnectionHandler connectionHandler = new ConnectionHandler(clientSock);
                clientList.add(connectionHandler);
                new Thread(connectionHandler).start();
            }
            
        } catch (IOException ex) {
            Logger.getLogger(ServerGomoku.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
