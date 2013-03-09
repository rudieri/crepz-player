/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.pele;

import com.config.Configuracaoes;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;

/**
 *
 * @author rudieri
 */
public class ColorUtils {

    private static final ArrayList<Pele> listaPelesConhecidas;
    private static final ArrayList<JTable> listaTabelas;
    private static final ArrayList<Component> listaComponentes;
    private static Pele peleAtual = Pele.PELE_PADRAO;

    static {
        listaTabelas = new ArrayList<JTable>();
        listaComponentes = new ArrayList<Component>();
        listaPelesConhecidas = new ArrayList<Pele>();
        //        listaPelesConhecidas.add(PELE_PADRAO);
        ArrayList<String> list = Configuracaoes.getList(Configuracaoes.CONF_PELES);
        for (int i = 0; i < list.size(); i++) {
            String confPele = list.get(i);
            try {
                listaPelesConhecidas.add(Pele.carregarPele(confPele));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (listaPelesConhecidas.isEmpty()) {
            listaPelesConhecidas.add(Pele.PELE_PADRAO);
        } else {
            setPeleAtual(Configuracaoes.getString(Configuracaoes.CONF_PELE_ATUAL).trim());
        }

    }

    public static void aplicarTema() {
        // Primeiro os componentes das janelas
        if (peleAtual == Pele.PELE_PADRAO) {
            Configuracaoes.set(Configuracaoes.CONF_PELE_ATUAL, peleAtual.getNome(), true);
            return;
        }
        for (int i = 0; i < listaComponentes.size(); i++) {
            Component component = listaComponentes.get(i);
            inicializaCoresComponentes(component, ColorUtils.getFrenteJanela(), ColorUtils.getFundoJanela());
        }
        for (int i = 0; i < listaTabelas.size(); i++) {
            JTable jTable = listaTabelas.get(i);

            jTable.setForeground(getFrenteTabelaNaoSelecionada());
            jTable.setSelectionForeground(getFrenteTabelaSelecionada());
            jTable.setBackground(getFundoTabelaNaoSelecionada());
            jTable.setSelectionBackground(getFundoTabelaSelecionada());
        }
        ArrayList<String> salvarPeles = new ArrayList<String>();
        for (int i = 0; i < listaPelesConhecidas.size(); i++) {
            Pele pele = listaPelesConhecidas.get(i);
            salvarPeles.add(pele.toString());
        }
        Configuracaoes.set(Configuracaoes.CONF_PELE_ATUAL, peleAtual.getNome(), true);
        Configuracaoes.set(Configuracaoes.CONF_PELES, salvarPeles,  true);

    }

    private static void inicializaCoresComponentes(Component component, Color corFrenteJanela, Color corFundoJanele) {
        try {

            component.setBackground(corFundoJanele);
            component.setForeground(corFrenteJanela);

            if (component instanceof JPanel) {
                Border border = ((JPanel) component).getBorder();
                if (border instanceof TitledBorder) {
                    ((TitledBorder) border).setTitleColor(corFrenteJanela);
                }
            } else if (component instanceof JTable) {
                JTableHeader tableHeader = ((JTable) component).getTableHeader();
                if (tableHeader != null) {
                    tableHeader.setBackground(corFundoJanele);
                    tableHeader.setForeground(corFrenteJanela);
                }
            }

            if (component instanceof Container) {
                Component[] components = ((Container) component).getComponents();
                for (Component filho : components) {
                    inicializaCoresComponentes(filho, corFrenteJanela, corFundoJanele);
                }
            }
        } catch (Exception ex) {
            System.out.println("Erro ao trocar cor do componente: " + component.getName() + " => " + component.toString());
            ex.printStackTrace();
        }
    }

    public static void registrar(JTable jTable) {
        listaTabelas.add(jTable);
    }

    public static void registrar(Window window) {
        listaComponentes.add(window);
    }

    public static boolean removerRegistro(JTable jTable) {
        return listaTabelas.remove(jTable);
    }

    public static Color getFrenteTabelaNaoSelecionada() {
        return peleAtual.getFrenteTabelaNaoSelecionada();
    }

    public static Color getFrenteTabelaSelecionada() {
        return peleAtual.getFrenteTabelaSelecionada();
    }

    public static Color getFundoTabelaNaoSelecionada() {
        return peleAtual.getFundoTabelaNaoSelecionada();
    }

    public static Color getFundoTabelaSelecionada() {
        return peleAtual.getFundoTabelaSelecionada();
    }

    public static Color getFundoJanela() {
        return peleAtual.getFundoJanela();
    }

    public static Color getFrenteJanela() {
        return peleAtual.getFrenteJanela();
    }

    public static void setFrenteTabelaNaoSelecionada(Color frenteTabelaNaoSelecionada) {
        peleAtual.setFrenteTabelaNaoSelecionada(frenteTabelaNaoSelecionada);
    }

    public static void setFrenteTabelaSelecionada(Color frenteTabelaSelecionada) {
        peleAtual.setFrenteTabelaSelecionada(frenteTabelaSelecionada);
    }

    public static void setFundoTabelaNaoSelecionada(Color fundoTabelaNaoSelecionada) {
        peleAtual.setFundoTabelaNaoSelecionada(fundoTabelaNaoSelecionada);
    }

    public static void setFundoTabelaSelecionada(Color fundoTabelaSelecionada) {
        peleAtual.setFundoTabelaSelecionada(fundoTabelaSelecionada);
    }

    public static void setFundoJanela(Color fundoJanela) {
        peleAtual.setFundoJanela(fundoJanela);
    }

    public static void setFrenteJanela(Color frenteJanela) {
        peleAtual.setFrenteJanela(frenteJanela);
    }

    public static ArrayList<Pele> getListaPelesConhecidas() {
        return listaPelesConhecidas;
    }

    public static Pele getPeleAtual() {
        return peleAtual;
    }

    public static void setPeleAtual(String peleAtual) {
        setPeleAtual(getPelePorNome(peleAtual));
    }

    public static void setPeleAtual(Pele peleAtual) {
        ColorUtils.peleAtual = peleAtual;
    }

    public static Pele getPelePorNome(String nome) {
        for (int i = 0; i < listaPelesConhecidas.size(); i++) {
            Pele pele = listaPelesConhecidas.get(i);
            if (pele.getNome().toLowerCase().equals(nome.toLowerCase())) {
                return pele;
            }
        }
        return null;
    }

    public static void setNomePele(String novoNome) {
        peleAtual = null;
        for (int i = 0; i < listaPelesConhecidas.size(); i++) {
            Pele pele = listaPelesConhecidas.get(i);
            if (pele.getNome().toLowerCase().equals(novoNome.toLowerCase())) {
                peleAtual = pele;
            }
        }
        if (peleAtual == null) {
            peleAtual = new Pele(novoNome);
            listaPelesConhecidas.add(peleAtual);
        }
//        else if (peleAtual == PELE_PADRAO) {
//            peleAtual = new Pele("MOD " + novoNome);
//            listaPelesConhecidas.add(peleAtual);
//        }

    }
}
