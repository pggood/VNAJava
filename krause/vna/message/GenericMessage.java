package krause.vna.message;

import krause.vna.resources.VNAMessages;

public abstract class GenericMessage {
   private String message;
   private GenericMessage.MESSAGE_TYPE type;

   public GenericMessage() {
   }

   public GenericMessage(String id, GenericMessage.MESSAGE_TYPE info) {
      this.setMessage(VNAMessages.getString(id));
      this.setType(info);
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public GenericMessage.MESSAGE_TYPE getType() {
      return this.type;
   }

   public void setType(GenericMessage.MESSAGE_TYPE type) {
      this.type = type;
   }

   public static enum MESSAGE_TYPE {
      INFO,
      WARN,
      ERROR,
      FATAL;
   }
}
