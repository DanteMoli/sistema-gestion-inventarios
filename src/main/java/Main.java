import java.awt.desktop.SystemSleepEvent;
import java.sql.*;
import java.util.Scanner;

public class Main {
    // Configuración de la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_inventario";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        int opcion = 0;

        try (Connection con = DriverManager.getConnection(URL, USUARIO, PASSWORD)) {
            System.out.println("[SISTEMA] Conexión establecida con éxito.");

            while (opcion != 8) {
                System.out.println("\n--- MENÚ DE INVENTARIO ---");
                System.out.println("1. Registrar nuevo producto");
                System.out.println("2. Ver todos los productos");
                System.out.println("3. Actualizar productos");
                System.out.println("4. Eliminar productos");
                System.out.println("5. Consultas de stock");
                System.out.println("6. Calculo del valor de inventario");
                System.out.println("7. Busqueda por nombre");
                System.out.println("8. Salir");
                System.out.print("Seleccione una opción: ");
                opcion = teclado.nextInt();
                teclado.nextLine(); // Limpiar el buffer

                switch (opcion) {
                    case 1:
                        registrarProducto(con, teclado);
                        break;
                    case 2:
                        mostrarProductos(con);
                        break;
                    case 3:
                        actualizarProductos(con, teclado);
                        break;
                    case 4:
                        eliminarProductos(con, teclado);
                        break;
                    case 5:
                        consultaProductos(con, teclado);
                        break;
                    case 6:
                        menuStock(con,teclado);
                        break;
                    case 7:
                        buscarporNombre(con,teclado);
                        break;
                    case 8:
                        System.out.println("Cerrando sistema... ¡Hasta pronto, Ingeniero!");
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error crítico: " + e.getMessage());
        }
    }

    // --- MÉTODOS DE APOYO ---

    private static void registrarProducto(Connection con, Scanner teclado) {
        System.out.print("Nombre del producto: ");
        String nombre = teclado.nextLine();
        System.out.print("Precio: ");
        double precio = teclado.nextDouble();
        System.out.print("Stock inicial: ");
        int stock = teclado.nextInt();

        String sql = "INSERT INTO productos (nombre, precio, stock) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setDouble(2, precio);
            pstmt.setInt(3, stock);
            pstmt.executeUpdate();
            System.out.println("✅ ¡Producto registrado con éxito!");
        } catch (SQLException e) {
            System.out.println("❌ Error al registrar: " + e.getMessage());
        }
    }

    private static void mostrarProductos(Connection con) {
        String sql = "SELECT * FROM productos";
        System.out.println("\n--- LISTA DE PRODUCTOS ---");
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf("ID: %d | Nombre: %s | Precio: $%.2f | Stock: %d%n",
                        rs.getInt("id"), rs.getString("nombre"),
                        rs.getDouble("precio"), rs.getInt("stock"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al consultar: " + e.getMessage());
        }
    }
    private static void actualizarProductos(Connection con, Scanner teclado){

        System.out.print("ID del producto a modificar: ");
        int id = teclado.nextInt();
        teclado.nextLine();

        System.out.print("Nombre del producto: ");
        String nombre = teclado.nextLine();
        System.out.print("Precio: ");
        double precio = teclado.nextDouble();
        System.out.print("Stock inicial: ");
        int stock = teclado.nextInt();

        String sql = "UPDATE productos SET nombre = ?, precio = ?, stock = ? WHERE id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setDouble(2, precio);
            pstmt.setInt(3, stock);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            System.out.println("✅ ¡Producto actualizado con éxito!");
        } catch (SQLException e) {
            System.out.println("❌ Error al registrar: " + e.getMessage());
        }

    }

    private static void eliminarProductos(Connection con, Scanner teclado){

        System.out.print("ID del producto a eliminar: ");
        int id = teclado.nextInt();
        teclado.nextLine();

        String sql = "DELETE FROM productos WHERE id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int filasBorradas = pstmt.executeUpdate();

            if (filasBorradas > 0) {
                System.out.println("✅ ¡Producto borrado con éxito!");
            } else {
                System.out.println("⚠️ No se encontró ningún producto con el ID: " + id);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al registrar: " + e.getMessage());
        }

    }
    private static void consultaProductos(Connection con, Scanner teclado){

        System.out.println("Digita el limite de stock que deseas revisar:");
        int stock = teclado.nextInt();
        teclado.nextLine();

        String sql = "SELECT * FROM productos where stock <= ?";


        try (PreparedStatement pstmt = con.prepareStatement(sql)){
             pstmt.setInt(1, stock);
             ResultSet rs = pstmt.executeQuery();

            boolean hayproducto = false;
            while (rs.next()) {
                if (!hayproducto){
                hayproducto = true;
                System.out.println("\n--- Productos con stock de " +stock+ " o menos ---");
            }
                System.out.printf("ID: %d | Nombre: %s | Precio: $%.2f | Stock: %d%n",
                        rs.getInt("id"), rs.getString("nombre"),
                        rs.getDouble("precio"), rs.getInt("stock"));
                }
            if (!hayproducto){
                System.out.println("No hay productos con stock menor a: " +stock);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al consultar: " + e.getMessage());
        }
    }

    private static void menuStock(Connection con, Scanner teclado){

        int subOpcion = 0;
        do {
            System.out.println("Digita la opciones que deseas realizar" );
            System.out.println("1. Valor total de productos");
            System.out.println("2. Valor total Individual");
            System.out.println("3. Regresar al Menú Principal");
            subOpcion =teclado.nextInt();
            teclado.nextLine();

            switch (subOpcion) {
                case 1:
                    valorTotal(con);
                    break;
                case 2:
                    valorIndividual(con, teclado);
                    break;
                case 3:
                    System.out.println("Regresando...");
                    break;
                default:
                    System.out.println("⚠️ Opción no válida.");
            }
        } while (subOpcion!=3);


    }

    private static void valorTotal(Connection con){

        double  totalGeneral = 0;
        String sql = " SELECT * FROM productos";

        try (PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {


            while (rs.next()){
                double valorProducto = rs.getDouble("precio") * rs.getInt("stock");
                totalGeneral += valorProducto;
            }
            System.out.printf("\n VALOR TOTAL DEL INVENTARIO: $%.2f%n", totalGeneral);

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar: " + e.getMessage());
        }

    }

    private static void valorIndividual (Connection con, Scanner teclado){

        System.out.println("Digite el ID del producto para revisar su valor total: ");
        int id = teclado.nextInt();
        teclado.nextLine();

        String sql = "SELECT nombre,precio,stock FROM productos WHERE id = ?" ;
        double  totalGeneral = 0;

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
             pstmt.setInt(1, id);
             ResultSet rs = pstmt.executeQuery();

             if (rs.next()){

                 String nombre = rs.getString("nombre");
                 double precio = rs.getDouble("precio");
                 int stock = rs.getInt("stock");

                 double subTotal = precio * stock;

                 System.out.println("\n --VALUACIÓN DEL PRODUCTO---");
                 System.out.println("Producto: "+nombre);
                 System.out.printf("Inversión total en el producto: $%.2f%n", subTotal);
             }else {
                 System.out.println("\n ---No se encontro ningun producto con ese ID ---");
             }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar: " + e.getMessage());
        }


    }

    public static void  buscarporNombre (Connection con, Scanner teclado){

        System.out.println("Digite el nombre de producto que desea buscar: ");
        String producto = teclado.nextLine();


        String sql = "SELECT * FROM productos WHERE nombre LIKE ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setString(1, "%" + producto + "%");
            ResultSet rs = pstmt.executeQuery();

                boolean encontrado = false;
                while (rs.next()) {
                    if (!encontrado){
                        encontrado = true;
                        System.out.println("\n --- Producto encontrado con exito ---");
                    }
                    System.out.printf("ID: %d | Nombre: %s | Precio: $%.2f | Stock: %d%n",
                            rs.getInt("id"), rs.getString("nombre"),
                            rs.getDouble("precio"), rs.getInt("stock"));
            }
                    if (!encontrado){
                    System.out.println("\n ---No se ha encontrado el producto--- \n");
                    }
        }catch (SQLException e) {
            System.out.println("❌ Error al consultar: " + e.getMessage());
        }
    }
}