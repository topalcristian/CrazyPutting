package com.golf.game.Parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class ReadAndAnalyse {
    private static String fileName;
    private static float[][] result;

    private static int n;

    public static void calculate(String pFileName) {
        fileName = pFileName;
        Scanner in = null;
        {
            try {
                FileReader reader = new FileReader(fileName);
                in = new Scanner(reader);
                String l = in.nextLine();
                String[] words = l.split("\\s+");
                n = Integer.parseInt(words[words.length - 1]);
                result = new float[n][2];
                in.nextLine();
                int i = 0;
                while (in.hasNextLine()) {
                    l = in.nextLine();
                    words = l.split("\\s+");
                    int j = 0;
                    for (int x = 0; x < words.length; x++) {
                        if (isNumeric(words[x])) {
                            result[i][j] = Float.parseFloat(words[x]);
                            j++;
                        }
                    }
                    i++;
                }
                reader.close();
            } catch (FileNotFoundException e) {
                System.out.println("Bad file name");
            } catch (IOException e) {
                System.out.println("Corrupted file");
            }
        }
    }

    public static int getN() {
        return n;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    public static float[][] getResult() {
        return result;
    }


}
