package EstoqueQuentinha;

import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class EstoqueGUI extends JFrame {
    private String nomeUsuario;
    private boolean isAdmin;
    private GerenciadorUsuarios gerenciador;

    private DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Nome", "Quantidade"}, 0);
    private JTable tabela = new JTable(tableModel);

    private ArrayList<Produto> estoque = new ArrayList<>();
    private ArrayList<LogEntrada> logs = new ArrayList<>();

    private JComboBox<String> seletorPresets;

    public EstoqueGUI(String nomeUsuario, GerenciadorUsuarios gerenciador) {
        this.nomeUsuario = nomeUsuario;
        this.gerenciador = gerenciador;
        this.isAdmin = nomeUsuario.equalsIgnoreCase("Root");

        setTitle("Controle de Estoque - Usuário: " + nomeUsuario);
        setSize(600, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Visualizar");
        JMenuItem verLogs = new JMenuItem("Log de Alterações");
        menu.add(verLogs);
        menuBar.add(menu);

        if (isAdmin) {
            JMenu adminMenu = new JMenu("Admin");
            JMenuItem gerenciarPresets = new JMenuItem("Gerenciar Pratos (Presets)");
            JMenuItem gerenciarUsuarios = new JMenuItem("Gerenciar Funcionários");

            gerenciarPresets.addActionListener(e -> gerenciarPresets());
            gerenciarUsuarios.addActionListener(e -> gerenciarUsuarios());

            adminMenu.add(gerenciarPresets);
            adminMenu.add(gerenciarUsuarios);
            menuBar.add(adminMenu);
        }

        setJMenuBar(menuBar);

        verLogs.addActionListener(e -> mostrarLog());

        // Painel de botões
        JPanel painelBotoes = new JPanel();
        JButton btnAdicionar = new JButton("Registrar Entrada/Saída");
        JButton btnRemover = new JButton("Remover Registro");
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnRemover);

        // Seletor de pratos preset
        seletorPresets = new JComboBox<>();
        atualizarPresets();
        painelBotoes.add(seletorPresets);

        // Tabela
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        // Ações
        btnAdicionar.addActionListener(e -> adicionarProduto());
        btnRemover.addActionListener(e -> removerProduto());

        // Carregar estoque salvo
        carregarProdutosDoArquivo();

        setVisible(true);
    }

    private void atualizarPresets() {
        seletorPresets.removeAllItems();
        for (String nome : gerenciador.getPresets()) {
            seletorPresets.addItem(nome);
        }
    }

    private void adicionarProduto() {
        String nome = (String) seletorPresets.getSelectedItem();
        if (nome == null) {
            JOptionPane.showMessageDialog(this, "Nenhum prato disponível.");
            return;
        }

        String qtdStr = JOptionPane.showInputDialog(this, "Quantidade a adicionar/retirar (use negativo para saída):");
        try {
            int qtd = Integer.parseInt(qtdStr);

            Produto existente = null;
            for (Produto p : estoque) {
                if (p.getNome().equals(nome)) {
                    existente = p;
                    break;
                }
            }

            if (existente == null) {
                existente = new Produto(nome, qtd);
                estoque.add(existente);
                tableModel.addRow(new Object[]{nome, qtd});
            } else {
                int novaQtd = existente.getQuantidade() + qtd;
                existente.setQuantidade(novaQtd);
                int index = estoque.indexOf(existente);
                tableModel.setValueAt(novaQtd, index, 1);
            }

            logs.add(new LogEntrada((qtd >= 0 ? "ENTRADA" : "SAÍDA"), nome, Math.abs(qtd), nomeUsuario));

            salvarProdutosEmArquivo();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido.");
        }
    }

    private void removerProduto() {
        int row = tabela.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um item.");
            return;
        }

        Produto p = estoque.get(row);
        logs.add(new LogEntrada("REMOVIDO", p.getNome(), p.getQuantidade(), nomeUsuario));
        estoque.remove(row);
        tableModel.removeRow(row);

        salvarProdutosEmArquivo();
    }

    private void mostrarLog() {
        JTextArea areaTexto = new JTextArea(15, 40);
        areaTexto.setEditable(false);

        StringBuilder sb = new StringBuilder();
        for (LogEntrada log : logs) {
            sb.append(log.getTextoFormatado()).append("\n");
        }

        areaTexto.setText(sb.toString());
        JScrollPane scrollPane = new JScrollPane(areaTexto);
        JOptionPane.showMessageDialog(this, scrollPane, "Histórico de Alterações", JOptionPane.INFORMATION_MESSAGE);
    }

    private void gerenciarPresets() {
        String[] opcoes = {"Adicionar", "Editar", "Remover"};
        String opcao = (String) JOptionPane.showInputDialog(this, "O que deseja fazer?", "Presets", JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);

        if (opcao == null) return;

        switch (opcao) {
            case "Adicionar" -> {
                String nome = JOptionPane.showInputDialog(this, "Nome do novo prato:");
                if (nome != null && !nome.trim().isEmpty()) {
                    gerenciador.adicionarPreset(nome.trim());
                    atualizarPresets();
                }
            }
            case "Editar" -> {
                String atual = (String) seletorPresets.getSelectedItem();
                if (atual == null) return;

                String novo = JOptionPane.showInputDialog(this, "Novo nome:", atual);
                if (novo != null && !novo.trim().isEmpty()) {
                    gerenciador.editarPreset(atual, novo.trim());
                    atualizarPresets();
                }
            }
            case "Remover" -> {
                String atual = (String) seletorPresets.getSelectedItem();
                if (atual != null) {
                    gerenciador.removerPreset(atual);
                    atualizarPresets();
                }
            }
        }
    }

    private void gerenciarUsuarios() {
        JOptionPane.showMessageDialog(this, "Funcionalidade futura: gerenciamento de contas.");
    }

    private void salvarProdutosEmArquivo() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("estoque.dat"))) {
            out.writeObject(estoque);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar o estoque.");
        }
    }

    private void carregarProdutosDoArquivo() {
        File arquivo = new File("estoque.dat");
        if (!arquivo.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(arquivo))) {
            estoque = (ArrayList<Produto>) in.readObject();
            tableModel.setRowCount(0); // Limpa antes de adicionar
            for (Produto p : estoque) {
                tableModel.addRow(new Object[]{p.getNome(), p.getQuantidade()});
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar o estoque.");
        }
    }
}
