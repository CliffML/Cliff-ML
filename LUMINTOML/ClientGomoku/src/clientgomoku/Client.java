package clientgomoku;

import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends Applet implements ActionListener
{
        PrintWriter out;
        BufferedReader in;
        BufferedReader stdIn;
    // A Button to click
        Button okButton;
        Button okButton2;
    // A textField to get text input
        TextField nameField;
        TextField nameField2;
        TextField nameField3;
    // A group of radio buttons
        Label label1;
        Label label3;
        Label label4;
        Label label5;
        
        
    class Gomoku extends Applet implements Runnable {
        /* the Game stuff */
        GameBoard theBoard;
        int turn;  // whose turn is it? 
        boolean got_move;
        int cell_x, cell_y;

        /* the GUI stuff */
        TextArea dispA;
        TextField textChat;
        Panel inputPanel;
        Panel topPanel;
        Label labelName;
        /* the Thread */
        Thread kicker;
        Button startButton;
        Button chatButton;

        Scanner s;

        boolean canMove = false;

        
        
        
        
        public void init()
        {
            
            //try {
                //try {
                setLayout( new BorderLayout() );
                //out.println("getroomname");
                
                labelName = new Label("Welcome, "+Client.nickname);
                startButton = new Button("Start");
                startButton.setEnabled(false);
                startButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Button source = (Button)e.getSource();
                            out.println("startgame");
                        source.setEnabled(false);
                    }
                });
                
                topPanel = new Panel();
                topPanel.setLayout( new BorderLayout() );
                topPanel.add("Center",labelName);
                topPanel.add("East", startButton);
                add("North", topPanel);
                
                chatButton = new Button("Send");
                chatButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Button source = (Button)e.getSource();
                            out.println("chat "+textChat.getText());
                    }
                });
                inputPanel = new Panel();
                inputPanel.setLayout( new BorderLayout() );
                inputPanel.add("North", dispA=new TextArea(5, 35));
                inputPanel.add("Center", textChat=new TextField(32));
                inputPanel.add("East", chatButton);
                
                add("South", inputPanel);
                
                
                Thread waitPlayers = new Thread() {
                    public void run() {
                        try {
                            String inLine;
                            out.println("getroomsize");
                            inLine = in.readLine();
                            System.out.println(inLine);
                            if (inLine.contains("joined")) {
                                inLine = in.readLine();
                                System.out.println(inLine);
                            }
                            int nPlayer = 0;
                            nPlayer = Integer.parseInt(inLine);
                            while (nPlayer < 3) {
                                while ((inLine = in.readLine()) == null) {}
                                if (inLine.contains("chat")) {
                                    out.println("chat "+inLine.substring(5));
                                    display(in.readLine().substring(5));
                                }
                                else if (inLine.contains("joined")) {
                                    nPlayer++;
                                }
                            }
                            startButton.setEnabled(true);
                        } catch (IOException ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };
                waitPlayers.start();

                            addMouseListener(new mseL());
                            resize(600, 600);
                initBoard();
                            /*} catch (IOException ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                            }*/
                        /*} catch (IOException ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }*/
                    }

        public void start()
        {
            /* start the thread */
            kicker = new Thread(this);
            kicker.start();
            /*out.println("getroomname 0");
            String name = stdIn.readLine();
            JLabel adsf =  */
        }

        public void stop()
        {
              kicker.stop();
              kicker=null;
        }

        /* the main Thread loop */
        public void run()
        {
            String input;
            /* here is the main event loop */
            while( kicker != null)
            {
                    got_move = false;

                    /*
                     * Wait for a move to be entered (with the mouse)
                     * This busy wait is a bit silly here, but it's just
                     * what we'll need for the networked version.
                     */
                    while (!canMove) {
                        try {
                            System.out.println("asfd");
                            while ((input = in.readLine()) == null) {}
                                //System.out.println(input);
                                if (input.equals("canMove")) {
                                    canMove = true;
                                    System.out.println("true canmove");
                                    display("Giliran Anda");
                                    input = in.readLine();
                                }
                                else if (input.equals("win")) {
                                    display("Anda menang");
                                    stop();
                                }
                                else if (input.contains("winner")) {
                                    s = new Scanner(input);
                                    String temp = s.next();
                                    temp = s.next();
                                    display(temp + " adalah pemenangnya!");
                                    stop();
                                }
                                else if (input.contains("turn")) {
                                    s = new Scanner(input);
                                    String temp = s.next();
                                    temp = s.next();
                                    display("Giliran " + temp);
                                }
                                else {
                                    s = new Scanner(input);
                                    int x = s.nextInt();
                                    int y = s.nextInt();
                                    int turn = s.nextInt()+1;
                                    System.out.println(x + " " + y + " " + turn);
                                    doMove(x, y, turn);
                                    repaint();
                                }
                        } catch (IOException ex) {
                            Logger.getLogger(ClientGomoku.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    while( got_move == false)
                    {
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {}
                    }
                    //System.out.println("Got: "+cell_x+","+cell_y);

                    if (validMove(cell_x, cell_y))
                    {
                        try {
                            //display("validmove");
                            System.out.println("mau move");
                            out.println("move "+cell_x+" "+cell_y);

                            while ((input = in.readLine()) == null) { }

                            s = new Scanner(input);
                            int x = s.nextInt();
                            int y = s.nextInt();
                            int turn = s.nextInt()+1;
                            System.out.println(x + " " + y + " " + turn);
                            doMove(x, y, turn);


                            /*}else
                            {
                                display("game is unfinished");
                                if (turn<8){
                                    turn=turn+1; //next player's move
                                } else{ turn = 1; //player 1 move again
                                }
                                display("Waiting for next player's move");
                            }*/
                            canMove = false;
                            repaint();
                        } catch (IOException ex) {
                            Logger.getLogger(ClientGomoku.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
            }
        }

        /* just pass along all paint work to the GameBoard class */
        public void paint(Graphics g)
        {
            System.out.println("paint");
            Dimension d = getSize();
            d.height -= inputPanel.getPreferredSize().height;
            theBoard.paintBoard(g, new Rectangle( 0,0, d.width, d.height ) );
        }

        /* this method sets up the board */
        public void initBoard()
        {
            int numOfPlayer = 8;
            display("Waiting for 1st player's move");
            System.out.println("masuk");
            theBoard = new GameBoard( 20, 20, numOfPlayer);
        }

        /* ok, check if x,y is a valid (empty) square */
        boolean validMove(int x, int y)
        {
            return ( theBoard.pieceAt(x,y) == GameBoard.EMPTY );
        }

        /* ok this actually makes a move. */
        public void doMove(int x, int y, int color)
        {
            theBoard.addPiece( x,y,color );
        }



        /* display a string in the TextArea */
        public void display(String str)
        {
            dispA.append(str+"\n");
        }

        class mseL extends MouseAdapter
        {
            public void mousePressed(MouseEvent e)
            {
                if (canMove) {
                    got_move = true;
                    cell_x = e.getX()/theBoard.pieceWidth;
                    cell_y = e.getY()/theBoard.pieceHeight;
                }
            }
        }
    }
        
    public static String nickname;
    public static int roomID;
    public static String roomName;
    public void init()
    {
            try {
                String hostName = "127.0.0.1";
                int portNum = 8000;
                Socket sock = new Socket(hostName, portNum);
                out = new PrintWriter(sock.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                // Tell the applet not to use a layout manager.
                setLayout(null);
                label1 = new Label("Insert Your Nickname : ");
                label3 = new Label("Create Room : ");
                label4 = new Label("Join Room : ");
                label5 = new Label("");
                // initialze the button and give it a text.
                okButton = new Button("Create");
                okButton2 = new Button("Join");
                // text and length of the field
                nameField = new TextField("",100);
                nameField2 = new TextField("",100);
                nameField3 = new TextField("",100);
                okButton.addActionListener(this);
                okButton2.addActionListener(this);
                
                // now we will specify the positions of the GUI components.
                // this is done by specifying the x and y coordinate and
                //the width and height.
                label1.setBounds(110, 25, 200, 25);
                nameField.setBounds(50,50,250,25);
                label3.setBounds(50,100,100,25);
                nameField2.setBounds(150,100,100,25);
                label4.setBounds(50,135,100,25);
                nameField3.setBounds(150,135,100,25);
                okButton.setBounds(260,100,50,25);
                okButton2.setBounds(260,135,50,25);
                label5.setBounds(60, 165, 300, 25);
                
                
                
                // now that all is set we can add these components to the applet
                add(okButton);
                add(okButton2);
                add(label3);
                add(label4);
                add(label1);
                add(label5);
                add(nameField);
                add(nameField2);
                add(nameField3);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
     }

    @Override
    public void actionPerformed(ActionEvent e) {
            try {
                Button source = (Button)e.getSource();
                if (source.getLabel().equals("Create")){
                    roomName = nameField2.getText();
                    if (roomName.equals(""))
                        label5.setText("Enter the room name.");
                    else {
                        out.println("create_room "+roomName);
                        label5.setText(in.readLine());
                    }
                } else {
                    nickname = nameField.getText();
                    if (nickname.equals("") || nameField3.getText().equals(""))
                        label5.setText("Enter both the nickname and the room ID.");
                    else {
                        System.out.println(nickname);
                        out.println("create_user "+nickname);
                        in.readLine();
                        roomID = Integer.parseInt(nameField3.getText());
                        out.println("join_room "+roomID);
                        System.out.println(roomID);
                        String response = in.readLine();
                        if (response.equals("success")) {
                            remove(okButton);
                            remove(okButton2);
                            remove(label1);
                            remove(label3);
                            remove(label4);
                            remove(label5);
                            remove(nameField);
                            remove(nameField2);
                            remove(nameField3);
                            resize(1080,600);
                            Gomoku playGomoku = new Gomoku();
                            setLayout(new GridLayout(1,0));
                            add(playGomoku);
                            playGomoku.init();
                            playGomoku.start();
                        }
                        else {
                            label5.setText("Room with ID "+roomID+" does not exist.");
                        }
                    }
                        
                }
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
} 

