package parking;

import common.Utils;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Parking {
    private final Semaphore places;
    private final Semaphore[] portes;
    private final int capaciteMax;
    private final int nbPortes;
    private final AtomicInteger voituresPresentes = new AtomicInteger(0);

    public Parking(int capaciteMax, int nbPortes) {
        this.capaciteMax = capaciteMax;
        this.nbPortes = nbPortes;
        this.places = new Semaphore(capaciteMax, true);

        this.portes = new Semaphore[nbPortes];
        for (int i = 0; i < nbPortes; i++) {
            portes[i] = new Semaphore(1, true);
        }
    }

    public void entrer(int voitureId, int porte) throws InterruptedException {
        int indexPorte = porte - 1;

        Utils.log("[INFO] La voiture " + voitureId +
                " demande l'accès au parking par la porte " + porte + ".");

        if (places.availablePermits() == 0) {
            Utils.log("[ATTENTE] La voiture " + voitureId +
                    " patiente car le parking est actuellement complet.");
        }

        places.acquire();

        Utils.log("[INFO] La voiture " + voitureId +
                " a obtenu une place et attend maintenant l'accès à la porte " + porte + ".");

        portes[indexPorte].acquire();

        try {
            Utils.log("[PASSAGE] La voiture " + voitureId +
                    " franchit la porte d'entrée " + porte + ".");

            Utils.sleep(1000);

            int presentes = voituresPresentes.incrementAndGet();

            Utils.log("[PASSAGE] La voiture " + voitureId +
                    " est entrée dans le parking par la porte " + porte +
                    ". Occupation actuelle : " + presentes + "/" + capaciteMax + ".");
        } finally {
            portes[indexPorte].release();
        }
    }

    public void sortir(int voitureId, int porte) throws InterruptedException {
        int indexPorte = porte - 1;

        Utils.log("[INFO] La voiture " + voitureId +
                " souhaite quitter le parking par la porte " + porte + ".");

        portes[indexPorte].acquire();

        try {
            Utils.log("[SORTIE] La voiture " + voitureId +
                    " franchit la porte de sortie " + porte + ".");

            Utils.sleep(1000);

            int presentes = voituresPresentes.decrementAndGet();
            places.release();

            Utils.log("[SORTIE] La voiture " + voitureId +
                    " a quitté le parking par la porte " + porte +
                    ". Occupation actuelle : " + presentes + "/" + capaciteMax + ".");
        } finally {
            portes[indexPorte].release();
        }
    }

    public int getCapaciteMax() {
        return capaciteMax;
    }

    public int getVoituresPresentes() {
        return voituresPresentes.get();
    }

    public int getNbPortes() {
        return nbPortes;
    }
}