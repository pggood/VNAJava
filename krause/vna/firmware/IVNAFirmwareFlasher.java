package krause.vna.firmware;

import krause.common.exception.ProcessingException;
import krause.vna.device.IVNADriver;

public interface IVNAFirmwareFlasher {
   void burnBuffer(FirmwareFileParser var1, IVNADriver var2) throws ProcessingException;

   String getDeviceType();

   int getPageSize();

   int getFlashSize();

   int getEEpromSize();

   int getPagePtr();

   int getRetryCount();

   void setAutoReset(boolean var1);

   void setMessenger(StringMessenger var1);
}
