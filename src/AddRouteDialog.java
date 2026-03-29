import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddRouteDialog extends JDialog {

    private int creatorId;
    private JTextField txtName;
    private JComboBox<String> cmbDifficulty;
    private JTextField txtSteps;
    private JTextArea txtDesc;
    private final Color DARK_BG = new Color(45, 45, 45);
    private final Color TEXT_COLOR = new Color(230, 230, 230);
    private final Color INPUT_BG = new Color(69, 73, 74);

    public AddRouteDialog(JFrame parent, int adminId) {

        super(parent, "Yeni Rota Ekle", true);
        this.creatorId = adminId;

        setSize(420, 360);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(DARK_BG);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(DARK_BG);

        panel.add(createLabel("Rota Adı:"));
        txtName = createTextField();
        panel.add(txtName);

        panel.add(createLabel("Zorluk Seviyesi:"));
        String[] difficulties = {"1 - Çok Kolay", "2 - Kolay", "3 - Orta", "4 - Zor", "5 - Çok Zor"};
        cmbDifficulty = new JComboBox<>(difficulties);
        cmbDifficulty.setBackground(INPUT_BG);
        cmbDifficulty.setForeground(TEXT_COLOR);
        panel.add(cmbDifficulty);

        panel.add(createLabel("Ortalama Adım Sayısı:"));
        txtSteps = createTextField();
        panel.add(txtSteps);

        panel.add(createLabel("Açıklama:"));
        txtDesc = new JTextArea();
        txtDesc.setLineWrap(true);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDesc.setBackground(INPUT_BG);
        txtDesc.setForeground(TEXT_COLOR);
        txtDesc.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JScrollPane scrollPane = new JScrollPane(txtDesc);
        scrollPane.setBorder(null);
        panel.add(scrollPane);

        JButton btnSave = new JButton("Kaydet");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBackground(new Color(200, 200, 200));
        btnSave.setForeground(new Color(45, 45, 45));
        btnSave.setFocusPainted(false);

        panel.add(new JLabel(""));
        panel.add(btnSave);

        add(panel);

        btnSave.addActionListener(e -> saveRoute());
    }

    private void saveRoute() {

        String name = txtName.getText();
        String stepsStr = txtSteps.getText();
        String desc = txtDesc.getText();
        int difficulty = cmbDifficulty.getSelectedIndex() + 1;

        if(name.isEmpty() || stepsStr.isEmpty() || desc.isEmpty()) {

            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!");

            return;
        }

        try(Connection conn = DBHelper.getConnection()) {

            String sql = "INSERT INTO routes (route_name, difficulty_level, avg_steps, description, creator_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, difficulty);
            ps.setInt(3, Integer.parseInt(stepsStr));
            ps.setString(4, desc);
            ps.setInt(5, creatorId);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Yeni rota başarıyla eklendi.");

            this.dispose();
        }
        catch(NumberFormatException ex) {

            JOptionPane.showMessageDialog(this, "Lütfen adım sayısına sadece rakam girin!");
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