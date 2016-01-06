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
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeMap;

public class IfoFileReader {

    private TreeMap<String, String> info = new TreeMap<String, String>();

    public IfoFileReader(File file) {
        Scanner scanner;
        try {
            scanner = new Scanner(file, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (isFieldValuePair(line)) {
                String[] pair = line.split("=", 2);
                info.put(pair[0], pair[1]);
            }
        }
    }

    private boolean isFieldValuePair(String s) {
        return (s.indexOf('=') != -1);
    }

    public String getDictName() {
        return info.get("bookname");
    }

    public String getAuthor() {
        String author = info.get("author");
        return (author != null) ? author : "Not available";
    }

    public int getWordCount() {
        return Integer.parseInt(info.get("wordcount"));
    }

    public int getSynWordCount() {
        String synCountStr = info.get("synwordcount");
        return (synCountStr != null) ? Integer.parseInt(synCountStr) : 0;
    }
}
