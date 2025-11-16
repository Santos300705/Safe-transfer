package com.safetransfer.pix;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PixController {

    private final PixService pixService;

    public PixController(PixService pixService) {
        this.pixService = pixService;
    }

    @GetMapping("/pix/{chave}")
    public ResponseEntity<?> buscarPorChave(@PathVariable("chave") String chavePix) {
        PixLookupResponse response = pixService.buscarPorChave(chavePix);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse.single("error", "Chave PIX não encontrada"));
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validar-pix")
    public ResponseEntity<?> validar(@RequestBody PixValidationRequest request) {
        List<ValidationError> errors = validarCampos(request);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(errors));
        }
        PixValidationResponse resposta = pixService.validar(request);
        HttpStatus status = "VÁLIDO".equals(resposta.status()) ? HttpStatus.OK : HttpStatus.UNPROCESSABLE_ENTITY;
        return ResponseEntity.status(status).body(resposta);
    }

    private List<ValidationError> validarCampos(PixValidationRequest request) {
        List<ValidationError> erros = new ArrayList<>();
        if (request.chavePix() == null || request.chavePix().trim().isEmpty()) {
            erros.add(new ValidationError("chavePix", "Informe a chave PIX"));
        }
        if (request.nomeInformado() == null || request.nomeInformado().trim().isEmpty()) {
            erros.add(new ValidationError("nomeInformado", "Informe o nome para validação"));
        }
        return erros;
    }

    public record ErrorResponse(List<ValidationError> errors) {
        static ErrorResponse single(String field, String message) {
            List<ValidationError> list = new ArrayList<>();
            list.add(new ValidationError(field, message));
            return new ErrorResponse(list);
        }
    }

    public record ValidationError(String field, String message) { }
}
