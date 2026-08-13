package com.investigacion.ejemplo.springejem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.investigacion.ejemplo.springejem.model.Empleado;
import com.investigacion.ejemplo.springejem.services.EmpleadoService;

// Un objeto persistido en Java es una instancia de clase cuyos datos no se pierden al cerrar el programa.
// Se guardan en un lugar seguro como una base de datos o un archivo usando herramientas como JPA, Hibernate o JDBC

@RestController
@RequestMapping("/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    // 1. TRANSIENT
    // ENTIDAD QUE ACABA DE SER CREADO PERO NO HA SIDO ALMACENADA NI ADMINISTRADA, EVITA QUE SE GUARDE EN LA DB (SOLO EN MEMORIA)
    @GetMapping("/jpa/transient")
    public ResponseEntity<Empleado> transientState() {

        Empleado empleado =
                empleadoService.demostrarTransient();

        return ResponseEntity.ok(empleado);
    }

    // 2. PERSIST / MANAGED
    // CUALQUIER CAMBIO REALIZADO EN EL OBJETO SE SINCRONIZA CON LA DB 
    // Si modificás el objeto con un método set, el framework detecta el cambio sin necesidad de llamar a un método de actualización explícito
    // Los cambios se guardan en la base de datos al ejecutar commit() o realizar un flush()

    @PostMapping("/jpa/persist")
    public ResponseEntity<Empleado> persistState() {

        Empleado empleado =
                empleadoService.demostrarPersist();

        return ResponseEntity.ok(empleado);
    }

    // 3. FIND
    // ENCUENTRA LE ENTIDAD EN LA BASE DE DATOS

    @GetMapping("/jpa/find/{id}")
    public ResponseEntity<Empleado> findState(
            @PathVariable Long id) {

        Empleado empleado =
                empleadoService.demostrarFind(id);

        if (empleado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(empleado);
    }

    // 4. MODIFICACIÓN
    // SE EDITA/REALIZA UN CAMBIO EN LA ENTIDAD (SE ACTUALIZA EN LA DB PORQUE LA ENTIDAD ESTA EN ESTADO MANAGED)

    @PutMapping("/jpa/modificar/{id}")
    public ResponseEntity<Empleado> modificarState(
            @PathVariable Long id) {

        Empleado empleado =
                empleadoService.demostrarModificacion(id);

        if (empleado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(empleado);
    }

    // 5. DETACH
    // SE DESVINCULA LA ENTIDAD DEL CONTEXTO DE PERSISTENCIA. LA ENTIDAD SIGUE EXISTIENDO EN MEMORIA
    // PERO JPA DEJA DE GESTIONAR ESA INSTANCIA

    @GetMapping("/jpa/detach/{id}")
    public ResponseEntity<Empleado> detachState(
            @PathVariable Long id) {

        Empleado empleado =
                empleadoService.demostrarDetach(id);

        if (empleado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(empleado);
    }

    // 6. MERGE
    // SE VUELVE A UNIR LA ENTIDAD DESVINCULADA (DETACHED) A LA DB, AHORA VUELVE A SINCRONIZAR SUS DATOS

    @PutMapping("/jpa/merge/{id}")
    public ResponseEntity<Empleado> mergeState(
            @PathVariable Long id) {

        Empleado empleado =
                empleadoService.demostrarMerge(id);

        if (empleado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(empleado);
    }

    // 7. REFRESH
    // VUELVE A CARGAR EL ESTADO DE UNA ENTIDAD MANAGED DESDE LA BASE DE DATOS, DESCARTANDO LOS CAMBIOS LOCALES NO SINCRONIZADOS.
    // El método refresh() en Java JPA sirve para sincronizar el estado de una entidad en memoria con los datos
    // reales de la base de datos, sobrescribiendo cualquier cambio local no guardado

    @GetMapping("/jpa/refresh/{id}")
    public ResponseEntity<Empleado> refreshState(
            @PathVariable Long id) {

        Empleado empleado =
                empleadoService.demostrarRefresh(id);

        if (empleado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(empleado);
    }


    // 8. REMOVE
    // ELIMINA LA ENTIDAD COMPLETAMENTE

    @DeleteMapping("/jpa/remove/{id}")
    public ResponseEntity<String> removeState(
            @PathVariable Long id) {

        Empleado empleado =
                empleadoService.buscarPorId(id);

        if (empleado == null) {
            return ResponseEntity.notFound().build();
        }

        empleadoService.demostrarRemove(id);

        return ResponseEntity.ok(
                "Empleado eliminado correctamente."
        );
    }

}