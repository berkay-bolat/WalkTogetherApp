import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.text.SimpleDateFormat;

public class MainMenu extends JFrame {

    private int currentUserId;
    private String currentUserRole;
    private JTable tblRoutes;
    private DefaultTableModel modelRoutes;
    private JTextField txtSearch;
    private JCheckBox chkHardRoutes;
    private JCheckBox chkDiscovery;
    private JTable tblLeaderboard;
    private DefaultTableModel modelLeaderboard;
    private JTable tblProfile;
    private DefaultTableModel modelProfile;
    private JLabel lblMyBadge;
    private JLabel lblAdminStats;
    private final Color DARK_BG = new Color(45, 45, 45);
    private final Color PANEL_BG = new Color(60, 63, 65);
    private final Color TEXT_COLOR = new Color(230, 230, 230);
    private final Color INPUT_BG = new Color(69, 73, 74);
    private final Color TABLE_HEADER_BG = new Color(200, 200, 200);
    private final Color TABLE_HEADER_TEXT = Color.BLACK;
    private final Color ACCENT_COLOR = new Color(75, 110, 175);
    private final Color GRID_COLOR = new Color(100, 100, 100);

    public MainMenu(int userId, String username, String role) {

        this.currentUserId = userId;
        this.currentUserRole = role;

        try {

            for(UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {

                if("Nimbus".equals(info.getName())) {

                    UIManager.setLookAndFeel(info.getClassName());

                    break;
                }
            }
        }
        catch(Exception e) {}

        setTitle("WalkTogether | Kullanıcı: " + username);
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(DARK_BG);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(DARK_BG);
        tabbedPane.setForeground(Color.BLACK);

        JPanel pnlRoutes = new JPanel(new BorderLayout(10, 10));
        pnlRoutes.setBackground(DARK_BG);
        pnlRoutes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlTop.setBackground(PANEL_BG);
        pnlTop.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel lblSearch = new JLabel("Rota Ara:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSearch.setForeground(TEXT_COLOR);

        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBackground(INPUT_BG);
        txtSearch.setForeground(TEXT_COLOR);
        txtSearch.setCaretColor(Color.WHITE);

        chkHardRoutes = new JCheckBox("Yalnızca Zorlu Rotalar");
        chkHardRoutes.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chkHardRoutes.setBackground(PANEL_BG);
        chkHardRoutes.setForeground(new Color(200, 200, 200));
        chkHardRoutes.setFocusPainted(false);

        chkDiscovery = new JCheckBox("Yalnızca Daha Önce Katılmadığım Rotalar");
        chkDiscovery.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chkDiscovery.setBackground(PANEL_BG);
        chkDiscovery.setForeground(new Color(200, 200, 200));
        chkDiscovery.setFocusPainted(false);

        pnlTop.add(lblSearch);
        pnlTop.add(txtSearch);
        pnlTop.add(chkHardRoutes);
        pnlTop.add(chkDiscovery);

        String[] routeCols = {"Rota ID", "Rota Adı", "Zorluk", "Ortalama Adım", "Açıklama"};
        modelRoutes = new DefaultTableModel(routeCols, 0) {
            public boolean isCellEditable(int row, int column) {

                return false;
            }
        };
        tblRoutes = new JTable(modelRoutes);
        int[] routeWidths = {40, 160, 40, 80, 400};
        styleTable(tblRoutes, routeWidths);

        pnlRoutes.add(pnlTop, BorderLayout.NORTH);
        pnlRoutes.add(new JScrollPane(tblRoutes), BorderLayout.CENTER);

        JPanel pnlRouteActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlRouteActions.setBackground(DARK_BG);

        JButton btnCreateEvent = createButton("Yeni Etkinlik Oluştur", new Color(200, 200, 200));
        btnCreateEvent.setForeground(new Color(45, 45, 45));

        JButton btnShowEvents = createButton("Seçili Rota İçin Etkinlikleri Gör", new Color(200, 200, 200));
        btnShowEvents.setForeground(new Color(45, 45, 45));

        pnlRouteActions.add(btnCreateEvent);
        pnlRouteActions.add(btnShowEvents);
        pnlRoutes.add(pnlRouteActions, BorderLayout.SOUTH);

        JPanel pnlLeaderboard = new JPanel(new BorderLayout());
        pnlLeaderboard.setBackground(DARK_BG);
        pnlLeaderboard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] leaderCols = {"Sıra", "Kullanıcı Adı", "Toplam Puan", "Rozet"};
        modelLeaderboard = new DefaultTableModel(leaderCols, 0);
        tblLeaderboard = new JTable(modelLeaderboard);
        int[] leaderWidths = {40, 240, 200, 240};
        styleTable(tblLeaderboard, leaderWidths);

        pnlLeaderboard.add(new JScrollPane(tblLeaderboard), BorderLayout.CENTER);

        JPanel pnlProfile = new JPanel(new BorderLayout(10, 10));
        pnlProfile.setBackground(DARK_BG);
        pnlProfile.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlBadgeInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        pnlBadgeInfo.setBackground(PANEL_BG);
        pnlBadgeInfo.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        lblMyBadge = new JLabel("Yükleniyor...");
        lblMyBadge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblMyBadge.setForeground(TEXT_COLOR);

        pnlBadgeInfo.add(lblMyBadge);

        JPanel pnlProfButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlProfButtons.setBackground(DARK_BG);

        pnlProfButtons.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JButton btnCancel = createButton("Seçili Kaydı İptal Et", new Color(200, 200, 200));
        JButton btnShowLog = createButton("Aktivite Raporunu Getir", new Color(200, 200, 200));
        JButton btnInvite = createButton("Arkadaşını Davet Et", new Color(200, 200, 200));
        JButton btnMyInvites = createButton("Gelen Davetler", new Color(200, 200, 200));

        pnlProfButtons.add(btnCancel);
        pnlProfButtons.add(btnInvite);
        pnlProfButtons.add(btnMyInvites);
        pnlProfButtons.add(btnShowLog);

        btnInvite.addActionListener(e -> {
            int selectedRow = tblProfile.getSelectedRow();

            if(selectedRow == -1) {

                JOptionPane.showMessageDialog(this, "Lütfen davet göndermek istediğiniz etkinliği seçin.");

                return;
            }

            String status = (String) modelProfile.getValueAt(selectedRow, 3);

            if(!"Kayıt Yapıldı".equals(status)) {

                JOptionPane.showMessageDialog(this, "Sadece aktif kayıtlı olduğunuz etkinliklere davet edebilirsiniz!");

                return;
            }

            int eventId = (int) modelProfile.getValueAt(selectedRow, 0);
            new InviteUserDialog(this, currentUserId, eventId).setVisible(true);
        });

        btnMyInvites.addActionListener(e -> {
            new IncomingInvitationsDialog(this, currentUserId).setVisible(true);
            loadProfileData();
        });

        String[] profileCols = {"Event ID", "Rota Adı", "Tarih", "Durum"};
        modelProfile = new DefaultTableModel(profileCols, 0) {
            public boolean isCellEditable(int row, int column) {

                return false;
            }
        };
        tblProfile = new JTable(modelProfile);
        int[] profileWidths = {40, 280, 200, 200};
        styleTable(tblProfile, profileWidths);

        JPanel pnlTopContainer = new JPanel(new BorderLayout());
        pnlTopContainer.setBackground(DARK_BG);
        pnlTopContainer.add(pnlBadgeInfo, BorderLayout.NORTH);
        pnlTopContainer.add(pnlProfButtons, BorderLayout.CENTER);

        pnlProfile.add(pnlTopContainer, BorderLayout.NORTH);
        pnlProfile.add(new JScrollPane(tblProfile), BorderLayout.CENTER);

        JPanel pnlAdmin = null;
        if(currentUserRole.equals("admin")) {

            pnlAdmin = new JPanel(new BorderLayout(10, 10));
            pnlAdmin.setBackground(DARK_BG);
            pnlAdmin.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel pnlAdminStats = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            pnlAdminStats.setBackground(PANEL_BG);
            pnlAdminStats.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            lblAdminStats = new JLabel("Sistem İstatistikleri Yükleniyor...");
            lblAdminStats.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblAdminStats.setForeground(TEXT_COLOR);
            pnlAdminStats.add(lblAdminStats);

            pnlAdmin.add(pnlAdminStats, BorderLayout.NORTH);

            JPanel pnlAdminButtons = new JPanel(new GridBagLayout());
            pnlAdminButtons.setBackground(DARK_BG);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 0, 10, 0);
            gbc.gridx = 0;
            gbc.fill = GridBagConstraints.NONE;

            JButton btnAddUser = createButton("Yeni Kullanıcı Ekle", new Color(200, 200, 200));
            btnAddUser.setPreferredSize(new Dimension(250, 50));
            JButton btnAddRoute = createButton("Yeni Rota Ekle", new Color(200, 200, 200));
            btnAddRoute.setPreferredSize(new Dimension(250, 50));
            JButton btnDeleteRoute = createButton("Bir Rota Sil", new Color(200, 200, 200));
            btnDeleteRoute.setPreferredSize(new Dimension(250, 50));
            JButton btnMakeAdmin = createButton("Bir Kullanıcıyı Yönetici Yap", new Color(200, 200, 200));
            btnMakeAdmin.setPreferredSize(new Dimension(250, 50));

            gbc.gridy = 0;
            pnlAdminButtons.add(btnAddUser, gbc);
            gbc.gridy = 1;
            pnlAdminButtons.add(btnAddRoute, gbc);
            gbc.gridy = 2;
            pnlAdminButtons.add(btnDeleteRoute, gbc);
            gbc.gridy = 3;
            pnlAdminButtons.add(btnMakeAdmin, gbc);

            pnlAdmin.add(pnlAdminButtons, BorderLayout.CENTER);

            btnAddUser.addActionListener(e -> new AddUserDialog(this).setVisible(true));
            btnAddRoute.addActionListener(e -> {
                new AddRouteDialog(this, currentUserId).setVisible(true);
                refreshRoutes();
            });
            btnDeleteRoute.addActionListener(e -> {
                new DeleteRouteDialog(this).setVisible(true);
            });
            btnMakeAdmin.addActionListener(e -> {
                new MakeAdminDialog(this).setVisible(true);
            });
        }

        tabbedPane.addTab("Rotalar", pnlRoutes);
        tabbedPane.addTab("Liderlik Tablosu", pnlLeaderboard);
        tabbedPane.addTab("Profilim", pnlProfile);

        if(pnlAdmin != null) {

            tabbedPane.addTab("Yönetici Paneli", pnlAdmin);
        }

        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            String tabName = tabbedPane.getTitleAt(index);

            if(index == 1 || index == 2) {

                checkCompletedEvents();

                if(index == 1) {

                    loadLeaderboard();
                }
                if(index == 2) {

                    updateMyBadge();
                    loadProfileData();
                }
            }

            if("Yönetici Paneli".equals(tabName) && currentUserRole.equals("admin")) {

                updateAdminStats();
            }
        });

        add(tabbedPane);

        KeyAdapter searchAction = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {

                refreshRoutes();
            }
        };
        txtSearch.addKeyListener(searchAction);
        chkHardRoutes.addItemListener(e -> refreshRoutes());
        chkDiscovery.addItemListener(e -> refreshRoutes());

        btnShowEvents.addActionListener(e -> {
            int selectedRow = tblRoutes.getSelectedRow();

            if(selectedRow != -1) {

                int routeId = (int) modelRoutes.getValueAt(selectedRow, 0);

                new EventDialog(this, routeId, currentUserId).setVisible(true);
            }
            else {

                JOptionPane.showMessageDialog(this, "Lütfen tablodan bir rota seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCreateEvent.addActionListener(e -> {
            int selectedRow = tblRoutes.getSelectedRow();

            if(selectedRow != -1) {

                int routeId = (int) modelRoutes.getValueAt(selectedRow, 0);
                String routeName = (String) modelRoutes.getValueAt(selectedRow, 1);
                new CreateEventDialog(this, routeId, routeName).setVisible(true);
            }
            else {

                JOptionPane.showMessageDialog(this, "Lütfen tablodan bir rota seçin!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnShowLog.addActionListener(e -> {
            checkCompletedEvents();
            showCursorReportLog();
        });
        btnCancel.addActionListener(e -> cancelSelectedParticipation());

        refreshRoutes();
        loadLeaderboard();
        updateMyBadge();
        checkCompletedEvents();
        loadProfileData();
        if(currentUserRole.equals("admin")) updateAdminStats();
    }

    private void updateAdminStats() {

        if(lblAdminStats == null) return;

        try(Connection conn = DBHelper.getConnection()) {

            String sql = "SELECT get_admin_stats()";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {

                lblAdminStats.setText(rs.getString(1));
            }
        }
        catch (SQLException ex) {

            lblAdminStats.setText("İstatistikler alınamadı!");
            JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
        }
    }

    public void refreshRoutes() {

        modelRoutes.setRowCount(0);
        String searchText = txtSearch.getText().trim();
        boolean filterHard = chkHardRoutes.isSelected();
        boolean discoveryMode = chkDiscovery.isSelected();

        try(Connection conn = DBHelper.getConnection()) {

            StringBuilder sql = new StringBuilder();
            PreparedStatement ps;

            if (discoveryMode) {

                sql.append("SELECT * FROM get_unjoined_routes(?)");
                boolean hasWhere = false;

                if (!searchText.isEmpty()) {

                    sql.append(" WHERE route_name ILIKE ?");
                    hasWhere = true;
                }

                if (filterHard) {

                    sql.append(hasWhere ? " AND " : " WHERE ");
                    sql.append("difficulty_level >= 3");
                }

                sql.append(" ORDER BY route_id");

                ps = conn.prepareStatement(sql.toString());
                ps.setInt(1, currentUserId);

                if (!searchText.isEmpty()) {

                    ps.setString(2, "%" + searchText + "%");
                }
            } else {

                sql.append("SELECT route_id, route_name, difficulty_level, avg_steps, description FROM routes ");
                boolean hasWhere = false;

                if (!searchText.isEmpty()) {

                    sql.append("WHERE route_name ILIKE ? ");
                    hasWhere = true;
                }

                if (filterHard) {

                    sql.append(hasWhere ? " AND " : " WHERE ");
                    sql.append("difficulty_level >= 3 ");
                }

                sql.append("ORDER BY route_id");
                ps = conn.prepareStatement(sql.toString());

                if (!searchText.isEmpty()) {

                    ps.setString(1, "%" + searchText + "%");
                }
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                modelRoutes.addRow(new Object[]{
                        rs.getInt("route_id"),
                        rs.getString("route_name"),
                        rs.getInt("difficulty_level"),
                        rs.getInt("avg_steps"),
                        rs.getString("description")
                });
            }
        }
        catch(SQLException ex) {

            JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
        }
    }

    private void styleTable(JTable table, int[] colWidths) {

        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setBackground(PANEL_BG);
        table.setForeground(TEXT_COLOR);
        table.setSelectionBackground(ACCENT_COLOR);
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

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        TableColumnModel columnModel = table.getColumnModel();

        for(int i = 0; i < columnModel.getColumnCount(); i++) {

            if(i < colWidths.length) columnModel.getColumn(i).setPreferredWidth(colWidths[i]);

            columnModel.getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JButton createButton(String text, Color bg) {

        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(new Color(45, 45, 45));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        return btn;
    }

    private void loadProfileData() {

        modelProfile.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy");

        try(Connection conn = DBHelper.getConnection()) {

            String sql = "SELECT ep.event_id, r.route_name, e.event_date, ep.status FROM event_participants ep JOIN events e ON ep.event_id = e.event_id JOIN routes r ON e.route_id = r.route_id WHERE ep.user_id = ? ORDER BY e.event_date DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, currentUserId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {

                Timestamp ts = rs.getTimestamp("event_date");
                String formattedDate = (ts != null) ? sdf.format(ts) : "";
                modelProfile.addRow(new Object[]{
                        rs.getInt("event_id"),
                        rs.getString("route_name"),
                        formattedDate,
                        rs.getString("status")
                });
            }
        }
        catch(SQLException ex) {

            JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
        }
    }

    private void showCursorReportLog() {

        try(Connection conn = DBHelper.getConnection()) {

            String sql = "SELECT get_user_activity_report(?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, currentUserId);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {

                JDialog logDialog = new JDialog(this, "Aktivite Raporu", true);
                logDialog.setSize(900, 400);
                logDialog.setLocationRelativeTo(this);
                logDialog.getContentPane().setBackground(DARK_BG);
                logDialog.setLayout(new BorderLayout());

                JTextArea ta = new JTextArea(rs.getString(1));
                ta.setEditable(false);
                ta.setFont(new Font("Consolas", Font.PLAIN, 13));
                ta.setBackground(INPUT_BG);
                ta.setForeground(TEXT_COLOR);
                ta.setMargin(new Insets(10,10,10,10));

                JScrollPane sp = new JScrollPane(ta);
                sp.setBorder(BorderFactory.createEmptyBorder());
                logDialog.add(sp, BorderLayout.CENTER);

                JButton btnClose = createButton("Kapat", ACCENT_COLOR);
                btnClose.addActionListener(e -> logDialog.dispose());

                JPanel pnlBot = new JPanel();
                pnlBot.setBackground(DARK_BG);
                pnlBot.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
                pnlBot.add(btnClose);
                logDialog.add(pnlBot, BorderLayout.SOUTH);

                logDialog.setVisible(true);
            }
        }
        catch (SQLException ex) {

            JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
        }
    }

    private void cancelSelectedParticipation() {

        int selectedRow = tblProfile.getSelectedRow();

        if(selectedRow == -1) {

            JOptionPane.showMessageDialog(this, "Lütfen tablodan bir kayıt seçin!");

            return;
        }

        String status = (String) modelProfile.getValueAt(selectedRow, 3);

        if("Tamamlandı".equals(status) || "İptal".equals(status)) {

            JOptionPane.showMessageDialog(this, "Sadece 'Kayıt Yapıldı' durumundaki etkinlikleri iptal edebilirsiniz!");

            return;
        }

        int eventId = (int) modelProfile.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Seçili etkinliğe katılımınız iptal edilecek. Emin misiniz?", "Onayla", JOptionPane.YES_NO_OPTION);

        if(confirm == JOptionPane.YES_OPTION) {

            try(Connection conn = DBHelper.getConnection()) {

                String sql = "DELETE FROM event_participants WHERE user_id = ? AND event_id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, currentUserId);
                ps.setInt(2, eventId);

                int rows = ps.executeUpdate();

                if(rows > 0) {

                    JOptionPane.showMessageDialog(this, "İptal edildi.");
                    loadProfileData();
                    updateMyBadge();
                }
            }
            catch(SQLException ex) {

                JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
            }
        }
    }

    public void loadLeaderboard() {

        modelLeaderboard.setRowCount(0);

        try(Connection conn = DBHelper.getConnection()) {

            String sql = "SELECT * FROM leaderboard_view";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) modelLeaderboard.addRow(new Object[]{rs.getInt("rank_no"), rs.getString("username"), rs.getInt("total_points"), rs.getString("badge_name")});
        }
        catch(SQLException ex) {

            JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
        }
    }

    private void updateMyBadge() {

        try(Connection conn = DBHelper.getConnection()) {

            String sqlUser = "SELECT total_points, get_user_badge(total_points) as my_badge FROM users WHERE user_id = ?";
            PreparedStatement psUser = conn.prepareStatement(sqlUser);
            psUser.setInt(1, currentUserId);
            ResultSet rsUser = psUser.executeQuery();

            if(rsUser.next()) {

                int currentPoints = rsUser.getInt("total_points");
                String currentBadge = rsUser.getString("my_badge");
                String sqlNext = "SELECT min_points, badge_name FROM badges WHERE min_points > ? ORDER BY min_points ASC LIMIT 1";
                PreparedStatement psNext = conn.prepareStatement(sqlNext);
                psNext.setInt(1, currentPoints);
                ResultSet rsNext = psNext.executeQuery();

                StringBuilder finalText = new StringBuilder();
                finalText.append("Mevcut Puan:  ").append(currentPoints);
                finalText.append("      |      Mevcut Rozet:  ").append(currentBadge);

                if(rsNext.next()) {

                    int targetPoints = rsNext.getInt("min_points");
                    String nextBadgeName = rsNext.getString("badge_name");
                    int pointsNeeded = targetPoints - currentPoints;

                    finalText.append("      |      Sıradaki Rozet:  ").append(nextBadgeName);
                    finalText.append(" (").append(pointsNeeded).append(" puan kaldı)");
                }
                else {

                    finalText.append("      |      Sıradaki Rozet:  En iyi rozete sahipsiniz.");
                }

                lblMyBadge.setText(finalText.toString());
            }
        }
        catch(SQLException ex) {

            lblMyBadge.setText("Rozet bilgisi alınamadı!");
            JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
        }
    }

    private void checkCompletedEvents() {

        try(Connection conn = DBHelper.getConnection()) {

            String sql = "UPDATE event_participants ep SET status = 'Tamamlandı' FROM events e WHERE ep.event_id = e.event_id AND ep.user_id = ? AND ep.status = 'Kayıt Yapıldı' AND e.event_date < CURRENT_TIMESTAMP";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, currentUserId);

            int rows = ps.executeUpdate();

            if(rows > 0) {

                JOptionPane.showMessageDialog(this, rows + " etkinlik tamamlandı, puanlar yüklendi.");
                updateMyBadge();
                loadLeaderboard();
                loadProfileData();
            }
        }
        catch(SQLException ex) {

            JOptionPane.showMessageDialog(this, "VERİTABANI HATASI: " + ex.getMessage());
        }
    }
}