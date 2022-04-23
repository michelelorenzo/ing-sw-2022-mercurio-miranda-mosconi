package it.polimi.ingsw.model;

import java.util.*;

public class Player {
    private final char[] nickName;
    private int wallet;
    private final Deck wizardDeck;
    private final int playerNumber;
    private final SchoolBoard schoolBoard;
    private AssistantCard lastAssistantCard;

    public Player(){
        this.nickName = null;
        this.wallet = 0;
        this.wizardDeck = null;
        this.playerNumber = 0;
        this.schoolBoard = null;
        this.lastAssistantCard = null;
    }

    public Player(char[] nickName, Deck wizard, int playerNumber, SchoolBoard schoolBoard) {
        this.nickName = nickName;
        this.wallet = 0;
        this.wizardDeck = wizard;
        this.playerNumber = playerNumber;
        this.schoolBoard = schoolBoard;
        this.lastAssistantCard = null;
    }

    public SchoolBoard getSchoolBoard(){
        return schoolBoard;
    }

    public char[] getNickName() {
        return nickName;
    }

    public int getWallet() {
        return wallet;
    }

    public Deck getDeck() {
        return wizardDeck;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setWallet(int wallet) {
        this.wallet = wallet;
    }

    public void collectCoin(PawnColor color){
        if(schoolBoard.checkForCoin(color))
            setWallet(getWallet()+1);
    }

    public void useAssistantCard(AssistantCard card)
    {
        for(AssistantCard chosenCard:this.wizardDeck.getCards())
        {
            if(chosenCard.equals(card) && !chosenCard.isConsumed())
            {
                chosenCard.setConsumed(true);
                this.lastAssistantCard = chosenCard;
            }
        }
    }

    public AssistantCard getLastAssistantCard() {
        return lastAssistantCard;
    }

    public int moveMotherNature(AssistantCard card){
        System.out.println("choose the movement, max "+ card.getMotherMovement());
        Scanner scanner = new Scanner(System.in);
        int a = scanner.nextInt();
        scanner.close();
        return a;
    }

//move a specific student from the entrance to the hall
    public void moveFromEntranceToHall(PawnColor color){
        if(schoolBoard.getStudentEntrance().get(color)<=0)
            System.out.println(color + "not present in Entrance");
        else {
            schoolBoard.removeStudentEntrance(color);
            schoolBoard.addStudentHall(color);
        }
    }


 //remove all the student from a specific cloud and put them in the entrance
    public void moveFromCloudToEntrance(CloudTile cloud) {

        for (PawnColor color : PawnColor.values()) {
            schoolBoard.addStudentEntrance(color, cloud.getStudents().get(color));
            cloud.reset(color);

        }
    }

    //move a specific student color from the Entrance to a specific island
    public void moveFromEntranceToIsland(Island island, PawnColor color){
        if(schoolBoard.getStudentEntrance().get(color)<=0)
            System.out.println(color + "not present in Entrance");
        else {
            schoolBoard.removeStudentEntrance(color);
            island.addStudent(color);
        }
    }

}
