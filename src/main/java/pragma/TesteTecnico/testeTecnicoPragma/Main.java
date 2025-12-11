package pragma.TesteTecnico.testeTecnicoPragma;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import pragma.TesteTecnico.testeTecnicoPragma.games.game;
import pragma.TesteTecnico.testeTecnicoPragma.player.player;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    // Entrada do codigo
    public static void main(String[] args) throws Exception {
        // Se o usuario não passar o caminho do arquivo, usa padrões
        String input = args.length > 0 ? args[0] : "Quake.log";
        String saidaJson = args.length > 1 ? args[1] : "games.json";

        // Ler o arquivo, organizar e processar ele
        List<game> games = parseLogFile_func(input);

        // Transformar ele em saida Json
        writeJson_func(games, saidaJson);

        // Analises sobre os dados que foram coletados atraves dos logs
        analises(games);
    }

    // Separando o arquivo de log em partidas
    public static List<game> parseLogFile_func(String filename) throws IOException {
        // lemos todos os logs e dividimos nas seções que começam com InitGame:
        List<game> result = new ArrayList<>();
        String content = new String(Files.readAllBytes(Paths.get(filename)));

        // Dividir mantendo o InitGame: no início de cada bloco
        String[] parts = content.split("(?=\\bInitGame:)"); // lookahead, mantém InitGame no começo
        int gameCounter = 0;

        Pattern mapPattern = Pattern.compile("mapname\\\\([^\\\\\\s]+)");
        Pattern clientConnectPattern = Pattern.compile("ClientConnect:\\s*(\\d+)");
        Pattern clientUserInfoPattern = Pattern.compile("ClientUserinfoChanged:\\s*(\\d+)\\s+.*n\\\\([^\\\\]+)\\\\");
        Pattern killPattern = Pattern.compile("Kill:\\s*(\\d+)\\s*(\\d+)\\s*(\\d+):\\s*(.*)");
        Pattern itemPattern = Pattern.compile("Item:\\s*(\\d+)\\s*(\\S+)");

        for (String part : parts) {
            if (!part.contains("InitGame:")) continue;
            gameCounter++;
            game games = new game(gameCounter);

            // Nome do mapa
            Matcher mMap = mapPattern.matcher(part);
            if (mMap.find()) {
                games.mapa = mMap.group(1);
            } else {
                games.mapa = "unknown";
            }

            // Ler linha a linha dos logs
            BufferedReader reader = new BufferedReader(new StringReader(part));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // ClientUserinfoChanged => atualiza nome do id
                Matcher mu = clientUserInfoPattern.matcher(line);
                if (mu.find()) {
                    int id = Integer.parseInt(mu.group(1));
                    String name = mu.group(2);
                    games.criarJogador(id, name);
                    continue;
                }

                // ClientConnect => garante player com id (nome pode vir depois)
                Matcher mc = clientConnectPattern.matcher(line);
                if (mc.find()) {
                    int id = Integer.parseInt(mc.group(1));
                    games.criarJogador(id, null);
                    continue;
                }

                // Item => registro de item coletado
                Matcher mi = itemPattern.matcher(line);
                if (mi.find()) {
                    int id = Integer.parseInt(mi.group(1));
                    String item = mi.group(2);
                    games.itemColetado(id, item);
                    continue;
                }

                // Morte => registro de morte
                Matcher mk = killPattern.matcher(line);
                if (mk.find()) {
                    int assassino = Integer.parseInt(mk.group(1));
                    int vitima = Integer.parseInt(mk.group(2));
                    // terceira coluna é a arma id numérico (não usamos), descrição após :
                    String desc = mk.group(4);

                    // vamos extrair a arma
                    String arma = null;
                    int byIndex = desc.lastIndexOf(" by ");
                    if (byIndex >= 0) {
                        arma = desc.substring(byIndex + 4).trim(); // ex: MOD_ROCKET_SPLASH
                    }
                    games.contarMortesDosJogadoresDoMapa(assassino, vitima, arma);
                    continue;
                }
            }
            // fim leitura linhas
            result.add(games);
        }
        return result;
    }

    // Transformar os dados em uma saida JSON
    public static void writeJson_func(List<game> games, String outJson) throws IOException {

        // Usamos o Jackson pra gravar de forma mais organizada
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Transformar a entidade game para estrutura mais simples
        List<Map<String, Object>> outList = new ArrayList<>();
        for (game g : games) {
            // Adiciona informações básicas da partida
            Map<String, Object> gm = new LinkedHashMap<>();
            gm.put("game", g.game);
            gm.put("map", g.mapa);
            gm.put("total_kills", g.mortesMapa);

            List<Map<String, Object>> players = new ArrayList<>();
            // Para cada jogador dessa partida
            for (player p : g.jogadores.values()) {
                Map<String, Object> pm = new LinkedHashMap<>();
                pm.put("id", p.id);
                pm.put("current_name", p.nome);
                pm.put("old_names", new ArrayList<>(p.nomesAntigos));
                pm.put("kills", p.execucaoes);
                pm.put("deaths", p.mortes);
                pm.put("suicides", p.suicidios);
                pm.put("favorite_weapon", p.verificarArmaMaisUsada());
                pm.put("collected_items", p.itens);
                players.add(pm);
            }

            // Adiciona o jogador convertido para a lista de players
            gm.put("players", players);
            outList.add(gm);
        }
        mapper.writeValue(new File(outJson), outList);
        System.out.println("JSON gravado em: " + outJson);
    }

    // Analises sobre os dados coletados
    public static void analises(List<game> games) {
        // Primeira analise: Top jogadores por execucoes
        Map<String, Integer> killsByName = new HashMap<>();
        Map<String, Integer> killsByWeapon = new HashMap<>();
        int totalKillsAllGames = 0;

        for (game g : games) {
            totalKillsAllGames += g.mortesMapa;
            for (player p : g.jogadores.values()) {
                String name = p.nome == null || p.nome.isEmpty() ? ("player_" + p.id) : p.nome;
                killsByName.put(name, killsByName.getOrDefault(name, 0) + p.execucaoes);
                // armas
                for (Map.Entry<String, Integer> e : p.mortesPorArma.entrySet()) {
                    killsByWeapon.put(e.getKey(), killsByWeapon.getOrDefault(e.getKey(), 0) + e.getValue());
                }
            }
        }

        System.out.println("\n=== Análises ===");
        // A
        System.out.println("\n(A) Top jogadores por execucoes:");
        killsByName.entrySet().stream()
                .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(e -> System.out.println(e.getKey() + " => " + e.getValue() + " kills"));

        // B
        System.out.println("\n(B) Arma mais usada (por execucoes):");
        killsByWeapon.entrySet().stream()
                .sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(e -> System.out.println(e.getKey() + " => " + e.getValue() + " kills"));

        // C
        double mortesPorJogo = games.isEmpty() ? 0.0 : (double) totalKillsAllGames / games.size();
        System.out.println("\n(C) Média de execucoes por partida: " + String.format("%.2f", mortesPorJogo));
    }
}
