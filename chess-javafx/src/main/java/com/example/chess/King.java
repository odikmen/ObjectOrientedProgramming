
package com.example.chess;

import java.util.ArrayList;
import java.util.List;

import static com.example.chess.Piece.Color;

public class King extends Piece {
    public King(Color color) { super(color); }

    @Override
    public List<Move> pseudoLegalMoves(Position from, Board board) {
        List<Move> moves = new ArrayList<>();
        int[] dr = {1,1,1,0,0,-1,-1,-1};
        int[] dc = {1,0,-1,1,-1,1,0,-1};
        for (int k=0;k<8;k++) {
            Position to = new Position(from.row+dr[k], from.col+dc[k]);
            if (!to.inBounds()) continue;
            Piece p = board.getPiece(to);
            if (p==null || p.color!=this.color) moves.add(new Move(from,to));
        }

        if (!this.hasMoved) {

            if (this.color==Color.WHITE) {
                if (!board.whiteKingMoved && !board.whiteKingsideRookMoved) {
                    if (board.getPiece(new Position(0,5))==null && board.getPiece(new Position(0,6))==null) {
                        Move m = new Move(from, new Position(0,6));
                        moves.add(m);
                    }
                }
                if (!board.whiteKingMoved && !board.whiteQueensideRookMoved) {
                    if (board.getPiece(new Position(0,1))==null && board.getPiece(new Position(0,2))==null && board.getPiece(new Position(0,3))==null) {
                        Move m = new Move(from, new Position(0,2));
                        moves.add(m);
                    }
                }
            } else {
                if (!board.blackKingMoved && !board.blackKingsideRookMoved) {
                    if (board.getPiece(new Position(7,5))==null && board.getPiece(new Position(7,6))==null) {
                        Move m = new Move(from, new Position(7,6));
                        moves.add(m);
                    }
                }
                if (!board.blackKingMoved && !board.blackQueensideRookMoved) {
                    if (board.getPiece(new Position(7,1))==null && board.getPiece(new Position(7,2))==null && board.getPiece(new Position(7,3))==null) {
                        Move m = new Move(from, new Position(7,2));
                        moves.add(m);
                    }
                }
            }
        }
        return moves;
    }

    @Override
    public String getSymbol() {
        return color==Color.WHITE ? "♔" : "♚";
    }
}
