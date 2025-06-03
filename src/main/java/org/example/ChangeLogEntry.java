package org.example;

import java.util.Map;

/**
 * The ChangeLogEntry class represents a log entry that is made between leads, during deduplication.
 * It stores the discarged lead, kept lead and the map of changes made to the lead's fields.
 */
public class ChangeLogEntry {
  private final Lead source;
  private final Lead kept;
  private final Map<String, Object[]> fieldChanges;

  public ChangeLogEntry(Lead source, Lead kept, Map<String, Object[]> fieldChanges) {
    this.source = source;
    this.kept = kept;
    this.fieldChanges = fieldChanges;
  }

  public Lead getSource() { return source; }
  public Lead getKept() { return kept; }
  public Map<String, Object[]> getFieldChanges() { return fieldChanges; }

  /**
   * Retuns a String representation of the log change entries, includes the discarded , kept
   * and changed leads.
   * @return a formatted structure of the log
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("\nDiscarded record:\n").append(source).append('\n');
    sb.append("\nKept record:\n").append(kept).append('\n');
    sb.append("\nChanged fields:\n");
    fieldChanges.forEach((k, v) -> sb.append("  ").append(k).append(" : ")
            .append(v[0]).append("  ➜  ")
            .append(v[1]).append('\n'));
    return sb.toString();
  }
}
