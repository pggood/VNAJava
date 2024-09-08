package krause.vna.device;

import java.util.List;
import krause.common.exception.DialogNotImplementedException;
import krause.common.exception.InitializationException;
import krause.common.exception.ProcessingException;
import krause.common.validation.ValidationResults;
import krause.vna.background.IVNABackgroundTaskStatusListener;
import krause.vna.data.VNAFrequencyRange;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.VNAScanMode;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.calibrate.mode1.VNACalibrationRange;

public interface IVNADriver {
   long calculateInternalFrequencyValue(long var1);

   void destroy();

   VNACalibrationRange[] getCalibrationRanges();

   VNAScanMode getDefaultMode();

   String getDeviceFirmwareInfo();

   VNADeviceInfoBlock getDeviceInfoBlock();

   Double getDeviceSupply();

   Double getDeviceTemperature();

   String getDriverConfigPrefix();

   IVNADriverMathHelper getMathHelper();

   List<String> getPortList() throws ProcessingException;

   String getPortname();

   VNACalibrationRange[] getSpecificCalibrationRanges();

   void init() throws InitializationException;

   boolean isScanSupported(int var1, VNAFrequencyRange var2, VNAScanMode var3);

   VNASampleBlock scan(VNAScanMode var1, long var2, long var4, int var6, IVNABackgroundTaskStatusListener var7) throws ProcessingException;

   void setPortname(String var1);

   void showDriverDialog(VNAMainFrame var1);

   void showDriverNetworkDialog(VNAMainFrame var1);

   void showGeneratorDialog(VNAMainFrame var1) throws DialogNotImplementedException;

   void startGenerator(long var1, long var3, int var5, int var6, int var7, int var8) throws ProcessingException;

   void stopGenerator() throws ProcessingException;

   ValidationResults validateScanRange(VNAScanRange var1);

   boolean checkForDevicePresence(boolean var1);
}
