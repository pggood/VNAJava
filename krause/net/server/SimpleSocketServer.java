package krause.net.server;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Properties;
import krause.common.exception.ProcessingException;
import krause.net.server.data.ServerController;
import krause.net.server.data.ServerStatusBlock;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;

public class SimpleSocketServer extends Thread {
   public static final String CONFIG_ACCEPT_PORT = "SimpleSocketServer.AcceptPort";
   public static final String CONFIG_ACCEPT_TIMEOUT = "SimpleSocketServer.AcceptTimeout";
   public static final String CONFIG_HANDLER_CLASSNAME = "SimpleSocketServer.SocketRequestHandlerClassname";
   private int acceptPort;
   private int acceptTimeout;
   private InetAddress bindAddr;
   private ServerController serverController;
   private ServerStatusBlock status = new ServerStatusBlock();
   private Properties parameters = null;
   private String socketRequestHandlerClassname = null;
   private Class<SimpleServerSocketBaseRequestHandler> socketRequestHandlerClass = null;
   private Constructor<SimpleServerSocketBaseRequestHandler> socketRequestHandlerConstructor = null;

   public SimpleSocketServer(InetAddress pBindAddr, ServerController pServerControl, Properties pProps) throws ProcessingException {
      TraceHelper.entry(this, "SimpleSocketServer");
      this.parameters = pProps;
      this.bindAddr = pBindAddr;
      this.serverController = pServerControl;
      this.acceptPort = Integer.parseInt(this.parameters.getProperty("SimpleSocketServer.AcceptPort"));
      this.acceptTimeout = Integer.parseInt(this.parameters.getProperty("SimpleSocketServer.AcceptTimeout"));
      this.setupClassFactory();
      TraceHelper.exit(this, "SimpleSocketServer");
   }
/*
   private void setupClassFactory() throws ProcessingException {
      String methodName = "setupClassFactory";
      TraceHelper.entry(this, "setupClassFactory");
      this.socketRequestHandlerClassname = this.parameters.getProperty("SimpleSocketServer.SocketRequestHandlerClassname");

      try {
         this.socketRequestHandlerClass = Class.forName(this.socketRequestHandlerClassname);
         this.socketRequestHandlerConstructor = this.socketRequestHandlerClass.getConstructor(Socket.class, Properties.class, ServerStatusBlock.class);
      } catch (NoSuchMethodException | ClassNotFoundException var3) {
         ErrorLogHelper.exception(this, "setupClassFactory", var3);
         throw new ProcessingException(var3);
      }

      TraceHelper.exit(this, "setupClassFactory");
   }
*/
private void setupClassFactory() throws ProcessingException {
    String methodName = "setupClassFactory";
    TraceHelper.entry(this, methodName);

    // Retrieve the class name from properties
    this.socketRequestHandlerClassname = this.parameters.getProperty(CONFIG_HANDLER_CLASSNAME);

    try {
        // Load the class
        Class<?> clazz = Class.forName(this.socketRequestHandlerClassname);

        // Check if it is a subclass of SimpleServerSocketBaseRequestHandler
        if (SimpleServerSocketBaseRequestHandler.class.isAssignableFrom(clazz)) {
            // Safe cast
            @SuppressWarnings("unchecked")
            Class<SimpleServerSocketBaseRequestHandler> handlerClass = (Class<SimpleServerSocketBaseRequestHandler>) clazz;
            this.socketRequestHandlerClass = handlerClass;

            // Obtain the constructor
            this.socketRequestHandlerConstructor = this.socketRequestHandlerClass.getConstructor(Socket.class, Properties.class, ServerStatusBlock.class);
        } else {
            throw new ProcessingException("Class " + this.socketRequestHandlerClassname + " is not a subclass of SimpleServerSocketBaseRequestHandler");
        }
    } catch (NoSuchMethodException | ClassNotFoundException e) {
        ErrorLogHelper.exception(this, methodName, e);
        throw new ProcessingException(e);
    }

    TraceHelper.exit(this, methodName);
}
   public void run() {
      TraceHelper.entry(this, "run");
      this.status.setStartTime(new Date());

      try {
         ServerSocket clientConnect = new ServerSocket(this.acceptPort, 0, this.bindAddr);
         clientConnect.setSoTimeout(this.acceptTimeout);

         while(!this.serverController.serverShouldStop()) {
            this.status.setLastLifesign(new Date());
            this.serverController.reportServerStatus(this.status);
            Socket clientReq = null;

            try {
               clientReq = clientConnect.accept();
               SimpleServerSocketBaseRequestHandler socketHandler = this.createNewSocketRequestHandler(clientReq);
               socketHandler.handle();
            } catch (SocketTimeoutException var15) {
            } catch (Exception var16) {
               ErrorLogHelper.exception(this, "run", var16);
            } finally {
               if (clientReq != null) {
                  try {
                     clientReq.close();
                  } catch (IOException var14) {
                     ErrorLogHelper.exception(this, "run", var14);
                  }
               }

            }
         }

         clientConnect.close();
      } catch (IOException var18) {
      }

      TraceHelper.exit(this, "run");
   }

   private SimpleServerSocketBaseRequestHandler createNewSocketRequestHandler(Socket clientReq) throws ProcessingException {
      SimpleServerSocketBaseRequestHandler rc = null;
      TraceHelper.entry(this, "createNewSocketRequestHandler");

      try {
         rc = (SimpleServerSocketBaseRequestHandler)this.socketRequestHandlerConstructor.newInstance(clientReq, this.parameters, this.status);
      } catch (IllegalArgumentException var4) {
         ErrorLogHelper.exception(this, "createNewSocketRequestHandler", var4);
         throw new ProcessingException(var4);
      } catch (InstantiationException var5) {
         ErrorLogHelper.exception(this, "createNewSocketRequestHandler", var5);
         throw new ProcessingException(var5);
      } catch (IllegalAccessException var6) {
         ErrorLogHelper.exception(this, "createNewSocketRequestHandler", var6);
         throw new ProcessingException(var6);
      } catch (InvocationTargetException var7) {
         ErrorLogHelper.exception(this, "createNewSocketRequestHandler", var7);
         throw new ProcessingException(var7);
      }

      TraceHelper.exit(this, "createNewSocketRequestHandler");
      return rc;
   }
}
