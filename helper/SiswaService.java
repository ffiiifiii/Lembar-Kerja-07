package LembarKerja7.helper;

import java.io.IOException;
import java.util.List;

public class SiswaService {
    private final SiswaRepository repository;

    public SiswaService(SiswaRepository repository) {
        this.repository = repository;
    }

    public List<Siswa> ambilSemuaSiswa() throws IOException {
        return repository.bacaSemua();
    }

    public void tambahSiswa(Siswa siswaBaru) throws DuplicateNisException, IOException {
        List<Siswa> daftar = repository.bacaSemua();
        for (Siswa s : daftar) {
            if (s.getNis().equals(siswaBaru.getNis())) {
                throw new DuplicateNisException("Gagal: Data dengan NIS " + siswaBaru.getNis() + " sudah terdaftar!");
            }
        }
        daftar.add(siswaBaru);
        repository.simpanSemua(daftar);
    }

    public void updateSiswa(String nis, String namaBaru, String alamatBaru) throws IOException {
        List<Siswa> daftar = repository.bacaSemua();
        for (Siswa s : daftar) {
            if (s.getNis().equals(nis)) {
                s.setNama(namaBaru);
                s.setAlamat(alamatBaru);
                repository.simpanSemua(daftar);
                return;
            }
        }
    }

    public void hapusSiswa(String nis) throws IOException {
        List<Siswa> daftar = repository.bacaSemua();
        daftar.removeIf(s -> s.getNis().equals(nis));
        repository.simpanSemua(daftar);
    }
}