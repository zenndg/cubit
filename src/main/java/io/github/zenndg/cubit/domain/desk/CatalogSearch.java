package io.github.zenndg.cubit.domain.desk;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * The search of the catalog. It compares the name the player sees, in their language, without case
 * or accents. A search looks in every category, and without one only the chosen category shows
 * (answer 1523).
 */
public final class CatalogSearch {
  private static final Pattern ACCENTS = Pattern.compile("\\p{M}+");
  private static final String NOTHING = "";

  private final String text;

  private CatalogSearch(String text) {
    this.text = text;
  }

  public static CatalogSearch of(String typed) {
    return new CatalogSearch(typed == null ? NOTHING : fold(typed.strip()));
  }

  public boolean isEmpty() {
    return text.isEmpty();
  }

  public boolean matches(String name) {
    return fold(name).contains(text);
  }

  public boolean shows(DeskCategory category, String name, DeskCategory chosen) {
    return isEmpty() ? category == chosen : matches(name);
  }

  private static String fold(String value) {
    String split = Normalizer.normalize(value, Normalizer.Form.NFD);
    return ACCENTS.matcher(split).replaceAll(NOTHING).toLowerCase(Locale.ROOT);
  }
}
