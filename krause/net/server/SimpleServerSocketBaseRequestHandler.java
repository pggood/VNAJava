package krause.net.server;

import java.net.Socket;
import java.util.Properties;
import krause.net.server.data.ServerStatusBlock;
import krause.util.ras.logging.TraceHelper;

public abstract class SimpleServerSocketBaseRequestHandler {
   public static final String CONFIG_READ_TIMEOUT = "SocketRequestHandler.ReadTimeout";
   public static final String CONFIG_LINGER_TIMEOUT = "SocketRequestHandler.LingerTimeout";
   protected Properties parameters;
   protected Socket socket;
   protected ServerStatusBlock status;
   protected int socketTimeout = 10000;
   protected int lingerTime = 100;

   public SimpleServerSocketBaseRequestHandler(Socket clientReq, Properties pProps, ServerStatusBlock pStatus) {
      TraceHelper.entry(this, "SimpleServerSocketBaseRequestHandler");
      this.socket = clientReq;
      this.status = pStatus;
      this.parameters = pProps;
      this.socketTimeout = Integer.parseInt(pProps.getProperty("SocketRequestHandler.ReadTimeout"));
      this.lingerTime = Integer.parseInt(pProps.getProperty("SocketRequestHandler.LingerTimeout"));
      TraceHelper.exit(this, "SimpleServerSocketBaseRequestHandler");
   }

   public abstract void handle() throws Exception;

   public Properties getParameters() {
      return this.parameters;
   }

   public void setParameters(Properties parameters) {
      this.parameters = parameters;
   }

   public Socket getSocket() {
      return this.socket;
   }

   public void setSocket(Socket socket) {
      this.socket = socket;
   }

   public ServerStatusBlock getStatus() {
      return this.status;
   }

   public void setStatus(ServerStatusBlock status) {
      this.status = status;
   }
}
