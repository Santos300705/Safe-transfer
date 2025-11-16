package com.safetransfer.pix;

public record PixValidationRequest(String chavePix, String nomeInformado, Long usuarioId) {
}
