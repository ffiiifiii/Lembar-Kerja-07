package LembarKerja7;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import LembarKerja7.helper.*; 

public class gui extends JFrame {
    private DefaultTableModel tableModel;
    private final SiswaService siswaService;
    private final String FILE_NAME = "LembarKerja7/data/siswa.csv"; 

    private Color bgUtama = new Color(240, 248, 255);
    private Color teksJudul = new Color(25, 25, 112);
    private Color bgTombol = new Color(135, 206, 235);
    private Color bgTabelHeader = new Color(173, 216, 230);

    public gui() {
        SiswaRepository repository = new CsvSiswaRepository(FILE_NAME);
        this.siswaService = new SiswaService(repository);

        UIManager.put("OptionPane.background", bgUtama);
        UIManager.put("Panel.background", bgUtama);

        setTitle("Menu Utama - Perpustakaan SMP");
        setSize(480, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgUtama);

        JPanel panelAtas = new JPanel(new GridLayout(2, 1));
        panelAtas.setBackground(bgUtama);
        panelAtas.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel lblWelcome = new JLabel("✨ Selamat Datang di Sistem Perpustakaan! ✨", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblWelcome.setForeground(teksJudul);

        JLabel lblSub = new JLabel("Silakan pilih menu di bawah ini:", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(teksJudul);

        panelAtas.add(lblWelcome);
        panelAtas.add(lblSub);
        add(panelAtas, BorderLayout.NORTH);

        JPanel panelMenu = new JPanel(new GridLayout(2, 2, 15, 15));
        panelMenu.setBackground(bgUtama);
        panelMenu.setBorder(BorderFactory.createEmptyBorder(10, 30, 40, 30));

        JButton btnLihat = styleButton("📄 Lihat Semua Data");
        JButton btnCreate = styleButton("➕ Tambah Data Baru");
        JButton btnUpdate = styleButton("✏️ Update Data");
        JButton btnDelete = styleButton("🗑️ Hapus Data");

        btnCreate.setBackground(new Color(153, 255, 153));
        btnUpdate.setBackground(new Color(255, 255, 153));
        btnDelete.setBackground(new Color(255, 153, 153));

        panelMenu.add(btnLihat);
        panelMenu.add(btnCreate);
        panelMenu.add(btnUpdate);
        panelMenu.add(btnDelete);

        add(panelMenu, BorderLayout.CENTER);

        String[] kolom = { "NIS", "Nama Siswa", "Alamat" };
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        checkAndLoadData();

        btnLihat.addActionListener(e -> showDataDialog());
        btnCreate.addActionListener(e -> showCreateDialog());
        btnUpdate.addActionListener(e -> showUpdateDialog());
        btnDelete.addActionListener(e -> showDeleteDialog());
    }

    private JButton styleButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgTombol);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void refreshTableData() {
        tableModel.setRowCount(0);
        try {
            for (Siswa s : siswaService.ambilSemuaSiswa()) {
                tableModel.addRow(new Object[]{ s.getNis(), s.getNama(), s.getAlamat() });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data tabel: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void showDataDialog() {
        refreshTableData();
        JTable tabelSiswa = new JTable(tableModel);
        tabelSiswa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabelSiswa.setRowHeight(25);
        tabelSiswa.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabelSiswa.getTableHeader().setBackground(bgTabelHeader);

        JScrollPane scrollPane = new JScrollPane(tabelSiswa);
        scrollPane.setPreferredSize(new Dimension(500, 250));

        JOptionPane.showMessageDialog(this, scrollPane, "Data Semua Siswa", JOptionPane.PLAIN_MESSAGE);
    }

    private void showCreateDialog() {
        JTextField txtNis = new JTextField();
        JTextField txtNama = new JTextField();
        JTextField txtAlamat = new JTextField();

        Object[] formFields = {
                "📌 Masukkan NIS:", txtNis,
                "👤 Masukkan Nama Siswa:", txtNama,
                "🏠 Masukkan Alamat:", txtAlamat
        };

        int option = JOptionPane.showConfirmDialog(this, formFields, "Tambah Data Siswa", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String nis = txtNis.getText().trim();
            String nama = txtNama.getText().trim();
            String alamat = txtAlamat.getText().trim();

            if (nis.isEmpty() || nama.isEmpty() || alamat.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua kolom harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                siswaService.tambahSiswa(new Siswa(nis, nama, alamat));
                refreshTableData();
                JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (DuplicateNisException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan sistem: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUpdateDialog() {
        String inputNis = JOptionPane.showInputDialog(this, "Masukkan NIS siswa yang ingin di-update:");
        if (inputNis != null && !inputNis.trim().isEmpty()) {
            inputNis = inputNis.trim();
            int rowIndex = findRowByNis(inputNis);
            if (rowIndex != -1) {
                JTextField txtNama = new JTextField(tableModel.getValueAt(rowIndex, 1).toString());
                JTextField txtAlamat = new JTextField(tableModel.getValueAt(rowIndex, 2).toString());

                Object[] formFields = {
                        "📌 NIS: " + inputNis + " (Tidak dapat diubah)",
                        "👤 Nama Siswa Baru:", txtNama,
                        "🏠 Alamat Baru:", txtAlamat
                };

                int option = JOptionPane.showConfirmDialog(this, formFields, "Update Data", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (option == JOptionPane.OK_OPTION) {
                    try {
                        siswaService.updateSiswa(inputNis, txtNama.getText().trim(), txtAlamat.getText().trim());
                        refreshTableData();
                        JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Gagal memperbarui data!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Data dengan NIS " + inputNis + " tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showDeleteDialog() {
        String inputNis = JOptionPane.showInputDialog(this, "Masukkan NIS siswa yang ingin dihapus:");
        if (inputNis != null && !inputNis.trim().isEmpty()) {
            inputNis = inputNis.trim();
            int rowIndex = findRowByNis(inputNis);
            if (rowIndex != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Hapus data '" + tableModel.getValueAt(rowIndex, 1) + "'?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        siswaService.hapusSiswa(inputNis);
                        refreshTableData();
                        JOptionPane.showMessageDialog(this, "Data berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Gagal menghapus data!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int findRowByNis(String nis) {
        refreshTableData();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).toString().equals(nis)) return i;
        }
        return -1;
    }

    private void checkAndLoadData() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            int pilihan = JOptionPane.showConfirmDialog(
                this, 
                "File data (siswa.csv) belum ditemukan.\n" +
                "Sistem akan membuat file baru secara otomatis saat Anda menyimpan data nanti.\n\n" +
                "Tetap masuk ke sistem?", 
                "Informasi File", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.INFORMATION_MESSAGE
            );

            if (pilihan != JOptionPane.YES_OPTION) {
                System.exit(0);
            }
            return;
        }
        refreshTableData();
    }

    public static void main(String[] args) {
        try {
            if (!System.getProperty("os.name").toLowerCase().contains("mac")) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) { e.printStackTrace(); }

        SwingUtilities.invokeLater(() -> new gui().setVisible(true));
    }
}