/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.utils.pele;

import java.awt.Color;

/**
 *
 * @author rudieri
 */
public class Pele {

    public static final Pele PELE_PADRAO = new Pele("Padrão");
    private Color frenteTabelaSelecionada;
    private Color frenteTabelaNaoSelecionada;
    private Color fundoTabelaSelecionada;
    private Color fundoTabelaNaoSelecionada;
    private Color fundoJanela;
    private Color frenteJanela;
    private final String nome;

    public Pele(String nome) {
        this.nome = nome;
        

        fundoTabelaNaoSelecionada =  Color.BLACK;
        frenteTabelaNaoSelecionada = Color.DARK_GRAY;
        fundoTabelaSelecionada = new Color(255, 51, 0, 255);
        frenteTabelaSelecionada = Color.BLACK;
        fundoJanela = Color.BLACK;
        frenteJanela= Color.BLUE;
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
        String nome = configs.substring(0, idxFimNome).trim();
        if (nome.equals(PELE_PADRAO.getNome())) {
            return PELE_PADRAO;
        }
        String[] dados = configs.substring(idxFimNome+4, configs.length()).split("[|]");
        Pele pele =new Pele(nome);
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
