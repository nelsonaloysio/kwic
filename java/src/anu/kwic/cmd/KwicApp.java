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
  private File fileObj;
  private final String word;
  private int contextLength;
  private List<Map.Entry<String, String>> contexts;

  public KwicApp(final String filePath, final String word, final int context)
            throws FileNotFoundException {
    this.word = word;
    this.contextLength = context;
    this.fileObj = new File(filePath);
    if (!this.fileObj.exists()) {
      this.fileObj = null;
      throw new FileNotFoundException("Given file: " + filePath + " does "
            + "not exist");
    }
  }

  public List<Map.Entry<String, String>> getContexts() {
    return this.contexts;
  }

  public void searchContext() {
    ArrayList<String> tokenList = new ArrayList<String>();
    try (BufferedReader br =
      new BufferedReader(new FileReader(this.fileObj))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] lineTokens = line.split(" ");
        tokenList.addAll(Arrays.asList(lineTokens));
      }
    } catch (IOException ioexp) {
      System.err.println(ioexp.getMessage());
    }
    if (!tokenList.isEmpty() && tokenList.contains(word)) {
      this.contexts = new ArrayList<Map.Entry<String, String>>();
      int lastIdx = tokenList.size();
      for (int i = 0;  i < lastIdx - 1; i++) {
        if (word.equals(tokenList.get(i))) {
          StringBuilder rightContext = new StringBuilder();
          StringBuilder leftContext = new StringBuilder();
          for (int j = i + 1; j < lastIdx && j - i <= this.contextLength; j++) {
            rightContext.append(tokenList.get(j));
            rightContext.append(" ");
          }
          
          for (int j = i - 1; j > 0 && i - j <= this.contextLength; j--) {
            leftContext.insert(0, tokenList.get(j) + " ");
            //System.out.println(tokenList.get(j));
          }
          Map<String, String> pair = new HashMap<String, String>();
          pair.put(leftContext.toString(), rightContext.toString());
          this.contexts.addAll(pair.entrySet());
        }
      }
    }
  }

  public void displayConcordance() {
    if (this.contexts != null) {
      for (int i = 0; i < this.contexts.size(); i++) {
        Map.Entry<String, String> contextDico = this.contexts.get(i);
        System.out.format("%s\t%s\t%s\n", contextDico.getKey(),
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

  public static void main(String[] args) {
    String filePath = null;
    String word = null;
    int contextSize = -1;
    KwicApp kwicInfo = null;
    if (args.length != 6) {
      displayHelp();
    } else {
      Map<String, String> argMap = new HashMap<String, String>();
      for (int i = 0; i < 5; i += 2) {
        if (args[i].startsWith("-")) {
          String key = args[i].substring(1).toLowerCase();
          argMap.put(key, args[i + 1]);
        } else {
          System.out.println("Syntax Error");
          displayHelp();
          return;
        }
      }

      if (argMap.containsKey("file") && argMap.containsKey("word")
          && argMap.containsKey("context")) {
        filePath = argMap.get("file");
        word = argMap.get("word");

        try {
          contextSize = Integer.parseInt(argMap.get("context"));
          if (contextSize < 0) {
            throw new NumberFormatException("negative context");
          }
        } catch (NumberFormatException numexp) {
          System.err.println("NumberFormatException: " + numexp.getMessage());
          System.out.println("Expecting a number for the context size");
        }

        try {
          kwicInfo = new KwicApp(filePath, word, contextSize);
          kwicInfo.searchContext();
          kwicInfo.displayConcordance();
        } catch (Exception exp) {
          System.err.println(exp.getMessage());
        } finally {
          kwicInfo = null;
        }
      } else {
        System.out.println("Syntax Error");
        displayHelp();
      }
    }
  }
}
