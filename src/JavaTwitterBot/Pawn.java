package JavaTwitterBot;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends ChessPiece{

    private final int BLACK_START_ROW = 7;
    private final int WHITE_START_ROW = 2;

    public Pawn(boolean black) {
        super(black);
    }

    @Override
    public boolean isMoveLegal(ChessLocation startPosition, ChessLocation endPosition, ChessBoard board) {
        if (!super.isMoveLegal(startPosition, endPosition, board)) return false;
        //Check if the move is legal;
        //Check if the move is straight (a regular move) or diagonal (a capture)
        int rowDiff = startPosition.getRow() - endPosition.getRow();
        int colDiff = startPosition.getColumn() - endPosition.getColumn();
        int moveDistance = Math.abs(rowDiff);
        if (moveDistance == 0) return false;
        int moveDirection = rowDiff/moveDistance;
        //must be forward
        boolean forward = (this.isBlack() ? (moveDirection == 1) : (moveDirection == -1));
        if (!forward) return false;
        if (colDiff == 0) {
            //Straight (move)
            //Rules:
            //must be 1 space (or 2 if in starting row)
            boolean oneSpace = (moveDistance == 1);
            boolean twoSpaces = (moveDistance == 2) && this.isInStartingRow(startPosition);
            //If two spaces the space in passed through must be clear
            if (twoSpaces) {
                if (!(board.allLocationsBetweenEmpty(startPosition, endPosition))) return false;
            }
            //System.out.println("1" + oneSpace + "2" + twoSpaces);
            if (!(oneSpace || twoSpaces)) return false;
            //must be to an unoccupied space
            if (!board.isSpaceEmpty(endPosition)) return false;
        } else {
            //Diagonal (capture)
            //Must be to the side one space and one space ahead
            if ((Math.abs(colDiff) != 1) || (moveDistance != 1)) return false;
            //Target must be an enemy piece
            if (board.isSpaceEmpty(endPosition)) return false;
            if (this.isBlack() == board.getPieceAt(endPosition).isBlack()) return false;
        }
        return true;
    }

    public List<ChessLocation> generateAllPseudoLegalMoves(ChessLocation startPosition, ChessBoard board) {
        List<ChessLocation> legalMoves = new ArrayList<>();
        int direction = (this.isBlack() ? -1 : 1);
        legalMoves.add(new ChessLocation(startPosition.getRow() + direction, startPosition.getColumn()));
        legalMoves.add(new ChessLocation(startPosition.getRow() + direction, startPosition.getColumn() + 1));
        legalMoves.add(new ChessLocation(startPosition.getRow() + direction, startPosition.getColumn() - 1));
        legalMoves.add(new ChessLocation(startPosition.getRow(), startPosition.getColumn()));
        legalMoves.add(new ChessLocation(startPosition.getRow(), startPosition.getColumn() + 1));
        legalMoves.add(new ChessLocation(startPosition.getRow(), startPosition.getColumn() - 1));
        if (this.isInStartingRow(startPosition)) {
            legalMoves.add(new ChessLocation(startPosition.getRow() + (2 * direction), startPosition.getColumn()));
        }
        return legalMoves;
    }

    public boolean isInStartingRow(ChessLocation location) {
        if (this.isBlack()) {
            return location.getRow() == BLACK_START_ROW;
        } else {
            return location.getRow() == WHITE_START_ROW;
        }
    }
}
