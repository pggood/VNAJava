package krause.vna.gui.update;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import krause.util.ras.logging.TraceHelper;
import krause.vna.resources.VNAMessages;
import krause.vna.update.FileDownloadJob;

public class VNAUpdateTableModel extends AbstractTableModel {
   private List<FileDownloadJob> jobs = new ArrayList();
   private String[] columnNames = new String[]{VNAMessages.getString("VNAUpdateTableModel.filename"), VNAMessages.getString("VNAUpdateTableModel.filesize"), VNAMessages.getString("VNAUpdateTableModel.status")};

   public int getRowCount() {
      return this.jobs.size();
   }

   public int getColumnCount() {
      return this.columnNames.length;
   }

   public Object getValueAt(int rowIndex, int columnIndex) {
      Object rc = "";
      FileDownloadJob job = (FileDownloadJob)this.jobs.get(rowIndex);
      if (columnIndex == 0) {
         rc = job.getFile().getLocalFileName();
      } else if (columnIndex == 1) {
         rc = job.getFile().getFileSize();
      } else if (columnIndex == 2) {
         int s = job.getStatus();
         if (s == -1) {
            rc = "New";
         } else if (s == 0) {
            rc = "Downloading ...";
         } else if (s == 1) {
            rc = "OK";
         } else if (s == 3) {
            rc = "Abort";
         } else if (s == 2) {
            rc = "Error";
         } else {
            rc = "???";
         }
      } else {
         rc = "???";
      }

      return rc;
   }

   public List<FileDownloadJob> getJobs() {
      return this.jobs;
   }

   public void addElement(FileDownloadJob block) {
      this.jobs.add(block);
      this.fireTableDataChanged();
   }

   public String getColumnName(int column) {
      return this.columnNames[column];
   }

   public void clear() {
      this.jobs.clear();
      this.fireTableDataChanged();
   }

   public void updateElement(FileDownloadJob job) {
      TraceHelper.entry(this, "updateElement");
      this.fireTableDataChanged();
      TraceHelper.exit(this, "updateElement");
   }
}
