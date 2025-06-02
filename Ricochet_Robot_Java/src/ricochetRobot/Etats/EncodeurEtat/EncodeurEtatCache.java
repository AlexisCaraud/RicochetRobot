package ricochetRobot.Etats.EncodeurEtat;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

import ricochetRobot.Etats.EtatPlateau;

public class EncodeurEtatCache {

    private static final int MAX_CACHE_SIZE = 10_000; // ajustable selon RAM dispo

    private final Map<EtatPlateau, BigInteger> cache;

    public EncodeurEtatCache() {
        cache = new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<EtatPlateau, BigInteger> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };
    }

    public BigInteger getCode(EtatPlateau etat, String robotPrincipal) {
        return cache.computeIfAbsent(etat, e -> EncodeurEtatFlexFast.encoderEtatPlateau(e, robotPrincipal));
    }
}
