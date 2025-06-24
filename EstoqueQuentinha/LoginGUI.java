package EstoqueQuentinha;

import javax.swing.*;
import java.awt.*;

public class LoginGUI extends JFrame {
    private GerenciadorUsuarios gerenciador;

    public LoginGUI() {
        gerenciador = new GerenciadorUsuarios();

        setTitle("Login - Controle de Estoque");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Administrador", adminPanel());
        tabs.addTab("Funcionário", funcionarioPanel());

        add(tabs);
        setVisible(true);
    }

    private JPanel adminPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2));
        JTextField nomeField = new JTextField();
        JPasswordField senhaField = new JPasswordField();

        JButton btnLogin = new JButton("Entrar");

        panel.add(new JLabel("Usuário:"));
        panel.add(nomeField);
        panel.add(new JLabel("Senha:"));
        panel.add(senhaField);
        panel.add(new JLabel());
        panel.add(btnLogin);

        btnLogin.addActionListener(e -> {
            String nome = nomeField.getText().trim();
            String senha = new String(senhaField.getPassword());

            if (gerenciador.autenticar(nome, senha) && gerenciador.isAdmin(nome)) {
                JOptionPane.showMessageDialog(this, "Login bem-sucedido como Administrador.");
                new EstoqueGUI(nome, gerenciador); // nome do usuário e gerenciador
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Credenciais inválidas.");
            }
        });

        return panel;
    }

    private JPanel funcionarioPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        JTextField nomeField = new JTextField();
        JPasswordField senhaField = new JPasswordField();

        JButton btnLogin = new JButton("Entrar");
        JButton btnCriar = new JButton("Criar Conta");

        panel.add(new JLabel("Usuário:"));
        panel.add(nomeField);
        panel.add(new JLabel("Senha:"));
        panel.add(senhaField);
        panel.add(btnLogin);
        panel.add(btnCriar);

        btnLogin.addActionListener(e -> {
            String nome = nomeField.getText().trim();
            String senha = new String(senhaField.getPassword());

            if (gerenciador.autenticar(nome, senha) && !gerenciador.isAdmin(nome)) {
                JOptionPane.showMessageDialog(this, "Login bem-sucedido como Funcionário.");
                new EstoqueGUI(nome, gerenciador); // nome do usuário e gerenciador
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Credenciais inválidas.");
            }
        });

        btnCriar.addActionListener(e -> {
            String novoNome = JOptionPane.showInputDialog(this, "Novo nome de usuário:");
            String novaSenha = JOptionPane.showInputDialog(this, "Nova senha:");
            String senhaAdmin = JOptionPane.showInputDialog(this, "Digite a senha ROOT:");

            if (!"RootQuentinhas".equals(senhaAdmin)) {
                JOptionPane.showMessageDialog(this, "Senha ROOT incorreta.");
                return;
            }

            if (gerenciador.criarFuncionario(novoNome, novaSenha)) {
                JOptionPane.showMessageDialog(this, "Funcionário criado com sucesso.");
            } else {
                JOptionPane.showMessageDialog(this, "Usuário já existe.");
            }
        });

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginGUI::new);
    }
}
