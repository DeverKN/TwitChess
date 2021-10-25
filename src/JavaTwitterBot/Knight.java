package JavaTwitterBot;

import java.util.ArrayList;
import java.util.List;

public class Knight extends ChessPiece{

    public Knight(boolean black) {
        super(black);
    }

    @Override
    public boolean isMoveLegal(ChessLocation startPosition, ChessLocation endPosition, ChessBoard board) {
        if (!super.isMoveLegal(startPosition, endPosition, board)) return false;
        //Ending location can be empty or occupied by an enemy piece
        int rowDiff = Math.abs(startPosition.getRow() - endPosition.getRow());
        int colDiff = Math.abs(startPosition.getColumn() - endPosition.getColumn());
        //One diff must be 2 and the other must be 1
        return ((rowDiff == 1 && colDiff == 2) || (rowDiff == 2 && colDiff == 1));
        //return true;
    }

    public List<ChessLocation> generateAllPseudoLegalMoves(ChessLocation startPosition, ChessBoard board) {
        List<ChessLocation> legalMoves = new ArrayList<>();
        legalMoves.add(new ChessLocation(startPosition.getRow() + 2, startPosition.getColumn() + 1));
        legalMoves.add(new ChessLocation(startPosition.getRow() + 2, startPosition.getColumn() - 1));
        legalMoves.add(new ChessLocation(startPosition.getRow() - 2, startPosition.getColumn() + 1));
        legalMoves.add(new ChessLocation(startPosition.getRow() - 2, startPosition.getColumn() - 1));
        legalMoves.add(new ChessLocation(startPosition.getRow() + 1, startPosition.getColumn() + 2));
        legalMoves.add(new ChessLocation(startPosition.getRow() + 1, startPosition.getColumn() - 2));
        legalMoves.add(new ChessLocation(startPosition.getRow() - 1, startPosition.getColumn() + 2));
        legalMoves.add(new ChessLocation(startPosition.getRow() - 1, startPosition.getColumn() - 2));
        return legalMoves;
    }

}
