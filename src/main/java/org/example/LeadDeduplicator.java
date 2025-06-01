package org.example;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class LeadDeduplicator {
  public static class Result {
    private final List<Lead> deduped;
    private final List<ChangeLogEntry> log;
    public Result(List<Lead> deduped, List<ChangeLogEntry> log) {
      this.deduped = deduped;
      this.log = log;
    }
    public List<Lead> getDeduped() {
      return deduped;
    }
    public List<ChangeLogEntry> getLog(){
      return log;
    }
  }

  public static Result deduplicate(List<Lead> leads) {
    List<Lead> survivors = new ArrayList<>();
    List<ChangeLogEntry> logs = new ArrayList<>();
    Map<String, Lead> idMap = new HashMap<>();
    Map<String, Lead> emailMap = new HashMap<>();

    for (int i = 0; i < leads.size(); i++) {
      Lead current = leads.get(i);
      Lead duplicate = Optional.ofNullable(idMap.get(current.getId()))
              .orElse(emailMap.get(current.getEmail()));

      if (duplicate == null) {
        survivors.add(current);
        idMap.put(current.getId(), current);
        emailMap.put(current.getEmail(), current);
        continue;
      }

      boolean currentWins = isCurrentPreferred(current, duplicate, i, survivors.indexOf(duplicate));
      if (currentWins) {

        survivors.remove(duplicate);
        survivors.add(current);

        idMap.put(current.getId(), current);
        emailMap.put(current.getEmail(), current);

        if (!duplicate.getEmail().equals(current.getEmail())) {
          emailMap.remove(duplicate.getEmail());
        }

        logs.add(new ChangeLogEntry(duplicate, current, diff(duplicate, current)));
      } else {
        logs.add(new ChangeLogEntry(current, duplicate, diff(current, duplicate)));
      }
    }
    return new Result(survivors, logs);
  }

  private static boolean isCurrentPreferred(Lead current, Lead existing, int curIndex, int existIndex) {
    Instant c = current.getEntryDate();
    Instant e = existing.getEntryDate();
    if (c.isAfter(e)) return true;
    if (c.equals(e)) return curIndex > existIndex;
    return false;
  }

  private static Map<String, Object[]> diff(Lead src, Lead kept) {
    Map<String, Object[]> d = new LinkedHashMap<>();
    if (!Objects.equals(src.getId(),kept.getId())) d.put("_id", new Object[]{src.getId(), kept.getId()});
    if (!Objects.equals(src.getEmail(), kept.getEmail())) d.put("email", new Object[]{src.getEmail(), kept.getEmail()});
    if (!Objects.equals(src.getFirstName(), kept.getFirstName())) d.put("firstName", new Object[]{src.getFirstName(), kept.getFirstName()});
    if (!Objects.equals(src.getLastName(), kept.getLastName())) d.put("lastName", new Object[]{src.getLastName(), kept.getLastName()});
    if (!Objects.equals(src.getAddress(), kept.getAddress())) d.put("address", new Object[]{src.getAddress(), kept.getAddress()});
    if (!Objects.equals(src.getEntryDate(), kept.getEntryDate())) d.put("entryDate", new Object[]{src.getEntryDate(), kept.getEntryDate()});
    return d;
  }
}
