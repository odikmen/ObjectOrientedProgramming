
package com.example.chess;

public class Move {
    public final Position from;
    public final Position to;
    public String promotion = null;
    public boolean isEnPassant = false;
    public boolean isCastling = false;

    public Move(Position from, Position to) {
        this.from = from;
        this.to = to;
    }

    public Move(Position from, Position to, String promotion) {
        this(from, to);
        this.promotion = promotion;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Move)) return false;
        Move m = (Move)o;
        return from.equals(m.from) && to.equals(m.to) && ((promotion==null && m.promotion==null) || (promotion!=null && promotion.equals(m.promotion)));
    }


    public boolean equalsIgnorePromotion(Move other) {
        return this.from.equals(other.from) && this.to.equals(other.to);
    }

    @Override
    public String toString() {
        return from + "->" + to + (promotion!=null?"="+promotion:"") + (isEnPassant?" e.p.":"") + (isCastling?" castle":"");
    }
}
