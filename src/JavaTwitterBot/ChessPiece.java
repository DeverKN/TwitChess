package JavaTwitterBot;

import java.util.List;
import java.util.stream.Collectors;

public class ChessPiece {

    private Boolean black;

    public ChessPiece(Boolean black) {
        this.black = black;
    }

    public boolean isMoveLegal(ChessLocation startPosition, ChessLocation endPosition, ChessBoard board) {
        //Check if the target position is on the board
        if (!board.isSpaceOnBoard(endPosition)) return false;
        //Can't move to the same spot
        if (startPosition.equals(endPosition)){
            //System.out.println("Cannot move to same spot");
            return false;
        }
        //Check if the target position is occupied by a friendly piece
        //System.out.println("Move ok");
        if ((board.getPieceAt(endPosition) != null) && (board.getPieceAt(endPosition).isBlack() == this.isBlack())) return false;
        return true;
    }

    public List<ChessLocation> generateAllLegalMoves(ChessLocation startPosition, ChessGame game, boolean black) {
        return this.generateAllPseudoLegalMoves(startPosition, game.getBoard())
                    .stream().filter((endPosition) -> game.board.canMove(startPosition, endPosition))
                    .collect(Collectors.toList());
    }

    public List<ChessLocation> generateAllPseudoLegalMoves(ChessLocation startPosition, ChessBoard board) {
        return null;
    }

    public Boolean isBlack() {
        return black;
    }

    public void setBlack(Boolean black) {
        this.black = black;
    }
}
