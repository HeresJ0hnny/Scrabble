
/**
* Main Scrabble class, runs the game and graphics
*
* @author Jonathan Shi
* @version 6/2/19
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

public class Scrabble extends JPanel implements ActionListener, MouseListener
{
    private JFrame frame;
    private Board board;
    private int numPlayers;
    private Tile[] tiles;
    private Tile picking;
    private int selected;
    private int blankSelected;
    private String gameState;
    private int skip;
    private boolean cantPlay;
    private boolean textBoxSelected;
    private String inputText;
    
    private boolean mouseDownOnTile;
    private int height;
    private int width;
    private int boxSide;
    private int numSide;
    private int[] oldCoords;
    private static final Color BG_COLOR = new Color(237,28,36);
    
    private static final ImageIcon PLAYER_ICON = new ImageIcon("resources/icons/player.png");
    private static final ImageIcon PLAY_ICON = new ImageIcon("resources/icons/play.png");
    private static final ImageIcon BAG_ICON = new ImageIcon("resources/icons/bag.png");
    private static final ImageIcon SHUFFLE_ICON = new ImageIcon("resources/icons/shuffle.png");
    private static final ImageIcon RECALL_ICON = new ImageIcon("resources/icons/recall.png");
    private static final ImageIcon SKIP_ICON = new ImageIcon("resources/icons/skip.png");
    private static final ImageIcon SCRABBLE_LOGO = new ImageIcon("resources/icons/scrabble.png");
    private static final ImageIcon SCRABBLE_ICON = new ImageIcon("resources/icons/scrabbleicon.png");
    private static final ImageIcon HOME_ICON = new ImageIcon("resources/icons/home.png");
    
    /**
     * Makes Scrabble game object
     * @param  numPlayers  the default number of players in the game (can change later)
     */
    public Scrabble(int numPlayers)
    {
        this.board = new Board(numPlayers);
        this.numPlayers = numPlayers;
        this.tiles = new Tile[7];
        oldCoords = new int[] {-100,-100};
        for (int i = 0; i < tiles.length; i++) tiles[i] = new Tile(board.letters()[i]);
        this.picking = null;
        this.selected = -1;
        this.blankSelected = -1;
        this.gameState = "start";
        this.skip = 0;
        this.cantPlay = false;
        this.textBoxSelected = false;
        this.inputText = "";
    }
    
    /**
     * Creates the window in which the gameś graphics are displayed.
     */
    public void createWindow()
    {
        this.frame = new JFrame();
        frame.setIconImage(SCRABBLE_ICON.getImage());
        setBackground(BG_COLOR);
        frame.getContentPane().add(this);
        frame.getContentPane().setPreferredSize(new Dimension(1100, 900));
        frame.setLocation(0, 0);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(new KeyListener()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (textBoxSelected)
                {
                    int keyCode = e.getKeyCode();
                    Rectangle textBox = new Rectangle(width/2+8*boxSide,height/2-boxSide,boxSide*3,boxSide/2);
                    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && inputText.length() > 0)
                    {
                        inputText = inputText.substring(0,inputText.length()-1);
                    }
                    else if (Utility.letters().substring(0,26).indexOf(("" + (char) keyCode).toLowerCase()) != -1 && inputText.length() < 30)
                    {
                        inputText += (char) keyCode;
                    }
                }
            }
            
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {}  
        }
        );
        frame.getContentPane().addMouseListener(this);
        frame.getContentPane().addMouseMotionListener(new MouseMotionListener()
        {
            public void mouseDragged(MouseEvent me)
            {
                if (gameState.equals("playing"))
                {
                    int x = me.getX();
                    int y = me.getY();
                    //System.out.println(x + " " + y);
                    int boxSide = (int)(0.8 * Math.min(width,height)) / board.getBoard().length;
                    if (selected > -1 && tiles[selected].mouseIn(oldCoords[0]+1,oldCoords[1]+1,boxSide))
                    {
                        tiles[selected].moveTo(x+1-boxSide/2,y+1-boxSide/2);
                        oldCoords = new int[] {x,y};
                    }
                    else
                    {
                        for (Tile tile : tiles)
                        {
                            if (tile.mouseIn(oldCoords[0]+1,oldCoords[1]+1,boxSide))
                            {
                                //System.out.println(tile.letter());
                                tile.moveTo(x+1-boxSide/2,y+1-boxSide/2);
                                oldCoords = new int[] {x,y};
                                //tile.draw(g,x,y,boxSide * board.getBoard().length,boxSide);
                                break;
                            }
                        }
                    }
                }
                repaint();
            }
            
            public void mouseMoved(MouseEvent me) {}
        });
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Draws most of the graphics of the game.
     * @param  g  the Graphics object
     */
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        this.height = frame.getContentPane().getHeight();
        this.width = frame.getContentPane().getWidth();
        this.boxSide = (int)(0.8 * Math.min(width,height)) / board.getBoard().length;
        this.numSide = board.getBoard().length;
        int side = boxSide * numSide;
        if (gameState.equals("start"))
        {
            g.drawImage(Utility.getScaledImage(SCRABBLE_LOGO.getImage(),side*3/2,side*9/8),(width-side*3/2)/2,(height-side*11/8)/2,null);
            g.setColor(new Color(255, 157, 0));
            Rectangle newGame = new Rectangle(width/2-3*boxSide,height/2 + boxSide*3,boxSide*6,boxSide);
            Rectangle loadGame = new Rectangle(width/2-3*boxSide,height/2 + boxSide*9/2,boxSide*6,boxSide);
            g.fillRect(newGame.x,newGame.y,newGame.width,newGame.height);
            g.fillRect(loadGame.x,loadGame.y,loadGame.width,loadGame.height);
            g.setColor(new Color(128, 66, 0));
            g.drawRect(newGame.x,newGame.y,newGame.width,newGame.height);
            g.drawRect(loadGame.x,loadGame.y,loadGame.width,loadGame.height);
            g.setColor(new Color(255, 242, 230));
            Utility.centeredString(g,"New Game",newGame,new Font("SansSerif",Font.BOLD,boxSide/3*2));
            Utility.centeredString(g,"Load Game",loadGame,new Font("SansSerif",Font.BOLD,boxSide/3*2));
        }
        else if (gameState.equals("newSettings"))
        {
            g.setColor(Color.WHITE);
            Rectangle topText = new Rectangle(width/2-boxSide*2,height/2-boxSide*5/2,boxSide*4,boxSide*3/2);
            Utility.centeredString(g,"Number of Players",topText,new Font("SansSerif",Font.BOLD,boxSide));
            Rectangle rectPlayers = new Rectangle(width/2-boxSide,height/2-boxSide,boxSide*2,boxSide*2);
            Rectangle lessPlayers = new Rectangle(width/2-boxSide*2,height/2-boxSide,boxSide/2,boxSide*2);
            Rectangle morePlayers = new Rectangle(width/2+boxSide*3/2,height/2-boxSide,boxSide/2,boxSide*2);
            Rectangle select = new Rectangle(width/2-boxSide*3/2,height/2+boxSide*3/2,boxSide*3,boxSide);
            g.fillRect(rectPlayers.x,rectPlayers.y,rectPlayers.width,rectPlayers.height);
            g.setColor(new Color(255, 157, 0));
            g.fillRect(lessPlayers.x,lessPlayers.y,lessPlayers.width,lessPlayers.height);
            g.fillRect(morePlayers.x,morePlayers.y,morePlayers.width,morePlayers.height);
            g.fillRect(select.x,select.y,select.width,select.height);
            g.setColor(new Color(128, 66, 0));
            g.drawRect(lessPlayers.x-1,lessPlayers.y,lessPlayers.width,lessPlayers.height);
            g.drawRect(morePlayers.x-1,morePlayers.y,morePlayers.width,morePlayers.height);
            g.drawRect(select.x,select.y,select.width,select.height);
            g.setColor(new Color(139,0,0));
            g.drawRect(rectPlayers.x,rectPlayers.y,rectPlayers.width-1,rectPlayers.height-1);
            Utility.centeredString(g,""+numPlayers,rectPlayers,new Font("SansSerif",Font.BOLD,boxSide*3/2));
            g.setColor(new Color(255, 242, 230));
            Utility.centeredString(g,"-",lessPlayers,new Font("SansSerif",Font.PLAIN,boxSide*2/3));
            Utility.centeredString(g,"+",morePlayers,new Font("SansSerif",Font.PLAIN,boxSide*2/3));
            Utility.centeredString(g,"Play",select,new Font("SansSerif",Font.BOLD,boxSide*2/3));
        }
        else
        {
            for (int i = 0; i < tiles.length; i++)
            {
                int[] rc = tiles[i].getRC();
                if (!tiles[i].onRack() && rc[0] != -1)
                {
                    int newX = (width - side) / 2 + rc[1] * boxSide;
                    int newY = (height - side) / 2 + rc[0] * boxSide;
                    tiles[i].moveTo(newX,newY);
                }
            }
            drawBoard(g);
            drawRack(g);
            int alpha = 255;
            if (gameState.equals("bag")) alpha = 128;
            g.setColor(new Color(255, 157, 0, alpha));
            g.fillRect((width - (side * 7 / numSide)) / 2 + 16 * boxSide / 2,(height + side + boxSide) / 2,boxSide,boxSide);
            g.setColor(new Color(255, 157, 0));
            g.fillRect((width - (side * 7 / numSide)) / 2 + 19 * boxSide / 2,(height + side + boxSide) / 2,boxSide,boxSide);
            g.fillRect((width - (side * 7 / numSide)) / 2 + 22 * boxSide / 2,(height + side + boxSide) / 2,boxSide,boxSide);
            g.fillRect((width - (side * 7 / numSide)) / 2 - 7 * boxSide / 2,(height + side + boxSide) / 2,boxSide,boxSide);
            g.fillRect((width - (side * 7 / numSide)) / 2 - 2 * boxSide,(height + side + boxSide) / 2,boxSide,boxSide);
            
            g.setColor(new Color(128, 66, 0));
            g.drawRect((width - (side * 7 / numSide)) / 2 + 16 * boxSide / 2-1,(height + side + boxSide) / 2-1,boxSide+1,boxSide+1);
            g.drawRect((width - (side * 7 / numSide)) / 2 + 19 * boxSide / 2-1,(height + side + boxSide) / 2-1,boxSide+1,boxSide+1);
            g.drawRect((width - (side * 7 / numSide)) / 2 + 22 * boxSide / 2-1,(height + side + boxSide) / 2-1,boxSide+1,boxSide+1);
            g.drawRect((width - (side * 7 / numSide)) / 2 - 7 * boxSide / 2-1,(height + side + boxSide) / 2-1,boxSide+1,boxSide+1);
            g.drawRect((width - (side * 7 / numSide)) / 2 - 2 * boxSide-1,(height + side + boxSide) / 2-1,boxSide+1,boxSide+1);
            
            g.setColor(Color.WHITE);
            Utility.centeredString(g,"" + board.tilesLeft(),
                new Rectangle((width - (side * 7 / numSide)) / 2 + 16 * boxSide / 2 + boxSide*3/4,(height + side + boxSide) / 2 + boxSide*3/4,boxSide/3,boxSide/3),
                new Font("SansSerif",Font.BOLD,boxSide*3/10));
            
            g.drawImage(Utility.getScaledImage(PLAY_ICON.getImage(),boxSide,boxSide),(width - (side * 7 / numSide)) / 2 + 22 * boxSide / 2-1,(height + side + boxSide) / 2,null);
            g.drawImage(Utility.getScaledImage(BAG_ICON.getImage(),boxSide,boxSide),(width - (side * 7 / numSide)) / 2 + 16 * boxSide / 2+1,(height + side + boxSide) / 2,null);
            g.drawImage(Utility.getScaledImage(SHUFFLE_ICON.getImage(),boxSide,boxSide),(width - (side * 7 / numSide)) / 2 - 2 * boxSide+1,(height + side + boxSide) / 2,null);
            g.drawImage(Utility.getScaledImage(RECALL_ICON.getImage(),boxSide,boxSide),(width - (side * 7 / numSide)) / 2 - 7 * boxSide / 2,(height + side + boxSide) / 2,null);
            g.drawImage(Utility.getScaledImage(SKIP_ICON.getImage(),boxSide,boxSide),(width - (side * 7 / numSide)) / 2 + 19 * boxSide / 2,(height + side + boxSide) / 2,null);
            
            if (cantPlay)
            {
                g.setColor(new Color(255,0,0,100));
                g.fillRect((width - (side * 7 / numSide)) / 2 + 22 * boxSide / 2,(height + side + boxSide) / 2,boxSide,boxSide);
            }
            
            if (inputText.length() == 0) g.setColor(Color.WHITE);
            else if (Utility.isWord(inputText)) g.setColor(new Color(152, 230, 152));
            else g.setColor(new Color(255, 153, 153));
            Rectangle textBox = new Rectangle(width/2+8*boxSide,height/2-boxSide,boxSide*3,boxSide/2);
            g.fillRect(textBox.x,textBox.y,textBox.width,textBox.height);
            if (!textBoxSelected) g.setColor(Color.GRAY);
            else g.setColor(Color.BLUE);
            g.drawRect(textBox.x-1,textBox.y-1,textBox.width+1,textBox.height+1);
            Utility.centeredString(g,inputText.substring(Math.max(0,inputText.length()-12),inputText.length()),textBox,new Font("SansSerif",Font.BOLD,textBox.height*2/3));
            g.setColor(Color.GRAY);
            if (inputText.length() == 0 && !textBoxSelected) Utility.centeredString(g,"Dictionary",textBox,new Font("SansSerif",Font.BOLD,textBox.height/2));
            
            g.setColor(new Color(255, 242, 230, 64));
            g.fillRect(0, 0, width, (height - side - boxSide) / 2);
            g.setColor(new Color(255, 242, 230));
            int numPlayers = board.players().size();
            //numPlayers = 4;
            //g.fillRect(width/2 - (numPlayers * (3*boxSide) - boxSide)/2,boxSide/4,numPlayers * (3*boxSide) - boxSide, boxSide);
            for (int i = 0; i < numPlayers; i++)
            {
                g.setColor(Color.BLACK);
                g.drawRect(width/2 - (numPlayers * (3*boxSide) - boxSide)/2 + i * (3*boxSide) - 1,boxSide/4-1,(2*boxSide)+1, boxSide+1);
                g.setColor(new Color(255, 242, 230));
                g.fillRect(width/2 - (numPlayers * (3*boxSide) - boxSide)/2 + i * (3*boxSide),boxSide/4,2*boxSide, boxSide);
                g.drawImage(Utility.getScaledImage(PLAYER_ICON.getImage(),boxSide-2,boxSide-1),width/2 - (numPlayers * (3*boxSide) - boxSide)/2 + i * 3*boxSide,boxSide/4+1,null);
                if (board.turn() == i) g.setColor(new Color(255,0,0));
                Utility.centeredString(g,""+(i+1),new Rectangle(width/2 - (numPlayers * (3*boxSide) - boxSide)/2 + i * 3*boxSide-1,boxSide/4+2,boxSide,boxSide/2),new Font("SansSerif",Font.BOLD,boxSide/2));
                g.setColor(new Color(51, 26, 0));
                Utility.centeredString(g,""+board.score(i),new Rectangle(width/2 - (numPlayers * (3*boxSide) - boxSide)/2 + i * 3*boxSide + boxSide,boxSide/4,boxSide, boxSide),new Font("SansSerif",Font.BOLD,boxSide/2));
            }
            
            //g.drawString("" + board.turn(),10,10);
            if (gameState.equals("transition"))
            {
                g.setColor(new Color(243, 89, 94));
                g.fillRect(-100,-100,width + 100,height + 100);
                g.setColor(new Color(255, 242, 230));
                Utility.centeredString(g,"Switch to Player " + (board.turn() + 1),new Rectangle(0,0,width,height),new Font("SansSerif",Font.BOLD,boxSide));
            }
            else if (gameState.equals("blank"))
            {
                g.setColor(new Color(0,0,0,50));
                g.fillRect(-100,-100,width + 100,height + 100);
                g.setColor(new Color(243, 89, 94));
                g.fillRect(width/2-2*boxSide,height/2-2*boxSide,4*boxSide,4*boxSide);
                if (picking == null)
                {
                    picking = new Tile('_');
                    picking.setBlank(tiles[blankSelected].getBlank());
                    picking.showBlank(true);
                }
                picking.draw(g,width/2-boxSide*3/4,height/2-boxSide*3/4,numSide,boxSide*3/2);
                g.setColor(new Color(255, 157, 0));
                Rectangle left = new Rectangle(width/2-boxSide*3/2,height/2-boxSide*3/4+2,boxSide/2,boxSide*3/2-3);
                Rectangle right = new Rectangle(width/2+boxSide+1,height/2-boxSide*3/4+2,boxSide/2,boxSide*3/2-3);
                g.fillRect(left.x,left.y,left.width,left.height);
                g.fillRect(right.x,right.y,right.width,right.height);
                g.setColor(new Color(255,242,230));
                Utility.centeredString(g,"\u2039",left,new Font("SansSerif",Font.BOLD,boxSide/2));
                Utility.centeredString(g,"\u203A",right,new Font("SansSerif",Font.BOLD,boxSide/2));
                g.setColor(new Color(128, 66, 0));
                g.drawRect(width/2-2*boxSide-1,height/2-2*boxSide-1,4*boxSide+1,4*boxSide+1);
                g.drawRect(left.x-1,left.y-1,left.width+1,left.height+1);
                g.drawRect(right.x-1,right.y-1,right.width+1,right.height+1);
            }
            else if (gameState.equals("endgame"))
            {
                g.setColor(new Color(243, 89, 94,200));
                g.fillRect(-100,-100,width + 100,height + 100);
                Rectangle center = new Rectangle(width/4,height/4-boxSide*2,width/2,height/2);
                g.setColor(new Color(255, 242, 230));
                String finalText;
                ArrayList<Integer> winners = board.winner();
                if (winners.size() == 1)
                {
                    finalText = "Player " + (winners.get(0) + 1) + " wins!";
                }
                else
                {
                    finalText = "Players ";
                    for (Integer num : winners)
                    {
                        finalText += (num + 1) + ", ";
                    }
                    finalText = finalText.substring(0,finalText.length()-2) + " win!";
                }
                Utility.centeredString(g,finalText,center,new Font("SansSerif",Font.BOLD,boxSide * 2));
                g.setColor(new Color(255, 157, 0));
                Rectangle viewBoard = new Rectangle(width/2-boxSide*3/2-1,height/2+boxSide*2,boxSide*3+2,boxSide);
                Rectangle end = new Rectangle(width/2-boxSide*3/2-1,height/2+boxSide*7/2,boxSide*3+2,boxSide);
                g.fillRect(viewBoard.x,viewBoard.y,viewBoard.width,viewBoard.height);
                g.fillRect(end.x,end.y,end.width,end.height);
                g.setColor(new Color(128, 66, 0));
                g.drawRect(viewBoard.x,viewBoard.y,viewBoard.width,viewBoard.height);
                g.drawRect(end.x,end.y,end.width,end.height);
                g.setColor(Color.WHITE);
                Utility.centeredString(g,"View Board",viewBoard,new Font("SansSerif",Font.BOLD,boxSide/2));
                Utility.centeredString(g,"Back to Main",end,new Font("SansSerif",Font.BOLD,boxSide*2/5));
            }
        }
        if (gameState.equals("newSettings") || gameState.equals("playing"))
        {
            g.setColor(new Color(255, 157, 0));
            Rectangle back = new Rectangle(boxSide/4,boxSide/6,boxSide,boxSide);
            g.fillRect(back.x,back.y,back.width,back.height);
            g.setColor(new Color(128, 66, 0));
            g.drawRect(back.x,back.y,back.width,back.height);
            g.drawImage(Utility.getScaledImage(HOME_ICON.getImage(),boxSide,boxSide),back.x,back.y,null);
        }
    }
    
    /**
     * Draws the board and the tiles on it.
     * @param  g  the Graphics object
     */
    public void drawBoard(Graphics g)
    {
        char[][] brd = board.getBoard();
        int side = boxSide * brd.length;
        Color color;
        String text;
        for (int r = 0; r < brd.length; r++)
        {
            for (int c = 0; c < brd[r].length; c++)
            {
                int size = (int) (boxSide * 0.4);
                if (!Character.isLetter(brd[r][c]) && brd[r][c] != '_')
                {
                    switch (brd[r][c])
                    {
                        case '+':
                            color = new Color(51, 153, 255);
                            text = "2L";
                            break;
                        case '*':
                            color = new Color(51, 204, 0);
                            text = "3L";
                            break;
                        case '2':
                            color = new Color(255, 51, 0);
                            text = "2W";
                            break;
                        case '3':
                            color = new Color(255, 163, 26);
                            text = "3W";
                            break;
                        case '@':
                            color = new Color(230, 46, 0);
                            text = "\u2605";
                            size = (int) (boxSide * 0.7);
                            break;
                        default:
                            color = new Color(255, 242, 230);
                            text = "";
                            break;
                    }
                    Rectangle rect = new Rectangle((width - side) / 2 + c * boxSide + 1,(height - side) / 2 + r * boxSide + 1,boxSide - 1,boxSide - 1);
                    g.setColor(color);
                    g.fillRect(rect.x,rect.y,boxSide - 1,boxSide - 1);
                    g.setColor(new Color(255, 242, 230));
                    Font font = new Font("SansSerif",Font.BOLD,size);
                    if (text.length() > 0)  Utility.centeredString(g,text,rect,font);
                }
                else
                {
                    Rectangle rect = new Rectangle((width - side) / 2 + c * boxSide + 1,(height - side) / 2 + r * boxSide + 1,boxSide - 1,boxSide - 1);
                    g.setColor(new Color(255, 206, 153));
                    g.fillRect(rect.x,rect.y,boxSide - 1,boxSide - 1);
                    if (Character.toUpperCase(brd[r][c]) != brd[r][c])
                    {
                        g.setColor(new Color(51, 26, 0));
                        Rectangle smallRect = new Rectangle((width - side) / 2 + c * boxSide + boxSide * 2 / 3 + 1, (height - side) / 2 + r * boxSide + boxSide * 3 / 5 + 1,boxSide / 3 - 2,boxSide * 2 / 5 - 2);
                        Utility.centeredString(g,"" + Utility.points()[Utility.letters().indexOf(brd[r][c])],smallRect,new Font("SansSerif",Font.BOLD,boxSide * 3 / 10));                    
                        Utility.centeredString(g,("" + brd[r][c]).toUpperCase(),rect,new Font("SansSerif",Font.BOLD,boxSide * 3 / 5));
                    }
                    else
                    {
                        g.setColor(new Color(102, 179, 255));
                        Utility.centeredString(g,("" + brd[r][c]).toUpperCase(),rect,new Font("SansSerif",Font.BOLD,boxSide * 3 / 5));
                    }
                }
            }
        }
        g.setColor(Color.BLACK);
        g.drawRect((width - side) / 2,(height - side) / 2,side,side);
        for (int x = (width - side) / 2 + boxSide; x < (width + side) / 2; x += boxSide) g.drawLine(x,(height - side) / 2,x,(height + side) / 2);
        for (int y = (height - side) / 2 + boxSide; y < (height + side) / 2; y += boxSide) g.drawLine((width - side) / 2,y,(width + side) / 2,y);
        repaint();
    }
    
    /**
     * Draws the rack/hand with the unplayed tiles.
     * @param  g  the Graphics object
     */
    public void drawRack(Graphics g)
    {
        int side = boxSide * numSide;
        //char[] letters = board.letters();
        g.setColor(new Color(128, 66, 0));
        g.drawRect((width - (side * 7 / numSide)) / 2,(height + side + boxSide) / 2,7 * boxSide,boxSide);
        for (int i = 1; i < 7; i++)
        {
            int x = (width - (side * 7 / numSide)) / 2 + i * boxSide;
            g.drawLine(x,(height + side + boxSide) / 2, x,(height + side + 3 * boxSide) / 2);
        }
        for (int i = 0; i < tiles.length; i++)
        {
            if (tiles[i].onRack() && i != selected)
            {
                //System.out.println("" + tiles[i].x() + "" + tiles[i].y());
                //System.out.println(tiles[i].letter());
                tiles[i].draw(g,(width - (side * 7 / numSide)) / 2 + i * boxSide,(height + side + boxSide) / 2,numSide,boxSide);
            }
            else if (i != selected)
                tiles[i].draw(g,tiles[i].x(),tiles[i].y(),numSide,boxSide);
            //drawTile(g,tiles[i],(width - (side * 7 / numSide)) / 2 + i * boxSide + 1,(height + side + boxSide) / 2 + 1);
        }
        if (selected > -1)
        {
            tiles[selected].draw(g,tiles[selected].x(),tiles[selected].y(),numSide,boxSide);
            //System.out.println(tiles[selected].letter());
        }
        repaint();
    }
    
    /**
     * Tries to execute a player's turn.
     * @return whether the turn was successful/valid or not
     */
    public boolean playTiles()
    {
        ArrayList<Tile> wordTiles = new ArrayList<Tile>();
        for (Tile t : tiles) if (!t.onRack()) wordTiles.add(t);
        boolean noTiles = true;
        int[] startRC = new int[2];
        for (int r = 0; r < numSide; r++)
        {
            for (int c = 0; c < numSide; c++)
            {
                if (Character.isLetter(board.getBoard()[r][c])) noTiles = false;
                if (board.getBoard()[r][c] == '@') startRC = new int[] {r,c};
            }
        }
        if (wordTiles.size() > 0)
        {
            int[] firstRC = wordTiles.get(0).getRC();
            boolean inALine = true;
            for (int i = 0; i < wordTiles.size(); i++)
            {
                int[] rc = wordTiles.get(i).getRC();
                if (rc[0] != firstRC[0] && rc[1] != firstRC[1])
                {
                    inALine = false;
                    break;
                }
            }
            if (inALine)
            {
                boolean bordersExisting = false;
                int row = firstRC[0];
                int column = firstRC[1];
                int change;
                boolean after = true;
                if (wordTiles.size() > 1) 
                {
                    int[] nextRC = wordTiles.get(1).getRC();
                    if (row == nextRC[0]) change = 1;
                    else change = 0;
                }
                else
                {
                    if ((column + 1 < board.getBoard().length &&
                        Character.isLetter(board.getBoard()[row][column + 1])))
                    {
                        change = 1;
                    }
                    else if (column - 1 > 0 &&
                    Character.isLetter(board.getBoard()[row][column - 1]))
                    {
                        change = 1;
                        after = false;
                    }
                    else if ((row + 1 < board.getBoard().length &&
                        Character.isLetter(board.getBoard()[row + 1][column])))
                    {
                        change = 0;
                    }
                    else if (row - 1 > 0 &&
                    Character.isLetter(board.getBoard()[row - 1][column]))
                    {
                        change = 0;
                        after = false;
                    }
                    else return false;
                }
                ArrayList<String> words = new ArrayList<String>();
                String majorWord = "";
                boolean done = false;
                boolean valid = false;
                int used = 0;
                while (!done)
                {
                    boolean isTile = false;
                    for (Tile t : wordTiles) 
                    {
                        int[] tileRC = t.getRC();
                        char letter = t.letter();
                        if (letter == '_') letter = Character.toUpperCase(t.getBlank());
                        if (tileRC[0] == row && tileRC[1] == column)
                        {
                            if (after) majorWord += "" + board.getBoard()[row][column] + letter;
                            else majorWord = "" + board.getBoard()[row][column] + letter + majorWord;
                            used++;
                            isTile = true;
                        }
                        if (noTiles && tileRC[0] == startRC[0] && tileRC[1] == startRC[1])
                        {
                            bordersExisting = true;
                        }
                    }
                    char nextLetter = board.getBoard()[row][column];
                    if (Character.isLetter(nextLetter))
                    {
                        if (after) majorWord += nextLetter;
                        else majorWord = nextLetter + majorWord;
                        bordersExisting = true;
                    }
                    else if (!isTile)
                    {
                        if (after)
                        {
                            if (change == 1) column = firstRC[1];
                            else row = firstRC[0];
                            after = false;
                        }
                        else
                        {
                            if (Utility.onlyLetters(majorWord).length() > 1 && used == wordTiles.size())
                            {
                                valid = true;
                                majorWord = majorWord.replace(".","");
                                words.add(majorWord);
                                //System.out.println(majorWord);
                            }
                            done = true;
                        }
                    }
                    if (after)
                    {
                        if (change == 1) column++;
                        else row++;
                    }
                    else
                    {
                        if (change == 1) column--;
                        else row--;
                    }
                    if (column >= numSide || row >= numSide)
                    {
                        if (change == 1) column = firstRC[1];
                        else row = firstRC[0];
                        after = false;
                        if (change == 1) column--;
                        else row--;
                    }
                    else if (!done && (column < 0 || row < 0))
                    {
                        if (Utility.onlyLetters(majorWord).length() > 1 && used == wordTiles.size())
                        {
                            valid = true;
                            majorWord = majorWord.replace(".","");
                            words.add(majorWord);
                            //System.out.println(majorWord);
                        }
                        done = true;
                    }
                }
                for (Tile t : wordTiles)
                { 
                    after = true;
                    done = false;
                    int[] rc = t.getRC();
                    row = rc[0];
                    column = rc[1];
                    char letter = t.letter();
                    if (letter == '_') letter = Character.toUpperCase(t.getBlank());
                    String minorWord = "" + board.getBoard()[row][column] + letter;
                    if (change == 1) row++;
                    else column++;
                    if (column >= numSide || row >= numSide)
                    {
                        if (change == 1) row = rc[0] - 1;
                        else column = rc[1] - 1;
                        after = false;
                    }
                    while (!done)
                    {
                        char nextLetter = board.getBoard()[row][column];
                        if (Character.isLetter(nextLetter))
                        {
                            if (after) minorWord += nextLetter;
                            else minorWord = nextLetter + minorWord;
                            bordersExisting = true;
                        }
                        else
                        {
                            if (after || column >= numSide || row >= numSide)
                            {
                                if (change == 1) row = rc[0];
                                else column = rc[1];
                                after = false;
                            }
                            else
                            {
                                if (Utility.onlyLetters(minorWord).length() > 1 && used == wordTiles.size())
                                {
                                    minorWord = minorWord.replace(".","");
                                    words.add(minorWord);
                                    //System.out.println(minorWord);
                                }
                                done = true;
                            }
                        }
                        if (after)
                        {
                            if (change == 1) row++;
                            else column++;
                        }
                        else
                        {
                            if (change == 1) row--;
                            else column--;
                        }
                        if (column >= numSide || row >= numSide)
                        {
                            if (change == 1) row = rc[0];
                            else column = rc[1];
                            after = false;
                        }
                        else if (!done && (column < 0 || row < 0))
                        {
                            if (Utility.onlyLetters(minorWord).length() > 1 && used == wordTiles.size())
                            {
                                minorWord = minorWord.replace(".","");
                                words.add(minorWord);
                                //System.out.println(minorWord);
                            }
                            done = true;
                        }
                    }
                }
                boolean allLegal = true;
                ArrayList<String> notWords = new ArrayList<String>();
                for (String word : words)
                {
                    if (!Utility.isWord(Utility.onlyLetters(word)))
                    {
                        allLegal = false;
                        notWords.add(Utility.onlyLetters(word));
                    }
                }
                if (!allLegal)
                {
                    String message = "";
                    for (String word : notWords)
                    {
                        message += word + " ";
                    }
                    //System.out.println("Not words: " + message.toLowerCase());
                }
                else if (valid && bordersExisting && allLegal)
                {
                    board.playWords(words);
                    char[] chars = new char[tiles.length];
                    boolean bingo = true;
                    for (int i = 0; i < tiles.length; i++)
                    {
                        if (!tiles[i].onRack())
                        {
                            char letter = tiles[i].letter();
                            if (letter == '_') letter = Character.toUpperCase(tiles[i].getBlank());
                            board.setTile(letter,tiles[i].getRC()[0],tiles[i].getRC()[1]);
                            chars[i] = '.';
                        }
                        else
                        {
                            chars[i] = tiles[i].letter();
                            bingo = false;
                        }
                    }
                    //for (char c : chars) System.out.print("" + c);
                    //System.out.println();
                    if (bingo) board.bingo();
                    board.drawTiles(chars);
                    //System.out.println("working");
                    //for (int i = 0; i < tiles.length; i++) tiles[i] = new Tile(board.letters()[i]);
                    //not necessary for turn changes
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Saves the game layout/state.
     * @throws IOException
     */
    public void save() throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter("saves/game.txt"));
        String bagString = "[ ";
        for (int num : board.getBag()) bagString += num + " ";
        bagString += "]";
        String[] text = new String[] {"players: " + board.numPlayers(),"skip: " + skip,"turn: " + board.turn(),"gameState: " + gameState,"bag: " + bagString};
        String fileContent = "";
        for (String str : text)
        {
            fileContent += str + System.getProperty("line.separator");
        }
        for (char[] row : board.getBoard())
        {
            fileContent += "[ ";
            for (char c : row) fileContent += c + " ";
            fileContent += "]" + System.getProperty("line.separator");
        }
        for (int i = 1; i <= board.numPlayers(); i++)
        {
            fileContent += "player " + i + " | score: " + board.players().get(i-1).getScore() + ", hand: [ ";
            for (char c : board.players().get(i-1).getLetters()) fileContent += c + " ";
            fileContent += "]" + System.getProperty("line.separator");
        }
        writer.write(fileContent);
        writer.close();
    }
    
    /**
     * Loads a game from the save.
     * @throws IOException
     */
    public void loadGame() throws IOException
    {
        newGame();
        Scanner loader = new Scanner(new File("saves/game.txt"));
        int boardRow = 0;
        while (loader.hasNextLine())
        {
            String line = loader.nextLine();
            if (line.substring(0,7).equals("players"))
            {
                this.board = new Board(Integer.parseInt(line.substring(9)));
            }
            else if (line.substring(0,1).equals("["))
            {
                this.board.setRow(boardRow,line.substring(2,line.indexOf("]")-1).split(" "));
                boardRow++;
            }
            else if (line.substring(0,4).equals("skip"))
            {
                this.skip = Integer.parseInt(line.substring(6));
            }
            else if (line.substring(0,4).equals("turn"))
            {
                this.board.setTurn(Integer.parseInt(line.substring(6)));
            }
            else if (line.substring(0,9).equals("gameState"))
            {
                this.gameState = line.substring(11);
            }
            else if (line.substring(0,3).equals("bag"))
            {
                this.board.setBag(line.substring(7,line.indexOf("]")-1).split(" "));
            }
            else if (line.substring(0,7).equals("player "))
            {
                int index = Integer.parseInt(line.substring(7,line.indexOf(" ",8)))-1;
                board.players().get(index).setScore(Integer.parseInt(line.substring(line.indexOf("score: ") + 7,line.indexOf(","))));
                board.players().get(index).setLetters(line.substring(line.indexOf("hand: ") + 8,line.indexOf("]")-1).split(" "));
                for (int i = 0; i < tiles.length; i++)
                {
                    if (board.letters()[i] != '.') tiles[i] = new Tile(board.letters()[i]);
                    else tiles[i] = new Tile();
                }
            }
        }
        if (gameState.equals("playing")) gameState = "transition";
        repaint();
    }
    
    
    /**
     * Initializes the settings for a new game.
     */
    public void newGame()
    {
        this.board = new Board(numPlayers);
        this.numPlayers = numPlayers;
        oldCoords = new int[] {-100,-100};
        for (int i = 0; i < tiles.length; i++) tiles[i] = new Tile(board.letters()[i]);
        this.picking = null;
        this.selected = -1;
        this.blankSelected = -1;
        this.skip = 0;
        this.textBoxSelected = false;
        this.inputText = "";
    }
    
    /**
     * Actions for when the mouse is clicked.
     */
    public void mouseClicked(MouseEvent me)
    {
        int x = me.getX();
        int y = me.getY();
        //Rectangle back = new Rectangle(boxSide/4,boxSide/6,boxSide,boxSide);
        if ((gameState.equals("newSettings") || gameState.equals("playing")) && me.getClickCount() >= 2)
        {
            if (x > boxSide/4 && //back
                y > boxSide/6 &&
                x < boxSide/4 + boxSide &&
                y < boxSide/6 + boxSide)
            {
                try
                {
                    if (gameState.equals("playing")) save();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                gameState = "start";
            }
        }
        if (gameState.equals("start") && me.getClickCount() >= 2)
        {
            //Rectangle newGame = new Rectangle(width/2-3*boxSide,height/2 + boxSide*3,boxSide*6,boxSide);
            if (x > width/2-3*boxSide && //newGame
                y > height/2 + boxSide*3 &&
                x < width/2-3*boxSide + boxSide*6 &&
                y < height/2 + boxSide*3 + boxSide)
            {
                gameState = "newSettings";
            }
            //Rectangle loadGame = new Rectangle(width/2-3*boxSide,height/2 + boxSide*9/2,boxSide*6,boxSide);
            else if (x > width/2-3*boxSide && //loadGame
                     y > height/2 + boxSide*9/2 &&
                     x < width/2-3*boxSide + boxSide*6 &&
                     y < height/2 + boxSide*9/2 + boxSide)
            {
                try
                {
                    loadGame();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else if (gameState.equals("newSettings"))
        {
            //Rectangle lessPlayers = new Rectangle(width/2-boxSide*2,height/2-boxSide,boxSide/2,boxSide*2);
            //Rectangle morePlayers = new Rectangle(width/2+boxSide*3/2,height/2-boxSide,boxSide/2,boxSide*2);
            newGame();
            if (x > width/2-boxSide*2 && //lessPlayers
                y > height/2-boxSide &&
                x < width/2-boxSide*2 + boxSide/2 &&
                y < height/2-boxSide + boxSide*2)
            {
                numPlayers = Math.max(2,numPlayers-1);
            }
            else if (x > width/2+boxSide*3/2 && //morePlayers
                    y > height/2-boxSide &&
                    x < width/2+boxSide*3/2 + boxSide/2 &&
                    y < height/2-boxSide + boxSide*2)
            {
                numPlayers = Math.min(8,numPlayers+1);
            }
            //Rectangle select = new Rectangle(width/2-boxSide*3/2,height/2+boxSide*3/2,boxSide*3,boxSide);
            else if (x > width/2-boxSide*3/2 && //select
                    y > height/2+boxSide*3/2 &&
                    x < width/2-boxSide*3/2 + boxSide*3 &&
                    y < height/2+boxSide*3/2 + boxSide &&
                    me.getClickCount() >= 2)
            {
                board = new Board(numPlayers);
                for (int i = 0; i < tiles.length; i++) tiles[i] = new Tile(board.letters()[i]);
                newGame();
                gameState = "playing";
                try
                {
                    save();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else if (gameState.equals("endgame"))
        {
            //Rectangle viewBoard = new Rectangle(width/2-boxSide*3/2-1,height/2+boxSide*2,boxSide*3+2,boxSide);
            //Rectangle end = new Rectangle(width/2-boxSide*3/2-1,height/2+boxSide*7/2,boxSide*3+2,boxSide);
            //TODO: implement these buttons
            if (x > width/2-boxSide*3/2-1 && //viewBoard
                y > height/2+boxSide &&
                x < width/2-boxSide*3/2-1 + boxSide*3+2 &&
                y < height/2+boxSide*2 + boxSide)
            {
                gameState = "viewBoard";
            }
            else if (x > width/2-boxSide*3/2-1 && //end
                    y > height/2+boxSide*7/2 &&
                    x < width/2-boxSide*3/2-1 + boxSide*3+2 &&
                    y < height/2+boxSide*7/2 + boxSide &&
                    me.getClickCount() >= 2)
            {
                newGame();
                gameState = "start";
            }
        }
        else if (gameState.equals("viewBoard"))
        {
            gameState = "endgame";
        }
        else
        {
            if (me.getClickCount() >= 2)
            {
                int side = boxSide * numSide;
                int tilesLeft = 0;
                for (int num : board.getBag()) tilesLeft += num;
                if (x > width/2+8*boxSide && //textBox
                    y > height/2-boxSide &&
                    x < width/2+8*boxSide + boxSide*3 &&
                    y < height/2-boxSide + boxSide/2)
                {
                    inputText = "";
                }
                else if (x > (width - (side * 7 / numSide)) / 2 + 19 * boxSide / 2 && //skip button
                    y > (height + side + boxSide) / 2 &&
                    x < (width - (side * 7 / numSide)) / 2 + 21 * boxSide / 2 &&
                    y < (height + side + 3 * boxSide) / 2)
                {
                    skip++;
                    inputText = "";
                    if (skip >= board.players().size() * 3)
                    {
                        gameState = "endgame";
                    }
                    else if (skip >= board.players().size() && tilesLeft == 0)
                    {
                        gameState = "endgame";
                    }
                    else
                    {
                        gameState = "transition";
                        board.nextTurn();
                        tiles = new Tile[7];
                        for (int i = 0; i < tiles.length; i++)
                        {
                            if (board.letters()[i] != '.')
                            {
                                tiles[i] = new Tile(board.letters()[i]);
                            }
                            else tiles[i] = new Tile();
                        }
                    }
                }
                //g.fillRect((width - (side * 7 / numSide)) / 2 + 22 * boxSide / 2,(height + side + boxSide) / 2,boxSide,boxSide);
                else if (x > (width - (side * 7 / numSide)) / 2 + 22 * boxSide / 2 && //play button
                    y > (height + side + boxSide) / 2 &&
                    x < (width - (side * 7 / numSide)) / 2 + 24 * boxSide / 2 &&
                    y < (height + side + 3 * boxSide) / 2)
                {
                    if (gameState.equals("playing"))
                    {
                        if (playTiles())
                        {
                            skip = 0;
                            inputText = "";
                            boolean noHandTiles = true;
                            for (char c : board.players().get(board.turn()).getLetters())
                            {
                                if (c != '.')
                                {
                                    noHandTiles = false;
                                    break;
                                }
                            }
                            if (!(tilesLeft == 0 && noHandTiles))
                            {    
                                //AI turn
                                board.nextTurn();
                                if (board.numPlayers() > 1) gameState = "transition";
                                tiles = new Tile[7];
                                /*if (board.players().get(board.turn()) instanceof AI)
                                {
                                    do
                                    {
                                        tiles = new Tile[7];
                                        if (board.players().get(board.turn()) instanceof AI)
                                        {
                                            tiles = board.ai();
                                        
                                        }
                                    } while (!playTiles());
                                    
                                    
                                    board.clearAICoords();
                                    board.nextTurn();
                                }
                            else
                                {*/
                                //for (char c : board.letters()) System.out.print(c + " ");
                                //System.out.println();
                                
                                for (int i = 0; i < tiles.length; i++)
                                {
                                    if (board.letters()[i] != '.')
                                    {
                                        tiles[i] = new Tile(board.letters()[i]);
                                    }
                                    else tiles[i] = new Tile();
                                }
                                //for (char let : board.letters()) System.out.print(" " + let);
                                //System.out.println();
                                //}
                            }
                            else
                            {
                                board.endScoring();
                                gameState = "endgame";
                            }
                            try
                            {
                                save();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            cantPlay = true;
                        }
                    }
                    else if (gameState.equals("bag"))
                    {
                        skip++;
                        inputText = "";
                        boolean[] replaces = new boolean[tiles.length];
                        char[] chars = new char[tiles.length];
                        for (int i = 0; i < tiles.length; i++)
                        {
                            if (tiles[i].switching()) replaces[i] = true;
                            chars[i] = tiles[i].letter();
                        }
                        int replaceCount = 0;
                        for (boolean b : replaces) if (b) replaceCount++;
                        if (replaceCount <= tilesLeft && tilesLeft > 0)
                        {
                            board.replaceTiles(chars, replaces);
                            gameState = "transition";
                            board.nextTurn();
                            board.drawTiles(board.letters());
                            for (int i = 0; i < tiles.length; i++) tiles[i] = new Tile(board.letters()[i]);
                        }
                        else
                        {
                            for (Tile t : tiles) t.setSwitching(false);
                            gameState = "playing";
                        }
                    }
                }
                else if (x > (width - (side * 7 / numSide)) / 2 - 7 * boxSide / 2 && //recall button
                    y > (height + side + boxSide) / 2 &&
                    x < (width - (side * 7 / numSide)) / 2 - 5 * boxSide / 2 &&
                    y < (height + side + 3 * boxSide) / 2 && gameState.equals("playing"))
                {
                    for (Tile t : tiles)
                    {
                        if (!t.onRack()) t.toRack();
                        t.showBlank(false);
                    }
                }
                else if (x > (width - (side * 7 / numSide)) / 2 - 2 * boxSide && //shuffle button
                    y > (height + side + boxSide) / 2 &&
                    x < (width - (side * 7 / numSide)) / 2 - boxSide &&
                    y < (height + side + 3 * boxSide) / 2 && gameState.equals("playing"))
                {
                    Collections.shuffle(Arrays.asList(tiles));
                }
                //g.fillRect((width - (side * 7 / numSide)) / 2 + 16 * boxSide / 2,(height + side + boxSide) / 2,boxSide,boxSide);
                else if (x > (width - (side * 7 / numSide)) / 2 + 16 * boxSide / 2 && //bag button
                    y > (height + side + boxSide) / 2 &&
                    x < (width - (side * 7 / numSide)) / 2 + 18 * boxSide / 2 &&
                    y < (height + side + 3 * boxSide) / 2)
                {
                    for (Tile t : tiles)
                    {
                        t.toRack();
                        t.showBlank(false);
                    }
                    if (gameState.equals("bag"))
                    {
                        gameState = "playing";
                        for (Tile t : tiles) t.setSwitching(false);
                    }
                    else if (gameState.equals("playing"))
                    {
                        gameState = "bag";
                    }
                }
            }
            else if (gameState.equals("transition"))
            {
                gameState = "playing";
                //for (int i = 0; i < tiles.length; i++) tiles[i] = new Tile(board.letters()[i]);
            }
            else if (gameState.equals("blank"))
            {
                //picking.draw(g,width/2-boxSide*3/4,height/2-boxSide*3/4,numSide,boxSide*3/2);
                if (x > width/2-boxSide*3/4 && //bag button
                    y > height/2-boxSide*3/4 &&
                    x < width/2-boxSide*3/4 + boxSide*3/2 &&
                    y < height/2-boxSide*3/4 + boxSide*3/2)
                {
                    tiles[blankSelected].setBlank(picking.getBlank());
                    tiles[blankSelected].showBlank(true);
                    picking = null;
                    blankSelected = -1;
                    gameState = "playing";
                }
            }
            else if (me.getClickCount() == 1)
            {
                if (x > width/2+8*boxSide && //textBox
                    y > height/2-boxSide &&
                    x < width/2+8*boxSide + boxSide*3 &&
                    y < height/2-boxSide + boxSide/2)
                {
                    textBoxSelected = true;
                }
            }
        }
        repaint();
    }

    /**
     * Actions for when the mouse is pressed.
     */
    public void mousePressed(MouseEvent me)
    {
        int x = me.getX();
        int y = me.getY();
        cantPlay = false;
        if (!(x > width/2+8*boxSide && //textBox
                y > height/2-boxSide &&
                x < width/2+8*boxSide + boxSide*3 &&
                y < height/2-boxSide + boxSide/2))
            textBoxSelected = false;
        if (gameState.equals("blank"))
        {
            if (x > width/2-boxSide*3/2 && //left
                y > height/2-boxSide*3/4+2 &&
                x < width/2-boxSide*3/2 + boxSide/2 &&
                y < height/2-boxSide*3/4 + boxSide*3/2-1)
            {
                picking.nextBlankLet(true);
            }
            else if (x > width/2+boxSide+1 && //right
                y > height/2-boxSide*3/4+2 &&
                x < width/2+boxSide+1 + boxSide/2 &&
                y < height/2-boxSide*3/4 + boxSide*3/2-1)
            {
                picking.nextBlankLet(false);
            }
            //g.fillRect(width/2-2*boxSide,height/2-2*boxSide,4*boxSide,4*boxSide);
            else if (x < width/2-2*boxSide || //outside
                y < height/2-2*boxSide ||
                x > width/2-2*boxSide + 4*boxSide ||
                y > height/2-2*boxSide + 4*boxSide)
            {
                tiles[blankSelected].toRack();
                gameState = "playing";
            }
            //width/2-boxSide*3/4,height/2-boxSide*3/4,numSide,boxSide*3/2
            else if (x < width/2-boxSide*3/4 || //tile
                y < height/2-boxSide*3/4 ||
                x > width/2-boxSide*3/4 + numSide ||
                y > height/2-boxSide*3/4 + boxSide*3/2)
            {
                tiles[blankSelected].setBlank(picking.getBlank());
                tiles[blankSelected].showBlank(true);
                gameState = "playing";
                blankSelected = -1;
            }
        }
        else
        {
            oldCoords = new int[] {x,y};
            //System.out.println("x: " + x + ", y: " + y);
            for (int i = 0; i < tiles.length; i++)
            {
                if (tiles[i].mouseIn(x+1,y+1,boxSide))
                {
                    tiles[i].showBlank(false);
                    if (gameState.equals("bag"))
                    {
                        tiles[i].setSwitching(!tiles[i].switching());
                    }
                    else if (gameState.equals("playing"))
                    {
                        selected = i;
                        tiles[i].setRC(-1,-1);
                        tiles[i].moveTo(x+1-boxSide/2,y+1-boxSide/2);
                        //tile.draw(g,x,y,boxSide * board.getBoard().length,boxSide);
                        break;
                    }
                }
            }
        }
        repaint();
    }
    
    /**
     * Actions for when the mouse is released.
     */
    public void mouseReleased(MouseEvent me) 
    {
        if (selected != -1)
        {
            int x = tiles[selected].x() + boxSide / 2 - 1;
            int y = tiles[selected].y() + boxSide / 2 - 1;
            int side = boxSide * numSide;
            int row = (y - (height - side) / 2) / boxSide;
            int column = (x - (width - side) / 2) / boxSide;
            tiles[selected].setRC(row,column);
            boolean sameSpace = false;
            //System.out.println(row + "," + column);
            if (row < 0 || row > board.getBoard().length - 1 ||
                column < 0 || column > board.getBoard().length - 1 ||
                Character.isLetter(board.getBoard()[row][column])) 
                    sameSpace = true;
            else
            {
                for (Tile tile : tiles)
                {
                    int[] rc = tile.getRC();
                    if (tile != tiles[selected] && rc[0] != -1 && rc[0] == row && rc[1] == column)
                        sameSpace = true;
                }
            }
            if (!sameSpace)
            {
                int newX = (width - side) / 2 + column * boxSide;
                int newY = (height - side) / 2 + row * boxSide;
                tiles[selected].moveTo(newX,newY);
                if (tiles[selected].letter() == '_')
                {
                    gameState = "blank";
                    blankSelected = selected;
                }
            }
            else
            {
                if (x > (width - (side * 7 / numSide)) / 2 &&
                y > (height + side + boxSide) / 2 &&
                x < (width - (side * 7 / numSide)) / 2 + 7 * boxSide &&
                y < (height + side + boxSide) / 2 + boxSide)
                {
                    Tile moving = tiles[selected];
                    if (tiles[column - 4].onRack()) //column - 4 only works for size 15
                    {
                        if (column - 4 < selected && column - 4 >= 0)
                        {
                            int spot = ((x + boxSide / 2)- (width - side) / 2) / boxSide - 4;
                            for (int i = selected; i > spot; i--)
                            {
                                tiles[i] = tiles[i-1];
                            }
                            tiles[spot] = moving;
                        }
                        else if (column - 4 > selected && column - 4 < numSide)
                        {
                            int spot = ((x - boxSide / 2)- (width - side) / 2) / boxSide - 4;
                            for (int i = selected; i < spot; i++)
                            {
                                tiles[i] = tiles[i+1];
                            }
                            tiles[spot] = moving;
                        }
                    }
                    else
                    {
                        tiles[selected] = tiles[column - 4];
                        tiles[column - 4] = moving;
                    }
                    moving.toRack();
                }
                else
                {
                    tiles[selected].toRack();
                }
            }
            selected = -1;
        }
        repaint();
    }
    
    public static void main(String[] args)
    {
        Scrabble scrabble = new Scrabble(2);
        scrabble.createWindow();
        /*Scanner scan = new Scanner(System.in);
        System.out.println("Scrabble Dictionary\n----------------------------");
        System.out.println("Enter a word (-1 to quit): ");
        while (scan.hasNextLine())
        {
            String word = scan.nextLine();
            if (!word.equals("-1"))
            {
                System.out.println(word + ": " + Utility.isWord(word));
                System.out.println("\nEnter a word (-1 to quit): ");
            }
        }*/
    }
    
    public void actionPerformed(ActionEvent me) {}
    public void mouseEntered(MouseEvent me) {}
    public void mouseExited(MouseEvent me) {} 
}
