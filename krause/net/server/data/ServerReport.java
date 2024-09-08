package krause.net.server.data;

import java.util.HashMap;
import java.util.Map;

public class ServerReport {
   private Map<String, ServerReportItem> items = new HashMap();

   public ServerReportItem getItem(String name) {
      ServerReportItem lItem = (ServerReportItem)this.items.get(name);
      if (lItem == null) {
         lItem = new ServerReportItem();
         lItem.setName(name);
         this.items.put(name, lItem);
      }

      return lItem;
   }

   public void updateItem(ServerReportItem item) {
      ServerReportItem lItem = (ServerReportItem)this.items.get(item.getName());
      if (lItem != null) {
         lItem.setIntValue(item.getIntValue());
         lItem.setStringValue(item.getStringValue());
      }

   }

   public void incItemValue(String name) {
      ServerReportItem lItem = (ServerReportItem)this.items.get(name);
      if (lItem == null) {
         lItem = new ServerReportItem();
         lItem.setName(name);
         this.items.put(name, lItem);
      }

      lItem.incIntValue();
   }

   public void decItemValue(String name) {
      ServerReportItem lItem = (ServerReportItem)this.items.get(name);
      if (lItem == null) {
         lItem = new ServerReportItem();
         lItem.setName(name);
         this.items.put(name, lItem);
      }

      lItem.decIntValue();
   }

   public void updateItem(String name, int value) {
      ServerReportItem lItem = (ServerReportItem)this.items.get(name);
      if (lItem == null) {
         lItem = new ServerReportItem();
         lItem.setName(name);
         lItem.setIntValue(value);
         this.items.put(name, lItem);
      }

      lItem.setIntValue(value);
   }

   public void updateItem(String name, String value) {
      ServerReportItem lItem = (ServerReportItem)this.items.get(name);
      if (lItem != null) {
         lItem.setStringValue(value);
      }

   }

   public void setItems(Map<String, ServerReportItem> items) {
      this.items = items;
   }

   public Map<String, ServerReportItem> getItems() {
      return this.items;
   }
}
