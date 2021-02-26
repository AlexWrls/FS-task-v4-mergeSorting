
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Param {
    private static final String PATH = System.getProperty("user.dir")+"/src/main/resources/";
    private final Logger log = Logger.getGlobal();

    private boolean isAscend = true;                     // false - по убыванию | true - по возрастанию
    private boolean isStr = true;                        // false - число | true - строка
    private String nameOutFile = "";                     // имя выходного файла
    private List<String> nameInFile = new ArrayList<>(); // имя входных файлов

    private Param(){};
    private static Param param;
    public static synchronized Param getParam(String... args){
        if (param == null){
            param = new Param();
            param.initial(args);
        }
        return param;
    }

    private static void showParamOptions(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("sort-it.exe","Параметры программы задаются при запуске через аргументы командной строки, по порядку:",options ,
                "out.txt имя выходного файла, обязательное;\nin1.txt остальные параметры – имена входных файлов, не менее одного;");

    }
    private static void printOption(Options options, int status){
        showParamOptions(options);
        System.exit(status);
    }

    void initial(String[] args) {
        Options options = new Options();
        options.addOption("a", false, "режим сортировки по возрастанию (по умолчанию) при отсутствии -a или -d;");
        options.addOption("d", false, "режим сортировки по убыванию. (-d);");
        options.addOption("s", false, "тип данных строки, обязательное (-i);");
        options.addOption("i", false, "тип данных числа, обязательное, взаимоисключительна с (-s);");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (UnrecognizedOptionException e) {
            log.warning(String.format("Неизвестный параметр: %s",e.getOption()));
            printOption(options, 1);
        } catch (ParseException e) {
            log.warning(String.format("Сбой разбора параметра аргументов: %s", e.getMessage()));
            printOption(options, 2);
        }

        if (cmd == null) {
            log.warning("Ошибка вполнения программы");
            printOption(options, 3);
        } else {
            if (cmd.hasOption("a") && cmd.hasOption("d")) {
                log.warning("Не корректно задан режим сортировки (пример: -a или -d)");
                printOption(options, 4);
            }
            if (!(cmd.hasOption("i") || cmd.hasOption("s"))) {
                log.warning("Отсутствует обязательная опция тип данных (пример: -s или -i)");
                printOption(options, 5);
            }
            if (cmd.hasOption("i") && cmd.hasOption("s")) {
                log.warning("Не корректно задан тип данных, только одна опция (пример: -s или -i)");
                printOption(options, 6);
            }

            List<String> files = cmd.getArgList();
            if (files.size() < 1) {
                log.warning("Отсутствуют остальные параметры: имя выходного файла; (пример: out.txt)");
                printOption(options, 7);
            }
            if (files.size() < 2) {
                log.warning("Отсутствуют остальные параметры: имена входных файлов, не менее одного; (пример: in1.txt)");
                printOption(options, 8);
            }

            if (cmd.hasOption("d")) isAscend = false;
            if (cmd.hasOption("i")) isStr = false;
            nameOutFile = PATH+"result/"+files.get(0);
            files.remove(0);
            for (String inFile:files){
                nameInFile.add(PATH+"source_file/"+inFile);
            }
            log.info("Входной файл:\n"+nameInFile);
            log.info(isAscend?"Задана срторовка по возрастанию":"Задана срторовка по убыванию");
            log.info(isStr?"Тип данных - строка":"Тип данных - число");
            log.info("Выходной файл:\n"+nameOutFile);

        }
    }


    public boolean isAscend() {
        return isAscend;
    }

    public boolean isStr() {
        return isStr;
    }

    public String getNameOutFile() {
        return nameOutFile;
    }

    public List<String> getNameInFile() {
        return nameInFile;
    }
}