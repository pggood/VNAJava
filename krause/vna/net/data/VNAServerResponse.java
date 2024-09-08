package krause.vna.net.data;

import java.io.Serializable;
import krause.vna.data.VNASampleBlock;
import krause.vna.device.VNADeviceInfoBlock;

public class VNAServerResponse implements Serializable {
   private VNAClientRequest request;
   private String infoText;
   private VNASampleBlock data;
   private VNAServerResponse.SERVER_RESPONSES responseType;
   private VNADeviceInfoBlock dib;

   public VNAServerResponse(VNAServerResponse.SERVER_RESPONSES pType) {
      this.responseType = pType;
   }

   public VNAClientRequest getRequest() {
      return this.request;
   }

   public void setRequest(VNAClientRequest request) {
      this.request = request;
   }

   public VNASampleBlock getData() {
      return this.data;
   }

   public void setData(VNASampleBlock data) {
      this.data = data;
   }

   public void setInfoText(String infoText) {
      this.infoText = infoText;
   }

   public String getInfoText() {
      return this.infoText;
   }

   public void setResponseType(VNAServerResponse.SERVER_RESPONSES responseType) {
      this.responseType = responseType;
   }

   public VNAServerResponse.SERVER_RESPONSES getResponseType() {
      return this.responseType;
   }

   public void setDeviceInfoBlock(VNADeviceInfoBlock dib) {
      this.dib = dib;
   }

   public VNADeviceInfoBlock getDeviceInfoBlock() {
      return this.dib;
   }

   public static enum SERVER_RESPONSES {
      SCAN_DATA,
      GEN_STARTED,
      GEN_STOPPED,
      ERROR,
      PINGED,
      FIRMWARE_VERSION;
   }
}
