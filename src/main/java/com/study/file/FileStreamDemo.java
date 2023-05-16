/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.study.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileStreamDemo {
    public static void main(String[] args) throws IOException {
        String filePath = "/Users/lizhimin/Downloads/example.txt";
        int fileSize = 8192;
        byte[] data = new byte[fileSize];

        // Fill the data with random bytes.
        for (int i = 0; i < fileSize; i++) {
            data[i] = (byte) (Math.random() * 256 - 128);
        }

        // Write the data to the file.
        FileOutputStream fos = new FileOutputStream(new File(filePath));
        fos.write(data);
        fos.close();

        main2(args);
    }

    public static void main2(String[] args) throws IOException {
        String filePath = "/Users/lizhimin/Downloads/example.txt";
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // Open the file and wrap the input stream in a buffered stream for better performance.
        InputStream is = new BufferedInputStream(Files.newInputStream(Paths.get(filePath)));

        // Read the file in chunks of 1024 bytes.
        int bytesRead;
        do {
            // Process the data here.
            // For example, you could write it to another file, or print it to the console.
            // System.out.println(new String(buffer, 0, bytesRead));

            bytesRead = is.read(buffer, 0, bufferSize);
            System.out.println(bytesRead);
        }
        while (bytesRead != -1);

        // Close the input stream when you're done with it.
        is.close();
    }
}