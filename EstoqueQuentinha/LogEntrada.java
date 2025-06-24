package EstoqueQuentinha;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntrada {
    private String tipo;
    private String nomeProduto;
    private int quantidade;
    private String usuario;
    private LocalDateTime dataHora;

    public LogEntrada(String tipo, String nomeProduto, int quantidade, String usuario) {
        this.tipo = tipo;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.usuario = usuario;
        this.dataHora = LocalDateTime.now();
    }

    public String getTextoFormatado() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return String.format("[%s] %s - %s %d unidades de \"%s\"", 
                dataHora.format(formatter), usuario, tipo, quantidade, nomeProduto);
    }
}
