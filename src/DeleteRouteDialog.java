import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteRouteDialog extends JDialog {

    private JComboBox<String> cmbRoutes;
    private MainMenu parentMenu;
    private final Color DARK_BG = new Color(45, 45, 45);
    private final Color TEXT_COLOR = new Color(230, 230, 230);
    private final Color INPUT_BG = new Color(69, 73, 74);

    public DeleteRouteDialog(MainMenu parent) {

        super(parent, "Bir Rota Sil", true);
        this.parentMenu = parent;

        setSize(500, 180);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(DARK_BG);

        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        panel.add(createLabel("Silinecek Rotayı Seçiniz:"));

        cmbRoutes = new JComboBox<>();
        cmbRoutes.setBackground(INPUT_BG);
        cmbRoutes.setForeground(TEXT_COLOR);
        loadRoutesToCombo();
        panel.add(cmbRoutes);

        JButton btnDelete = new JButton("Sil");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.setBackground(new Color(200, 200, 200));
        btnDelete.setForeground(new Color(45, 45, 45));
        btnDelete.setFocusPainted(false);

        panel.add(new JLabel(""));
        panel.add(btnDelete);

        add(panel);

        btnDelete.addActionListener(e -> deleteSelectedRoute());
    }

    private void loadRoutesToCombo() {

        try(Connection conn = DBHelper.getConnection()) {

            String sql = "SELECT route_id, route_name FROM routes ORDER BY route_id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            boolean hasData = false;

            while(rs.next()) {

                cmbRoutes.addItem("[Rota ID: " + rs.getInt("route_id") + "] " + rs.getString("route_name"));
                hasData = true;
            }

            if(!hasData) {

                cmbRoutes.addItem("Listelenecek rota bulunamadı!");
                cmbRoutes.setEnabled(false);
            }
        }
        catch (SQLException ex) {

            JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
        }
    }

    private void deleteSelectedRoute() {

        String selected = (String) cmbRoutes.getSelectedItem();

        if(selected == null || selected.equals("Listelenecek rota bulunamadı!")) {

            JOptionPane.showMessageDialog(this, "Lütfen geçerli bir rota seçin!");

            return;
        }

        try {

            int idEndIndex = selected.indexOf("]");
            String idStr = selected.substring(selected.indexOf(":") + 1, idEndIndex).trim();
            int routeId = Integer.parseInt(idStr);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Dikkat! Bu rotayı silmek üzeresiniz.\nTüm etkinlikler ve veriler kaybolacak. Emin misiniz?",
                    "Onayla", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if(confirm == JOptionPane.YES_OPTION) {

                try(Connection conn = DBHelper.getConnection()) {

                    String sql = "DELETE FROM routes WHERE route_id = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, routeId);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Rota başarıyla silindi.");

                    parentMenu.refreshRoutes();

                    this.dispose();
                }
                catch (SQLException ex) {

                    JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
                }
            }
        }
        catch(Exception ex) {

            JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
        }
    }

    private JLabel createLabel(String text) {

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(TEXT_COLOR);

        return lbl;
    }
}