package it.polimi.ingsw.model;

import java.util.HashMap;
import java.util.Map;

public class Island {
    private Map<PawnColor, Integer> islandStudents;
    private TowerColor towerColor;
    private int towerNumber;
    private boolean noEntryTile;
    private boolean motherNature;

    public Island(Map<PawnColor, Integer> islandStudents, TowerColor towerColor, int towerNumber, boolean noEntryTile, boolean motherNature) {
        this.islandStudents = islandStudents;
        this.towerColor = towerColor;
        this.towerNumber = towerNumber;
        this.noEntryTile = noEntryTile;
        this.motherNature = motherNature;
    }

    public void setTowerColor(TowerColor color){
        this.towerColor = color;
    }
    public TowerColor getTowerColor(){
        return this.towerColor;
    }

    public void setTowersNumber(int number){
        this.towerNumber = number;
    }
    public int getTowersNumber(){
        return this.towerNumber;
    }

    //We don't set/get the whole map but only the integer related to the key color.
    //The map should be implemented as an HashMap, 'cause Map is an interface.
    public Map<PawnColor, Integer> getIslandStudents() {
        return this.islandStudents;
    }

    //is this really useful?
    private void setIslandStudents(Map<PawnColor, Integer> islandStudents) {
        for (Map.Entry<PawnColor, Integer> entry : this.islandStudents.entrySet()) {
            // using put method to copy one Map to Other
            islandStudents.put(entry.getKey(),
                    entry.getValue());
        }
    }

    public int getStudentNumber(PawnColor color){
        return this.islandStudents.get(color);
    }

    public void addStudent(PawnColor color){
        this.islandStudents.put(color, getStudentNumber(color)+1);
    }

    public void setMotherNature(boolean isPresent){
        this.motherNature = isPresent;
    }
    public boolean isMotherNature(){
        return motherNature;
    }

    public void setNoEntryTile(boolean noEntryTile) {
        this.noEntryTile = noEntryTile;
    }
    public boolean isNoEntryTile() {
        return noEntryTile;
    }

    //We should modify it if there's a 4 player game, as this version does not support it.
    public int calculatePlayerInfluence(SchoolBoard schoolboard){
        int totStudents = 0;
        for (PawnColor color : schoolboard.getProfessorTable().keySet()){
               if (schoolboard.getProfessorTable().get(color))
                   totStudents += getStudentNumber(color);
        }
        if (schoolboard.getTowersColor().equals(this.towerColor))
            totStudents += getTowersNumber();
        return totStudents;
    }

    public void assignInfluence(SchoolBoard[] schoolBoardsArray){
        int maxInfluence = 0;
        TowerColor influence = null;

        for (SchoolBoard schoolBoard : schoolBoardsArray) {
            if (schoolBoard.getTowersColor().equals(this.getTowerColor())){
                maxInfluence = calculatePlayerInfluence(schoolBoard);
                influence = schoolBoard.getTowersColor();
            }
        }

        for (SchoolBoard schoolBoard : schoolBoardsArray) {
            int currentInfluence = calculatePlayerInfluence(schoolBoard);
            if (currentInfluence > maxInfluence) {
                maxInfluence = currentInfluence;
                influence = schoolBoard.getTowersColor();
            }
        }
        this.towerColor = influence;
    }
}