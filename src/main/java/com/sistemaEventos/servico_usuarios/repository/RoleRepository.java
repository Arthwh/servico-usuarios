package com.sistemaEventos.servico_usuarios.repository;

import com.sistemaEventos.servico_usuarios.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    /**
     * Busca uma {@link Role} pelo seu nome exato (ex: "ROLE_USER").
     *
     * @param name O nome exato da role a ser buscada.
     * @return Um {@link Optional} contendo a {@link Role} se encontrada,
     * ou {@link Optional#empty()} caso contrário.
     */
    Optional<Role> findByName(String name);
}
