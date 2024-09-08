package krause.vna.data.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.LogHelper;
import krause.util.ras.logging.TraceHelper;

public final class VNABaseFilterHelper {
   private VNABaseFilterHelper() {
   }

public static double[] loadFilterParameters(String fName) {
    String methodName = "loadFilterParameters";
    double[] rc = null;
    TraceHelper.entry(VNABaseFilterHelper.class, methodName);
    ArrayList<Double> listParameters = new ArrayList<>();

    try (FileInputStream fstream = new FileInputStream(fName);
         DataInputStream dis = new DataInputStream(fstream);
         BufferedReader br = new BufferedReader(new InputStreamReader(dis))) {

        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.length() > 0) {
                try {
                    Double aParm = Double.valueOf(line);
                    listParameters.add(aParm);
                } catch (NumberFormatException e) {
                    ErrorLogHelper.text(VNABaseFilterHelper.class, methodName, "Invalid number format in line: " + line);
                }
            } else {
                ErrorLogHelper.text(VNABaseFilterHelper.class, methodName, "Empty line ignored");
            }
        }

        TraceHelper.text(VNABaseFilterHelper.class, methodName, "Filter file read");
    } catch (IOException e) {
        ErrorLogHelper.exception(VNABaseFilterHelper.class, methodName, e);
    }

    int paramSize = listParameters.size();
    TraceHelper.text(VNABaseFilterHelper.class, methodName, "Filter with " + paramSize + " lines");
    if (paramSize > 0 && paramSize % 2 == 1) {
        rc = new double[paramSize];
        int i = 0;
        for (Double aDouble : listParameters) {
            rc[i++] = aDouble;
        }
    }

    if (rc == null) {
        LogHelper.text(VNABaseFilterHelper.class, methodName, "Creating default filter set");
        rc = (new Gaussian(1.0D)).kernel1D(15);
        saveFilterdata(fName, rc);
    }

    TraceHelper.exitWithRC(VNABaseFilterHelper.class, methodName, rc);
    return rc;
}


public static void saveFilterdata(String filename, double[] parms) {
    String methodName = "saveFilterdata";
    TraceHelper.entry(VNABaseFilterHelper.class, methodName);

    try (FileOutputStream os = new FileOutputStream(filename);
         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os))) {

        for (double parm : parms) {
            bw.write(Double.toString(parm));
            bw.newLine();
        }

        TraceHelper.text(VNABaseFilterHelper.class, methodName, "Filter file [" + filename + "] written");

    } catch (IOException e) {
        ErrorLogHelper.exception(VNABaseFilterHelper.class, methodName, e);
    }

    TraceHelper.exit(VNABaseFilterHelper.class, methodName);
}

}
