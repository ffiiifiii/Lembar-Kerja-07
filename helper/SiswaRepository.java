package LembarKerja7.helper;

import java.io.IOException;
import java.util.List;

public interface SiswaRepository {
    List<Siswa> bacaSemua() throws IOException;
    void simpanSemua(List<Siswa> daftarSiswa) throws IOException;
}