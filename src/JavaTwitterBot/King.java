package JavaTwitterBot;

import java.util.ArrayList;
import java.util.List;

public class King extends ChessPiece {

    public King(boolean black) {
        super(black);
    }

    @Override
    public boolean isMoveLegal(ChessLocation startPosition, ChessLocation endPosition, ChessBoard board) {
        if (!super.isMoveLegal(startPosition, endPosition, board)) return false;
        int rowDiff = Math.abs(startPosition.getRow() - endPosition.getRow());
        int colDiff = Math.abs(startPosition.getColumn() - endPosition.getColumn());
        //Must only be a move of at most 1 in any direction
        if (rowDiff > 1 || colDiff > 1) return false;
        return true;
    }

    public List<ChessLocation> generateAllPseudoLegalMoves(ChessLocation startPosition, ChessBoard board) {
        List<ChessLocation> legalMoves = new ArrayList<>();
        legalMoves.add(new ChessLocation(startPosition.getRow() + 1, startPosition.getColumn() + 1));
        legalMoves.add(new ChessLocation(startPosition.getRow() + 1, startPosition.getColumn() - 1));
        legalMoves.add(new ChessLocation(startPosition.getRow() - 1, startPosition.getColumn() + 1));
        legalMoves.add(new ChessLocation(startPosition.getRow() - 1, startPosition.getColumn() - 1));
        legalMoves.add(new ChessLocation(startPosition.getRow(), startPosition.getColumn() + 1));
        legalMoves.add(new ChessLocation(startPosition.getRow(), startPosition.getColumn() - 1));
        legalMoves.add(new ChessLocation(startPosition.getRow() + 1, startPosition.getColumn()));
        legalMoves.add(new ChessLocation(startPosition.getRow() - 1, startPosition.getColumn()));
        return legalMoves;
    }

}
