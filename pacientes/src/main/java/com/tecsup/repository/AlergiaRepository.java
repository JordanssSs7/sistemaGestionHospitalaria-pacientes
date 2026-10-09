package com.tecsup.repository;

import com.tecsup.model.Alergia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlergiaRepository extends JpaRepository<Alergia, Integer> {

    // Quita la alergia de todos los pacientes que la tienen (tabla intermedia paciente_alergia)
    @Modifying
    @Query(value = "DELETE FROM paciente_alergia WHERE id_alergia = :idAlergia", nativeQuery = true)
    void quitarDePacientes(@Param("idAlergia") Integer idAlergia);
}
