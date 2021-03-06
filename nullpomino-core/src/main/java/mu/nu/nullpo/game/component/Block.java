/*
    Copyright (c) 2010, NullNoname
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * Neither the name of NullNoname nor the names of its
          contributors may be used to endorse or promote products derived from
          this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
    LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
    SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
    CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
    ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
    POSSIBILITY OF SUCH DAMAGE.
*/
package mu.nu.nullpo.game.component;

import mu.nu.nullpo.game.play.GameEngine;

import java.io.Serializable;

/**
 * Block
 */
public class Block implements Serializable {
    /**
     * Serial version ID
     */
    private static final long serialVersionUID = -7126899262733374545L;

    /**
     * Block colorConstantcount
     */
    public static final int BLOCK_COLOR_INVALID = -1;
    public static final int BLOCK_COLOR_NONE = 0;
    public static final int BLOCK_COLOR_GRAY = 1;
    public static final int BLOCK_COLOR_RED = 2;
    public static final int BLOCK_COLOR_ORANGE = 3;
    public static final int BLOCK_COLOR_YELLOW = 4;
    public static final int BLOCK_COLOR_GREEN = 5;
    public static final int BLOCK_COLOR_CYAN = 6;
    public static final int BLOCK_COLOR_BLUE = 7;
    public static final int BLOCK_COLOR_PURPLE = 8;
    public static final int BLOCK_COLOR_GEM_RED = 9;
    public static final int BLOCK_COLOR_GEM_ORANGE = 10;
    public static final int BLOCK_COLOR_GEM_YELLOW = 11;
    public static final int BLOCK_COLOR_GEM_GREEN = 12;
    public static final int BLOCK_COLOR_GEM_CYAN = 13;
    public static final int BLOCK_COLOR_GEM_BLUE = 14;
    public static final int BLOCK_COLOR_GEM_PURPLE = 15;
    public static final int BLOCK_COLOR_SQUARE_GOLD_1 = 16;
    public static final int BLOCK_COLOR_SQUARE_GOLD_2 = 17;
    public static final int BLOCK_COLOR_SQUARE_GOLD_3 = 18;
    public static final int BLOCK_COLOR_SQUARE_GOLD_4 = 19;
    public static final int BLOCK_COLOR_SQUARE_GOLD_5 = 20;
    public static final int BLOCK_COLOR_SQUARE_GOLD_6 = 21;
    public static final int BLOCK_COLOR_SQUARE_GOLD_7 = 22;
    public static final int BLOCK_COLOR_SQUARE_GOLD_8 = 23;
    private static final int BLOCK_COLOR_SQUARE_GOLD_9 = 24;
    public static final int BLOCK_COLOR_SQUARE_SILVER_1 = 25;
    public static final int BLOCK_COLOR_SQUARE_SILVER_2 = 26;
    public static final int BLOCK_COLOR_SQUARE_SILVER_3 = 27;
    public static final int BLOCK_COLOR_SQUARE_SILVER_4 = 28;
    public static final int BLOCK_COLOR_SQUARE_SILVER_5 = 29;
    public static final int BLOCK_COLOR_SQUARE_SILVER_6 = 30;
    public static final int BLOCK_COLOR_SQUARE_SILVER_7 = 31;
    public static final int BLOCK_COLOR_SQUARE_SILVER_8 = 32;
    private static final int BLOCK_COLOR_SQUARE_SILVER_9 = 33;
    private static final int BLOCK_COLOR_RAINBOW = 34;
    public static final int BLOCK_COLOR_GEM_RAINBOW = 35;

    /**
     * Constant-itemcount
     */
    public static final int BLOCK_ITEM_NONE = 0,
            BLOCK_ITEM_RANDOM = 1;

    public static final int MAX_ITEM = 1;

    /**
     * NormalBlock colorOfMaximumcount
     */
    public static final int BLOCK_COLOR_COUNT = 9;

    /**
     * + Normal GemBlock colorOfMaximumcount
     */
    public static final int BLOCK_COLOR_EXT_COUNT = 16;

    /**
     * BlockIndicator
     */
    public static final int BLOCK_ATTRIBUTE_VISIBLE = 1;

    /**
     * Display some border
     */
    public static final int BLOCK_ATTRIBUTE_OUTLINE = 2;

    /**
     * BoneBlock
     */
    public static final int BLOCK_ATTRIBUTE_BONE = 4;

    /**
     * On theBlockAre connected with
     */
    public static final int BLOCK_ATTRIBUTE_CONNECT_UP = 8;

    /**
     * UnderBlockAre connected with
     */
    public static final int BLOCK_ATTRIBUTE_CONNECT_DOWN = 16;

    /**
     * LeftBlockAre connected with
     */
    public static final int BLOCK_ATTRIBUTE_CONNECT_LEFT = 32;

    /**
     * RightBlockAre connected with
     */
    public static final int BLOCK_ATTRIBUTE_CONNECT_RIGHT = 64;

    /**
     * I put my ownBlock
     */
    public static final int BLOCK_ATTRIBUTE_SELFPLACED = 128;

    /**
     * Part of the piece that was broken
     */
    public static final int BLOCK_ATTRIBUTE_BROKEN = 256;

    /**
     * ojama block
     */
    public static final int BLOCK_ATTRIBUTE_GARBAGE = 512;

    /**
     * Wall
     */
    public static final int BLOCK_ATTRIBUTE_WALL = 1024;

    /**
     * Plan to disappearBlock
     */
    public static final int BLOCK_ATTRIBUTE_ERASE = 2048;

    /**
     * Temporary mark for block linking check algorithm
     */
    public static final int BLOCK_ATTRIBUTE_TEMP_MARK = 4096;

    /**
     * "Block has fallen" flag for cascade gravity
     */
    public static final int BLOCK_ATTRIBUTE_CASCADE_FALL = 8192;

    /**
     * Anti-gravity flag (The block will not fall by gravity)
     */
    public static final int BLOCK_ATTRIBUTE_ANTIGRAVITY = 16384;

    /**
     * Last commit flag -- block was part of last placement or cascade
     **/
    public static final int BLOCK_ATTRIBUTE_LAST_COMMIT = 32768;

    /**
     * Ignore block connections (for Avalanche modes)
     */
    public static final int BLOCK_ATTRIBUTE_IGNORE_BLOCKLINK = 65536;

    /**
     * Block color
     */
    public int color;

    /**
     * BlockPicture of
     */
    public int skin;

    /**
     * BlockAttributes
     */
    public int attribute;

    /**
     * I have elapsed since a fixed frame count
     */
    public int elapsedFrames;

    /**
     * BlockThe darkness of the, It or brightness (0.03If it&#39;s the case3%Darkly, -0.05If it&#39;s the case5%Bright)
     */
    public float darkness;

    /**
     * Transparency (1.0fOpacity in, 0.0fCompletely transparent in)
     */
    public float alpha;

    /**
     * What number I put in the game since the start ofBlockOr (NegativecountIf it was I or initial placementgarbage block)
     */
    private int pieceNum;

    /**
     * Item number
     */
    public int item;

    /**
     * Number of extra clears required before block is erased
     */
    public int hard;

    /**
     * Counter for blocks that count down before some effect occurs
     */
    public int countdown;

    /**
     * Color-shift phase for rainbow blocks
     */
    private static int rainbowPhase = 0;

    /**
     * Color to turn into when garbage block turns into a regular block
     */
    public int secondaryColor;

    /**
     * Bonus value awarded when cleared
     */
    public int bonusValue;

    /**
     * Constructor
     */
    public Block() {
        reset();
    }

    /**
     * The color can be specifiedConstructor
     *
     * @param color Block color
     */
    public Block(int color) {
        reset();
        this.color = color;
    }

    /**
     * Specify color and pattern can beConstructor
     *
     * @param color Block color
     * @param skin  BlockPicture of
     */
    public Block(int color, int skin) {
        reset();
        this.color = color;
        this.skin = skin;
    }

    /**
     * Specify color and pattern and attributes that can beConstructor
     *
     * @param color     Block color
     * @param skin      BlockPicture of
     * @param attribute BlockAttributes
     */
    public Block(int color, int skin, int attribute) {
        reset();
        this.color = color;
        this.skin = skin;
        this.attribute = attribute;
    }

    /**
     * Copy constructor
     *
     * @param b Copy source
     */
    public Block(Block b) {
        copy(b);
    }

    /**
     * SettingsReset to defaults
     */
    private void reset() {
        color = BLOCK_COLOR_NONE;
        skin = 0;
        attribute = 0;
        elapsedFrames = 0;
        darkness = 0f;
        alpha = 1f;
        pieceNum = -1;
        item = 0;
        hard = 0;
        countdown = 0;
        secondaryColor = 0;
        bonusValue = 0;
    }

    /**
     * Settings to otherBlockCopied from the
     *
     * @param b Copy source
     */
    public void copy(Block b) {
        color = b.color;
        skin = b.skin;
        attribute = b.attribute;
        elapsedFrames = b.elapsedFrames;
        darkness = b.darkness;
        alpha = b.alpha;
        pieceNum = b.pieceNum;
        item = b.item;
        hard = b.hard;
        countdown = b.countdown;
        secondaryColor = b.secondaryColor;
        bonusValue = b.bonusValue;
    }

    /**
     * Specified attribute stateExamine the
     *
     * @param attr I want to examine the attributes
     * @return If the specified attribute has been set alltrue
     */
    public boolean getAttribute(int attr) {
        return ((attribute & attr) != 0);
    }

    /**
     * Change the attributes
     *
     * @param attr   I want to change the attributes
     * @param status After the change state
     */
    public void setAttribute(int attr, boolean status) {
        if (status) attribute |= attr;
        else attribute &= ~attr;
    }

    /**
     * ThisBlockDetermine whether the space is
     *
     * @return ThisBlockIf it is left blanktrue
     */
    public boolean isEmpty() {
        return (color < BLOCK_COLOR_GRAY);
    }

    /**
     * ThisBlockThe jewelBlockDetermine whether
     *
     * @return ThisBlockThe jewelBlockIf it&#39;s the casetrue
     */
    public boolean isGemBlock() {
        return ((color >= BLOCK_COLOR_GEM_RED) && (color <= BLOCK_COLOR_GEM_PURPLE)) ||
                (color == BLOCK_COLOR_GEM_RAINBOW);
    }

    /**
     * Checks to see if <code>this</code> is a gold square block
     *
     * @return <code>true</code> if the block is a gold square block
     */
    public boolean isGoldSquareBlock() {
        return (color >= BLOCK_COLOR_SQUARE_GOLD_1) && (color <= BLOCK_COLOR_SQUARE_GOLD_9);
    }

    /**
     * Checks to see if <code>this</code> is a silver square block
     *
     * @return <code>true</code> if the block is a silver square block
     */
    public boolean isSilverSquareBlock() {
        return (color >= BLOCK_COLOR_SQUARE_SILVER_1) && (color <= BLOCK_COLOR_SQUARE_SILVER_9);
    }

    /**
     * Checks to see if <code>this</code> is a normal block (gray to purple)
     *
     * @return <code>true</code> if the block is a normal block
     */
    public boolean isNormalBlock() {
        return (color >= BLOCK_COLOR_GRAY) && (color <= BLOCK_COLOR_PURPLE);
    }

    public int getDrawColor() {
        if (color == BLOCK_COLOR_GEM_RAINBOW)
            return BLOCK_COLOR_GEM_RED + (rainbowPhase / 3);
        else if (color == BLOCK_COLOR_RAINBOW)
            return BLOCK_COLOR_RED + (rainbowPhase / 3);
        else
            return color;
    }

    /**
     * @return the character representing the color of this block
     */
    private char blockToChar() {
        //'0'-'9','A'-'Z' represent colors 0-35.
        //Colors beyond that would follow the ASCII table starting at '['.
        if (color >= 10) {
            return (char) ('A' + (color - 10));
        }
        return (char) ('0' + Math.max(0, color));
    }

    @Override
    public String toString() {
        return "" + blockToChar();
    }

    /**
     * @param c A character representing a block
     * @return The int representing the block's color
     */
    public static int charToBlockColor(char c) {
        int blkColor = 0;

        //With a radix of 36, the digits encompass '0'-'9','A'-'Z'.
        //With a radix higher than 36, we can also have characters 'a'-'z' represent digits.
        blkColor = Character.digit(c, 36);

        //Given the current implementation of other functions, I assumed that
        //if we needed additional BLOCK_COLOR values, it would follow from 'Z'->'['
        //in the ASCII chart.
        if (blkColor == -1) {
            blkColor = (c - '[') + 36;
        }
        return blkColor;
    }

    private static void updateRainbowPhase(int time) {
        rainbowPhase = time % 21;
    }

    public static void updateRainbowPhase(GameEngine engine) {
        if (engine != null && engine.timerActive)
            updateRainbowPhase(engine.statistics.time);
        else {
            rainbowPhase++;
            if (rainbowPhase >= 21)
                rainbowPhase = 0;
        }
    }

    public static int gemToNormalColor(int color) {
        if ((color >= BLOCK_COLOR_GEM_RED) && (color <= BLOCK_COLOR_GEM_PURPLE))
            return color - 7;
        else if (color == BLOCK_COLOR_GEM_RAINBOW)
            return BLOCK_COLOR_RAINBOW;
        else
            return color;
    }
}
