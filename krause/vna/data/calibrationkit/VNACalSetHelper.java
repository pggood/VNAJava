package krause.vna.data.calibrationkit;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;

public class VNACalSetHelper {
public boolean save(List<VNACalibrationKit> calSets, String myFileName) {
    TraceHelper.entry(this, "save");
    boolean result = false;
    TraceHelper.text(this, "save", "Trying to write to [" + myFileName + "]");

    FileOutputStream fos = null;
    XMLEncoder enc = null;

    try {
        // Initialize FileOutputStream and XMLEncoder
        fos = new FileOutputStream(myFileName);
        enc = new XMLEncoder(fos);

        // Write each VNACalibrationKit object to XML
        for (VNACalibrationKit aCalSet : calSets) {
            enc.writeObject(aCalSet);
        }

        result = true;  // Indicate success
    } catch (IOException e) {
        // Handle IOExceptions that occur during file operations
        ErrorLogHelper.exception(this, "save", e);
        ErrorLogHelper.text(this, "save", e.getMessage());
    } finally {
        // Ensure resources are closed properly
        if (enc != null) {
            try {
                enc.close();
            } catch (Exception e) {
                // Log or handle exception during closing
                ErrorLogHelper.exception(this, "save", e);
            }
        }
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                // Log or handle exception during closing
                ErrorLogHelper.exception(this, "save", e);
            }
        }
    }

    TraceHelper.exitWithRC(this, "save", result);
    return result;
}


public List<VNACalibrationKit> load(String myFileName) {
    TraceHelper.entry(this, "load");
    List<VNACalibrationKit> result = new ArrayList<>();
    TraceHelper.text(this, "load", "Trying to read from [" + myFileName + "]");

    FileInputStream fis = null;
    XMLDecoder dec = null;

    try {
        // Initialize FileInputStream and XMLDecoder
        fis = new FileInputStream(myFileName);
        dec = new XMLDecoder(fis);

        // Read and add VNACalibrationKit objects to the result list
        while (true) {
            VNACalibrationKit calKit = (VNACalibrationKit) dec.readObject();
            if (calKit == null) {
                break; // End of input
            }
            result.add(calKit);
        }
    } catch (ArrayIndexOutOfBoundsException e) {
        // Handle or log the ArrayIndexOutOfBoundsException if needed
        TraceHelper.text(this, "load", "ArrayIndexOutOfBoundsException occurred: " + e.getMessage());
    } catch (IOException e) {
        // Handle IOExceptions (e.g., file not found)
        TraceHelper.text(this, "load", "file [" + myFileName + "] not found. Using defaults.");
    } finally {
        // Ensure XMLDecoder and FileInputStream are closed
        if (dec != null) {
            try {
                dec.close();
            } catch (Exception e) {
                // Log any exception during closing
                TraceHelper.text(this, "load", "Exception occurred while closing XMLDecoder: " + e.getMessage());
            }
        }
        if (fis != null) {
            try {
                fis.close();
            } catch (IOException e) {
                // Log any exception during closing
                TraceHelper.text(this, "load", "Exception occurred while closing FileInputStream: " + e.getMessage());
            }
        }
    }

    // Default value if no valid objects were loaded
    if (result.isEmpty()) {
        result.add(new VNACalibrationKit("DEFAULT"));
    }

    TraceHelper.exitWithRC(this, "load", result);
    return result;
}

}
