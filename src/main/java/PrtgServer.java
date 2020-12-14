import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class PrtgServer {

    // Logger Initialisieren
    private static final Logger LOG = LogManager.getLogger(PrtgServer.class);

    // Attribute der Klasse
    private String username;
    private String password;
    private URL url;
    private String passwordHash;

    /**
     * Konskturor für das Erstellen eines neuen Prtg Server Objektes
     *
     * @param username Benutzername für den Server Zugriff
     * @param password Passwort für den Server Zugriff
     */
    public PrtgServer(String username, String password, URL url) {
        this.username = username;
        this.password = password;
        this.url = url;

        // gegenüber dem Server authentifizieren
        try {
            this.loadPasswordHash();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Passwort Hash erstellen
     *
     * @return PasswortHash als Typ String
     */
    private void loadPasswordHash() throws IOException {

        // URL aus Attributen zusammenstellen
        String apiURL = this.url + "/api/getpasshash.htm?username=" + this.username + "&password=" + this.password;
        URL authenticationURL = new URL(apiURL);

        // Verbindung zu Webseite aufbauen
        HttpURLConnection conn = (HttpURLConnection) authenticationURL.openConnection();
        conn.setRequestMethod("GET");

        // Neuen Reader initialisieren
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        // Passwort Hash erstellen
        this.passwordHash = reader.readLine();
        LOG.debug("Verbindung zum Prtg Server hergestellt");
    }

    /**
     * Rückgabe aller Sensoren mit einem bestimmten Status
     *
     * @param status Angabe des Status als Nummer [https://www.paessler.com/manuals/prtg/live_multiple_object_property_status#query_builder]
     */
    public String getSensors(int status) throws IOException {

        // URL aus Attributen zusammenstellen
        String apiURL = this.url + "/api/table.json?content=sensors&filter_status=" + status + "&username=" + this.username + "&passhash=" + this.passwordHash;
        URL requestURL = new URL(apiURL);

        // Verbindung zur Webseite aufbauen
        HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
        conn.setRequestMethod("GET");

        // Neuen Reader initialisieren
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        // String in JSON Objekt umwandeln
        JSONObject prtgStatus = new JSONObject(reader.readLine());
        JSONArray prtgSensors = prtgStatus.getJSONArray("sensors");

        // Sensoren mit Fehler durchlaufen und Ausgeben
        for (int i = 0; i < prtgSensors.length(); i++) {
            JSONObject innerObj = prtgSensors.getJSONObject(i);
            return innerObj.getString("device");
        }

        return null;
    }

    /**
     * Überschreiben der toString Methode
     *
     * @return Informationen zum Objekt als Typ String
     */
    @Override
    public String toString() {
        String toString = "[URL=" + this.url + "] [USER=" + this.username + "] [HASH=" + this.passwordHash + "]";
        LOG.debug(toString);
        return toString;
    }
}
