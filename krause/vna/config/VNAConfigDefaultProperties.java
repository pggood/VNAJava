package krause.vna.config;

import java.util.Properties;

public class VNAConfigDefaultProperties extends Properties {
   public VNAConfigDefaultProperties() {
      this.put("ErrorLogger.classname", "krause.util.ras.logging.ConsoleErrorLogger");
      this.put("ErrorLogger.logging", "true");
      this.put("ErrorLogger.shortclassname", "true");
      this.put("ApplicationLogger.classname", "krause.util.ras.logging.ConsoleLogger");
      this.put("ApplicationLogger.logging", "false");
      this.put("ApplicationLogger.shortclassname", "true");
      this.put("Tracer.classname", "krause.util.ras.logging.ConsoleTracer");
      this.put("Tracer.tracing", "false");
      this.put("Tracer.shortclassname", "true");
      this.put("askOnExit", "false");
      this.put("showToolbar", "true");
      this.put("VNA.type", "0");
      this.put("VNADriver.Sample.PortName", "DummySamplePort");
      this.put("VNA.exportFileName", "VNA_{0,date,yyMMdd}_{0,time,HHmmss}");
      this.put("VNA.exportComment", "Date:        {0}\nMode:        {1}\nAnalyser:    {2} / {3}\nScan\n   Start:    {4} / {6}\n   Stop:     {5} / {7}\n   Samples:  {8}\n   Overscan: {9}\nCalibration\n   Samples:  {10}\n   Overscan: {11}\n   File:     {12}\nUser:        {13}\nHeadline:     {14}\nPort extension len: {15}m\nPort extension vf: {16}");
      this.put("VNA.exportTitle", "{2} - {0,date,yyMMdd}_{0,time,HHmmss}");
   }
}
