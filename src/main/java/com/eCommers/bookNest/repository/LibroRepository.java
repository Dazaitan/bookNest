package com.eCommers.bookNest.repository;

import com.eCommers.bookNest.entity.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroRepository extends JpaRepository<Libro, Long> {

}
