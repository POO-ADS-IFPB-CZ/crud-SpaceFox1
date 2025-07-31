package view;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import dao.GenericDao;
import model.Produto;
import utils.RoundedHeaderUI;
import utils.RoundedScrollPane;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class TelaPrincipal extends JFrame {
    private JPanel contentPane;
    private JButton addButton;
    private JButton atualizarButton;
    private JButton removerButton;
    private JPanel tableHolder;
    private GenericDao<Produto> produtoDao;
    private String[] colunas = new String[]{
            "Código", "Descrição", "Preço"
    };
    private String[][] dados = new String[][]{};

    private JScrollPane loadData(JPanel tableHolder) throws IOException, ClassNotFoundException {
        tableHolder.removeAll();
        tableHolder.revalidate();
        tableHolder.repaint();
        Set<Produto> produtos = produtoDao.getAll();
        dados = produtos.stream()
                .filter(Objects::nonNull)
                .map(p -> new String[]{
                        p.getCodigo(),
                        p.getDescricao(),
                        String.valueOf(p.getPreco())
                })
                .toArray(String[][]::new);
        JScrollPane scrollPane = getJScrollPane(colunas, dados);
        tableHolder.add(scrollPane, new com.intellij.uiDesigner.core.GridConstraints(
                0, 0, 1, 1,
                com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null, 0, false));
        tableHolder.revalidate();
        tableHolder.repaint();
        return scrollPane;
    }

    public TelaPrincipal() throws IOException, ClassNotFoundException {
        try {
            produtoDao = new GenericDao<>("produtos.txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao tentar abrir o produto", "Erro", JOptionPane.ERROR_MESSAGE);
        }

        AtomicReference<JScrollPane> scrollPane = new AtomicReference<>(loadData(tableHolder));
        tableHolder.setVisible(true);
        setTitle("Crud - Produtos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(contentPane);

        // interatividade

        addButton.addActionListener(e -> {
            AddEdit addModal = new AddEdit(false, null, null, null);
            Produto resultado = addModal.resultado;
            if (addModal.sucesso) {
                try {
                    produtoDao.salvar(resultado);
                    scrollPane.set(loadData(tableHolder));
                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        atualizarButton.addActionListener(e -> {
            JViewport viewport = scrollPane.get().getViewport();
            JTable tabela = (JTable) viewport.getView();
            int row = tabela.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(null, "Selecione um item para editar", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String codigo = tabela.getValueAt(row, 0).toString();
            String descricao = tabela.getValueAt(row, 1).toString();
            double preco = Double.parseDouble(tabela.getValueAt(row, 2).toString());
            AddEdit addEdit = new AddEdit(true, codigo, descricao, preco);
            Produto resultado = addEdit.resultado;
            if (!addEdit.sucesso) return;
            try {
                produtoDao.atualizar(resultado);
                scrollPane.set(loadData(tableHolder));
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        removerButton.addActionListener(e -> {
            JViewport viewport = scrollPane.get().getViewport();
            JTable tabela = (JTable) viewport.getView();
            int row = tabela.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(null, "Selecione um item para editar", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String codigo = tabela.getValueAt(row, 0).toString();
            String descricao = tabela.getValueAt(row, 1).toString();
            double preco = Double.parseDouble(tabela.getValueAt(row, 2).toString());
            Produto resultado = new Produto(codigo, descricao, preco);
            int resposta = JOptionPane.showConfirmDialog(
                    null,
                    "Deseja apagar realmente o produto " + codigo + "?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION
            );
            if (resposta == JOptionPane.NO_OPTION) return;
            try {
                produtoDao.remover(resultado);
                scrollPane.set(loadData(tableHolder));
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private static JScrollPane getJScrollPane(String[] colunas, String[][] dados) {
        JTable tabela = new JTable(dados, colunas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            final int columnIndex = i;

            tabela.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    if (c instanceof JLabel label) {
                        Border padding = BorderFactory.createEmptyBorder(0, 12, 0, 12);
                        int bottom = (row < table.getRowCount() - 1) ? 1 : 0;
                        int right = (columnIndex == table.getColumnCount() - 1) ? 0 : 1;

                        Border cellBorder = BorderFactory.createMatteBorder(0, 0, bottom, right, Color.darkGray);

                        label.setBorder(BorderFactory.createCompoundBorder(cellBorder, padding));
                        label.setOpaque(true);
                    }

                    return c;
                }
            });
        }


        tabela.setRowHeight(32);
        tabela.setRowMargin(6);
        JTableHeader header = tabela.getTableHeader();
        header.setUI(new RoundedHeaderUI(0));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder());
        JScrollPane scrollPane = new RoundedScrollPane(tabela, 15);
        scrollPane.setOpaque(false);

        return scrollPane;
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
            UIManager.put("Button.background", new Color(255, 140, 0));
            UIManager.put("Button.foreground", Color.black);
            UIManager.put("Button.focusedBackground", new Color(255, 160, 40));
            UIManager.put("Button.hoverBackground", new Color(255, 165, 60));

            TelaPrincipal dialog = new TelaPrincipal();
            dialog.setMinimumSize(new Dimension(800, 600));
            dialog.pack();
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(16, 16, 16, 16), -1, 16));
        tableHolder = new JPanel();
        tableHolder.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(tableHolder, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), 16, -1));
        contentPane.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, new Dimension(-1, 30), 0, false));
        addButton = new JButton();
        addButton.setEnabled(true);
        addButton.setText("Adicionar");
        panel1.add(addButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        atualizarButton = new JButton();
        atualizarButton.setText("Atualizar");
        panel1.add(atualizarButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removerButton = new JButton();
        removerButton.setText("Remover");
        panel1.add(removerButton, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
