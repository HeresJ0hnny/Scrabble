
/**
 * A unplayed Scrabble tile.
 *
 * @author Jonathan Shi
 * @version 6/2/19
 */
import java.awt.*;
public class Tile
{
    private char letter;
    private char blankLet;
    private boolean showBlankLetter;
    private int points;
    private int x;
    private int y;
    private int row;
    private int column;
    private boolean onRack;
    private boolean switching;
    
    /**
     * The absence of a tile.
     */
    public Tile()
    {
        this.letter = '.';
        this.blankLet = '.';
        this.showBlankLetter = false;
        this.points = -1;
        this.x = -100;
        this.y = -100;
        this.row = -1;
        this.column = -1;
        this.onRack = true;
        this.switching = false;
    }
    
    /**
     * Creates the tile.
     * @param letter  the letter of the tile
     */
    public Tile(char letter)
    {
        this.letter = letter;
        this.blankLet = letter;
        this.showBlankLetter = false;
        this.points = Utility.points()[Utility.letters().indexOf(letter)];
        this.x = -100;
        this.y = -100;
        this.row = -1;
        this.column = -1;
        this.onRack = true;
    }
    
    /**
     * Draws the tile.
     * @param g  the Graphics object
     * @param x  the x coord
     * @param y  the y coord
     * @param numSide  graphics stuff
     * @param boxSide  graphics stuff
     */
    public void draw(Graphics g, int x, int y, int numSide, int boxSide)
    {
        this.x = x;
        this.y = y;
        int side = boxSide * numSide;
        if (letter != '.')
        {
            int alpha = 255;
            if (switching) alpha = 128;
            Rectangle rect = new Rectangle(x + 1,y + 1,boxSide - 2,boxSide - 2);
            g.setColor(new Color(255, 206, 153, alpha));
            g.fillRect(x + 1,y + 1,boxSide - 2,boxSide - 2);
            g.setColor(new Color(128, 66, 0, alpha));
            g.drawRect(x + 1,y + 1,boxSide - 2,boxSide - 2);
            if (letter != '_')
            {
                g.setColor(new Color(51, 26, 0, alpha));
                Utility.centeredString(g,("" + letter).toUpperCase(),rect,new Font("SansSerif",Font.BOLD,boxSide * 3 / 5));
                Rectangle smallRect = new Rectangle(x + boxSide * 2 / 3 + 1, y + boxSide * 3 / 5 + 1,boxSide / 3 - 2,boxSide * 2 / 5 - 2);
                Utility.centeredString(g,"" + points,smallRect,new Font("SansSerif",Font.BOLD,boxSide * 3 / 10));
            }
            else if (blankLet != '_' && showBlankLetter)
            {
                g.setColor(new Color(102, 179, 255, alpha));
                Utility.centeredString(g,("" + blankLet).toUpperCase(),rect,new Font("SansSerif",Font.BOLD,boxSide * 3 / 5));
            }
        }
    }
    
    /**
     * Moves the tile to new coords.
     * @param x  the x coord
     * @param y  the y coord
     */
    public void moveTo(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.onRack = false;
    }
    
    /**
     * Moves the tile to the rack.
     */
    public void toRack()
    {
        this.x = -100;
        this.y = -100;
        this.row = -1;
        this.column = -1;
        this.onRack = true;
    }
    
    /**
     * Determines if the mouse is in the tile.
     * @param mouseX  the x coord of the mouse
     * @param mouseY  the y coord of the mouse
     * @param boxSide  graphics stuff
     * @return whether the mouse is in the tile or not
     */
    public boolean mouseIn(int mouseX, int mouseY, int boxSide)
    {
        if (mouseX > x && mouseY > y && mouseX < x + boxSide && mouseY < y + boxSide)
        {
            return true;
        }
        return false;
    }
    
    /**
     * Sets the row and column of the tile
     * @param row  the new row
     * @param column  the new column
     */
    public void setRC(int row, int column)
    {
        this.row = row;
        this.column = column;
    }
    
    /**
     * Gives the row and column.
     * @return the coords
     */
    public int[] getRC()
    {
        return new int[] {row,column};
    }
    
    /**
     * Gives the letter.
     * @return the letter
     */
    public char letter()
    {
        return letter;
    }
    
    /**
     * Gives the x coord
     * @return the x coord
     */
    public int x()
    {
        return x;
    }
    
    /**
     * Gives the y coord
     * @return the y coord
     */
    public int y()
    {
        return y;
    }
    
    /**
     * Gives whether the tile is on the rack
     * @return whether it is on the rack
     */
    public boolean onRack()
    {
        return onRack;
    }
    
    /**
     * Set the switching value.
     * @param bool whether it is switching or not
     */
    public void setSwitching(boolean bool)
    {
        switching = bool;
    }
    
    /**
     * Gives the switching value.
     * @return whether it is switching or not
     */
    public boolean switching()
    {
        return switching;
    }
    
    /**
     * Goes to the next letter for a blank tile.
     * @param prev  whether it should go forward or back
     */
    public void nextBlankLet(boolean prev)
    {
        if (letter == '_')
        {
            final String realLetters = "abcdefghijklmnopqrstuvwxyz";
            if (!prev)
            {
                int index = realLetters.indexOf(blankLet)+1;
                if (index >= realLetters.length())
                {
                    index = 0;
                }
                blankLet = realLetters.charAt(index);
            }
            else
            {
                int index = realLetters.indexOf(blankLet)-1;
                if (index < 0)
                {
                    index = realLetters.length()-1;
                }
                blankLet = realLetters.charAt(index);
            }
            //System.out.println(blankLet);
        }
    }
    
    /**
     * Shows blank or not.
     * @param bool  whether it should or not
     */
    public void showBlank(boolean bool)
    {
        showBlankLetter = bool;
        if (blankLet == '_') blankLet = 'a';
    }
    
    /**
     * Gives the blank letter
     * @return the blank letter
     */
    public char getBlank()
    {
        return blankLet;
    }
    
    /**
     * Sets the blank letter
     * @param let  the new letter
     */
    public void setBlank(char let)
    {
        blankLet = let;
    }
}
