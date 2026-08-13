package com.investigacion.ejemplo.springejem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.investigacion.ejemplo.springejem.model.Empleado;
import com.investigacion.ejemplo.springejem.services.EmpleadoService;

@Controller
@RequestMapping("/empleados")
public class EmpleadoViewController {

    private final EmpleadoService empleadoService;

    public EmpleadoViewController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public String listar(Model model) {

        model.addAttribute(
                "empleados",
                empleadoService.listarTodos()
        );

        return "empleados";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        model.addAttribute(
                "empleado",
                new Empleado()
        );

        return "formulario-empleado";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Empleado empleado) {

        empleadoService.guardar(empleado);

        return "redirect:/empleados";
    }

    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model model) {

        Empleado empleado =
                empleadoService.buscarPorId(id);

        model.addAttribute(
                "empleado",
                empleado
        );

        return "formulario-empleado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(
            @PathVariable Long id) {

        empleadoService.eliminar(id);

        return "redirect:/empleados";
    }
}