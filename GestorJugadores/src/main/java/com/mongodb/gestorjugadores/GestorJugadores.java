/* @author Jesús López Viña */
package com.mongodb.gestorjugadores;

// Importes de librerias
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import static com.mongodb.client.model.Filters.*;
import org.bson.Document;
import java.util.Scanner;

public class GestorJugadores {

    // Representa una colección de documentos en MongoDB para realizar operaciones CRUD
    private static MongoCollection<Document> coleccion;

    // Objeto Scanner para leer la entrada del usuario desde la consola
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        // Cadena de conexion con la base de datos MongoDB
        String uri = "mongodb://localhost:27017";

        // Se abre la conexión con MongoDB
        try (MongoClient mongoClient = MongoClients.create(uri)) {

            // Obtener o crear una base de datos MongoDB desde Java
            MongoDatabase database = mongoClient.getDatabase("equipoFutbol");

            // Obtener o crear una colección MongoDB
            coleccion = database.getCollection("jugadores");

            
            // Mostrar el menu
            Menu();
            
        } catch (Exception ex) {
            System.out.println("Error con MongoDB: " + ex.getMessage());
        }
    }

    public static void Menu() {
        
        int opcion;
        boolean salida = false;

        while (!salida) {
            System.out.println("\n==== GESTOR DE JUGADORES DE FÚTBOL ====");
            System.out.println("1. Registrar un nuevo jugador");
            System.out.println("2. Listar todos los jugadores");
            System.out.println("3. Actualizar datos de un jugador");
            System.out.println("4. Eliminar un jugador");
            System.out.println("5. Buscar jugadores por posición");
            System.out.println("6. Salir");
            System.out.print("Elige una opción: ");

            try {
                opcion = Integer.parseInt(sc.nextLine());

                switch (opcion) {
                    case 1:
                        RegistrarJugador();
                        break;
                    case 2:
                        ListarJugadores();
                        break;
                    case 3:
                        ActualizarJugadores();
                        break;
                    case 4:
                        EliminarJugadores();
                        break;
                    case 5:
                        BuscarPorPosicion();
                        break;
                    case 6:
                        salida = true;
                        System.out.println("¡Has finalizado la aplicación!");
                        break;
                    default:
                        System.out.println("Opción no válida. Inténtalo de nuevo.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Por favor, introduce un número válido.");
            }
        }
    }

    private static void RegistrarJugador() {
        System.out.println("\n REGISTRAR JUGADOR ");

        // Nombre a introducir
        System.out.println("Nombre: ");
        String nombre = sc.nextLine();

        // Posición a introducir
        System.out.println("Posicion: ");
        String posicion = sc.nextLine();

        // Edad a introducir en un bucle while para que no se ponga edades letras o caracteres especiales en vez de numeros.
        int edad = 0;
        boolean edadValida = false;
        while (!edadValida) {
            try {
                System.out.println("Edad: ");
                edad = Integer.parseInt(sc.nextLine());
                edadValida = true;
            } catch (NumberFormatException e) {
                System.out.println("Por favor, introduce un número válido para la edad.");
            }
        }

        // Numero del jugador a introducir. Bucle while con un try catch para que no alla letras o caracteres especiales y un if para comprobar el nuemero no se repite.
        int numero = 0;
        boolean numeroValido = false;

        while (!numeroValido) {
            try {
                System.out.println("Numero de camiseta: ");
                numero = Integer.parseInt(sc.nextLine());

                if (esNumeroRepetido(numero)) {
                    System.out.println("Este numero de camiseta ya esta asignado. Elige otro");
                } else {
                    numeroValido = true;
                }

            } catch (NumberFormatException e) {
                System.out.println("Por favor, introduce un número válido para la camiseta.");
            }
        }

        // Crear el documento en MongoDB
        Document jugador = new Document("nombre", nombre)
                .append("posicion", posicion)
                .append("edad", edad)
                .append("numero", numero);

        // Insertar en la colección
        coleccion.insertOne(jugador);
        System.out.println("¡Jugador registrado!");
    }
    
    // Verificar que el numero no esta repetido
    private static boolean esNumeroRepetido(int numero) {
        
        Document query = new Document("numero", numero);
        return coleccion.countDocuments(query) > 0;
        
    }

    
    private static void ListarJugadores() {
       
        System.out.println("\n LISTA DE JUGADORES");
        
        // Buscar al jugador
        FindIterable<Document> jugadores = coleccion.find();
        boolean hayJugadores = false;
        
        for(Document jugador : jugadores){
             mostrarJugador(jugador);
             hayJugadores = true;
        }
        
        if(!hayJugadores){
            System.out.println("No hay jugadores registrados.");
        }
    }
    
    
    // Mostrar infomación del Jugador
    private static void mostrarJugador(Document jugador) {
        
        System.out.println("\nNombre: " + jugador.getString("nombre"));
        System.out.println("Posición: " + jugador.getString("posicion"));
        System.out.println("Edad: " + jugador.getInteger("edad"));
        System.out.println("Número: " + jugador.getInteger("numero"));
    }

    
    private static void ActualizarJugadores() {
       
       System.out.println("\n ACTUALIZAR JUGADOR");
       System.out.print("Nombre del jugador a actualizar: ");
       String nombre = sc.nextLine();  
       
        // Buscar jugador por nombre
        Document query = new Document("nombre", nombre);
        Document jugador = coleccion.find(query).first();
        
        if (jugador == null) {
            System.out.println("No se encontró ningún jugador con ese nombre.");
            return;
        }
       
        System.out.println("\nDatos actuales del jugador:");
        mostrarJugador(jugador);
        
        // Menú de actualización
        System.out.println("\n¿Qué dato deseas actualizar?");
        System.out.println("1. Posición");
        System.out.println("2. Edad");
        System.out.println("3. Número de camiseta");
        System.out.print("Elige una opción: ");
        
        try {
            int opcion = Integer.parseInt(sc.nextLine());
            
            switch(opcion) {
                case 1:
                    System.out.print("Nueva posición: ");
                    String nuevaPosicion = sc.nextLine();
                    coleccion.updateOne(
                        eq("nombre", nombre),
                        Updates.set("posicion", nuevaPosicion)
                    );
                    break;
                case 2:
                    try {
                        System.out.print("Nueva edad: ");
                        int nuevaEdad = Integer.parseInt(sc.nextLine());
                        coleccion.updateOne(
                            eq("nombre", nombre),
                            Updates.set("edad", nuevaEdad)
                        );
                    } catch (NumberFormatException e) {
                        System.out.println("Edad no válida. No se ha actualizado.");
                        return;
                    }
                    break;
                case 3:
                    try {
                        System.out.print("Nuevo número de camiseta: ");
                        int nuevoNumero = Integer.parseInt(sc.nextLine());
                        
                        // Verificar que el número no esté repetido
                        if (esNumeroRepetido(nuevoNumero)) {
                            System.out.println("Este número de camiseta ya está asignado. No se ha actualizado.");
                            return;
                        }
                        
                        coleccion.updateOne(
                            eq("nombre", nombre),
                            Updates.set("numero", nuevoNumero)
                        );
                    } catch (NumberFormatException e) {
                        System.out.println("Número no válido. No se ha actualizado.");
                        return;
                    }
                    break;
                default:
                    System.out.println("Opción no válida.");
                    return;
            }
            
            System.out.println("¡Jugador actualizado con éxito!");
            
        } catch (NumberFormatException e) {
            System.out.println("Por favor, introduce un número válido.");
        }
    }
    

    private static void EliminarJugadores() {
        
        System.out.println("\n ELIMINAR JUGADOR");
        System.out.print("Nombre del jugador a eliminar: ");
        String nombre = sc.nextLine();
        
        // Buscar jugador por nombre
        Document query = new Document("nombre", nombre);
        Document jugador = coleccion.find(query).first();
        
        if (jugador == null) {
            System.out.println("No se encontró ningún jugador con ese nombre.");
            return;
        }
        
        System.out.println("\n Datos del jugador a eliminar:");
        mostrarJugador(jugador);
        
        System.out.print("¿Estás seguro de que deseas eliminar este jugador? (S/N): ");
        String confirmacion = sc.nextLine();
        
        if(confirmacion.equalsIgnoreCase("S")){
            coleccion.deleteOne(query);
               System.out.println("¡Jugador eliminado con éxito!");
        } else {
            System.out.println("Operación cancelada.");
        }    
    }

    private static void BuscarPorPosicion() {

      System.out.println("\n BUSCAR JUGADORES POR POSICIÓN");
      System.out.print("Posición a buscar: ");
      String posicion = sc.nextLine();
        
      // Buscar jugadores por posición
      Document query = new Document("posicion", posicion);
      FindIterable<Document> jugadores = coleccion.find(query);  
      
      boolean hayJugadores = false;
      System.out.println("\n Jugadores en la posición '" + posicion + "':");
      
      for(Document jugador : jugadores){
          mostrarJugador(jugador);
          hayJugadores = true;
      }
        
      if(!hayJugadores){
          System.out.println("No hay jugadores registrado en esa posición.");
      }  
        
    }

}
