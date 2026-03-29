import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddUserDialog extends JDialog {

    private JTextField txtUsername;
    private JTextField txtPassword;
    private JComboBox<String> cmbRole;
    private final Color DARK_BG = new Color(45, 45, 45);
    private final Color TEXT_COLOR = new Color(230, 230, 230);
    private final Color INPUT_BG = new Color(69, 73, 74);

    public AddUserDialog(JFrame parent) {

        super(parent, "Yeni Kullanıcı Ekle", true);
        setSize(300, 300);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(DARK_BG);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        panel.add(createLabel("Kullanıcı Adı:"));
        txtUsername = createTextField();
        panel.add(txtUsername);

        panel.add(createLabel("Şifre:"));
        txtPassword = createTextField();
        panel.add(txtPassword);

        panel.add(createLabel("Rol:"));
        String[] roles = {"user", "admin"};
        cmbRole = new JComboBox<>(roles);
        cmbRole.setBackground(INPUT_BG);
        cmbRole.setForeground(TEXT_COLOR);
        panel.add(cmbRole);

        JButton btnSave = new JButton("Kaydet");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBackground(new Color(200, 200, 200));
        btnSave.setForeground(new Color(45, 45, 45));
        btnSave.setFocusPainted(false);

        panel.add(new JLabel(""));
        panel.add(btnSave);

        add(panel);

        btnSave.addActionListener(e -> saveUser());
    }

    private void saveUser() {

        String user = txtUsername.getText();
        String pass = txtPassword.getText();
        String role = (String) cmbRole.getSelectedItem();

        if(user.isEmpty() || pass.isEmpty()) {

            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!");

            return;
        }

        try(Connection conn = DBHelper.getConnection()) {

            String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.setString(3, role);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kullanıcı başarıyla eklendi.");

            this.dispose();
        }
        catch(SQLException ex) {

            JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
        }
    }

    private JLabel createLabel(String text) {

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(TEXT_COLOR);

        return lbl;
    }

    private JTextField createTextField() {

        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBackground(INPUT_BG);
        txt.setForeground(TEXT_COLOR);
        txt.setCaretColor(Color.WHITE);
        txt.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        return txt;
    }
}