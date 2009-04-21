
/**
 * This was modified 2009-04-20 by Irving Bunton, Jr
 */
// Expand to define memory size define
@DMEMSIZEDEF@
//#ifdef DLARGEMEM
package net.eiroca.j2me.util;

public interface HTTPAttach {

  String getMimeType();

  byte[] getData();

}
//#endif
