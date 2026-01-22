
package com.example.chess;

import java.util.*;
import static com.example.chess.Piece.Color;

public class Board {
    private Piece[][] b = new Piece[8][8];
    public Color currentTurn = Color.WHITE;

    public boolean whiteKingMoved = false, whiteKingsideRookMoved = false, whiteQueensideRookMoved = false;
    public boolean blackKingMoved = false, blackKingsideRookMoved = false, blackQueensideRookMoved = false;

    public Position enPassantTarget = null;

    public Board() {
        setupStart();
    }

    public void setupStart() {

        for (int r=0;r<8;r++) for (int c=0;c<8;c++) b[r][c]=null;

        for (int c=0;c<8;c++) b[1][c]=new Pawn(Color.WHITE);
        for (int c=0;c<8;c++) b[6][c]=new Pawn(Color.BLACK);

        b[0][0]=new Rook(Color.WHITE); b[0][7]=new Rook(Color.WHITE);
        b[7][0]=new Rook(Color.BLACK); b[7][7]=new Rook(Color.BLACK);

        b[0][1]=new Knight(Color.WHITE); b[0][6]=new Knight(Color.WHITE);
        b[7][1]=new Knight(Color.BLACK); b[7][6]=new Knight(Color.BLACK);

        b[0][2]=new Bishop(Color.WHITE); b[0][5]=new Bishop(Color.WHITE);
        b[7][2]=new Bishop(Color.BLACK); b[7][5]=new Bishop(Color.BLACK);

        b[0][3]=new Queen(Color.WHITE); b[7][3]=new Queen(Color.BLACK);

        b[0][4]=new King(Color.WHITE); b[7][4]=new King(Color.BLACK);

        currentTurn = Color.WHITE;
        whiteKingMoved = whiteKingsideRookMoved = whiteQueensideRookMoved = false;
        blackKingMoved = blackKingsideRookMoved = blackQueensideRookMoved = false;
        enPassantTarget = null;
    }

    public Piece getPiece(Position p) {
        if (!p.inBounds()) return null;
        return b[p.row][p.col];
    }

    public void setPiece(Position p, Piece piece) {
        if (p==null) return;
        b[p.row][p.col]=piece;
    }

    public void makeMove(Move m) {
        Piece p = getPiece(m.from);
        if (p == null) return;

        if (m.isEnPassant) {

            int dir = p.color==Color.WHITE ? 1 : -1;
            Position cap = new Position(m.to.row - dir, m.to.col);
            setPiece(cap, null);
        }

        if (m.isCastling) {
            if (m.to.col == 6) {
                Position rookFrom = new Position(m.from.row,7);
                Position rookTo = new Position(m.from.row,5);
                Piece rook = getPiece(rookFrom);
                setPiece(rookFrom,null);
                setPiece(rookTo,rook);
                if (rook!=null) rook.hasMoved = true;
            } else if (m.to.col == 2) {
                Position rookFrom = new Position(m.from.row,0);
                Position rookTo = new Position(m.from.row,3);
                Piece rook = getPiece(rookFrom);
                setPiece(rookFrom,null);
                setPiece(rookTo,rook);
                if (rook!=null) rook.hasMoved = true;
            }
        }


        setPiece(m.to, p);
        setPiece(m.from, null);


        if (m.promotion != null) {
            Piece promoted = switch (m.promotion) {
                case "Queen" -> new Queen(p.color);
                case "Rook" -> new Rook(p.color);
                case "Bishop" -> new Bishop(p.color);
                case "Knight" -> new Knight(p.color);
                default -> new Queen(p.color);
            };
            setPiece(m.to, promoted);
        }


        enPassantTarget = null;
        if (p instanceof Pawn) {
            if (Math.abs(m.to.row - m.from.row) == 2) {
                int dir = p.color==Color.WHITE ? 1 : -1;
                enPassantTarget = new Position(m.from.row + dir, m.from.col);
            }
        }


        if (p instanceof King) {
            if (p.color==Color.WHITE) whiteKingMoved = true; else blackKingMoved = true;
        }
        if (p instanceof Rook) {
            if (p.color==Color.WHITE) {
                if (m.from.row==0 && m.from.col==0) whiteQueensideRookMoved = true;
                if (m.from.row==0 && m.from.col==7) whiteKingsideRookMoved = true;
            } else {
                if (m.from.row==7 && m.from.col==0) blackQueensideRookMoved = true;
                if (m.from.row==7 && m.from.col==7) blackKingsideRookMoved = true;
            }
        }
        p.hasMoved = true;


        currentTurn = (currentTurn==Color.WHITE)?Color.BLACK:Color.WHITE;
    }


    public List<Move> generateLegalMovesForPiece(Position from) {
        Piece p = getPiece(from);
        if (p==null) return Collections.emptyList();
        List<Move> pseudo = p.pseudoLegalMoves(from, this);
        List<Move> legal = new ArrayList<>();
        for (Move m: pseudo) {

            if (p instanceof Pawn && enPassantTarget!=null && m.to.equals(enPassantTarget)) {
                m.isEnPassant = true;
            }
            if (p instanceof King && Math.abs(m.to.col - from.col)==2) {
                m.isCastling = true;
            }
            Board copy = this.copy();
            copy.makeMove(m);

            if (!copy.isKingInCheck(p.color)) {
                legal.add(m);
            }
        }
        return legal;
    }

    public boolean isKingInCheck(Piece.Color color) {
        Position kingPos = findKing(color);
        if (kingPos==null) return false;
        return isSquareAttacked(kingPos, opposite(color));
    }

    public boolean isInCheckmate(Piece.Color color) {

        if (!isKingInCheck(color)) return false;
        for (int r=0;r<8;r++) for (int c=0;c<8;c++) {
            Position p = new Position(r,c);
            Piece pc = getPiece(p);
            if (pc!=null && pc.color==color) {
                if (!generateLegalMovesForPiece(p).isEmpty()) return false;
            }
        }
        return true;
    }

    public boolean isInStalemate(Piece.Color color) {
        if (isKingInCheck(color)) return false;
        for (int r=0;r<8;r++) for (int c=0;c<8;c++) {
            Position p = new Position(r,c);
            Piece pc = getPiece(p);
            if (pc!=null && pc.color==color) {
                if (!generateLegalMovesForPiece(p).isEmpty()) return false;
            }
        }
        return true;
    }

    private Position findKing(Piece.Color color) {
        for (int r=0;r<8;r++) for (int c=0;c<8;c++) {
            Piece p = b[r][c];
            if (p instanceof King && p.color==color) return new Position(r,c);
        }
        return null;
    }

    private Piece.Color opposite(Piece.Color c) {
        return (c==Piece.Color.WHITE)?Piece.Color.BLACK:Piece.Color.WHITE;
    }


    public boolean isSquareAttacked(Position sq, Piece.Color byColor) {
        for (int r=0;r<8;r++) for (int c=0;c<8;c++) {
            Position p = new Position(r,c);
            Piece pc = getPiece(p);
            if (pc==null || pc.color!=byColor) continue;
            List<Move> moves = pc.pseudoLegalMoves(p, this);
            for (Move m: moves) {

                if (pc instanceof Pawn) {
                    int dir = (pc.color==Piece.Color.WHITE)?1:-1;
                    Position attack1 = new Position(r+dir, c-1);
                    Position attack2 = new Position(r+dir, c+1);
                    if (attack1.equals(sq) || attack2.equals(sq)) return true;
                } else {
                    if (m.to.equals(sq)) return true;
                }
            }
        }
        return false;
    }


    public Board copy() {
        Board nb = new Board();
        nb.b = new Piece[8][8];
        for (int r=0;r<8;r++) for (int c=0;c<8;c++) {
            Piece p = this.b[r][c];
            if (p==null) nb.b[r][c]=null;
            else {

                if (p instanceof Pawn) nb.b[r][c] = new Pawn(p.color);
                else if (p instanceof Rook) nb.b[r][c] = new Rook(p.color);
                else if (p instanceof Knight) nb.b[r][c] = new Knight(p.color);
                else if (p instanceof Bishop) nb.b[r][c] = new Bishop(p.color);
                else if (p instanceof Queen) nb.b[r][c] = new Queen(p.color);
                else if (p instanceof King) nb.b[r][c] = new King(p.color);
                nb.b[r][c].hasMoved = p.hasMoved;
            }
        }
        nb.currentTurn = this.currentTurn;
        nb.whiteKingMoved = this.whiteKingMoved;
        nb.whiteKingsideRookMoved = this.whiteKingsideRookMoved;
        nb.whiteQueensideRookMoved = this.whiteQueensideRookMoved;
        nb.blackKingMoved = this.blackKingMoved;
        nb.blackKingsideRookMoved = this.blackKingsideRookMoved;
        nb.blackQueensideRookMoved = this.blackQueensideRookMoved;
        nb.enPassantTarget = (this.enPassantTarget==null)?null:new Position(this.enPassantTarget.row,this.enPassantTarget.col);
        return nb;
    }
}
