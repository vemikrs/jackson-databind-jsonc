/**
 * Package providing Jackson extensions to process JSONC (JSON with Comments).
 *
 * <p>Main classes:
 * <ul>
 *   <li>{@link jp.vemi.jsoncmapper.JsoncMapper} — Extends Jackson's {@code JsonMapper} and
 *       performs pre-processing for JSONC comment removal and optional JSON5-compatible
 *       features (trailing commas, single quotes, hexadecimal numbers, plus-prefixed numbers,
 *       Infinity/NaN, multiline strings, and escaping of unescaped control characters).</li>
 *   <li>{@link jp.vemi.jsoncmapper.JsoncUtils} — Utilities for comment removal and JSON5
 *       compatibility transformations. All algorithms run in linear time with protection
 *       against ReDoS attacks.</li>
 * </ul>
 *
 * <h2>Security and Input Validation</h2>
 * <ul>
 *   <li>Public APIs do not accept {@code null} inputs and throw {@link IllegalArgumentException}.</li>
 *   <li>Comment parsing avoids regular expressions and safely preserves content inside
 *       JSON string literals.</li>
 * </ul>
 *
 * <h2>Compatibility</h2>
 * <ul>
 *   <li>Built with a Java 21 toolchain while producing Java 8–compatible bytecode.</li>
 *   <li>Maintains compatibility with standard Jackson APIs by delegating/overriding commonly
 *       used methods such as {@code readValue} and {@code readTree}.</li>
 * </ul>
 *
 * @since 1.0.0
 */
package jp.vemi.jsoncmapper;
