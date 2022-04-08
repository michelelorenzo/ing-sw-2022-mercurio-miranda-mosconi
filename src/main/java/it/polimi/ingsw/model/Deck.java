package it.polimi.ingsw.model;

import java.util.Set;

public class Deck {
    private final Set<AssistantCard> cards;
    private WizardType wizard;

    public Deck(Set<AssistantCard> cards)
    {
        this.cards = cards;
        this.wizard = null;
    }

    public WizardType getWizard() {
        return wizard;
    }

    public Set<AssistantCard> getCards() {
        return cards;
    }

    public void setWizard(WizardType wizard) {
        this.wizard = wizard;
    }

}
