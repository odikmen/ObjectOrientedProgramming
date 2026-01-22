
package com.example.chess;

import java.util.ArrayList;
import java.util.List;

import static com.example.chess.Piece.Color;

public class Knight extends Piece {
    public Knight(Color color) { super(color); }

    @Override
    public List<Move> pseudoLegalMoves(Position from, Board board) {
        List<Move> moves = new ArrayList<>();
        int[] dr = {2,2,-2,-2,1,1,-1,-1};
        int[] dc = {1,-1,1,-1,2,-2,2,-2};
        for (int k=0;k<8;k++) {
            Position to = new Position(from.row+dr[k], from.col+dc[k]);
            if (!to.inBounds()) continue;
            Piece p = board.getPiece(to);
            if (p==null || p.color!=this.color) moves.add(new Move(from,to));
        }
        return moves;
    }

    @Override
    public String getSymbol() {
        return color==Color.WHITE ? "♘" : "♞";
    }
}
