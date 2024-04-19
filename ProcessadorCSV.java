//          EMPRESA: EFFETIVE SISTEMAS LTDA
//          CANDOIDATO: Joao victor lopes rodrigues 
//          DESAFIO: validarCSV

//          TAREFAS DESIGNADAS A VOCÊ:
 
//  Durante a divisão de tarefas, foi definido que sua incumbência será:
 
//  1) Criar um JAR que leia todos os CSV dentro de um diretório "/PENDENTES" ;
//  2) O JAR deve validar se os CSV possuem no mínimo as seguintes características:
//  a) Não esteja vazio;
//  b) Cada linha do arquivo tenha a quantidade exata de colunas esperadas a serem importadas, sendo essas: 
//  NUMERO_DA_VENDA; NOME_DO_CLIENTE; DATA_DA_VENDA; VALOR_DA_VENDA
//  c) Validar que o arquivo tenha apenas linhas válidas de vendas
//  d) Todos os campos são obrigatórios, isso deve ser validado
//  e) Campo de DATA_DA_VENDA deve ser uma data válida, não futura e com formato DD/MM/YYYY
//  f) Campo de VALOR_DA_VENDA tem que ser maior que zero e aceitar casas decimais
//  g) Não pode haver duplicidade de linhas com o mesmo NUMERO_DA_VENDA
//  3) Caso o arquivo lido atenda às condições do item 2, ele deverá ser movido para o diretório "/VALIDADO";
//  4) Caso o arquivo lido não atenda às condições do item 2, ele deverá ser movido para o diretório "/INVALIDADO";
//  5) Imprimir qualquer falha no Console indicando a linha do erro e qual erro foi encontrado

//                          DESAFIOS ENFRENTADOS NA REALIZAÇÃO

//   kkkk Embora o GitLab seja praticamente similar ao GitHub como um sistema de hospedagem, achei um pouco estranho o fato de que os comandos do Git são os mesmos. Acho o GitHub esteticamente mais bonito. Outra dificuldade que encontrei foi no início, pois minha lógica para verificação de datas estava no padrão dos Estados Unidos. Só percebi isso às 3 da manhã, mas foi uma experiência muito divertida. Agradeço muito pela oportunidade.!

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProcessadorCSV {

    private static final String DIRETORIO_PENDENTES = "/PENDENTES";
    private static final String DIRETORIO_VALIDADO = "/VALIDADO";
    private static final String DIRETORIO_INVALIDADO = "/INVALIDADO";

    // Colunas esperadas no arquivo CSV
    private static final String[] COLUNAS_ESPERADAS = {"NUMERO_DA_VENDA", "NOME_DO_CLIENTE", "DATA_DA_VENDA", "VALOR_DA_VENDA"};

    public static void main(String[] args) {
        processarCSVs();
    }

    // Método principal para processar arquivos CSV
    private static void processarCSVs() {
        // Obtém o diretório de arquivos pendentes
        File diretorioPendentes = new File(System.getProperty("user.dir") + DIRETORIO_PENDENTES);
        // Lista todos os arquivos CSV no diretório
        File[] arquivosCSV = diretorioPendentes.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));

        // Verifica se há arquivos CSV no diretório
        if (arquivosCSV != null) {
          
            for (File arquivo : arquivosCSV) {
                // Valida o arquivo CSV e move para o diretório correspondente
                if (validarCSV(arquivo)) {
                    moverArquivo(arquivo, DIRETORIO_VALIDADO);
                } else {
                    moverArquivo(arquivo, DIRETORIO_INVALIDADO);
                }
            }
        } else {
            System.out.println("Nenhum arquivo CSV encontrado no diretório PENDENTES.");
        }
    }

    // Método para validar o conteúdo do arquivo CSV
    private static boolean validarCSV(File arquivo) {
        // Le as linhas do arquivo CSV
        List<String> linhas = lerLinhasCSV(arquivo);
        // Verifica se ta vazio
        if (linhas.isEmpty()) {
            System.out.println("Arquivo vazio: " + arquivo.getName());
            return false;
        }

        // Conjunto para armazenar números de venda e verificar se tem duplicatas
        Set<String> numerosVenda = new HashSet<>();
    
        for (int i = 0; i < linhas.size(); i++) {
            String linha = linhas.get(i);
            String[] colunas = linha.split(";");

            // Verifica se o número de colunas está correto
            if (colunas.length != COLUNAS_ESPERADAS.length) {
                System.out.println("Número incorreto de colunas na linha " + (i + 1) + " do arquivo " + arquivo.getName());
                return false;
            }

            // Verifica se algum campo obrigatório está vazio
            for (int j = 0; j < colunas.length; j++) {
                if (colunas[j].isEmpty()) {
                    System.out.println("Campo obrigatório vazio na linha " + (i + 1) + ", coluna " + (j + 1) + " do arquivo " + arquivo.getName());
                    return false;
                }
            }

            // Verifica o formato da data
            if (!colunas[2].matches("\\d{2}/\\d{2}/\\d{4}")) {
                System.out.println("Formato de data inválido na linha " + (i + 1) + " do arquivo " + arquivo.getName());
                return false;
            }

            // Verifica se a data é válida
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                dateFormat.parse(colunas[2]);
            } catch (ParseException e) {
                System.out.println("Data inválida na linha " + (i + 1) + " do arquivo " + arquivo.getName());
                return false;
            }

            // Verifica se o valor da venda é válido
            if (Double.parseDouble(colunas[3]) <= 0) {
                System.out.println("Valor de venda inválido na linha " + (i + 1) + " do arquivo " + arquivo.getName());
                return false;
            }

            // Verifica duplicatas de número de venda
            if (!numerosVenda.add(colunas[0])) {
                System.out.println("Duplicidade de número de venda na linha " + (i + 1) + " do arquivo " + arquivo.getName());
                return false;
            }
        }
        return true;
    }

    // Método para ler as linhas do arquivo CSV
    private static List<String> lerLinhasCSV(File arquivo) {
        List<String> linhas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linhas.add(linha);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return linhas;
    }

    // Método para mover o arquivo para o diretório de destino
    private static void moverArquivo(File arquivo, String destino) {
        Path origemPath = arquivo.toPath();
        Path destinoPath = Paths.get(System.getProperty("user.dir") + destino + "/" + arquivo.getName());
        try {
            Files.move(origemPath, destinoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}