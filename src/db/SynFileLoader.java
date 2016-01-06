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

import others.Constants;
import others.Utilities;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class SynFileLoader extends FileBytesLoader {

    private int currentIndex;
    private String word;
    private int synIndex;

    public SynFileLoader(File file) {
        super(file);
    }

    public boolean available() {
        return (currentIndex < fileBytes.length);
    }

    public String getNextWord() {
        readNext();
        return word;
    }

    private void readNext() {
        int baseIndex = currentIndex;

        // Find string terminator
        while (fileBytes[currentIndex] != Constants.UTF8_END_BYTE)
            currentIndex++;

        int wordBytesCount = currentIndex - baseIndex;
        try {
            word = new String(fileBytes, baseIndex, wordBytesCount, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        currentIndex++;
        byte[] synIndexBytes = new byte[4];
        System.arraycopy(fileBytes, currentIndex, synIndexBytes, 0, 4);
        synIndex = Utilities.byteArrayToInt(synIndexBytes);

        // Point to next word
        currentIndex += 4;
    }

    public int getSynIndex() {
        return synIndex;
    }
}
