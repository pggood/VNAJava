package krause.vna.firmware;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.IVNAFlashableDevice;
import org.jdesktop.swingworker.SwingWorker;

public class VnaBackgroundFlashBurner extends SwingWorker<Integer, String> implements StringMessenger {
   private boolean autoReset = false;
   private IVNABackgroundFlashBurnerConsumer consumer = null;
   private IVNADriver driver = null;
   private FirmwareFileParser flashFile = null;
   private SimpleStringListbox listbox = null;

   public Integer doInBackground() {
      int rc = 0;
      TraceHelper.entry(this, "doInBackground");

      try {
         this.publish(new String[]{"Checking for matching firmware loader ... "});
         String flasherClassName = ((IVNAFlashableDevice)this.getDriver()).getFirmwareLoaderClassName();
         if (flasherClassName != null) {
            this.publish(new String[]{"Starting firmware download ... "});
            IVNAFirmwareFlasher burner = (IVNAFirmwareFlasher)Class.forName(flasherClassName.trim()).getDeclaredConstructor().newInstance();
            burner.setMessenger(this);
            burner.setAutoReset(this.autoReset);
            burner.burnBuffer(this.getFlashFile(), this.getDriver());
         } else {
            this.publish(new String[]{"Firmware flashing not supported in this configuration"});
         }
      } catch (Exception var4) {
         ErrorLogHelper.exception(this, "doInBackground", var4);
         this.publish(new String[]{var4.getMessage()});
         rc = 1;
      }

      TraceHelper.exit(this, "doInBackground");
      return Integer.valueOf(rc);
   }

   protected void done() {
      TraceHelper.entry(this, "done");

      try {
         Integer rc = (Integer)this.get();
         this.consumer.consumeReturnCode(rc);
      } catch (InterruptedException var2) {
         ErrorLogHelper.exception(this, "done", var2);
      } catch (ExecutionException var3) {
         ErrorLogHelper.exception(this, "done", var3);
      }

      TraceHelper.exit(this, "done");
   }

   public IVNADriver getDriver() {
      return this.driver;
   }

   public FirmwareFileParser getFlashFile() {
      return this.flashFile;
   }

   public SimpleStringListbox getListbox() {
      return this.listbox;
   }

   protected void process(List<String> messages) {
      if (this.listbox != null) {
         Iterator var3 = messages.iterator();

         while(var3.hasNext()) {
            String message = (String)var3.next();
            this.listbox.addMessage(message);
         }
      }

   }

   public void publishMessage(String message) {
      this.publish(new String[]{message});
   }

   public void setAutoReset(boolean selected) {
      this.autoReset = selected;
   }

   public void setDataConsumer(IVNABackgroundFlashBurnerConsumer pConsumer) {
      TraceHelper.entry(this, "setDataConsumer");
      this.consumer = pConsumer;
      TraceHelper.exit(this, "setDataConsumer");
   }

   public void setDriver(IVNADriver driver) {
      this.driver = driver;
   }

   public void setFlashFile(FirmwareFileParser hexFile) {
      this.flashFile = hexFile;
   }

   public void setListbox(SimpleStringListbox listbox) {
      this.listbox = listbox;
   }
}
