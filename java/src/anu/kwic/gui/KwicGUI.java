package anu.kwic.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import anu.kwic.cmd.KwicApp;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

class KwicGUI extends JFrame {
    
    private String filepath;
    private JTextField keywordField;
    private JTextField contextField;
    
    class Terminator extends WindowAdapter {
        @Override
	public void windowClosing(WindowEvent e) {
            JOptionPane.showMessageDialog(null, "Bye");
            System.exit(0); 
        }
    }
    
    private void displayFile(File file_path,JTextArea textArea) {
        try (BufferedReader br = new BufferedReader(new FileReader(file_path))) {
            String line;
            while ((line = br.readLine()) != null) {
                textArea.append(line);
                textArea.append("\n");
            }
        }
        catch(IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void doSearch() {
        try {
            String keyword = keywordField.getText();
            KwicApp backend= new KwicApp(filepath,keyword,Integer.parseInt(contextField.getText()));
            backend.searchContext();
            List<Map.Entry<String,String>> contexts = backend.getContexts();
            if(contexts != null) {
                
                JPanel displayPanel = new JPanel(new GridLayout(0,3));
                for(int i=0; i<contexts.size()-1; i++) {
                    Map.Entry<String,String> x = contexts.get(i);
                    displayPanel.add(new JLabel(x.getKey()));
                    displayPanel.add(new JLabel(keyword));
                    displayPanel.add(new JLabel(x.getValue()));
                    JOptionPane.showMessageDialog(null, displayPanel);
                    //System.out.format("%s\t%s\t%s\n", x.getKey(),this.word,x.getValue());
                }
            }
            
        }
        catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Context length should be an integer"
                    ,"Error",JOptionPane.ERROR_MESSAGE);
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void createMenuBar(JTextArea textArea, JPanel inputPanel) {
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        JMenuBar menubar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem searchMi = new JMenuItem("Search");
        searchMi.setToolTipText("Search keyword");
        searchMi.setEnabled(false);
        JMenuItem openMi = new JMenuItem("Open");
        openMi.setToolTipText("Open a file");
        
        JMenuItem closeMi = new JMenuItem("Close");
        closeMi.setToolTipText("Close the file");
        closeMi.setEnabled(false);
        JMenuItem exitMi = new JMenuItem("Exit");
        exitMi.setToolTipText("Exit application");
        exitMi.setMnemonic(KeyEvent.VK_X);
        
        JMenuItem aboutMi = new JMenuItem("About");
        
        openMi.addActionListener((ActionEvent event) -> {
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                 File file = fc.getSelectedFile();
                 this.filepath = file.getAbsolutePath();
                 displayFile(file,textArea);
                 closeMi.setEnabled(true);
                 searchMi.setEnabled(true);
                 openMi.setEnabled(false);
            }
        });
        
        closeMi.addActionListener((ActionEvent event) -> {
                textArea.setText("");
                closeMi.setEnabled(false);
                openMi.setEnabled(true);
                searchMi.setEnabled(false);
                this.filepath = null;
        });
        
        exitMi.addActionListener((ActionEvent event) -> {
            JOptionPane.showMessageDialog(null, "Bye");
            System.exit(0);
        });
        
        searchMi.addActionListener((ActionEvent event) -> {
            int result = JOptionPane.showConfirmDialog(null, inputPanel, 
               "Please Enter Search Values", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                doSearch();
                keywordField.setText("");
                contextField.setText("");
            }
            
        });
        
        aboutMi.addActionListener((ActionEvent event)-> {
            JOptionPane.showMessageDialog(null, "A simple implementation of\n"
                    + "Key Word In Context program\nas described in\n"
                    +  "Foundations of Statistical NLP\nby\nManning and Schütze");
        });
        
        fileMenu.add(openMi);
        fileMenu.add(closeMi);
        fileMenu.addSeparator();
        fileMenu.add(searchMi);
        fileMenu.addSeparator();
        fileMenu.add(exitMi);
        menubar.add(fileMenu);
        menubar.add(aboutMi);
        setJMenuBar(menubar);
        
    }
        
    public KwicGUI() {
        JTextArea textArea = new JTextArea(5, 20);
        keywordField = new JTextField(20);
        contextField = new JTextField(5);
        JPanel inputPanel = new JPanel(new GridLayout(0,2));
        inputPanel.add(new JLabel("Keyword:"));
        inputPanel.add(keywordField);
        inputPanel.add(new JLabel("Context length:"));
        inputPanel.add(contextField);
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);
        createMenuBar(textArea,inputPanel);
        setTitle("A Kwic Implementation");
        setSize(1000,500);
        setLocation(10,20);
        addWindowListener( new Terminator());
        this.add(scrollPane);
    }

    public static void main(String [] args) {
        JFrame gui = new KwicGUI();
        gui.setVisible(true);
    }
}
