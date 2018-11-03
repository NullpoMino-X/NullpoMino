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
package mu.nu.nullpo.gui.slick;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.JOptionPane;

import mu.nu.nullpo.game.net.NetObserverClient;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.ModeManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

/**
 * NullpoMino SlickVersion
 */
class NullpoMinoSlick extends StateBasedGame {
    /**
     * Log
     */
    private static Logger log = Logger.getLogger(NullpoMinoSlick.class);

    /**
     * Command that was passed to the programLinesArgumentcount
     */
    private static String[] programArgs;

    /**
     * Save settingsUseProperty file
     */
    public static CustomProperties propConfig;

    /**
     * Save settingsUseProperty file (AllVersionCommon)
     */
    public static CustomProperties propGlobal;

    /**
     * Music ListProperty file
     */
    public static CustomProperties propMusic;

    /**
     * ObserverFor the functionProperty file
     */
    private static CustomProperties propObserver;

    /**
     * Default language file
     */
    private static CustomProperties propLangDefault;

    /**
     * Language file
     */
    private static CustomProperties propLang;

    /**
     * Default game mode description file
     */
    public static CustomProperties propDefaultModeDesc;

    /**
     * Game mode description file
     */
    public static CustomProperties propModeDesc;

    /**
     * ScreenshotUse
     */
    private static BufferedImage ssImage;

    /**
     * Mode Management
     */
    public static ModeManager modeManager;

    /**
     * AppGameContainer
     */
    public static AppGameContainer appGameContainer;

    /**
     * State of the loading screen
     */
    private static StateLoading stateLoading;

    /**
     * State of the title screen
     */
    private static StateTitle stateTitle;

    /**
     * State of the game screen
     */
    public static StateInGame stateInGame;

    /**
     * Mode State selection screen
     */
    private static StateSelectMode stateSelectMode;

    /**
     * State selection screen replay
     */
    private static StateReplaySelect stateReplaySelect;

    /**
     * State of the configuration screen
     */
    private static StateConfigMainMenu stateConfigMainMenu;

    /**
     * State of the general settings screen
     */
    private static StateConfigGeneral stateConfigGeneral;

    /**
     * State rules on the selection screen
     */
    public static StateConfigRuleSelect stateConfigRuleSelect;

    /**
     * AIState selection screen
     */
    public static StateConfigAISelect stateConfigAISelect;

    /**
     * State of the keyboard setting screen
     */
    public static StateConfigKeyboard stateConfigKeyboard;

    /**
     * Joystick buttonState of the configuration screen
     */
    public static StateConfigJoystickButton stateConfigJoystickButton;

    /**
     * State of Play screen net
     */
    private static StateNetGame stateNetGame;

    /**
     * Joystick Settings MainMenu State
     */
    public static StateConfigJoystickMain stateConfigJoystickMain;

    /**
     * Joystick State of the test screen
     */
    public static StateConfigJoystickTest stateConfigJoystickTest;

    /**
     * State of tuning settings screen
     */
    public static StateConfigGameTuning stateConfigGameTuning;

    /**
     * Style select state
     */
    public static StateConfigRuleStyleSelect stateConfigRuleStyleSelect;

    /**
     * Keyboard menu navigation settings state
     */
    public static StateConfigKeyboardNavi stateConfigKeyboardNavi;

    /**
     * Keyboard Reset menu state
     */
    public static StateConfigKeyboardReset stateConfigKeyboardReset;

    /**
     * Rule select (after mode selection)
     */
    private static StateSelectRuleFromList stateSelectRuleFromList;

    /**
     * Mode folder select
     */
    private static StateSelectModeFolder stateSelectModeFolder;

    /**
     * Timing of alternate FPS sleep (false=render true=update)
     */
    public static boolean alternateFPSTiming;

    /**
     * Allow dynamic adjust of target FPS (as seen in Swing version)
     */
    private static boolean alternateFPSDynamicAdjust;

    /**
     * Perfect FPS mode (more accurate, eats more CPU)
     */
    private static boolean alternateFPSPerfectMode;

    /**
     * Execute Thread.yield() during Perfect FPS mode
     */
    private static boolean alternateFPSPerfectYield;

    /**
     * Target FPS
     */
    public static int altMaxFPS;

    /**
     * Current max FPS
     */
    private static int altMaxFPSCurrent;

    /**
     * Used for FPS calculation
     */
    private static long periodCurrent;

    /**
     * FPSFor maintaining
     */
    private static long beforeTime;

    /**
     * FPSFor maintaining
     */
    private static long overSleepTime;

    /**
     * FPSFor maintaining
     */
    private static int noDelays;

    /**
     * FPSFor calculation
     */
    private static long calcInterval = 0;

    /**
     * FPSFor calculation
     */
    private static long prevCalcTime = 0;

    /**
     * frame count
     */
    private static long frameCount = 0;

    /**
     * ActualFPS
     */
    private static double actualFPS = 0.0;

    /**
     * FPSDisplayDecimalFormat
     */
    private static DecimalFormat df = new DecimalFormat("0.0");

    /**
     * Used by perfect fps mode
     */
    private static long perfectFPSDelay = 0;

    /**
     * ObserverClient
     */
    private static NetObserverClient netObserverClient;

    /**
     * true if read keyboard input from JInput
     */
    public static boolean useJInputKeyboard;

    /**
     * true to use safer texture loading (Use BigImage instead of regular Image)
     */
    public static boolean useBigImageTextureLoad;

    /**
     * Main functioncount
     *
     * @param args Command that was passed to the programLinesArgumentcount
     */
    public static void main(String[] args) {
        programArgs = args;

        PropertyConfigurator.configure("config/etc/log_slick.cfg");
        Log.setLogSystem(new LogSystemLog4j());
        log.info("NullpoMinoSlick Start");

        propConfig = new CustomProperties();
        propGlobal = new CustomProperties();
        propMusic = new CustomProperties();

        // Read configuration file
        try {
            FileInputStream in = new FileInputStream("config/setting/slick.cfg");
            propConfig.load(in);
            in.close();
        } catch (IOException e) {
        }
        loadGlobalConfig();
        try {
            FileInputStream in = new FileInputStream("config/setting/music.cfg");
            propMusic.load(in);
            in.close();
        } catch (IOException e) {
        }

        // Read language file
        propLangDefault = new CustomProperties();
        try {
            FileInputStream in = new FileInputStream("config/lang/slick_default.properties");
            propLangDefault.load(in);
            in.close();
        } catch (IOException e) {
            log.error("Couldn't load default UI language file", e);
        }

        propLang = new CustomProperties();
        try {
            FileInputStream in = new FileInputStream("config/lang/slick_" + Locale.getDefault().getCountry() + ".properties");
            propLang.load(in);
            in.close();
        } catch (IOException e) {
        }

        // Game mode description
        propDefaultModeDesc = new CustomProperties();
        try {
            FileInputStream in = new FileInputStream("config/lang/modedesc_default.properties");
            propDefaultModeDesc.load(in);
            in.close();
        } catch (IOException e) {
            log.error("Couldn't load default mode description file", e);
        }

        propModeDesc = new CustomProperties();
        try {
            FileInputStream in = new FileInputStream("config/lang/modedesc_" + Locale.getDefault().getCountry() + ".properties");
            propModeDesc.load(in);
            in.close();
        } catch (IOException e) {
        }

        // ModeRead
        modeManager = new ModeManager();
        try {
            BufferedReader txtMode = new BufferedReader(new FileReader("config/list/mode.lst"));
            modeManager.loadGameModes(txtMode);
            txtMode.close();
        } catch (IOException e) {
            log.error("Mode list load failed", e);
        }

        // Set default rule selections
        try {
            CustomProperties propDefaultRule = new CustomProperties();
            FileInputStream in = new FileInputStream("config/list/global_defaultrule.properties");
            propDefaultRule.load(in);
            in.close();

            for (int pl = 0; pl < 2; pl++)
                for (int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
                    // TETROMINO
                    if (i == 0) {
                        if (propGlobal.getProperty(pl + ".rule") == null) {
                            propGlobal.setProperty(pl + ".rule", propDefaultRule.getProperty("default.rule", ""));
                            propGlobal.setProperty(pl + ".rulefile", propDefaultRule.getProperty("default.rulefile", ""));
                            propGlobal.setProperty(pl + ".rulename", propDefaultRule.getProperty("default.rulename", ""));
                        }
                    }
                    // etc
                    else {
                        if (propGlobal.getProperty(pl + ".rule." + i) == null) {
                            propGlobal.setProperty(pl + ".rule." + i, propDefaultRule.getProperty("default.rule." + i, ""));
                            propGlobal.setProperty(pl + ".rulefile." + i, propDefaultRule.getProperty("default.rulefile." + i, ""));
                            propGlobal.setProperty(pl + ".rulename." + i, propDefaultRule.getProperty("default.rulename." + i, ""));
                        }
                    }
                }
        } catch (Exception e) {
        }

        // Command line options
        useJInputKeyboard = false;
        useBigImageTextureLoad = false;

        for (String str : args) {
            if (str.equals("-j") || str.equals("/j")) {
                useJInputKeyboard = true;
                log.info("-j option is used. Use JInput to read keyboard input.");
            } else if (str.equals("-b") || str.equals("/b")) {
                useBigImageTextureLoad = true;
                log.info("-b option is used. Use BigImage instead of normal Image.");
            }
        }

        perfectFPSDelay = System.nanoTime();

        // Get driver name and version
        String strDriverName = null;
        String strDriverVersion = null;
        try {
            strDriverName = Display.getAdapter();
            strDriverVersion = Display.getVersion();
            log.info("Driver adapter:" + strDriverName + ", Driver version:" + strDriverVersion);
        } catch (Throwable e) {
            log.fatal("LWJGL load failed", e);

            // LWJGL Load failed! Do the file of LWJGL exist?
            File fileLWJGL = null;
            if (!System.getProperty("os.arch").contains("64") && System.getProperty("os.name").contains("Windows")) {
                fileLWJGL = new File("lib/lwjgl.dll");
            } else if (System.getProperty("os.arch").contains("64") && System.getProperty("os.name").contains("Windows")) {
                fileLWJGL = new File("lib/lwjgl64.dll");
            } else if (System.getProperty("os.name").contains("Mac OS")) {
                fileLWJGL = new File("lib/liblwjgl.jnilib");
            } else if (System.getProperty("os.arch").contains("64")) {
                fileLWJGL = new File("lib/liblwjgl64.so");
            } else {
                fileLWJGL = new File("lib/liblwjgl.so");
            }

            if (fileLWJGL.isFile() && fileLWJGL.canRead()) {
                // File exists but incompatible with your OS
                String strErrorTitle = getUIText("LWJGLLoadFailedMessage_Title");
                String strErrorMessage = String.format(getUIText("LWJGLLoadFailedMessage_Body"), e.toString());
                JOptionPane.showMessageDialog(null, strErrorMessage, strErrorTitle, JOptionPane.ERROR_MESSAGE);
            } else {
                // Not found
                String strErrorTitle = getUIText("LWJGLNotFoundMessage_Title");
                String strErrorMessage = String.format(getUIText("LWJGLNotFoundMessage_Body"), e.toString());
                JOptionPane.showMessageDialog(null, strErrorMessage, strErrorTitle, JOptionPane.ERROR_MESSAGE);
            }

            // Exit
            System.exit(-3);
        }
        if (strDriverName == null) strDriverName = "(Unknown)";
        if (strDriverVersion == null) strDriverVersion = "(Unknown)";

        // Initialization, such as the game screen
        try {
            int sWidth = propConfig.getProperty("option.screenwidth", 640);
            int sHeight = propConfig.getProperty("option.screenheight", 480);

            NullpoMinoSlick obj = new NullpoMinoSlick();

            if ((sWidth != 640) || (sHeight != 480)) {
                ScalableGame sObj = new ScalableGame(obj, 640, 480, true);
                appGameContainer = new AppGameContainer(sObj);
            } else {
                appGameContainer = new AppGameContainer(obj);
            }
            appGameContainer.setShowFPS(false);
            appGameContainer.setClearEachFrame(false);
            appGameContainer.setMinimumLogicUpdateInterval(0);
            appGameContainer.setMaximumLogicUpdateInterval(0);
            appGameContainer.setUpdateOnlyWhenVisible(false);
            appGameContainer.setForceExit(false);
            appGameContainer.setDisplayMode(sWidth, sHeight, propConfig.getProperty("option.fullscreen", false));
            appGameContainer.start();
        } catch (SlickException e) {
            log.fatal("Game initialize failed (SlickException)", e);

            // Display an error dialog
            String strErrorTitle = getUIText("InitFailedMessageSlick_Title");
            String strErrorMessage = String.format(getUIText("InitFailedMessageSlick_Body"), strDriverName, strDriverVersion, e.toString());
            JOptionPane.showMessageDialog(null, strErrorMessage, strErrorTitle, JOptionPane.ERROR_MESSAGE);

            // Exit
            System.exit(-1);
        } catch (Throwable e) {
            log.fatal("Game initialize failed (NON-SlickException)", e);

            // Display an error dialog
            String strErrorTitle = getUIText("InitFailedMessageGeneral_Title");
            String strErrorMessage = String.format(getUIText("InitFailedMessageGeneral_Body"), strDriverName, strDriverVersion, e.toString());
            JOptionPane.showMessageDialog(null, strErrorMessage, strErrorTitle, JOptionPane.ERROR_MESSAGE);

            // Exit
            System.exit(-2);
        }

        stopObserverClient();

        if (stateNetGame.netLobby != null) {
            log.debug("Calling netLobby shutdown routine");
            stateNetGame.netLobby.shutdown();
        }

        System.exit(0);
    }

    /**
     * Save the configuration file
     */
    public static void saveConfig() {
        try {
            FileOutputStream out = new FileOutputStream("config/setting/slick.cfg");
            propConfig.store(out, "NullpoMino Slick-frontend Config");
            out.close();
        } catch (IOException e) {
            log.error("Failed to save Slick-specific config", e);
        }

        try {
            FileOutputStream out = new FileOutputStream("config/setting/global.cfg");
            propGlobal.store(out, "NullpoMino Global Config");
            out.close();
        } catch (IOException e) {
            log.error("Failed to save global config", e);
        }
    }

    /**
     * (Re-)Load global config file
     */
    public static void loadGlobalConfig() {
        try {
            FileInputStream in = new FileInputStream("config/setting/global.cfg");
            propGlobal.load(in);
            in.close();
        } catch (IOException e) {
        }
    }

    /**
     * To reflect the various settings
     */
    public static void setGeneralConfig() {
        appGameContainer.setTargetFrameRate(-1);
        beforeTime = System.nanoTime();
        overSleepTime = 0L;
        noDelays = 0;

        alternateFPSTiming = propConfig.getProperty("option.alternateFPSTiming", false);
        alternateFPSDynamicAdjust = propConfig.getProperty("option.alternateFPSDynamicAdjust", true);
        alternateFPSPerfectMode = propConfig.getProperty("option.alternateFPSPerfectMode", false);
        alternateFPSPerfectYield = propConfig.getProperty("option.alternateFPSPerfectYield", false);
        altMaxFPS = propConfig.getProperty("option.maxfps", 60);
        altMaxFPSCurrent = altMaxFPS;
        periodCurrent = (long) (1.0 / altMaxFPSCurrent * 1000000000);

        appGameContainer.setVSync(propConfig.getProperty("option.vsync", true));
        appGameContainer.setAlwaysRender(!alternateFPSTiming);

        int sevolume = propConfig.getProperty("option.sevolume", 128);
        appGameContainer.setSoundVolume(sevolume / (float) 128);

        ControllerManager.method = propConfig.getProperty("option.joymethod", ControllerManager.CONTROLLER_METHOD_SLICK_DEFAULT);
        ControllerManager.controllerID[0] = propConfig.getProperty("joyUseNumber.p0", -1);
        ControllerManager.controllerID[1] = propConfig.getProperty("joyUseNumber.p1", -1);
        int joyBorder = propConfig.getProperty("joyBorder.p0", 0);
        ControllerManager.border[0] = joyBorder / (float) 32768;
        joyBorder = propConfig.getProperty("joyBorder.p1", 0);
        ControllerManager.border[1] = joyBorder / (float) 32768;
        ControllerManager.ignoreAxis[0] = propConfig.getProperty("joyIgnoreAxis.p0", false);
        ControllerManager.ignoreAxis[1] = propConfig.getProperty("joyIgnoreAxis.p1", false);
        ControllerManager.ignorePOV[0] = propConfig.getProperty("joyIgnorePOV.p0", false);
        ControllerManager.ignorePOV[1] = propConfig.getProperty("joyIgnorePOV.p1", false);

        //useJInputKeyboard = propConfig.getProperty("option.useJInputKeyboard", true);
    }

    /**
     * ScreenshotSave
     *
     * @param container GameContainer
     * @param g         Graphics
     */
    public static void saveScreenShot(GameContainer container, Graphics g) {
        // FilenameI decided to
        String dir = propGlobal.getProperty("custom.screenshot.directory", "ss");
        Calendar c = Calendar.getInstance();
        DateFormat dfm = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String filename = dir + "/" + dfm.format(c.getTime()) + ".png";
        log.info("Saving screenshot to " + filename);

        // ScreenshotCreating
        try {
            File ssfolder = new File(dir);
            if (!ssfolder.exists()) {
                if (ssfolder.mkdir()) {
                    log.info("Created screenshot folder: " + dir);
                } else {
                    log.info("Couldn't create screenshot folder at " + dir);
                }
            }

            int screenWidth = container.getWidth();
            int screenHeight = container.getHeight();

            Image screenImage = new Image(screenWidth, screenHeight);
            g.copyArea(screenImage, 0, 0);

            // Upside down and the following way
            //ImageOut.write(screenImage, filename);

            // Copy the screen on their own so
            if (ssImage == null) {
                ssImage = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
            }

            for (int i = 0; i < screenWidth; i++)
                for (int j = 0; j < screenHeight; j++) {
                    Color color = screenImage.getColor(i, j + 1);    // SomehowY-coordinateThe+1I seem not to deviate

                    int rgb =
                            ((color.getRed() & 0x000000FF) << 16) |
                                    ((color.getGreen() & 0x000000FF) << 8) |
                                    ((color.getBlue() & 0x000000FF) << 0);

                    ssImage.setRGB(i, j, rgb);
                }

            // Save to File
            javax.imageio.ImageIO.write(ssImage, "png", new File(filename));
        } catch (Throwable e) {
            log.error("Failed to create screen shot", e);
        }
    }

    /**
     * PosttranslationalUIGets a string of
     *
     * @param str String
     * @return PosttranslationalUIString (If you do not acceptstrReturns)
     */
    public static String getUIText(String str) {
        String result = propLang.getProperty(str);
        if (result == null) {
            result = propLangDefault.getProperty(str, str);
        }
        return result;
    }

    /**
     * FPS cap routine
     */
    public static void alternateFPSSleep() {
        alternateFPSSleep(false);
    }

    /**
     * FPS cap routine
     *
     * @param ingame <code>true</code> if during the gameplay
     */
    public static void alternateFPSSleep(boolean ingame) {
        int maxfps = altMaxFPSCurrent;

        if (maxfps > 0) {
            boolean sleepFlag = false;
            long afterTime, timeDiff, sleepTime, sleepTimeInMillis;

            afterTime = System.nanoTime();
            timeDiff = afterTime - beforeTime;

            sleepTime = (periodCurrent - timeDiff) - overSleepTime;
            sleepTimeInMillis = sleepTime / 1000000L;

            if ((sleepTimeInMillis >= 10) && (!alternateFPSPerfectMode || !ingame)) {
                // If it is possible to use sleep
                if (maxfps > 0) {
                    try {
                        Thread.sleep(sleepTimeInMillis);
                    } catch (InterruptedException e) {
                    }
                }
                // sleep() oversleep
                overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
                perfectFPSDelay = System.nanoTime();
                sleepFlag = true;
            } else if ((alternateFPSPerfectMode && ingame) || (sleepTime > 0)) {
                // Perfect FPS
                overSleepTime = 0L;
                if (altMaxFPSCurrent > altMaxFPS + 5) altMaxFPSCurrent = altMaxFPS + 5;
                if (alternateFPSPerfectYield) {
                    while (System.nanoTime() < perfectFPSDelay + 1000000000 / altMaxFPS) {
                        Thread.yield();
                    }
                } else {
                    while (System.nanoTime() < perfectFPSDelay + 1000000000 / altMaxFPS) {
                    }
                }
                perfectFPSDelay += 1000000000 / altMaxFPS;

                // Don't run in super fast after the heavy slowdown
                if (System.nanoTime() > perfectFPSDelay + 2000000000 / altMaxFPS) {
                    perfectFPSDelay = System.nanoTime();
                }

                sleepFlag = true;
            }

            if (!sleepFlag) {
                // Impossible to sleep!
                overSleepTime = 0L;
                if (++noDelays >= 16) {
                    Thread.yield();
                    noDelays = 0;
                }
                perfectFPSDelay = System.nanoTime();
            }

            beforeTime = System.nanoTime();
            calcFPS(ingame, periodCurrent);
        } else {
            periodCurrent = (long) (1.0 / 60 * 1000000000);
            calcFPS(ingame, periodCurrent);
        }
    }

    /**
     * FPSCalculation of
     *
     * @param period FPSInterval to calculate the
     */
    private static void calcFPS(boolean ingame, long period) {
        frameCount++;
        calcInterval += period;

        // 1Second intervalsFPSRecalculate the
        if (calcInterval >= 1000000000L) {
            long timeNow = System.nanoTime();

            // Actual elapsed timeMeasure
            long realElapsedTime = timeNow - prevCalcTime; // Unit: ns

            // FPSCalculate the
            // realElapsedTimeThe unit ofnsSosConverted to
            actualFPS = ((double) frameCount / realElapsedTime) * 1000000000L;

            frameCount = 0L;
            calcInterval = 0L;
            prevCalcTime = timeNow;

            // Set new target fps
            if ((altMaxFPS > 0) && (alternateFPSDynamicAdjust) && (!alternateFPSPerfectMode)) {
                if (ingame) {
                    if (actualFPS < altMaxFPS - 1) {
                        // Too Slow
                        altMaxFPSCurrent++;
                        if (altMaxFPSCurrent > altMaxFPS + 20) altMaxFPSCurrent = altMaxFPS + 20;
                        periodCurrent = (long) (1.0 / altMaxFPSCurrent * 1000000000);
                    } else if (actualFPS > altMaxFPS + 1) {
                        // Too Fast
                        altMaxFPSCurrent--;
                        if (altMaxFPSCurrent < altMaxFPS - 0) altMaxFPSCurrent = altMaxFPS - 0;
                        if (altMaxFPSCurrent < 1) altMaxFPSCurrent = 1;
                        periodCurrent = (long) (1.0 / altMaxFPSCurrent * 1000000000);
                    }
                } else if ((!ingame) && (altMaxFPSCurrent != altMaxFPS)) {
                    altMaxFPSCurrent = altMaxFPS;
                    periodCurrent = (long) (1.0 / altMaxFPSCurrent * 1000000000);
                }
            }
        }
    }

    /**
     * Constructor
     */
    private NullpoMinoSlick() {
        super("NullpoMino (Now Loading...)");
    }

    /*
     * State (Added) or ne thing of the scene or game title
     */
    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        stateLoading = new StateLoading();
        stateTitle = new StateTitle();
        stateInGame = new StateInGame();
        stateSelectMode = new StateSelectMode();
        stateReplaySelect = new StateReplaySelect();
        stateConfigMainMenu = new StateConfigMainMenu();
        stateConfigGeneral = new StateConfigGeneral();
        stateConfigRuleSelect = new StateConfigRuleSelect();
        stateConfigAISelect = new StateConfigAISelect();
        stateConfigKeyboard = new StateConfigKeyboard();
        stateConfigJoystickButton = new StateConfigJoystickButton();
        stateNetGame = new StateNetGame();
        stateConfigJoystickMain = new StateConfigJoystickMain();
        stateConfigJoystickTest = new StateConfigJoystickTest();
        stateConfigGameTuning = new StateConfigGameTuning();
        stateConfigRuleStyleSelect = new StateConfigRuleStyleSelect();
        stateConfigKeyboardNavi = new StateConfigKeyboardNavi();
        stateConfigKeyboardReset = new StateConfigKeyboardReset();
        stateSelectRuleFromList = new StateSelectRuleFromList();
        stateSelectModeFolder = new StateSelectModeFolder();

        addState(stateLoading);
        addState(stateTitle);
        addState(stateInGame);
        addState(stateSelectMode);
        addState(stateReplaySelect);
        addState(stateConfigMainMenu);
        addState(stateConfigGeneral);
        addState(stateConfigRuleSelect);
        addState(stateConfigAISelect);
        addState(stateConfigKeyboard);
        addState(stateConfigJoystickButton);
        addState(stateNetGame);
        addState(stateConfigJoystickMain);
        addState(stateConfigJoystickTest);
        addState(stateConfigGameTuning);
        addState(stateConfigRuleStyleSelect);
        addState(stateConfigKeyboardNavi);
        addState(stateConfigKeyboardReset);
        addState(stateSelectRuleFromList);
        addState(stateSelectModeFolder);
    }

    /**
     * FPS display
     *
     * @param container GameContainer
     */
    public static void drawFPS(GameContainer container) {
        drawFPS(container, false);
    }

    /**
     * FPS display
     *
     * @param container GameContainer
     */
    public static void drawFPS(GameContainer container, boolean ingame) {
        if (propConfig.getProperty("option.showfps", true) == true) {
            if (!alternateFPSDynamicAdjust || alternateFPSPerfectMode || !ingame)
                NormalFontSlick.printFont(0, 480 - 16, df.format(actualFPS), NormalFontSlick.COLOR_BLUE);
            else
                NormalFontSlick.printFont(0, 480 - 16, df.format(actualFPS) + "/" + altMaxFPSCurrent, NormalFontSlick.COLOR_BLUE);
        }
    }

    /**
     * ObserverStart the client
     */
    public static void startObserverClient() {
        log.debug("startObserverClient called");

        propObserver = new CustomProperties();
        try {
            FileInputStream in = new FileInputStream("config/setting/netobserver.cfg");
            propObserver.load(in);
            in.close();
        } catch (IOException e) {
        }

        if (propObserver.getProperty("observer.enable", false) == false) return;
        if ((netObserverClient != null) && netObserverClient.isConnected()) return;

        String host = propObserver.getProperty("observer.host", "");
        int port = propObserver.getProperty("observer.port", NetObserverClient.DEFAULT_PORT);

        if ((host.length() > 0) && (port > 0)) {
            netObserverClient = new NetObserverClient(host, port);
            netObserverClient.start();
        }
    }

    /**
     * ObserverStop the client
     */
    public static void stopObserverClient() {
        log.debug("stopObserverClient called");

        if (netObserverClient != null) {
            if (netObserverClient.isConnected()) {
                netObserverClient.send("disconnect\n");
            }
            netObserverClient.threadRunning = false;
            netObserverClient.connectedFlag = false;
            netObserverClient = null;
        }
        propObserver = null;
    }

    /**
     * ObserverDrawing information from the client
     */
    public static void drawObserverClient() {
        if ((netObserverClient != null) && netObserverClient.isConnected()) {
            int fontcolor = NormalFontSlick.COLOR_BLUE;
            if (netObserverClient.getObserverCount() > 1) fontcolor = NormalFontSlick.COLOR_GREEN;
            if (netObserverClient.getObserverCount() > 0 && netObserverClient.getPlayerCount() > 0)
                fontcolor = NormalFontSlick.COLOR_RED;
            String strObserverInfo = String.format("%d/%d", netObserverClient.getObserverCount(), netObserverClient.getPlayerCount());
            String strObserverString = String.format("%40s", strObserverInfo);
            NormalFontSlick.printFont(0, 480 - 16, strObserverString, fontcolor);
        }
    }
}
