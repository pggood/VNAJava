package krause.vna.device;

public interface IVNAFlashableDevice {
   int getFirmwareLoaderBaudRate();

   String getFirmwareLoaderClassName();

   boolean hasResetButton();

   boolean supportsAutoReset();
}
