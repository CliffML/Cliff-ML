/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientgomoku;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Asus
 */
public class ClientGomoku extends Applet implements Runnable {
    /* the Game stuff */
    GameBoard theBoard;
    int turn;  // whose turn is it? 
    boolean got_move;
    int cell_x, cell_y;

    /* the GUI stuff */
    TextArea dispA;
    Panel inputPanel;

    /* the Thread */
    Thread kicker;

    PrintWriter out;
    BufferedReader in;
    BufferedReader stdIn;
    
    Scanner s;
    
    boolean canMove = false;


    public void init()
    {
    
	/* Set up GUI stuff */
	setLayout( new BorderLayout() );
	inputPanel = new Panel();
	inputPanel.setLayout( new BorderLayout() );
	inputPanel.add("Center", dispA=new TextArea(5, 35));
	add("South", inputPanel);

	addMouseListener(new mseL());

	
        // TODO code application logic here
        /*if (args.length != 2) {
            System.out.println("Usage : java ClientGomoku <host name> <port number>");
            System.exit(1);
        }*/
        String hostName = "127.0.0.1";
        int portNum = 8000;
        try {
            // TODO code application logic here
            Socket sock = new Socket(hostName, portNum);
            out = new PrintWriter(sock.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            
            /*ServerListener listener = new ServerListener(sock);
            new Thread(listener).start();*/
            
            String userInput;
            /*while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("echo: " + userInput);
            }*/
        } catch (IOException ex) {
            Logger.getLogger(ClientGomoku.class.getName()).log(Level.SEVERE, null, ex);
        }
        /* start a new game */
	initBoard();
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
                            }
                            else {
                                s = new Scanner(input);
                                int x = s.nextInt();
                                int y = s.nextInt();
                                int turn = s.nextInt()+1;
                                System.out.println(x + " " + y + " " + turn);
                                doMove(x, y, turn);
                                System.out.println("asf");
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
                        
                        
                        /* if the game is over */
                        /*if( endGame(cell_x, cell_y) ){
                            System.out.println("a");
                            //Menentukan siapa pemenang
                            if (turn == GameBoard.RED){
                                display("Red wins!");}
                            else if (turn == GameBoard.YELLOW){
                                display("Yellow wins!");}
                            else if (turn == GameBoard.GREEN){
                                display("Green wins!");}
                            else if (turn == GameBoard.BLUE){
                                display("Blue wins!");}
                            else if (turn == GameBoard.BLACK){
                                display("Black wins!");}
                            else if (turn == GameBoard.GRAY){
                                display("Gray wins!");}
                            else if (turn == GameBoard.PINK){
                                display("Pink wins!");}
                            else if (turn == GameBoard.MAGENTA){
                                display("Magenta wins!");}
                            
                            //Permainan selesai
                            stop();
                            /*
                            * At this point there really should be someway
                            * to restart the game, but that's not
                            * currently implemented.
                            */
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
        /*
            //turn = 1;
            int numOfPlayer = 8;
            
            display("Waiting for 1st player's move");
            System.out.println("masuk");
            theBoard = new GameBoard( 20, 20, numOfPlayer);*/
            
        try {
            //turn = 1;
            int numOfPlayer = 8;
            String input;
            out.println("create_user user3");
            input = in.readLine();
            /*out.println("create_room asdf");
            input = in.readLine();*/
            out.println("join_room 2 0");
            input = in.readLine();
            //out.println("startgame");
            display("Waiting for 1st player's move");
            System.out.println("masuk");
            theBoard = new GameBoard( 20, 20, numOfPlayer);
        } catch (IOException ex) {
            Logger.getLogger(ClientGomoku.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    /*private class ServerListener implements Runnable {
        Socket socket = null;
        public ServerListener(Socket sock) {
            socket = sock;
        }
        
        @Override
        public void run() {
            try {
                String input;
                while ((input = in.readLine()) != null) {
                    if (input.equals("canMove"))
                        canMove = true;
                    System.out.println("Received : "+input);
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientGomoku.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }*/
    
    /**
     * @param args the command line arguments
     */
    /*public static void main(String[] args) {
        
    }*/
    
}
