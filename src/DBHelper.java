import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DBHelper {

    private static final String databaseName = "jdbc:postgresql://localhost:5432/VTYSProject";
    private static final String userName = "postgres";
    private static final String userPassword = "2034";

    public static Connection getConnection() {

        Connection connection = null;

        try {

            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(databaseName, userName, userPassword);

            System.out.println("Veritabanına başarıyla bağlanıldı!");
        }
        catch (ClassNotFoundException e) {

            JOptionPane.showMessageDialog(null, "JDBC Driver bulunamadı!");

            e.printStackTrace();
        }
        catch (SQLException ex) {

            JOptionPane.showMessageDialog(null, "VERİTABANI HATASI: " + ex.getMessage());
        }

        return connection;
    }
}