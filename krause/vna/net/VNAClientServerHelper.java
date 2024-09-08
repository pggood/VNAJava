package krause.vna.net;

import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.net.data.VNAClientRequest;
import krause.vna.net.data.VNAServerResponse;

public class VNAClientServerHelper {
   public static boolean responseTypeMatchesRequest(VNAServerResponse resp, VNAClientRequest req) {
      VNAServerResponse.SERVER_RESPONSES respType = resp.getResponseType();
      VNAClientRequest.CLIENT_CMDS reqType = req.getCommand();
      if (reqType == VNAClientRequest.CLIENT_CMDS.SCAN) {
         return respType == VNAServerResponse.SERVER_RESPONSES.SCAN_DATA || respType == VNAServerResponse.SERVER_RESPONSES.ERROR;
      } else if (reqType == VNAClientRequest.CLIENT_CMDS.START_GEN) {
         return respType == VNAServerResponse.SERVER_RESPONSES.GEN_STARTED || respType == VNAServerResponse.SERVER_RESPONSES.ERROR;
      } else if (reqType == VNAClientRequest.CLIENT_CMDS.STOP_GEN) {
         return respType == VNAServerResponse.SERVER_RESPONSES.GEN_STOPPED || respType == VNAServerResponse.SERVER_RESPONSES.ERROR;
      } else if (reqType == VNAClientRequest.CLIENT_CMDS.PING) {
         return respType == VNAServerResponse.SERVER_RESPONSES.PINGED;
      } else {
         return false;
      }
   }

   public static boolean requestTypeMatchesDriver(VNAClientRequest req, IVNADriver driver) {
      VNADeviceInfoBlock dibReq = req.getDeviceInfoBlock();
      VNADeviceInfoBlock dibDrv = driver.getDeviceInfoBlock();
      String reqType = dibReq.getType();
      String drvType = dibDrv.getType();
      if ("21".equals(reqType) && "1".equals(drvType)) {
         return true;
      } else if ("22".equals(reqType) && "2".equals(drvType)) {
         return true;
      } else {
         return "20".equals(reqType) && "0".equals(drvType);
      }
   }

   public static boolean responseDriverMatchesRequestDriver(VNAServerResponse resp, VNAClientRequest req) {
      if ("0".equals(resp.getDeviceInfoBlock().getType())) {
         return "20".equals(req.getDeviceInfoBlock().getType());
      } else {
         return "1".equals(resp.getDeviceInfoBlock().getType()) ? "21".equals(req.getDeviceInfoBlock().getType()) : false;
      }
   }
}
