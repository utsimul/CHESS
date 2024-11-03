import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Game {
    User white;
    User black;
    User currentTurn;
    boolean isGameOver;
    Piece[][] board = new Piece[8][8];
    Piece currently_moved;

    Game() {
        this.white = new User(true);
        this.black = new User(false);
        this.currentTurn = black;
        this.isGameOver = false;
    }

    void switchTurn() {
        currentTurn = (currentTurn == white) ? this.black : this.white;
    }

    void initializeBoard() {
        //white pawns:
        for (int i = 0; i < 8; i++){
            Pawn p = new Pawn(this.white,new int[]{1,i});
            this.board[1][i] = p;
            this.white.pawns[i] = p;
        }

        //black pawns:
        for (int i = 0; i < 8; i++){
            Pawn p = new Pawn(this.black,new int[]{6,i});
            this.board[6][i] = p;
            this.black.pawns[i] = p;
        }

        //rooks:
        this.white.rooks[0] = new Rook(this.white, new int[]{0,0});
        this.white.rooks[1] = new Rook(this.white, new int[]{0,7});
        this.black.rooks[0] = new Rook(this.black, new int[]{7,0});
        this.board[7][7] = new Rook(this.black, new int[]{7,7});
        this.board[0][0] = this.white.rooks[0];
        this.board[0][7] = this.white.rooks[1];
        this.board[7][0] = this.black.rooks[0];
        //knights:
        this.board[0][1] = new Knight(this.white, new int[]{0,1});
        this.board[0][6] = new Knight(this.white, new int[]{0,6});
        this.board[7][1] = new Knight(this.black, new int[]{7,1});
        this.board[7][6] = new Knight(this.black, new int[]{7,6});

        //bishops:
        this.board[0][2] = new Bishop(this.white, new int[]{0,2});
        this.board[0][5] = new Bishop(this.white, new int[]{0,5});
        this.board[7][2] = new Bishop(this.black, new int[]{7,2});
        this.board[7][5] = new Bishop(this.black, new int[]{7,5});

        //queens:
        this.white.queen = new Queen(this.white, new int[]{0,3});
        this.board[0][3] = this.white.queen;
        this.black.queen = new Queen(this.black, new int[]{7,3});
        this.board[7][3] = this.black.queen;

        //kings:
        this.white.king = new King(this.white, new int[]{0,4});
        this.board[0][4] = this.white.king;
        this.black.king = new King(this.black, new int[]{7,4});
        this.board[7][4] = this.black.king;

    }

    boolean checkForcheck(int[] location_modified){
        //checks whether there is any check to either of the kings in the current state after recent movt.

        //check due to attack of piece that recently moved:
        if(this.currently_moved.can_cause_check_to_opp==true) return true;

        //check due to vulnerability 
        return false;
        
    }

    void displayBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (GameManager.g.board[row][col] != null) {
                    // Display piece on top of the correct background
                    System.out.print((row + col) % 2 == 0 ? "\u001B[47m" : "\u001B[40m"); // White or Black background
                    System.out.print(" " + GameManager.g.board[row][col].symbol + " ");
                } else {
                    // Display empty black or white square
                    System.out.print((row + col) % 2 == 0 ? "\u001B[47m   " : "\u001B[40m   "); // White or Black square
                }
                System.out.print("\u001B[0m"); // Reset to default after each square
            }
            System.out.println(); // New line after each row
        }

    
    }
    void displayBoardReversed() {
        for (int row = 7; row >= 0; row--) { 
            for (int col = 7; col >= 0; col--) { 
                if (GameManager.g.board[row][col] != null) {
                    System.out.print((row + col) % 2 == 0 ? "\u001B[47m" : "\u001B[40m");
                    System.out.print(" " + GameManager.g.board[row][col].symbol + " ");
                } else {
                    System.out.print((row + col) % 2 == 0 ? "\u001B[47m   " : "\u001B[40m   "); 
                }
                System.out.print("\u001B[0m"); 
            }
            System.out.println(); 
        }
    }
    void displayMoves(List<int[]> moves){
        int counter=0;
        int n_moves = moves.size();
        for(int row=0; row<8; row++){
            for(int col=0;col<8;col++){
                if(counter<n_moves){
                    if(row==moves.get(counter)[0] && col==moves.get(counter)[1]){
                        System.out.print((row + col) % 2 == 0 ? "\u001B[47m" : "\u001B[40m"); // White or Black background
                        System.out.print(" " + GameManager.g.board[row][col].symbol);
                        System.out.print(" " + 'X' + " ");
                        counter++;
                    }
                }else if (GameManager.g.board[row][col] != null) {
                    // Display piece on top of the correct background
                    System.out.print((row + col) % 2 == 0 ? "\u001B[47m" : "\u001B[40m"); // White or Black background
                    System.out.print(" " + GameManager.g.board[row][col].symbol + " ");
                } else {
                    // Display empty black or white square
                    System.out.print((row + col) % 2 == 0 ? "\u001B[47m   " : "\u001B[40m   "); // White or Black square
                }
                System.out.print("\u001B[0m");
            }
            System.out.println();
        }
    }

    void displayMovesReversed(List<int[]>moves){
        int counter=moves.size()-1;
        for(int row=8; row>=0; row--){
            for(int col=8;col>=0;col--){
                if(counter>=0){
                    if(row==moves.get(counter)[0] && col==moves.get(counter)[1]){
                        System.out.print((row + col) % 2 == 0 ? "\u001B[47m" : "\u001B[40m"); // White or Black background
                        System.out.print(" " + GameManager.g.board[row][col].symbol);
                        System.out.print(" " + 'X' + " ");
                        counter++;
                    }
                }else if (GameManager.g.board[row][col] != null) {
                    // Display piece on top of the correct background
                    System.out.print((row + col) % 2 == 0 ? "\u001B[47m" : "\u001B[40m"); // White or Black background
                    System.out.print(" " + GameManager.g.board[row][col].symbol + " ");
                } else {
                    // Display empty black or white square
                    System.out.print((row + col) % 2 == 0 ? "\u001B[47m   " : "\u001B[40m   "); // White or Black square
                }
                System.out.print("\u001B[0m");
            }
            System.out.println();
        }
    }

}

class User {
    boolean identifier;
    King king;
    Queen queen;
    Rook[] rooks = new Rook[2];
    Knight[] knights = new Knight[2];
    Pawn[] pawns = new Pawn[8];
    Bishop[] bishops = new Bishop[2];

    User(boolean id) {
        this.identifier = id;
    }
}

abstract class Piece {
    User side;
    int[] loc;
    boolean can_cause_check_to_opp=false; //whether it can check it's opponent or not in the next to next move
    protected char symbol;
    int[] clicked;
    List<int[]> curMoves = new ArrayList<>();
    Piece(User s, int[] pos) {
        this.side = s;
        this.loc = pos;
    }

    abstract void generateMoves();
    
    void kill() {
        GameManager.g.board[this.loc[0]][this.loc[1]] = null;
    }

    void move(int[] next) {
        GameManager.g.board[this.loc[0]][this.loc[1]] = null;
        this.clicked = this.loc;
        this.loc = next;
        GameManager.g.board[next[0]][next[1]] = this;
        GameManager.g.currently_moved = this;
    }

    boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    // public ImageIcon getImage() {
    //     return ChessImageManager.getInstance().getImage(this);
    // }
}

class Rook extends Piece {
    Rook(User s, int[] pos) {
        super(s, pos);
        this.symbol = (GameManager.g.white==s) ? '\u2656' : '\u265C';
    }

    @Override
    void generateMoves() {
        this.curMoves.clear();
        this.curMoves = new ArrayList<>();
        GameManager.allMoves(this, new int[][]{{0,1},{1,0},{0,-1},{-1,0}});
    }
}

class Knight extends Piece {
    static int[][] moves = {
        {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
        {1, -2}, {1, 2}, {2, -1}, {2, 1}
    };

    Knight(User s, int[] pos) {
        super(s, pos);
        this.symbol = (GameManager.g.white==s) ? '\u2658' : '\u265E';

    }

    @Override
    void generateMoves() {
        this.curMoves.clear();
        this.curMoves = new ArrayList<>();
        for (int[] move : moves) {
            int newRow = this.loc[0] + move[0];
            int newCol = this.loc[1] + move[1];
            
            if (isValidPosition(newRow, newCol)) {
                Piece piece = GameManager.g.board[newRow][newCol];
                if (piece == null || piece.side != this.side) {
                    this.curMoves.add(new int[]{newRow, newCol});
                }
            }
        }
    }
}

class Bishop extends Piece {
    Bishop(User s, int[] pos) {
        super(s, pos);
        this.symbol = (GameManager.g.white==s) ? '\u2657' : '\u265D';

    }

    @Override
    void generateMoves() {
        this.curMoves.clear();
        this.curMoves = new ArrayList<>();
        GameManager.allMoves(this, new int[][]{{1,1},{-1,-1},{-1,1},{1,-1}});
    }
}

class Pawn extends Piece {
    Pawn(User s, int[] pos) {
        super(s, pos);
        this.symbol = (GameManager.g.white==s) ? '\u2659' : '\u265F';

    }

    @Override
    void generateMoves() {
        this.curMoves.clear();
        this.curMoves = new ArrayList<>();
        int direction = this.side.identifier ? 1 : -1; // up for white, down for black
        int startRow = this.side.identifier ? 1 : 6;
        
        // Move forward
        if (isValidPosition(loc[0] + direction, loc[1]) && 
            GameManager.g.board[loc[0] + direction][loc[1]] == null) {
            this.curMoves.add(new int[]{loc[0] + direction, loc[1]});
            
            // Double move from starting position
            if (loc[0] == startRow && 
                GameManager.g.board[loc[0] + 2 * direction][loc[1]] == null) {
                this.curMoves.add(new int[]{loc[0] + 2 * direction, loc[1]});
            }
        }
        
        // Capture diagonally
        int[][] captureMoves = {{direction, -1}, {direction, 1}};
        for (int[] move : captureMoves) {
            int newRow = loc[0] + move[0];
            int newCol = loc[1] + move[1];
            if (isValidPosition(newRow, newCol)) {
                Piece piece = GameManager.g.board[newRow][newCol];
                if (piece != null && piece.side != this.side) {
                    this.curMoves.add(new int[]{newRow, newCol});
                }
            }
        }
    }
}

class King extends Piece {
    static int[][] dirs = {
        {-1,-1}, {-1,0}, {-1,1}, {0,-1},
        {0,1}, {1,-1}, {1,0}, {1,1}
    };

    King(User s, int[] pos) {
        super(s, pos);
        this.symbol = (GameManager.g.white==s) ? '\u2654' : '\u265A';

    }

    @Override
    void generateMoves() {
        this.curMoves.clear();
        this.curMoves = new ArrayList<>();
        for (int[] dir : dirs) {
            int newRow = this.loc[0] + dir[0];
            int newCol = this.loc[1] + dir[1];
            if (isValidPosition(newRow, newCol)) {
                Piece piece = GameManager.g.board[newRow][newCol];
                if (piece == null || piece.side != this.side) {
                    this.curMoves.add(new int[]{newRow, newCol});
                }
            }
        }
    }
}

class Queen extends Piece {
    static int[][] dirs = {
        {0,1},{1,0},{0,-1},{-1,0},
        {1,1},{-1,-1},{-1,1},{1,-1}
    };

    Queen(User s, int[] pos) {
        super(s, pos);
        this.symbol = (GameManager.g.white==s) ? '\u2655' : '\u265B';

    }

    @Override
    void generateMoves() {
        this.curMoves.clear();
        this.curMoves = new ArrayList<>();
        GameManager.allMoves(this, dirs);
    }
}

public class GameManager {
    static Game g = new Game();
    public static void main(String[] args) {
        // g.initializeBoard();
        // // while (!g.isGameOver) {
        // //     // TODO: Implement game loop
        // //     // Get move from current player
        // //     // Make move
        // //     // Check for check, checkmate, etc.
        // //     g.switchTurn();
        // // }
        // g.displayBoard();
    }

    public static void allMoves(Piece p, int[][] directions) {
        for (int[] direction : directions) {
            directionMoves(p, direction[0], direction[1]);
        }
    }

    public void run(){
        GameManager.g.initializeBoard();
        System.out.println("WELCOME TO CHESS -------");
        System.out.println("Which one first? 1. Black 2. White");
        Scanner sc = new Scanner(System.in);
        int choice = sc.nextInt();
        switch(choice){
            case 1:
                GameManager.g.currentTurn = GameManager.g.black;
                break;
            case 2:
                GameManager.g.currentTurn = GameManager.g.white;
        }
        GameManager.g.displayBoard();
    }

    private static void directionMoves(Piece p, int rowInc, int colInc) {
        int row = p.loc[0] + rowInc;
        int col = p.loc[1] + colInc;

        while (p.isValidPosition(row, col)) {
            Piece piece = g.board[row][col];
            if (piece != null && piece.side == p.side) break;
            p.curMoves.add(new int[]{row, col});
            if (piece != null && piece.side != p.side) break;

            row += rowInc;
            col += colInc;
        }
    }

    public static boolean checkForCheck(Piece movedPiece, int newRow, int newCol, int oldRow, int oldCol) {
        // Store the original board state
        Piece originalDestPiece = g.board[newRow][newCol];
        
        // Temporarily make the move
        g.board[oldRow][oldCol] = null;
        g.board[newRow][newCol] = movedPiece;
        movedPiece.loc = new int[]{newRow, newCol};
        
        boolean isInCheck = false;
        
        // Get the relevant king (the one belonging to the side that just moved)
        King friendlyKing = (movedPiece.side == g.white) ? g.white.king : g.black.king;
        King enemyKing = (movedPiece.side == g.white) ? g.black.king : g.white.king;
        
        // Check if the move puts the player's own king in check
        isInCheck = isKingInCheck(friendlyKing);
        
        // If own king is not in check, check if the move puts opponent's king in check
        if (!isInCheck) {
            isInCheck = isKingInCheck(enemyKing);
        }
        
        // Restore the original board state
        g.board[oldRow][oldCol] = movedPiece;
        g.board[newRow][newCol] = originalDestPiece;
        movedPiece.loc = new int[]{oldRow, oldCol};
        
        return isInCheck;
    }

    private static boolean isKingInCheck(King king) {
        // Check for threats from all directions
        return isPawnThreat(king) ||
            isKnightThreat(king) ||
            isDiagonalThreat(king) ||
            isOrthogonalThreat(king);
    }

    private static boolean isPawnThreat(King king) {
        int direction = (king.side == g.white) ? 1 : -1;
        int row = king.loc[0];
        int col = king.loc[1];
        
        // Check both diagonal squares that could contain threatening pawns
        for (int colOffset : new int[]{-1, 1}) {
            int checkRow = row - direction;
            int checkCol = col + colOffset;
            
            if (king.isValidPosition(checkRow, checkCol)) {
                Piece p = g.board[checkRow][checkCol];
                if (p != null && p instanceof Pawn && p.side != king.side) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isKnightThreat(King king) {
        int[][] knightMoves = {{-2,-1}, {-2,1}, {-1,-2}, {-1,2}, 
                            {1,-2}, {1,2}, {2,-1}, {2,1}};
        
        for (int[] move : knightMoves) {
            int newRow = king.loc[0] + move[0];
            int newCol = king.loc[1] + move[1];
            
            if (king.isValidPosition(newRow, newCol)) {
                Piece p = g.board[newRow][newCol];
                if (p != null && p instanceof Knight && p.side != king.side) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isDiagonalThreat(King king) {
        int[][] directions = {{-1,-1}, {-1,1}, {1,-1}, {1,1}};
        
        for (int[] dir : directions) {
            int row = king.loc[0] + dir[0];
            int col = king.loc[1] + dir[1];
            
            while (king.isValidPosition(row, col)) {
                Piece p = g.board[row][col];
                if (p != null) {
                    if (p.side != king.side && 
                    (p instanceof Bishop || p instanceof Queen)) {
                        return true;
                    }
                    break;
                }
                row += dir[0];
                col += dir[1];
            }
        }
        return false;
    }

    private static boolean isOrthogonalThreat(King king) {
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        
        for (int[] dir : directions) {
            int row = king.loc[0] + dir[0];
            int col = king.loc[1] + dir[1];
            
            while (king.isValidPosition(row, col)) {
                Piece p = g.board[row][col];
                if (p != null) {
                    if (p.side != king.side && 
                    (p instanceof Rook || p instanceof Queen)) {
                        return true;
                    }
                    break;
                }
                row += dir[0];
                col += dir[1];
            }
        }
        return false;
    }
}
