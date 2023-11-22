import java.awt.*;
import java.awt.event.*;
// import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
// import java.util.Calendar;
// import java.util.Timer;
// import java.util.TimerTask;


//TO DO:
//  int for keeping how many mines left?
//  Timer
//  Highscores:
//      Date collection
//      Sqlite
//  Restart
//  Input:
//      Presets?
//  Split to multiple files

public class Minesweeper {

    //unsure if good practice - isMine tied with GUI. Can make another int[][] or ArrayList<MineTile>
    private class MineTile extends JButton {
        int r;  //row
        int c;  //collumn
        boolean isChecked = false;  //for possibility of clicking again on tile
        boolean isMine = false;

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    int tileSize = 70;  //should change by monitor size?
    int numRows = 10;   //should gather input
    int numCols = 10;
    final int boardWidth = numCols * tileSize;
    final int boardHeight = numRows * tileSize;
    int mineCount = 10; //should gather input   //used for generation
    int tilesClicked = 0; //to track when to win
    boolean gameOver = false; //locks board
    
    MineTile[][] board = new MineTile[numRows][numCols];
    Random random = new Random();

    JFrame frame = new JFrame("Minesweeper v1");
    JLabel textLabel = new JLabel();    //time label
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    
    // JButton restartButton = new JButton("Restart");
    // JButton highscoresButton = new JButton("Highscores");

    

    Minesweeper() {
        //frame.setVisible(true);                 
        frame.setSize(boardWidth, boardHeight);   //resolution
        frame.setLocationRelativeTo(null);      //middle of the screen
        frame.setResizable(true);       //can be dragged on the sides
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   //if 'x'ed, calls program close on terminal too
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper: " + numRows + "x" + numCols + " with " + mineCount + " bombs");
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));
        // boardPanel.setBackground(Color.green);
        frame.add(boardPanel);

        setBoard();

        setMines();

        frame.setVisible(true);     //to see the app  //made visible after loading everything
    }

    void setBoard() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                tile.setFocusable(false);   //no interaction with keyboard
                tile.setMargin(new Insets(0, 0, 0, 0)); //no spaces
                tile.setFont(new Font("Arial", Font.BOLD, 25));

                //long one - adds mouse events
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }
                        MineTile tile = (MineTile) e.getSource(); //which tile got event(ed)

                        //left click
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText() == "") {
                                if (tile.isMine) {  //clicked on mine
                                    revealMines();
                                }
                                else {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        }
                        //middle click - BUTTON2
                        //right click
                        else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.getText() == "" && !tile.isChecked) {
                                tile.setText("F");
                            }
                            else if (tile.getText() == "F") {
                                tile.setText("");
                            }
                        }
                    } 
                });

                boardPanel.add(tile);
                
            }
        }
    }

    void setMines() {
        //mineList = new ArrayList<MineTile>();

        int mineLeft = mineCount;
        while (mineLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);

            MineTile tile = board[r][c]; 
            if (!tile.isMine) {
                tile.isMine = true;
                mineLeft -= 1;
            }
        }
    }

    void revealMines() {    //called when game is lost
        for (int r=0; r<numRows; r++) {
            for (int c=0; c<numCols; c++){
                if(board[r][c].isMine){ board[r][c].setText("B"); };
            }
        }
        
        gameOver = true;
        textLabel.setText("Game Over!");
    }

    void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {   //if in bounds of grid
            return;
        }

        MineTile tile = board[r][c];
        if (tile.isChecked) {
            return;
        }
        tile.setEnabled(false);
        tile.isChecked = true;
        tilesClicked += 1;

        int minesFound = 0;

        //checks around
        for(int tR=r-1; tR<=r+1; tR++){
            for(int tC=c-1; tC<=c+1; tC++){
                if(tR<0 || tR>=numRows || tC<0 || tC>=numCols){ continue; }    //checks if valid tile
                //if(tR==r && tC==c){ continue; } //unsure if even speeds up
                minesFound += (board[tR][tC].isMine) ? 1 : 0;  //top left
            }
        }
            

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        }
        else {  //if tile has 0 bombs around, checks surrounding tiles
            tile.setText("");

            for(int tR=r-1; tR<=r+1; tR++){
                for(int tC=c-1; tC<=c+1; tC++){
                    if(tR<0 || tR>=numRows || tC<0 || tC>=numCols){ continue; }    //checks if valid tile
                    //if(tR==r && tC==c){ continue; } //unsure if even speeds up
                    checkMine(tR, tC);  //recursive
                }
            }

        }

        if (tilesClicked == numRows * numCols - mineCount) {    //TODO?
            gameOver = true;
            textLabel.setText("Mines Cleared!");
        }
    }
}
