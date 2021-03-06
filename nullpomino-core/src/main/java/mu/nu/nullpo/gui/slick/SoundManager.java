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

import org.apache.log4j.Logger;
import org.newdawn.slick.Sound;

import java.net.URL;
import java.util.HashMap;

/**
 * Sound effectsManager
 */
public class SoundManager {
    /**
     * Log
     */
    private static Logger log = Logger.getLogger(SoundManager.class);

    /**
     * You can registerWAVE file OfMaximumcount
     */
    private int maxClips;

    /**
     * WAVE file  data (Name-> dataBody)
     */
    private HashMap<String, Sound> clipMap;

    /**
     * Was registeredWAVE file count
     */
    private int counter = 0;

    /**
     * Constructor
     */
    public SoundManager() {
        this(128);
    }

    /**
     * Constructor
     *
     * @param maxClips You can registerWAVE file OfMaximumcount
     */
    private SoundManager(int maxClips) {
        this.maxClips = maxClips;
        clipMap = new HashMap<>(maxClips);
    }

    /**
     * Load WAVE file
     *
     * @param name     Registered name
     * @param filename Filename (String)
     * @return true if successful, false if failed
     */
    public boolean load(String name, String filename) {
        if (counter >= maxClips) {
            log.error("No more wav files can be loaded (" + maxClips + ")");
            return false;
        }

        try {
            Sound clip = new Sound(filename);
            clipMap.put(name, clip);
        } catch (Throwable e) {
            log.error("Failed to load wav file", e);
            return false;
        }

        return true;
    }

    /**
     * Load WAVE file
     *
     * @param name    Registered name
     * @param fileurl Filename (URL)
     * @return true if successful, false if failed
     */
    public boolean load(String name, URL fileurl) {
        if (counter >= maxClips) {
            log.error("No more wav files can be loaded (" + maxClips + ")");
            return false;
        }

        try {
            Sound clip = new Sound(fileurl);
            clipMap.put(name, clip);
        } catch (Throwable e) {
            log.error("Failed to load wav file", e);
            return false;
        }

        return true;
    }

    /**
     * Playback
     *
     * @param name Registered name
     */
    public void play(String name) {
        // NameGet the clip corresponding to the
        Sound clip = clipMap.get(name);

        if (clip != null) {
            clip.play();
        }
    }

    /**
     * Stop
     *
     * @param name Registered name
     */
    public void stop(String name) {
        Sound clip = clipMap.get(name);

        if (clip != null) {
            clip.stop();
        }
    }
}
