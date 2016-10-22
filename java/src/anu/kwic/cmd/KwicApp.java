package anu.kwic.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KwicApp {
  private final File fileObj;
  private final String word;
  private final int contextLength;
  private List<Map.Entry<String, String>> contexts;

  public KwicApp(final String filePath, final String word, final int context)
            throws FileNotFoundException {
    this.word = word;
    this.contextLength = context;
    this.fileObj = new File(filePath);
    if (!this.fileObj.exists()) {
      throw new FileNotFoundException("Given file: " + filePath + " does "
            + "not exist");
    }
  }

  public List<Map.Entry<String, String>> getContexts() {
    return this.contexts;
  }

  public void searchContext() {
    ArrayList<String> tokenList = new ArrayList<String>();
    try (BufferedReader buffReader =
      new BufferedReader(new FileReader(this.fileObj))) {
      String line;
      while ((line = buffReader.readLine()) != null) {
        String[] lineTokens = null;
        lineTokens = line.split(" ");
        tokenList.addAll(Arrays.asList(lineTokens));
      }
    } catch (IOException ioexp) {
      System.err.println(ioexp.getMessage());
    }
    if (!tokenList.isEmpty() && tokenList.contains(word)) {
      StringBuilder rightContext = new StringBuilder();
      StringBuilder leftContext = new StringBuilder();

      Map<String, String> pair = new HashMap<String, String>();
      this.contexts = new ArrayList<Map.Entry<String, String>>();
      final int lastIdx = tokenList.size();
      for (int i = 0;  i < lastIdx - 1; i++) {
        if (word.equals(tokenList.get(i))) {
          for (int j = i + 1; j < lastIdx && j - i <= this.contextLength; j++) {
            rightContext.append(tokenList.get(j));
            rightContext.append(' ');
          }

          for (int j = i - 1; j > 0 && i - j <= this.contextLength; j--) {
            leftContext.insert(0, tokenList.get(j) + " ");
            //System.out.println(tokenList.get(j));
          }

          pair.put(leftContext.toString(), rightContext.toString());
          this.contexts.addAll(pair.entrySet());
          rightContext.setLength(0);
          leftContext.setLength(0);
          pair.clear();
        }
      }
    }
  }

  public void displayConcordance() {
    final List<Map.Entry<String, String>> output = getContexts();
    if (output != null) {
      for (int i = 0; i < output.size(); i++) {
        Map.Entry<String, String> contextDico = output.get(i);
        System.out.format("%s\t%s\t%s%n", contextDico.getKey(),
            this.word, contextDico.getValue());
      }
    }
  }

  public static void displayHelp() {
    System.out.println("Syntax: KwicApp -options");
    System.out.println("where the options are :");
    System.out.println("\t-file <complete path of the file>");
    System.out.println("\t-word <word whose concordance needs to be generated>");
    System.out.println("\t-context <Length of the context around the word>");
  }
  /**
   * @param args arguments file, word and context.
   */
  
  static Map<String, String> parseArguments(final String[] args) {
    Map<String, String> argMap = new HashMap<String, String>();
    final int argsSize = 6;

    if (args.length == argsSize) {
      for (int i = 0; i < 5; i += 2) {
        if (args[i].charAt(0) == '-') {
          String key = args[i].substring(1).toLowerCase();
          argMap.put(key, args[i + 1]);
        } else {
          System.out.println("Syntax Error");
          displayHelp();
          argMap.clear();
        }
      }
      if (argMap.containsKey("file") && argMap.containsKey("word")
          && argMap.containsKey("context")) {
        int contextSize = Integer.parseInt(argMap.get("context"));
        if (contextSize < 0) {
          throw new NumberFormatException("negative context");
        }
      }
    } else {
      displayHelp();
    }
    return argMap;
  }

  public static void main(final String[] args) {

    Map<String, String> argMap;
    argMap = parseArguments(args);

    if (argMap.containsKey("file") && argMap.containsKey("word")
        && argMap.containsKey("context")) {
      String filePath;
      filePath = argMap.get("file");
      String word;
      word = argMap.get("word");
      int contextSize = -1;

      try {
        contextSize = Integer.parseInt(argMap.get("context"));
        KwicApp kwicInfo;
        kwicInfo = new KwicApp(filePath, word, contextSize);
        kwicInfo.searchContext();
        kwicInfo.displayConcordance();
      } catch (NumberFormatException numexp) {
        System.err.println("NumberFormatException: " + numexp.getMessage());
        System.out.println("Expecting a number for the context size");
      } catch (Exception exp) {
        System.err.println(exp.getMessage());
      }
    } else {
      System.out.println("Syntax Error");
      displayHelp();
    }
  }
}
