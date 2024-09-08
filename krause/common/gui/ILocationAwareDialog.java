package krause.common.gui;

public interface ILocationAwareDialog {
   void restoreWindowPosition();

   void restoreWindowSize();

   void storeWindowPosition();

   void storeWindowSize();

   void showInPlace();
}
