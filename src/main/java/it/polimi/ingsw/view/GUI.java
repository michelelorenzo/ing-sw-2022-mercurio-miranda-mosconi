package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.*;

public class GUI extends View{
    private GuiStarter guiStarter;
    private Client client;
    //private boolean ready2go = false;
    private Player playerSchoolboard;
    private String winnerUsername;
    private NetworkHandler networkHandler;
    public String getWinnerUsername() {
        return winnerUsername;
    }

    public void setWinnerUsername(String winnerUsername) {
        this.winnerUsername = winnerUsername;
    }

    public Player getPlayerSchoolboard() {
        return playerSchoolboard;
    }

    public void setPlayerSchoolboard(Player playerSchoolboard) {
        this.playerSchoolboard = playerSchoolboard;
    }

    public NetworkHandler getNetworkHandler() {
        return networkHandler;
    }

    public void setNetworkHandler(NetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
    }


    public GuiStarter getGuiStarter() {
        return guiStarter;
    }
    public void setGuiStarter(GuiStarter guiStarter) {
        this.guiStarter = guiStarter;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void prepareMessage() {
        networkHandler.sendMessage();
    }

    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Shows the correct GUI scene according to the client's phase.
     */
    public void processScene(){
        switch (phase){
            case SETTINGS -> {
                guiStarter.switchToWizardsScene();
            }
            case PLANNING -> {
                guiStarter.switchToDeckScene();
                guiStarter.switchToMainBoard();
            }
            case CHOOSING_FIRST_MOVE, CHOOSING_SECOND_MOVE, CHOOSING_THIRD_MOVE, CHOOSING_FOURTH_MOVE -> {
                //System.out.println("Ora faccio partire la scelta");
                guiStarter.switchToMainBoard();
                guiStarter.choosePawnMove();
            }
            case CHOOSING_MN_SHIFT -> {
                guiStarter.switchToMainBoard();
                guiStarter.chooseMNmovement();
            }
            case CHOOSING_CT -> {
                guiStarter.switchToMainBoard();
                guiStarter.chooseCT();
            }
        }
    }

    /**
     * Prepares the message type to send to the server while requesting to create or join a match.
     */
    @Override
    public void login() {
        player = new Player();
        player.setNickName(username);
        setUsername(username);
        setIdGame(idGame);
        Message msg_out = new Message();

        if (messageType.equals(MessageType.JOIN_MATCH)){
            msg_out.setType(MessageType.JOIN_MATCH);
            setMessageType(MessageType.JOIN_MATCH);
        }
        else {
            msg_out.setType(MessageType.CREATE_MATCH);
            setMessageType(MessageType.CREATE_MATCH);
        }
    }

    /**
     * Stores in the view's attribute the settings chosen by the user.
     */
    @Override
    public void settings() {
        player.getSchoolBoard().setTowersColor(towerColor);
    }

    /**
     * Stores in the view's attributes the last used assistant card and the new maximum mother nature shift.
     */
    @Override
    public void chooseAssistantCard() {
        player.setMaxShift(chosenAssistantCard.getMotherMovement());
        chosenAssistantCard.setConsumed(true);
        player.setLastAssistantCard(chosenAssistantCard);
    }

    @Override
    public void choosePawnMove() {

    }

    @Override
    public void showTable() {

    }

    @Override
    public void chooseMNmovement() {

    }

    @Override
    public void chooseCT() {

    }

    @Override
    public void showUsedAssistantCards() {
    }
}
