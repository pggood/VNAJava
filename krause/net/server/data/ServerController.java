package krause.net.server.data;

public interface ServerController {
   boolean serverShouldStop();

   void reportServerStatus(ServerStatusBlock var1);
}
