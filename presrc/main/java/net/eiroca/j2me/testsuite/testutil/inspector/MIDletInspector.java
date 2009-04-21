/**
 * This was modified no later than 2009-04-20 by Irving Bunton, Jr
 */
// Expand to define memory size define
@DMEMSIZEDEF@
//#ifdef DLARGEMEM
package net.eiroca.j2me.testsuite.testutil.inspector;

import net.eiroca.j2me.app.BaseApp;
import net.eiroca.j2me.testsuite.testutil.AbstractProcessor;

public class MIDletInspector extends AbstractProcessor {

  public static final String CATEGORY = "Properties";
  public static final String PREFIX = null;

  public MIDletInspector() {
    super(MIDletInspector.CATEGORY, MIDletInspector.PREFIX);
  }

  public void execute() {
    String ver = BaseApp.midlet.readAppProperty("MIDlet-Version", "1.0.0");
    addResult("TestSuite", ver);
  }

}
//#endif
