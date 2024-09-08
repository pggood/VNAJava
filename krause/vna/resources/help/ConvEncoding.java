package krause.vna.resources.help;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConvEncoding {
   public static void main(String[] args) {
    BufferedReader br = null;
    FileOutputStream fileOut = null;
    
    try {
        // Open input file
        br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), StandardCharsets.UTF_8));
        
        // Load properties from file
        Properties properties = new Properties();
        String line;
        while ((line = br.readLine()) != null) {
            int i = line.indexOf('=');
            if (i != -1) {
                String key = line.substring(0, i);
                String value = line.substring(i + 1);
                properties.put(key, value);
            }
        }
        
        // Save properties to output file
        File file = new File(args[1]);
        fileOut = new FileOutputStream(file);
        properties.store(fileOut, ConvEncoding.class.getCanonicalName());
    } catch (IOException e) {
        // Handle IOExceptions
        System.err.println("An error occurred: " + e.getMessage());
        e.printStackTrace();
    } finally {
        // Ensure resources are closed
        try {
            if (br != null) br.close();
            if (fileOut != null) fileOut.close();
        } catch (IOException e) {
            // Handle exceptions thrown while closing resources
            System.err.println("Failed to close resources: " + e.getMessage());
            e.printStackTrace();
        }
    }
  }
}
