package py.edu.uc.lp3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import py.edu.uc.lp3.domain.Entidad;

public interface EntidadRepository extends JpaRepository<Entidad, Long> {

    List<Entidad> findByVidaGreaterThan(int vida);
}
