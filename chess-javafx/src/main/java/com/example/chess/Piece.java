
package com.example.chess;

import java.util.List;

public abstract class Piece {
    public enum Color { WHITE, BLACK }
    public final Color color;
    public boolean hasMoved = false;

    public Piece(Color color) {
        this.color = color;
    }

    public abstract List<Move> pseudoLegalMoves(Position from, Board board);

    public abstract String getSymbol();
}
