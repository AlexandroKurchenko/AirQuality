package com.okurchenko.ecocity.utils

import timber.log.Timber
import java.util.regex.Matcher
import java.util.regex.Pattern

fun convertUnicodeToString(unicodeData: String?): String? {
    try {
        Timber.e("Start processing string $unicodeData")
        if (unicodeData == null) {
            return ""
        }
        var firstPart = unicodeData.split(" ")[0]
        firstPart = firstPart.replace("\\", "")
        val arr: List<String> = firstPart.split("u")
        var text = ""
        for (i in 1 until arr.size) {
            val hexVal = arr[i].toInt(16)
            text += hexVal.toChar()
        }
        return if (unicodeData.split(" ").size > 1) {
            text + unicodeData.split(" ")[1]
        } else {
            text
        }
    } catch (ex: NumberFormatException) {
        Timber.e("Exception for string $unicodeData")
//        Timber.e(ex)
    }
    return unicodeData
}

//fun finder(unicodeData: String): StringBuffer {
//    val p = Pattern.compile("\\\\u(\\p{XDigit}{4})")
//    val m = p.matcher(unicodeData)
//    val buf = StringBuffer(unicodeData.length)
//    while (m.find()) {
//        val ch = String.valueOf((char) Integer. parseInt (m.group(1), 16));
//        m.appendReplacement(buf, Matcher.quoteReplacement(ch));
//    }
//    m.appendTail(buf)
//    return buf
//}
fun removeUTFCharacters(data: String): String {
    val p = Pattern.compile("\\\\u(\\p{XDigit}{4})")
    val m = p.matcher(data)
    val buf = StringBuffer(data.length)
    while (m.find()) {
        val ch: Char = m.group(1).toInt(16) as Char
        m.appendReplacement(buf, Matcher.quoteReplacement(ch.toString()))
    }
    m.appendTail(buf)
    return buf.toString()
}

