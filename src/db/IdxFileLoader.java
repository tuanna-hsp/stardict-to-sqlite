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

public class IdxFileLoader extends FileBytesLoader {

    private int currentIndex;
    private String word;

    // Despite stardict uses 32 or 64 bits unsigned integer to represent data offset and size,
    // i just use java 32 bits signed int. It's ok because for most cases the .dict
    // file's size is not likely to go past 2^31 = 2GB.
    private int dataOffset;
    private int dataSize;

    public IdxFileLoader(File file) {
        super(file);
    }

    public boolean available() {
        return (currentIndex < fileBytes.length);
    }

    public String getNextWord() {
        readNext();
        return word;
    }

    public int getDataOffset() {
        return dataOffset;
    }

    public int getDataSize() {
        return dataSize;
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
        byte[] dataOffsetBytes = new byte[4];
        System.arraycopy(fileBytes, currentIndex, dataOffsetBytes, 0, 4);
        dataOffset = Utilities.byteArrayToInt(dataOffsetBytes);

        currentIndex += 4;
        byte[] dataSizeBytes = new byte[4];
        System.arraycopy(fileBytes, currentIndex, dataSizeBytes, 0, 4);
        dataSize = Utilities.byteArrayToInt(dataSizeBytes);

        // Point to next word
        currentIndex += 4;
    }
}
