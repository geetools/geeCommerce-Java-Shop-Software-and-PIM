/*

Copyright (c) 2008, Jared Crapo All rights reserved. 

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met: 

- Redistributions of source code must retain the above copyright
  notice, this list of conditions and the following disclaimer. 

- Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution. 

- Neither the name of jactiveresource.org nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

 */

package org.jactiveresource;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * a port of the excellent Inflector class in ruby's ActiveSupport library
 * 
 * @version $LastChangedRevision: 5 $ <br>
 *          $LastChangedDate: 2008-05-03 13:44:24 -0600 (Sat, 03 May 2008) $
 * @author $LastChangedBy: jared $
 */
public class Inflector {

    public static String camelize(String word, boolean firstLetterInUppercase) {

        return null;
    }

    private static Pattern underscorePattern = Pattern.compile("_");

    /**
     * replace underscores with dashes in a string
     * 
     * @param word
     * @return
     */
    public static String dasherize(String word) {
        Matcher m = underscorePattern.matcher(word);
        return m.replaceAll("-");
    }

    private static Pattern dashPattern = Pattern.compile("-");

    /**
     * replace dashes with underscores in a string
     * 
     * @param word
     * @return
     */
    public static String underscorize(String word) {
        Matcher m = dashPattern.matcher(word);
        return m.replaceAll("_");
    }

    private static Pattern doubleColonPattern = Pattern.compile("::");
    private static Pattern underscore1Pattern = Pattern.compile("([A-Z]+)([A-Z][a-z])");
    private static Pattern underscore2Pattern = Pattern.compile("([a-z\\d])([A-Z])");

    /**
     * The reverse of camelize. Makes an underscored form from the expression in
     * the string.
     * 
     * Changes '::' to '/' to convert namespaces to paths.
     * 
     * @param word
     * @return
     */
    public static String underscore(String word) {

        String out;
        Matcher m;

        m = doubleColonPattern.matcher(word);
        out = m.replaceAll("/");

        m = underscore1Pattern.matcher(out);
        out = m.replaceAll("$1_$2");

        m = underscore2Pattern.matcher(out);
        out = m.replaceAll("$1_$2");

        out = underscorize(out);

        return out.toLowerCase();
    }

    /**
     * return the plural form of word
     * 
     * @param word
     * @return
     */
    public static String pluralize(String word) {
        String out = new String(word);
        if ((out.length() == 0) || (!uncountables.contains(word.toLowerCase()))) {
            for (ReplacementRule r : plurals) {
                if (r.find(word)) {
                    out = r.replace(word);
                    break;
                }
            }
        }
        return out;
    }

    /**
     * return the singular form of word
     * 
     * @param word
     * @return
     */
    public static String singularize(String word) {
        String out = new String(word);
        if ((out.length() == 0) || (!uncountables.contains(out.toLowerCase()))) {
            for (ReplacementRule r : singulars) {
                if (r.find(word)) {
                    out = r.replace(word);
                    break;
                }
            }
        }
        return out;
    }

    public static void irregular(String singular, String plural) {
        String regexp, repl;

        if (singular.substring(0, 1).toUpperCase().equals(plural.substring(0, 1).toUpperCase())) {
            // singular and plural start with the same letter
            regexp = "(?i)(" + singular.substring(0, 1) + ")" + singular.substring(1) + "$";
            repl = "$1" + plural.substring(1);
            plurals.add(0, new ReplacementRule(regexp, repl));

            regexp = "(?i)(" + plural.substring(0, 1) + ")" + plural.substring(1) + "$";
            repl = "$1" + singular.substring(1);
            singulars.add(0, new ReplacementRule(regexp, repl));
        } else {
            // singular and plural don't start with the same letter
            regexp = singular.substring(0, 1).toUpperCase() + "(?i)" + singular.substring(1) + "$";
            repl = plural.substring(0, 1).toUpperCase() + plural.substring(1);
            plurals.add(0, new ReplacementRule(regexp, repl));

            regexp = singular.substring(0, 1).toLowerCase() + "(?i)" + singular.substring(1) + "$";
            repl = plural.substring(0, 1).toLowerCase() + plural.substring(1);
            plurals.add(0, new ReplacementRule(regexp, repl));

            regexp = plural.substring(0, 1).toUpperCase() + "(?i)" + plural.substring(1) + "$";
            repl = singular.substring(0, 1).toUpperCase() + singular.substring(1);
            singulars.add(0, new ReplacementRule(regexp, repl));

            regexp = plural.substring(0, 1).toLowerCase() + "(?i)" + plural.substring(1) + "$";
            repl = singular.substring(0, 1).toLowerCase() + singular.substring(1);
            singulars.add(0, new ReplacementRule(regexp, repl));
        }
    }

    private static ArrayList<ReplacementRule> plurals;
    private static ArrayList<ReplacementRule> singulars;
    private static ArrayList<String> uncountables;

    static {
        plurals = new ArrayList<ReplacementRule>(17);
        plurals.add(0, new ReplacementRule("$", "s"));
        plurals.add(0, new ReplacementRule("(?i)s$", "s"));
        plurals.add(0, new ReplacementRule("(?i)(ax|test)is$", "$1es"));
        plurals.add(0, new ReplacementRule("(?i)(octop|vir)us$", "$1i"));
        plurals.add(0, new ReplacementRule("(?i)(alias|status)$", "$1es"));
        plurals.add(0, new ReplacementRule("(?i)(bu)s$", "$1es"));
        plurals.add(0, new ReplacementRule("(?i)(buffal|tomat)o$", "$1oes"));
        plurals.add(0, new ReplacementRule("(?i)([ti])um$", "$1a"));
        plurals.add(0, new ReplacementRule("sis$", "ses"));
        plurals.add(0, new ReplacementRule("(?i)(?:([^f])fe|([lr])f)$", "$1$2ves"));
        plurals.add(0, new ReplacementRule("(?i)(hive)$", "$1s"));
        plurals.add(0, new ReplacementRule("(?i)([^aeiouy]|qu)y$", "$1ies"));
        plurals.add(0, new ReplacementRule("(?i)(x|ch|ss|sh)$", "$1es"));
        plurals.add(0, new ReplacementRule("(?i)(matr|vert|ind)(?:ix|ex)$", "$1ices"));
        plurals.add(0, new ReplacementRule("(?i)([m|l])ouse$", "$1ice"));
        plurals.add(0, new ReplacementRule("^(?i)(ox)$", "$1en"));
        plurals.add(0, new ReplacementRule("(?i)(quiz)$", "$1zes"));

        singulars = new ArrayList<ReplacementRule>(24);
        singulars.add(0, new ReplacementRule("s$", ""));
        singulars.add(0, new ReplacementRule("(n)ews$", "$1ews"));
        singulars.add(0, new ReplacementRule("([ti])a$", "$1um"));
        singulars.add(0,
            new ReplacementRule("((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$", "$1$2sis"));
        singulars.add(0, new ReplacementRule("(^analy)ses$", "$1sis"));
        singulars.add(0, new ReplacementRule("([^f])ves$", "$1fe"));
        singulars.add(0, new ReplacementRule("(hive)s$", "$1"));
        singulars.add(0, new ReplacementRule("(tive)s$", "$1"));
        singulars.add(0, new ReplacementRule("([lr])ves$", "$1f"));
        singulars.add(0, new ReplacementRule("([^aeiouy]|qu)ies$", "$1y"));
        singulars.add(0, new ReplacementRule("(s)eries$", "$1eries"));
        singulars.add(0, new ReplacementRule("(m)ovies$", "$1ovie"));
        singulars.add(0, new ReplacementRule("(x|ch|ss|sh)es$", "$1"));
        singulars.add(0, new ReplacementRule("([m|l])ice$", "$1ouse"));
        singulars.add(0, new ReplacementRule("(bus)es$", "$1"));
        singulars.add(0, new ReplacementRule("(o)es$", "$1"));
        singulars.add(0, new ReplacementRule("(shoe)s$", "$1"));
        singulars.add(0, new ReplacementRule("(cris|ax|test)es$", "$1is"));
        singulars.add(0, new ReplacementRule("(octop|vir)i$", "$1us"));
        singulars.add(0, new ReplacementRule("(alias|status)es$", "$1"));
        singulars.add(0, new ReplacementRule("(ox)en$", "$1"));
        singulars.add(0, new ReplacementRule("(virt|ind)ices$", "$1ex"));
        singulars.add(0, new ReplacementRule("(matr)ices$", "$1ix"));
        singulars.add(0, new ReplacementRule("(quiz)zes$", "$1"));

        irregular("person", "people");
        irregular("man", "men");
        irregular("child", "children");
        irregular("sex", "sexes");
        irregular("move", "moves");
        irregular("cow", "kine");

        uncountables = new ArrayList<String>(8);
        uncountables.add("equipment");
        uncountables.add("information");
        uncountables.add("rice");
        uncountables.add("money");
        uncountables.add("species");
        uncountables.add("series");
        uncountables.add("fish");
        uncountables.add("sheep");
    }
}
