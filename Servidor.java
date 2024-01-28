import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    static boolean exit = false;

    public static void main(String[] args) {

        // Definir el port al qual el servidor escoltarà les connexions
        int puertoDestino = 2222;

        try {
            // Crear un servidor de soquets que escoltarà al port especificat
            ServerSocket serverSocket = new ServerSocket(puertoDestino);

            // Acceptar connexions entrants
            Socket server = serverSocket.accept();

            while (!exit) {
                System.out.println("Connexió rebuda!");

                // Llegir dades del flux d'entrada del client
                InputStream is = server.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader bf = new BufferedReader(isr);
                String linea;

                List<String> llista = new ArrayList<>();

                // Llegir les línies fins que es rebi una línia buida o fins a un màxim de 4 línies
                while ((linea = bf.readLine()) != null && !linea.isEmpty()) {
                    System.out.println("Línia rebuda: " + linea);
                    llista.add(linea);

                    // Sortir del bucle si ja s'han rebut les 3 línies
                    if (llista.size() == 4) {
                        break;
                    }
                    else if(!llista.get(0).equals("insert") && llista.size() == 2){
                        break;
                    }
                }
                String option, id, name, surname;
                option = llista.get(0);
                id = llista.get(1);
                switch (option.trim()) {
                    case "insert":
                        if(!idFinder(id, "bbdd.txt")){
                            name = llista.get(2);
                            surname = llista.get(3);
                            try {
                                FileWriter fw = new FileWriter("bbdd.txt", true);
                                fw.write("\n"+"{");
                                fw.write("\n"+id);
                                fw.write("\n"+name);
                                fw.write("\n"+surname);
                                fw.write("\n"+"}");
                                fw.close();
                                linea = "INSERT was successful";
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            linea = "L'ID ja existeix, error a l'inserir les dades";
                        }

                        break;
                    case "select":
                        if(idFinder(id, "bbdd.txt")){
                            linea = idFinderSelect(id, "bbdd.txt");
                        }
                        else{
                            linea = "L'ID no existeix, error al mostrar les dades";
                        }
                        break;
                    case "delete":
                        if(idFinder(id, "bbdd.txt")){
                            idFinderDelete(id, "bbdd.txt");
                            linea = "DELETE was successful";
                        }
                        else{
                            linea = "L'ID no existeix, error al mostrar les dades";
                        }
                        break;
                    default:
                        System.err.println("La informació ja existeix o és invàlida");
                }
                // Enviar la resposta al client a través del flux de sortida
                OutputStream os = server.getOutputStream();
                PrintWriter pw = new PrintWriter(os);
                pw.println(linea);
                pw.flush();
            }
        } catch (IOException e) {
            System.out.println("Error del servidor");
        }
    }
    private static boolean idFinder(String id, String file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.equals(id)){
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
        return false;
    }
    private static String idFinderSelect(String id, String file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.equals(id)){
                    String name, surname, fullString;
                    name = br.readLine();
                    surname = br.readLine();
                    fullString = "ID: " + id +"/" + "NOM: " + name + "/" + "COGNOM: " + surname;
                    return fullString;
                }

            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
        return "ERROR";
    }
    private static void idFinderDelete(String id, String file) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(id)) {
                    // Skip the lines for this record
                    for (int i = 0; i < 3; i++) {
                        br.readLine();
                    }
                } else {
                    // Add lines other than the ones to be deleted
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }

        // Write the updated content back to the file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                bw.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
    }

}
