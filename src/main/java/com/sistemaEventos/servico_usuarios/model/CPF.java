package com.sistemaEventos.servico_usuarios.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.sistemaEventos.servico_usuarios.exception.CpfInvalidException;

/**
 * Representa o Cadastro de Pessoa Física (CPF).
 * <p>
 * Esta classe encapsula a lógica de negócio de um CPF, garantindo que
 * uma instância desta classe só possa existir se o CPF for válido.
 * <p>
 * Ela é projetada para se integrar perfeitamente com o Jackson (para DTOs)
 * e o JPA (para Entidades), tratando o CPF como um tipo de dado próprio,
 * e não apenas como uma {@link String}.
 */
public class CPF {
    /**
     * Armazena o valor do CPF (apenas números) após a validação.
     */
    private final String cpf;

    /**
     * Construtor principal e ponto de desserialização do Jackson.
     * <p>
     * Este método é anotado com {@link JsonCreator}, instruindo o Jackson
     * a usá-lo quando encontrar um campo "cpf" em um JSON e precisar
     * convertê-lo de String para um objeto {@code CPF}.
     * <p>
     * A validação é executada imediatamente no momento da criação.
     *
     * @param cpf O CPF como uma String de 11 dígitos (sem formatação).
     * @throws CpfInvalidException se o CPF for nulo, tiver um formato
     * inválido, for uma sequência de números repetidos ou falhar no
     * cálculo dos dígitos verificadores.
     */
    @JsonCreator
    public CPF(String cpf) {
        if (cpf == null || !validateCpf(cpf)) {
            throw new CpfInvalidException("CPF inválido, " + cpf);
        }
        this.cpf = cpf;
    }

    /**
     * Retorna o valor do CPF (apenas números).
     * <p>
     * Anotado com {@link JsonValue}, instruindo o Jackson a usar este
     * método para serializar o objeto {@code CPF} de volta para uma
     * String simples no JSON de resposta.
     *
     * @return A String de 11 dígitos do CPF.
     */
    @JsonValue
    public String getCpf() {
        return cpf;
    }

    /**
     * Retorna o CPF em seu formato de exibição padrão (ex: "***.***.***-**").
     *
     * @return O CPF formatado como String.
     */
    public String getCpfFormatted() {
        return formatCpf(cpf);
    }

    /**
     * Método auxiliar privado para formatar a string do CPF.
     *
     * @param cpf O CPF de 11 dígitos.
     * @return O CPF formatado.
     */
    private String formatCpf(String cpf) {
        String cpfFormatted = String.valueOf(cpf);
        return (cpfFormatted.substring(0, 3) + "." + cpfFormatted.substring(3, 6) + "." +
                cpfFormatted.substring(6, 9) + "-" + cpfFormatted.substring(9, 11));
    }

    /**
     * Executa o algoritmo oficial de validação de CPF (Módulo 11).
     * <p>
     * Este método verifica:
     * <p>
     * - Se o CPF não é uma sequência de números repetidos.
     * <p>
     * - Se o tamanho é exatamente 11.
     * <p>
     * - Se o 1º e o 2º dígitos verificadores estão corretos.
     *
     * @param cpf A string do CPF a ser validada (apenas números).
     * @return {@code true} se o CPF for válido, {@code false} caso contrário.
     */
    private boolean validateCpf(String cpf) {
        if (cpf.equals("00000000000") ||
                cpf.equals("11111111111") ||
                cpf.equals("22222222222") || cpf.equals("33333333333") ||
                cpf.equals("44444444444") || cpf.equals("55555555555") ||
                cpf.equals("66666666666") || cpf.equals("77777777777") ||
                cpf.equals("88888888888") || cpf.equals("99999999999") ||
                (cpf.length() != 11))
            return false;

        char dig10, dig11;
        int sm, i, r, num, peso;

        // Cálculo do 1o. Dígito Verificador
        sm = 0;
        peso = 10;
        for (i = 0; i < 9; i++) {
            num = (int) (cpf.charAt(i) - 48);
            sm = sm + (num * peso);
            peso = peso - 1;
        }

        r = 11 - (sm % 11);
        if ((r == 10) || (r == 11))
            dig10 = '0';
        else dig10 = (char) (r + 48);

        // Cálculo do 2o. Dígito Verificador
        sm = 0;
        peso = 11;
        for (i = 0; i < 10; i++) {
            num = (int) (cpf.charAt(i) - 48);
            sm = sm + (num * peso);
            peso = peso - 1;
        }

        r = 11 - (sm % 11);
        if ((r == 10) || (r == 11))
            dig11 = '0';
        else dig11 = (char) (r + 48);

        // Verifica se os dígitos calculados conferem com os dígitos informados.
        return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));
    }
}
