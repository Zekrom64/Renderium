package com.zekrom_64.renderium.util;

import org.lwjgl.glfw.GLFW;

public enum Key {
	A(GLFW.GLFW_KEY_A, 'A', 'a'),
	B(GLFW.GLFW_KEY_B, 'B', 'b'),
	C(GLFW.GLFW_KEY_C, 'C', 'c'),
	D(GLFW.GLFW_KEY_D, 'D', 'd'),
	E(GLFW.GLFW_KEY_E, 'E', 'e'),
	F(GLFW.GLFW_KEY_F, 'F', 'f'),
	G(GLFW.GLFW_KEY_G, 'G', 'g'),
	H(GLFW.GLFW_KEY_H, 'H', 'h'),
	I(GLFW.GLFW_KEY_I, 'I', 'i'),
	J(GLFW.GLFW_KEY_J, 'J', 'j'),
	K(GLFW.GLFW_KEY_K, 'K', 'k'),
	L(GLFW.GLFW_KEY_L, 'L', 'l'),
	M(GLFW.GLFW_KEY_M, 'M', 'm'),
	N(GLFW.GLFW_KEY_N, 'N', 'n'),
	O(GLFW.GLFW_KEY_O, 'O', 'o'),
	P(GLFW.GLFW_KEY_P, 'P', 'p'),
	Q(GLFW.GLFW_KEY_Q, 'Q', 'q'),
	R(GLFW.GLFW_KEY_R, 'R', 'r'),
	S(GLFW.GLFW_KEY_S, 'S', 's'),
	T(GLFW.GLFW_KEY_T, 'T', 't'),
	U(GLFW.GLFW_KEY_U, 'U', 'u'),
	V(GLFW.GLFW_KEY_V, 'V', 'v'),
	W(GLFW.GLFW_KEY_W, 'W', 'w'),
	X(GLFW.GLFW_KEY_X, 'X', 'x'),
	Y(GLFW.GLFW_KEY_Y, 'Y', 'y'),
	Z(GLFW.GLFW_KEY_Z, 'Z', 'z'),
	
	_1(GLFW.GLFW_KEY_1, '!', '1'),
	_2(GLFW.GLFW_KEY_1, '@', '2'),
	_3(GLFW.GLFW_KEY_1, '#', '3'),
	_4(GLFW.GLFW_KEY_1, '$', '4'),
	_5(GLFW.GLFW_KEY_1, '%', '5'),
	_6(GLFW.GLFW_KEY_1, '^', '6'),
	_7(GLFW.GLFW_KEY_1, '&', '7'),
	_8(GLFW.GLFW_KEY_1, '*', '8'),
	_9(GLFW.GLFW_KEY_1, '(', '9'),
	_0(GLFW.GLFW_KEY_1, ')', '0'),
	
	GRAVE(GLFW.GLFW_KEY_GRAVE_ACCENT, '~', '`'),
	MINUS(GLFW.GLFW_KEY_MINUS, '_', '-'),
	EQUAL(GLFW.GLFW_KEY_EQUAL, '+', '='),
	LBRACKET(GLFW.GLFW_KEY_LEFT_BRACKET, '{', '['),
	RBRACKET(GLFW.GLFW_KEY_RIGHT_BRACKET, '}', ']'),
	BACKSLASH(GLFW.GLFW_KEY_BACKSLASH, '|', '\\'),
	SEMICOLON(GLFW.GLFW_KEY_SEMICOLON, ':', ';'),
	QUOTE(GLFW.GLFW_KEY_APOSTROPHE, '\"', '\''),
	COMMA(GLFW.GLFW_KEY_COMMA, '<', ','),
	PERIOD(GLFW.GLFW_KEY_PERIOD, '>', '.'),
	SLASH(GLFW.GLFW_KEY_SLASH, '?', '/'),
	
	SPACE(GLFW.GLFW_KEY_SPACE, ' '),
	TAB(GLFW.GLFW_KEY_TAB, '\t'),
	BACKSPACE(GLFW.GLFW_KEY_BACKSPACE, '\b'),
	ESCAPE(GLFW.GLFW_KEY_ESCAPE, (char)0x1B),
	ENTER(GLFW.GLFW_KEY_ENTER, '\n'),
	
	F1(GLFW.GLFW_KEY_F1),
	F2(GLFW.GLFW_KEY_F2),
	F3(GLFW.GLFW_KEY_F3),
	F4(GLFW.GLFW_KEY_F4),
	F5(GLFW.GLFW_KEY_F5),
	F6(GLFW.GLFW_KEY_F6),
	F7(GLFW.GLFW_KEY_F7),
	F8(GLFW.GLFW_KEY_F8),
	F9(GLFW.GLFW_KEY_F9),
	F10(GLFW.GLFW_KEY_F10),
	F11(GLFW.GLFW_KEY_F11),
	F12(GLFW.GLFW_KEY_F12),
	
	INSERT(GLFW.GLFW_KEY_INSERT),
	DELETE(GLFW.GLFW_KEY_DELETE),
	HOME(GLFW.GLFW_KEY_HOME),
	END(GLFW.GLFW_KEY_END),
	PAGE_UP(GLFW.GLFW_KEY_PAGE_UP),
	PAGE_DOWN(GLFW.GLFW_KEY_PAGE_DOWN),
	
	LEFT(GLFW.GLFW_KEY_LEFT),
	UP(GLFW.GLFW_KEY_UP),
	RIGHT(GLFW.GLFW_KEY_RIGHT),
	DOWN(GLFW.GLFW_KEY_DOWN),
	
	NUMPAD_0(GLFW.GLFW_KEY_KP_0, '0'),
	NUMPAD_1(GLFW.GLFW_KEY_KP_1, '1'),
	NUMPAD_2(GLFW.GLFW_KEY_KP_2, '2'),
	NUMPAD_3(GLFW.GLFW_KEY_KP_3, '3'),
	NUMPAD_4(GLFW.GLFW_KEY_KP_4, '4'),
	NUMPAD_5(GLFW.GLFW_KEY_KP_5, '5'),
	NUMPAD_6(GLFW.GLFW_KEY_KP_6, '6'),
	NUMPAD_7(GLFW.GLFW_KEY_KP_7, '7'),
	NUMPAD_8(GLFW.GLFW_KEY_KP_8, '8'),
	NUMPAD_9(GLFW.GLFW_KEY_KP_9, '9'),
	NUMPAD_DIVIDE(GLFW.GLFW_KEY_KP_DIVIDE, '/'),
	NUMPAD_MULTIPLY(GLFW.GLFW_KEY_KP_MULTIPLY, '*'),
	NUMPAD_SUBTRACT(GLFW.GLFW_KEY_KP_SUBTRACT, '-'),
	NUMPAD_ADD(GLFW.GLFW_KEY_KP_ADD, '+'),
	NUMPAD_ENTER(GLFW.GLFW_KEY_KP_ENTER, '\n'),
	NUMPAD_DECIMAL(GLFW.GLFW_KEY_KP_DECIMAL, '.'),
	
	LSHIFT(GLFW.GLFW_KEY_LEFT_SHIFT),
	LCTRL(GLFW.GLFW_KEY_LEFT_CONTROL),
	LALT(GLFW.GLFW_KEY_LEFT_ALT),
	RSHIFT(GLFW.GLFW_KEY_RIGHT_SHIFT),
	RCTRL(GLFW.GLFW_KEY_RIGHT_CONTROL),
	RALT(GLFW.GLFW_KEY_RIGHT_ALT);
	
	private static Key[] glfwkeys = new Key[GLFW.GLFW_KEY_LAST + 1];
	
	static {
		for(Key key : values()) glfwkeys[key.glfwKey] = key;
	}
	
	public static Key fromGLFW(int glfwkey) {
		if (glfwkey >= glfwkeys.length) return null;
		return glfwkeys[glfwkey];
	}
	
	public final int glfwKey;
	public final char upperChar;
	public final char lowerChar;
	
	private Key(int key) {
		this.glfwKey = key;
		this.upperChar = '\0';
		this.lowerChar = '\0';
	}
	
	private Key(int key, char c) {
		this.glfwKey = key;
		this.upperChar = c;
		this.lowerChar = c;
	}
	
	private Key(int key, char upper, char lower) {
		this.glfwKey = key;
		this.upperChar = upper;
		this.lowerChar = lower;
	}
	
	public char toChar(KeyMod mods) {
		return mods.shift() ? upperChar : lowerChar;
	}
}
