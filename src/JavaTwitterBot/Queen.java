package JavaTwitterBot;

import java.util.ArrayList;
import java.util.List;

public class Queen extends ChessPiece{

    public Queen(boolean black) {
        super(black);
    }

    @Override
    public boolean isMoveLegal(ChessLocation startPosition, ChessLocation endPosition, ChessBoard board) {
        if (!super.isMoveLegal(startPosition, endPosition, board)) return false;
        //Ending location can be empty or occupied by an enemy piece
        int rowDiff = Math.abs(startPosition.getRow() - endPosition.getRow());
        int colDiff = Math.abs(startPosition.getColumn() - endPosition.getColumn());
        //Must be diagonal move or a vertical/horizontal move
        boolean rowMove = (rowDiff != 0);
        boolean colMove = (colDiff != 0);
        if (!((rowDiff == colDiff) || (rowMove ^ colMove))) return false;
        //Every location between start and end must be empty
        if (!board.allLocationsBetweenEmpty(startPosition, endPosition)) return false;
        return true;
    }

    //TODO: Definitely a better way to do this
    public List<ChessLocation> generateAllPseudoLegalMoves(ChessLocation startPosition, ChessBoard board) {
        List<ChessLocation> legalMoves = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int n = 1; n <= 8; n++) {
                legalMoves.add(new ChessLocation(i, n));
            }
        }
        return legalMoves;
    }

}
