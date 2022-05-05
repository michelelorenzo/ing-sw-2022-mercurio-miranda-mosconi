package it.polimi.ingsw.model;
import java.util.*;

public class Game {


    //attributes
    private ArrayList<Player> players;
    private int numberOfPlayers;
    private int gameID;
    private GameMode gameMode;
    private ArrayList<CloudTile> cloudTiles;
    private ArrayList<SchoolBoard> schoolBoards;
    private IslandManager islandManager;
    private Map<PawnColor, Integer> bag;
    private ArrayList<CharacterCard> allCharacterCards; /*dare dimensione quando si istanzia
     "new ArrayList<charcards>(..dimensione..)"*/
    private ArrayList<Integer> playerOrder;
    private ArrayList<WizardType> wizards;
    private ArrayList<CharacterCard> chosenCharacterCards;
    private int bank;
//methods @Setter @Getter
    public void setGameMode(GameMode gamemode){
        this.gameMode=gamemode;
    }
    public GameMode getGameMode() {
        return gameMode;
    }
    public void setGameID(int gameID){
        this.gameID=gameID;
    }
    public int getGameID() {
        return gameID;
    }
    public ArrayList<CloudTile> getCloudTiles() {
        return cloudTiles;
    }
    public void setCloudTiles(ArrayList<CloudTile> cloudTiles) {
        this.cloudTiles = cloudTiles;
    }
    public ArrayList<SchoolBoard> getSchoolBoards() {
        return schoolBoards;
    }
    public void setSchoolBoards(ArrayList<SchoolBoard> schoolBoards) {
        this.schoolBoards = schoolBoards;
    }
    public void setIslandManager(IslandManager islandManager) {
        this.islandManager = islandManager;
    }
    public IslandManager getIslandManager() {
        return islandManager;
    }
    public void setBag(Map<PawnColor,Integer> bag){
        this.bag=bag;
    }
    public Map<PawnColor, Integer> getBag() {
        return bag;
    }
    public void setBank(int coins){
        this.bank=coins;
    }
    public int getBank(){
        return bank;
    }
    public ArrayList<Integer> getPlayerOrder() {
        return playerOrder;
    }
    public void setPlayerOrder(ArrayList<Integer> playerOrder) {
        this.playerOrder = playerOrder;
    }
    public void setWizards(ArrayList<WizardType> wizards) {
        this.wizards = wizards;
    }
    public ArrayList<WizardType> getWizards() {
        return wizards;
    }
    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }
    public ArrayList<Player> getPlayers() {
        return players;
    }
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }
    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }
    public void setAllCharacterCards(ArrayList<CharacterCard> allCharacterCards) {
        this.allCharacterCards = allCharacterCards;
    }
    public void setChosenCharacterCards(ArrayList<CharacterCard> chosenCharacterCards) {
        this.chosenCharacterCards = chosenCharacterCards;
    }
    public ArrayList<CharacterCard> getChosenCharacterCards() {
        return chosenCharacterCards;
    }
//other methods
    public void moveFromBagToCloud(CloudTile cloud){
        PawnColor rdColor=PawnColor.randomColor();
        if(this.bag.get(rdColor)==0){
            while(this.bag.get(rdColor)==0){
                rdColor=PawnColor.randomColor();
            }
        }//controllo che la bag abbia disponibilità di studenti del colore random
        this.bag.replace(rdColor, this.bag.get(rdColor)-1);
        cloud.getStudents().replace(rdColor, cloud.getStudents().get(rdColor)+1);
    }
    public void updateProfessor(PawnColor color){
        int currentMax=0;
        /*
        si potrebbe tenere in game una mappa che tenga traccia del numero di studenti che ha il player
        che possiede già il prof in modo da effettuare la update solo quando serve piuttosto che farlo
        per ogni studente spostato
        */
        for (SchoolBoard s : schoolBoards){
            if(s.getStudentHall().get(color)>currentMax){
                currentMax=s.getStudentHall().get(color);
                for(SchoolBoard f : schoolBoards){
                    f.getProfessorTable().replace(color,false);
                }
                s.getProfessorTable().replace(color,true);
            }
        }
    }
    public ArrayList<CharacterCard> shuffleCharacterCards() {
        Collections.shuffle(allCharacterCards);
        return allCharacterCards;
    }
    public void setupGame()/*gamemode e noOfPlayers si prendono dal controller?*/{
        this.bag = new HashMap<>();
        fillBag();
        /* generatore randomico ID del game (?)
        Random rn = new Random();
        int n = 100000 - 0 + 1;
        int j = rn.nextInt() % n;
        this.setGameID(j); */
        ArrayList<Island> islands = generateIslands();
        IslandManager islandManager= new IslandManager(islands);
        this.setIslandManager(islandManager);
        //ArrayList<Player> players = generatePlayers(this.numberOfPlayers);
        //this.setPlayers(players);
            //todo inizializzare i players - probabilmente verrà fatto dal controller che darà in input al setupGame l'array di players da settare
        ArrayList<SchoolBoard> schoolBoards = generateSchoolBoards();
        initSchoolBoards();
        this.setSchoolBoards(schoolBoards);
        Player firstPlayer = SelectFirstPlayer();
        ArrayList<Integer> tmpOrder = new ArrayList<>(this.numberOfPlayers);
        for(int i=0;i<this.numberOfPlayers;i++){
            tmpOrder.add(firstPlayer.getPlayerNumber()+i % this.numberOfPlayers);
        }
        this.setPlayerOrder(tmpOrder);
        /*ArrayList<WizardType> wizards = new ArrayList<>(numberOfPlayers);{
            for(Player p : players) {
                wizards.add(p.getDeck().getWizard());
            }
        }*/ //todo da mettere nel GenerateControllerState
       // this.setWizards(wizards);//todo forse il mago viene scelto nel setup del controller ?
        ArrayList<CloudTile> cloudTiles = generateCloudTiles();
        //fillCloudTiles(); vengono riempite nel SETUPSTATE dopo il playerOrder
        if(gameMode.equals(GameMode.expert)){
            initAllCharacterCards();
            ArrayList<CharacterCard> chosenCharacterCards = initChosenCharacterCards();
            this.setChosenCharacterCards(chosenCharacterCards);
            this.setBank(20);
        }
    }
    public void updatePlayerOrder(){
        ArrayList<Player> clonePlayerArray = this.players;
        clonePlayerArray.sort(new Comparator<Player>() {
            public int compare(Player i1, Player i2) {
                return i1.getLastAssistantCard().getValue() - i2.getLastAssistantCard().getValue();
            }
        });
        ArrayList<Integer> tmpOrder = new ArrayList<>(numberOfPlayers);
        for(int i=0;i<numberOfPlayers;i++){
            tmpOrder.add(clonePlayerArray.get(i).getPlayerNumber());
        }
        this.setPlayerOrder(tmpOrder);
        //dovrebbe andare ma non sono sicurissimo
    }
//setupGame extractions
    private ArrayList<Island> generateIslands() {
        ArrayList<Island> islands = new ArrayList<>(12);
        {
            {
                Map<PawnColor,Integer> islandInitializationBag = new HashMap<>();
                for(PawnColor col : PawnColor.values()){
                    islandInitializationBag.replace(col,2);
                    this.bag.replace(col,this.bag.get(col)-2);
                }
                for(int i=0;i<12;i++){
                    islands.add(new Island(null,null,0,false,false));
                    if(i!=0&&i!=5){
                        PawnColor rdColor=PawnColor.randomColor();
                        islandInitializationBag.replace(rdColor, islandInitializationBag.get(rdColor)-1);
                        islands.get(i).addStudent(rdColor);
                    }//initializes islands with students
                    else if(i==0){
                        islands.get(i).setMotherNature(true);
                    }//initializes mothernature
                }
            }
        }
        return islands;
    }
    private ArrayList<SchoolBoard> generateSchoolBoards() {
        ArrayList<SchoolBoard> schoolBoards = new ArrayList<>(numberOfPlayers);
        {
            for(int i = 0; i< numberOfPlayers; i++){
                schoolBoards.add(new SchoolBoard());
            }
        }
        return schoolBoards;
    }
    public void fillCloudTiles() {
        for(CloudTile c : this.cloudTiles){
            for(int i=0;i<3;i++){
                moveFromBagToCloud(c);
            }
            if(this.numberOfPlayers == 3){
                moveFromBagToCloud(c);
            }
        }
    }
    private ArrayList<CloudTile> generateCloudTiles() {
        ArrayList<CloudTile> cloudTiles = new ArrayList<>(numberOfPlayers);
        {
            for(int i = 0; i< numberOfPlayers; i++){
                cloudTiles.add(new CloudTile());
            }
        }
        return cloudTiles;
    }
    private void fillBag() {
        for(PawnColor color : PawnColor.values()){
            this.initBag(color,26);
        }//setta la bag ai valori di default
    }
    /*
    private ArrayList<Player> generatePlayers(int numberOfPlayers) {
        ArrayList<Player> players = new ArrayList<>(numberOfPlayers);
        {
            for(int i = 0; i< numberOfPlayers; i++){
                players.add(new Player());
            }
        }
        return players;
    } da gestire nel controller*/
    private void initSchoolBoards() {
        /* for(SchoolBoard s: schoolBoards){
            if(this.numberOfPlayers==2){
                s.setTowersNumber(8);
            }
            if(this.numberOfPlayers==3){
                s.setTowersNumber(6);
            }
            if(this.numberOfPlayers==4){
                s.setTowersNumber(4);
            }//qui va modificato -- todo valutare di aggiungere un attributo che indichi squadra al player e ricordarsi di cambiare le torri qunado si gioca in 4
            for(int i=0;i<7;i++){
                PawnColor rdColor=PawnColor.randomColor();
                this.bag.replace(rdColor, this.bag.get(rdColor)-1);
                s.addStudentEntrance(rdColor);
            }
            //manca da settare il colore delle torri

        }*/
    }
    private Player SelectFirstPlayer(){
        int min_val = 0;
        int max_val = players.size();
        Random rd = new Random();
        int randNum = min_val+rd.nextInt((max_val - min_val) + 1);
        return players.get(randNum);
    }//genera casualmente il primo giocatore che lancia la carta assistente al primo turno
    private void initBag(PawnColor color,int n){
        this.bag.replace(color, n);
    }
//gestione init character cards
    private ArrayList<CharacterCard> initChosenCharacterCards() {
        return (ArrayList<CharacterCard>) getAllCharacterCards().subList(0,2);
    }
    private ArrayList<CharacterCard> getAllCharacterCards() {
        return shuffleCharacterCards();
    } /* restituisce tutte le charcards in disordine;
     quando si chiama la init si pescano le prime 3 in disordine;
     dopo la init si accede alle chosen tramite la get*/
    private void initAllCharacterCards(){
        this.allCharacterCards.add(new CharacterCard (1, new CharacterCard1()));
        this.allCharacterCards.add(new CharacterCard (2, new CharacterCard2()));
        this.allCharacterCards.add(new CharacterCard (3, new CharacterCard3()));
        this.allCharacterCards.add(new CharacterCard (1, new CharacterCard4()));
        this.allCharacterCards.add(new CharacterCard (2, new CharacterCard5()));
        this.allCharacterCards.add(new CharacterCard (3, new CharacterCard6()));
        this.allCharacterCards.add(new CharacterCard (1, new CharacterCard7()));
        this.allCharacterCards.add(new CharacterCard (2, new CharacterCard8()));
        this.allCharacterCards.add(new CharacterCard (3, new CharacterCard9()));
        this.allCharacterCards.add(new CharacterCard (1, new CharacterCard10()));
        this.allCharacterCards.add(new CharacterCard (2, new CharacterCard11()));
        this.allCharacterCards.add(new CharacterCard (3, new CharacterCard12()));
    }
}
