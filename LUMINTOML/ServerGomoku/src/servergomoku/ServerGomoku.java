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
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import clientgomoku.GameBoard;

/**
 *
 * @author Asus
 */
public class ServerGomoku {
    private static ArrayList<Room> roomList = new ArrayList<>();
    private static ArrayList<User> users = new ArrayList<>();
    private static ArrayList<ConnectionHandler> clientList = new ArrayList<>();
    private static int counter = 0;
    
    private static class ConnectionHandler implements Runnable {
        private int clientNo;
        private Socket socket = null;
        private GameBoard theBoard;
        
        public ConnectionHandler(Socket sock) {
            socket = sock;
            clientNo = counter;
            System.out.println(clientNo);
            theBoard = new GameBoard(20, 20, 99);
        }
        
        /* check if the game is over */
        public boolean endGame(int x, int y)
        {
            int count, color;
            int tx, ty;

            // See whether the move just made at x,y has won.
            // We need to see if we now have five-in-a-row.
            color = theBoard.pieceAt(x,y);

            // check horizontal first
            tx = x; ty = y;
            while ((tx>0) && (theBoard.pieceAt(tx-1,ty)==color))
                    tx--;
            count = 1;
            while ((tx < theBoard.cols-1) && (theBoard.pieceAt(tx+1,ty)==color))
            {
                    count++;
                    tx++;
            }
            //display("horiz count="+count);
            if (count >= 5)
                    return true;

            // then do the three counts with vertical components
            for (int dx = -1; dx <= 1; dx++)
            {
                    tx = x; ty = y;
                    while ((ty>0) && ((tx-dx)>=0) && ((tx-dx)<theBoard.cols)
                                    && (theBoard.pieceAt(tx-dx,ty-1)==color))
                    {
                            tx-=dx;
                            ty--;
                    }
                    count = 1;
                    while ((ty<theBoard.rows-1) && ((tx+dx)>=0) && ((tx+dx)<theBoard.cols)
                                    && (theBoard.pieceAt(tx+dx,ty+1)==color))
                    {
                            count++;
                            tx+=dx;
                            ty++;
                    }
                    //display("count (dx="+dx+")="+count);
                    if (count >= 5)
                            return true;
            }
            return false;
        }
        
        public String parseCommand(String line) {
            Scanner s = new Scanner(line);
            String command = null;
            String ret = null;
            
            command = s.next();
            if (command.compareToIgnoreCase("create_room") == 0) {
                roomList.add(new Room(s.next()));
                write("Created room " + roomList.get(roomList.size()-1).getName());
                System.out.println("Created room " + roomList.get(roomList.size()-1).getName());;
            }
            else if (command.compareToIgnoreCase("join_room") == 0) {
                
                int id = clientNo;
                int i;
                int roomID = Integer.parseInt(s.next());
                if (roomID < roomList.size()) {
                    User user = null;
                    i = 0;
                    while (i < users.size() && id != users.get(i).getUserID())
                        i++;
                    user = users.get(i);

                    roomList.get(roomID).addUser(user);
                    write("success");
                    System.out.println("User " + user.getNick() + " joined room #" + roomID);
                    int j;
                    for (j=0;j<roomList.get(roomID).getNUser();j++) {
                        sendToSpecific("joined", roomList.get(roomID).getUsers().get(j).getUserID());
                    }
                }
                else
                {
                    write("failed");
                }
            }
            else if (command.compareToIgnoreCase("create_user") == 0) {
                users.add(new User(s.next(), clientNo));
                write("Created user " + users.get(users.size()-1).getNick());
                System.out.println("Created user " + users.get(users.size()-1).getNick());
            }
            else if (command.compareToIgnoreCase("getroomname") == 0) {
                int id = clientNo;
                int i;
                User user = null;
                i = 0;
                while (i < users.size() && id != users.get(i).getUserID())
                    i++;
                user = users.get(i);
                i = 0;
                while (i < roomList.size() && !roomList.get(i).getUsers().contains(user)) {
                    i++;
                }
                write(roomList.get(i).getName());
            }
            else if (command.compareToIgnoreCase("getroomsize") == 0) {
                int id = clientNo;
                int i;
                User user = null;
                i = 0;
                while (i < users.size() && id != users.get(i).getUserID())
                    i++;
                user = users.get(i);
                i = 0;
                while (i < roomList.size() && !roomList.get(i).getUsers().contains(user)) {
                    i++;
                }
                write(Integer.toString(roomList.get(i).getNUser()));
                System.out.println(Integer.toString(roomList.get(i).getNUser()));
            }
            else if (command.compareToIgnoreCase("getusers") == 0) {
                int i = Integer.parseInt(s.next());
                write(roomList.get(i).getName());
                //System.out.println("Created user " + users.get(users.size()-1).getNick());
                //write("clientNo "+Integer.toString(users.size()-1)); //assign clientNo
                //System.out.println("clientNo "+Integer.toString(users.size()-1));
            }
            else if (command.compareToIgnoreCase("startgame") == 0) {
                int id = clientNo;
                int i;
                User user = null;
                i = 0;
                while (i < users.size() && id != users.get(i).getUserID())
                    i++;
                user = users.get(i);
                i = 0;
                while (i < roomList.size() && !roomList.get(i).getUsers().contains(user)) {
                    i++;
                }
                
                sendToSpecific("canMove", roomList.get(i).getUsers().get(0).getUserID());
                for (int j=0;j<roomList.get(i).getNUser();j++) {
                    sendToSpecific("turn " + roomList.get(i).getUsers().get(0).getNick(), roomList.get(i).getUsers().get(j).getUserID());
                }
            }
            else if (command.compareToIgnoreCase("request") == 0) {
                write("response");
            }
            else if (command.compareToIgnoreCase("move") == 0) {
                int x = Integer.parseInt(s.next());
                int y = Integer.parseInt(s.next());
                int id = clientNo;
                int i;
                User user = null;
                i = 0;
                while (i < users.size() && id != users.get(i).getUserID())
                    i++;
                user = users.get(i);
                i = 0;
                while (i < roomList.size() && !roomList.get(i).getUsers().contains(user)) {
                    i++;
                }
                String str = Integer.toString(x) + " " + Integer.toString(y) + " " + Integer.toString(roomList.get(i).getUsers().indexOf(user));
                for (int j=0;j<roomList.get(i).getNUser();j++) {
                    sendToSpecific(str, roomList.get(i).getUsers().get(j).getUserID());
                }
                theBoard.addPiece(x, y, roomList.get(i).getUsers().indexOf(user) + 1);
                int nextTurn = (roomList.get(i).getUsers().indexOf(user) + 1)%roomList.get(i).getNUser();
                if (endGame(x, y)) {
                    //Menentukan siapa pemenang
                    write("win");
                    for (int j=0;j<roomList.get(i).getNUser();j++) {
                        sendToSpecific("winner " + user.getNick(), roomList.get(i).getUsers().get(j).getUserID());
                    }
                }
                else {
                    //System.out.println(nextTurn);
                    //System.out.println("UserID : " + roomList.get(i).getUsers().get(nextTurn).getUserID());
                    sendToSpecific("canMove", roomList.get(i).getUsers().get(nextTurn).getUserID());
                    for (int j=0;j<roomList.get(i).getNUser();j++) {
                        sendToSpecific("turn " + roomList.get(i).getUsers().get(nextTurn).getNick(), roomList.get(i).getUsers().get(j).getUserID());
                    }
                }
            }
            else if (command.compareToIgnoreCase("chat") == 0) {
                String msg;
                int id = clientNo;
                int i;
                User user = null;
                i = 0;
                while (i < users.size() && id != users.get(i).getUserID())
                    i++;
                user = users.get(i);
                msg = "chat " + user.getNick() + ":";
                while (s.hasNext()) {
                    msg += s.nextLine();
                }
                i = 0;
                while (i < roomList.size() && !roomList.get(i).getUsers().contains(user)) {
                    i++;
                }
                for (int j=0; j<roomList.get(i).getNUser(); j++) {
                    sendToSpecific(msg, roomList.get(i).userList.get(j).getUserID());
                }
            }
            else {
                write("No such command");
                //System.out.println("No such command");
            }
            return ret;
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
                    String line = parseCommand(inputLine);
                    if (line != null)
                        sendToAll(line);
                    System.out.println(inputLine);
                }
            } catch (IOException ex) {
                Logger.getLogger(ServerGomoku.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private static class Room {
        private ArrayList<User> userList = new ArrayList<>();
        private String name;
        private int roomID;
        
        public Room(String name) {
            this.name = name;
        }
        public void addUser(User user) {
            userList.add(user);
        }
        public int getNUser() {
            return userList.size();
        }
        public String getName() {
            return name;
        }
        public ArrayList<User> getUsers() {
            return userList;
        }
        
    }
    
    private static class User {
        private String nickname;
        private int score;
        private int userID;
        
        public User(String nick, int ID) {
            nickname = nick;
            score = 0;
            userID = ID;
        }
        public String getNick() {
            return nickname;
        }
        public int getScore() {
            return score;
        }
        public int getUserID() {
            return userID;
        }
        public void addScore(int x) {
            score += x;
        }
        public void setUserID(int x) {
            userID = x;
        }
    }
    
    public static void sendToAll(String line) {
        clientList.stream().forEach((client) -> {
            client.write(line);
        });
    }
    
    public static void sendToSpecific(String line, int x) {
        int i = 0;
        while (i < clientList.size() && i != x) {
            i++;
        }
        clientList.get(i).write(line);
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
                counter++;
            }
            
        } catch (IOException ex) {
            Logger.getLogger(ServerGomoku.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
