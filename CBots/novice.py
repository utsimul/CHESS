import chess
import pygame
import sys
import time

class ChessGame:
    def __init__(self):
        # Initialize pygame
        pygame.init()
        
        # Initialize chess board
        self.board = chess.Board()
        
        # Screen dimensions
        self.SCREEN_WIDTH = 600
        self.SCREEN_HEIGHT = 650
        self.BOARD_SIZE = 500
        self.SQUARE_SIZE = self.BOARD_SIZE // 8
        
        # Colors
        self.LIGHT_SQUARE = (240, 217, 181)  # Chess light square color
        self.DARK_SQUARE = (181, 136, 99)    # Chess dark square color
        self.HIGHLIGHT = (169, 169, 255)     # Blue for selected piece
        self.POSSIBLE_MOVE = (255, 255, 153) # Yellow for possible moves
        self.BACKGROUND = (50, 50, 50)       # Dark background
        self.TEXT_WHITE = (255, 255, 255)    # White text
        self.TEXT_BLACK = (0, 0, 0)          # Black text
        
        # Create screen
        self.screen = pygame.display.set_mode((self.SCREEN_WIDTH, self.SCREEN_HEIGHT))
        pygame.display.set_caption("Python Chess Game")
        
        # Fonts - using a font that supports chess symbols
        try:
            # Try Windows font
            self.piece_font = pygame.font.Font("Arial", 36)
        except:
            try:
                # Try Linux font
                self.piece_font = pygame.font.Font("NotoSans-Regular.ttf", 36)
            except:
                try:
                    # Try Mac-specific fonts
                    self.piece_font = pygame.font.SysFont("Apple Symbols", 36)
                except:
                    try:
                        self.piece_font = pygame.font.SysFont("Arial Unicode MS", 36)
                    except:
                        # Final fallback
                        self.piece_font = pygame.font.SysFont(None, 36)
                        print("Using default font - chess symbols may not display correctly")
        
        self.ui_font = pygame.font.SysFont("Arial", 24)
        self.small_font = pygame.font.SysFont("Arial", 16)
        
        # Chess piece Unicode characters
        self.unicode_pieces = {
            'r': '\u265C',  # Black rook
            'n': '\u265E',  # Black knight
            'b': '\u265D',  # Black bishop
            'q': '\u265B',  # Black queen
            'k': '\u265A',  # Black king
            'p': '\u265F',  # Black pawn
            'R': '\u2656',  # White rook
            'N': '\u2658',  # White knight
            'B': '\u2657',  # White bishop
            'Q': '\u2655',  # White queen
            'K': '\u2654',  # White king
            'P': '\u2659'   # White pawn
        }

        
        # Game variables
        self.selected_piece = None
        self.highlighted_squares = []
        self.move_history = []
        self.difficulty = 2  # Default difficulty
        
        # Main game loop
        self.running = True
        self.run()
    
    def draw_board(self):
        """Draw the chess board and pieces"""
        # Draw board background
        self.screen.fill(self.BACKGROUND)
        
        # Draw chess board squares
        for row in range(8):
            for col in range(8):
                x = col * self.SQUARE_SIZE + (self.SCREEN_WIDTH - self.BOARD_SIZE) // 2
                y = row * self.SQUARE_SIZE + 20
                
                # Alternate square colors
                if (row + col) % 2 == 0:
                    color = self.LIGHT_SQUARE
                else:
                    color = self.DARK_SQUARE
                
                # Highlight selected square and possible moves
                square = chess.square(col, 7 - row)
                if square == self.selected_piece:
                    color = self.HIGHLIGHT
                elif square in self.highlighted_squares:
                    color = self.POSSIBLE_MOVE
                
                pygame.draw.rect(self.screen, color, (x, y, self.SQUARE_SIZE, self.SQUARE_SIZE))
                
                # Draw piece if present
                piece = self.board.piece_at(square)
                if piece:
                    piece_char = self.unicode_pieces[piece.symbol()]
                    text_color = self.TEXT_WHITE if piece.color == chess.WHITE else self.TEXT_BLACK
                    text = self.piece_font.render(piece_char, True, text_color)
                    text_rect = text.get_rect(center=(x + self.SQUARE_SIZE//2, y + self.SQUARE_SIZE//2))
                    self.screen.blit(text, text_rect)
        
        # Draw status
        status_text = self.get_status_text()
        status = self.ui_font.render(status_text, True, self.TEXT_WHITE)
        self.screen.blit(status, (20, self.BOARD_SIZE + 40))
        
        # Draw buttons
        self.draw_button("New Game", 20, self.BOARD_SIZE + 80, 120, 40)
        self.draw_button("Undo Move", 160, self.BOARD_SIZE + 80, 120, 40)
        
        # Draw difficulty selector
        diff_text = self.small_font.render("Bot Difficulty:", True, self.TEXT_WHITE)
        self.screen.blit(diff_text, (300, self.BOARD_SIZE + 80))
        
        for i, level in enumerate(["1", "2", "3"]):
            color = (100, 200, 100) if self.difficulty == i+1 else (70, 70, 70)
            pygame.draw.rect(self.screen, color, (400 + i*50, self.BOARD_SIZE + 80, 40, 40))
            level_text = self.ui_font.render(level, True, self.TEXT_WHITE)
            self.screen.blit(level_text, (420 + i*50 - level_text.get_width()//2, 
                            self.BOARD_SIZE + 100 - level_text.get_height()//2))
        
        pygame.display.flip()
    
    def draw_button(self, text, x, y, width, height):
        """Draw a button with text"""
        pygame.draw.rect(self.screen, (70, 70, 70), (x, y, width, height))
        pygame.draw.rect(self.screen, (100, 100, 100), (x, y, width, height), 2)
        text_surf = self.small_font.render(text, True, self.TEXT_WHITE)
        self.screen.blit(text_surf, (x + width//2 - text_surf.get_width()//2, 
                                   y + height//2 - text_surf.get_height()//2))
    
    def get_status_text(self):
        """Get the current game status text"""
        if self.board.is_checkmate():
            winner = "Black" if self.board.turn == chess.WHITE else "White"
            return f"Checkmate! {winner} wins!"
        elif self.board.is_stalemate():
            return "Stalemate!"
        elif self.board.is_insufficient_material():
            return "Draw by insufficient material!"
        elif self.board.is_check():
            return f"{'White' if self.board.turn == chess.WHITE else 'Black'} is in check!"
        else:
            return f"{'White' if self.board.turn == chess.WHITE else 'Black'}'s turn"
    
    def handle_click(self, pos):
        """Handle mouse click events"""
        x, y = pos
        
        # Check if difficulty level was clicked
        if self.BOARD_SIZE + 80 <= y <= self.BOARD_SIZE + 120:
            for i in range(3):
                if 400 + i*50 <= x <= 440 + i*50:
                    self.difficulty = i + 1
                    return
        
        # Check if New Game button was clicked
        if 20 <= x <= 140 and self.BOARD_SIZE + 80 <= y <= self.BOARD_SIZE + 120:
            self.reset_game()
            return
        
        # Check if Undo Move button was clicked
        if 160 <= x <= 280 and self.BOARD_SIZE + 80 <= y <= self.BOARD_SIZE + 120:
            self.undo_move()
            return
        
        # Check if chess square was clicked
        board_x = x - (self.SCREEN_WIDTH - self.BOARD_SIZE) // 2
        board_y = y - 20
        
        if 0 <= board_x < self.BOARD_SIZE and 0 <= board_y < self.BOARD_SIZE:
            col = board_x // self.SQUARE_SIZE
            row = board_y // self.SQUARE_SIZE
            square = chess.square(col, 7 - row)
            piece = self.board.piece_at(square)
            
            # If no piece is selected, select the clicked piece if it's the player's turn
            if self.selected_piece is None:
                if piece and piece.color == self.board.turn:
                    self.selected_piece = square
                    self.highlighted_squares = [move.to_square for move in self.board.legal_moves 
                                              if move.from_square == square]
            else:
                # Try to make a move
                move = chess.Move(self.selected_piece, square)
                
                # Check if it's a promotion
                if (piece and piece.piece_type == chess.PAWN and 
                    (chess.square_rank(square) == 7 or chess.square_rank(square) == 0)):
                    move = chess.Move(self.selected_piece, square, promotion=chess.QUEEN)
                
                if move in self.board.legal_moves:
                    self.make_move(move)
                else:
                    # If another piece of the same color is clicked, select that instead
                    if piece and piece.color == self.board.turn:
                        self.selected_piece = square
                        self.highlighted_squares = [move.to_square for move in self.board.legal_moves 
                                                  if move.from_square == square]
                    else:
                        # Invalid move, deselect
                        self.selected_piece = None
                        self.highlighted_squares = []
    
    def make_move(self, move):
        """Make a move on the board"""
        self.move_history.append(self.board.fen())  # Save current state
        
        self.board.push(move)
        self.selected_piece = None
        self.highlighted_squares = []
        
        # Check if game is over
        if self.board.is_game_over():
            return
        
        # If it's now the bot's turn, make a move
        if self.board.turn == chess.BLACK:  # Human is white, bot is black
            self.make_bot_move()
    
    def make_bot_move(self):
        """Let the custom bot make a move"""
        try:
            # Find best move using minimax
            best_move = self.find_best_move(self.difficulty)
            
            if best_move:
                self.make_move(best_move)
        except Exception as e:
            print(f"Bot move failed: {str(e)}")
    
    def evaluate_board(self):
        """Simple evaluation function for the bot"""
        piece_values = {
            chess.PAWN: 1,
            chess.KNIGHT: 3,
            chess.BISHOP: 3,
            chess.ROOK: 5,
            chess.QUEEN: 9,
            chess.KING: 0
        }
        
        score = 0
        
        # Material count
        for square in chess.SQUARES:
            piece = self.board.piece_at(square)
            if piece:
                value = piece_values[piece.piece_type]
                if piece.color == chess.WHITE:
                    score += value
                else:
                    score -= value
        
        # Check/checkmate bonus
        if self.board.is_checkmate():
            if self.board.turn == chess.WHITE:
                score -= 1000  # Black wins
            else:
                score += 1000  # White wins
        elif self.board.is_check():
            if self.board.turn == chess.WHITE:
                score += 0.5  # Black is in check
            else:
                score -= 0.5  # White is in check
        
        return score
    
    def find_best_move(self, depth):
        """Find the best move using minimax with alpha-beta pruning"""
        best_move = None
        best_value = -float('inf')
        alpha = -float('inf')
        beta = float('inf')
        
        for move in self.board.legal_moves:
            self.board.push(move)
            move_value = self.minimax(depth - 1, alpha, beta, False)
            self.board.pop()
            
            if move_value > best_value:
                best_value = move_value
                best_move = move
            
            alpha = max(alpha, best_value)
            if alpha >= beta:
                break
        
        return best_move
    
    def minimax(self, depth, alpha, beta, maximizing_player):
        """Minimax algorithm with alpha-beta pruning"""
        if depth == 0 or self.board.is_game_over():
            return self.evaluate_board()
        
        if maximizing_player:
            max_eval = -float('inf')
            for move in self.board.legal_moves:
                self.board.push(move)
                eval = self.minimax(depth - 1, alpha, beta, False)
                self.board.pop()
                max_eval = max(max_eval, eval)
                alpha = max(alpha, eval)
                if beta <= alpha:
                    break
            return max_eval
        else:
            min_eval = float('inf')
            for move in self.board.legal_moves:
                self.board.push(move)
                eval = self.minimax(depth - 1, alpha, beta, True)
                self.board.pop()
                min_eval = min(min_eval, eval)
                beta = min(beta, eval)
                if beta <= alpha:
                    break
            return min_eval
    
    def reset_game(self):
        """Reset the game to initial state"""
        self.board.reset()
        self.selected_piece = None
        self.highlighted_squares = []
        self.move_history = []
    
    def undo_move(self):
        """Undo the last move"""
        if len(self.move_history) > 0:
            self.board.set_fen(self.move_history.pop())
            self.selected_piece = None
            self.highlighted_squares = []
    
    def run(self):
        """Main game loop"""
        while self.running:
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    self.running = False
                elif event.type == pygame.MOUSEBUTTONDOWN:
                    if event.button == 1:  # Left mouse button
                        self.handle_click(event.pos)
            
            self.draw_board()
            pygame.time.delay(30)
        
        pygame.quit()
        sys.exit()

if __name__ == "__main__":
    game = ChessGame()