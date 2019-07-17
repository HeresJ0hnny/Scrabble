
/**
 * Contains info for the Scrabble game and board.
 *
 * | + = double letter
 * | * = triple letter
 * | 2 = double word
 * | 3 = triple word
 * | @ = start (double word)
 * | j = letter
 * | J = letter (blank)
 *
 * @author Jonathan Shi
 * @version 6/2/19
 */
import java.io.*;
import java.util.*;
public class Board
{
    private int[] bag;
    private char[][] board;
    private ArrayList<Player> players;
    private int numPlayers;
    private int turn;
    private final int SIDE = 15; //number of rows/columns
    
    /**
     * Creates the Board class that stores the info.
     * @param  numPlayers  the number of players in the game
     */
    public Board(int numPlayers)
    { 
        this.bag = new int[27];
        this.turn = 0;
        this.numPlayers = numPlayers;
        this.players = new ArrayList<Player>();
        for (int i = 0; i < numPlayers; i++)
        {
            Player person = new Player(i);
            this.players.add(person);
        }
        /*for (int i = numPlayers; i < numPlayers + numAI; i++)
        {
            Player ai = new AI(i);
            this.players.add(ai);
        }*/
        this.board = new char[SIDE][SIDE];
        try
        {
            /**/Scanner letterScan = new Scanner(new File("resources/letterData.txt"));
            for (int i = 0; i < 27 && letterScan.hasNextLine(); i++)
            {
                String info = letterScan.nextLine();
                this.bag[i] = Integer.parseInt(info.substring(6,8));
            }
            Scanner boardScan = new Scanner(new File("resources/boardLayout.txt"));
            for (int r = 0; r < SIDE && boardScan.hasNextLine(); r++)
            {
                String row = boardScan.nextLine();
                for (int c = 0; c < SIDE; c++)
                {
                    this.board[r][c] = row.charAt(c);
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        for (int t = 0; t < players.size(); t++)
        {
            turn = t;
            drawTiles(new char[] {'.','.','.','.','.','.','.'});
            //drawTiles(new char[] {'p','e','n','g','u','i','n'});
        }
        turn = 0;
        ///////
        //this.board[7][8] = 's';
        /*this.board[7][7] = 'a';
        this.board[8][6] = 'e';
        this.board[9][6] = 'd';
        this.board[10][6] = 'c';
        this.board[8][8] = 'f';
        this.board[9][8] = 'e';
        this.board[10][8] = 'g';
        this.board[11][7] = 'b';*/
    }
    
    /**
     * Sets an entire row of elements.
     * @param row  the row number
     * @param chars  the list to set it to
     */
    public void setRow(int row, String[] chars)
    {
        for (int i = 0; i < SIDE; i++)
        {
            board[row][i] = chars[i].charAt(0);
        }
    }
    
    /**
     * Sets a specific tile on the board.
     * @param letter  the letter to set it to
     * @param row  the row of the target
     * @param column  the column of the target
     */
    public void setTile(char letter, int row, int column)
    {
        board[row][column] = letter;
    }
    
    /**
     * Draws tiles from the bag.
     * @param chars  the hand to fill with new tiles
     */
    public void drawTiles(char[] chars)
    {
        Random rand = new Random();
        int tilesLeft = 0;
        for (int num : bag) tilesLeft += num;
        for (int i = 0; i < chars.length; i++)
        {
            if (chars[i] == '.' && tilesLeft > 0)
            {
                int tileNum = rand.nextInt(tilesLeft);
                int sum = 0;
                for (int j = 0; j < bag.length && sum >= 0; j++)
                {
                    if (bag[j] > 0 && sum + bag[j] >= tileNum)
                    {
                        chars[i] = Utility.letters().charAt(j);
                        bag[j]--;
                        sum = -1;
                    }
                    else
                    {
                        sum += bag[j];
                    }
                }
                tilesLeft--;
            }
        }
        players.get(turn).setLetters(chars);
        /*Random rand = new Random();
        char[] tiles = new char[numTiles];
        for (int i = 0; i < numTiles; i++) tiles[i] = '.';
        int tilesLeft = 0;
        for (int num : bag) tilesLeft += num;
        for (int i = 0; i < numTiles && tilesLeft > 0; i++)
        {
            int tileNum = rand.nextInt(tilesLeft);
            int sum = 0;
            for (int j = 0; j < bag.length && sum >= 0; j++)
            {
                if (bag[j] > 0 && sum + bag[j] >= tileNum)
                {
                    tiles[i] = Utility.letters().charAt(j);
                    bag[j]--;
                    sum = -1;
                }
                else
                {
                    sum += bag[j];
                }
            }
            tilesLeft--;
        }
        return tiles;*/
    }
    
    /**
     * Replaces tiles of the hand with ones from the bag.
     * @param chars  the hand to be replaced
     * @param replaces  the tiles to replace
     */
    public void replaceTiles(char[] chars, boolean[] replaces)
    {
        for (int i = 0; i < replaces.length; i++)
        {
            if (replaces[i])
            {
                bag[Utility.letters().indexOf(chars[i])] += 1;
                chars[i] = '.';
            }
        }
        drawTiles(chars);
    }
    
    /**
     * Plays the words for the player
     * @param words  the words to play
     */
    public void playWords(ArrayList<String> words)
    {
        players.get(turn).playWords(words);
    }
    
    /**
     * Adds bingo points.
     */
    public void bingo()
    {
        players.get(turn).addPoints(50);
    }
    
    /**
     * Gives the score of a player.
     * @param player  the player number
     * @return the score
     */
    public int score(int player)
    {
        return players.get(player).getScore();
    }
    
    /**
     * Goes to the next turn.
     */
    public void nextTurn()
    {
        turn++;
        if (turn >= players.size()) turn = 0;
    }
    
    /**
     * Sets the turn to a value.
     * @param t  the value to set the turn to
     */
    public void setTurn(int t)
    {
        turn = t;
    }
    
    /*public Tile[] ai()
    {
        //System.out.println("nextTurn " + (players.get(turn) instanceof AI));
        AI ai = (AI) players.get(turn);
        return ai.aiTiles(ai.aiCoords(board));
    }
    
    public void clearAICoords()
    {
        ((AI) players.get(turn)).clearCoords();
    }*/
    
    /**
     * Gives the turn.
     * @return the turn number
     */
    public int turn()
    {
        return turn;
    }
    
    /**
     * Gives the letters of current player.
     * @return the list of tiles
     */
    public char[] letters()
    {
        return players.get(turn).getLetters();
    }
    
    /**
     * Gives the bag.
     * @return the bag
     */
    public int[] getBag()
    {
        return bag;
    }
    
    /**
     * Gives the number of tiles left.
     * @return the number of tiles left
     */
    public int tilesLeft()
    {
        int sum = 0;
        for (int num : bag) sum += num;
        return sum;
    }
    
    /**
     * GIves the board in array form.
     * @return the 2D array of the board
     */
    public char[][] getBoard()
    {
        return board;
    }
    
    /**
     * Gives a list of the players.
     * @return an ArrayList of the Players
     */
    public ArrayList<Player> players()
    {
        return players;
    }
    
    /**
     * Gives the number of players.
     * @return the number of players
     */
    public int numPlayers()
    {
        return numPlayers;
    }
    
    /**
     * Sets the amount of each letter in the bag.
     * @param bag2  the new bag numbers
     */
    public void setBag(String[] bag2)
    {
        for (int i = 0; i < bag.length; i++)
        {
            bag[i] = Integer.parseInt(bag2[i]);
        }
    }
    
    /**
     * Gives the winner(s).
     * @return a list of the winners
     */
    public ArrayList<Integer> winner()
    {
        ArrayList<Integer> winners = new ArrayList<Integer>();
        winners.add(0);
        for (int i = 1; i < players.size(); i++)
        {
            if (players.get(i).getScore() > players.get(winners.get(0)).getScore())
            {
                winners = new ArrayList<Integer>();
                winners.add(i);
            }
            else if (players.get(i).getScore() == players.get(winners.get(0)).getScore())
            {
                winners.add(i);
            }
        }
        return winners;
    }
    
    /**
     * Calculates and adds the end scores.
     */
    public void endScoring()
    {
        int totScore = 0;
        for (int i = 0; i < players.size(); i++)
        {
            if (i != turn)
            {
                char[] letters = players.get(i).getLetters();
                int score = 0;
                for (char c : letters)
                {
                    if (c != '.') score += Utility.points()[(Utility.letters().indexOf(c))];
                }
                players.get(i).addPoints(-score);
                totScore += score;
            }
        }
        players.get(turn).addPoints(totScore);
    }
}
