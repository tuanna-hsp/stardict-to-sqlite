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

import java.io.*;

public abstract class FileBytesLoader {
    protected byte[] fileBytes;

    public FileBytesLoader(File file) {
        try {
            FileInputStream fileIn = new FileInputStream(file);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[524288];   // 512KB
            int bytesCount;

            // Read bytes content of this file and temporarily
            // store in an  bytes stream
            while (fileIn.available() != 0) {
                bytesCount = fileIn.read(buffer);
                outStream.write(buffer, 0, bytesCount);
            }

            fileBytes = outStream.toByteArray();
            fileIn.close();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
