package krause.vna.device;

import krause.common.exception.ProcessingException;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrated.VNACalibrationPoint;
import krause.vna.data.calibrationkit.VNACalibrationKit;

public interface IVNADriverMathHelper {
   IVNADriver getDriver();

   void setDriver(IVNADriver var1);

   VNACalibrationBlock createCalibrationBlockFromRaw(VNACalibrationContext var1, VNASampleBlock var2, VNASampleBlock var3, VNASampleBlock var4, VNASampleBlock var5) throws ProcessingException;

   VNACalibratedSampleBlock createCalibratedSamples(VNACalibrationContext var1, VNASampleBlock var2);

   void createCalibrationPoints(VNACalibrationContext var1, VNACalibrationBlock var2);

   VNACalibratedSample createCalibratedSample(VNACalibrationContext var1, VNABaseSample var2, VNACalibrationPoint var3);

   VNACalibrationPoint createCalibrationPoint(VNACalibrationContext var1, VNABaseSample var2, VNABaseSample var3, VNABaseSample var4, VNABaseSample var5);

   VNACalibrationContext createCalibrationContextForCalibrationPoints(VNACalibrationBlock var1, VNACalibrationKit var2);

   VNACalibrationContext createCalibrationContextForCalibratedSamples(VNACalibrationBlock var1);

   void applyFilter(VNABaseSample[] var1);
}
