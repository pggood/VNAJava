package krause.util;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Properties;

public class TokenReplacer extends HashMap<String, String> implements GlobalSymbols {
   private String delimiter = null;

   public TokenReplacer() {
   }

   public TokenReplacer(String newDelimiter, Properties props, String tokenPostfix, String valuePostfix) {
      this.setDelimiter(newDelimiter);
      int i = 1;

      while(i > 0) {
         String tokKey = String.valueOf(i) + tokenPostfix;
         String valKey = String.valueOf(i) + valuePostfix;
         String tok = props.getProperty(tokKey);
         String val = props.getProperty(valKey);
         if (tok != null) {
            this.put(tok, val);
            ++i;
         } else {
            i = -1;
         }
      }

   }

   public TokenReplacer(String newDelimiter, HashMap<String, String> newTokenList) {
      this.setDelimiter(newDelimiter != null ? newDelimiter : "");
      this.putAll(newTokenList);
   }

   public void addToken(String token, String value) {
      this.put(token, value);
   }

   public String getDelimiter() {
      return this.delimiter;
   }

   public String replace(String sOld) {
      boolean inVar = false;
      StringBuilder varName = new StringBuilder(100);
      StringBuilder result = new StringBuilder(2 * sOld.length());

      try {
         StringReader theReader = new StringReader(sOld);
         int ci = 0;
         char delimiterChar = '$';

         //int ci;
         while((ci = theReader.read()) != -1) {
            char cc = (char)ci;
            if (inVar) {
               if (delimiterChar == cc) {
                  String replacement = (String)this.get(varName.toString());
                  if (replacement == null) {
                     result.append(delimiterChar).append(varName.toString()).append(delimiterChar);
                  } else {
                     result.append(replacement);
                  }

                  inVar = false;
               } else {
                  varName.append(cc);
               }
            } else {
               int delimiterIndex = this.getDelimiter().indexOf(cc);
               if (delimiterIndex != -1) {
                  delimiterChar = this.getDelimiter().charAt(delimiterIndex);
                  inVar = true;
               } else {
                  result.append(cc);
               }
            }
         }
      } catch (Exception var10) {
         result = new StringBuilder(sOld);
      }

      return result.toString();
   }

   public void setDelimiter(String delimiter) {
      this.delimiter = delimiter;
   }

   public String getProperty(Object key) {
      return (String)this.get(key);
   }
}
