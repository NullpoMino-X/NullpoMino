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
package mu.nu.nullpo.game.net;

import mu.nu.nullpo.game.play.GameManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Client(PlayerUse)
 */
public class NetPlayerClient extends NetBaseClient {
    /**
     * Log
     */
    static final Logger log = Logger.getLogger(NetPlayerClient.class);

    /**
     * PlayerInformation
     */
    private LinkedList<NetPlayerInfo> playerInfoList = new LinkedList<>();

    /**
     * Room Information
     */
    private LinkedList<NetRoomInfo> roomInfoList = new LinkedList<>();

    /**
     * OwnPlayerName
     */
    private String playerName;

    /**
     * OwnTeam name
     */
    private String playerTeam;

    /**
     * OwnPlayerIdentification number
     */
    private int playerUID;

    /**
     * ServerVersion
     */
    private float serverVersion = -1f;

    /**
     * Number of players
     */
    private int playerCount = -1;

    /**
     * Observercount
     */
    private int observerCount = -1;

    /**
     * Default constructor
     */
    private NetPlayerClient() {
        super();
    }

    /**
     * Constructor
     *
     * @param host Destination host
     */
    private NetPlayerClient(String host) {
        super(host);
    }

    /**
     * Constructor
     *
     * @param host Destination host
     * @param port Destination port number
     */
    private NetPlayerClient(String host, int port) {
        super(host, port);
    }

    /**
     * Constructor
     *
     * @param host Destination host
     * @param port Destination port number
     * @param name PlayerOfName
     */
    private NetPlayerClient(String host, int port, String name) {
        super();
        this.host = host;
        this.port = port;
        this.playerName = name;
        this.playerTeam = "";
    }

    /**
     * Constructor
     *
     * @param host Destination host
     * @param port Destination port number
     * @param name PlayerOfName
     * @param team BelongTeam name
     */
    public NetPlayerClient(String host, int port, String name, String team) {
        super();
        this.host = host;
        this.port = port;
        this.playerName = name;
        this.playerTeam = team;
    }

    /*
     * The various processing depending on the received message
     */
    @Override
    protected void processPacket(String fullMessage) throws IOException {
        String[] message = fullMessage.split("\t");    // Tab delimited

        // Connection completion
        if (message[0].equals("welcome")) {
            //welcome\t[VERSION]\t[PLAYERS]\t[OBSERVERS]\t[VERSION MINOR]\t[VERSION STRING]\t[PING INTERVAL]\t[DEV BUILD]
            playerCount = Integer.parseInt(message[2]);
            observerCount = Integer.parseInt(message[3]);

            long pingInterval = (message.length > 6) ? Long.parseLong(message[6]) : PING_INTERVAL;
            if (pingInterval != PING_INTERVAL) {
                startPingTask(pingInterval);
            }

            send("login\t" + GameManager.getVersionMajor() + "\t" + NetUtil.urlEncode(playerName) + "\t" + Locale.getDefault().getCountry() + "\t" +
                    NetUtil.urlEncode(playerTeam) + "\t" + GameManager.getVersionMinor() + "\t" + GameManager.isDevBuild() + "\n");
        }
        // PeoplecountUpdate
        if (message[0].equals("observerupdate")) {
            //observerupdate\t[PLAYERS]\t[OBSERVERS]
            playerCount = Integer.parseInt(message[1]);
            observerCount = Integer.parseInt(message[2]);
        }
        // Successful login
        if (message[0].equals("loginsuccess")) {
            //loginsuccess\t[NAME]\t[UID]
            playerName = NetUtil.urlDecode(message[1]);
            playerUID = Integer.parseInt(message[2]);
        }
        // PlayerList
        if (message[0].equals("playerlist")) {
            //playerlist\t[PLAYERS]\t[PLAYERDATA...]

            int numPlayers = Integer.parseInt(message[1]);

            for (int i = 0; i < numPlayers; i++) {
                NetPlayerInfo p = new NetPlayerInfo(message[2 + i]);
                playerInfoList.add(p);
            }
        }
        // PlayerInformation update/A newPlayer
        if (message[0].equals("playerupdate") || message[0].equals("playernew")) {
            //playerupdate\t[PLAYERDATA]

            NetPlayerInfo p = new NetPlayerInfo(message[1]);
            NetPlayerInfo p2 = getPlayerInfoByUID(p.uid);

            if (p2 == null) {
                playerInfoList.add(p);
            } else {
                int index = playerInfoList.indexOf(p2);
                playerInfoList.set(index, p);
            }
        }
        // PlayerCut
        if (message[0].equals("playerlogout")) {
            //playerlogout\t[PLAYERDATA]

            NetPlayerInfo p = new NetPlayerInfo(message[1]);
            NetPlayerInfo p2 = getPlayerInfoByUID(p.uid);

            if (p2 != null) {
                playerInfoList.remove(p2);
                p2.delete();
            }
        }
        // Room list
        if (message[0].equals("roomlist")) {
            //roomlist\t[ROOMS]\t[ROOMDATA...]

            int numRooms = Integer.parseInt(message[1]);

            for (int i = 0; i < numRooms; i++) {
                NetRoomInfo r = new NetRoomInfo(message[2 + i]);
                roomInfoList.add(r);
            }
        }
        // Room information update/New room appearance
        if (message[0].equals("roomupdate") || message[0].equals("roomcreate")) {
            //roomupdate\t[ROOMDATA]

            NetRoomInfo r = new NetRoomInfo(message[1]);
            NetRoomInfo r2 = getRoomInfo(r.roomID);

            if (r2 == null) {
                roomInfoList.add(r);
            } else {
                int index = roomInfoList.indexOf(r2);
                roomInfoList.set(index, r);
            }
        }
        // Annihilation Room
        if (message[0].equals("roomdelete")) {
            //roomdelete\t[ROOMDATA]

            NetRoomInfo r = new NetRoomInfo(message[1]);
            NetRoomInfo r2 = getRoomInfo(r.roomID);

            if (r2 != null) {
                roomInfoList.remove(r2);
                r2.delete();
            }
        }
        // Participation status change
        if (message[0].equals("changestatus")) {
            NetPlayerInfo p = getPlayerInfoByUID(Integer.parseInt(message[2]));

            if (p != null) {
                switch (message[1]) {
                    case "watchonly":
                        p.seatID = -1;
                        p.queueID = -1;
                        break;
                    case "joinqueue":
                        p.seatID = -1;
                        p.queueID = Integer.parseInt(message[4]);
                        break;
                    case "joinseat":
                        p.seatID = Integer.parseInt(message[4]);
                        p.queueID = -1;
                        break;
                }
            }
        }

        // ListenerCall
        super.processPacket(fullMessage);
    }

    /**
     * DesignatedIDReturns information room
     *
     * @param roomID RoomID
     * @return Room Information(Does not existnull)
     */
    public NetRoomInfo getRoomInfo(int roomID) {
        if (roomID < 0) return null;

        for (NetRoomInfo roomInfo : roomInfoList) {
            if (roomID == roomInfo.roomID) {
                return roomInfo;
            }
        }

        return null;
    }

    /**
     * SpecifiedNameOfPlayerGet the
     *
     * @param name Name
     * @return SpecifiedNameOfPlayerInformation(There were nonull)
     */
    public NetPlayerInfo getPlayerInfoByName(String name) {
        for (NetPlayerInfo pInfo : playerInfoList) {
            if ((pInfo != null) && (pInfo.strName == name)) {
                return pInfo;
            }
        }
        return null;
    }

    /**
     * SpecifiedIDOfPlayerGet the
     *
     * @param uid ID
     * @return SpecifiedIDOfPlayerInformation(There were nonull)
     */
    public NetPlayerInfo getPlayerInfoByUID(int uid) {
        for (NetPlayerInfo pInfo : playerInfoList) {
            if ((pInfo != null) && (pInfo.uid == uid)) {
                return pInfo;
            }
        }
        return null;
    }

    /**
     * @return PlayerList of information
     */
    public LinkedList<NetPlayerInfo> getPlayerInfoList() {
        return playerInfoList;
    }

    /**
     * @return Listing Information Room
     */
    public LinkedList<NetRoomInfo> getRoomInfoList() {
        return roomInfoList;
    }

    /**
     * @return Current PlayerName
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @return Current PlayerIdentification of number
     */
    public int getPlayerUID() {
        return playerUID;
    }

    /**
     * Get your own information
     *
     * @return Their own information
     */
    public NetPlayerInfo getYourPlayerInfo() {
        return getPlayerInfoByUID(playerUID);
    }

    /**
     * @return Current room ID
     */
    private int getCurrentRoomID() {
        try {
            return getYourPlayerInfo().roomID;
        } catch (NullPointerException e) {
        }
        return -1;
    }

    /**
     * @return Current room info
     */
    public NetRoomInfo getCurrentRoomInfo() {
        return getRoomInfo(getCurrentRoomID());
    }

    /**
     * @return ServerVersion
     */
    public float getServerVersion() {
        return serverVersion;
    }

    /**
     * @return Number of players
     */
    public int getPlayerCount() {
        return playerCount;
    }

    /**
     * @return Observercount
     */
    public int getObserverCount() {
        return observerCount;
    }
}
