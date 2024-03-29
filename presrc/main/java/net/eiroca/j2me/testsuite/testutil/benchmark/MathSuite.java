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
// Expand to define memory size define
@DMEMSIZEDEF@
//#ifdef DLARGEMEM
package net.eiroca.j2me.testsuite.testutil.benchmark;

public class MathSuite extends SuiteAbstract {

  public static final String PREFIX = "B.M.";

  public static final String CATEGORY = "Math";

  public MathSuite() {
    super(MathSuite.CATEGORY, MathSuite.PREFIX);
    benchmark = new BenchmarkAbstract[3];
    benchmark[0] = new BenchmarkADD(this);
    benchmark[1] = new BenchmarkMUL(this);
    benchmark[2] = new BenchmarkDIV(this);
  }

}
//#endif
