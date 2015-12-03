import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
import java.io.*;

/*
 * GoMoku applet.
 *
 * Implement the traditional five-in-a-row game for
 * two players on one machine.
 *
 * This code is heavily based on the Othello code from
 * Chapter 15 of The Black Art of Java Game Programming.
 *
 * sources kode referensi: http://www.mscs.mu.edu/~mikes/198/GoMoku.txt
 *Dimodifikasi untuk keperluan tugas Jarkom 2015 ITB
 * Modified by mike slattery - april 1999
 */

public class GoMoku extends Applet implements Runnable {

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


public void init()
{
	/* Set up GUI stuff */
	setLayout( new BorderLayout() );
	inputPanel = new Panel();
	inputPanel.setLayout( new BorderLayout() );
	inputPanel.add("Center", dispA=new TextArea(5, 35));
	add("South", inputPanel);

	addMouseListener(new mseL());

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
	/* here is the main event loop */
	while( kicker != null)
	{
		got_move = false;

		/*
		 * Wait for a move to be entered (with the mouse)
		 * This busy wait is a bit silly here, but it's just
		 * what we'll need for the networked version.
		 */
		while( got_move == false)
		{
			try {
				Thread.sleep(100);
			} catch (Exception e) {}
		}
		//System.out.println("Got: "+cell_x+","+cell_y);

		if (validMove(cell_x, cell_y))
		{
                        //display("validmove");
			doMove(cell_x, cell_y, turn);
			/* if the game is over */
			if( endGame(cell_x, cell_y) ){
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
                        }else
			{
                                display("game is unfinished");
                                if (turn<8){
                                turn=turn+1; //next player's move
                                } else{ turn = 1; //player 1 move again
                                }
				display("Waiting for next player's move");
			}
			repaint();
		}
	}
}

/* just pass along all paint work to the GameBoard class */
public void paint(Graphics g)
{
	Dimension d = getSize();
	d.height -= inputPanel.getPreferredSize().height;
	theBoard.paintBoard(g, new Rectangle( 0,0, d.width, d.height ) );
}

/* this method sets up the board */
public void initBoard()
{
	turn = 1;
        int numOfPlayer = 8;
	display("Waiting for 1st player's move");
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
		got_move = true;
		cell_x = e.getX()/theBoard.pieceWidth;
		cell_y = e.getY()/theBoard.pieceHeight;
	}
}

} // end class GoMoku



