package net.omegaboshi.nullpomino.game.subsystem.randomizer;

import java.util.Random;

import mu.nu.nullpo.game.component.Piece;

public abstract class Randomizer {

    Random r;
    int[] pieces;

    Randomizer() {
    }

    Randomizer(boolean[] pieceEnable, long seed) {
        setState(pieceEnable, seed);
    }

    public void init() {
    }

    public abstract int next();

    public void setState(boolean[] pieceEnable, long seed) {
        setPieceEnable(pieceEnable);
        reseed(seed);
        init();
    }

    public void setPieceEnable(boolean[] pieceEnable) {
        int piece = 0;
        for (int i = 0; i < Piece.PIECE_COUNT; i++) {
            if (pieceEnable[i]) piece++;
        }
        pieces = new int[piece];
        piece = 0;
        for (int i = 0; i < Piece.PIECE_COUNT; i++) {
            if (pieceEnable[i]) {
                pieces[piece] = i;
                piece++;
            }
        }
    }

    private void reseed(long seed) {
        r = new Random(seed);
    }

    boolean isPieceSZOOnly() {
        for (int piece : pieces) {
            if (piece != Piece.PIECE_O && piece != Piece.PIECE_Z && piece != Piece.PIECE_S)
                return false;
        }

        return true;
    }
}
