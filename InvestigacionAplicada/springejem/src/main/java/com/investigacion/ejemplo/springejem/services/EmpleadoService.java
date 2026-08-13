package com.investigacion.ejemplo.springejem.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.investigacion.ejemplo.springejem.model.Empleado;
import com.investigacion.ejemplo.springejem.repository.EmpleadoRepository;

import java.util.List;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    // CRUD NORMAL

    public List<Empleado> listarTodos() {
        return empleadoRepository.findAll();
    }

    public Empleado buscarPorId(Long id) {
        return empleadoRepository.findById(id).orElse(null);
    }

    public Empleado guardar(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    public void eliminar(Long id) {
        empleadoRepository.deleteById(id);
    }

    // 1. TRANSIENT
    @Transactional
    public Empleado demostrarTransient() {

        Empleado empleado = new Empleado(
                "Juan",
                "Pérez",
                "Programador",
                650.00
        );

        System.out.println("--- 1. ESTADO TRANSIENT ---");
        System.out.println("Empleado creado.");
        System.out.println("Nombre: " + empleado.getNombre());
        System.out.println("Apellido: " + empleado.getApellido());
        System.out.println("Puesto: " + empleado.getPuesto());
        System.out.println("ID: " + empleado.getId());


        // NO hacemos persist()
        // Por eso la entidad sigue siendo TRANSIENT.
        return empleado;
    }

    // 2. PERSIST / MANAGED
    @Transactional
    public Empleado demostrarPersist() {

        Empleado empleado = new Empleado(
                "Juan",
                "Pérez",
                "Programador",
                650.00
        );

        System.out.println("2. PERSIST / MANAGED");
        System.out.println("Antes de persist:");
        System.out.println("Nombre: " + empleado.getNombre());
        System.out.println("Apellido: " + empleado.getApellido());
        System.out.println("Puesto: " + empleado.getPuesto());
        System.out.println("ID: " + empleado.getId());

        // Nuevo a Managed: Se logra al invocar entityManager.persist(objeto).
        entityManager.persist(empleado);

        System.out.println("Después de persist:");
        System.out.println("Nombre: " + empleado.getNombre());
        System.out.println("Apellido: " + empleado.getApellido());
        System.out.println("Puesto: " + empleado.getPuesto());
        System.out.println("ID: " + empleado.getId());

        System.out.println("¿El EM contiene el Empleado como MANAGED?: " + entityManager.contains(empleado));

        entityManager.flush();

        return empleado;
    }

    // 3. FIND
    @Transactional
    public Empleado demostrarFind(Long id) {

        System.out.println("3. FIND");

        // se utiliza dentro del EntityManager para buscar un registro por su clave primaria (ID)
        // find() permite obtener una entidad por su identificador. Si existe, la instancia obtenida
        // queda gestionada por el contexto de persistencia
        Empleado empleado = entityManager.find(Empleado.class, id);

        if (empleado == null) {
            return null;
        }

        System.out.println("Empleado encontrado:");
        System.out.println("ID: " + empleado.getId());
        System.out.println("Nombre: " + empleado.getNombre());
        System.out.println("Salario: " + empleado.getSalario());

        System.out.println("¿El EM contiene el Empleado como MANAGED?: " + entityManager.contains(empleado));

        return empleado;
    }

    // 4. MODIFICACIÓN DE MANAGED
    @Transactional
    public Empleado demostrarModificacion(Long id) {

        System.out.println("4. MODIFICACIÓN DE MANAGED");

        Empleado empleado = entityManager.find(Empleado.class, id);

        if (empleado == null) {
            return null;
        }

        System.out.println("Salario anterior: " + empleado.getSalario());

        empleado.setSalario(900.00);

        System.out.println("Nuevo salario: " + empleado.getSalario());

        System.out.println("¿El EM contiene el Empleado como MANAGED?: " + entityManager.contains(empleado));

        entityManager.flush();

        System.out.println("Se realizo un entityManager.flush(); y Hibernate sincronizó el cambio con MySQL.");

        return empleado;
    }

    // 5. DETACH
    @Transactional
    public Empleado demostrarDetach(Long id) {

        Empleado empleado = entityManager.find(Empleado.class, id);

        System.out.println("¿EM contiene el empleado con managed? " + entityManager.contains(empleado));

        // Su función principal es desvincular una entidad del contexto de persistencia actual.
        // el objeto pasa de estar administrado (managed) a estar separado (detached)
        // los cambios que le hagas ya no se guardarán de forma automática en la base de datos.
        entityManager.detach(empleado);

        System.out.println("¿EM contiene el empleado luego de ejecutar detach?: " + entityManager.contains(empleado));

        // Modificamos la entidad DESPUÉS de detach
        empleado.setSalario(2000.00);

        System.out.println("-- Empleado en Memoria --");
        System.out.println("Nombre: " + empleado.getNombre());
        System.out.println("Apellido: " + empleado.getApellido());
        System.out.println("Puesto: " + empleado.getPuesto());
        System.out.println("Salario: " + empleado.getSalario());
        System.out.println("ID: " + empleado.getId());

        entityManager.flush();

        return empleado;
    }


    // 6. MERGE

    @Transactional
    public Empleado demostrarMerge(Long id) {

        System.out.println("6. MERGE");

        Empleado empleado = entityManager.find(Empleado.class, id);

        if (empleado == null) {
            return null;
        }

        // Primero lo hacemos DETACHED
        entityManager.detach(empleado);

        System.out.println("¿EM contiene el empleado luego de ejecutar detach?: " + entityManager.contains(empleado));

        // merge() devuelve una NUEVA referencia gestionada
        // Detached a Managed, se logra al invocar entityManager.merge(objeto) para reconectar una entidad que perdió su vínculo
        // merge() copia el estado de una entidad detached a una instancia gestionada y devuelve esa instancia gestionada
        Empleado empleadoManaged = entityManager.merge(empleado);
        empleadoManaged.setSalario(1200.00);

        System.out.println("¿La entidad original que se gestiono esta contenida en el EM?" + entityManager.contains(empleado));

        System.out.println("Entidad devuelta por merge gestionada: " + entityManager.contains(empleadoManaged));

        System.out.println("-- Empleado Merge --");
        System.out.println("Nombre: " + empleado.getNombre());
        System.out.println("Apellido: " + empleado.getApellido());
        System.out.println("Puesto: " + empleado.getPuesto());
        System.out.println("Salario: " + empleado.getSalario());
        System.out.println("ID: " + empleado.getId());

        entityManager.flush();

        return empleadoManaged;
    }

    // 7. REFRESH

    @Transactional
    public Empleado demostrarRefresh(Long id) {

        System.out.println("7. REFRESH");

        Empleado empleado = entityManager.find(Empleado.class, id);

        if (empleado == null) {
            return null;
        }

        // refresh() vuelve a cargar el estado desde la BD y sobrescribe los cambios que tenías en memoria.
        // VUELVE A CARGAR EL ESTADO DE UNA ENTIDAD MANAGED DESDE LA BASE DE DATOS, DESCARTANDO LOS CAMBIOS LOCALES NO SINCRONIZADOS.

        System.out.println("-- Empleado ANTES DE MODIFICAR --");
        System.out.println("Nombre: " + empleado.getNombre());
        System.out.println("Apellido: " + empleado.getApellido());
        System.out.println("Puesto: " + empleado.getPuesto());
        System.out.println("Salario: " + empleado.getSalario());
        System.out.println("ID: " + empleado.getId());

        empleado.setSalario(9999.99);

        System.out.println("-- Empleado CON SALARIO MODIFICADO --");
        System.out.println("Nombre: " + empleado.getNombre());
        System.out.println("Apellido: " + empleado.getApellido());
        System.out.println("Puesto: " + empleado.getPuesto());
        System.out.println("Salario: " + empleado.getSalario());
        System.out.println("ID: " + empleado.getId());

        entityManager.refresh(empleado);

        System.out.println("-- Empleado LUEGO QUE SE EJECUTARA EL REFRESH --");
        System.out.println("Nombre: " + empleado.getNombre());
        System.out.println("Apellido: " + empleado.getApellido());
        System.out.println("Puesto: " + empleado.getPuesto());
        System.out.println("Salario: " + empleado.getSalario());
        System.out.println("ID: " + empleado.getId());

        return empleado;
    }

    // 8. REMOVE

    @Transactional
    public void demostrarRemove(Long id) {

        System.out.println("8. REMOVE");

        Empleado empleado = entityManager.find(Empleado.class, id);

        if (empleado == null) {
            System.out.println("Empleado no encontrado.");
            return;
        }

        System.out.println("¿Está gestionado antes de remove? " + entityManager.contains(empleado));

        entityManager.remove(empleado);

        System.out.println("Entidad marcada para eliminación.");

        entityManager.flush();

        System.out.println("Empleado eliminado de la base de datos.");
    }
}