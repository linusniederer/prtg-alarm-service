import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Service {

    // Logger Initialisieren
    private static final Logger LOG = LogManager.getLogger(PrtgServer.class);

    public static void main(String[] args) throws IOException, InterruptedException {

        /*
         * Daten aus der Config laden
         */
        Properties config = new Properties();
        String propFileName = "config.properties";

        InputStream configStream = Service.class.getClassLoader().getResourceAsStream(propFileName);
        config.load(configStream);

        /*
         *  Neues PRTG Objekt erstellen
         *  Dieses Objekt hat die Möglichkeit, die Hue Lampen zu beeinflussen
         */
        URL url = new URL(config.getProperty("prtgServer"));
        PrtgServer prtgOpacc = new PrtgServer(config.getProperty("prtgUser"), config.getProperty("prtgPassword"), url);

        /*
         *  Neues HueBridge Objekt erfassen
         *  Die HueBridge wird benötigt um die Lampen zu bedienen
         */
        HueBridge opaccIT = new HueBridge(config.getProperty("bridgeIpAddress"), config.getProperty("bridgeName"), config.getProperty("bridgeApplication"), config.getProperty("bridgeUser"));

        /*
         * Lampen der HueBride erfassen
         */
        opaccIT.generateLights();

        /*
         * Applikation wird hier in Endlosschleife versetzt
         */
        while (true) {
            /*
             *  Abfrage der Sensoren eines bestimmten Status
             *  @link https://www.paessler.com/manuals/prtg/live_multiple_object_property_status#query_builder
             */
            String device = prtgOpacc.getSensors(5);
            if (device != null) {
                LOG.debug("Sensor liefert einen Fehler: " + device);
                opaccIT.switchLightsOn();
            } else {
                opaccIT.switchLightsOff();
            }

            // Timeout von 5 Sekunden
            TimeUnit.SECONDS.sleep(5);
        }
    }
}
