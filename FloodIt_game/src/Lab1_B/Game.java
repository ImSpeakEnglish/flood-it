package Lab1_B;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Game implements EventsListener {
    // --- FIELDS ------------------------------------------------------------------------------------------------------
    // --- Static ---
    private static final Color[] colors = {
		    Color.decode("#FFEB3B"), // yellow
		    Color.decode("#4CAF50"), // green
		    Color.decode("#E91E63"), // red
		    Color.decode("#FF9800"), // orange
		    Color.decode("#673AB7"), // purple
		    Color.decode("#03A9F4"), // cyan
		    Color.decode("#91eae1"), // riptide
		    Color.decode("#9E9E9E")  // gray
    };
    private static final Color buttonColor = Color.decode("#c4c4c4");
	private static final Color activeButtonColor = Color.decode("#919191");
	private static final Color fontColor = Color.black;
	private static final Color backColor = Color.white;
    public static final int[] sizes = {6, 10, 14, 20, 26, 36, 50};
    /**
     * Distance between window top left corner to game board.
     */
    private static final Point boardOffset = new Point(12, 9);
    private static final Point minWindowSize = new Point(40, 23);
    private static Random rand = new Random();

    // --- Instance Fields ---
    private MyScreenKTU sc;
	/**
	 * Is debug mode on?
	 */
	private boolean debug;
	/**
	 * Distance between game board and window bottom right corner.
	 */
	private Point boardEndOffset = new Point(2,2);
	private Point windowSize;
	
	// --- Gameplay variables ---
	/**
	 * Game board size in cells.
	 * x*y  or  columns * rows
	 */
	private Point boardSize;
	/**
	 * Holds info about all game board cells.
	 * [x][y]  or  [column][row]
	 */
	private Cell[][] board;
	/**
	 * Number of colors used in this game.
	 */
	private int usedColors;
	private int moves = 0;
	private boolean gameActive = true;

    // --- CONSTRUCTORS ------------------------------------------------------------------------------------------------
    /**
     * Creates a new game window, with a game board of size x*y and specified amount of colors in game.
     * @param x Game board width.
     * @param y Game board height.
     * @param colors Number of colors to use in game.
     */
    public Game(int x, int y, int colors) {
        this(x, y, colors, false);
    }
    /**
     * @param debug If set to true, displays grid and cell numbers.
     */
    public Game(int x, int y, int colors, boolean debug) {
        boardSize = new Point(x, y);
        board = new Cell[x][y];
	    usedColors = colors;
	    
	    // Setting minimum window size
	    if (boardOffset.x + x + boardEndOffset.x < minWindowSize.x)
	    	boardEndOffset.x = minWindowSize.x - x - boardOffset.x;
	    if (boardOffset.y + y + boardEndOffset.y < minWindowSize.y)
	    	boardEndOffset.y = minWindowSize.y - y - boardOffset.y;
	    
	    windowSize = new Point(boardOffset.x + boardSize.x + boardEndOffset.x,
			    boardOffset.y + boardSize.y + boardEndOffset.y);
	    /* ch = cell height
	     * cw = cell width
	     * sh = screen height (in cells)
	     * sw = screen width (in cells)
	     */
        sc = new MyScreenKTU(15, 15,
                boardOffset.y + y + boardEndOffset.y,
                boardOffset.x + x + boardEndOffset.x,
                debug ? MyScreenKTU.Grid.ON : MyScreenKTU.Grid.OFF);
        sc.addListener(this);
        this.debug = debug;
        DrawNewGame();
    }

    // --- METHODS -----------------------------------------------------------------------------------------------------
    // --- Gameplay logic ---
	/**
	 * Processes player's moves i.e. when a colored cell is clicked
	 */
	private void NextMove(int clickX, int clickY) {
    	if (!gameActive) // Game is already finished
    		return;
    	
    	int newColor = board[clickX][clickY].color; // Get new color
    	if (newColor == board[0][0].color) // Return in case it's the same
    		return;
    	
    	// Do all the magic stuff
        for (Cell[] column : board) {
        	for (Cell cell : column) {
        		if (cell.isConquered)
        			CheckSurroundingCells(cell, newColor);
	        }
        }
		moves++;
        sc.print(sizes.length + colors.length + 4, 1, Integer.toString(moves), backColor);
        
        // Check if game was won (all cells are conquired)
		boolean gameWon = true;
		for (Cell[] column : board) {
			for (Cell cell : column) {
				if (!cell.isConquered) {
					gameWon = false;
					break;
				}
			}
			if (!gameWon) break;
		}
		
		if (gameWon)
			GameWon();
		sc.refresh();
	}
	private void CheckSurroundingCells(Cell c, int newColor) {
		if (c.color != newColor) {
			sc.fillRect(c.y + boardOffset.y, c.x + boardOffset.x, 1, 1, colors[newColor]);
			c.color = newColor;
		}
		
		if (boardSize.x - 1 > c.x)  // Right
			CheckCell(board[c.x + 1][c.y], newColor);
		
		if (c.x != 0)               // Left
			CheckCell(board[c.x - 1][c.y], newColor);
		
		if (c.y != 0)               // Up
			CheckCell(board[c.x][c.y - 1], newColor);
		
		if (boardSize.y - 1 > c.y)  // Down
			CheckCell(board[c.x][c.y + 1], newColor);
	}
	private void CheckCell(Cell c, int newColor) {
		if (!c.isConquered && c.color == newColor) {
			c.isConquered = true;
			sc.fillRect(c.y + boardOffset.y, c.x + boardOffset.x, 1, 1, colors[newColor]);
			CheckSurroundingCells(c, newColor);
		}
	}
	private void GameWon() {
		if (debug) System.out.println(String.format("--- GAME WON ---%nGame %dx%d#%d completed in %d moves.",
				boardSize.x, boardSize.y, usedColors, moves));
		
		sc.print(boardOffset.y + boardSize.y + boardEndOffset.y - 2, 1, "Congratulations! You have completed");
		sc.print(boardOffset.y + boardSize.y + boardEndOffset.y - 1, 1,
				String.format("%dx%d#%d game in %d moves.", boardSize.x, boardSize.y, usedColors, moves));
		
		gameActive = false;
	}
	private void NewGame(int size, int colors) {
		Game g = new Game(size, size, colors, debug);
		g.sc.getJFrame().setLocation(sc.getJFrame().getLocation()); // Sets the same window location for new window
		sc.getJFrame().dispose();
	}
	
    // --- Graphics methods ---
    private void DrawNewGame() {
        sc.clearAll(debug ? Color.gray : backColor);
        if (debug) sc.printRowsColumnsNumbers();
        ColorBoardRandomly();
        DrawGUI();
        DrawTitle();
        sc.refresh();
    }
    /**
     * Prepares board for new game. Colors every board cell in random color.
     */
    private void ColorBoardRandomly() {
        for (int column = 0; column < board.length; column++) {
            for (int row = 0; row < board[column].length; row++) {
                int newColor = rand.nextInt(usedColors);
                board[column][row] = new Cell(column, row, newColor);
                sc.fillRect(row + boardOffset.y, column + boardOffset.x, 1,1, colors[newColor]);
            }
        }
        board[0][0].isConquered = true;
    }
	/**
	 * Draws new game buttons and other GUI elements.
	 */
	private void DrawGUI() {
        sc.setColors(backColor, fontColor);
		sc.print(6, 12, "Click cells. Fill the board");
		sc.print(7, 12, "with a single color.");
		sc.print(1, 1, "New game:");
		
	    // Print sizes buttons
		int i = 0;
		for ( ; i < sizes.length; i++) {
			sc.print(2 + i, 1,
					String.format("%2dx%-2d", sizes[i], sizes[i]),
					sizes[i] == boardSize.x ? activeButtonColor : buttonColor);
		}
		
		// Print colors buttons
		sc.print(i + 3,1, "Colors:", backColor);
		for (int j = 0; j < colors.length - 2; j++) {
			sc.print(i + 4 + j, 1,
					String.format("  %-3d", j + 3),
					j + 3 == usedColors ? activeButtonColor : buttonColor);
		}
		
		// Print moves:
		i += colors.length + 2;
		sc.print(i + 1, 1, "Moves:", backColor);
		sc.print(i + 2, 1, "0");
		
		// Print '?' button
		sc.print(windowSize.y - 1, windowSize.x - 1, "?", buttonColor);
	}
    private void DrawTitle() {
        Color c = Color.decode("#2d98ef");
        sc.fillRect(1,12,4,1, c); // |        // Letter F
        sc.fillRect(1,12,1,2, c); // Top -
        sc.fillRect(3,12,1,2, c); // Bottom -
        sc.fillRect(1,15,4,1, c); // |        // Letter L
        sc.fillRect(4,15,1,2, c); // _
        sc.fillRect(2,18,2,1, c); // |        // Letter O
        sc.fillRect(2,20,2,1, c); //   |
	    sc.fillRect(1,19,1,1, c); // Top -
	    sc.fillRect(4,19,1,1, c); // Bottom -
	    sc.fillRect(2,22,2,1, c); // |        // Letter O
	    sc.fillRect(2,24,2,1, c); //   |
	    sc.fillRect(1,23,1,1, c); // Top -
	    sc.fillRect(4,23,1,1, c); // Bottom -
	    sc.fillRect(1,26,4,1, c); // |        // Letter D
	    sc.fillRect(1,27,1,1, c); // Top -
	    sc.fillRect(4,27,1,1, c); // Bottom -
	    sc.fillRect(2,28,2,1, c); //   |
	    sc.fillRect(1,32,4,1, c); // |        // Letter I
	    sc.fillRect(1,35,4,1, c); // |        // Letter T
	    sc.fillRect(1,34,1,3, c); // -
    }
    
    // --- EVENTS ------------------------------------------------------------------------------------------------------
    private void CellClicked(int x, int y) {
        if (debug) System.out.println(String.format("In-game. X: %-3d, Y: %-3d", x, y));

        if (board[x][y].color == board[0][0].color) { // Same color, ignore
	        if (debug) System.out.println("SAME COLOR");
        } else {
	        NextMove(x, y);
        }
    }
    private void ButtonClicked(int x, int y) {
		if (1 < y && y < 2 + sizes.length) {
			NewGame(sizes[y - 2], usedColors);
		} else if (sizes.length + 3 < y && y < sizes.length + 3 + colors.length - 1) {
			NewGame(boardSize.x, y - sizes.length - 1);
		}
    }
    private void InfoButtonClicked() {
		String message = String.format(
				"Made by Mindaugas Vaitiekūnas%nIFF-7/10%nKauno technologijos universitetas%nInformatikos fakultetas");
	    JOptionPane.showMessageDialog(sc.getJFrame(), message, "Flood It", JOptionPane.INFORMATION_MESSAGE);
    }
    @Override
    public void MouseClicked(int x, int y, MouseEvent e) {
        if (x >= boardOffset.x && x < boardOffset.x + boardSize.x &&
            y >= boardOffset.y && y < boardOffset.y + boardSize.y) { // Mouse clicked in-game
            CellClicked(x - boardOffset.x, y - boardOffset.y);
        } else { // Mouse clicked elsewhere
            if (debug) System.out.println(String.format("Mouse clicked. X: %-3d, Y: %-3d", x, y));
            
            if (0 < x && x < 6) // Button clicked
            	ButtonClicked(x, y);
            else if (x == windowSize.x - 1 && y == windowSize.y - 1)
            	InfoButtonClicked();
        }
    }

}
