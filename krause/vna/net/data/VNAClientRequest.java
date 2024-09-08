package krause.vna.net.data;

import java.io.Serializable;
import krause.vna.device.VNADeviceInfoBlock;

public class VNAClientRequest implements Serializable {
   private long startFrequency;
   private long stopFrequency;
   private int numberOfSamples;
   private boolean transmissionMode;
   private VNADeviceInfoBlock dib;
   private int frequencyI;
   private int frequencyQ;
   private int attenuationI;
   private int attenuationQ;
   private int phase;
   private int mainAttenuation;
   private VNAClientRequest.CLIENT_CMDS command;

   public VNAClientRequest(VNAClientRequest.CLIENT_CMDS cmd, VNADeviceInfoBlock deviceInfoBlock) {
      this.command = cmd;
      this.dib = deviceInfoBlock;
   }

   public long getStartFrequency() {
      return this.startFrequency;
   }

   public void setStartFrequency(long startFrequency) {
      this.startFrequency = startFrequency;
   }

   public long getStopFrequency() {
      return this.stopFrequency;
   }

   public void setStopFrequency(long stopFrequency) {
      this.stopFrequency = stopFrequency;
   }

   public int getNumberOfSamples() {
      return this.numberOfSamples;
   }

   public void setNumberOfSamples(int numberOfSamples) {
      this.numberOfSamples = numberOfSamples;
   }

   public boolean isTransmissionMode() {
      return this.transmissionMode;
   }

   public void setTransmissionMode(boolean transmissionMode) {
      this.transmissionMode = transmissionMode;
   }

   public VNAClientRequest.CLIENT_CMDS getCommand() {
      return this.command;
   }

   public void setCommand(VNAClientRequest.CLIENT_CMDS command) {
      this.command = command;
   }

   public VNADeviceInfoBlock getDeviceInfoBlock() {
      return this.dib;
   }

   public int getFrequencyI() {
      return this.frequencyI;
   }

   public void setFrequencyI(int frequencyI) {
      this.frequencyI = frequencyI;
   }

   public int getFrequencyQ() {
      return this.frequencyQ;
   }

   public void setFrequencyQ(int frequencyQ) {
      this.frequencyQ = frequencyQ;
   }

   public int getAttenuationI() {
      return this.attenuationI;
   }

   public void setAttenuationI(int attenuationI) {
      this.attenuationI = attenuationI;
   }

   public int getAttenuationQ() {
      return this.attenuationQ;
   }

   public void setAttenuationQ(int attenuationQ) {
      this.attenuationQ = attenuationQ;
   }

   public int getPhase() {
      return this.phase;
   }

   public void setPhase(int phase) {
      this.phase = phase;
   }

   public int getMainAttenuation() {
      return this.mainAttenuation;
   }

   public void setMainAttenuation(int mainAttenuation) {
      this.mainAttenuation = mainAttenuation;
   }

   public static enum CLIENT_CMDS {
      PING,
      SCAN,
      START_GEN,
      STOP_GEN,
      READ_FIRMWARE;
   }
}
