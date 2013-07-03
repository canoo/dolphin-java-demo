package org.opendolphin.demo.data;

public class LastNameGenerator extends AbstractValueGenerator<String> {

    private static final String[] TITLE = {
            "de ", "van ", "von ", "Mc", "Mac", "O'"
    };

    private static final String[] FIRST_PREFIX = {
            "A", "Al", "Ben", "Ca", "Co", "De", "E", "El", "Fa", "Fur", "Gar", "Go", "Gut",
            "He", "Lo", "In", "Ke", "Koe", "Mul", "New", "O", "Schwarz", "Rav", "U", "Za", "Zo"
    };

    private static final String[] FIRST_SUFFIX = {
            "bel", "bi", "by", "del", "e", "en", "el", "le", "lis", "i",
            "men", "mi", "nig", "om", "pen", "ro", "ru", "u", "von"
    };

    private static final String[] SECOND_PREFIX = {
            "Berg", "Brend", "Egg", "Fell", "Flint", "Fred", "Gel", "Gold", "Holz", "I",
            "King", "Lipp", "Port", "Ray", "Rock", "Smith", "Tar", "Tomb", "Wind"
    };

    private static final String[] SECOND_SUFFIX = {
            "del", "e", "en", "er", "el", "le", "man", "sen", "son", "sor", "stone", "ter", "ton", "tosh"
    };

    public String randomValue() {
        StringBuilder builder = new StringBuilder();
        boolean useTitle = getRandomBoolean(0.1);
        if (useTitle) {
            builder.append(getRandomString(TITLE));
        }
        boolean useFirstPrefix = getRandomBoolean(0.8);
        if (useFirstPrefix) {
            builder.append(getRandomString(FIRST_PREFIX));
        }
        boolean useFirstSuffix = useFirstPrefix && getRandomBoolean(0.8);
        if (useFirstSuffix) {
            String baseName = getRandomString(FIRST_SUFFIX);
            builder.append(baseName);
        }
        boolean useSecondPrefix = !useFirstPrefix || getRandomBoolean(0.2);
        if (useSecondPrefix) {
            String secondPrefix = getRandomString(SECOND_PREFIX);
            if (useFirstPrefix) {
                secondPrefix = secondPrefix.toLowerCase();
            }
            builder.append(secondPrefix);
        }
        boolean useSecondSuffix = !useFirstSuffix || getRandomBoolean(0.2);
        if (useSecondSuffix) {
            builder.append(getRandomString(SECOND_SUFFIX));
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        LastNameGenerator generator = new LastNameGenerator();
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 20; j++) {
                System.out.print(generator.randomValue() + ", ");
            }
            System.out.println();
        }
        System.out.println("LastName: " + TITLE.length * FIRST_PREFIX.length * FIRST_PREFIX.length * SECOND_PREFIX.length * SECOND_SUFFIX.length + " variations possible");
    }

}
