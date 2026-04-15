package tunnel;

import common.Direction;
import common.Utils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Tunnel {
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition condition = lock.newCondition();

    private Direction sensCourant = null;
    private int nbDansTunnel = 0;

    public void entrer(Direction sens, int voitureId) throws InterruptedException {
        lock.lock();
        try {
            Utils.log("[INFO] La voiture " + voitureId +
                    " souhaite entrer dans le tunnel en direction " + sens + ".");

            while (sensCourant != null && sensCourant != sens) {
                Utils.log("[ATTENTE] La voiture " + voitureId +
                        " patiente car le tunnel est occupé dans le sens opposé.");
                condition.await();
            }

            sensCourant = sens;
            nbDansTunnel++;

            Utils.log("[PASSAGE] La voiture " + voitureId +
                    " entre dans le tunnel. Sens actuel : " + sens +
                    ". Nombre de voitures dans le tunnel : " + nbDansTunnel + ".");
        } finally {
            lock.unlock();
        }
    }

    public void sortir(Direction sens, int voitureId) {
        lock.lock();
        try {
            nbDansTunnel--;

            Utils.log("[SORTIE] La voiture " + voitureId +
                    " quitte le tunnel. Voitures restantes : " + nbDansTunnel + ".");

            if (nbDansTunnel == 0) {
                sensCourant = null;
                Utils.log("[INFO] Le tunnel est maintenant vide. Un changement de sens est possible.");
                condition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
}