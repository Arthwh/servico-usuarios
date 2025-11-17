package com.sistemaEventos.servico_usuarios.model.converter;

import com.sistemaEventos.servico_usuarios.exception.CpfInvalidException;
import com.sistemaEventos.servico_usuarios.model.CPF;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Conversor JPA (AttributeConverter) para o Objeto de Valor {@link CPF}.
 * <p>
 * Esta classe ensina o JPA a "traduzir" o tipo customizado {@code CPF}
 * para um tipo que o banco de dados entende ({@code String}) e vice-versa.
 * <p>
 * A anotação {@link Converter} com {@code autoApply = true} instrui o JPA
 * a aplicar este conversor automaticamente a *todos* os atributos de entidade
 * do tipo {@link CPF} (ex: o campo {@code cpf} na entidade {@code User}),
 * eliminando a necessidade de anotar cada campo individualmente.
 */
//@Converter diz ao JPA que esta é uma classe tradutora
@Converter(autoApply = true)
public class CpfConverter implements AttributeConverter<CPF, String> {
    /**
     * Converte o Objeto de Valor {@link CPF} (lado do Java) em uma {@code String}
     * (lado do banco de dados) para ser persistida.
     *
     * @param cpf O objeto {@link CPF} da entidade.
     * @return A representação em String do CPF (apenas números) ou {@code null} se o
     * objeto CPF for nulo.
     */
    @Override
    public String convertToDatabaseColumn(CPF cpf) {
        if (cpf == null) {
            return null;
        }

        return cpf.getCpf();
    }

    /**
     * Converte a {@code String} lida da coluna do banco de dados de volta
     * para o Objeto de Valor {@link CPF} (lado do Java).
     * <p>
     * Ao chamar o construtor {@code new CPF(dbData)}, este método garante
     * que os dados lidos do banco também passem pela lógica de validação
     * do Objeto de Valor.
     * <p>
     * @param dbData A String (apenas números) lida da coluna do banco.
     * @return Uma nova instância de {@link CPF} ou {@code null} se o dado do
     * banco for nulo.
     * @throws CpfInvalidException (via CPF) se o dado no banco for inválido.
     */
    @Override
    public CPF convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return new CPF(dbData);
    }
}
