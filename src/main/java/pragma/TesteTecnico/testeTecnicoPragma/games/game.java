package pragma.TesteTecnico.testeTecnicoPragma.games;

import pragma.TesteTecnico.testeTecnicoPragma.player.player;

import java.util.LinkedHashMap;
import java.util.Map;

public class game {
    public int game;
    public String mapa;
    public int mortesMapa = 0;
    public Map<Integer, player> jogadores = new LinkedHashMap<>();

    public game(int game){
        this.game = game;
    }


    public player criarJogador(int id, String nome){
        //Verifica se o jogador já existe na lista e se nao cria um novo
        if(!jogadores.containsKey(id)){
            jogadores.put(id, new player(id, nome == null ? "" : nome));
        } else if(nome != null && !nome.isEmpty()){
            // se ele existir apenas pegas as informacoes dele e atualiza o nome
            jogadores.get(id).atualizarNomePlayer(nome);
        }
        return jogadores.get(id);
    }

    public void contarMortesDosJogadoresDoMapa(int assasino, int vitima, String arma) {
        // 2 - total de mortes sempre aumenta
        this.mortesMapa++;
        // se o assasino for numero 1022 (world), não incrementa mortes do jogador
        if (assasino == 1022) {
            // Morte pelo mundo: so adiciona morte a vitima
            criarJogador(vitima, null).adicionarMorte();
            return;
        }
        // Se o assassino for igual a vitima quer dizer que foi um suicidio
        criarJogador(vitima, null); // garante existência
        criarJogador(assasino, null);

        if (assasino == vitima) {
            // suicídio
            jogadores.get(assasino).adicionarSuicidio();
            jogadores.get(assasino).adicionarMorte();
        } else {
            // Morte normal
           jogadores.get(assasino).adicionarExecucao(arma);
           jogadores.get(vitima).adicionarMorte();
        }
    }

    public void itemColetado(int jogadorid, String item) {
        // 3 - registra item
        criarJogador(jogadorid, null).adicionarItem(item);
    }
}
