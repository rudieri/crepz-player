/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.pele;

import com.sun.java.swing.plaf.windows.DesktopProperty;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.SystemColor;
import java.awt.Toolkit;

/**
 *
 * @author rudieri
 */
public class Pele {

    private Color frenteTabelaSelecionada;
    private Color frenteTabelaNaoSelecionada;
    private Color fundoTabelaSelecionada;
    private Color fundoTabelaNaoSelecionada;
    private Color fundoJanela;
    private Color frenteJanela;
    private final String nome;

    public Pele(String nome) {
        this.nome = nome;
        

        fundoTabelaNaoSelecionada = new Color(238, 146, 54, 255);
        frenteTabelaNaoSelecionada = Color.DARK_GRAY;
        fundoTabelaSelecionada = Color.DARK_GRAY;
        frenteTabelaSelecionada = new Color(238, 146, 16, 205);
        fundoJanela = Color.DARK_GRAY;
        frenteJanela= Color.WHITE;
    }

    public Color getFrenteTabelaNaoSelecionada() {
        return frenteTabelaNaoSelecionada;
    }

    public void setFrenteTabelaNaoSelecionada(Color frenteTabelaNaoSelecionada) {
        this.frenteTabelaNaoSelecionada = frenteTabelaNaoSelecionada;
    }

    public Color getFrenteTabelaSelecionada() {
        return frenteTabelaSelecionada;
    }

    public void setFrenteTabelaSelecionada(Color frenteTabelaSelecionada) {
        this.frenteTabelaSelecionada = frenteTabelaSelecionada;
    }

    public Color getFundoTabelaNaoSelecionada() {
        return fundoTabelaNaoSelecionada;
    }

    public void setFundoTabelaNaoSelecionada(Color fundoTabelaNaoSelecionada) {
        this.fundoTabelaNaoSelecionada = fundoTabelaNaoSelecionada;
    }

    public Color getFundoTabelaSelecionada() {
        return fundoTabelaSelecionada;
    }

    public void setFundoTabelaSelecionada(Color fundoTabelaSelecionada) {
        this.fundoTabelaSelecionada = fundoTabelaSelecionada;
    }

    public Color getFrenteJanela() {
        return frenteJanela;
    }

    public void setFrenteJanela(Color frenteJanela) {
        this.frenteJanela = frenteJanela;
    }

    public Color getFundoJanela() {
        return fundoJanela;
    }

    public void setFundoJanela(Color fundoJanela) {
        this.fundoJanela = fundoJanela;
    }
    

    public String getNome() {
        return nome;
    }
    
    public static Pele carregarPele(String configs){
        int idxFimNome = configs.indexOf(" :=>");
        String nome = configs.substring(0, idxFimNome);
        String[] dados = configs.substring(idxFimNome+4, configs.length()).split("[|]");
        Pele pele =new Pele(nome.trim());
        pele.setFrenteTabelaSelecionada(new Color(Integer.valueOf(dados[0]), true));
        pele.setFrenteTabelaNaoSelecionada(new Color(Integer.valueOf(dados[1]), true));
        pele.setFundoTabelaSelecionada(new Color(Integer.valueOf(dados[2]), true));
        pele.setFundoTabelaNaoSelecionada(new Color(Integer.valueOf(dados[3]), true));
        pele.setFundoJanela(new Color(Integer.valueOf(dados[4]), true));
        pele.setFrenteJanela(new Color(Integer.valueOf(dados[5]), true));
        return pele;
    }

    @Override
    public String toString() {
        return nome + " :=>" + frenteTabelaSelecionada.getRGB() + "|" + frenteTabelaNaoSelecionada.getRGB()
                + "|" + fundoTabelaSelecionada.getRGB() + "|" + fundoTabelaNaoSelecionada.getRGB()
                + "|" + fundoJanela.getRGB() + "|" + frenteJanela.getRGB();
    }
}
