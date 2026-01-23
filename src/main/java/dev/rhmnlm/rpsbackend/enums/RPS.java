package dev.rhmnlm.rpsbackend.enums;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum RPS {
    ROCK {
        public boolean beats(RPS other) { return other == SCISSORS; }
        public RPS beatenBy() { return PAPER; }
    },
    PAPER {
        public boolean beats(RPS other) { return other == ROCK; }
        public RPS beatenBy() { return SCISSORS; }
    },
    SCISSORS {
        public boolean beats(RPS other) { return other == PAPER; }
        public RPS beatenBy() { return ROCK; }
    };

    public abstract boolean beats(RPS other);
    public abstract RPS beatenBy();

    public static RPS fromString(String value) {
        return RPS.valueOf(value.trim().toUpperCase());
    }

    private static final RPS[] VALUES = values();

    public static RPS random() {
        return VALUES[ThreadLocalRandom.current().nextInt(VALUES.length)];
    }

    public static String generateMoveSets(int count) {
        if (count < 1 || count > 10) {
            throw new IllegalArgumentException("Count must be between 1 and 10");
        }

        return Stream.generate(RPS::random)
                .limit(count)
                .map(RPS::name)
                .collect(Collectors.joining(","));
    }
}

