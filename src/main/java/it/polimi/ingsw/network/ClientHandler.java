package it.polimi.ingsw.network;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.virtualview.VirtualView;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A class that represents the client inside the server.
 */
public class ClientHandler implements Runnable, ModelListener
{
    private VirtualView virtualView;
    private Socket client;
    private PrintWriter out;
    private BufferedReader in;
    private static Map<String, ArrayList<String>> networkMap = new HashMap<>(); //mappa di gameid e lista di player
    private static Map<String, Game> gameMap = new HashMap<>(); //mappa di gameid e game

    /**
     * Initializes a new handler using a specific socket connected to
     * a client.
     * @param client The socket connection to the client.
     */
    ClientHandler(Socket client)
    {
        this.client = client;
    }
    /**
     * Connects to the client and runs the event loop.
     */
    @Override
    public void run(){
        try {
            out = new PrintWriter(this.client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
        } catch (IOException e) {
            System.out.println("could not open connection to " + client.getInetAddress());
            return;
        }

        System.out.println("Connected to " + client.getInetAddress());

        try {
            Pinger pinger = new Pinger(out, client, "Server to "+ client.getInetAddress());
            Thread thread = new Thread(pinger, "serverPing_" + client.getInetAddress());
            thread.start();
            handleClientConnection(pinger);
        } catch (IOException e) {
            System.out.println("client " + client.getInetAddress() + " connection dropped");
        }

        try {
            client.close();
        } catch (IOException e) {
            System.out.println("Error with the socket closing");
        }
    }

    /**
     * An event loop that receives messages from the client and processes
     * them in the order they are received.
     * @throws IOException If a communication error occurs.
     */
    private void handleClientConnection(Pinger pinger) throws IOException
    {
        virtualView = new VirtualView();
        virtualView.setClientHandler(this);
        System.out.println("sending ack");
        out.println("ACK");

        try {
            while (true) {
                System.out.println("Ora leggo da user: " + virtualView.getUsername());
                String input = in.readLine();
                System.out.println("Appena letto da user: "+ virtualView.getUsername()+"il msg" + input);
                if(!input.equals("ping") && !input.equals("MODEL_UPDATED")){
                    String processedInput = virtualView.read(input);
                    if(processedInput!=null){
                        System.out.println("sending... " + processedInput);
                        System.out.println("ora scrivo a user:"+virtualView.getUsername()+"il msg"+processedInput);
                        out.println(processedInput);
                    }
                }
            }
        } catch (SocketTimeoutException e) {
            System.err.println("Client no more reachable ");
        } catch(IOException ex)
        {
            //
        }
        finally
        {

            try
            {
                pinger.stop();
                in.close();
                out.close();
                this.client.close();
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
            System.out.println("[" + client.getInetAddress() + "] " + ">> Connessione terminata <<");
        }
    }
    public void tellToWait() {
        Message msg_out = new Message();
        msg_out.setType(MessageType.WAIT);
        System.out.println("sending... " + msg_out.toSend() + "to" + this.virtualView.getUsername());

        out.println(msg_out.toSend());
    }

    public void tellToPlay() {
        Message msg_out = new Message();
        msg_out.setType(MessageType.IS_YOUR_TURN);
        System.out.println("sending... " + msg_out.toSend() + "to" + this.virtualView.getUsername());

        out.println(msg_out.toSend());
    }

    public void tellAPlayerJoined(Integer playersToGo) {
        Message msg_out = new Message();
        msg_out.setType(MessageType.LOBBY_UPDATED);
        System.out.println("sending... " + msg_out.toSend() + "to" + this.virtualView.getUsername());

        out.println(msg_out.toSend());
    }


    @Override
    public void updateAvailableWizards(ArrayList<WizardType> wizards) {
        Message msg_out = new Message();
        ArrayList<String> payloads = new ArrayList<>();
        msg_out.setType(MessageType.AVAILABLE_WIZARDS);
        for(WizardType w : wizards){
            payloads.add(String.valueOf(w));
        }
        msg_out.fill(payloads);
        out.println(msg_out.toSend());
    }
    @Override
    public void updateAvailableTowerColors(ArrayList<TowerColor> towerColors){
        Message msg_out = new Message();
        ArrayList<String> payloads = new ArrayList<>();
        msg_out.setType(MessageType.AVAILABLE_TOWER_COLORS);
        for(TowerColor tc : towerColors){
            payloads.add(String.valueOf(tc));
        }
        msg_out.fill(payloads);
        out.println(msg_out.toSend());
    }
    @Override
    public void updateLastAssistantCard(Player player) {
        Message msg_out = new Message();
        ArrayList<String> payloads = new ArrayList<>();
        msg_out.setType(MessageType.UPDATE_ASSISTANT_CARD);
        payloads.add(player.getNickName());
        payloads.add(String.valueOf(player.getLastAssistantCard().getValue()));
        msg_out.fill(payloads);
        out.println(msg_out.toSend());
    }
    @Override
    public void updateIsland(Island island) {
        Message msg_out=new Message();
        ArrayList<String> payloads = new ArrayList<>();
        msg_out.setType(MessageType.UPDATE_ISLAND);
        payloads.add(String.valueOf(island.getIslandID()));
        for(PawnColor c: PawnColor.values()){
            payloads.add(String.valueOf(c));
            payloads.add(String.valueOf(island.getIslandStudents().get(c)));
        }
        msg_out.fill(payloads);
        out.println(msg_out.toSend());
    }
    @Override
    public void updateSchoolBoardEntrance(Player player) {
        System.out.println("Sto inviando la entrance del player :" + player.getNickName() + "al client "+ virtualView.getUsername());
        System.out.println(player.getSchoolBoard().getStudentEntrance());
        Message msg_out=new Message();
        ArrayList<String> payloads = new ArrayList<>();
        msg_out.setType(MessageType.UPDATE_SCHOOL_BOARD_ENTRANCE);
        payloads.add(player.getNickName());
        for(PawnColor c : PawnColor.values()){
            payloads.add(String.valueOf(c));
            payloads.add(String.valueOf(player.getSchoolBoard().getStudentEntrance().get(c)));
        }
        msg_out.fill(payloads);
        out.println(msg_out.toSend());
    }
    @Override
    public void updateSchoolBoardHall(Player player) {
        Message msg_out=new Message();
        ArrayList<String> payloads = new ArrayList<>();
        msg_out.setType(MessageType.UPDATE_SCHOOL_BOARD_HALL);
        payloads.add(player.getNickName());
        for(PawnColor c : PawnColor.values()){
            payloads.add(String.valueOf(c));
            payloads.add(String.valueOf(player.getSchoolBoard().getStudentHall().get(c)));
        }
        msg_out.fill(payloads);
        out.println(msg_out.toSend());
    }
    @Override
    public void updateProfessorTables(ArrayList<Player> players) {
        Message msg_out=new Message();
        ArrayList<String> payloads = new ArrayList<>();
        msg_out.setType(MessageType.UPDATE_PROFESSORS);
        for(Player p : players){
            payloads.add(p.getNickName());
            for(PawnColor c : PawnColor.values()){
                payloads.add(String.valueOf(c));
                payloads.add(String.valueOf(p.getSchoolBoard().getProfessorTable().get(c)));
            }
        }
        msg_out.fill(payloads);
        out.println(msg_out.toSend());
    }

    @Override
    public void updatePlayers(ArrayList<Player> players) {

    }

    @Override
    public void modelCreated() {
        Message msg_out = new Message();
        msg_out.setType(MessageType.MODEL_CREATED);
        out.println(msg_out.toSend());
    }
    @Override
    public void updateIslandList(ArrayList<Island> islandList) {
        Message msg_out=new Message();
        ArrayList<String> payloads = new ArrayList<>();
        msg_out.setType(MessageType.UPDATE_ISLAND_LIST);
        for(Island i : islandList){
            payloads.add(String.valueOf(i.getIslandID()));
            payloads.add(String.valueOf(i.isMotherNature()));
            payloads.add(String.valueOf(i.isNoEntryTile()));
            payloads.add(String.valueOf(i.getTowerColor()));
            payloads.add(String.valueOf(i.getTowersNumber()));
            for(PawnColor color : PawnColor.values()){
                payloads.add(String.valueOf(color));
                payloads.add(String.valueOf(i.getIslandStudents().get(color)));
            }
        }
        msg_out.fill(payloads);
        out.println(msg_out.toSend());
    }
    @Override
    public void updateCTs(ArrayList<CloudTile> cts) {
        Message msg_out=new Message();
        ArrayList<String> payloads = new ArrayList<>();
        msg_out.setType(MessageType.UPDATE_CLOUDTILES);
        for(CloudTile ct : cts) {
            payloads.add(String.valueOf(ct.getCloudID()));
            for (PawnColor color : PawnColor.values()) {
                payloads.add(String.valueOf(color));
                payloads.add(String.valueOf(ct.getStudents().get(color)));
            }
        }
        msg_out.fill(payloads);
        out.println(msg_out.toSend());
    }

    @Override
    public void updateCardStudents(CharacterCard cc) {
        System.out.println("sending init chosenCharacter cards + their attributes (if they have any)");
        Message msg_out = new Message();
        ArrayList<String> payloads = new ArrayList<>();
        msg_out.setType(MessageType.UPDATE_CARD_STUDENTS);
        payloads.add(String.valueOf(cc.getID()));
        if(cc.getCardBehavior() instanceof CharacterCard1) {
            for (PawnColor color : PawnColor.values()) {
                payloads.add(String.valueOf(color));
                payloads.add(String.valueOf(((CharacterCard1) cc.getCardBehavior()).getStudents().get(color)));
            }
        }
        if(cc.getCardBehavior() instanceof CharacterCard7){
            for (PawnColor color : PawnColor.values()) {
                payloads.add(String.valueOf(color));
                payloads.add(String.valueOf(((CharacterCard7) cc.getCardBehavior()).getStudents().get(color)));
            }
        }
        if(cc.getCardBehavior() instanceof CharacterCard11){
            for (PawnColor color : PawnColor.values()) {
                payloads.add(String.valueOf(color));
                payloads.add(String.valueOf(((CharacterCard11) cc.getCardBehavior()).getStudents().get(color)));
            }
        }
        msg_out.fill(payloads);
        out.println(msg_out.toSend());
    }
    @Override
    public void updateBonus2InfluencePoints(Player player) {
        /*System.out.println("sending bonus 2 influence points " + player.getNickName());
        Message msg_out = new Message();
        ArrayList<String > payloads = new ArrayList<>();
        payloads.add(player.getNickName());
        payloads.add(String.valueOf(player.getSchoolBoard().isBonus2influencepoints()));
        msg_out.setType(MessageType.UPDATE_BONUS_2_INFL_POINTS);
        msg_out.fill(payloads);
        out.println(msg_out.toSend());*/
    }
    @Override
    public void updateMaxShift(Player player) {
        System.out.println("sending update Max Shift for player " + player.getNickName());
        Message msg_out = new Message();
        ArrayList<String> payloads = new ArrayList<>();
        payloads.add(player.getNickName());
        msg_out.setType(MessageType.UPDATE_MAX_SHIFT);
        msg_out.fill(payloads);
        out.println(msg_out.toSend());
    }
    @Override
    public void updateKeptOut(PawnColor keptOutColor) {
        /*System.out.println("sending kept out color: " + keptOutColor);
        Message msg_out = new Message();
        ArrayList<String > payloads = new ArrayList<>();
        payloads.add(String.valueOf(keptOutColor));
        msg_out.setType(MessageType.UPDATE_KEPT_OUT_COLOR);
        msg_out.fill(payloads);
        out.println(msg_out.toSend());*/
    }
    @Override
    public void updateSetupPlayers(ArrayList<Player> players) {
        System.out.println("invio update players a "+ virtualView.getUsername() + players);
        Message msg_out=new Message();
        ArrayList<String> payloads = new ArrayList<>();
        msg_out.setType(MessageType.SETUP_PLAYERS);
        for(Player p : players){
            payloads.add(p.getNickName());
            payloads.add(String.valueOf(p.getPlayerNumber()));
            payloads.add(String.valueOf(p.getDeck().getWizard()));
            payloads.add(String.valueOf(p.getSchoolBoard().getTowersColor()));
            //all'inizio si manda questo messaggio - successivamente si manda un updatePlayer singolo
            //quando viene modificata la schoolbaord o il wallet (il wallet viene mandato anche all'inizio se e solo se expertMode)
        }
        msg_out.fill(payloads);
        out.println(msg_out.toSend());
    }

    public void tellWhoWon(Player winner) {
        System.out.println("sending : Player "+winner.getNickName()+" won the game.");
        Message msg_out = new Message();
        String payloads = "";
        msg_out.setType(MessageType.GAME_ENDED);
        payloads= winner.getNickName();
        msg_out.fill(payloads);
        out.println(msg_out.toSend());
    }

    @Override
    public void updateWallet(Player p){
        System.out.println("sending player "+p.getNickName()+"'s wallet update");
        Message msg_out = new Message();
        ArrayList<String> payloads = new ArrayList<>();
        msg_out.setType(MessageType.UPDATE_WALLET);
        payloads.add(p.getNickName());
        payloads.add(String.valueOf(p.getWallet()));
        msg_out.fill(payloads);
        out.println(msg_out.toSend());
    }

    @Override
    public void initializedCharacterCards(ArrayList<CharacterCard> chosenCharacterCards) {
        System.out.println("sending init chosenCharacter cards + their attributes (if they have any)");
        Message msg_out = new Message();
        ArrayList<String> payloads = new ArrayList<>();
        msg_out.setType(MessageType.INIT_CHARACTER_CARDS);
        for(CharacterCard cc : chosenCharacterCards){
            payloads.add(String.valueOf(cc.getID()));
            payloads.add(String.valueOf(cc.getPrice()));

            if(cc.getCardBehavior() instanceof CharacterCard1) {
                for (PawnColor color : PawnColor.values()) {
                    payloads.add(String.valueOf(color));
                    payloads.add(String.valueOf(((CharacterCard1) cc.getCardBehavior()).getStudents().get(color)));
                }
            }
            else if(cc.getCardBehavior() instanceof CharacterCard7){
                for(PawnColor color : PawnColor.values()){
                    payloads.add(String.valueOf(color));
                    payloads.add(String.valueOf(((CharacterCard7) cc.getCardBehavior()).getStudents().get(color)));
                }
            }
            else if(cc.getCardBehavior() instanceof CharacterCard11){
                for(PawnColor color : PawnColor.values()){
                    payloads.add(String.valueOf(color));
                    payloads.add(String.valueOf(((CharacterCard11) cc.getCardBehavior()).getStudents().get(color)));
                }
            }
        }
        msg_out.fill(payloads);
        out.println(msg_out.toSend());
    }

    @Override
    public void updatePrice(CharacterCard currEffect) {
        System.out.println("sendind price increased card "+ currEffect.getID());
        Message msg_out = new Message();
        ArrayList<String> payloads = new ArrayList<>();
        msg_out.setType(MessageType.PRICE_INCREASE);
        payloads.add(String.valueOf(currEffect.getID()));
        msg_out.fill(payloads);
        out.println(msg_out.toSend());
    }

    @Override
    public void updateTowersDoCount(boolean towersDoCount) {
        /*System.out.println("sending TowersDoCount" + towersDoCount);
        Message msg_out = new Message();
        String payload = "";
        payload = String.valueOf(towersDoCount);
        msg_out.setType(MessageType.UPDATE_TOWERS_DO_COUNT);
        msg_out.fill(payload);
        out.println(msg_out.toSend());*/
    }

    public void tellAPlayerActivatedACard(String username, Integer id) {
        System.out.println("sending a msg to tell that player " + username +" used the card n. " + id);
        Message msg_out = new Message();
        ArrayList<String> payloads = new ArrayList<>();
        msg_out.setType(MessageType.SOMEONE_ACTIVATED_AN_EFFECT);
        payloads.add(username);
        payloads.add(String.valueOf(id));
        msg_out.fill(payloads);
        out.println(msg_out.toSend());
    }
    public void tellTheEffectEnded(){
        System.out.println("sending a msg to tell the effect of the active card has ended.");
        Message msg_out = new Message();
        msg_out.setType(MessageType.EFFECT_ENDED);
        out.println(msg_out.toSend());
    }
}
