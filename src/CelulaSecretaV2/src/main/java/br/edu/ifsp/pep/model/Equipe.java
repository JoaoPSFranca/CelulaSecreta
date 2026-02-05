package br.edu.ifsp.pep.model;

public class Equipe {
    private String nome;
    private Carta cartaSecreta;

    public Equipe(String nome) {
        this.nome = nome;
    }

    // Getters e Setters
    public void setCartaSecreta(Carta carta) { this.cartaSecreta = carta; }
    public Carta getCartaSecreta() { return cartaSecreta; }
}
