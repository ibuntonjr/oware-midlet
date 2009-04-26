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
// Expand to define DJSR184 define
//#define DNOJSR184
// Expand to define memory size define
//#define DREGULARMEM
// Expand to define logging define
//#define DNOLOGGING
//#ifdef DLARGEMEM
//@package net.eiroca.j2me.testsuite.testutil;
//@
//@import java.util.Hashtable;
//@import java.util.Vector;
//@import javax.microedition.lcdui.Form;
//@import net.eiroca.j2me.app.BaseApp;
//@import net.eiroca.j2me.util.HTTPAttach;
//@import net.eiroca.j2me.testsuite.testutil.benchmark.MathSuite;
//@import net.eiroca.j2me.testsuite.testutil.benchmark.PrecisionSuite;
//@import net.eiroca.j2me.testsuite.testutil.benchmark.SuiteAbstract;
//@import net.eiroca.j2me.testsuite.testutil.inspector.APIsInspector;
//@import net.eiroca.j2me.testsuite.testutil.inspector.CanvasInspector;
//@import net.eiroca.j2me.testsuite.testutil.inspector.Graphic3DInspector;
//@import net.eiroca.j2me.testsuite.testutil.inspector.LocalDeviceInspector;
//@import net.eiroca.j2me.testsuite.testutil.inspector.MIDletInspector;
//@import net.eiroca.j2me.testsuite.testutil.inspector.MultimediaInspector;
//@import net.eiroca.j2me.testsuite.testutil.inspector.PrivacyPropertyInspector;
//@import net.eiroca.j2me.testsuite.testutil.inspector.PropertyInspector;
//@import net.eiroca.j2me.testsuite.testutil.inspector.SystemInspector;
//@
//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif
//@
//@public class Suite implements HTTPAttach {
//@
//@  public static final String MAPPING = "/testsuite_mapping.txt";
//@  public static final String VERSION = "1.0.0";
//@
//@  private boolean finished = false;
//@  private final Vector tests = new Vector();
//@  private Hashtable mapping = null;
//@  private final AbstractProcessor[] inspectors;
//@  private final SuiteAbstract[] benchmarks;
//@
	//#ifdef DLOGGING
//@	private Logger logger = Logger.getLogger("Suite");
//@	private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@  private boolean finerLoggable = logger.isLoggable(Level.FINER);
//@	private boolean finestLoggable = logger.isLoggable(Level.FINEST);
//@	private boolean traceLoggable = logger.isLoggable(Level.TRACE);
	//#endif
//@
//@  public Suite() {
//@    inspectors = new AbstractProcessor[9];
//@    benchmarks = new SuiteAbstract[2];
//@		try {
//@			inspectors[0] = new MIDletInspector();
//@			inspectors[1] = new CanvasInspector();
//@			inspectors[2] = new PropertyInspector();
//@			inspectors[3] = new APIsInspector();
//@			inspectors[4] = new SystemInspector();
//@			inspectors[5] = new MultimediaInspector();
//@			inspectors[6] = new PrivacyPropertyInspector();
//@			inspectors[7] = new LocalDeviceInspector();
//@			inspectors[8] = new Graphic3DInspector();
//@			benchmarks[0] = new PrecisionSuite();
//@			benchmarks[1] = new MathSuite();
//@			for (int i = 0; i < inspectors.length; i++) {
//@				if (inspectors[i] != null) {
//@					inspectors[i].setSuite(this);
//@				}
//@			}
//@			for (int i = 0; i < benchmarks.length; i++) {
//@				if (benchmarks[i] != null) {
//@					benchmarks[i].setSuite(this);
//@				}
//@			}
//@		} catch (Throwable e) {
//@			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("constructor error", e);
			//#endif
//@		}
//@  }
//@
//@  public void run() {
//@    finished = false;
//@    for (int i = 0; i < benchmarks.length; i++) {
//@      if (benchmarks[i] != null) {
//@        benchmarks[i].execute();
//@      }
//@    }
//@    for (int i = 0; i < inspectors.length; i++) {
//@      if (inspectors[i] != null) {
//@        inspectors[i].execute();
//@      }
//@    }
//@  }
//@
//@  public void addResult(final TestResult test) {
//@    if (test != null) {
//@      tests.addElement(test);
//@    }
//@  }
//@
//@  public String getDesc(final String key) {
//@    if (mapping == null) {
//@      mapping = BaseApp.readMap(Suite.MAPPING, '=');
//@      if (mapping == null) {
//@        mapping = new Hashtable();
//@      }
//@    }
//@    String res = key;
//@    if (key != null) {
//@      res = (String) mapping.get(key);
//@      if (res == null) {
//@        res = key;
//@      }
//@    }
//@    return res;
//@  }
//@
//@  public Vector getTests() {
//@    return tests;
//@  }
//@
//@  public void benchmark(final Form list, final String category) {
//@    if (!finished) {
//@      finished = true;
//@      for (int i = 0; i < benchmarks.length; i++) {
//@        if (!benchmarks[i].finished) {
//@          finished = false;
//@          break;
//@        }
//@      }
//@    }
//@    export(list, category);
//@    if (!finished) {
//@      list.append("... still working ..." + BaseApp.NL);
//@    }
//@  }
//@
//@  public void export(final Form list, final String category) {
//@    StringBuffer sb;
//@    for (int i = 0; i < tests.size(); i++) {
//@      final TestResult inf = (TestResult) tests.elementAt(i);
//@      if (inf.category.equals(category)) {
//@        if (inf.val != null) {
//@          sb = new StringBuffer(40);
//@          sb.append(getDesc(inf.key)).append('=').append(inf.val).append(BaseApp.CR);
//@          list.append(sb.toString());
//@        }
//@      }
//@    }
//@  }
//@
//@  public byte[] getData() {
//@    final StringBuffer buf = new StringBuffer(8192);
//@    for (int i = 0; i < tests.size(); i++) {
//@      final TestResult inf = (TestResult) tests.elementAt(i);
//@      final String v = (inf.val == null ? "" : inf.val.toString());
//@      buf.append(inf.key).append('=').append(v).append(BaseApp.CR);
//@    }
//@    return buf.toString().getBytes();
//@  }
//@
//@  public String getMimeType() {
//@    return "text/plain";
//@  }
//@
//@}
//#endif
