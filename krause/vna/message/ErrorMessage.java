package krause.vna.message;

public class ErrorMessage extends GenericMessage {
   public ErrorMessage(String error) {
      super("Error", GenericMessage.MESSAGE_TYPE.ERROR);
      this.setMessage(this.getMessage() + error);
   }
}
