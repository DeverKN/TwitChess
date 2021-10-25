package JavaTwitterBot;

public class ChessLocation {

    private int row;
    private int column;
    private static final int A_CHAR_CODE = (int) 'a';

    public ChessLocation(int row, int column) {
        this.row = row;
        this.column = column;
    }


    public ChessLocation(int row, char column) {
        this.row = row;
        this.column = ((int) column - A_CHAR_CODE) + 1;
    }

    public ChessLocation(String locationString) {
        this(Integer.parseInt(locationString.substring(1,2)), locationString.toLowerCase().charAt(0));
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public char getColumnAsChar() {
        return ((char) (this.column + A_CHAR_CODE - 1));
    }

    public String toString() {
        return (Character.toUpperCase(this.getColumnAsChar()) + Integer.toString(row));
    }

    public boolean equals(ChessLocation otherLocation) {
        return (otherLocation.getRow() == this.row) && (otherLocation.getColumn() == this.column);
    }
}
