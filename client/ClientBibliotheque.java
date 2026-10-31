import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// Petit programme Java (sans Spring) qui appelle notre API comme n'importe quel client.
// Lancer l'API d'abord, puis :  cd client  ->  javac ClientBibliotheque.java  ->  java ClientBibliotheque
public class ClientBibliotheque {

    private static final String BASE_URL = "http://localhost:8080/api/v1";

    private static final HttpClient client = HttpClient.newHttpClient();

    // Compteurs pour le bilan final
    private static int nombreEtapes = 0;
    private static int nombreConformes = 0;

    // ---------- Les 4 méthodes d'appel ----------

    private static HttpResponse<String> get(String chemin) throws Exception {
        HttpRequest requete = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + chemin))
                .header("Accept", "application/json")
                .GET()
                .build();
        return client.send(requete, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> post(String chemin, String corpsJson) throws Exception {
        HttpRequest requete = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + chemin))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(corpsJson))
                .build();
        return client.send(requete, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> patch(String chemin) throws Exception {
        // HttpClient n'a pas de .PATCH() : on passe par .method(...)
        HttpRequest requete = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + chemin))
                .header("Accept", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        return client.send(requete, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> delete(String chemin) throws Exception {
        HttpRequest requete = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + chemin))
                .DELETE()
                .build();
        return client.send(requete, HttpResponse.BodyHandlers.ofString());
    }

    // ---------- Affichage d'une étape ----------

    private static void afficher(String description, String methode, String chemin,
                                 HttpResponse<String> reponse, int codeAttendu) {
        nombreEtapes++;
        boolean conforme = reponse.statusCode() == codeAttendu;
        if (conforme) {
            nombreConformes++;
        }

        System.out.println("=== Étape " + nombreEtapes + " : " + description + " ===");
        System.out.println(methode + " " + chemin + " → " + reponse.statusCode()
                + " (attendu : " + codeAttendu + ") " + (conforme ? "CONFORME" : "NON CONFORME"));
        System.out.println("Réponse : " + reponse.body());
        System.out.println();
    }

    // ---------- Le scénario en 10 étapes ----------

    public static void main(String[] args) throws Exception {

        // 1
        String chemin = "/livres/disponibles?size=3";
        afficher("Lister les 3 premiers livres disponibles", "GET", chemin, get(chemin), 200);

        // 2
        chemin = "/livres/99";
        afficher("Lire un livre inexistant", "GET", chemin, get(chemin), 404);

        // 3
        String julesVerne = """
                {"nom": "Verne", "prenom": "Jules", "nationalite": "Française"}
                """;
        afficher("Créer l'auteur Jules Verne", "POST", "/auteurs", post("/auteurs", julesVerne), 201);

        // 4
        String empruntLivre2 = """
                {"livreId": 2, "emprunteur": "Alice Durand", "emailEmprunteur": "alice.durand@email.com"}
                """;
        afficher("Emprunter le livre 2", "POST", "/emprunts", post("/emprunts", empruntLivre2), 201);

        // 5
        String empruntLivre8 = """
                {"livreId": 8, "emprunteur": "Alice Durand", "emailEmprunteur": "alice.durand@email.com"}
                """;
        afficher("Emprunter le livre 8 (stock à 0)", "POST", "/emprunts", post("/emprunts", empruntLivre8), 409);

        // 6
        chemin = "/emprunts/2/retour";
        afficher("Rendre l'emprunt 2", "PATCH", chemin, patch(chemin), 200);

        // 7
        afficher("Rendre l'emprunt 2 une deuxième fois", "PATCH", chemin, patch(chemin), 409);

        // 8
        chemin = "/auteurs/1";
        afficher("Supprimer l'auteur 1 (il a des livres)", "DELETE", chemin, delete(chemin), 409);

        // 9
        String livreInvalide = """
                {"titre": "", "isbn": "978-2-0000-0000-0", "prix": -5, "stock": 1, "auteurId": 1}
                """;
        afficher("Créer un livre invalide (titre vide, prix négatif)", "POST", "/livres",
                post("/livres", livreInvalide), 400);

        // 10
        chemin = "/auteurs/5";
        afficher("Supprimer l'auteur 5 (aucun livre)", "DELETE", chemin, delete(chemin), 204);

        System.out.println("Bilan : " + nombreConformes + "/" + nombreEtapes + " étapes conformes");
    }
}
