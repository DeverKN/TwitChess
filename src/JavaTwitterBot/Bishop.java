package JavaTwitterBot;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends ChessPiece {

    public Bishop(boolean black) {
        super(black);
    }

    @Override
    public boolean isMoveLegal(ChessLocation startPosition, ChessLocation endPosition, ChessBoard board) {
        if (!super.isMoveLegal(startPosition, endPosition, board)) return false;
        //Ending location can be empty or occupied by an enemy piece
        int rowDiff = Math.abs(startPosition.getRow() - endPosition.getRow());
        int colDiff = Math.abs(startPosition.getColumn() - endPosition.getColumn());
        //Must be diagonal move
        if (!(rowDiff == colDiff)) return false;
        //Every location between start and end must be empty
        if (!board.allLocationsBetweenEmpty(startPosition, endPosition)) return false;
        return true;
    }

    //TODO: Definitely a better way to do this
    public List<ChessLocation> generateAllPseudoLegalMoves(ChessLocation startPosition, ChessBoard board) {
        List<ChessLocation> legalMoves = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            legalMoves.add(new ChessLocation(startPosition.getRow() + i, startPosition.getColumn() + i));
            legalMoves.add(new ChessLocation(startPosition.getRow() - i, startPosition.getColumn() - i));
            legalMoves.add(new ChessLocation(startPosition.getRow() - i, startPosition.getColumn() + i));
            legalMoves.add(new ChessLocation(startPosition.getRow() + i, startPosition.getColumn() - i));
        }
        return legalMoves;
    }
}
