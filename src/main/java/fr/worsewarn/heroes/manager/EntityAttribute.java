package fr.worsewarn.heroes.manager;

public enum EntityAttribute {

    STRENGTH("heroes.attribute_strength", 20), VITALITY("heroes.attribute_vitality", 20), AGILITY("heroes.attribute_agility", 20), SPAWN_PERCENT("heroes.attribute_spawn_percent", 20);

    private String name;
    private int modifier;

    EntityAttribute(String name, int modifier) {
        this.name = name;
        this.modifier = modifier;
    }

    public String getName() {
        return name;
    }

    public int getModifier() {
        return modifier;
    }
}
