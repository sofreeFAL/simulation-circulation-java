package tunnel;

import common.Direction;
import common.Utils;

public class VoitureTunnel extends Thread {
    private final int id;
    private final Direction sens;
    private final Tunnel tunnel;

    public VoitureTunnel(int id, Direction sens, Tunnel tunnel) {
        this.id = id;
        this.sens = sens;
        this.tunnel = tunnel;
    }

    @Override
    public void run() {
        try {
            tunnel.entrer(sens, id);
            Utils.sleep(1500);
            tunnel.sortir(sens, id);
        } catch (InterruptedException e) {
            interrupt();
            Utils.log("[ERREUR] La voiture " + id +
                    " a été interrompue pendant la traversée du tunnel.");
        }
    }
}