package carrefour;

import common.Direction;
import common.Utils;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Carrefour {
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition condition = lock.newCondition();

    private final Map<Direction, Integer> attente = new EnumMap<>(Direction.class);
    private boolean occupe = false;

    public Carrefour() {
        attente.put(Direction.A, 0);
        attente.put(Direction.B, 0);
        attente.put(Direction.C, 0);
        attente.put(Direction.D, 0);
    }

    private Direction droite(Direction d) {
        return switch (d) {
            case A -> Direction.B;
            case B -> Direction.C;
            case C -> Direction.D;
            case D -> Direction.A;
            default -> throw new IllegalArgumentException("Direction invalide");
        };
    }

    private boolean toutLeMondeAttend() {
        return attente.get(Direction.A) > 0
                && attente.get(Direction.B) > 0
                && attente.get(Direction.C) > 0
                && attente.get(Direction.D) > 0;
    }

    public void entrer(Direction direction, int voitureId) throws InterruptedException {
        lock.lock();
        try {
            attente.put(direction, attente.get(direction) + 1);
            Utils.log("[INFO] La voiture " + voitureId + " arrive au carrefour depuis la direction " + direction + ".");

            while (occupe || (attente.get(droite(direction)) > 0 && !toutLeMondeAttend())) {
                Utils.log("[ATTENTE] La voiture " + voitureId + " attend au carrefour depuis "
                        + direction + " car la priorité à droite revient à la direction " + droite(direction) + ".");
                condition.await();
            }

            if (!occupe && toutLeMondeAttend()) {
                Utils.log("[DÉBLOCAGE] Un blocage circulaire a été détecté. "
                        + "La voiture " + voitureId + " depuis " + direction + " est autorisée à avancer.");
            }

            attente.put(direction, attente.get(direction) - 1);
            occupe = true;

            Utils.log("[PASSAGE] La voiture " + voitureId + " entre dans le carrefour depuis la direction " + direction + ".");
        } finally {
            lock.unlock();
        }
    }

    public void sortir(Direction direction, int voitureId) {
        lock.lock();
        try {
            occupe = false;
            Utils.log("[SORTIE] La voiture " + voitureId + " a quitté le carrefour depuis la direction " + direction + ".");
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}