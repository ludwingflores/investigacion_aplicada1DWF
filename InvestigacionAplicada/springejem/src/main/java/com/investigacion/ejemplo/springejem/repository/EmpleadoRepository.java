package com.investigacion.ejemplo.springejem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.investigacion.ejemplo.springejem.model.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
}