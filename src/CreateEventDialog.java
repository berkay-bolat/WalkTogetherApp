import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class CreateEventDialog extends JDialog {

    private int routeId;
    private JTextField txtDate;
    private JSpinner spnQuota;
    private final Color DARK_BG = new Color(45, 45, 45);
    private final Color TEXT_COLOR = new Color(230, 230, 230);
    private final Color INPUT_BG = new Color(69, 73, 74);

    public CreateEventDialog(JFrame parent, int routeId, String routeName) {

        super(parent, "Etkinlik Oluştur: " + routeName, true);
        this.routeId = routeId;

        setSize(400, 240);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(DARK_BG);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        panel.setBackground(DARK_BG);

        JLabel lblDate = new JLabel("Tarih (YYYY-AA-GG SS:DD):");
        lblDate.setForeground(TEXT_COLOR);
        lblDate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lblDate);

        txtDate = new JTextField("2026-01-20 09:00");
        txtDate.setBackground(INPUT_BG);
        txtDate.setForeground(TEXT_COLOR);
        txtDate.setCaretColor(Color.WHITE);
        txtDate.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtDate.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(txtDate);

        JLabel lblQuota = new JLabel("Kontenjan:");
        lblQuota.setForeground(INPUT_BG);
        lblQuota.setForeground(TEXT_COLOR);
        lblQuota.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lblQuota);

        spnQuota = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        JComponent editor = spnQuota.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor) editor;
            JFormattedTextField tf = defaultEditor.getTextField();
            tf.setBackground(INPUT_BG);
            tf.setForeground(new Color(45, 45, 45));
            tf.setCaretColor(Color.WHITE);
        }
        spnQuota.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(spnQuota);

        JButton btnCreate = new JButton("Oluştur");
        btnCreate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCreate.setBackground(new Color(200, 200,200));
        btnCreate.setForeground(new Color(45, 45, 45));
        btnCreate.setFocusPainted(false);

        panel.add(new JLabel(""));
        panel.add(btnCreate);

        add(panel);

        btnCreate.addActionListener(e -> createEvent());
    }

    private void createEvent() {

        String dateStr = txtDate.getText();
        int quota = (int) spnQuota.getValue();

        try {

            Timestamp eventTimestamp = Timestamp.valueOf(dateStr + ":00");

            try(Connection conn = DBHelper.getConnection()) {

                String sql = "INSERT INTO events (route_id, event_date, quota) VALUES (?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, routeId);
                ps.setTimestamp(2, eventTimestamp);
                ps.setInt(3, quota);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Etkinlik başarıyla oluşturuldu.");

                this.dispose();
            }
        }
        catch(IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(this, "Lütfen tarihi şu formatta girin: \"YYYY-AA-GG SS:DD\"!\nÖrnek: 2026-05-20 14:30", "Tarih Hatası", JOptionPane.ERROR_MESSAGE);
        }
        catch(SQLException ex) {

            JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
        }
    }
}