import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InviteUserDialog extends JDialog {

    private JComboBox<String> cmbUsers;
    private int currentUserId;
    private int eventId;
    private final Color DARK_BG = new Color(45, 45, 45);
    private final Color TEXT_COLOR = new Color(230, 230, 230);
    private final Color INPUT_BG = new Color(69, 73, 74);

    public InviteUserDialog(JFrame parent, int currentUserId, int eventId) {

        super(parent, "Kullanıcı Davet Et", true);
        this.currentUserId = currentUserId;
        this.eventId = eventId;

        setSize(400, 200);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(DARK_BG);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        JLabel lblInfo = new JLabel("Davet edilecek kullanıcıyı seçiniz:");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblInfo.setForeground(TEXT_COLOR);
        panel.add(lblInfo);

        cmbUsers = new JComboBox<>();
        cmbUsers.setBackground(INPUT_BG);
        cmbUsers.setForeground(TEXT_COLOR);
        loadUsers();
        panel.add(cmbUsers);

        JButton btnInvite = new JButton("Davet Gönder");
        btnInvite.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnInvite.setBackground(new Color(200, 200, 200));
        btnInvite.setForeground(new Color(45, 45, 45));
        btnInvite.setFocusPainted(false);

        btnInvite.addActionListener(e -> sendInvitation());

        panel.add(btnInvite);
        add(panel);
    }

    private void loadUsers() {

        try(Connection conn = DBHelper.getConnection()) {

            String sql = "SELECT username FROM users WHERE user_id != ? ORDER BY username";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, currentUserId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {

                cmbUsers.addItem(rs.getString("username"));
            }
        }
        catch(SQLException ex) {

            JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
        }
    }

    private void sendInvitation() {

        String selectedUser = (String) cmbUsers.getSelectedItem();

        if(selectedUser == null) return;

        try(Connection conn = DBHelper.getConnection()) {

            String idSql = "SELECT user_id FROM users WHERE username = ?";
            PreparedStatement psId = conn.prepareStatement(idSql);
            psId.setString(1, selectedUser);
            ResultSet rsId = psId.executeQuery();

            if(rsId.next()) {

                int receiverId = rsId.getInt("user_id");

                String insertSql = "INSERT INTO invitations (sender_id, receiver_id, event_id) VALUES (?, ?, ?)";
                PreparedStatement psIns = conn.prepareStatement(insertSql);
                psIns.setInt(1, currentUserId);
                psIns.setInt(2, receiverId);
                psIns.setInt(3, eventId);

                psIns.executeUpdate();
                JOptionPane.showMessageDialog(this, selectedUser + " başarıyla davet edildi.");
                dispose();
            }
        }
        catch(SQLException ex) {

            if(ex.getMessage().contains("unique constraint")) {

                JOptionPane.showMessageDialog(this, "Bu kullanıcıyı zaten davet ettiniz!", "Hata", JOptionPane.WARNING_MESSAGE);
            }
            else {

                JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
            }
        }
    }
}