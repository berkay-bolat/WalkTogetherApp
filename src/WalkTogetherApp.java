import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WalkTogetherApp extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private final Color DARK_BG = new Color(45, 45, 45);
    private final Color PANEL_BG = new Color(60, 63, 65);
    private final Color TEXT_COLOR = new Color(230, 230, 230);
    private final Color INPUT_BG = new Color(69, 73, 74);
    private final Color BTN_COLOR = new Color(200, 200, 200);
    private final Color BTN_TEXT = new Color(45, 45, 45);

    public WalkTogetherApp() {

        try {

            for(UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {

                if("Nimbus".equals(info.getName())) {

                    UIManager.setLookAndFeel(info.getClassName());

                    break;
                }
            }
        }
        catch(Exception e) {}

        setTitle("WalkTogether - Giriş Yap");
        setSize(360, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(DARK_BG);
        setLayout(new GridBagLayout());

        JPanel pnlCard = new JPanel(new GridBagLayout());
        pnlCard.setBackground(PANEL_BG);
        pnlCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                BorderFactory.createEmptyBorder(30, 40, 40, 40)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel lblTitle = new JLabel("WalkTogether", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(TEXT_COLOR);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        pnlCard.add(lblTitle, gbc);

        JLabel lblUser = new JLabel("Kullanıcı Adı:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(TEXT_COLOR);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 2, 0);
        pnlCard.add(lblUser, gbc);

        txtUsername = createTextField();
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.ipady = 7;
        pnlCard.add(txtUsername, gbc);

        JLabel lblPass = new JLabel("Şifre:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPass.setForeground(TEXT_COLOR);
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 2, 0);
        gbc.ipady = 0;
        pnlCard.add(lblPass, gbc);

        txtPassword = createPasswordField();
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.ipady = 7;
        pnlCard.add(txtPassword, gbc);

        JButton btnLogin = new JButton("Giriş Yap");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setBackground(BTN_COLOR);
        btnLogin.setForeground(BTN_TEXT);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> login());
        gbc.gridy = 5;
        gbc.insets = new Insets(35, 0, 0, 0);
        gbc.ipady = 10;
        pnlCard.add(btnLogin, gbc);

        add(pnlCard);
    }

    private JTextField createTextField() {

        JTextField txt = new JTextField(15);
        txt.setBackground(INPUT_BG);
        txt.setForeground(TEXT_COLOR);
        txt.setCaretColor(Color.WHITE);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        return txt;
    }

    private JPasswordField createPasswordField() {

        JPasswordField txt = new JPasswordField(15);
        txt.setBackground(INPUT_BG);
        txt.setForeground(TEXT_COLOR);
        txt.setCaretColor(Color.WHITE);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        return txt;
    }

    private void login() {

        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());

        try(Connection conn = DBHelper.getConnection()) {

            if(conn != null) {

                String sql = "SELECT role, user_id, username FROM users WHERE username = ? AND password = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, user);
                ps.setString(2, pass);

                ResultSet rs = ps.executeQuery();

                if(rs.next()) {

                    String role = rs.getString("role");
                    int userId = rs.getInt("user_id");
                    String usernameFromDb = rs.getString("username");
                    new MainMenu(userId, usernameFromDb, role).setVisible(true);
                    this.dispose();
                }
                else {

                    JOptionPane.showMessageDialog(this, "Hatalı kullanıcı adı veya şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        catch(Exception ex) {

            JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new WalkTogetherApp().setVisible(true));
    }
}