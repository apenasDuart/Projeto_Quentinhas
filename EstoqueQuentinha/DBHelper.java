package EstoqueQuentinha;

import java.sql.*;

public class DBHelper {
    private static final String DB_URL = "jdbc:sqlite:estoque.db";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void inicializarBD() {
        try (Connection conn = conectar(); Statement stmt = conn.createStatement()) {
            // Tabela de produtos
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS produtos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL UNIQUE,
                    quantidade INTEGER NOT NULL
                );
            """);

            // Tabela de usuários
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS usuarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    senha TEXT NOT NULL,
                    tipo TEXT NOT NULL
                );
            """);

            // Tabela de logs
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    usuario TEXT,
                    acao TEXT,
                    produto TEXT,
                    quantidade INTEGER,
                    datahora TEXT
                );
            """);

            // Criação do usuário Root padrão
            stmt.execute("""
                INSERT OR IGNORE INTO usuarios (username, senha, tipo)
                VALUES ('Root', 'RootQuentinhas', 'admin');
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
