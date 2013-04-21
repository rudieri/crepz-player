package com.hotkey.linux;

/**
 *
 * @author c90
 */
public enum TipoComando {
    ABRIR_CREPZ("--open"),
    REPRODUZIR_MUSICA("--play"),
    PAUSAR_MUSICA("--pause"),
    PARAR_MUSICA("--stop"),
    AVANCAR_MUSICA("--next"),
    VOLTAR_MUSICA("--prev"),
    OBTER_LISTA("--list-music"),
    ADICIONAR_LISTA("--add");
    private final String comando;

    private TipoComando(String comando) {
        this.comando = comando;
    }

    public String getComando() {
        return comando;
    }
    public static TipoComando getPorComando(String comando){
        for (TipoComando tipoComando : values()) {
            if (tipoComando.getComando().equals(comando)) {
                return tipoComando;
            }
        }
        return null;
    }
    
}
