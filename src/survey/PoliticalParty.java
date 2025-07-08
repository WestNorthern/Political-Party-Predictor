package src.survey;

import java.util.HashMap;
import java.util.Map;

public enum PoliticalParty {
    DEMOCRATIC_ESTABLISHMENT("Democratic Establishment"),
    DEMOCRATIC_SOCIALIST("Democratic Socialist"),
    TRUMP_REPUBLICAN("Trump-aligned Republican"),
    ESTABLISHMENT_REPUBLICAN("Establishment Republican"),
    GREEN("Green Party"),
    LIBERTARIAN("Libertarian"),
    INDEPENDENT("Independent/Other");

    private final String displayName;

    PoliticalParty(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PoliticalParty fromString(String text) {
        for (PoliticalParty party : PoliticalParty.values()) {
            if (party.displayName.equalsIgnoreCase(text) || party.name().equalsIgnoreCase(text)) {
                return party;
            }
        }
        throw new IllegalArgumentException("No political party with text: " + text);
    }
} 