/**
 * Contains info for a Scrabble player.
 *
 * @author Jonathan Shi
 * @version 6/2/19
 */
import java.util.*;
public class Player
{
    private int playerNum;
    private int score;
    private char[] letters;

    /**
     * Creates a player object with a number.
     * @param playerNum  the player's number
     */
    public Player(int playerNum)
    {
       this.playerNum = playerNum;
       this.score = 0;
       letters = new char[] {'.','.','.','.','.','.','.'};
    }
    
    /**
     * Gives the score of the player.
     * @return the player's score
     */
    public int getScore()
    {
        return score;
    }
    
    /**
     * Sets the score of the player.
     * @param s  the number to set the score to
     */
    public void setScore(int s)
    {
        score = s;
    }
    
    /**
     * Adds points to the player's score.
     * @param points  the number of points to add
     */
    public void addPoints(int points)
    {
        score += points;
    }
    
    /**
     * Gives the hand of the player.
     * @return a list of the tiles of the player
     */
    public char[] getLetters()
    {
        return letters;
    }
    
    /**
     * Sets the tiles of the player.
     * @param chars  the new tiles to set to
     */
    public void setLetters(char[] chars)
    {
        for (int i = 0; i < chars.length; i++) letters[i] = chars[i];
    }
    
    /**
     * Sets the tiles of the player.
     * @param chars  the new tiles to set to
     */
    public void setLetters(String[] chars)
    {
        for (int i = 0; i < chars.length; i++) letters[i] = chars[i].charAt(0);
    }
    
    /**
     * Determines and adds the points of the turn.
     * @param words  the words used to calculate the points
     */
    public void playWords(ArrayList<String> words)
    {
        for (String word : words)
        {
            int wordScore = 0;
            int multiplier = 1;
            int letterMultiplier = 1;
            for (int i = 0; i < word.length(); i++)
            {
                switch (word.charAt(i))
                {
                    case '3':
                        multiplier *= 3;
                        break;
                    case '2':
                    case '@':
                        multiplier *= 2;
                        break;
                    case '*':
                        letterMultiplier = 3;
                        break;
                    case '+':
                        letterMultiplier = 2;
                        break;
                    default:
                        if (Character.toUpperCase(word.charAt(i)) != word.charAt(i)) wordScore += Utility.points()[(Utility.letters().indexOf(word.charAt(i)))] * letterMultiplier;
                        letterMultiplier = 1;
                }
            }
            wordScore *= multiplier;
            score += wordScore;
        }
        //System.out.println(score);
        /*if (Utility.isWord(word))
        {
            score += Utility.calcPoints(word);
            return true;
        }
        return false;*/
    }
}
