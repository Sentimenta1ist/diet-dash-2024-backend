package app.dietdash.config;

import com.fasterxml.jackson.core.Version;

import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * By default, liquibase is sorting the changelog files in alphabet. To support versioning in the file name this comparator parses major, minor and patch via
 * regex and uses Jackson's version class to compare them.
 */
@SuppressWarnings("unused")
public class LiquibaseChangelogComparator implements Comparator<String> {

    /**
     * Ignores the file path and only checks the file name for a version.
     * Input: db/changelog/changes/v1.0.0.yaml
     */
    private static final Pattern pattern = Pattern.compile("v(?<major>\\d*)\\.(?<minor>\\d*)\\.(?<patch>\\d*)\\.\\w*");

    @Override
    public int compare(String o1, String o2) {
        var matcher1 = pattern.matcher(o1);
        if (!matcher1.find()) {
            throw new IllegalStateException(o1 + " doesn't follow the naming convention. E.g. 'v1.1.12.yaml'.");
        }

        var matcher2 = pattern.matcher(o2);
        if (!matcher2.find()) {
            throw new IllegalStateException(o2 + " doesn't follow the naming convention. E.g. 'v1.1.12.yaml'.");
        }

        var version1 = createVersion(matcher1.group("major"), matcher1.group("minor"), matcher1.group("patch"));
        var version2 = createVersion(matcher2.group("major"), matcher2.group("minor"), matcher2.group("patch"));
        return version1.compareTo(version2);
    }

    private Version createVersion(String major, String minor, String patch) {
        return new Version(Integer.parseInt(major), Integer.parseInt(minor), Integer.parseInt(patch), null, null, null);
    }
}