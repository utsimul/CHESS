import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class gui {
    public static List<ChessSquare> squares = new ArrayList<>();
    public static List<ChessSquare> highlighted = new ArrayList<>();
    private static JPanel chessboard;
    private static JPanel sidePanel;
    private static JTextArea msgArea;
    public static Color highlighter = new Color(144, 238, 144, 128);
    public static ChessSquare selected;
    public static JFrame frame;

    public static void main(String[] args) {
        frame = new JFrame("Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        
        // Add menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        
        JMenuItem botWhiteItem = new JMenuItem("Play vs Bot (as Black)");
        botWhiteItem.addActionListener(e -> connectToBot(true));
        
        JMenuItem botBlackItem = new JMenuItem("Play vs Bot (as White)");
        botBlackItem.addActionListener(e -> connectToBot(false));
        
        gameMenu.add(botWhiteItem);
        gameMenu.add(botBlackItem);
        menuBar.add(gameMenu);
        frame.setJMenuBar(menuBar);
        
        GameManager.g.initializeBoard();
        chessboard = new JPanel(new GridLayout(8, 8));
        chessboard.setPreferredSize(new Dimension(600, 600)); 
        setupBoard();
        
        setupSidePanel();
        sidePanel.setPreferredSize(new Dimension(200, 700));

        frame.setLayout(new BorderLayout());
        frame.add(chessboard, BorderLayout.CENTER);
        frame.add(sidePanel, BorderLayout.EAST);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void setupBoard() {
        for(int row = 0; row < 8; row++) {
            for(int col = 0; col < 8; col++) {
                Piece piece = GameManager.g.board[row][col];
                ChessSquare chsq = new ChessSquare(
                    (row + col) % 2 == 0 ? Color.WHITE : Color.DARK_GRAY, 
                    piece, row, col
                );
                chessboard.add(chsq);
                squares.add(chsq);
            }
        }
        chessboard.revalidate();
        chessboard.repaint();
    }

    private static void connectToBot(boolean playAsBlack) {
        String host = JOptionPane.showInputDialog(frame, "Enter bot host (localhost):", "localhost");
        if (host == null) return;
        
        String portStr = JOptionPane.showInputDialog(frame, "Enter bot port:", "12345");
        if (portStr == null) return;
        
        try {
            int port = Integer.parseInt(portStr);
            GameManager.connectToBot(host, port, !playAsBlack);
            
            // If bot plays first, trigger its move
            if (playAsBlack && GameManager.g.currentTurn == GameManager.g.white) {
                GameManager.makeBotMove();
            }
        } catch (Exception e) {
            displayMessage("Error connecting to bot: " + e.getMessage());
        }
    }

    public static void display_bottom_white() {
        chessboard.removeAll();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                chessboard.add(squares.get(row * 8 + col));
            }
        }
        chessboard.revalidate();
        chessboard.repaint();
    }

    public static void shuffle() {
        if (selected != null) {
            selected.clearMoves();
            selected = null;
        }
        
        // Switch turns
        GameManager.g.currentTurn = (GameManager.g.currentTurn == GameManager.g.white) 
            ? GameManager.g.black : GameManager.g.white;
        
        // If bot is enabled and it's bot's turn, make the bot move
        if (GameManager.isBotEnabled() && 
            ((GameManager.isBotPlayingWhite() && GameManager.g.currentTurn == GameManager.g.white) ||
             (!GameManager.isBotPlayingWhite() && GameManager.g.currentTurn == GameManager.g.black))) {
            GameManager.makeBotMove();
        }
    }

    public static void setupSidePanel() {
        sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(200, 700));
        sidePanel.setBackground(new Color(34, 139, 34));

        msgArea = new JTextArea();
        msgArea.setEditable(false);
        msgArea.setBackground(new Color(144, 238, 144));
        msgArea.setFont(new Font("Serif", Font.PLAIN, 16));
        msgArea.setForeground(Color.BLACK);
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(msgArea);
        sidePanel.setLayout(new BorderLayout());
        sidePanel.add(scrollPane, BorderLayout.CENTER);
    }

    public static void displayGameOver(String winningSide) {
        JPanel overlay = new JPanel();
        overlay.setBackground(new Color(0, 100, 0, 200)); 
        overlay.setLayout(new GridBagLayout());
        JLabel message = new JLabel(winningSide + " WINS!!!! ");
        message.setFont(new Font("Serif", Font.BOLD, 50));
        message.setForeground(Color.WHITE);
        overlay.add(message, new GridBagConstraints());

        frame.setGlassPane(overlay);
        overlay.setVisible(true);
        frame.getGlassPane().setVisible(true);
    }

    public static void displayMessage(String message) {
        msgArea.append(message + "\n");
        msgArea.setCaretPosition(msgArea.getDocument().getLength());
    }
}

class ChessSquare extends JPanel {
    Piece p;
    final int row;
    final int col;
    final Color original;
    
    public ChessSquare(Color color, Piece piece, int row, int col) {
        this.setBackground(color);
        this.original = color;
        this.p = piece;
        this.row = row;
        this.col = col;
        this.setLayout(new OverlayLayout(this));
        updateDisplay();
    }

    public void updateDisplay() {
        this.removeAll();
        if(this.p != null) {
            JLabel pieceLabel = new JLabel(" " + this.p.symbol + " ");
            pieceLabel.setFont(new Font("Serif", Font.PLAIN, 50));
            pieceLabel.setHorizontalAlignment(SwingConstants.CENTER);
            this.add(pieceLabel);
        }
        this.addMouseListener(new SquareMouseAdapter(this));
        this.revalidate();
        this.repaint();
    }

    public void updateState(Piece newPiece) {
        this.p = newPiece;
        updateDisplay();
    }

    public void showMoves() {
        if(this.p != null) {
            if(this.p.clicked != this.p.loc) this.p.generateMoves();
            
            for(int i = 0; i < this.p.curMoves.size(); i++) {
                int[] move = this.p.curMoves.get(i);
                ChessSquare targetSquare = gui.squares.get(move[0] * 8 + move[1]);
                if(targetSquare != null) {
                    targetSquare.setBackground(gui.highlighter);
                    gui.highlighted.add(targetSquare);
                    targetSquare.revalidate();
                    targetSquare.repaint();
                }
            }
        }
    }

    public void clearMoves() {
        for(int i = 0; i < gui.highlighted.size(); i++) {
            ChessSquare targetSquare = gui.highlighted.get(i);
            targetSquare.setBackground(targetSquare.original);
        }
        gui.highlighted.clear();
    }
}

class SquareMouseAdapter extends MouseAdapter {
    ChessSquare square;
    
    public SquareMouseAdapter(ChessSquare square) {
        this.square = square;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(this.square.p != null && this.square.p.side == GameManager.g.currentTurn) {
            this.square.setBackground(this.square.original.darker());
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if(!gui.highlighted.contains(this.square)) {
            square.setBackground(square.original);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(gui.highlighted.contains(this.square)) {
            ChessSquare sourceSquare = gui.selected;
            Piece movingPiece = sourceSquare.p;
            this.square.clearMoves();
            
            if(GameManager.g.board[this.square.row][this.square.col] != null && 
               GameManager.g.board[this.square.row][this.square.col].side != movingPiece.side) {
                gui.displayMessage("Piece taken!");
            }
            
            if(GameManager.checkForCheck(movingPiece, this.square.row, this.square.col, 
                                      movingPiece.loc[0], movingPiece.loc[1])) {
                gui.displayMessage("CHECK!");
            }
            
            if(GameManager.g.board[this.square.row][this.square.col] instanceof King) {
                gui.displayGameOver(GameManager.g.board[this.square.row][this.square.col].side == GameManager.g.white 
                    ? "BLACK" : "WHITE");
            }
            
            movingPiece.move(new int[]{this.square.row, this.square.col});
            sourceSquare.p = null; 
            sourceSquare.updateDisplay();
            
            this.square.p = movingPiece;  
            this.square.updateDisplay();
            
            gui.selected = null;
            gui.shuffle();  
        } 
        else if(this.square.p != null && this.square.p.side == GameManager.g.currentTurn && 
                (this.square != gui.selected)) {
            if(gui.selected != null) {
                gui.selected.clearMoves();
            }
            this.square.showMoves();
            gui.selected = this.square;
        }
    }
}