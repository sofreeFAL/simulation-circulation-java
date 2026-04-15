package parking;

import common.Utils;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Parking {
    private final Semaphore places;
    private final int capaciteMax;
    private final AtomicInteger voituresPresentes = new AtomicInteger(0);

    public Parking(int capaciteMax) {
        this.capaciteMax = capaciteMax;
        this.places = new Semaphore(capaciteMax, true);
    }

    public void entrer(int voitureId, int porte) throws InterruptedException {

        Utils.log("[INFO] La voiture " + voitureId +
                " demande l'accès au parking par la porte " + porte + ".");

        if (places.availablePermits() == 0) {
            Utils.log("[ATTENTE] La voiture " + voitureId +
                    " patiente car le parking est actuellement complet.");
        }

        places.acquire();

        int presentes = voituresPresentes.incrementAndGet();

        Utils.log("[PASSAGE] La voiture " + voitureId +
                " entre dans le parking par la porte " + porte +
                ". Occupation actuelle : " + presentes + "/" + capaciteMax + ".");
    }

    public void sortir(int voitureId, int porte) {
        int presentes = voituresPresentes.decrementAndGet();
        places.release();

        Utils.log("[SORTIE] La voiture " + voitureId +
                " quitte le parking par la porte " + porte +
                ". Occupation actuelle : " + presentes + "/" + capaciteMax + ".");
    }

    public int getCapaciteMax() {
        return capaciteMax;
    }

    public int getVoituresPresentes() {
        return voituresPresentes.get();
    }
}