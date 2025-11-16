package com.safetransfer.pix;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class PixService {

    private final Map<String, String> cadastros = Map.of(
            "111.222.333-44", "Fulano da Silva",
            "fulano@example.com", "Fulano da Silva",
            "+55 11 98877-6655", "Beltrano Souza",
            "123e4567-e89b-12d3-a456-426614174000", "Empresa XYZ LTDA"
    );

    public Map<String, String> listarCadastros() {
        return Collections.unmodifiableMap(cadastros);
    }

    public PixLookupResponse buscarPorChave(String chavePix) {
        String nome = Optional.ofNullable(chavePix)
                .map(String::trim)
                .filter(c -> !c.isEmpty())
                .map(cadastros::get)
                .orElse(null);

        if (nome == null) {
            return null;
        }
        return new PixLookupResponse(chavePix, nome);
    }

    public PixValidationResponse validar(PixValidationRequest request) {
        String chave = Optional.ofNullable(request.chavePix()).map(String::trim).orElse("");

        if (chave.isEmpty()) {
            return new PixValidationResponse("INVÁLIDO", "Informe a chave PIX", null);
        }

        PixLookupResponse encontrado = buscarPorChave(chave);
        if (encontrado == null) {
            return new PixValidationResponse("INVÁLIDO", "Chave PIX não encontrada", null);
        }

        return new PixValidationResponse("VÁLIDO", "Chave PIX confirmada com sucesso", encontrado.nomeReal());
    }
}
