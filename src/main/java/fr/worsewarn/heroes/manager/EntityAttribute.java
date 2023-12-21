package fr.worsewarn.heroes.manager;

public enum EntityAttribute {

    STRENGHT(20), VITALITY(20), AGILITY(20), SPAWN_PERCENT(20);

    private int modifier;

    EntityAttribute(int modifier) {
        this.modifier = modifier;
    }

    public int getModifier() {
        return modifier;
    }
}
