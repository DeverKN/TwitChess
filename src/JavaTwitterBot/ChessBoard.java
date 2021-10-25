package JavaTwitterBot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessBoard {

    Map<Integer, Map<Integer, ChessPiece>> board;

    public Map<Integer, Map<Integer, ChessPiece>> getBoard() {
        return board;
    }

    public void setBoard(Map<Integer, Map<Integer, ChessPiece>> board) {
        this.board = board;
    }

    public List<ChessPiece> getBlackPiecesCaptured() {
        return blackPiecesCaptured;
    }

    public void setBlackPiecesCaptured(List<ChessPiece> blackPiecesCaptured) {
        this.blackPiecesCaptured = blackPiecesCaptured;
    }

    public List<ChessPiece> getWhitePiecesCaptured() {
        return whitePiecesCaptured;
    }

    public void setWhitePiecesCaptured(List<ChessPiece> whitePiecesCaptured) {
        this.whitePiecesCaptured = whitePiecesCaptured;
    }

    List<ChessPiece> blackPiecesCaptured;
    List<ChessPiece> whitePiecesCaptured;

    public ChessBoard() {
        this.setupBoard();
    }

    public ChessBoard(ChessBoard otherboard) {
        this.setupBoard();
        //this.board = new HashMap<Integer, Map<Integer, ChessPiece>>();
        for (int rowNum = 8; rowNum >= 1; rowNum--) {
            for (int colNum = 1; colNum <= 8; colNum++) {
                ChessLocation location = new ChessLocation(rowNum, colNum);
                this.setPieceAt(location, otherboard.getPieceAt(location));
            }
        }
        this.whitePiecesCaptured = otherboard.getWhitePiecesCaptured();
        this.blackPiecesCaptured = otherboard.getBlackPiecesCaptured();
    }

    public boolean canMove(ChessLocation startPosition, ChessLocation endPosition) {
        ChessPiece pieceToMove = this.getPieceAt(startPosition);
        if (pieceToMove == null) {
            //System.out.println("null piece");
            return false;
        }
        ChessPiece targetPiece = this.getPieceAt(endPosition);
        //System.out.println(targetPiece);
        if ((targetPiece != null) && (targetPiece.isBlack() == pieceToMove.isBlack())) {
            //System.out.println("friendly fire");
            return false;
        }
        boolean canMove = pieceToMove.isMoveLegal(startPosition, endPosition, this);
        //System.out.println("Can: "+ canMove);
        return (canMove);
    }

    public char getSpaceChar(ChessLocation location) {
        return this.getPieceChar(this.getPieceAt(location), (location.getRow() % 2 == location.getColumn() % 2));
    }

    public char getPieceChar(ChessPiece piece, boolean blackBackground) {
        char spaceChar = ' ';
        if (piece == null) return (blackBackground ? '+' : ' ');
        //System.out.println("Class: " + piece.getClass());
        if (piece instanceof King) {
            spaceChar = (piece.isBlack() ? 'l' : 'k');
        } else if (piece instanceof Queen) {
            spaceChar = (piece.isBlack() ? 'w' : 'q');
        } else if (piece instanceof Knight) {
            spaceChar = (piece.isBlack() ? 'm' : 'n');
        } else if (piece instanceof Bishop) {
            spaceChar = (piece.isBlack() ? 'v' : 'b');
        } else if (piece instanceof Rook) {
            spaceChar = (piece.isBlack() ? 't' : 'r');
        } else if (piece instanceof Pawn) {
            spaceChar = (piece.isBlack() ? 'o' : 'p');
        }
        return (blackBackground ? Character.toUpperCase(spaceChar) : Character.toLowerCase(spaceChar));
    }

    public void pieceCaptured(ChessPiece piece) {
        System.out.println("Piece captured");
        if (piece.isBlack()) {
            this.blackPiecesCaptured.add(piece);
        } else {
            this.whitePiecesCaptured.add(piece);
        }
    }

    public void setupBoard() {
        this.board = new HashMap<Integer, Map<Integer, ChessPiece>>();
        this.blackPiecesCaptured = new ArrayList<ChessPiece>();
        this.whitePiecesCaptured = new ArrayList<ChessPiece>();
        for (int rowNum = 1; rowNum <= 8; rowNum++) {
            Map<Integer, ChessPiece> row = new HashMap<Integer, ChessPiece>();
            if ((rowNum == 1) || (rowNum == 8)) {
                row.put(1, new Rook((rowNum == 8)));
                row.put(2, new Knight((rowNum == 8)));
                row.put(3, new Bishop((rowNum == 8)));
                row.put(4, new Queen((rowNum == 8)));
                row.put(5, new King((rowNum == 8)));
                row.put(6, new Bishop((rowNum == 8)));
                row.put(7, new Knight((rowNum == 8)));
                row.put(8, new Rook((rowNum == 8)));
            } else if ((rowNum == 2) || (rowNum == 7)) {
                for (int colNum = 1; colNum <= 8; colNum++) {
                    row.put(colNum, new Pawn((rowNum == 7)));
                }
            } else {
                for (int colNum = 1; colNum <= 8; colNum++) {
                    row.put(colNum, null);
                }
            }
            //System.out.println(rowNum);
            //System.out.println(row.toString());
            board.put(rowNum, row);
        }
    }

    public ChessPiece getPieceAt(ChessLocation location) {
        if (this.isSpaceOnBoard(location)) {
            return this.board.get(location.getRow()).get(location.getColumn());
        } else {
            return null;
        }
    }

    public boolean setPieceAt(ChessLocation location, ChessPiece piece) {
        if (this.isSpaceOnBoard(location)) {
            this.board.get(location.getRow()).put(location.getColumn(), piece);
            return true;
        } else {
            return false;
        }
    }

    public boolean isSpaceEmpty(ChessLocation location) {
        return this.getPieceAt(location) == null;
    }

    public boolean isSpaceOnBoard(ChessLocation location) {
        return (location.getRow() >= 1) && (location.getColumn() >= 1) && (location.getRow() <= 8) && (location.getColumn() <= 8);
    }

    //TODO: Allow for diagonals
    public boolean allLocationsBetweenEmpty(ChessLocation startPosition, ChessLocation endPosition) {

        int rowDiff = Math.abs(startPosition.getRow() - endPosition.getRow());
        int colDiff = Math.abs(startPosition.getColumn() - endPosition.getColumn());

        int colNum = Math.min(startPosition.getColumn(), endPosition.getColumn());
        int rowNum = Math.min(startPosition.getRow(), endPosition.getRow());
        int maxCol = Math.max(startPosition.getColumn(), endPosition.getColumn());
        int maxRow = Math.max(startPosition.getRow(), endPosition.getRow());
        boolean done = false;
        boolean isEmpty = true;
        while (!done) {
            int doneMaybe = 0;
            if (colNum < maxCol) {
                colNum++;
            } else {
                doneMaybe++;
            }
            if (rowNum < maxRow) {
                rowNum++;
            } else {
                doneMaybe++;
            }
            done = (doneMaybe == 2);
            ChessLocation location = new ChessLocation(rowNum, colNum);
            if (!(location.equals(endPosition) || location.equals(startPosition))) {
                //System.out.println(location + " Is Empty? " + this.isSpaceEmpty(location));
                isEmpty = isEmpty && this.isSpaceEmpty(location);
            }
        }

        /*boolean isEmpty = true;
        if (rowDiff == colDiff) {
            //Diagonal
            for (int i = 1; i < colDiff; i++) {
                isEmpty = isEmpty && this.isSpaceEmpty(new ChessLocation(Math.min(startPosition.getRow(), endPosition.getRow()) + i, Math.min(startPosition.getColumn(), endPosition.getColumn()) + i));
            }
        } else if (rowDiff == 0) {
            //Check every column
            for (int i = 1; i < colDiff; i++) {
                isEmpty = isEmpty && this.isSpaceEmpty(new ChessLocation(startPosition.getRow(), Math.min(startPosition.getColumn(), endPosition.getColumn()) + i));
            }
        } else if (colDiff == 0) {
            //Check every row
            for (int i = 1; i < rowDiff; i++) {
                isEmpty = isEmpty && this.isSpaceEmpty(new ChessLocation(Math.min(startPosition.getRow(), endPosition.getRow()) + i, startPosition.getColumn()));
            }
        } else {
            isEmpty = false;
        }*/
        return isEmpty;
    }

    public void pawnsToQueens() {
        for (int colNum = 1; colNum <= 8; colNum++) {
            ChessLocation row1Loc = new ChessLocation(1, colNum);
            ChessPiece row1Piece = this.getPieceAt(row1Loc);
            ChessLocation row8Loc = new ChessLocation(8, colNum);
            ChessPiece row8Piece = this.getPieceAt(row8Loc);
            if (row1Piece instanceof Pawn) this.setPieceAt(row1Loc, new Queen(row1Piece.isBlack()));
            if (row8Piece instanceof Pawn) this.setPieceAt(row8Loc, new Queen(row8Piece.isBlack()));
        }
    }
}
