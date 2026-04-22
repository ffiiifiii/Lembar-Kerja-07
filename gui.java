package LK07;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;

class DuplicateNisException extends Exception {
    public DuplicateNisException(String message) {
        super(message);
    }
}

public class gui extends JFrame {

    private DefaultTableModel tableModel;
    private final String FILE_NAME = "siswa.csv";

    // --- DEKLARASI WARNA TEMA ---
    private Color bgUtama = new Color(240, 248, 255);
    private Color teksJudul = new Color(25, 25, 112);
    private Color bgTombol = new Color(135, 206, 235);
    private Color bgTabelHeader = new Color(173, 216, 230);

    public gui() {
        // --- SETUP UIMANAGER ---
        UIManager.put("OptionPane.background", bgUtama);
        UIManager.put("Panel.background", bgUtama);

        // 1. Setup Frame Utama (Menu)
        setTitle("Menu Utama - Perpustakaan SMP");
        setSize(480, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgUtama);

        // --- BAGIAN ATAS (Judul) ---
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

        // --- BAGIAN TENGAH (Tombol Menu) ---
        JPanel panelMenu = new JPanel(new GridLayout(2, 2, 15, 15));
        panelMenu.setBackground(bgUtama);
        panelMenu.setBorder(BorderFactory.createEmptyBorder(10, 30, 40, 30));

        JButton btnLihat = styleButton("📄 Lihat Semua Data");
        JButton btnCreate = styleButton("➕ Tambah Data Baru");
        JButton btnUpdate = styleButton("✏️ Update Data");
        JButton btnDelete = styleButton("🗑️ Hapus Data");

        // --- WARNA KHUSUS TOMBOL ---
        btnCreate.setBackground(new Color(153, 255, 153));
        btnUpdate.setBackground(new Color(255, 255, 153));
        btnDelete.setBackground(new Color(255, 153, 153));

        panelMenu.add(btnLihat);
        panelMenu.add(btnCreate);
        panelMenu.add(btnUpdate);
        panelMenu.add(btnDelete);

        add(panelMenu, BorderLayout.CENTER);

        // --- INISIALISASI TABEL ---
        String[] kolom = { "NIS", "Nama Siswa", "Alamat" };
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Load data di awal (Popup muncul di sini jika file belum ada)
        loadDataFromFile();

        // --- EVENT LISTENERS ---
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

    // --- LOGIKA CRUD ---

    private void showDataDialog() {
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
                if (findRowByNis(nis) != -1) {
                    throw new DuplicateNisException("Gagal: Data dengan NIS " + nis + " sudah terdaftar!");
                }
                tableModel.addRow(new Object[] { nis, nama, alamat });
                saveDataToFile();
                JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (DuplicateNisException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUpdateDialog() {
        String inputNis = JOptionPane.showInputDialog(this, "Masukkan NIS siswa yang ingin di-update:");
        if (inputNis != null && !inputNis.trim().isEmpty()) {
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
                    tableModel.setValueAt(txtNama.getText().trim(), rowIndex, 1);
                    tableModel.setValueAt(txtAlamat.getText().trim(), rowIndex, 2);
                    saveDataToFile();
                    JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Data dengan NIS " + inputNis + " tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showDeleteDialog() {
        String inputNis = JOptionPane.showInputDialog(this, "Masukkan NIS siswa yang ingin dihapus:");
        if (inputNis != null && !inputNis.trim().isEmpty()) {
            int rowIndex = findRowByNis(inputNis);
            if (rowIndex != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Hapus data '" + tableModel.getValueAt(rowIndex, 1) + "'?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    tableModel.removeRow(rowIndex);
                    saveDataToFile();
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int findRowByNis(String nis) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).toString().equals(nis)) return i;
        }
        return -1;
    }

    // --- FILE I/O DENGAN POPUP ---

    private void loadDataFromFile() {
        File file = new File(FILE_NAME);
        
        // JIKA FILE TIDAK ADA: Munculkan GUI Popup Konfirmasi
        if (!file.exists()) {
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
                System.exit(0); // Keluar jika user pilih 'No'
            }
            return; // Masuk ke sistem jika user pilih 'Yes'
        }

        // JIKA FILE ADA: Baca datanya
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) tableModel.addRow(data);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal membaca file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveDataToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                bw.write(tableModel.getValueAt(i, 0) + "," + tableModel.getValueAt(i, 1) + "," + tableModel.getValueAt(i, 2));
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
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