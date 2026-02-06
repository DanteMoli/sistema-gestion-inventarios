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

            while (opcion != 5) {
                System.out.println("\n--- MENÚ DE INVENTARIO ---");
                System.out.println("1. Registrar nuevo producto");
                System.out.println("2. Ver todos los productos");
                System.out.println("3. Actualizar productos");
                System.out.println("4. Eliminar productos");
                System.out.println("5. Salir");
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

        String sql = "DELETE from productos WHERE id = ?";

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
}