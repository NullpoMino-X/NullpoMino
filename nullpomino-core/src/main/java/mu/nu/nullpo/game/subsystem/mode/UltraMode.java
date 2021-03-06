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
package mu.nu.nullpo.game.subsystem.mode;

import mu.nu.nullpo.game.component.BGMStatus;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.net.NetUtil;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * ULTRA Mode
 */
public class UltraMode extends NetDummyMode {
    /**
     * Current version
     */
    private static final int CURRENT_VERSION = 1;

    /**
     * Number of entries in rankings
     */
    private static final int RANKING_MAX = 5;

    /**
     * Number of ranking types
     */
    private static final int RANKING_TYPE = 2;

    /**
     * Time limit type
     */
    private static final int GOALTYPE_MAX = 5;

    /**
     * Most recent scoring event type constants
     */
    private static final int EVENT_NONE = 0,
            EVENT_SINGLE = 1,
            EVENT_DOUBLE = 2,
            EVENT_TRIPLE = 3,
            EVENT_FOUR = 4,
            EVENT_TSPIN_ZERO_MINI = 5,
            EVENT_TSPIN_ZERO = 6,
            EVENT_TSPIN_SINGLE_MINI = 7,
            EVENT_TSPIN_SINGLE = 8,
            EVENT_TSPIN_DOUBLE_MINI = 9,
            EVENT_TSPIN_DOUBLE = 10,
            EVENT_TSPIN_TRIPLE = 11,
            EVENT_TSPIN_EZ = 12;

    /**
     * Most recent increase in score
     */
    private int lastscore;

    /**
     * Time to display the most recent increase in score
     */
    private int scgettime;

    /**
     * Most recent scoring event type
     */
    private int lastevent;

    /**
     * Most recent scoring event b2b
     */
    private boolean lastb2b;

    /**
     * Most recent scoring event combo count
     */
    private int lastcombo;

    /**
     * Most recent scoring event piece ID
     */
    private int lastpiece;

    /**
     * BGM number
     */
    private int bgmno;

    /**
     * Flag for types of T-Spins allowed (0=none, 1=normal, 2=all spin)
     */
    private int tspinEnableType;

    /**
     * Old flag for allowing T-Spins
     */
    private boolean enableTSpin;

    /**
     * Flag for enabling wallkick T-Spins
     */
    private boolean enableTSpinKick;

    /**
     * Spin check type (4Point or Immobile)
     */
    private int spinCheckType;

    /**
     * Immobile EZ spin
     */
    private boolean tspinEnableEZ;

    /**
     * Flag for enabling B2B
     */
    private boolean enableB2B;

    /**
     * Flag for enabling combos
     */
    private boolean enableCombo;

    /**
     * Big
     */
    private boolean big;

    /**
     * Time limit type
     */
    private int goaltype;

    /**
     * Last preset number used
     */
    private int presetNumber;

    /**
     * Version
     */
    private int version;

    /**
     * Current round's ranking rank
     */
    private int[] rankingRank;

    /**
     * Rankings' scores
     */
    private int[][][] rankingScore;

    /**
     * Rankings' line counts
     */
    private int[][][] rankingLines;

    /*
     * Mode name
     */
    @Override
    public String getName() {
        return "ULTRA";
    }

    /*
     * Initialization
     */
    @Override
    public void playerInit(GameEngine engine, int playerID) {
        owner = engine.owner;
        receiver = engine.owner.receiver;
        lastscore = 0;
        scgettime = 0;
        lastevent = EVENT_NONE;
        lastb2b = false;
        lastcombo = 0;
        lastpiece = 0;
        bgmno = 0;

        rankingRank = new int[RANKING_TYPE];
        rankingRank[0] = -1;
        rankingRank[1] = -1;

        rankingScore = new int[GOALTYPE_MAX][RANKING_TYPE][RANKING_MAX];
        rankingLines = new int[GOALTYPE_MAX][RANKING_TYPE][RANKING_MAX];

        engine.framecolor = GameEngine.FRAME_COLOR_BLUE;

        netPlayerInit(engine, playerID);

        if (engine.owner.replayMode == false) {
            presetNumber = engine.owner.modeConfig.getProperty("ultra.presetNumber", 0);
            loadPreset(engine, engine.owner.modeConfig, -1);
            loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
            version = CURRENT_VERSION;
        } else {
            presetNumber = 0;
            loadPreset(engine, engine.owner.replayProp, -1);
            version = engine.owner.replayProp.getProperty("ultra.version", 0);
            // NET: Load name
            netPlayerName = engine.owner.replayProp.getProperty(playerID + ".net.netPlayerName", "");
        }
    }

    /**
     * Load options from a preset
     *
     * @param engine GameEngine
     * @param prop   Property file to read from
     * @param preset Preset number
     */
    private void loadPreset(GameEngine engine, CustomProperties prop, int preset) {
        engine.speed.gravity = prop.getProperty("ultra.gravity." + preset, 4);
        engine.speed.denominator = prop.getProperty("ultra.denominator." + preset, 256);
        engine.speed.are = prop.getProperty("ultra.are." + preset, 0);
        engine.speed.areLine = prop.getProperty("ultra.areLine." + preset, 0);
        engine.speed.lineDelay = prop.getProperty("ultra.lineDelay." + preset, 0);
        engine.speed.lockDelay = prop.getProperty("ultra.lockDelay." + preset, 30);
        engine.speed.das = prop.getProperty("ultra.das." + preset, 14);
        bgmno = prop.getProperty("ultra.bgmno." + preset, 0);
        tspinEnableType = prop.getProperty("ultra.tspinEnableType." + preset, 1);
        enableTSpin = prop.getProperty("ultra.enableTSpin." + preset, true);
        enableTSpinKick = prop.getProperty("ultra.enableTSpinKick." + preset, true);
        spinCheckType = prop.getProperty("ultra.spinCheckType." + preset, 0);
        tspinEnableEZ = prop.getProperty("ultra.tspinEnableEZ." + preset, false);
        enableB2B = prop.getProperty("ultra.enableB2B." + preset, true);
        enableCombo = prop.getProperty("ultra.enableCombo." + preset, true);
        big = prop.getProperty("ultra.big." + preset, false);
        goaltype = prop.getProperty("ultra.goaltype." + preset, 2);
    }

    /**
     * Save options to a preset
     *
     * @param engine GameEngine
     * @param prop   Property file to save to
     * @param preset Preset number
     */
    private void savePreset(GameEngine engine, CustomProperties prop, int preset) {
        prop.setProperty("ultra.gravity." + preset, engine.speed.gravity);
        prop.setProperty("ultra.denominator." + preset, engine.speed.denominator);
        prop.setProperty("ultra.are." + preset, engine.speed.are);
        prop.setProperty("ultra.areLine." + preset, engine.speed.areLine);
        prop.setProperty("ultra.lineDelay." + preset, engine.speed.lineDelay);
        prop.setProperty("ultra.lockDelay." + preset, engine.speed.lockDelay);
        prop.setProperty("ultra.das." + preset, engine.speed.das);
        prop.setProperty("ultra.bgmno." + preset, bgmno);
        prop.setProperty("ultra.tspinEnableType." + preset, tspinEnableType);
        prop.setProperty("ultra.enableTSpin." + preset, enableTSpin);
        prop.setProperty("ultra.enableTSpinKick." + preset, enableTSpinKick);
        prop.setProperty("ultra.spinCheckType." + preset, spinCheckType);
        prop.setProperty("ultra.tspinEnableEZ." + preset, tspinEnableEZ);
        prop.setProperty("ultra.enableB2B." + preset, enableB2B);
        prop.setProperty("ultra.enableCombo." + preset, enableCombo);
        prop.setProperty("ultra.big." + preset, big);
        prop.setProperty("ultra.goaltype." + preset, goaltype);
    }

    /*
     * Called at settings screen
     */
    @Override
    public boolean onSetting(GameEngine engine, int playerID) {
        // NET: Net Ranking
        if (netIsNetRankingDisplayMode) {
            netOnUpdateNetPlayRanking(engine, goaltype);
        }
        // Menu
        else if (engine.owner.replayMode == false) {
            // Configuration changes
            int change = updateCursor(engine, 17);

            if (change != 0) {
                engine.playSE("change");

                int m = 1;
                if (engine.ctrl.isPress(Controller.BUTTON_E)) m = 100;
                if (engine.ctrl.isPress(Controller.BUTTON_F)) m = 1000;

                switch (menuCursor) {
                    case 0:
                        engine.speed.gravity += change * m;
                        if (engine.speed.gravity < -1) engine.speed.gravity = 99999;
                        if (engine.speed.gravity > 99999) engine.speed.gravity = -1;
                        break;
                    case 1:
                        engine.speed.denominator += change * m;
                        if (engine.speed.denominator < -1) engine.speed.denominator = 99999;
                        if (engine.speed.denominator > 99999) engine.speed.denominator = -1;
                        break;
                    case 2:
                        engine.speed.are += change;
                        if (engine.speed.are < 0) engine.speed.are = 99;
                        if (engine.speed.are > 99) engine.speed.are = 0;
                        break;
                    case 3:
                        engine.speed.areLine += change;
                        if (engine.speed.areLine < 0) engine.speed.areLine = 99;
                        if (engine.speed.areLine > 99) engine.speed.areLine = 0;
                        break;
                    case 4:
                        engine.speed.lineDelay += change;
                        if (engine.speed.lineDelay < 0) engine.speed.lineDelay = 99;
                        if (engine.speed.lineDelay > 99) engine.speed.lineDelay = 0;
                        break;
                    case 5:
                        engine.speed.lockDelay += change;
                        if (engine.speed.lockDelay < 0) engine.speed.lockDelay = 99;
                        if (engine.speed.lockDelay > 99) engine.speed.lockDelay = 0;
                        break;
                    case 6:
                        engine.speed.das += change;
                        if (engine.speed.das < 0) engine.speed.das = 99;
                        if (engine.speed.das > 99) engine.speed.das = 0;
                        break;
                    case 7:
                        bgmno += change;
                        if (bgmno < 0) bgmno = BGMStatus.BGM_COUNT - 1;
                        if (bgmno > BGMStatus.BGM_COUNT - 1) bgmno = 0;
                        break;
                    case 8:
                        big = !big;
                        break;
                    case 9:
                        goaltype += change;
                        if (goaltype < 0) goaltype = GOALTYPE_MAX - 1;
                        if (goaltype > GOALTYPE_MAX - 1) goaltype = 0;
                        break;
                    case 10:
                        //enableTSpin = !enableTSpin;
                        tspinEnableType += change;
                        if (tspinEnableType < 0) tspinEnableType = 2;
                        if (tspinEnableType > 2) tspinEnableType = 0;
                        break;
                    case 11:
                        enableTSpinKick = !enableTSpinKick;
                        break;
                    case 12:
                        spinCheckType += change;
                        if (spinCheckType < 0) spinCheckType = 1;
                        if (spinCheckType > 1) spinCheckType = 0;
                        break;
                    case 13:
                        tspinEnableEZ = !tspinEnableEZ;
                        break;
                    case 14:
                        enableB2B = !enableB2B;
                        break;
                    case 15:
                        enableCombo = !enableCombo;
                        break;
                    case 16:
                    case 17:
                        presetNumber += change;
                        if (presetNumber < 0) presetNumber = 99;
                        if (presetNumber > 99) presetNumber = 0;
                        break;
                }

                // NET: Signal options change
                if (netIsNetPlay && (netNumSpectators > 0)) netSendOptions(engine);
            }

            // Confirm
            if (engine.ctrl.isPush(Controller.BUTTON_A) && (menuTime >= 5)) {
                engine.playSE("decide");

                if (menuCursor == 16) {
                    loadPreset(engine, owner.modeConfig, presetNumber);

                    // NET: Signal options change
                    if (netIsNetPlay && (netNumSpectators > 0)) netSendOptions(engine);
                } else if (menuCursor == 17) {
                    savePreset(engine, owner.modeConfig, presetNumber);
                    receiver.saveModeConfig(owner.modeConfig);
                } else {
                    owner.modeConfig.setProperty("ultra.presetNumber", presetNumber);
                    savePreset(engine, owner.modeConfig, -1);
                    receiver.saveModeConfig(owner.modeConfig);

                    // NET: Signal start of the game
                    if (netIsNetPlay) netLobby.netPlayerClient.send("start1p\n");

                    return false;
                }
            }

            // Cancel
            if (engine.ctrl.isPush(Controller.BUTTON_B) && !netIsNetPlay) {
                engine.quitflag = true;
            }

            // NET: Netplay Ranking
            if (engine.ctrl.isPush(Controller.BUTTON_D) && netIsNetPlay && !netIsWatch && netIsNetRankingViewOK(engine)) {
                netEnterNetPlayRankingScreen(engine, playerID, goaltype);
            }

            menuTime++;
        }
        // Replay
        else {
            menuTime++;
            menuCursor = 0;

            if (menuTime >= 60) {
                menuCursor = 10;
            }
            return menuTime < 120;
        }

        return true;
    }

    /*
     * Setting screen drawing
     */
    @Override
    public void renderSetting(GameEngine engine, int playerID) {
        if (netIsNetRankingDisplayMode) {
            // NET: Netplay Ranking
            netOnRenderNetPlayRanking(engine, playerID, receiver);
        } else if (menuCursor < 10) {
            drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 0,
                    "GRAVITY", String.valueOf(engine.speed.gravity),
                    "G-MAX", String.valueOf(engine.speed.denominator),
                    "ARE", String.valueOf(engine.speed.are),
                    "ARE LINE", String.valueOf(engine.speed.areLine),
                    "LINE DELAY", String.valueOf(engine.speed.lineDelay),
                    "LOCK DELAY", String.valueOf(engine.speed.lockDelay),
                    "DAS", String.valueOf(engine.speed.das),
                    "BGM", String.valueOf(bgmno),
                    "BIG", GeneralUtil.getONorOFF(big),
                    "GOAL", (goaltype + 1) + "MIN");
        } else {
            String strTSpinEnable = "";
            if (version >= 1) {
                if (tspinEnableType == 0) strTSpinEnable = "OFF";
                if (tspinEnableType == 1) strTSpinEnable = "T-ONLY";
                if (tspinEnableType == 2) strTSpinEnable = "ALL";
            } else {
                strTSpinEnable = GeneralUtil.getONorOFF(enableTSpin);
            }
            drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 10,
                    "SPIN BONUS", strTSpinEnable,
                    "EZ SPIN", GeneralUtil.getONorOFF(enableTSpinKick),
                    "SPIN TYPE", (spinCheckType == 0) ? "4POINT" : "IMMOBILE",
                    "EZIMMOBILE", GeneralUtil.getONorOFF(tspinEnableEZ),
                    "B2B", GeneralUtil.getONorOFF(enableB2B),
                    "COMBO", GeneralUtil.getONorOFF(enableCombo));
            drawMenu(engine, playerID, receiver, 12, EventReceiver.COLOR_GREEN, 16,
                    "LOAD", String.valueOf(presetNumber),
                    "SAVE", String.valueOf(presetNumber));
        }
    }

    /*
     * This function will be called before the game actually begins (after Ready&Go screen disappears)
     */
    @Override
    public void startGame(GameEngine engine, int playerID) {
        engine.big = big;
        engine.b2bEnable = enableB2B;
        if (enableCombo) engine.comboType = GameEngine.COMBO_TYPE_NORMAL;
        else engine.comboType = GameEngine.COMBO_TYPE_DISABLE;
        engine.meterValue = 320;
        engine.meterColor = GameEngine.METER_COLOR_GREEN;

        if (netIsWatch) {
            owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
        } else {
            owner.bgmStatus.bgm = bgmno;
        }

        if (version >= 1) {
            engine.tspinAllowKick = enableTSpinKick;
            if (tspinEnableType == 0) {
                engine.tspinEnable = false;
            } else if (tspinEnableType == 1) {
                engine.tspinEnable = true;
            } else {
                engine.tspinEnable = true;
                engine.useAllSpinBonus = true;
            }
        } else {
            engine.tspinEnable = enableTSpin;
            engine.tspinAllowKick = enableTSpinKick;
        }

        engine.spinCheckType = spinCheckType;
        engine.tspinEnableEZ = tspinEnableEZ;
    }

    /*
     * Render score
     */
    @Override
    public void renderLast(GameEngine engine, int playerID) {
        if (owner.menuOnly) return;

        receiver.drawScoreFont(engine, playerID, 0, 0, "ULTRA", EventReceiver.COLOR_CYAN);
        receiver.drawScoreFont(engine, playerID, 0, 1, "(" + (goaltype + 1) + " MINUTE GAME)", EventReceiver.COLOR_CYAN);

        if ((engine.stat == GameEngine.Status.SETTING) || ((engine.stat == GameEngine.Status.RESULT) && (owner.replayMode == false))) {
            if ((owner.replayMode == false) && (big == false) && (engine.ai == null)) {
                receiver.drawScoreFont(engine, playerID, 0, 3, "SCORE RANKING", EventReceiver.COLOR_GREEN);
                receiver.drawScoreFont(engine, playerID, 3, 4, "SCORE  LINE", EventReceiver.COLOR_BLUE);

                for (int i = 0; i < RANKING_MAX; i++) {
                    receiver.drawScoreFont(engine, playerID, 0, 5 + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW);
                    receiver.drawScoreFont(engine, playerID, 3, 5 + i, String.valueOf(rankingScore[goaltype][0][i]), (i == rankingRank[0]));
                    receiver.drawScoreFont(engine, playerID, 10, 5 + i, String.valueOf(rankingLines[goaltype][0][i]), (i == rankingRank[0]));
                }

                receiver.drawScoreFont(engine, playerID, 0, 11, "LINE RANKING", EventReceiver.COLOR_GREEN);
                receiver.drawScoreFont(engine, playerID, 3, 12, "LINE SCORE", EventReceiver.COLOR_BLUE);

                for (int i = 0; i < RANKING_MAX; i++) {
                    receiver.drawScoreFont(engine, playerID, 0, 13 + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW);
                    receiver.drawScoreFont(engine, playerID, 3, 13 + i, String.valueOf(rankingLines[goaltype][1][i]), (i == rankingRank[1]));
                    receiver.drawScoreFont(engine, playerID, 8, 13 + i, String.valueOf(rankingScore[goaltype][1][i]), (i == rankingRank[1]));
                }
            }
        } else {
            receiver.drawScoreFont(engine, playerID, 0, 3, "SCORE", EventReceiver.COLOR_BLUE);
            String strScore;
            if ((lastscore == 0) || (scgettime >= 120)) {
                strScore = String.valueOf(engine.statistics.score);
            } else {
                strScore = String.valueOf(engine.statistics.score) + "(+" + String.valueOf(lastscore) + ")";
            }
            receiver.drawScoreFont(engine, playerID, 0, 4, strScore);

            receiver.drawScoreFont(engine, playerID, 0, 6, "LINE", EventReceiver.COLOR_BLUE);
            receiver.drawScoreFont(engine, playerID, 0, 7, String.valueOf(engine.statistics.lines));

            receiver.drawScoreFont(engine, playerID, 0, 9, "SCORE/MIN", EventReceiver.COLOR_BLUE);
            receiver.drawScoreFont(engine, playerID, 0, 10, String.format("%-10g", engine.statistics.spm));

            receiver.drawScoreFont(engine, playerID, 0, 12, "LINE/MIN", EventReceiver.COLOR_BLUE);
            receiver.drawScoreFont(engine, playerID, 0, 13, String.valueOf(engine.statistics.lpm));

            receiver.drawScoreFont(engine, playerID, 0, 15, "TIME", EventReceiver.COLOR_BLUE);
            int time = ((goaltype + 1) * 3600) - engine.statistics.time;
            if (time < 0) time = 0;
            int fontcolor = EventReceiver.COLOR_WHITE;
            if ((time < 30 * 60) && (time > 0)) fontcolor = EventReceiver.COLOR_YELLOW;
            if ((time < 20 * 60) && (time > 0)) fontcolor = EventReceiver.COLOR_ORANGE;
            if ((time < 10 * 60) && (time > 0)) fontcolor = EventReceiver.COLOR_RED;
            receiver.drawScoreFont(engine, playerID, 0, 16, GeneralUtil.getTime(time), fontcolor);

            if ((lastevent != EVENT_NONE) && (scgettime < 120)) {
                String strPieceName = Piece.getPieceName(lastpiece);

                switch (lastevent) {
                    case EVENT_SINGLE:
                        receiver.drawMenuFont(engine, playerID, 2, 21, "SINGLE", EventReceiver.COLOR_DARKBLUE);
                        break;
                    case EVENT_DOUBLE:
                        receiver.drawMenuFont(engine, playerID, 2, 21, "DOUBLE", EventReceiver.COLOR_BLUE);
                        break;
                    case EVENT_TRIPLE:
                        receiver.drawMenuFont(engine, playerID, 2, 21, "TRIPLE", EventReceiver.COLOR_GREEN);
                        break;
                    case EVENT_FOUR:
                        if (lastb2b) receiver.drawMenuFont(engine, playerID, 3, 21, "FOUR", EventReceiver.COLOR_RED);
                        else receiver.drawMenuFont(engine, playerID, 3, 21, "FOUR", EventReceiver.COLOR_ORANGE);
                        break;
                    case EVENT_TSPIN_ZERO_MINI:
                        receiver.drawMenuFont(engine, playerID, 2, 21, strPieceName + "-SPIN", EventReceiver.COLOR_PURPLE);
                        break;
                    case EVENT_TSPIN_ZERO:
                        receiver.drawMenuFont(engine, playerID, 2, 21, strPieceName + "-SPIN", EventReceiver.COLOR_PINK);
                        break;
                    case EVENT_TSPIN_SINGLE_MINI:
                        if (lastb2b)
                            receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-S", EventReceiver.COLOR_RED);
                        else
                            receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-S", EventReceiver.COLOR_ORANGE);
                        break;
                    case EVENT_TSPIN_SINGLE:
                        if (lastb2b)
                            receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-SINGLE", EventReceiver.COLOR_RED);
                        else
                            receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-SINGLE", EventReceiver.COLOR_ORANGE);
                        break;
                    case EVENT_TSPIN_DOUBLE_MINI:
                        if (lastb2b)
                            receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-D", EventReceiver.COLOR_RED);
                        else
                            receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-D", EventReceiver.COLOR_ORANGE);
                        break;
                    case EVENT_TSPIN_DOUBLE:
                        if (lastb2b)
                            receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-DOUBLE", EventReceiver.COLOR_RED);
                        else
                            receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-DOUBLE", EventReceiver.COLOR_ORANGE);
                        break;
                    case EVENT_TSPIN_TRIPLE:
                        if (lastb2b)
                            receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-TRIPLE", EventReceiver.COLOR_RED);
                        else
                            receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-TRIPLE", EventReceiver.COLOR_ORANGE);
                        break;
                    case EVENT_TSPIN_EZ:
                        if (lastb2b)
                            receiver.drawMenuFont(engine, playerID, 3, 21, "EZ-" + strPieceName, EventReceiver.COLOR_RED);
                        else
                            receiver.drawMenuFont(engine, playerID, 3, 21, "EZ-" + strPieceName, EventReceiver.COLOR_ORANGE);
                        break;
                }

                if ((lastcombo >= 2) && (lastevent != EVENT_TSPIN_ZERO_MINI) && (lastevent != EVENT_TSPIN_ZERO))
                    receiver.drawMenuFont(engine, playerID, 2, 22, (lastcombo - 1) + "COMBO", EventReceiver.COLOR_CYAN);
            }
        }

        // NET: Number of spectators
        netDrawSpectatorsCount(engine, 0, 19);
        // NET: All number of players
        if (playerID == getPlayers() - 1) {
            netDrawAllPlayersCount(engine);
            netDrawGameRate(engine);
        }
        // NET: Player name (It may also appear in offline replay)
        netDrawPlayerName(engine);
    }

    /*
     * Calculate score
     */
    @Override
    public void calcScore(GameEngine engine, int playerID, int lines) {
        // Line clear bonus
        int pts = 0;

        if (engine.tspin) {
            // T-Spin 0 lines
            if ((lines == 0) && (!engine.tspinez)) {
                if (engine.tspinmini) {
                    pts += 100;
                    lastevent = EVENT_TSPIN_ZERO_MINI;
                } else {
                    pts += 400;
                    lastevent = EVENT_TSPIN_ZERO;
                }
            }
            // Immobile EZ Spin
            else if (engine.tspinez && (lines > 0)) {
                if (engine.b2b) {
                    pts += 180 * (engine.statistics.level + 1);
                } else {
                    pts += 120 * (engine.statistics.level + 1);
                }
                lastevent = EVENT_TSPIN_EZ;
            }
            // T-Spin 1 line
            else if (lines == 1) {
                if (engine.tspinmini) {
                    if (engine.b2b) {
                        pts += 300;
                    } else {
                        pts += 200;
                    }
                    lastevent = EVENT_TSPIN_SINGLE_MINI;
                } else {
                    if (engine.b2b) {
                        pts += 1200;
                    } else {
                        pts += 800;
                    }
                    lastevent = EVENT_TSPIN_SINGLE;
                }
            }
            // T-Spin 2 lines
            else if (lines == 2) {
                if (engine.tspinmini && engine.useAllSpinBonus) {
                    if (engine.b2b) {
                        pts += 600 * (engine.statistics.level + 1);
                    } else {
                        pts += 400 * (engine.statistics.level + 1);
                    }
                    lastevent = EVENT_TSPIN_DOUBLE_MINI;
                } else {
                    if (engine.b2b) {
                        pts += 1800 * (engine.statistics.level + 1);
                    } else {
                        pts += 1200 * (engine.statistics.level + 1);
                    }
                    lastevent = EVENT_TSPIN_DOUBLE;
                }
            }
            // T-Spin 3 lines
            else if (lines >= 3) {
                if (engine.b2b) {
                    pts += 2400;
                } else {
                    pts += 1600;
                }
                lastevent = EVENT_TSPIN_TRIPLE;
            }
        } else {
            if (lines == 1) {
                pts += 100; // 1Column
                lastevent = EVENT_SINGLE;
            } else if (lines == 2) {
                pts += 300; // 2Column
                lastevent = EVENT_DOUBLE;
            } else if (lines == 3) {
                pts += 500; // 3Column
                lastevent = EVENT_TRIPLE;
            } else if (lines >= 4) {
                // 4 lines
                if (engine.b2b) {
                    pts += 1200;
                } else {
                    pts += 800;
                }
                lastevent = EVENT_FOUR;
            }
        }

        lastb2b = engine.b2b;

        // Combo
        if ((enableCombo) && (engine.combo >= 1) && (lines >= 1)) {
            pts += ((engine.combo - 1) * 50);
            lastcombo = engine.combo;
        }

        // All clear
        if ((lines >= 1) && (engine.field.isEmpty())) {
            engine.playSE("bravo");
            pts += 3000;
        }

        // Add to score
        if (pts > 0) {
            lastpiece = engine.nowPieceObject.id;
            lastscore = pts;
            scgettime = 0;
            if (lines >= 1) engine.statistics.scoreFromLineClear += pts;
            else engine.statistics.scoreFromOtherBonus += pts;
            engine.statistics.score += pts;
        }
    }

    /*
     * Soft drop
     */
    @Override
    public void afterSoftDropFall(GameEngine engine, int playerID, int fall) {
        engine.statistics.scoreFromSoftDrop += fall;
        engine.statistics.score += fall;
    }

    /*
     * Hard drop
     */
    @Override
    public void afterHardDropFall(GameEngine engine, int playerID, int fall) {
        engine.statistics.scoreFromHardDrop += fall * 2;
        engine.statistics.score += fall * 2;
    }

    /*
     * Each frame Processing at the end of
     */
    @Override
    public void onLast(GameEngine engine, int playerID) {
        if (engine.gameActive && engine.timerActive) {
            int limitTime = ((goaltype + 1) * 3600);
            int remainTime = ((goaltype + 1) * 3600) - engine.statistics.time;

            // Time meter
            engine.meterValue = (remainTime * receiver.getMeterMax(engine)) / limitTime;
            engine.meterColor = GameEngine.METER_COLOR_GREEN;
            if (remainTime <= 30 * 60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
            if (remainTime <= 20 * 60) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
            if (remainTime <= 10 * 60) engine.meterColor = GameEngine.METER_COLOR_RED;

            if (!netIsWatch) {
                // Out of time
                if (engine.statistics.time >= limitTime) {
                    engine.gameEnded();
                    engine.resetStatc();
                    engine.stat = GameEngine.Status.ENDINGSTART;
                    return;
                }

                // 10Seconds before the countdown
                if ((engine.statistics.time >= limitTime - (10 * 60)) && (engine.statistics.time % 60 == 0)) {
                    engine.playSE("countdown");
                }

                // 5Of seconds beforeBGM fadeout
                if (engine.statistics.time >= limitTime - (5 * 60)) {
                    owner.bgmStatus.fadesw = true;
                }

                // 1Per-minuteBackgroundSwitching
                if ((engine.statistics.time > 0) && (engine.statistics.time % 3600 == 0)) {
                    engine.playSE("levelup");
                    owner.backgroundStatus.fadesw = true;
                    owner.backgroundStatus.fadebg = owner.backgroundStatus.bg + 1;
                }
            }
        }

        scgettime++;
    }

    /*
     * Render results screen
     */
    @Override
    public void renderResult(GameEngine engine, int playerID) {
        drawResultStats(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, Statistic.SCORE);
        if (rankingRank[0] != -1) {
            String strRank = String.format("RANK %d", rankingRank[0] + 1);
            receiver.drawMenuFont(engine, playerID, 4, 2, strRank, EventReceiver.COLOR_ORANGE);
        }

        drawResultStats(engine, playerID, receiver, 3, EventReceiver.COLOR_BLUE, Statistic.LINES);
        if (rankingRank[1] != -1) {
            String strRank = String.format("RANK %d", rankingRank[1] + 1);
            receiver.drawMenuFont(engine, playerID, 4, 5, strRank, EventReceiver.COLOR_ORANGE);
        }

        drawResultStats(engine, playerID, receiver, 6, EventReceiver.COLOR_BLUE,
                Statistic.PIECE, Statistic.SPL, Statistic.SPM, Statistic.LPM, Statistic.PPS);

        drawResultNetRank(engine, playerID, receiver, 16, EventReceiver.COLOR_BLUE, netRankingRank[0]);
        drawResultNetRankDaily(engine, playerID, receiver, 18, EventReceiver.COLOR_BLUE, netRankingRank[1]);

        if (netIsPB) {
            receiver.drawMenuFont(engine, playerID, 2, 21, "NEW PB", EventReceiver.COLOR_ORANGE);
        }

        if (netIsNetPlay && (netReplaySendStatus == 1)) {
            receiver.drawMenuFont(engine, playerID, 0, 22, "SENDING...", EventReceiver.COLOR_PINK);
        } else if (netIsNetPlay && !netIsWatch && (netReplaySendStatus == 2)) {
            receiver.drawMenuFont(engine, playerID, 1, 22, "A: RETRY", EventReceiver.COLOR_RED);
        }
    }

    /*
     * Called when saving replay
     */
    @Override
    public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
        savePreset(engine, engine.owner.replayProp, -1);
        engine.owner.replayProp.setProperty("ultra.version", version);

        // NET: Save name
        if ((netPlayerName != null) && (netPlayerName.length() > 0)) {
            prop.setProperty(playerID + ".net.netPlayerName", netPlayerName);
        }

        // Update rankings
        if ((owner.replayMode == false) && (big == false) && (engine.ai == null)) {
            updateRanking(engine.statistics.score, engine.statistics.lines);

            if ((rankingRank[0] != -1) || (rankingRank[1] != -1)) {
                saveRanking(owner.modeConfig, engine.ruleopt.strRuleName);
                receiver.saveModeConfig(owner.modeConfig);
            }
        }
    }

    /**
     * Read rankings from property file
     *
     * @param prop     Property file
     * @param ruleName Rule name
     */
    @Override
    void loadRanking(CustomProperties prop, String ruleName) {
        for (int i = 0; i < GOALTYPE_MAX; i++) {
            for (int j = 0; j < RANKING_TYPE; j++) {
                for (int k = 0; k < RANKING_MAX; k++) {
                    rankingScore[i][j][k] = prop.getProperty("ultra.ranking." + ruleName + "." + i + "." + j + ".score." + k, 0);
                    rankingLines[i][j][k] = prop.getProperty("ultra.ranking." + ruleName + "." + i + "." + j + ".lines." + k, 0);
                }
            }
        }
    }

    /**
     * Save rankings to property file
     *
     * @param prop     Property file
     * @param ruleName Rule name
     */
    private void saveRanking(CustomProperties prop, String ruleName) {
        for (int i = 0; i < GOALTYPE_MAX; i++) {
            for (int j = 0; j < RANKING_TYPE; j++) {
                for (int k = 0; k < RANKING_MAX; k++) {
                    prop.setProperty("ultra.ranking." + ruleName + "." + i + "." + j + ".score." + k, rankingScore[i][j][k]);
                    prop.setProperty("ultra.ranking." + ruleName + "." + i + "." + j + ".lines." + k, rankingLines[i][j][k]);
                }
            }
        }
    }

    /**
     * Update rankings
     *
     * @param sc Score
     * @param li Lines
     */
    private void updateRanking(int sc, int li) {
        for (int i = 0; i < RANKING_TYPE; i++) {
            rankingRank[i] = checkRanking(sc, li, i);

            if (rankingRank[i] != -1) {
                // Shift down ranking entries
                for (int j = RANKING_MAX - 1; j > rankingRank[i]; j--) {
                    rankingScore[goaltype][i][j] = rankingScore[goaltype][i][j - 1];
                    rankingLines[goaltype][i][j] = rankingLines[goaltype][i][j - 1];
                }

                // Add new data
                rankingScore[goaltype][i][rankingRank[i]] = sc;
                rankingLines[goaltype][i][rankingRank[i]] = li;
            }
        }
    }

    /**
     * Calculate ranking position
     *
     * @param sc          Score
     * @param li          Lines
     * @param rankingtype Number of ranking types
     * @return Position (-1 if unranked)
     */
    private int checkRanking(int sc, int li, int rankingtype) {
        for (int i = 0; i < RANKING_MAX; i++) {
            if (rankingtype == 0) {
                if (sc > rankingScore[goaltype][rankingtype][i]) {
                    return i;
                } else if ((sc == rankingScore[goaltype][rankingtype][i]) && (li > rankingLines[goaltype][rankingtype][i])) {
                    return i;
                }
            } else {
                if (li > rankingLines[goaltype][rankingtype][i]) {
                    return i;
                } else if ((li == rankingLines[goaltype][rankingtype][i]) && (sc > rankingScore[goaltype][rankingtype][i])) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * NET: Send various in-game stats (as well as goaltype)
     *
     * @param engine GameEngine
     */
    @Override
    protected void netSendStats(GameEngine engine) {
        int bg = owner.backgroundStatus.fadesw ? owner.backgroundStatus.fadebg : owner.backgroundStatus.bg;
        String msg = "game\tstats\t";
        msg += engine.statistics.score + "\t" + engine.statistics.lines + "\t" + engine.statistics.totalPieceLocked + "\t";
        msg += engine.statistics.time + "\t" + engine.statistics.spm + "\t";
        msg += engine.statistics.lpm + "\t" + engine.statistics.spl + "\t" + goaltype + "\t";
        msg += engine.gameActive + "\t" + engine.timerActive + "\t";
        msg += lastscore + "\t" + scgettime + "\t" + lastevent + "\t" + lastb2b + "\t" + lastcombo + "\t" + lastpiece + "\t";
        msg += bg + "\n";
        netLobby.netPlayerClient.send(msg);
    }

    /**
     * NET: Receive various in-game stats (as well as goaltype)
     */
    @Override
    protected void netRecvStats(GameEngine engine, String[] message) {
        engine.statistics.score = Integer.parseInt(message[4]);
        engine.statistics.lines = Integer.parseInt(message[5]);
        engine.statistics.totalPieceLocked = Integer.parseInt(message[6]);
        engine.statistics.time = Integer.parseInt(message[7]);
        engine.statistics.spm = Double.parseDouble(message[8]);
        engine.statistics.lpm = Float.parseFloat(message[9]);
        engine.statistics.spl = Double.parseDouble(message[10]);
        goaltype = Integer.parseInt(message[11]);
        engine.gameActive = Boolean.parseBoolean(message[12]);
        engine.timerActive = Boolean.parseBoolean(message[13]);
        lastscore = Integer.parseInt(message[14]);
        scgettime = Integer.parseInt(message[15]);
        lastevent = Integer.parseInt(message[16]);
        lastb2b = Boolean.parseBoolean(message[17]);
        lastcombo = Integer.parseInt(message[18]);
        lastpiece = Integer.parseInt(message[19]);
        owner.backgroundStatus.bg = Integer.parseInt(message[20]);

        // Time meter
        int limitTime = ((goaltype + 1) * 3600);
        int remainTime = ((goaltype + 1) * 3600) - engine.statistics.time;
        engine.meterValue = (remainTime * receiver.getMeterMax(engine)) / limitTime;
        engine.meterColor = GameEngine.METER_COLOR_GREEN;
        if (remainTime <= 30 * 60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
        if (remainTime <= 20 * 60) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
        if (remainTime <= 10 * 60) engine.meterColor = GameEngine.METER_COLOR_RED;
    }

    /**
     * NET: Send end-of-game stats
     *
     * @param engine GameEngine
     */
    @Override
    protected void netSendEndGameStats(GameEngine engine) {
        String subMsg = "";
        subMsg += "SCORE;" + engine.statistics.score + "\t";
        subMsg += "LINE;" + engine.statistics.lines + "\t";
        subMsg += "PIECE;" + engine.statistics.totalPieceLocked + "\t";
        subMsg += "SCORE/LINE;" + engine.statistics.spl + "\t";
        subMsg += "SCORE/MIN;" + engine.statistics.spm + "\t";
        subMsg += "LINE/MIN;" + engine.statistics.lpm + "\t";
        subMsg += "PIECE/SEC;" + engine.statistics.pps + "\t";

        String msg = "gstat1p\t" + NetUtil.urlEncode(subMsg) + "\n";
        netLobby.netPlayerClient.send(msg);
    }

    /**
     * NET: Send game options to all spectators
     *
     * @param engine GameEngine
     */
    @Override
    void netSendOptions(GameEngine engine) {
        String msg = "game\toption\t";
        msg += engine.speed.gravity + "\t" + engine.speed.denominator + "\t" + engine.speed.are + "\t";
        msg += engine.speed.areLine + "\t" + engine.speed.lineDelay + "\t" + engine.speed.lockDelay + "\t";
        msg += engine.speed.das + "\t" + bgmno + "\t" + big + "\t" + goaltype + "\t" + tspinEnableType + "\t";
        msg += enableTSpinKick + "\t" + enableB2B + "\t" + enableCombo + "\t" + presetNumber + "\t";
        msg += spinCheckType + "\t" + tspinEnableEZ + "\n";
        netLobby.netPlayerClient.send(msg);
    }

    /**
     * NET: Receive game options
     */
    @Override
    protected void netRecvOptions(GameEngine engine, String[] message) {
        engine.speed.gravity = Integer.parseInt(message[4]);
        engine.speed.denominator = Integer.parseInt(message[5]);
        engine.speed.are = Integer.parseInt(message[6]);
        engine.speed.areLine = Integer.parseInt(message[7]);
        engine.speed.lineDelay = Integer.parseInt(message[8]);
        engine.speed.lockDelay = Integer.parseInt(message[9]);
        engine.speed.das = Integer.parseInt(message[10]);
        bgmno = Integer.parseInt(message[11]);
        big = Boolean.parseBoolean(message[12]);
        goaltype = Integer.parseInt(message[13]);
        tspinEnableType = Integer.parseInt(message[14]);
        enableTSpinKick = Boolean.parseBoolean(message[15]);
        enableB2B = Boolean.parseBoolean(message[16]);
        enableCombo = Boolean.parseBoolean(message[17]);
        presetNumber = Integer.parseInt(message[18]);
        spinCheckType = Integer.parseInt(message[19]);
        tspinEnableEZ = Boolean.parseBoolean(message[20]);
    }

    /**
     * NET: Get goal type
     */
    @Override
    protected int netGetGoalType() {
        return goaltype;
    }

    /**
     * NET: It returns true when the current settings doesn't prevent leaderboard screen from showing.
     */
    @Override
    boolean netIsNetRankingViewOK(GameEngine engine) {
        return (!big) && (engine.ai == null);
    }
}
