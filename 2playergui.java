// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.*;
// import java.util.ArrayList;
// import java.util.List;

// public class gui{
//     public static List<ChessSquare> squares = new ArrayList<>();
//     public static List<ChessSquare> highlighted = new ArrayList<>();
//     private static JPanel chessboard;
//     private static JPanel sidePanel;
//     private static JTextArea msgArea;
//     private static boolean isReversed = false;
//     public static Color highlighter = new Color(144, 238, 144, 128);
//     public static ChessSquare selected;
//     public static JFrame frame;
//     public static void main(String[] args) {
//         // Initialize the frame
//         frame = new JFrame("Chess");
//         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         frame.setSize(900, 700); 

//         GameManager.g.initializeBoard();
//         chessboard = new JPanel(new GridLayout(8, 8));
//         chessboard.setPreferredSize(new Dimension(600, 600)); 
//         chessboard.setMinimumSize(new Dimension(600, 600)); 
//         setupBoard();
        
//         setupSidePanel();
//         sidePanel.setPreferredSize(new Dimension(200, 700));
//         sidePanel.setMinimumSize(new Dimension(200, 700));

//         frame.setLayout(new BorderLayout());
//         frame.add(chessboard, BorderLayout.CENTER);
//         frame.add(sidePanel, BorderLayout.EAST);

//         frame.setLocationRelativeTo(null);
//         frame.setVisible(true);
//     }


//     public static void setupBoard(){
//         for(int row=0; row<8;row++){
//             for(int col=0; col<8;col++){
//                 Piece piece = GameManager.g.board[row][col];
//                 ChessSquare chsq = new ChessSquare((row+col) % 2 == 0 ? Color.WHITE : Color.DARK_GRAY, piece,row,col);
//                 chessboard.add(chsq);
//                 squares.add(chsq);
//             }
//         }
//         chessboard.revalidate();
//         chessboard.repaint();
//     }

//     public static void display_bottom_black() {
//         chessboard.removeAll();
//         for (int row = 7; row >= 0; row--) {
//             for (int col = 7; col >= 0; col--) {
//                 int index = row * 8 + col;
//                 if (index >= 0 && index < squares.size()) {
//                     chessboard.add(squares.get(index));
//                 }
//             }
//         }
//         chessboard.revalidate();
//         chessboard.repaint();
//     }

//     public static void display_bottom_white() {
//         chessboard.removeAll();
//         for (int row = 0; row < 8; row++) {
//             for (int col = 0; col < 8; col++) {
//                 int index = row * 8 + col;
//                 if (index >= 0 && index < squares.size()) {
//                     chessboard.add(squares.get(index));
//                 }
//             }
//         }
//         chessboard.revalidate();
//         chessboard.repaint();
//     }

//     public static void shuffle() {
//         if (selected != null) {
//             selected.clearMoves();
//             selected = null;
//         }
        
//         if (GameManager.g.currentTurn == GameManager.g.white) {
//             GameManager.g.currentTurn = GameManager.g.black;
//             display_bottom_white();
//         } else {
//             GameManager.g.currentTurn = GameManager.g.white;
//             display_bottom_black();
//         }
//     }

//     public static void setupSidePanel(){
//         gui.sidePanel = new JPanel();
//         gui.sidePanel.setPreferredSize(new Dimension(200,700));
//         gui.sidePanel.setBackground(new Color(34,139,34));

//         gui.msgArea = new JTextArea();
//         gui.msgArea.setEditable(false);
//         gui.msgArea.setBackground(new Color(144,238,144));
//         gui.msgArea.setFont(new Font("Serif", Font.PLAIN, 16));
//         gui.msgArea.setForeground(Color.BLACK);
//         gui.msgArea.setLineWrap(true);
//         gui.msgArea.setWrapStyleWord(true);
//         JScrollPane scrollPane = new JScrollPane(msgArea);
//         sidePanel.setLayout(new BorderLayout());
//         sidePanel.add(scrollPane, BorderLayout.CENTER);
//     }

//     public static void displayGameOver(String winningSide){
//         JPanel overlay = new JPanel();
//         overlay.setBackground(new Color(0, 100, 0, 200)); 
//         overlay.setLayout(new GridBagLayout());
//         JLabel message = new JLabel(winningSide + " WINS!!!! ");
//         message.setFont(new Font("Serif", Font.BOLD, 50));
//         message.setForeground(Color.WHITE);
//         overlay.add(message, new GridBagConstraints());

//         gui.frame.setGlassPane(overlay);
//         overlay.setVisible(true);
//         gui.frame.getGlassPane().setVisible(true);
//     }

//     public static void displayMessage(String message){
//         msgArea.append(message + "\n");
//         msgArea.setCaretPosition(msgArea.getDocument().getLength());
//     }
// }

// class ChessSquare extends JPanel{
//     Piece p;
//     final int row;
//     final int col;
//     final Color original;
    
//     public ChessSquare(Color color, Piece piece,int row, int col){
//         this.setBackground(color);
//         this.original = color;
//         this.p = piece;
//         this.row = row;
//         this.col = col;
//         this.setLayout(new OverlayLayout(this));
//         updateDisplay();
//     }

//     public void updateDisplay(){
//         this.removeAll();
//         if(this.p!=null){
//             JLabel pieceLabel = new JLabel(" " + this.p.symbol + " ");
//             pieceLabel.setFont(new Font("Serif", Font.PLAIN, 50));
//             pieceLabel.setHorizontalAlignment(SwingConstants.CENTER);
//             this.add(pieceLabel);
            
//         }
//         this.addMouseListener(new SquareMouseAdapter(this));
//         this.revalidate();
//         this.repaint();
//     }

//     public void updateState(Piece newPiece){
//         this.p = newPiece;
//         updateDisplay();
//     }

//     public void showMoves(){
//         if(this.p!=null){
//             if(this.p.clicked!=this.p.loc) this.p.generateMoves();
//             //we need to update moves only if it is clicked for first time.
//             for(int i=0; i< this.p.curMoves.size();i++){
//                 int[] move = this.p.curMoves.get(i);
//                 ChessSquare targetSquare =  gui.squares.get(move[0]*8 + move[1]);
//                 if(targetSquare!=null){
//                     targetSquare.setBackground(gui.highlighter);
//                     gui.highlighted.add(targetSquare);
//                     targetSquare.revalidate();
//                     targetSquare.repaint();
//                 }
//             }
//         }
//     }
//     public void clearMoves(){
//         for(int i=0; i< gui.highlighted.size();i++){
//             ChessSquare targetSquare = gui.highlighted.get(i);
//             targetSquare.setBackground(targetSquare.original);
//         }
//         gui.highlighted.clear();
//     }


// }

// class SquareMouseAdapter extends MouseAdapter{
//     ChessSquare square;
//     public SquareMouseAdapter(ChessSquare square){
//         this.square = square;
//     }

//     @Override
//     public void mouseEntered(MouseEvent e){
//         if(this.square.p!=null){
//             if(this.square.p.side==GameManager.g.currentTurn){
//                 this.square.setBackground(this.square.original.darker());
//             }
//         }
//     }

//     @Override
//     public void mouseExited(MouseEvent e){
//         if(gui.highlighted.contains(this.square)) return;
//         square.setBackground(square.original);
//     }

    
//     @Override
//     public void mouseClicked(MouseEvent e){
//         if(gui.highlighted.contains(this.square)){
//             ChessSquare sourceSquare = gui.selected;
//             Piece movingPiece = sourceSquare.p;
//             this.square.clearMoves();
//             if(GameManager.g.board[this.square.row][this.square.col]!=null){
//                 if(GameManager.g.board[this.square.row][this.square.col].side != movingPiece.side){
//                     gui.displayMessage("Piece taken!");
//                 }
//             }
//             if(GameManager.checkForCheck(movingPiece,this.square.row,this.square.col,movingPiece.loc[0],movingPiece.loc[1])) gui.displayMessage("CHECK!");
//             if(GameManager.g.board[this.square.row][this.square.col] instanceof King){
//                 if(GameManager.g.board[this.square.row][this.square.col].side == GameManager.g.white){
//                     gui.displayGameOver("BLACK");
//                 }else{
//                     gui.displayGameOver("WHITE");
//                 }
//             }
//             movingPiece.move(new int[]{this.square.row, this.square.col});
//             sourceSquare.p = null; 
//             sourceSquare.updateDisplay();
            
//             this.square.p = movingPiece;  
//             this.square.updateDisplay();
            
//             gui.selected = null;
//             gui.shuffle();  
            
//         } else if(this.square.p != null && this.square.p.side == GameManager.g.currentTurn && (this.square != gui.selected)){
//             if(gui.selected != null) gui.selected.clearMoves();
//             this.square.showMoves();
//             gui.selected = this.square;
//         }
//     }
// }