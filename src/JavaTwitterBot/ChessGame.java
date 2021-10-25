package JavaTwitterBot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//TODO:
//Display all moves for given piece
//Update account

//DONE:
//Checkmate
//Pawn to queen
//Forced moves in check
public class ChessGame {

    public ChessBoard getBoard() {
        return board;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessBot getBot() {
        return bot;
    }

    public void setBot(ChessBot bot) {
        this.bot = bot;
    }

    public Font getChessFont() {
        return chessFont;
    }

    public void setChessFont(Font chessFont) {
        this.chessFont = chessFont;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    ChessBoard board;
    ChessBot bot;
    Font chessFont;
    String playerName;
    String botName;
    Image playerProfilePicture;

    public static void main(String[] args) {
        //ChessGame testChessGame = new ChessGame(new RandomBot(), "Dever");
        //System.out.println(testChessGame.board.getPieceAt(new ChessLocation(3, 1)));
        //testChessGame.generateImage(new ChessLocation[]{new ChessLocation(2,1)}, null);
        //System.out.println(testChessGame.playerMove(new ChessLocation(3,1), new ChessLocation(4, 1)).getResultMessage());
        //System.out.println(testChessGame.playerMove(new ChessLocation(1,1), new ChessLocation(2, 1)).getResultMessage());
        //System.out.println(testChessGame.playerMove(new ChessLocation(2,1), new ChessLocation(4, 1)).getResultMessage());
        //System.out.println(testChessGame.playerMove(new ChessLocation(1,1), new ChessLocation(3, 1)).getResultMessage());
        //System.out.println(testChessGame.playerMove(new ChessLocation(3,1), new ChessLocation(3, 8)).getResultMessage());
        //System.out.println(testChessGame.playerMove(new ChessLocation(3,8), new ChessLocation(4, 7)).getResultMessage());
        //System.out.println(testChessGame.doMove(new ChessLocation(7,1), new ChessLocation(6, 1)).getResultMessage());
        //testChessGame.generateImage(new ChessLocation[]{new ChessLocation(2,1), new ChessLocation(3,1)});
    }

    public ChessGame(ChessBoard board, ChessBot bot, String playerName, Image playerProfilePicture) {
        try {
            this.chessFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/fonts/CASEFONT.TTF"));
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.playerName = playerName;
        this.botName = "ChessB0T";
        this.board = board;
        this.bot = bot;
        this.playerProfilePicture = playerProfilePicture;
    }

    public ChessGame(ChessBot bot, String playerName, Image playerProfilePicture) {
        this(new ChessBoard(), bot, playerName, playerProfilePicture);
    }

    public class MoveResult {

        public File getMoveImage() {
            return moveImage;
        }

        public void setMoveImage(File moveImage) {
            this.moveImage = moveImage;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getResultMessage() {
            return resultMessage;
        }

        public void setResultMessage(String resultMessage) {
            this.resultMessage = resultMessage;
        }

        public int getResult() {
            return this.result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        File moveImage;
        boolean success;
        String resultMessage;
        int result;

        public static final int RESULT_NONE = 0;
        public static final int RESULT_ERROR = 1;
        public static final int RESULT_MOVE = 2;
        public static final int RESULT_CAPTURE = 3;
        public static final int RESULT_WIN = 4;
        public static final int RESULT_LOSS = 4;

        public MoveResult(boolean success, File moveImage, String resultMessage, int result) {
            this.moveImage = moveImage;
            this.success = success;
            this.resultMessage = resultMessage;
            this.result = result;
        }
    }

    public MoveResult playerMove(ChessLocation startPosition, ChessLocation endPosition) {
        File boardImage = this.generateImage(null, null);
        //if (!userPlayerTurn) return new MoveResult(false, null, "It isn't your turn");
        if (this.board.getPieceAt(startPosition) == null) return new MoveResult(false, boardImage, "That space is empty", MoveResult.RESULT_ERROR);
        if (this.board.getPieceAt(startPosition).isBlack()) return new MoveResult(false, boardImage, "That isn't your piece", MoveResult.RESULT_ERROR);
        MoveResult result = this.doMove(startPosition, endPosition, false);
        if (result.success) {
            //userPlayerTurn = false;
            boolean botCheckMate = false;
            if (isPlayerInCheck(false)) {
                System.out.println("bot in check");
                //Player  in check
                if (this.generateEscapeCheckMoves(true).size() == 0) {
                    System.out.println("bot in checkmate");
                    //Bot in checkmate
                    botCheckMate = true;
                    result.setResultMessage(result.getResultMessage() + " You Won!");
                    result.setResult(MoveResult.RESULT_WIN);
                }
            }
            if (!botCheckMate) {
                ChessLocation[] botMove = this.bot.getMove(this);
                if (botMove != null) {
                    this.doMove(botMove[0], botMove[1], false);
                    result.setResultMessage(result.getResultMessage() + "\n" + "Bot Move: " + botMove[0].toString() + " to " + botMove[1].toString());
                }
                if (isPlayerInCheck(true)) {
                    //Player is in check
                    result.setResultMessage(result.getResultMessage() + " You're in Check!");
                    if (this.generateEscapeCheckMoves(false).size() == 0) {
                        //Checkmate
                        result.setResultMessage(result.getResultMessage() + " Checkmate, You Lost!");
                        result.setResult(MoveResult.RESULT_LOSS);
                    }
                }
                result.setMoveImage(this.generateImage(new ChessLocation[]{startPosition, endPosition}, botMove));
            }
        } else {
            result.setMoveImage(boardImage);
        }
        return result;
    }

    public void onPostMove() {
        this.board.pawnsToQueens();
    }

    public MoveResult doMove(ChessLocation startPosition, ChessLocation endPosition, boolean force) {
        boolean canMove = this.board.canMove(startPosition, endPosition);
        //If not a forced move check if it would put you in check
        if ((!force) && (canMove)) {
            canMove = !this.doesMoveResultInCheck(startPosition, endPosition, false);
        }
        if (canMove) {
            //List<File> images = new ArrayList<File>();
            //Get initial state
            //images.add(this.toImage(null));
            ChessPiece pieceToMove = this.board.getPieceAt(startPosition);
            ChessPiece targetPiece = this.board.getPieceAt(endPosition);
            //Highlight the spots
            ChessLocation[] highlightSpots = new ChessLocation[]{startPosition, endPosition};
            //images.add(this.toImage(highlightSpots));
            //Move the piece
            //Remove it
            this.board.setPieceAt(startPosition, null);
            //Replace the other piece
            this.board.setPieceAt(endPosition, pieceToMove);
            boolean kingCaptured = false;
            if (targetPiece != null) {
                this.board.pieceCaptured(targetPiece);
                if (targetPiece instanceof King) {
                    kingCaptured = true;
                }
            }
            MoveResult result = new MoveResult(true, null, "Move Completed: " + startPosition.toString() + " to " + endPosition.toString(), MoveResult.RESULT_MOVE);
            if (kingCaptured) {
                if (targetPiece.isBlack()) {
                    result.setResult(MoveResult.RESULT_WIN);
                    result.setResultMessage(result.getResultMessage() + "You Won!");
                } else {
                    result.setResult(MoveResult.RESULT_LOSS);
                    result.setResultMessage(result.getResultMessage() + "You Lost!");
                }
            }
            this.onPostMove();
            return result;
        }
        //System.out.println("couldn't do move");
        return new MoveResult(false, null, "Error cannot move piece at " + startPosition.toString() + " to " + endPosition.toString(), MoveResult.RESULT_ERROR);
    }

    /*public boolean canMove(ChessLocation startPosition, ChessLocation endPosition, Boolean black) {
        return (this.board.canMove(startPosition, endPosition)) && this.doesMoveResultInCheck(startPosition, endPosition, black);
    }*/

    public File generateImage(ChessLocation[] playerHighlightedSpots, ChessLocation[] botHighlightedSpots) {
        final int PIECE_SIZE = 100;
        final int NAME_SIZE = 50;
        final int NAME_BUFFER = 20;
        final int NUM_PIECES_PER_ROW = 10;
        final int CAP_PIECES_HEIGHT = (((int) (Math.ceil(this.board.getBlackPiecesCaptured().size() / 8) + Math.ceil(this.board.getWhitePiecesCaptured().size() / 8)))) * PIECE_SIZE;
        int width = (PIECE_SIZE * NUM_PIECES_PER_ROW);
        int height = (PIECE_SIZE * NUM_PIECES_PER_ROW) + NAME_SIZE + (NAME_BUFFER) + CAP_PIECES_HEIGHT;
        BufferedImage boardImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) boardImage.createGraphics();
        g.setBackground(Color.white);
        g.setColor(Color.white);
        g.fillRect(0,0,width,height);
        g.setColor(Color.black);

        final int PROF_PIC_SIZE = 100;
        final int PROF_PIC_BUFFER = 20;

        //Draw the profile pics
        Image botProfilePic = null;
        try {
            botProfilePic = ImageIO.read(new File("src/images/ChessBotPP.jpeg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.drawImage(playerProfilePicture, PROF_PIC_BUFFER, PROF_PIC_BUFFER, PROF_PIC_SIZE, PROF_PIC_SIZE,null);
        if (botProfilePic != null) g.drawImage(botProfilePic,width - PROF_PIC_SIZE - PROF_PIC_BUFFER, PROF_PIC_BUFFER, PROF_PIC_SIZE, PROF_PIC_SIZE,null);

        final int MAX_STRING_WIDTH = width - ((PROF_PIC_SIZE + (PROF_PIC_BUFFER * 2)) * 2);
        //Resize the text so it fits
        Font tnr = new Font("Times New Roman", Font.PLAIN, NAME_SIZE);
        g.setFont(tnr);
        String vsString = playerName + " VS " + botName;
        int stringWidth = g.getFontMetrics(tnr).stringWidth(vsString);
        double percentage = 1.0;
        while (stringWidth > MAX_STRING_WIDTH) {
            percentage -= .05;
            tnr = new Font("Times New Roman", Font.PLAIN, (int)(NAME_SIZE * percentage));
            g.setFont(tnr);
            stringWidth = g.getFontMetrics(tnr).stringWidth(vsString);
        }
        //Draw the Title
        g.drawString(vsString, ((width - stringWidth) / 2), NAME_BUFFER+NAME_SIZE);

        //Swap over to the chess font
        g.setFont(new Font(chessFont.getName(), Font.PLAIN, 100));

        //Highlight the highlighted spaces
        if (playerHighlightedSpots != null) {
            for (int i = 0; i < playerHighlightedSpots.length; i++) {
                g.setColor(Color.yellow);
                g.fillRect((playerHighlightedSpots[i].getColumn()) * PIECE_SIZE,
                           height - (((playerHighlightedSpots[i].getRow()) * PIECE_SIZE) + NAME_SIZE + 50 + CAP_PIECES_HEIGHT),
                              PIECE_SIZE,PIECE_SIZE);
            }
        }
        if (botHighlightedSpots != null) {
            for (int i = 0; i < botHighlightedSpots.length; i++) {
                g.setColor(Color.pink);
                g.fillRect((botHighlightedSpots[i].getColumn()) * PIECE_SIZE,
                        height - (((botHighlightedSpots[i].getRow()) * PIECE_SIZE) + NAME_SIZE + 50 + CAP_PIECES_HEIGHT),
                        PIECE_SIZE,PIECE_SIZE);
            }
        }

        //Draw the board
        g.setColor(Color.black);
        int currY = NAME_SIZE + (NAME_BUFFER) + PIECE_SIZE;
        int currX = 0;
        for (int rowNum = 9; rowNum >= 0; rowNum--) {
            String rowText = "";
            if (rowNum == 0) {
                rowText = "D";
                for (int colNum = 1; colNum <= 8; colNum++) {
                    rowText += this.getSideChar(colNum, true);
                }
                rowText += "F";
            } else if (rowNum == 9) {
                rowText = "A";
                for (int colNum = 1; colNum <= 8; colNum++) {
                    rowText += "\"";
                }
                rowText += "S";
            } else {
                rowText += "" + this.getSideChar(rowNum, false);
                for (int colNum = 1; colNum <= 8; colNum++) {
                    rowText += this.board.getSpaceChar(new ChessLocation(rowNum, colNum));
                }
                rowText += "%";
            }
            //System.out.println(rowText);
            g.drawString(rowText, currX, currY);
            currY += PIECE_SIZE;
        }

        //Draw the captured pieces
        System.out.println("Black: " + this.board.getBlackPiecesCaptured().size());
        System.out.println("White: " + this.board.getWhitePiecesCaptured().size());
        for (int i = 0; i < this.board.getWhitePiecesCaptured().size(); i++) {
            System.out.println(i);
            g.drawString(this.board.getPieceChar(this.board.getWhitePiecesCaptured().get(i), false) + "", currX, currY);
            currX += PIECE_SIZE;
            if (i == 7) {
                currX = 0;
                currY += PIECE_SIZE;
            }
        }

        for (int i = 0; i < this.board.getBlackPiecesCaptured().size(); i++) {
            System.out.println(i);
            g.drawString(this.board.getPieceChar(this.board.getBlackPiecesCaptured().get(i), false) + "", currX, currY);
            currX += PIECE_SIZE;
            if (i == 7) {
                currX = 0;
                currY += PIECE_SIZE;
            }
        }

        //g.drawString("KkLlWwQqPp", currX, currY);
        File tempImageLocation = new File("src/images/tempChessImage.jpeg");
        try {
            ImageIO.write(boardImage, "PNG", tempImageLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempImageLocation;
    }

    public char getSideChar(int number, boolean letter) {
        char[] numbers = new char[]{'à','á','â','ã','ä','å','æ','ç'};
        char[] letters = new char[]{'è','é','ê','ë','ì','í','î','ï'};
        if (letter) {
            return letters[number - 1];
        } else {
            return numbers[number - 1];
        }
    }

    public List<ChessLocation> generateLegalMovesAt(ChessLocation location, boolean black) {
        List<ChessLocation> moveList = new ArrayList<>();
        if (this.board.getPieceAt(location) != null) {
            moveList = this.board.getPieceAt(location).generateAllLegalMoves(location, this, black);
        }
        return moveList;
    }

    public List<ChessLocation[]> generateEscapeCheckMoves(boolean black) {
        //Get all possible moves
        List<ChessLocation[]> possibleMoves = generateAllLegalMovesForPlayer(black);
        //Check which don't result in the player being in check
        return possibleMoves.stream().filter((move) -> doesMoveResultInCheck(move[0], move[1], black)).collect(Collectors.toList());
    }

    public List<ChessLocation[]> generateAllLegalMovesForPlayer(boolean black) {
        List<ChessLocation[]> legalMoves = new ArrayList<>();
        for (int rowNum = 8; rowNum >= 1; rowNum--) {
            for (int colNum = 1; colNum <= 8; colNum++) {
                ChessLocation location = new ChessLocation(rowNum, colNum);
                ChessPiece currPiece = board.getPieceAt(location);
                if (currPiece != null) {
                    if (currPiece.isBlack() == black) {
                        legalMoves.addAll(currPiece.generateAllLegalMoves(location, this, black)
                                .stream()
                                .map((endLocation) -> new ChessLocation[]{location, endLocation})
                                .collect(Collectors.toList()));
                    }
                }
            }
        }
        return legalMoves;
    }

    public boolean doesMoveResultInCheck(ChessLocation startLocation, ChessLocation endLocation, boolean black) {
        ChessBoard savedBoard = new ChessBoard(this.board);
        this.doMove(startLocation, endLocation, true);
        boolean inCheck = this.isPlayerInCheck(!black);
        this.board = savedBoard;
        System.out.println(inCheck);
        return inCheck;
    }

    //TODO: Make sure this actually works (95% sure it doesn't)
    public boolean isPlayerInCheck(boolean userPlayer) {
        //Get all legal moves
        //Check if one of them results in the king being taken
        List<ChessLocation> legalMoves = new ArrayList<>();
        ChessLocation kingLocation = null;
        for (int rowNum = 8; rowNum >= 1; rowNum--) {
            for (int colNum = 1; colNum <= 8; colNum++) {
                ChessLocation location = new ChessLocation(rowNum, colNum);
                ChessPiece currPiece = this.board.getPieceAt(location);
                if (currPiece == null) continue;
                if (currPiece.isBlack() == userPlayer) {
                    legalMoves.addAll(currPiece.generateAllLegalMoves(location, this, !userPlayer));
                } else if ((currPiece instanceof King) && (currPiece.isBlack() != userPlayer)) {
                    kingLocation = location;
                }
            }
        }
        System.out.println((userPlayer ? "White" : "Black") + " : " + kingLocation);
        //legalMoves.forEach((loc) -> System.out.println(loc.toString()));
        //boolean check = legalMoves.contains(kingLocation);
        //System.out.println("check? "+ check);
        JavaTwitterBot.ChessLocation finalKingLocation = kingLocation;
        return legalMoves.stream().anyMatch((location) -> (finalKingLocation != null && location.equals(finalKingLocation)));
    }
}
