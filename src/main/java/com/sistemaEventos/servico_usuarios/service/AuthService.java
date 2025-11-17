package com.sistemaEventos.servico_usuarios.service;

import com.sistemaEventos.servico_usuarios.dto.UserLoginDTO;
import com.sistemaEventos.servico_usuarios.model.User;
import com.sistemaEventos.servico_usuarios.repository.UserRepository;
import com.sistemaEventos.servico_usuarios.config.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pela lógica de autenticação de usuários.
 * <p>
 * Este serviço valida as credenciais do usuário (e-mail e senha) e, se forem válidas,
 * utiliza o {@link JwtService} para gerar um token de acesso (JWT).
 */
@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    /**
     * Tenta autenticar um usuário com base no e-mail e senha fornecidos.
     *
     * @param dto O Data Transfer Object (DTO) contendo o e-mail e a senha do usuário.
     * @return Uma string representando o token JWT gerado se a autenticação for bem-sucedida.
     * @throws BadCredentialsException se o e-mail não for encontrado ou se a senha
     * não corresponder.
     */
    public String login(UserLoginDTO dto){
        User user = userRepository.findActiveUserByEmail(dto.email())
                .orElseThrow(() -> new BadCredentialsException("Email ou senha inválidos"));

        //Verifica a senha
        if (passwordEncoder.matches(dto.password(), user.getPassword())) {

            //Gera o token JWT
            return jwtService.gerarToken(user);
        }

        throw new BadCredentialsException("Email ou senha inválidos");
    }
}
