
/**
 * Miscellaneous methods for all the classes.
 *
 * @author Jonathan Shi
 * @version 6/12/19
 */
import java.awt.*;
import java.util.*;
import java.awt.image.*;
import java.io.*;
import java.util.Scanner;
public class Utility
{
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz_";
    private static int[] points;
    
    static
    {
        points = new int[27];
        try
        {
            Scanner scan = new Scanner(new File("resources/letterDataTest.txt"));
            for (int i = 0; i < 27 && scan.hasNextLine(); i++)
            {
                String info = scan.nextLine();
                points[i] = Integer.parseInt(info.substring(2,4));
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Draws a centered string in graphics.
     * @param g  the Graphics object
     * @param text  the string
     * @param rect  the rectangle to be centered in
     * @param font  the font of the string
     */
    public static void centeredString(Graphics g, String text, Rectangle rect, Font font)
    {
        FontMetrics fontMetrics = g.getFontMetrics(font);
        int x = rect.x + (rect.width - fontMetrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - fontMetrics.getHeight()) / 2) + fontMetrics.getAscent();
        g.setFont(font);
        g.drawString(text,x,y);
    }
    
    /**
     * Gives a list of the letters in Scrabble.
     * @return the letter list
     */
    public static String letters()
    {
        return LETTERS;
    }
    
    /**
     * Gives of list of the point values for letters.
     * @return the point value list
     */
    public static int[] points()
    {
        return points;
    }
    
    /**
     * Gives whether the word is in the Scrabble dictionary or not.
     * @param input  the word
     * @return whether it is valid or not
     */
    public static boolean isWord(String input)
    {
        boolean valid = false;
        if (input.length() > 0)
        {
            try
            {
                String word = input.toUpperCase();
                int firstValue = (int) word.charAt(0);
                int fileNum = -1;
                if (firstValue >= 65 && firstValue <= 68) fileNum = 1; //A-D
                else if (firstValue >= 69 && firstValue <= 76) fileNum = 2; //E-L
                else if (firstValue >= 77 && firstValue <= 82) fileNum = 3; //M-R 
                else if (firstValue >= 83 && firstValue <= 90) fileNum = 4; //S-Z
                else return false;
                Scanner scan = new Scanner(new File("resources/wordList/" + fileNum + ".txt"));
                while (scan.hasNextLine() && !valid)
                {
                    if (word.toUpperCase().equals(scan.nextLine()))
                    {
                        valid = true;
                    }
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        return valid;
    }
    
    /*public static int calcPoints(String word)
    {
        int total = 0;
        String[] letters = word.replaceAll("([A-Z])","_").split("");
        for (String letter : letters)
        {
            for (int i = 0; i < 27; i++)
            {
                if (LETTERS.substring(i,i+1).equals(letter))
                {
                    total += points[i];
                    break;
                }
            }
        }
        return total;
    }*/
    
    /**
     * Removes the letters from a string
     * @param word  the input string
     * @return the only letters string
     */
    public static String onlyLetters(String word)
    {
        String result = "";
        for (int i = 0; i < word.length(); i++)
        {
            if (Character.isLetter(word.charAt(i)))
            {
                result += word.charAt(i);
            }
        }
        return result;
    }
    
    /**
     * Gives a resized Image.
     * @param srcImg  the original image
     * @param w  the new width
     * @param h  the new height
     * @return the resized image
     */
    public static Image getScaledImage(Image srcImg, int w, int h)
    {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
    
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
    
        return resizedImg;
    }
    
    /**
     * Shuffles a Tile array.
     * @param array  the array to shuffle
     */
    public static void shuffle(Tile[] array)
    {
    	Random rgen = new Random(); // Random number generator
    	for (int i = 0; i<array.length; i++)
    	{
	    	int randomPosition = rgen.nextInt(array.length);
	    	Tile temp = array[i];
	    	array[i] = array[randomPosition];
	    	array[randomPosition] = temp;
    	}
    }
}
