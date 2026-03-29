import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class IncomingInvitationsDialog extends JDialog {

    private int currentUserId;
    private JTable tblInvites;
    private DefaultTableModel modelInvites;
    private final Color DARK_BG = new Color(45, 45, 45);
    private final Color PANEL_BG = new Color(60, 63, 65);
    private final Color TEXT_COLOR = new Color(230, 230, 230);
    private final Color TABLE_HEADER_BG = new Color(200, 200, 200);
    private final Color GRID_COLOR = new Color(100, 100, 100);

    public IncomingInvitationsDialog(JFrame parent, int userId) {

        super(parent, "Gelen Davetler", true);
        this.currentUserId = userId;

        setSize(700, 450);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(DARK_BG);
        setLayout(new BorderLayout(10, 10));

        String[] cols = {"Davet ID", "Etkinlik ID", "Davet Eden", "Rota", "Tarih"};
        modelInvites = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblInvites = new JTable(modelInvites);

        int[] colWidths = {70, 80, 90, 200, 160};
        styleTable(tblInvites, colWidths);

        add(new JScrollPane(tblInvites), BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlBtns.setBackground(DARK_BG);
        pnlBtns.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JButton btnAccept = new JButton("Kabul Et & Katıl");
        styleButton(btnAccept, new Color(200, 200, 200));
        JButton btnReject = new JButton("Reddet");
        styleButton(btnReject, new Color(200, 200, 200));

        pnlBtns.add(btnAccept);
        pnlBtns.add(btnReject);
        add(pnlBtns, BorderLayout.SOUTH);

        btnAccept.addActionListener(e -> processInvitation(true));
        btnReject.addActionListener(e -> processInvitation(false));

        loadInvitations();
    }

    private void loadInvitations() {

        modelInvites.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy");

        try(Connection conn = DBHelper.getConnection()) {

            String sql = "SELECT i.invitation_id, i.event_id, u.username, r.route_name, e.event_date " +
                         "FROM invitations i " +
                         "JOIN users u ON i.sender_id = u.user_id " +
                         "JOIN events e ON i.event_id = e.event_id " +
                         "JOIN routes r ON e.route_id = r.route_id " +
                         "WHERE i.receiver_id = ? " +
                         "ORDER BY i.sent_at DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, currentUserId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {

                Timestamp ts = rs.getTimestamp("event_date");
                String formattedDate = (ts != null) ? sdf.format(ts) : "";

                modelInvites.addRow(new Object[]{
                        rs.getInt("invitation_id"),
                        rs.getInt("event_id"),
                        rs.getString("username"),
                        rs.getString("route_name"),
                        formattedDate
                });
            }
        }
        catch(SQLException ex) {

            JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
        }
    }

    private void processInvitation(boolean accepted) {

        int row = tblInvites.getSelectedRow();

        if(row == -1) {

            JOptionPane.showMessageDialog(this, "Lütfen bir davet seçin!");

            return;
        }

        int inviteId = (int) modelInvites.getValueAt(row, 0);
        int eventId = (int) modelInvites.getValueAt(row, 1);

        try(Connection conn = DBHelper.getConnection()) {

            if(accepted) {

                String sqlJoin = "INSERT INTO event_participants (user_id, event_id, status) VALUES (?, ?, 'Kayıt Yapıldı')";
                PreparedStatement psJoin = conn.prepareStatement(sqlJoin);
                psJoin.setInt(1, currentUserId);
                psJoin.setInt(2, eventId);
                psJoin.executeUpdate();
                JOptionPane.showMessageDialog(this, "Daveti kabul ettiniz ve etkinliğe katıldınız.");
            }
            else {

                JOptionPane.showMessageDialog(this, "Davet reddedildi.");
            }

            String sqlDel = "DELETE FROM invitations WHERE invitation_id = ?";
            PreparedStatement psDel = conn.prepareStatement(sqlDel);
            psDel.setInt(1, inviteId);
            psDel.executeUpdate();

            loadInvitations();
        }
        catch(SQLException ex) {

            if(ex.getMessage().contains("kontenjan")) {

                JOptionPane.showMessageDialog(this, "Etkinlik kontenjanı dolu, katılamazsınız!", "Hata", JOptionPane.WARNING_MESSAGE);
            }
            else if(ex.getMessage().contains("unique constraint")) {

                JOptionPane.showMessageDialog(this, "Zaten bu etkinliktesiniz! Davet silindi.");
                deleteInvitationOnly(inviteId);
                loadInvitations();
            }
            else {

                JOptionPane.showMessageDialog(this, "HATA: " + ex.getMessage());
            }
        }
    }

    private void deleteInvitationOnly(int inviteId) {

        try(Connection conn = DBHelper.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("DELETE FROM invitations WHERE invitation_id = ?");
            ps.setInt(1, inviteId);
            ps.executeUpdate();
        }
        catch(Exception ex) {

            JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
        }
    }

    private void styleTable(JTable table, int[] colWidths) {

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
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        TableColumnModel columnModel = table.getColumnModel();

        for(int i = 0; i < columnModel.getColumnCount(); i++) {

            if(i < colWidths.length) {

                columnModel.getColumn(i).setPreferredWidth(colWidths[i]);
            }

            columnModel.getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void styleButton(JButton btn, Color bg) {

        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(new Color(45, 45, 45));
        btn.setFocusPainted(false);
    }
}