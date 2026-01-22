
package com.example.chess;

import java.util.ArrayList;
import java.util.List;

import static com.example.chess.Piece.Color;

public class Pawn extends Piece {
    public Pawn(Color color) { super(color); }

    @Override
    public List<Move> pseudoLegalMoves(Position from, Board board) {
        List<Move> moves = new ArrayList<>();
        int dir = (color==Color.WHITE) ? 1 : -1;
        Position one = new Position(from.row + dir, from.col);
        if (one.inBounds() && board.getPiece(one)==null) {

            if ((color==Color.WHITE && one.row==7) || (color==Color.BLACK && one.row==0)) {
                moves.add(new Move(from, one, "Queen"));
            } else moves.add(new Move(from, one));

            Position two = new Position(from.row + 2*dir, from.col);
            if (!hasMoved && two.inBounds() && board.getPiece(two)==null) {
                moves.add(new Move(from, two));
            }
        }

        Position c1 = new Position(from.row + dir, from.col-1);
        Position c2 = new Position(from.row + dir, from.col+1);
        if (c1.inBounds()) {
            Piece p = board.getPiece(c1);
            if (p!=null && p.color!=this.color) {
                if ((color==Color.WHITE && c1.row==7) || (color==Color.BLACK && c1.row==0)) {
                    moves.add(new Move(from, c1, "Queen"));
                } else moves.add(new Move(from, c1));
            } else if (board.enPassantTarget!=null && c1.equals(board.enPassantTarget)) {
                Move m = new Move(from, c1);
                m.isEnPassant = true;
                moves.add(m);
            }
        }
        if (c2.inBounds()) {
            Piece p = board.getPiece(c2);
            if (p!=null && p.color!=this.color) {
                if ((color==Color.WHITE && c2.row==7) || (color==Color.BLACK && c2.row==0)) {
                    moves.add(new Move(from, c2, "Queen"));
                } else moves.add(new Move(from, c2));
            } else if (board.enPassantTarget!=null && c2.equals(board.enPassantTarget)) {
                Move m = new Move(from, c2);
                m.isEnPassant = true;
                moves.add(m);
            }
        }
        return moves;
    }

    @Override
    public String getSymbol() {
        return color==Color.WHITE ? "♙" : "♟";
    }
}
