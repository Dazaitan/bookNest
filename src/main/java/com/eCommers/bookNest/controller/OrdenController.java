package com.eCommers.bookNest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ordenes")
public class OrdenController {
    @GetMapping("/1")
    public String libroGet(){
        return "Hola mundo desde el controlador de ordenes";
    }
}
