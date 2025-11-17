package com.sistemaEventos.servico_usuarios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Representa uma permissão de segurança (Role) no sistema.
 * <p>
 * Esta entidade define os níveis de acesso que um {@link User} pode ter
 * (ex: "ROLE_USER", "ROLE_ADMIN"). É usada para controlar a autorização
 * no sistema.
 */
@Entity
@Table(name = "roles")
public class Role {
    /**
     * O identificador numérico único (SERIAL) da role.
     */
    @Id
    private Integer id;
    /**
     * O nome único da role, que é usado pela lógica de segurança.
     * (Convenção: "ROLE_USER", "ROLE_ADMIN").
     */
    @Column(unique = true, nullable = false)
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
