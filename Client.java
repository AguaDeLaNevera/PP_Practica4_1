import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    // Mètode per obtenir el flux de dades de lectura
    public static BufferedReader getFlujo(InputStream is) {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader bfr = new BufferedReader(isr);
        return bfr;
    }

    public static void main(String[] args) {
        // Definir l'adreça de destinació i el port
        String destino = "localhost";
        int puertoDestino = 2222;

        // Crear un socket
        Socket socket = new Socket();
        InetSocketAddress direccion = new InetSocketAddress(destino, puertoDestino);

        try {
            // Establir la connexió amb el servidor
            socket.connect(direccion);

            while (true) {
                // Llegir l'entrada de l'usuari
                System.out.println("Escriu 'INSERT' per INSERT:");
                System.out.println("Escriu 'SELECT' per SELECT:");
                System.out.println("Escriu 'DELETE' per DELETE:");
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String txt = reader.readLine();
                String id, name, surname;
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                switch(txt.toLowerCase()){
                    case "insert":
                        System.out.println("Introdueix un ID: ");
                        id = reader.readLine();
                        System.out.println("Introdueix un NOM: ");
                        name = reader.readLine();
                        System.out.println("Introdueix un COGNOM: ");
                        surname = reader.readLine();
                        pw.println(txt);
                        pw.println(id);
                        pw.println(name);
                        pw.println(surname);
                        pw.flush();
                        break;
                    case "select":
                        System.out.println("Introdueix un ID: ");
                        id = reader.readLine();
                        pw.println(txt);
                        pw.println(id);
                        pw.flush();
                        break;
                    case "delete":
                        System.out.println("Introdueix un ID: ");
                        id = reader.readLine();
                        pw.println(txt);
                        pw.println(id);
                        pw.flush();
                        break;
                    default:
                        System.err.println("INTRODUCE ONE OF THE ABOVE");
                        txt = "";
                        pw.println(txt);
                        pw.flush();
                        continue;
                }
                // Obtindre i imprimir la resposta del servidor
                BufferedReader bfr = Client.getFlujo(socket.getInputStream());
                String line = bfr.readLine();
                if ("ERROR".equals(line)) {
                    System.err.println("Server returned an error. Please check your input.");
                } else {
                    String[] wordsArray = line.split("/");
                    StringBuilder str = new StringBuilder();
                    for (String word : wordsArray) {
                        str.append(word).append("\n");
                    }
                    System.out.println(str);
                }
            }
        } catch (IOException e) {
            System.out.println("Error Client");
        }
    }
}
