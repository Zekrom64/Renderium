package com.zekrom_64.renderium.input;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.lwjgl.glfw.GLFW;

import com.zekrom_64.renderium.localization.LocalizedString;

/** Enumeration of keyboard keys.
 * 
 * @author Zekrom_64
 *
 */
public enum Key {
	A(GLFW.GLFW_KEY_A, 'A', 'a', "key.a"),
	B(GLFW.GLFW_KEY_B, 'B', 'b', "key.b"),
	C(GLFW.GLFW_KEY_C, 'C', 'c', "key.c"),
	D(GLFW.GLFW_KEY_D, 'D', 'd', "key.d"),
	E(GLFW.GLFW_KEY_E, 'E', 'e', "key.e"),
	F(GLFW.GLFW_KEY_F, 'F', 'f', "key.f"),
	G(GLFW.GLFW_KEY_G, 'G', 'g', "key.g"),
	H(GLFW.GLFW_KEY_H, 'H', 'h', "key.h"),
	I(GLFW.GLFW_KEY_I, 'I', 'i', "key.i"),
	J(GLFW.GLFW_KEY_J, 'J', 'j', "key.j"),
	K(GLFW.GLFW_KEY_K, 'K', 'k', "key.k"),
	L(GLFW.GLFW_KEY_L, 'L', 'l', "key.l"),
	M(GLFW.GLFW_KEY_M, 'M', 'm', "key.m"),
	N(GLFW.GLFW_KEY_N, 'N', 'n', "key.n"),
	O(GLFW.GLFW_KEY_O, 'O', 'o', "key.o"),
	P(GLFW.GLFW_KEY_P, 'P', 'p', "key.p"),
	Q(GLFW.GLFW_KEY_Q, 'Q', 'q', "key.q"),
	R(GLFW.GLFW_KEY_R, 'R', 'r', "key.r"),
	S(GLFW.GLFW_KEY_S, 'S', 's', "key.s"),
	T(GLFW.GLFW_KEY_T, 'T', 't', "key.t"),
	U(GLFW.GLFW_KEY_U, 'U', 'u', "key.u"),
	V(GLFW.GLFW_KEY_V, 'V', 'v', "key.v"),
	W(GLFW.GLFW_KEY_W, 'W', 'w', "key.w"),
	X(GLFW.GLFW_KEY_X, 'X', 'x', "key.x"),
	Y(GLFW.GLFW_KEY_Y, 'Y', 'y', "key.y"),
	Z(GLFW.GLFW_KEY_Z, 'Z', 'z', "key.z"),
	
	_1(GLFW.GLFW_KEY_1, '!', '1', "key.1"),
	_2(GLFW.GLFW_KEY_2, '@', '2', "key.2"),
	_3(GLFW.GLFW_KEY_3, '#', '3', "key.3"),
	_4(GLFW.GLFW_KEY_4, '$', '4', "key.4"),
	_5(GLFW.GLFW_KEY_5, '%', '5', "key.5"),
	_6(GLFW.GLFW_KEY_6, '^', '6', "key.6"),
	_7(GLFW.GLFW_KEY_7, '&', '7', "key.7"),
	_8(GLFW.GLFW_KEY_8, '*', '8', "key.8"),
	_9(GLFW.GLFW_KEY_9, '(', '9', "key.9"),
	_0(GLFW.GLFW_KEY_0, ')', '0', "key.0"),
	
	GRAVE(GLFW.GLFW_KEY_GRAVE_ACCENT, '~', '`', "key.grave"),
	MINUS(GLFW.GLFW_KEY_MINUS, '_', '-', "key.minus"),
	EQUAL(GLFW.GLFW_KEY_EQUAL, '+', '=', "key.equals"),
	LBRACKET(GLFW.GLFW_KEY_LEFT_BRACKET, '{', '[', "key.lbracket"),
	RBRACKET(GLFW.GLFW_KEY_RIGHT_BRACKET, '}', ']', "key.rbracket"),
	BACKSLASH(GLFW.GLFW_KEY_BACKSLASH, '|', '\\', "key.backslash"),
	SEMICOLON(GLFW.GLFW_KEY_SEMICOLON, ':', ';', "key.semicolon"),
	QUOTE(GLFW.GLFW_KEY_APOSTROPHE, '\"', '\'', "key.apostrophe"),
	COMMA(GLFW.GLFW_KEY_COMMA, '<', ',', "key.comma"),
	PERIOD(GLFW.GLFW_KEY_PERIOD, '>', '.', "key.period"),
	SLASH(GLFW.GLFW_KEY_SLASH, '?', '/', "key.slash"),
	
	SPACE(GLFW.GLFW_KEY_SPACE, ' ', "key.space"),
	TAB(GLFW.GLFW_KEY_TAB, '\t', "key.tab"),
	BACKSPACE(GLFW.GLFW_KEY_BACKSPACE, '\b', "key.backspace"),
	ESCAPE(GLFW.GLFW_KEY_ESCAPE, (char)0x1B, "key.escape"),
	ENTER(GLFW.GLFW_KEY_ENTER, '\n', "key.enter"),
	
	F1(GLFW.GLFW_KEY_F1, "key.f1"),
	F2(GLFW.GLFW_KEY_F2, "key.f2"),
	F3(GLFW.GLFW_KEY_F3, "key.f3"),
	F4(GLFW.GLFW_KEY_F4, "key.f4"),
	F5(GLFW.GLFW_KEY_F5, "key.f5"),
	F6(GLFW.GLFW_KEY_F6, "key.f6"),
	F7(GLFW.GLFW_KEY_F7, "key.f7"),
	F8(GLFW.GLFW_KEY_F8, "key.f8"),
	F9(GLFW.GLFW_KEY_F9, "key.f9"),
	F10(GLFW.GLFW_KEY_F10, "key.f10"),
	F11(GLFW.GLFW_KEY_F11, "key.f11"),
	F12(GLFW.GLFW_KEY_F12, "key.f12"),
	
	INSERT(GLFW.GLFW_KEY_INSERT, "key.insert"),
	DELETE(GLFW.GLFW_KEY_DELETE, "key.delete"),
	HOME(GLFW.GLFW_KEY_HOME, "key.home"),
	END(GLFW.GLFW_KEY_END, "key.end"),
	PAGE_UP(GLFW.GLFW_KEY_PAGE_UP, "key.pageup"),
	PAGE_DOWN(GLFW.GLFW_KEY_PAGE_DOWN, "key.pagedown"),
	
	LEFT(GLFW.GLFW_KEY_LEFT, "key.left"),
	UP(GLFW.GLFW_KEY_UP, "key.up"),
	RIGHT(GLFW.GLFW_KEY_RIGHT, "key.right"),
	DOWN(GLFW.GLFW_KEY_DOWN, "key.down"),
	
	NUMPAD_0(GLFW.GLFW_KEY_KP_0, '0', "key.numpad0"),
	NUMPAD_1(GLFW.GLFW_KEY_KP_1, '1', "key.numpad1"),
	NUMPAD_2(GLFW.GLFW_KEY_KP_2, '2', "key.numpad2"),
	NUMPAD_3(GLFW.GLFW_KEY_KP_3, '3', "key.numpad3"),
	NUMPAD_4(GLFW.GLFW_KEY_KP_4, '4', "key.numpad4"),
	NUMPAD_5(GLFW.GLFW_KEY_KP_5, '5', "key.numpad5"),
	NUMPAD_6(GLFW.GLFW_KEY_KP_6, '6', "key.numpad6"),
	NUMPAD_7(GLFW.GLFW_KEY_KP_7, '7', "key.numpad7"),
	NUMPAD_8(GLFW.GLFW_KEY_KP_8, '8', "key.numpad8"),
	NUMPAD_9(GLFW.GLFW_KEY_KP_9, '9', "key.numpad9"),
	NUMPAD_DIVIDE(GLFW.GLFW_KEY_KP_DIVIDE, '/', "key.divide"),
	NUMPAD_MULTIPLY(GLFW.GLFW_KEY_KP_MULTIPLY, '*', "key.multiply"),
	NUMPAD_SUBTRACT(GLFW.GLFW_KEY_KP_SUBTRACT, '-', "key.subtract"),
	NUMPAD_ADD(GLFW.GLFW_KEY_KP_ADD, '+', "key.add"),
	NUMPAD_ENTER(GLFW.GLFW_KEY_KP_ENTER, '\n', "key.numpadenter"),
	NUMPAD_DECIMAL(GLFW.GLFW_KEY_KP_DECIMAL, '.', "key.decimal"),
	
	LSHIFT(GLFW.GLFW_KEY_LEFT_SHIFT, "key.lshift"),
	LCTRL(GLFW.GLFW_KEY_LEFT_CONTROL, "key.lctrl"),
	LALT(GLFW.GLFW_KEY_LEFT_ALT, "key.lalt"),
	RSHIFT(GLFW.GLFW_KEY_RIGHT_SHIFT, "key.rshift"),
	RCTRL(GLFW.GLFW_KEY_RIGHT_CONTROL, "key.rctrl"),
	RALT(GLFW.GLFW_KEY_RIGHT_ALT, "key.ralt");
	
	private static Key[] glfwkeys = new Key[GLFW.GLFW_KEY_LAST + 1];
	private static Map<String,Key> idkeys = new HashMap<>();
	
	static {
		for(Key key : values()) {
			glfwkeys[key.glfwKey] = key;
			idkeys.put(key.getID(), key);
		}
	}
	
	/** Gets a key from a GLFW key enumeration value.
	 * 
	 * @param glfwkey GLFW key value
	 * @return Key value
	 */
	public static @Nullable Key fromGLFW(int glfwkey) {
		if (glfwkey >= glfwkeys.length) return null;
		return glfwkeys[glfwkey];
	}
	
	/** Gets a key by its ID.
	 * 
	 * @param id Key ID
	 * @return Key value
	 */
	public static @Nullable Key getByID(@NonNull String id) {
		return idkeys.get(id);
	}
	
	/** The corresponding GLFW key enumeration value. */
	public final int glfwKey;
	/** The 'uppercase' (shifted) character equivalent for this key. */
	public final char upperChar;
	/** The 'lowercase' (unshifted) character equivalent for this key. */
	public final char lowerChar;
	/** The localized description of this key. */
	public final LocalizedString description;
	
	private Key(int key, @NonNull String description) {
		this.glfwKey = key;
		this.upperChar = '\0';
		this.lowerChar = '\0';
		this.description = new LocalizedString(description);
	}
	
	private Key(int key, char c, @NonNull String description) {
		this.glfwKey = key;
		this.upperChar = c;
		this.lowerChar = c;
		this.description = new LocalizedString(description);
	}
	
	private Key(int key, char upper, char lower, @NonNull String description) {
		this.glfwKey = key;
		this.upperChar = upper;
		this.lowerChar = lower;
		this.description = new LocalizedString(description);
	}
	
	/** Naively converts this key to a character based on the given modifiers.
	 * 
	 * @param mods Key modifiers
	 * @return Equivalent character
	 */
	public char toChar(@NonNull KeyMod mods) {
		return mods.shift() ? upperChar : lowerChar;
	}
	
	@Override
	public String toString() {
		return description.toString();
	}
	
	/** Gets a language-independent ID for this key.
	 * 
	 * @return Key ID
	 */
	public @NonNull String getID() {
		return description.key;
	}

}
