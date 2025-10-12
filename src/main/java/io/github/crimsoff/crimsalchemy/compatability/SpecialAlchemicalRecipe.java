package io.github.crimsoff.crimsalchemy.compatability;

public class SpecialAlchemicalRecipe {
    // This class only exists to display 2 specific interactions of the Alchemical Cauldron.
    // 0 = melting items to water, 1 = Glass Bottle into Witch's brew
    int type = 0;
    public SpecialAlchemicalRecipe(int type) {
        this.type = type;
    }
}
