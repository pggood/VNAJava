package krause.net.server.data;

import java.util.Date;

public class ServerStatusBlock {
   private Date startTime;
   private Date stopTime;
   private Date lastLifesign;
   private ServerReport report = new ServerReport();

   public Date getStartTime() {
      return this.startTime;
   }

   public void setStartTime(Date startTime) {
      this.startTime = startTime;
   }

   public Date getStopTime() {
      return this.stopTime;
   }

   public void setStopTime(Date stopTime) {
      this.stopTime = stopTime;
   }

   public ServerReport getReport() {
      return this.report;
   }

   public void setReport(ServerReport report) {
      this.report = report;
   }

   public void setLastLifesign(Date lastLifesign) {
      this.lastLifesign = lastLifesign;
   }

   public Date getLastLifesign() {
      return this.lastLifesign;
   }
}
