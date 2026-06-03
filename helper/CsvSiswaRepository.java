package LembarKerja7.helper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvSiswaRepository implements SiswaRepository {
    private final String filePath;

    public CsvSiswaRepository(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<Siswa> bacaSemua() throws IOException {
        List<Siswa> daftarSiswa = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return daftarSiswa;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    daftarSiswa.add(new Siswa(data[0].trim(), data[1].trim(), data[2].trim()));
                }
            }
        }
        return daftarSiswa;
    }

    @Override
    public void simpanSemua(List<Siswa> daftarSiswa) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Siswa s : daftarSiswa) {
                bw.write(s.getNis() + "," + s.getNama() + "," + s.getAlamat());
                bw.newLine();
            }
        }
    }
}