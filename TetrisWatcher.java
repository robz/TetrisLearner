import java.awt.*;
import java.awt.image.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.*;
import java.awt.geom.*;

public class TetrisWatcher {
    //static Rectangle boardRect = new Rectangle(497,315,180,360);
    static Rectangle boardRect = new Rectangle(215,330,180,360);
    //static Rectangle boardRect = new Rectangle(502,228,180,360);
    
    public static class PieceColorFinder {
        // each piece seems to have a different floating/set color
        static Color iPiece_s = new Color(15,155,215),
              iPiece_f = new Color(50,190,250),
              sPiece_s = new Color(89,177,1),
              sPiece_f = new Color(124,212,36),
              oPiece_s = new Color(227,159,2),
              oPiece_f = new Color(255,194,37),
              zPiece_s = new Color(215,15,55),
              zPiece_f = new Color(250,50,90),
              tPiece_s = new Color(175,41,138),
              tPiece_f = new Color(210,76,173),
              lPiece_s = new Color(227,91,2),
              lPiece_f = new Color(255,126,37),
              rPiece_s = new Color(33,65,198),
              rPiece_f = new Color(68,100,233),
              blank1 = new Color(47,47,47),
              blank2 = new Color(43,43,43),
              brick = new Color(153,153,153);
        
        static Color pieceColors[] = {
            iPiece_s, iPiece_f, 
            sPiece_s, sPiece_f, 
            oPiece_s, oPiece_f, 
            zPiece_s, zPiece_f, 
            tPiece_s, tPiece_f, 
            lPiece_s, lPiece_f, 
            rPiece_s, rPiece_f, 
            blank1, blank2, brick};
        
        static char pieceLetters[] = {'i', 'i', 's', 's', 'o', 'o', 'z', 'z', 't', 't', 'L', 'L', 'r', 'r', ' ', ' ', 'X'};
    
        public static double calcColorDist(Color c1, Color c2) {
            int dr = c1.getRed() - c2.getRed(),
                dg = c1.getGreen() - c2.getGreen(),
                db = c1.getBlue() - c2.getBlue();
            
            return Math.sqrt(dr*dr + dg*dg + db*db);
        };
        
        public static char getClosestPieceChar(int rgb) {
            Color color = new Color(rgb);
            double minDist = Double.MAX_VALUE;
            char bestMatch = '?';
        
            for (int i = 0; i < pieceColors.length; i++) {
                double dist = calcColorDist(pieceColors[i], color);
                if (dist < minDist) {
                    minDist = dist;
                    bestMatch = pieceLetters[i];
                }
            }
            
            return bestMatch;
        }
    
        public static char getPieceChar(int rgb) {
            for (int i = 0; i < pieceColors.length; i++) {
                if (pieceColors[i].getRGB() == rgb) { 
                    return pieceLetters[i];
                }
            }
            return '?';
        }
    }
    
    public static char[][] getBoardState(
        BufferedImage boardImg, 
        boolean debug
        ) throws Exception 
    {
        char board[][] = new char[20][10];
        HashSet<Integer> unknownSet = new HashSet<Integer>();
        
        Graphics2D g2d = null;
        
        if (debug) {
            g2d = boardImg.createGraphics();
            g2d.setColor(Color.red);
        }
        
        for (int r = 0; r < 20; r++) {
            int y = (int)(r*boardRect.height/20. + boardRect.height/20./2.);
            
            for (int c = 0; c < 10; c++) {
                int x = (int)(c*boardRect.width/10. + boardRect.width/10./2.);
                
                char ch = PieceColorFinder.getPieceChar(boardImg.getRGB(x, y));
                if (ch == '?') {
                   unknownSet.add(boardImg.getRGB(x, y));
                }
                
                board[r][c] = ch;
                
                if (debug) {
                    g2d.fill(new Ellipse2D.Float(x-1,y-1,2,2));
                }
            }
        }
        
        for (Integer s: unknownSet) {
            System.out.println("Did not recognize: " + new Color(s).toString());
        }
        
        if (debug) {
            g2d.dispose();
            File f = new File("board-debug.png");
            ImageIO.write(boardImg, "PNG", f);
        }
        
        return board;
    }
    
    public static void printBoard(char[][] board) {
        for (int r = 0; r < board.length; r++) {
            System.out.println(new String(board[r]));
        }
    }
    
    public static void main(String[] args) throws Exception {
        Robot robot = new Robot();
        char[][] board = getBoardState(
            robot.createScreenCapture(boardRect), 
            true);
        printBoard(board);
    }
}