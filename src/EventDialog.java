import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.sql.*;

public class EventDialog extends JDialog {

    private int userId;
    private int routeId;
    private JTable tblEvents;
    private DefaultTableModel modelEvents;
    private final Color DARK_BG = new Color(45, 45, 45);
    private final Color PANEL_BG = new Color(60, 63, 65);
    private final Color TEXT_COLOR = new Color(230, 230, 230);
    private final Color TABLE_HEADER_BG = new Color(200, 200, 200);
    private final Color TABLE_HEADER_TEXT = Color.BLACK;
    private final Color GRID_COLOR = new Color(100, 100, 100);

    public EventDialog(JFrame parent, int routeId, int userId) {

        super(parent, "Etkinlik Seçimi", true);
        this.routeId = routeId;
        this.userId = userId;

        setSize(600, 450);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(DARK_BG);
        setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(DARK_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = new JLabel("Bu Rotada Mevcut Etkinlikler", SwingConstants.CENTER);
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblInfo.setForeground(TEXT_COLOR);
        contentPanel.add(lblInfo, BorderLayout.NORTH);

        String[] cols = {"ID", "Tarih", "Kontenjan", "Kalan"};
        modelEvents = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) {

                return false;
            }
        };
        tblEvents = new JTable(modelEvents);
        styleTable(tblEvents);

        JScrollPane scrollPane = new JScrollPane(tblEvents);
        scrollPane.getViewport().setBackground(PANEL_BG);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel pnlButtons = new JPanel();
        pnlButtons.setBackground(DARK_BG);
        JButton btnJoin = new JButton("Katıl");
        btnJoin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnJoin.setBackground(new Color(200, 200, 200));
        btnJoin.setForeground(new Color(45, 45, 45));
        btnJoin.setPreferredSize(new Dimension(250, 40));

        pnlButtons.add(btnJoin);
        contentPanel.add(pnlButtons, BorderLayout.SOUTH);

        add(contentPanel);

        loadEvents();

        btnJoin.addActionListener(e -> {
            int selectedRow = tblEvents.getSelectedRow();

            if(selectedRow != -1) {

                int eventId = (int) modelEvents.getValueAt(selectedRow, 0);

                joinEvent(eventId);
            }
            else {

                JOptionPane.showMessageDialog(this, "Lütfen bir etkinlik seçin!");
            }
        });
    }

    private void styleTable(JTable table) {

        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setBackground(PANEL_BG);
        table.setForeground(TEXT_COLOR);
        table.setSelectionBackground(new Color(75, 110, 175));
        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);

        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setGridColor(GRID_COLOR);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TABLE_HEADER_TEXT);

        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        int[] widths = {50, 200, 100, 100};

        TableColumnModel columnModel = table.getColumnModel();
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for(int i = 0; i < columnModel.getColumnCount(); i++) {

            if(i < widths.length) {

                columnModel.getColumn(i).setPreferredWidth(widths[i]);
            }

            columnModel.getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void loadEvents() {

        modelEvents.setRowCount(0);

        try(Connection conn = DBHelper.getConnection()) {

            String sql = "SELECT event_id, event_date, quota FROM events WHERE route_id = ? AND event_date > CURRENT_TIMESTAMP ORDER BY event_date";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, routeId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {

                int eId = rs.getInt("event_id");
                int quota = rs.getInt("quota");
                PreparedStatement psQ = conn.prepareStatement("SELECT count(*) FROM event_participants WHERE event_id = ?");
                psQ.setInt(1, eId);
                ResultSet rsQ = psQ.executeQuery();
                rsQ.next();
                int used = rsQ.getInt(1);
                modelEvents.addRow(new Object[]{eId, rs.getTimestamp("event_date"), quota, (quota - used)});
            }
        }
        catch (SQLException ex) {

            JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
        }
    }

    private void joinEvent(int eventId) {

        try(Connection conn = DBHelper.getConnection()) {

            String sql = "INSERT INTO event_participants (user_id, event_id, status) VALUES (?, ?, 'Kayıt Yapıldı')";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, eventId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kaydınız başarıyla alındı.");
            loadEvents();
        }
        catch(SQLException ex) {

            if(ex.getMessage().contains("kontenjan dolmuştur")) {

                JOptionPane.showMessageDialog(this,
                        "Kontenjan dolu olduğu için kayıt yapılamadı!",
                        "Kayıt Başarısız",
                        JOptionPane.WARNING_MESSAGE);
            }
            else {

                JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
            }
        }
    }
}