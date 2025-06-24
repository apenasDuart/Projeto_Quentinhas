package EstoqueQuentinha;

import java.util.ArrayList;
import java.util.HashMap;

public class GerenciadorUsuarios {
    private HashMap<String, String> usuarios = new HashMap<>();
    private ArrayList<String> presets = new ArrayList<>();

    public GerenciadorUsuarios() {
        // Usuários padrão
        usuarios.put("root", "admin");        // Admin
        usuarios.put("funcionario", "123");   // Funcionário

        // Presets iniciais
        presets.add("Feijoada");
        presets.add("Arroz com Frango");
    }

    public boolean autenticar(String usuario, String senha) {
        return usuarios.containsKey(usuario) && usuarios.get(usuario).equals(senha);
    }

    public boolean isAdmin(String usuario) {
        return usuario.equals("root");
    }

    public boolean criarFuncionario(String usuario, String senha) {
    if (usuarios.containsKey(usuario)) {
        return false; // Já existe
    }
    usuarios.put(usuario, senha);
    return true; // Criado com sucesso
}

    // Presets de pratos
    public ArrayList<String> getPresets() {
        return new ArrayList<>(presets);
    }

    public void adicionarPreset(String nome) {
        if (!presets.contains(nome)) {
            presets.add(nome);
        }
    }

    public void editarPreset(String atual, String novo) {
        int index = presets.indexOf(atual);
        if (index != -1 && !presets.contains(novo)) {
            presets.set(index, novo);
        }
    }

    public void removerPreset(String nome) {
        presets.remove(nome);
    }
}
