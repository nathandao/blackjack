package blackjack.io;

import blackjack.engine.GameContext;
import blackjack.engine.InputManager;
import blackjack.models.Bet;
import blackjack.models.PlayerAction;
import blackjack.utils.UDPServer;

import java.util.*;

public class SensorInput implements InputManager  {
    UDPServer m_server;
    PlayerInputListener m_player1 = new PlayerInputListener(0); // Or store in a map?!
    PlayerInputListener m_player2 = new PlayerInputListener(1);
    PlayerBetListener m_betListener = new PlayerBetListener();

    public static final long TIMEOUT_READ_BETS_MS = 30000;
    public static final long TIMEOUT_READ_INPUT_MS = 30000;

    class PlayerInputListener extends SensorListener {
        PlayerInputListener(int playerId) {
            super("^((stay|hit)\\{" + playerId + "\\})");
        }
    }

    public void initialize(int port) throws Exception {
        m_server = new UDPServer();
        m_server.initialize(port);
        m_server.addListener(m_player1);
        m_server.addListener(m_player2);
        m_server.addListener(m_betListener);

        //DEBUG
        /*
        m_server.addListener(new UDPServer.PacketListener() {
            @Override
            public void packetArrived(String payload) {
                System.out.println("DEBUG: " + payload);
            }
        });*/

        m_server.startServer();
    }

    @Override
    public List<Bet> getBets() {
        //Read using RFID
        return m_betListener.readBets(TIMEOUT_READ_BETS_MS);
    }

    @Override
    public PlayerAction getInput(int playerId, GameContext gameState, Set<PlayerAction> options) {
        System.out.println("Starting to wait for sensor input");
        SensorListener listener = null;
        if (playerId == 0) {
            listener = m_player1;
        }
        else if (playerId == 1) {
            listener = m_player2;
        }
        if (listener == null)
            throw new RuntimeException("Player id invalid for SensorInput");

        String msg = listener.blockUntilMessage(TIMEOUT_READ_INPUT_MS);
        System.out.println("Message waited, result: " + msg);
        if (msg == null) {
            System.out.println("Timed out, no input.");
            return PlayerAction.Undecided;
        }
        else if (msg.startsWith("stay")) {
            return PlayerAction.Stay;
        }
        else if (msg.startsWith("hit")) {
            return PlayerAction.Hit;
        }
        else {
            return PlayerAction.QuitGame;
        }
    }

}
