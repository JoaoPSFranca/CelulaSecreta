package br.edu.ifsp.pep.model;

import java.io.Serializable;
import java.util.List;

public class Carta implements Serializable {
    private int id;
    private String nome;
    private String nomeImagem;
    private String descricao;
    private boolean celula; // true = célula, false = organela
    private int nivelDificuldade;
    private Reino reino;
    private List<String> propriedades;
    private List<String> funcoes;
    private List<String> organelas;

    public Carta(int id, String nome, String nomeImagem, String descricao, boolean celula, int nivelDificuldade, Reino reino, List<String> propriedades, List<String> funcoes, List<String> organelas) {
        this.id = id;
        this.nome = nome;
        this.nomeImagem = nomeImagem;
        this.descricao = descricao;
        this.celula = celula;
        this.nivelDificuldade = nivelDificuldade;
        this.reino = reino;
        this.propriedades = propriedades;
        this.funcoes = funcoes;
        this.organelas = organelas;
    }

    // Getters e Setters
    public String getNomeImagem() { return nomeImagem; }
    public void setNomeImagem(String nomeImagem) { this.nomeImagem = nomeImagem; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public boolean isCelula() { return celula; }
    public void setCelula(boolean celula) { this.celula = celula; }
    public int getNivelDificuldade() { return nivelDificuldade; }
    public void setNivelDificuldade(int nivelDificuldade) { this.nivelDificuldade = nivelDificuldade; }
    public Reino getReino() { return reino; }
    public void setReino(Reino reino) { this.reino = reino; }
    public List<String> getPropriedades() { return propriedades; }
    public void setPropriedades(List<String> propriedades) { this.propriedades = propriedades; }
    public List<String> getFuncoes() { return funcoes; }
    public void setFuncoes(List<String> funcoes) { this.funcoes = funcoes; }
    public List<String> getOrganelas() { return organelas; }
    public void setOrganelas(List<String> organelas) { this.organelas = organelas; }
}