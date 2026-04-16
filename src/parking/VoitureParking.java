package parking;

import common.Utils;

public class VoitureParking extends Thread {
    private final int id;
    private final int porteEntree;
    private final int porteSortie;
    private final Parking parking;
    private final long dureeStationnement;

    public VoitureParking(int id, int porteEntree, int porteSortie, Parking parking, long dureeStationnement) {
        this.id = id;
        this.porteEntree = porteEntree;
        this.porteSortie = porteSortie;
        this.parking = parking;
        this.dureeStationnement = dureeStationnement;
    }

    @Override
    public void run() {
        try {
            parking.entrer(id, porteEntree);

            Utils.log("[INFO] La voiture " + id +
                    " est stationnée pendant " + dureeStationnement + " ms.");
            Utils.sleep(dureeStationnement);

            parking.sortir(id, porteSortie);
        } catch (InterruptedException e) {
            interrupt();
            Utils.log("[ERREUR] La voiture " + id +
                    " a été interrompue pendant son passage dans le parking.");
        }
    }
}