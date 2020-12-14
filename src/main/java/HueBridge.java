import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.concurrent.TimeUnit;

public class HueBridge {

    // Logger Initialisieren
    private static final Logger LOG = LogManager.getLogger(PrtgServer.class);

    // Attribute der Klasse
    private String name;
    private String username;
    private String application;
    private String ipAddress;
    public HueLightStore hueBridgeLights;

    /**
     * Erstellen einer neuen HueBridge
     *
     * @param ipAddress   IPv4 Adresse der HueBridge im Netzwerk
     * @param name        Name der HueBridge
     * @param application Name der Applikation
     */
    public HueBridge(String ipAddress, String name, String application) throws IOException, InterruptedException {
        this.ipAddress = ipAddress;
        this.name = name;
        this.application = application;
        this.generateUsername();
    }

    /**
     * Erstellen einer neuen HueBridge
     *
     * @param ipAddress   IPv4 Adresse der HueBridge im Netzwerk
     * @param name        Name der HueBridge
     * @param application Name der Applikation
     * @param username    Username für den API Zugriff auf die Bridge
     */
    public HueBridge(String ipAddress, String name, String application, String username) {
        this.ipAddress = ipAddress;
        this.name = name;
        this.application = application;
        this.username = username;
        this.hueBridgeLights = new HueLightStore();
    }

    /**
     * Erstellen eines Users auf der HueBrige [Für diesen Vorgang muss der Button auf der HueBrige gedrückt werden]
     *
     * @return Rückgabe des Username als String
     */
    private void generateUsername() throws IOException, InterruptedException {

        // URL aus Attributen zusammenstellen
        String apiURL = "http://" + (String) this.ipAddress + "/api";
        URL authenticationURL = new URL(apiURL);

        // JSON String für Server vorbereiten
        String jsonInputString = "{\"devicetype\":\"" + this.name + "#" + this.application + "\"}\n";

        // String für Antwort erstellen
        String response = "error";

        // Ausführen bis Username erhalten werden kann
        while (response.contains("error")) {
            response = this.sendHTTPRequest(authenticationURL, jsonInputString, "POST");
            TimeUnit.SECONDS.sleep(5);
        }

        // Username aus JSON auslesen
        JSONArray jsonResponse = new JSONArray(response);
        JSONObject jsonObject = jsonResponse.getJSONObject(0);
        this.username = jsonObject.getJSONObject("success").getString("username");
    }

    /**
     * Methode um alle Lampen Objekte der HueBridge zu erstellen
     */
    public void generateLights() throws IOException {

        // URL aus Attributen zusammenstellen
        String apiURL = "http://" + (String) this.ipAddress + "/api/" + this.username + "/lights";
        URL lightsURL = new URL(apiURL);

        // Ausführen des Requests
        String response = this.sendHTTPRequest(lightsURL, "GET");

        // String in JSON Objekt umwandeln
        JSONObject hueLights = new JSONObject(response);

        // Jedes Lich Objekt einzeln durchlaufen
        for (int i = 1; i < hueLights.length() + 1; i++) {
            JSONObject hueLight = (JSONObject) hueLights.get(String.valueOf(i));

            // Variablen mit Werten erstellen
            String name = hueLight.get("name").toString();
            String productname = hueLight.get("productname").toString();

            // Neues Licht Objekt erstellen und in LightStoreSpeichern
            this.hueBridgeLights.addHueLight(new HueLight(i, name, productname));
            LOG.debug("Lampe Nummer " + i + " erstellt");
        }
    }

    /**
     * Methode für das Einschalten aller Lampen auf diesem Objekt
     */
    public void switchLightsOn() throws IOException {
        for (int i = 0; i < this.hueBridgeLights.getCount(); i++) {

            // Lampen aus dem Store auslesen
            HueLight tempLight = this.hueBridgeLights.getHueLight(i);

            // Nur ausführen wenn Lampen zurzeit ausgeschaltet sind
            if (tempLight.status == HueLightStatus.OFF) {

                // URL für Lampen vorbereiten
                String apiURL = "http://" + (String) this.ipAddress + "/api/" + this.username + "/lights/" + tempLight.number + "/state";
                URL lightURL = new URL(apiURL);

                // JSON String für Server vorbereiten
                String jsonInputString = "\t{\"on\":true, \"sat\":254, \"bri\":254,\"hue\":65280}";

                // Post Request an Bridge Senden
                String response = this.sendHTTPRequest(lightURL, jsonInputString, "PUT");
                LOG.debug("Licht Nummer " + tempLight.number + " wurde eingeschaltet!");

                // Status anpassen
                this.hueBridgeLights.setHueLightStatus(i, HueLightStatus.ON);
            }
        }
    }

    /**
     * Methode für das Ausschalten aller Lampen auf diesem Objekt
     */
    public void switchLightsOff() throws IOException {
        for (int i = 0; i < this.hueBridgeLights.getCount(); i++) {

            // Lampen aus dem Store auslesen
            HueLight tempLight = this.hueBridgeLights.getHueLight(i);

            // Nur ausführen wenn Lampen zurzeit eingeschaltet sind
            if (tempLight.status == HueLightStatus.ON) {

                // URL für Lampen vorbereiten
                String apiURL = "http://" + (String) this.ipAddress + "/api/" + this.username + "/lights/" + tempLight.number + "/state";
                URL lightURL = new URL(apiURL);

                // JSON String für Server vorbereiten
                String jsonInputString = "{\"on\":false}\n";

                // Post Request an Bridge Senden
                String response = this.sendHTTPRequest(lightURL, jsonInputString, "PUT");
                LOG.debug("Licht Nummer " + tempLight.number + " wurde ausgeschaltet!");

                // Status anpassen
                this.hueBridgeLights.setHueLightStatus(i, HueLightStatus.OFF);
            }
        }
    }

    /**
     * HttpRequest an die Bridge senden
     *
     * @param url             URL an welche der Befehl gesendet werden soll
     * @param jsonInputString Befehl im JSON Format
     * @param method          Art des HTTPRequests
     * @return Rückgabe der Antwort der HueBridge als String
     * @throws IOException
     */
    private String sendHTTPRequest(URL url, String jsonInputString, String method) throws IOException {

        // Verbindung zu Webseite aufbauen
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        // Abfrage ausführen
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Auslesen der Antwort und Rückgabe
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        return reader.readLine();
    }

    /**
     * HttpRequest an die Bridge senden ohne JSON String
     *
     * @param url    URL an welche der Befehl gesendet werden soll
     * @param method Art des HTTPRequests
     * @return Rückgabe der Antwort der HueBridge als String
     * @throws IOException
     */
    private String sendHTTPRequest(URL url, String method) throws IOException {

        // Verbindung zur Webseite aufbauen
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setDoOutput(true);

        // Auslesen der Antwort und Rückgabe
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        return reader.readLine();
    }
}
