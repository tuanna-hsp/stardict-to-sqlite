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

package db;

import java.io.File;

public class StardictManager {

    private IdxFileLoader idxLoader;
    private DictFileLoader dictLoader;
    private SynFileLoader synLoader;
    private IfoFileReader ifoReader;

    public void setDictFilesLocation(String path, String dictName) {
        String pathToDict = path + dictName;

        idxLoader = new IdxFileLoader(new File(pathToDict + ".idx"));
        dictLoader = new DictFileLoader(new File(pathToDict + ".dict"));
        ifoReader = new IfoFileReader(new File(pathToDict + ".ifo"));

        File synFile = new File(pathToDict + ".syn");
        if (synFile.isFile())
            synLoader = new SynFileLoader(synFile);
    }

    public boolean nextWordAvailable() {
        return idxLoader.available();
    }

    public StardictWord nextStardictWord() {
        String word = idxLoader.getNextWord();
        String definition = dictLoader.getDefinition(
                idxLoader.getDataOffset(),
                idxLoader.getDataSize());

        return new StardictWord(word, definition);
    }

    public boolean hasSynFile() {
        return (synLoader != null);
    }

    public boolean nextSynWordAvailable() {
        return ((synLoader != null) && synLoader.available());
    }

    public SynWord nextSynWord() {
        String word = synLoader.getNextWord();
        int synIndex = synLoader.getSynIndex();
        return new SynWord(word, synIndex);
    }

    public String getDictName() {
        return ifoReader.getDictName();
    }

    public String getAuthor() {
        return ifoReader.getAuthor();
    }

    public int getWordCount() {
        return ifoReader.getWordCount();
    }

    public int getSynWordCount() {
        return ifoReader.getSynWordCount();
    }

    public static class StardictWord {
        private String word;
        private String definition;

        public StardictWord(String word, String definition) {
            this.word = word;
            this.definition = definition;
        }

        public String getWord() {
            return word;
        }

        public String getDefinition() {
            return definition;
        }
    }

    public static class SynWord {
        private String word;
        private int synIndex;

        public SynWord(String word, int synIndex) {
            this.word = word;
            this.synIndex = synIndex;
        }

        public String getWord() {
            return word;
        }

        public int getSynIndex() {
            return synIndex;
        }
    }
}
