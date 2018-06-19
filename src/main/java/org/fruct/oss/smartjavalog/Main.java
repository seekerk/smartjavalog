package org.fruct.oss.smartjavalog;

import org.apache.commons.cli.*;

import static java.lang.System.exit;

/**
 * Core application class
 *
 * @author Kirill Kulakov
 */
public class Main {


    /**
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        JavaLogBuilder javalog = new JavaLogBuilder();

        CommandLine cmd = parseOptions(args);

        if (cmd == null)
            exit(1);

        javalog.setOwlFile(cmd.getOptionValue("input"));
        javalog.setOutputFolder(cmd.getOptionValue("output", "./output"));
        javalog.setPackageName(cmd.getOptionValue("name"));
        javalog.setPlatform(cmd.getOptionValue("platform", "default"));

        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        System.out.println("Use file \"" + javalog.getOwlFile() + "\"");
        System.out.println("Output folder: \"" + javalog.getOutputFolder() + "\"");
        System.out.println("Used platform: \"" + javalog.getPlatform() + "\"");

        //open file
        javalog.parse();

//        // создаем результирующий каталог
//        File outputDir = new File(javalog.getOutputFolder() + "/" + javalog.getPackageName().replace(".","/"));
//        if (!outputDir.exists()) {
//            if (!outputDir.mkdirs()) {
//                System.err.println("Can't create folder \"" + outputDir.getAbsolutePath() + "\"");
//                exit(2);
//            }
//        }
//
//        File baseOutputDir = new File(outputDir.getAbsolutePath() + "/base");
//        if (!baseOutputDir.exists()) {
//            if ()
//        }

        // генерация файлов
        javalog.generate();

    }

    /**
     * Parse command line options
     * @param args command line arguments
     * @return CommandLine object with values or null if arguments not parsed
     */
    private static CommandLine parseOptions(String[] args) {
        Options options = new Options();

        Option inputFileOpt = new Option("i", "input", true, "input owl file path");
        inputFileOpt.setRequired(true);
        options.addOption(inputFileOpt);

        Option outputFolderOpt = new Option("o", "output", true, "output folder path");
        outputFolderOpt.setRequired(false);
        options.addOption(outputFolderOpt);

        Option packageNameOpt = new Option("n", "name", true, "package name");
        packageNameOpt.setRequired(true);
        options.addOption(packageNameOpt);

        Option platformOpt = new Option("p", "platform", true, "target platform. Supports \"android\" or \"default\" values");
        platformOpt.setRequired(false);
        options.addOption(platformOpt);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine ret = null;

        try {
            ret = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("smartjavalog", options);
        }

        return ret;
    }
}
