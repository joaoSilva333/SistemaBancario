package br.com.bancosimples;

import br.com.bancosimples.ui.Menu;

/**
 * Ponto de entrada do Sistema Bancário.
 * Só chama o menu — simples assim.
 */
public class Main {

    public static void main(String[] args) {
        new Menu().iniciar();
    }
}
