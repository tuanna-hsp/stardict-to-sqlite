/*
 * A simple tool converting stardict database format to SQLite.
 * Copyright (C) 2015, Nguyễn Anh Tuấn
 * Email: anhtuanbk57@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

import db.SQLiteHelper;
import db.StardictManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class Main extends JFrame implements PropertyChangeListener {

    private JTextField textField;
    private JButton openButton;
    private JButton convertButton;
    private JProgressBar progressBar;
    private JTextArea logger;

    private SQLiteHelper helper = new SQLiteHelper();
    private StardictManager manager = new StardictManager();

    public Main() {
        JPanel topPanel = new JPanel(new BorderLayout());

        textField = new JTextField("No file selected.");
        textField.setEditable(false);
        textField.setPreferredSize(new Dimension(400, 20));
        topPanel.add(textField, BorderLayout.WEST);

        convertButton = new JButton("Convert");
        convertButton.setEnabled(false);
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.append("Starting...\n");
                progressBar.setVisible(true);
                ConvertingTask task = new ConvertingTask();
                task.addPropertyChangeListener(Main.this);
                task.execute();
                convertButton.setEnabled(false);
                openButton.setEnabled(false);
            }
        });
        topPanel.add(convertButton, BorderLayout.EAST);

        openButton = new JButton("Open");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                File currentDir = new File(System.getProperty("user.dir"));
                fc.setCurrentDirectory(currentDir);

                int returnVal = fc.showOpenDialog(Main.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    textField.setText(file.getPath());
                    textField.setToolTipText(file.getPath());
                    convertButton.setEnabled(true);

                    String dictName = extractDictionaryName(file.getName());
                    helper.createDatabase(dictName);
                    manager.setDictFilesLocation(file.getParent(), dictName);

                    // Show dict info to logger
                    logger.setText(null); // Clear out previous text
                    logger.append("***************************************************\n");
                    logger.append("Dictionary name: " + manager.getDictName() + "\n");
                    logger.append("Author: " + manager.getAuthor() + "\n");
                    logger.append("Word count: " + manager.getWordCount() + "\n");
                    logger.append("Synonym word count: " + manager.getSynWordCount() + "\n");
                    logger.append("***************************************************\n");
                }
            }
        });
        topPanel.add(openButton, BorderLayout.CENTER);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);

        logger = new JTextArea();
        logger.setPreferredSize(new Dimension(400, 200));
        logger.setBorder(BorderFactory.createLoweredBevelBorder());

        this.add(topPanel, BorderLayout.NORTH);
        this.add(logger, BorderLayout.CENTER);
        this.add(progressBar, BorderLayout.SOUTH);

        this.pack();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle("Stardict to SQLite converter");
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private String extractDictionaryName(String filename) {
        // Remove file extension
        int i = filename.lastIndexOf('.');
        return filename.substring(0, i);
}

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("progress")) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        }
    }

    private class ConvertingTask extends SwingWorker<Void, Void> {
        @Override
        public Void doInBackground() {
            int count = 0;
            int totalWords = manager.getSynWordCount() + manager.getWordCount();

            logger.append("Processing .idx and .dict files...\n");
            while (manager.nextWordAvailable()) {
                StardictManager.StardictWord word = manager.nextStardictWord();
                helper.insertToMainTable(word.getWord(), word.getDefinition());
                setProgress(++count * 100 / totalWords);
            }
            helper.flushMainTable();

            if (manager.hasSynFile()) {
                logger.append("Processing .syn file...\n");
                while (manager.nextSynWordAvailable()) {
                    StardictManager.SynWord word = manager.nextSynWord();
                    helper.insertToSynTable(word.getWord(), word.getSynIndex());
                    setProgress(++count * 100 / totalWords);
                }
            } else if (manager.getSynWordCount() > 0)
                logger.append("Missing synonym file!\n");

            progressBar.setVisible(false);
            logger.append("Writing data to disk...\n");
            helper.writeDatabaseToDisk();
            helper.closeDatabase();
            logger.append("All finished.");
            convertButton.setEnabled(true);
            openButton.setEnabled(true);

            return null;
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
