import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HueLightStore {

    // Neue Array List erstellen
    final List<HueLight> hueLights = new ArrayList<>();

    /**
     * Leerer Konsturktor
     */
    public HueLightStore() {
        // Keine Aktionen beim Konstruktor
    }

    /**
     * Hinzufügen eines neuen Lichtes zum Store
     *
     * @param hueLight Objekt vom Typ HueLight
     */
    public void addHueLight(HueLight hueLight) {
        this.hueLights.add(hueLight);
    }

    /**
     * Auslesen von Objekten aus dem Store
     *
     * @param index Index des gewünschten Objektes
     * @return Rückgabe des HueLight Objektes
     */
    public HueLight getHueLight(int index) {
        return this.hueLights.get(index);
    }

    /**
     * Status der Lampen im Store ändern
     *
     * @param status Status welcher gesetzt werden soll
     */
    public void setHueLightStatus(int index, HueLightStatus status) {
        this.hueLights.get(index).status = status;
    }

    /**
     * Anzahl der Objekte im Store zurückgeben
     *
     * @return Anzahl der Objekte als int
     */
    public int getCount() {
        return this.hueLights.size();
    }
}
