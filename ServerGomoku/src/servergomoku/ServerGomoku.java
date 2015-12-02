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
            Socket clientSock = servSock.accept();
            PrintWriter out =
                new PrintWriter(clientSock.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSock.getInputStream()));
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                out.println(inputLine);
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerGomoku.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
