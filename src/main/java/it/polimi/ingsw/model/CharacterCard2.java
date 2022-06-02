package it.polimi.ingsw.model;

import java.util.ArrayList;

public class CharacterCard2 extends CharacterCard{
/*
    Eff: durante il turno, prendi il controllo dei prof anche se nella tua sala hai lo stesso numero di studenti del giocatore che controlla i prof in quel momento
 */
    //essendo l'effetto limitato alla durata di un turno, bisogna salvare su una variabile temporanea lo stato corrente e ripristinarlo alla fine del turno
    private ArrayList<SchoolBoard> tmpSchoolBoards;
    private ArrayList<SchoolBoard> currentSchoolBoards;

    public CharacterCard2(int id, int price) {
        super(id, price);
    }

    //private ArrayList<SchoolBoard> currentSchoolBoards;
    @Override
    public void Effect(Parameter parameter){
        currentSchoolBoards.addAll(parameter.getGame().getSchoolBoards()); //salva per il ripristino le schoolboards
        tmpSchoolBoards=currentSchoolBoards;//Clone of the currentSchoolBoards array
        tmpUpdateProfessors(); //Temporary update of tmpSchoolBoards
        parameter.getGame().setSchoolBoards(tmpSchoolBoards);
        }

    @Override
    public void initializeCard(Parameter parameter) {
        //none
    }

    @Override
    public void endEffect(Parameter parameter) {
        parameter.getGame().setSchoolBoards(currentSchoolBoards); //Reset the original SchoolBoards array
    }

    public void tmpUpdateProfessors(){
        for(PawnColor color : PawnColor.values()) {
            int currentMax = 0;
            for (SchoolBoard s : tmpSchoolBoards) {
                if (s.getStudentHall().get(color) >= currentMax) {
                    currentMax = s.getStudentHall().get(color);
                    for (SchoolBoard f : tmpSchoolBoards) {
                        f.getProfessorTable().replace(color, false);
                    }
                    s.getProfessorTable().replace(color, true);
                }
            }
        }
    }
}
