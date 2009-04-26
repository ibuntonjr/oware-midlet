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
//#ifdef DLARGEMEM
//@package net.eiroca.j2me.testsuite.testutil.inspector;
//@
//@import java.util.Enumeration;
//@import java.util.Hashtable;
//#ifdef DJSR184
//@import javax.microedition.m3g.Graphics3D;
//#endif
//@import net.eiroca.j2me.app.BaseApp;
//@import net.eiroca.j2me.testsuite.testutil.AbstractProcessor;
//@
//@public class Graphic3DInspector extends AbstractProcessor {
//@
//@  public static final String PREFIX = "G.";
//@  public static final String CATEGORY = "Graphic3D";
//@
//@  public Graphic3DInspector() {
//@    super(Graphic3DInspector.CATEGORY, Graphic3DInspector.PREFIX);
//@  }
//@
//@  public void execute() {
//@    if (BaseApp.isClass("javax.microedition.m3g.Graphics3D")) {
			//#ifdef DJSR184
//@      final Hashtable props = Graphics3D.getProperties();
			//#else
//@      final Hashtable props = new Hashtable();
			//#endif
//@      Object key;
//@      Object val;
//@      for (final Enumeration e = props.keys(); e.hasMoreElements();) {
//@        key = e.nextElement();
//@        val = props.get(key);
//@        addResult("3." + key, val);
//@      }
//@
//@    }
//@  }
//@
//@}
//#endif
