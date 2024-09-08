package krause.vna.message;

import java.text.MessageFormat;
import krause.vna.resources.VNAMessages;

public class InfoMessage extends GenericMessage {
   public InfoMessage(String id) {
      super(id, GenericMessage.MESSAGE_TYPE.INFO);
   }

   public InfoMessage(String id, long i) {
      this.setMessage(MessageFormat.format(VNAMessages.getString(id), i));
      this.setType(GenericMessage.MESSAGE_TYPE.INFO);
   }

   public InfoMessage(String id, String s) {
      this.setMessage(MessageFormat.format(VNAMessages.getString(id), s));
      this.setType(GenericMessage.MESSAGE_TYPE.INFO);
   }

   public InfoMessage(String id, long i, long j) {
      this.setMessage(MessageFormat.format(VNAMessages.getString(id), i, j));
      this.setType(GenericMessage.MESSAGE_TYPE.INFO);
   }

   public InfoMessage(String id, long a, long b, long c) {
      this.setMessage(MessageFormat.format(VNAMessages.getString(id), a, b, c));
      this.setType(GenericMessage.MESSAGE_TYPE.INFO);
   }

   public InfoMessage(String id, long start, long stop, long i, long j) {
      this.setMessage(MessageFormat.format(VNAMessages.getString(id), start, stop, i, j));
      this.setType(GenericMessage.MESSAGE_TYPE.INFO);
   }

   public InfoMessage(String id, long start, long stop, long i, long j, long k) {
      this.setMessage(MessageFormat.format(VNAMessages.getString(id), start, stop, i, j, k));
      this.setType(GenericMessage.MESSAGE_TYPE.INFO);
   }
}
