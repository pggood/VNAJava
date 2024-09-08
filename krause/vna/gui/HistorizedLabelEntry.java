package krause.vna.gui;

public class HistorizedLabelEntry {
   private String text;
   private long timestamp;

   public HistorizedLabelEntry(String pText, long pTime) {
      this.text = pText;
      this.timestamp = pTime;
   }

   public String getText() {
      return this.text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public long getTimestamp() {
      return this.timestamp;
   }

   public void setTimestamp(long timestamp) {
      this.timestamp = timestamp;
   }
}
