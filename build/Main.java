// If you are a student, don't edit this directory.
// Go to the main page of the repository and read the instructions

import java.lang.Thread;
import java.util.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class Main {
    // also in .gitignore
    private static String BUILD_DIR = "./build/out";

    private static String[] allFiles = new String[]{ "./src/Part1.java", "./src/Part2.java" };
    // ideally these would be put in different subfolders
    private static String[] allClasses = new String[]{ "Part1", "Part2" };
    private static String[] allNames = new String[]{ "Part 1", "Part 2" };

    private static void build(int num) {
        System.out.println("Building " + allNames[num]);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                "javac", "-d", BUILD_DIR, allFiles[num]
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append('\n');
            }

            // should be enough time
            boolean processExited = process.waitFor(5, TimeUnit.SECONDS);
            process.destroy();

            if (processExited) {
                int exitCode = process.exitValue();
                if (exitCode != 0) {
                    System.err.println("Build failed with code " + exitCode
                            + " and message:\n" + output);
                    System.exit(1);
                }
            } else {
                System.err.println("Build timed out with message:\n" + output);
                System.exit(1);
            }

        }
        catch (IOException | InterruptedException e) {
            System.err.println("Build failed ");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void run(int num) {
        build(num);
        System.out.println("Running " + allNames[num]);

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                "java", "-cp", BUILD_DIR, allClasses[num]
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            inputStream = process.getInputStream();
            outputStream = process.getOutputStream();

            byte[] buffer = new byte[8192];
            while (process.isAlive()) {
                if (inputStream.available() > 0) {
                    int amountRead;
                    if ((amountRead = inputStream.read(buffer)) > 0) {
                        System.out.write(buffer, 0, amountRead);
                        System.out.flush();
                    }
                }
                if (System.in.available() > 0) {
                    int amountRead;
                    if ((amountRead = System.in.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, amountRead);
                        outputStream.flush();
                    }
                }
                Thread.sleep(10);
            }

            boolean processExited = process.waitFor(1, TimeUnit.SECONDS);
            process.destroy();
            if (processExited) {
                int exitCode = process.exitValue();
                System.out.println("Program exited with code: " + exitCode);
            }
            else {
                System.out.println("Error while running the process");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Run failed");
            e.printStackTrace();
        }

        if (inputStream != null) try { inputStream.close(); } catch(Throwable e) {}
        if (outputStream != null) try { outputStream.close(); } catch(Throwable e) {}
    }


    private static int testPart1() {
        final int num = 0;

        String[] names = new String[]{ "numbers 1-5", "numbers 10, 4, 22, 41, 2" };
        String[] inputs = new String[]{ "1\n2\n3\n4\n5\n", "10\n4\n22\n41\n2\n" };
        int[] lowestNums = new int[]{ 1, 2 };
        int[] highestNums = new int[]{ 5, 41 };
        final int count = inputs.length;

        Pattern[] patterns = new Pattern[]{
            Pattern.compile("1\\. Lowest number: (\\d+)"),
            Pattern.compile("2\\. Highest number: (\\d+)"),
        };
    

        build(num);
        System.out.println("Testing " + allNames[num]);

        int failed = 0;

        for (int i = 0; i < count; i++) {
            System.out.print("Test #" + i + " `" + names[i] + "`: ");

            try {
                Process process = new ProcessBuilder(
                    "java", "-cp", BUILD_DIR, allClasses[num]
                ).start();

                String out = runSilent(process, inputs[i]);
                boolean processExited = process.waitFor(5, TimeUnit.SECONDS);
                process.destroy();
                if (!processExited) {
                    throw new RuntimeException("Error while running the process:\n`" + out + "`");
                }
                int exitCode = process.exitValue();
                if (exitCode != 0) {
                    throw new RuntimeException("Process exited with code " + exitCode + ",\n`" + out + "`");
                }
                Matcher matcher;

                matcher = patterns[0].matcher(out);
                if (matcher.find()) {
                    int it = 0;
                    try { it = Integer.parseInt(matcher.group(1)); }
                    catch (Throwable e) { throw new RuntimeException("incorrect lowest number"); };

                    if (it != lowestNums[i]) {
                        throw new RuntimeException(
                            "Expected lowest to be " + lowestNums[i] + ", but got " + it
                        );
                    }
                }
                else {
                    throw new RuntimeException("Lowest number not found");
                }

                matcher = patterns[1].matcher(out);
                if (matcher.find()) {
                    int it = 0;
                    try { it = Integer.parseInt(matcher.group(1)); }
                    catch (Throwable e) { throw new RuntimeException("incorrect highest number"); };

                    if (it != highestNums[i]) {
                        throw new RuntimeException(
                            "Expected highest to be " + highestNums[i] + ", but got " + it
                        );
                    }
                }
                else {
                    throw new RuntimeException("Highest number not found");
                }

                System.out.println("OK");
            }
            catch (Throwable e) {
                failed++;
                System.out.println("FAIL: " + e.getMessage());
                //e.printStackTrace();
            }
        }

        print("Total #tests: " + count + ", failed: " + failed);
        return failed;
    }

    public static void main(String[] args) {
        if (args.length != 0) {
            switch (args[0].toLowerCase()) {
                case "run": {
                    int num = getAssignemntNum(args, 1);
                    if (num < 0 || num > 2) {
                        err("Provide assignment number to run (1 or 2)", 2);
                    }
                    run(num);
                    System.exit(0);
                } break;
                case "test": {
                     int num = getAssignemntNum(args, 1);
                     if (num == 0) {
                         int failedC = testPart1();
                         System.exit(failedC == 0 ? 0 : 3);
                     }
                     else {
                         err("Provide assignment number to test (1 or 2)", 2);
                     }
                }
            }
        }

        err(
            "Provide which part of the assignment"
            + " to run (1 or 2) or a command (run, test, or clean)",
            2
       );
    }

    private static void err(String msg, int code) {
        System.err.println(msg);
        System.exit(code);
    }

    private static void print(String msg) {
        System.out.println(msg);
    }

    private static int getAssignemntNum(String[] s, int i) {
        try {
            int number = Integer.parseInt(s[i]);
            if (number == 1 || number == 2) {
                return number - 1;
            }
        } catch (Throwable e) { }
        return -1;
    }

    private static String runSilent(Process process, String input) throws IOException {
        InputStream inputStream = process.getInputStream();
        OutputStream outputStream = process.getOutputStream();

        OutputStream outStream = process.getOutputStream();
        outStream.write(input.getBytes());
        outStream.flush();
        StringBuilder sb = new StringBuilder();

        char[] buffer = new char[512];
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            int readCount;
            while ((readCount = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, readCount);
            }
        }

        try { inputStream.close(); } catch(Throwable e) {}
        try { outputStream.close(); } catch(Throwable e) {}

        return sb.toString();
    }
}
