package krause.vna.data;

import java.util.List;
import krause.vna.background.VNABackgroundJob;

public interface IVNADataConsumer {
   void consumeDataBlock(List<VNABackgroundJob> var1);
}
