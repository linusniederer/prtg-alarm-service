import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HueLight {

    // Logger Initialisieren
    private static final Logger LOG = LogManager.getLogger(PrtgServer.class);

    // Attribute der Klasse
    public int number;
    public String name;
    private String productname;
    public HueLightStatus status = HueLightStatus.OFF;

    /**
     * Konsturktor für das Erstellen einer neuen HueLampe
     *
     * @param name        Name des Licht Objektes
     * @param productname Produktname des Licht Objektes
     */
    public HueLight(int number, String name, String productname) {
        this.number = number;
        this.name = name;
        this.productname = productname;
    }

    /**
     * Überschreiben der toString Methode
     *
     * @return Objektinformationen als String
     */
    @Override
    public String toString() {
        String msg = "Nummer: " + this.number + " Name: " + this.name;
        LOG.debug(msg);
        return msg;
    }
}
