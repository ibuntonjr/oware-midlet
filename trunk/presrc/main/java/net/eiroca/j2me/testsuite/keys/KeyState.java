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
@DMEMSIZEDEF@
//#ifdef DLARGEMEM
package net.eiroca.j2me.testsuite.keys;

/**
 * Holds the state of a key.
 */
public class KeyState {

  /** Constant value for the "pressed" key state. */
  public static final int PRESSED = 1;
  /** Constant value for the "released" key state. */
  public static final int RELEASED = 2;
  /** Constant value for the "repeated" or held-down key state. */
  public static final int REPEATED = 4;
  /** The key state maintained by this object. */
  int state;

  /**
   * Constructs an instance of the KeyState class.
   * @param keyState the initial key state
   */
  public KeyState(final int keyState) {
    state = keyState;
  }

  /**
   * Constructs an instance of the KeyState class.
   */
  public KeyState() {
    state = 0;
  }

  /**
   * Adds the given state to the currently recorded state.
   * @param keyState the new state
   */
  public void set(final int keyState) {
    state = state | keyState;
  }

  /**
   * Changes the recorded state to the given state.
   * @param keyState the new state
   */
  public void reset(final int keyState) {
    state = keyState;
  }

  /**
   * Changes the recorded state to the given state.
   * @param keyState the new state
   */
  public void reset(final KeyState keyState) {
    state = keyState.state;
  }

  /**
   * Returns true if the key has been in the pressed state.
   * @return true if pressed
   */
  public boolean isPressed() {
    return (state & KeyState.PRESSED) != 0;
  }

  /**
   * Returns true if the key has been in the released state.
   * @return true if released
   */
  public boolean isReleased() {
    return (state & KeyState.RELEASED) != 0;
  }

  /**
   * Returns true if the key has been in the repeated state.
   * @return true if repeated
   */
  public boolean isRepeated() {
    return (state & KeyState.REPEATED) != 0;
  }

}
//#endif
