package github.keshaparrot.fitnesshelper.utils;

public class CleanMarkdown {

    public static String clean(String raw) {
        if (raw == null) {
            return "[]";
        }

        String s = raw.trim();

        if (s.startsWith("```")) {
            int idx = s.indexOf('\n');
            s = idx >= 0 ? s.substring(idx + 1) : s.substring(3);
        }
        if (s.endsWith("```")) {
            s = s.substring(0, s.lastIndexOf("```"));
        }

        String lower = s.toLowerCase();
        if (lower.startsWith("json")) {
            int idx = s.indexOf('\n');
            s = idx >= 0 ? s.substring(idx + 1) : s.substring(4);
        }

        s = s.replaceAll("(?m)^\\s*…+\\s*,?\\s*$", "")
                .replace("...", "")
                .trim();

        if (!s.startsWith("[")) s = "[" + s;
        if (!s.endsWith("]"))   s = s + "]";

        return s.trim();
    }
}
