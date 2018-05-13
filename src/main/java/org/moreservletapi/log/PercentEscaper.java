package org.moreservletapi.log;

/**
 * based on guava com.google.common.net.PercentEscaper (can be replaced with it)
 */
final class PercentEscaper extends UnicodeEscaper {

    private static final char[] PLUS_SIGN = { '+' };

    private static final char[] UPPER_HEX_DIGITS =
            "0123456789ABCDEF".toCharArray();

    private final boolean plusForSpace;

    private final boolean[] safeOctets;

    PercentEscaper(String safeChars, boolean plusForSpace) {
        // TODO(user): Switch to static factory methods for creation now that class is final.
        // TODO(user): Support escapers where alphanumeric chars are not safe.
        checkNotNull(safeChars);  // eager for GWT.
        // Avoid any misunderstandings about the behavior of this escaper
        if (safeChars.matches(".*[0-9A-Za-z].*")) {
            throw new IllegalArgumentException(
                    "Alphanumeric characters are always 'safe' and should not be " +
                            "explicitly specified");
        }
        safeChars += "abcdefghijklmnopqrstuvwxyz" +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "0123456789";
        // Avoid ambiguous parameters. Safe characters are never modified so if
        // space is a safe character then setting plusForSpace is meaningless.
        if (plusForSpace && safeChars.contains(" ")) {
            throw new IllegalArgumentException(
                    "plusForSpace cannot be specified when space is a 'safe' character");
        }
        this.plusForSpace = plusForSpace;
        this.safeOctets = createSafeOctets(safeChars);
    }

    private static boolean[] createSafeOctets(String safeChars) {
        int maxChar = -1;
        char[] safeCharArray = safeChars.toCharArray();
        for (char c : safeCharArray) {
            maxChar = Math.max(c, maxChar);
        }
        boolean[] octets = new boolean[maxChar + 1];
        for (char c : safeCharArray) {
            octets[c] = true;
        }
        return octets;
    }

    @Override
    int nextEscapeIndex(CharSequence csq, int index, int end) {
        checkNotNull(csq);
        for (; index < end; index++) {
            char c = csq.charAt(index);
            if (c >= safeOctets.length || !safeOctets[c]) {
                break;
            }
        }
        return index;
    }

    @Override
    String escape(String s) {
        checkNotNull(s);
        int slen = s.length();
        for (int index = 0; index < slen; index++) {
            char c = s.charAt(index);
            if (c >= safeOctets.length || !safeOctets[c]) {
                return escapeSlow(s, index);
            }
        }
        return s;
    }

    @Override
    char[] escape(int cp) {
        if (cp < safeOctets.length && safeOctets[cp]) {
            return null;
        } else if (cp == ' ' && plusForSpace) {
            return PLUS_SIGN;
        } else if (cp <= 0x7F) {
            char[] dest = new char[3];
            dest[0] = '%';
            dest[2] = UPPER_HEX_DIGITS[cp & 0xF];
            dest[1] = UPPER_HEX_DIGITS[cp >>> 4];
            return dest;
        } else if (cp <= 0x7ff) {
            char[] dest = new char[6];
            dest[0] = '%';
            dest[3] = '%';
            dest[5] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[4] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
            cp >>>= 2;
            dest[2] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[1] = UPPER_HEX_DIGITS[0xC | cp];
            return dest;
        } else if (cp <= 0xffff) {
            char[] dest = new char[9];
            dest[0] = '%';
            dest[1] = 'E';
            dest[3] = '%';
            dest[6] = '%';
            dest[8] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[7] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
            cp >>>= 2;
            dest[5] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[4] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
            cp >>>= 2;
            dest[2] = UPPER_HEX_DIGITS[cp];
            return dest;
        } else if (cp <= 0x10ffff) {
            char[] dest = new char[12];
            dest[0] = '%';
            dest[1] = 'F';
            dest[3] = '%';
            dest[6] = '%';
            dest[9] = '%';
            dest[11] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[10] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
            cp >>>= 2;
            dest[8] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[7] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
            cp >>>= 2;
            dest[5] = UPPER_HEX_DIGITS[cp & 0xF];
            cp >>>= 4;
            dest[4] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
            cp >>>= 2;
            dest[2] = UPPER_HEX_DIGITS[cp & 0x7];
            return dest;
        } else {
            throw new IllegalArgumentException(
                    "Invalid unicode character value " + cp);
        }
    }
}

/**
 * based on guava com.google.common.escape.UnicodeEscaper
 */
abstract class UnicodeEscaper {

    private static final int DEST_PAD = 32;

    abstract char[] escape(int cp);

    int nextEscapeIndex(CharSequence csq, int start, int end) {
        int index = start;
        while (index < end) {
            int cp = codePointAt(csq, index, end);
            if (cp < 0 || escape(cp) != null) {
                break;
            }
            index += Character.isSupplementaryCodePoint(cp) ? 2 : 1;
        }
        return index;
    }

    String escape(String string) {
        checkNotNull(string);
        int end = string.length();
        int index = nextEscapeIndex(string, 0, end);
        return index == end ? string : escapeSlow(string, index);
    }

    final String escapeSlow(String s, int index) {
        int end = s.length();

        char[] dest = charBufferFromThreadLocal();
        int destIndex = 0;
        int unescapedChunkStart = 0;

        while (index < end) {
            int cp = codePointAt(s, index, end);
            if (cp < 0) {
                throw new IllegalArgumentException(
                        "Trailing high surrogate at end of input");
            }
            char[] escaped = escape(cp);
            int nextIndex = index + (Character.isSupplementaryCodePoint(cp) ? 2 : 1);
            if (escaped != null) {
                int charsSkipped = index - unescapedChunkStart;

                // This is the size needed to add the replacement, not the full
                // size needed by the string.  We only regrow when we absolutely must.
                int sizeNeeded = destIndex + charsSkipped + escaped.length;
                if (dest.length < sizeNeeded) {
                    int destLength = sizeNeeded + (end - index) + DEST_PAD;
                    dest = growBuffer(dest, destIndex, destLength);
                }
                // If we have skipped any characters, we need to copy them now.
                if (charsSkipped > 0) {
                    s.getChars(unescapedChunkStart, index, dest, destIndex);
                    destIndex += charsSkipped;
                }
                if (escaped.length > 0) {
                    System.arraycopy(escaped, 0, dest, destIndex, escaped.length);
                    destIndex += escaped.length;
                }
                // If we dealt with an escaped character, reset the unescaped range.
                unescapedChunkStart = nextIndex;
            }
            index = nextEscapeIndex(s, nextIndex, end);
        }

        // Process trailing unescaped characters - no need to account for escaped
        // length or padding the allocation.
        int charsSkipped = end - unescapedChunkStart;
        if (charsSkipped > 0) {
            int endIndex = destIndex + charsSkipped;
            if (dest.length < endIndex) {
                dest = growBuffer(dest, destIndex, endIndex);
            }
            s.getChars(unescapedChunkStart, end, dest, destIndex);
            destIndex = endIndex;
        }
        return new String(dest, 0, destIndex);
    }

    private static int codePointAt(CharSequence seq, int index, int end) {
        checkNotNull(seq);
        if (index < end) {
            char c1 = seq.charAt(index++);
            if (c1 < Character.MIN_HIGH_SURROGATE ||
                    c1 > Character.MAX_LOW_SURROGATE) {
                // Fast path (first test is probably all we need to do)
                return c1;
            } else if (c1 <= Character.MAX_HIGH_SURROGATE) {
                // If the high surrogate was the last character, return its inverse
                if (index == end) {
                    return -c1;
                }
                // Otherwise look for the low surrogate following it
                char c2 = seq.charAt(index);
                if (Character.isLowSurrogate(c2)) {
                    return Character.toCodePoint(c1, c2);
                }
                throw new IllegalArgumentException(
                        "Expected low surrogate but got char '" + c2 +
                                "' with value " + (int) c2 + " at index " + index +
                                " in '" + seq + "'");
            } else {
                throw new IllegalArgumentException(
                        "Unexpected low surrogate character '" + c1 +
                                "' with value " + (int) c1 + " at index " + (index - 1) +
                                " in '" + seq + "'");
            }
        }
        throw new IndexOutOfBoundsException("Index exceeds specified range");
    }

    private static char[] growBuffer(char[] dest, int index, int size) {
        char[] copy = new char[size];
        if (index > 0) {
            System.arraycopy(dest, 0, copy, 0, index);
        }
        return copy;
    }

    static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    /** Returns a thread-local 1024-char array. */
    static char[] charBufferFromThreadLocal() {
        return DEST_TL.get();
    }

    /**
     * A thread-local destination buffer to keep us from creating new buffers.
     * The starting size is 1024 characters.  If we grow past this we don't
     * put it back in the threadlocal, we just keep going and grow as needed.
     */
    private static final ThreadLocal<char[]> DEST_TL = ThreadLocal.withInitial(() -> new char[1024]);
}
