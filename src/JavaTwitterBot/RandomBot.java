package JavaTwitterBot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomBot implements ChessBot {

    private Random randomNum;
    private final String name = "Rand0mB0t";
    public RandomBot() {
        this.randomNum = new Random();
    }

    public ChessLocation[] getMove(ChessGame game) {
        List<ChessLocation[]> legalMoves = new ArrayList<>();
        if (game.isPlayerInCheck(true)) {
            legalMoves = game.generateEscapeCheckMoves(true);
        } else {
            legalMoves = game.generateAllLegalMovesForPlayer(true);
        }
        /*for (int rowNum = 8; rowNum >= 1; rowNum--) {
            for (int colNum = 1; colNum <= 8; colNum++) {
                ChessLocation location = new ChessLocation(rowNum, colNum);
                ChessPiece currPiece = game.getBoard().getPieceAt(location);
                if (currPiece != null) {
                    if (currPiece.isBlack()) {
                        legalMoves.addAll(currPiece.generateAllLegalMoves(location, game, true)
                                .stream()
                                .map((endLocation) -> new ChessLocation[]{location, endLocation})
                                .collect(Collectors.toList()));
                    }
                }
            }
        }*/
        if (legalMoves.size() > 0) {
            return legalMoves.get(randomNum.nextInt(legalMoves.size() - 1));
        } else {
            return null;
        }
    }

    public String getBotName() {
        return name;
    }
}
