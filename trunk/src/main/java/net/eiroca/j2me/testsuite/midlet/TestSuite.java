/** MIT LICENSE
 * Based upon Mobile Device Tools written by Andrew Scott
 *
 * Copyright (C) 2004 Andrew Scott
 * Copyright (C) 2006-2008 eIrOcA (eNrIcO Croce & sImOnA Burzio)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * This was modified no later than 2009-04-20 by Irving Bunton, Jr
 */
// Expand to define memory size define
//#define DREGULARMEM
// Expand to define MIDP define
//#define DMIDP20
// Expand to define JMUnit test define
//#define DNOJMTEST
// Expand to define logging define
//#define DNOLOGGING
//#ifdef DLARGEMEM
//@package net.eiroca.j2me.testsuite.midlet;
//@
//@import javax.microedition.lcdui.Canvas;
//@import javax.microedition.lcdui.Choice;
//@import javax.microedition.lcdui.Command;
//@import javax.microedition.lcdui.Displayable;
//@import javax.microedition.lcdui.Form;
//@import javax.microedition.lcdui.Image;
//@import javax.microedition.lcdui.List;
//@import net.eiroca.j2me.testsuite.keys.KeyStateCanvas;
//@import net.eiroca.j2me.app.Application;
//@import net.eiroca.j2me.app.BaseApp;
//@import net.eiroca.j2me.observable.Observable;
//@import net.eiroca.j2me.observable.Observer;
//@import net.eiroca.j2me.testsuite.testutil.DataSender;
//@import net.eiroca.j2me.testsuite.testutil.Suite;
//@import net.eiroca.j2me.testsuite.testutil.benchmark.MathSuite;
//@import net.eiroca.j2me.testsuite.testutil.benchmark.PrecisionSuite;
//@import net.eiroca.j2me.testsuite.testutil.inspector.APIsInspector;
//@import net.eiroca.j2me.testsuite.testutil.inspector.CanvasInspector;
//@import net.eiroca.j2me.testsuite.testutil.inspector.Graphic3DInspector;
//@import net.eiroca.j2me.testsuite.testutil.inspector.LocalDeviceInspector;
//@import net.eiroca.j2me.testsuite.testutil.inspector.MultimediaInspector;
//@import net.eiroca.j2me.testsuite.testutil.inspector.PrivacyPropertyInspector;
//@import net.eiroca.j2me.testsuite.testutil.inspector.PropertyInspector;
//@import net.eiroca.j2me.testsuite.testutil.inspector.SystemInspector;
//@import net.eiroca.j2me.testsuite.classbrowser.ClassBrowserHelper;
//@
//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//@import net.sf.jlogmicro.util.logging.FormHandler;
//@import net.sf.jlogmicro.util.logging.RecStoreHandler;
//@import net.sf.jlogmicro.util.presentation.RecStoreLoggerForm;
//#endif
//@
//@public final class TestSuite extends Application implements Observer {
//@
//@  public static final int MSG_APPLICATION = 0;
//@  public static final int MSG_OK = 1;
//@  public static final int MSG_BACK = 2;
//@  public static final int MSG_EXIT = 3;
//@  public static final int MSG_PREV = 4;
//@  public static final int MSG_ABOUT = 5;
//@
//@  private static final int AC_SHOWINSPECTOR = 1;
//@  private static final int AC_SHOWBENCHMARK = 2;
//@  private static final int AC_SHOWCLASSBROWSER = 3;
//@  private static final int AC_SHOWKEYSTATE = 4;
//@  private static final int AC_SHOWPOSTDATA = 5;
//@  private static final int AC_DOABOUT = 6;
//@  private static final int AC_OK = 7;
//@  private static final int AC_PREV = 8;
//@  private static final int AC_DOINSPECTOR = 9;
//@  private static final int AC_DOBENCHMARK = 10;
//@  private static final int AC_OPENCLASS = 11;
//@
//@  static final int COUNT = 15;
//@
//@  private final String[] INSPECTOR_CAT = new String[] {
//@      PropertyInspector.CATEGORY, APIsInspector.CATEGORY, CanvasInspector.CATEGORY, SystemInspector.CATEGORY, MultimediaInspector.CATEGORY, LocalDeviceInspector.CATEGORY, Graphic3DInspector.CATEGORY,
//@      PrivacyPropertyInspector.CATEGORY
//@  };
//@  private final String[] BENCHMARK_CAT = new String[] {
//@      PrecisionSuite.CATEGORY, MathSuite.CATEGORY
//@  };
//@
//@  private Command cPREV;
//@  private List fMenu;
//@
//@  /** The full path name to the level of class hierarchy being shown. */
//@  private String cbPackagePath;
//@  private Suite suite;
//@  private String[] classes;
//@
//@  private String url;
//@  private String[] menuDesc;
//@
	//#ifdef DLOGGING
//@  private boolean fineLoggable;
//@  private boolean finestLoggable;
//@  private boolean traceLoggable;
//@	private Logger logger;
	//#endif
//@
//@  public TestSuite() {
		//#ifdef DJMTEST
//@    super("TestSuite Test Suite");
		//#else
//@    super();
		//#endif
		//#ifdef DLOGGING
//@		logger = Logger.getLogger("TestSuite");
//@		fineLoggable = logger.isLoggable(Level.FINE);
//@		finestLoggable = logger.isLoggable(Level.FINEST);
//@		traceLoggable = logger.isLoggable(Level.TRACE);
		//#endif
//@  }
//@
//@  private Displayable fAbout;
//@  private Canvas fKeyState;
//@  private Form fPostData;
//@  private List fClassBrowser;
//@  private List fMenuInspector;
//@  private List fMenuBenchmark;
//@
//@  public boolean handleAction(final int action, final Displayable d, final Command cmd) {
//@    boolean processed = true;
//@		try {
//@			int i;
//@			Form fSpec;
//@			switch (action) {
//@				case AC_OK:
//@					processed = false;
//@					if (d == fPostData) {
//@						processed = true;
//@						final DataSender ds = new DataSender(suite);
//@						ds.addObserver(this);
//@						ds.submit(url, 400);
//@						BaseApp.back(null);
//@					}
//@					break;
//@				case AC_SHOWINSPECTOR:
//@					if (fMenuInspector == null) {
//@						fMenuInspector = new List("Inspectors", Choice.IMPLICIT, INSPECTOR_CAT, null);
//@						BaseApp.setup(fMenuInspector, BaseApp.cBACK, null);
//@						BaseApp.registerList(fMenuInspector, TestSuite.AC_DOINSPECTOR);
//@					}
//@					BaseApp.show(null, fMenuInspector, true);
//@					break;
//@				case AC_SHOWBENCHMARK:
//@					if (fMenuBenchmark == null) {
//@						fMenuBenchmark = new List("Benchmark", Choice.IMPLICIT, BENCHMARK_CAT, null);
//@						BaseApp.setup(fMenuBenchmark, BaseApp.cBACK, null);
//@						BaseApp.registerList(fMenuBenchmark, TestSuite.AC_DOBENCHMARK);
//@					}
//@					BaseApp.show(null, fMenuBenchmark, true);
//@					break;
//@				case AC_SHOWCLASSBROWSER:
//@					if (fClassBrowser == null) {
//@						ClassBrowserHelper.imPlus = BaseApp.createImage("testsuite_Plus.png");
//@						ClassBrowserHelper.imDash = BaseApp.createImage("testsuite_Dash.png");
//@						classes = BaseApp.readStrings("testsuite_classes.txt");
//@						fClassBrowser = new List("", Choice.IMPLICIT);
//@						cbPackagePath = "";
//@						loadClasses();
//@						BaseApp.setup(fClassBrowser, cPREV, BaseApp.cBACK);
//@						BaseApp.registerList(fClassBrowser, TestSuite.AC_OPENCLASS);
//@					}
//@					BaseApp.show(null, fClassBrowser, true);
//@					break;
//@				case AC_SHOWKEYSTATE:
//@					if (fKeyState == null) {
//@						fKeyState = new KeyStateCanvas();
//@						BaseApp.setup(fKeyState, BaseApp.cBACK, null);
//@					}
//@					BaseApp.show(null, fKeyState, true);
//@					break;
//@				case AC_SHOWPOSTDATA:
//@					if (fPostData == null) {
//@						fPostData = getPostDataForm();
//@					}
//@					BaseApp.show(null, fPostData, true);
//@					break;
//@				case AC_DOABOUT:
//@					if (fAbout == null) {
//@						fAbout = BaseApp.getTextForm(TestSuite.MSG_ABOUT, "testsuite_about.txt");
//@					}
//@					BaseApp.show(null, fAbout, true);
//@					break;
//@				case AC_DOINSPECTOR:
//@					i = fMenuInspector.getSelectedIndex();
//@					fSpec = new Form("Inspector: " + INSPECTOR_CAT[i]);
//@					BaseApp.setup(fSpec, BaseApp.cBACK, null);
//@					if (suite != null) {
//@						suite.export(fSpec, INSPECTOR_CAT[i]);
//@					}
//@					BaseApp.show(null, fSpec, true);
//@					break;
//@				case AC_DOBENCHMARK:
//@					i = fMenuBenchmark.getSelectedIndex();
//@					fSpec = new Form("Benchmark: " + BENCHMARK_CAT[i]);
//@					BaseApp.setup(fSpec, BaseApp.cBACK, null);
//@					if (suite != null) {
//@						suite.benchmark(fSpec, BENCHMARK_CAT[i]);
//@					}
//@					BaseApp.show(null, fSpec, true);
//@					break;
//@				case AC_OPENCLASS:
//@					final int iIdx = fClassBrowser.getSelectedIndex();
//@					final String sPackageElement = fClassBrowser.getString(iIdx);
//@					// package elements always begin with a lower-case letter..
//@					if ((sPackageElement != null) && Character.isLowerCase(sPackageElement.charAt(0))) {
//@						// set the new package path
//@						if ((cbPackagePath == null) || (cbPackagePath.length() == 0)) {
//@							cbPackagePath = sPackageElement;
//@						}
//@						else {
//@							cbPackagePath = cbPackagePath + "." + sPackageElement;
//@						}
//@						// redraw the screen object
//@						loadClasses();
//@					}
//@					break;
//@				case AC_PREV:
//@					// strip off the last bit of the package path
//@					final int iPos = cbPackagePath.lastIndexOf('.');
//@					if (iPos == -1) {
//@						cbPackagePath = ""; // back at the top level
//@					}
//@					else {
//@						cbPackagePath = cbPackagePath.substring(0, iPos);
//@					}
//@					// redraw the screen object
//@					loadClasses();
//@					break;
//@				default:
//@					processed = false;
//@					break;
//@			}
//@		} catch (Throwable e) {
//@			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("handleAction error", e);
			//#endif
//@		}
//@    return processed;
//@  }
//@
//@  /**
//@   * init
//@   */
//@  protected void init() {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("init");}
		//#endif
//@    try {
//@			super.init();
//@      url = readAppProperty("POSTURL", "http://www.eiroca.net/services/testsuite/store.php");
//@      BaseApp.messages = BaseApp.readStrings("messages.txt");
//@      final Image[] icons = new Image[6];
//@      icons[0] = BaseApp.createImage("testsuite_Specs.png");
//@      icons[1] = BaseApp.createImage("testsuite_Specs.png");
//@      icons[2] = BaseApp.createImage("testsuite_ClassBrowser.png");
//@      icons[3] = BaseApp.createImage("testsuite_Keys.png");
//@      icons[4] = BaseApp.createImage("testsuite_Dash.png");
//@      icons[5] = BaseApp.createImage("testsuite_icon.png");
//@      suite = new Suite();
//@      suite.run();
//@      menuDesc = new String[] {
//@          "Inspectors", "Benchmark", "Class browser", "Keys", "Post data", "About"
//@      };
//@      fMenu = new List("Main Menu", Choice.IMPLICIT, menuDesc, icons);
//@      BaseApp.cOK = BaseApp.newCommand(TestSuite.MSG_OK, Command.OK, 30, TestSuite.AC_OK);
//@      BaseApp.cBACK = BaseApp.newCommand(TestSuite.MSG_BACK, Command.BACK, 20, BaseApp.AC_BACK);
//@      BaseApp.cEXIT = BaseApp.newCommand(TestSuite.MSG_EXIT, Command.EXIT, 10, BaseApp.AC_EXIT);
//@      cPREV = BaseApp.newCommand(TestSuite.MSG_PREV, Command.SCREEN, 1, TestSuite.AC_PREV);
//@      BaseApp.registerListItem(fMenu, 0, TestSuite.AC_SHOWINSPECTOR);
//@      BaseApp.registerListItem(fMenu, 1, TestSuite.AC_SHOWBENCHMARK);
//@      BaseApp.registerListItem(fMenu, 2, TestSuite.AC_SHOWCLASSBROWSER);
//@      BaseApp.registerListItem(fMenu, 3, TestSuite.AC_SHOWKEYSTATE);
//@      BaseApp.registerListItem(fMenu, 4, TestSuite.AC_SHOWPOSTDATA);
//@      BaseApp.registerListItem(fMenu, 5, TestSuite.AC_DOABOUT);
//@      BaseApp.setup(fMenu, BaseApp.cEXIT, null);
//@    }
//@    catch (final Exception e) {
//@      fMenu.setTitle(e.getMessage());
//@    }
//@    BaseApp.show(null, fMenu, true);
//@  }
//@
//@  private Form getPostDataForm() {
//@		try {
//@			final Form f = new Form("Post Data");
//@			f.append("Post data to server " + url);
//@			BaseApp.setup(f, BaseApp.cBACK, BaseApp.cOK);
//@			return f;
//@		} catch (Throwable e) {
//@			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("getPostDataForm error", e);
			//#endif
//@			return null;
//@		}
//@  }
//@
//@  public void loadClasses() {
//@		try {
//@			ClassBrowserHelper.generateList(fClassBrowser, cbPackagePath, classes);
//@			fClassBrowser.setCommandListener(this);
//@			if ((cbPackagePath == null) || (cbPackagePath.length() == 0)) {
//@				fClassBrowser.setTitle("Top Level");
//@				fClassBrowser.removeCommand(cPREV);
//@			}
//@			else {
//@				fClassBrowser.setTitle(cbPackagePath);
//@				fClassBrowser.addCommand(cPREV);
//@			}
//@		} catch (Throwable e) {
//@			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("loadClasses error", e);
			//#endif
//@		}
//@  }
//@
//@  public void changed(final Observable observable) {
//@		try {
//@			final DataSender ds = (DataSender) observable;
//@			System.out.println(" " + ds.getStatus());
//@			fMenu.set(4, menuDesc[4] + " (" + ds.getStatus() + ")", ClassBrowserHelper.imPlus);
//@		} catch (Throwable e) {
//@			e.printStackTrace();
			//#ifdef DLOGGING
//@			logger.severe("changed error", e);
			//#endif
//@		}
//@  }
//@
//@}
//#endif
