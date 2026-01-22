
package com.example.chess;

import java.util.ArrayList;
import java.util.List;

import static com.example.chess.Piece.Color;

public class Rook extends Piece {
    public Rook(Color color) { super(color); }

    @Override
    public List<Move> pseudoLegalMoves(Position from, Board board) {
        List<Move> moves = new ArrayList<>();
        int[] dr = {1,-1,0,0};
        int[] dc = {0,0,1,-1};
        for (int k=0;k<4;k++) {
            int r = from.row + dr[k], c = from.col + dc[k];
            while (r>=0 && r<8 && c>=0 && c<8) {
                Position to = new Position(r,c);
                Piece p = board.getPiece(to);
                if (p==null) moves.add(new Move(from,to));
                else {
                    if (p.color!=this.color) moves.add(new Move(from,to));
                    break;
                }
                r+=dr[k]; c+=dc[k];
            }
        }
        return moves;
    }

    @Override
    public String getSymbol() {
        return color==Color.WHITE ? "♖" : "♜";
    }
}
