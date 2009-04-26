/** GPL >= 2.0
 *
 * Copyright (C) 2006-2008 eIrOcA (eNrIcO Croce & sImOnA Burzio)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
/**
 * This was modified no later than 2009-04-20 by Irving Bunton, Jr
 */
// Expand to define DJSR135 define
@DJSR135@
// Expand to define memory size define
@DMEMSIZEDEF@
//#ifdef DLARGEMEM
package net.eiroca.j2me.testsuite.testutil.inspector;

//#ifdef DJSR135
import javax.microedition.media.Manager;
//#endif
import net.eiroca.j2me.testsuite.testutil.AbstractProcessor;

public class MultimediaInspector extends AbstractProcessor {

  public static final String PREFIX = "M.";
  public static final String CATEGORY = "Multimedia";

  public MultimediaInspector() {
    super(MultimediaInspector.CATEGORY, MultimediaInspector.PREFIX);
  }

  public void execute() {
	//#ifdef DJSR135
    final String[] supportedProtocols = Manager.getSupportedProtocols(null);
	//#else
    final String[] supportedProtocols = new String[0];
	//#endif
    if (supportedProtocols != null) {
      for (int i = 0; i < supportedProtocols.length; i++) {
        final String protocol = supportedProtocols[i];
		//#ifdef DJSR135
        final String[] supportedContentTypes = Manager.getSupportedContentTypes(protocol);
		//#else
        final String[] supportedContentTypes = new String[0];
		//#endif
        for (int j = 0; j < supportedContentTypes.length; j++) {
          addResult(protocol + "." + j, supportedContentTypes[j]);
        }
      }
    }
  }

}
//#endif
