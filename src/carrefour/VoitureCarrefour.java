package carrefour;

import common.Direction;
import common.Utils;

public class VoitureCarrefour extends Thread {
    private final int id;
    private final Direction direction;
    private final Carrefour carrefour;

    public VoitureCarrefour(int id, Direction direction, Carrefour carrefour) {
        this.id = id;
        this.direction = direction;
        this.carrefour = carrefour;
    }

    @Override
    public void run() {
        try {
            carrefour.entrer(direction, id);
            Utils.sleep(1200);
            carrefour.sortir(direction, id);
        } catch (InterruptedException e) {
            interrupt();
            Utils.log("[ERREUR] La voiture " + id + " a été interrompue pendant la traversée du carrefour.");
        }
    }
}