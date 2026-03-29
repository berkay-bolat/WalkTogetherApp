import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MakeAdminDialog extends JDialog {

    private JComboBox<String> cmbUsers;
    private MainMenu parentMenu;
    private final Color DARK_BG = new Color(45, 45, 45);
    private final Color TEXT_COLOR = new Color(230, 230, 230);
    private final Color INPUT_BG = new Color(69, 73, 74);

    public MakeAdminDialog(MainMenu parent) {

        super(parent, "Bir Kullanıcıyı Yönetici Yap", true);
        this.parentMenu = parent;

        setSize(500, 180);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(DARK_BG);

        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        panel.add(createLabel("Kullanıcı Seçiniz:"));

        cmbUsers = new JComboBox<>();
        cmbUsers.setBackground(INPUT_BG);
        cmbUsers.setForeground(TEXT_COLOR);
        loadUsersToCombo();
        panel.add(cmbUsers);

        JButton btnMakeAdmin = new JButton("Yap");
        btnMakeAdmin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnMakeAdmin.setBackground(new Color(200, 200, 200));
        btnMakeAdmin.setForeground(new Color(45, 45, 45));
        btnMakeAdmin.setFocusPainted(false);

        panel.add(new JLabel(""));
        panel.add(btnMakeAdmin);

        add(panel);

        btnMakeAdmin.addActionListener(e -> makeUserAdmin());
    }

    private void loadUsersToCombo() {

        try(Connection conn = DBHelper.getConnection()) {

            String sql = "SELECT username FROM users WHERE role = 'user' ORDER BY username";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            boolean hasData = false;

            while(rs.next()) {

                cmbUsers.addItem(rs.getString("username"));
                hasData = true;
            }

            if(!hasData) {

                cmbUsers.addItem("Uygun kullanıcı yok!");
                cmbUsers.setEnabled(false);
            }
        }
        catch (SQLException ex) {

            JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
        }
    }

    private void makeUserAdmin() {

        String selectedUser = (String) cmbUsers.getSelectedItem();

        if(selectedUser == null || selectedUser.equals("Uygun kullanıcı yok!")) {

            JOptionPane.showMessageDialog(this, "Lütfen bir kullanıcı seçin!");

            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "'" + selectedUser + "' kullanıcısına \"admin\" yetkisi verilecek. Emin misiniz?",
                "Onayla", JOptionPane.YES_NO_OPTION);

        if(confirm == JOptionPane.YES_OPTION) {

            try(Connection conn = DBHelper.getConnection()) {

                String sql = "UPDATE users SET role = 'admin' WHERE username = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, selectedUser);

                int rows = ps.executeUpdate();

                if(rows > 0) {

                    JOptionPane.showMessageDialog(this, selectedUser + " artık bir \"admin\".");

                    parentMenu.loadLeaderboard();

                    this.dispose();
                }
            }
            catch (SQLException ex) {

                JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
            }
        }
    }

    private JLabel createLabel(String text) {

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(TEXT_COLOR);

        return lbl;
    }
}