package dev.rhmnlm.rpsbackend.enums;

import java.util.concurrent.ThreadLocalRandom;

public enum RPS {
    ROCK {
        public boolean beats(RPS other) { return other == SCISSORS; }
    },
    PAPER {
        public boolean beats(RPS other) { return other == ROCK; }
    },
    SCISSORS {
        public boolean beats(RPS other) { return other == PAPER; }
    };

    public abstract boolean beats(RPS other);

    public static RPS fromString(String value) {
        return RPS.valueOf(value.trim().toUpperCase());
    }

    private static final RPS[] VALUES = values();

    public static RPS random() {
        return VALUES[ThreadLocalRandom.current().nextInt(VALUES.length)];
    }
}

