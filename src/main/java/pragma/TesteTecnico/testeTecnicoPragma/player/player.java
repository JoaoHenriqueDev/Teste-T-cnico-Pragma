package pragma.TesteTecnico.testeTecnicoPragma.player;

import java.util.*;

public class player {
    public int id;
    public String nome;
    public Set<String> nomesAntigos = new LinkedHashSet<>();
    public int execucaoes = 0;
    public int mortes = 0;
    public int suicidios = 0;
    public Map<String, Integer> mortesPorArma = new HashMap<>();
    public List<String> itens = new ArrayList<>();

    public player(int id, String nome){
        this.id = id;
        this.nome = nome;
    }

    public void adicionarExecucao(String arma) {
        // Registrar uma nova execucao e uma execucao por arma
        this.execucaoes++;
        if (arma != null) {
            // Adiciono a morte com essa arma, verifico se já existem mortes com essa arma e faço a soma e se não existir assumo o valor 0 e adiciono mais uma morte
            this.mortesPorArma.put(arma, this.mortesPorArma.getOrDefault(arma, 0) + 1);
        }
    }

    public void adicionarMorte() {
        // Registrar uma nova morte do jogador
        this.mortes++;
    }

    public void adicionarSuicidio() {
        // Registrar uma nova morte do jogador por suicidio
        this.suicidios++;
    }

    public void atualizarNomePlayer (String novoNome) {
        // faz as verificações e atualiza o nome e guarda o antigo
        if (novoNome == null) return;
        //Veirifica se os nomes não são iguais
        if (!novoNome.equals(this.nome)) {
            if (this.nome != null && !this.nome.isEmpty()) {
                // guarda o nome atual na lista do antigos
                this.nomesAntigos.add(this.nome);
            }
            // atualiza o nome do jogador
            this.nome = novoNome;
        }
    }

    public void adicionarItem (String item) {
        // Registrar um novo item coletado pelo jogador
        if (item != null && !item.isEmpty()) {
            this.itens.add(item);
        }
    }

    public String verificarArmaMaisUsada() {
        // Verifica a arma com maior numero de execucoes
        if (mortesPorArma.isEmpty()) return null;
        // usa as collections pra comparar todas as armas até a com maior numero de kills e retornar o nome e a quantidade de kills
        return Collections.max(mortesPorArma.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
}
