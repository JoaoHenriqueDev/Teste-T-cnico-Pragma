# 🔫 Quake III Arena — Log Processor

Processamento de logs desenvolvido para o desafio técnico, capaz de transformar o log bruto do Quake III Arena em estatísticas completas e estruturadas.

---

## 📌 Sobre o Projeto

Este projeto lê o arquivo de log do **Quake III Arena**, identifica cada partida e extrai informações importantes como:

- Jogadores conectados  
- Mudanças de nome  
- Kills, mortes e suicídios  
- Armas utilizadas  
- Itens coletados  
- Arma favorita de cada jogador  
- Total de kills da partida  

Ao final, o programa gera:

- Um arquivo **JSON organizado** com todas as partidas  
- Estatísticas gerais no console (top jogadores, armas mais usadas, média de kills por partida)  

---

## 🧠 Funcionalidades

✔ Separação correta das partidas (`InitGame:`)  
✔ Identificação e atualização de jogadores  
✔ Registro completo de estatísticas individuais  
✔ Reconhecimento da arma usada em cada kill  
✔ Tratamento do `<world>`  
✔ Lista de itens coletados  
✔ Arma favorita  
✔ Geração de JSON  
✔ Impressão de análises gerais  

---

## 🚀 Como Executar

### **1. Requisitos**
- Java **11+**  
- Maven  

### **2. Compilar o projeto**
Execute dentro da pasta do projeto:

```bash
mvn clean package

```Isso gera o arquivo
target/quake-log-processor-1.0-SNAPSHOT.jar

### **3. Executar o programa
java -jar target/quake-log-processor-1.0-SNAPSHOT.jar Quake.log games.json

