package clientgomoku;

import java.awt.*;
import java.util.ArrayList;

/*
 * Supporting class for GoMoku applet.
 *
 * This code is heavily based on the Othello code from
 * Chapter 15 of The Black Art of Java Game Programming.
 ** sources kode referensi: http://www.mscs.mu.edu/~mikes/198.1999/GameBoard.txt
 *Dimodifikasi untuk keperluan tugas Jarkom 2015 ITB
 * Modified by mike slattery - april 1999
 */

public class GameBoard
{
private int numOfPlayer;
static final int OFF = -1;
static final int EMPTY = 0;
static final int RED = 1;
static final int YELLOW = 2;
static final int GREEN = 3;
static final int BLUE = 4;
static final int BLACK = 5;
static final int GRAY = 6;
static final int PINK = 7;
static final int MAGENTA = 8;

public int pieceWidth, pieceHeight;
public int rows, cols;  // number of rows/columns
int board[][]; // the actual board itself

	public GameBoard( int w, int h, int player) {
		board = new int[w][h];
                numOfPlayer=player;
		cols = w;
		rows = h;
                System.out.println("Kebuat boardnya");
		for (int i=0; i < w; i++)
			for (int j=0; j < h; j++)
				board[i][j] = EMPTY;
	}

	public void paintBoard(Graphics g, Rectangle r) {

		int x;
		int y;

		pieceWidth = (int) (r.width/cols);
		pieceHeight = (int) (r.height/rows);

		g.setColor(Color.white);
		g.fillRect( r.x, r.y, r.width, r.height );

		for( x=0; x<cols; x++)
		{
			g.setColor( Color.black );
			g.drawLine( x* pieceWidth, 0, x*pieceWidth, r.height );
			for(y=0; y< rows; y++) {
				g.setColor( Color.black );
				g.drawLine(0, y* pieceHeight, r.width, y*pieceHeight );
			
                                
                                 //input warna sesuai pemain
				if (board[x][y] == RED)
				{
					g.setColor(Color.red);
					g.fillOval(r.x+pieceWidth*x+1, r.y + pieceHeight*y+1, pieceWidth-2, pieceHeight-2);
				}
				else if (board[x][y] == YELLOW)
				{
					g.setColor(Color.yellow);
					g.fillOval(r.x+pieceWidth*x+1, r.y + pieceHeight*y+1, pieceWidth-2, pieceHeight-2);
				}
                                else if (board[x][y] == GREEN)
				{
					g.setColor(Color.green);
					g.fillOval(r.x+pieceWidth*x+1, r.y + pieceHeight*y+1, pieceWidth-2, pieceHeight-2);
				}
                                else if (board[x][y] == BLUE)
				{
					g.setColor(Color.blue);
					g.fillOval(r.x+pieceWidth*x+1, r.y + pieceHeight*y+1, pieceWidth-2, pieceHeight-2);
				}
                                else if (board[x][y] == BLACK)
				{
					g.setColor(Color.black);
					g.fillOval(r.x+pieceWidth*x+1, r.y + pieceHeight*y+1, pieceWidth-2, pieceHeight-2);
				}
                                else if (board[x][y] == GRAY)
				{
					g.setColor(Color.gray);
					g.fillOval(r.x+pieceWidth*x+1, r.y + pieceHeight*y+1, pieceWidth-2, pieceHeight-2);
				}
                                else if (board[x][y] == PINK)
				{
					g.setColor(Color.pink);
					g.fillOval(r.x+pieceWidth*x+1, r.y + pieceHeight*y+1, pieceWidth-2, pieceHeight-2);
				}
                                else if (board[x][y] == MAGENTA)
				{
					g.setColor(Color.magenta);
					g.fillOval(r.x+pieceWidth*x+1, r.y + pieceHeight*y+1, pieceWidth-2, pieceHeight-2);
				}
                             
			}
		}
	}

	public int pieceAt(int x, int y)
	{

		if( x>=cols || x<0 || y>= rows || y<0 ) return OFF;
		return board[x][y];
	}


	public void addPiece( int x, int y, int color)
	{

		if( x> cols || x<0 || y>rows || y<0) return ;
		board[x][y] = color;
	}
        
        

}