package JavaTwitterBot;

import java.util.ArrayList;
import java.util.List;

public class Rook extends ChessPiece{

    public Rook(boolean black) {
        super(black);
    }

    @Override
    public boolean isMoveLegal(ChessLocation startPosition, ChessLocation endPosition, ChessBoard board) {
        if (!super.isMoveLegal(startPosition, endPosition, board)) return false;
        //Ending location can be empty or occupied by an enemy piece
        int rowDiff = Math.abs(startPosition.getRow() - endPosition.getRow());
        int colDiff = Math.abs(startPosition.getColumn() - endPosition.getColumn());
        //Either rowDiff != 0 and colDiff == 0 or rowDiff == 0 and colDiff != 0
        boolean rowMove = (rowDiff != 0);
        boolean colMove = (colDiff != 0);
        if (!(rowMove ^ colMove)) return false;
        //Every location between start and end must be empty
        if (!board.allLocationsBetweenEmpty(startPosition, endPosition)) return false;
        return true;
    }

    public List<ChessLocation> generateAllPseudoLegalMoves(ChessLocation startPosition, ChessBoard board) {
        List<ChessLocation> legalMoves = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            legalMoves.add(new ChessLocation(startPosition.getRow(), i));
            legalMoves.add(new ChessLocation(i, startPosition.getColumn()));
        }
        return legalMoves;
    }
}
