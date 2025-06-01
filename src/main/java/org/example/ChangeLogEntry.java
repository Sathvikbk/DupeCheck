package org.example;

import java.util.Map;

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

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Discarded record:").append(source).append('\n');
    sb.append("Kept record:").append(kept).append('\n');
    sb.append("Changed fields:\n");
    fieldChanges.forEach((k, v) -> sb.append("  ").append(k).append(" : ")
            .append(v[0]).append("  ➜  ")
            .append(v[1]).append('\n'));
    return sb.toString();
  }
}
