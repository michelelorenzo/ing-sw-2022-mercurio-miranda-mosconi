package it.polimi.ingsw.view;

import it.polimi.ingsw.model.*;

import java.util.*;

public class View {
    private String username;
    private GameMode gamemode;
    private Integer playerNumber;
    private Player player;
    private ArrayList<SchoolBoard> schoolBoards;
    private ArrayList<CloudTile> clouds;
    private IslandManager islandManager;
    private Set<CharacterCard> characterCards;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GameMode getGamemode() {
        return gamemode;
    }

    public void setGamemode(GameMode gamemode) {
        this.gamemode = gamemode;
    }

    public Integer getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(Integer playerNumber) {
        this.playerNumber = playerNumber;
    }

    public Set<CharacterCard> getCharacterCards() {
        return characterCards;
    }

    public void setCharacterCards(Set<CharacterCard> characterCards) {
        this.characterCards = characterCards;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public ArrayList<SchoolBoard> getSchoolBoards() {
        return schoolBoards;
    }

    public void setSchoolBoards(ArrayList<SchoolBoard> schoolBoards) {
        this.schoolBoards = schoolBoards;
    }

    public ArrayList<CloudTile> getClouds() {
        return clouds;
    }

    public void setClouds(ArrayList<CloudTile> clouds) {
        this.clouds = clouds;
    }

    public IslandManager getIslandManager() {
        return islandManager;
    }

    public void setIslandManager(IslandManager islandManager) {
        this.islandManager = islandManager;
    }

    public void login(){
    }

    public void settings(){}

    public void chooseAssistantCard(){}

    public void waiting(){}

    public void mainboard(){}

    public void lobby(){}

    public void chooseWizard(){}

    public void chooseMNmovement(){}

}
