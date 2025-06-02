package org.example;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class LeadDeduplicator {
  public static class Result {
    private final List<Lead> deduped;
    private final List<ChangeLogEntry> log;
    private final List<String> invalidLogs;
    public Result(List<Lead> deduped, List<ChangeLogEntry> log, List<String> invalidLogs) {
      this.deduped = deduped;
      this.log = log;
      this.invalidLogs = invalidLogs;
    }
    public List<Lead> getDeduped() {
      return deduped;
    }
    public List<ChangeLogEntry> getLog(){
      return log;
    }
    public List<String> getInvalidLogs(){return invalidLogs;}
  }

  public static Result deduplicate(List<Lead> leads) {
    List<Lead> survivors = new ArrayList<>();
    List<ChangeLogEntry> logs = new ArrayList<>();
    List<String> invalidLogs = new ArrayList<>();
    Map<String, Lead> idMap = new HashMap<>();
    Map<String, Lead> emailMap = new HashMap<>();

    for (int i = 0; i < leads.size(); i++) {
      Lead current = leads.get(i);
      normalize(current);

      if(!isValid(current)){
        invalidLogs.add("Removed record due to Null or empty field/fields" + current);
        continue;
      }

      String id = current.getId();
      String email = normalizeEmail(current.getEmail());

      Lead duplicate = Optional.ofNullable(idMap.get(id))
              .orElse(emailMap.get(email));

      if (duplicate == null) {
        survivors.add(current);
        idMap.put(id, current);
        emailMap.put(email, current);
        continue;
      }

      boolean currentWins = isCurrentPreferred(current, duplicate, i, survivors.indexOf(duplicate));
      if (currentWins) {

        survivors.remove(duplicate);
        survivors.add(current);

        idMap.put(id, current);
        emailMap.put(email, current);

        String dupEmail = normalizeEmail(duplicate.getEmail());
        if (!dupEmail.equals(email)) {
          emailMap.remove(dupEmail);
        }

        logs.add(new ChangeLogEntry(duplicate, current, diff(duplicate, current)));
      } else {
        logs.add(new ChangeLogEntry(current, duplicate, diff(current, duplicate)));
      }
    }
    return new Result(survivors, logs, invalidLogs);
  }

  private static void normalize(Lead lead){
    if(lead.getId() != null) lead.setId(lead.getId().trim());
    if(lead.getEmail() != null) lead.setEmail(lead.getEmail().trim());
  }

  private static String normalizeEmail(String s){
    return (s == null) ? null : s.trim().toLowerCase();
  }
  private static boolean isValid(Lead l){
    return notBlank(l.getId()) && notBlank(l.getEmail()) && notBlank(l.getFirstName())
            && notBlank(l.getLastName()) && notBlank(l.getAddress()) && l.getEntryDate() != null;
  }

  private static boolean notBlank(String s){
    return s != null && !s.trim().isEmpty();
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
    if (!Objects.equals(src.getId(),kept.getId())) d.put("_id",
            new Object[]{src.getId(), kept.getId()});
    if (!Objects.equals(src.getEmail(), kept.getEmail())) d.put("email",
            new Object[]{src.getEmail(), kept.getEmail()});
    if (!Objects.equals(src.getFirstName(), kept.getFirstName())) d.put("firstName",
            new Object[]{src.getFirstName(), kept.getFirstName()});
    if (!Objects.equals(src.getLastName(), kept.getLastName())) d.put("lastName",
            new Object[]{src.getLastName(), kept.getLastName()});
    if (!Objects.equals(src.getAddress(), kept.getAddress())) d.put("address",
            new Object[]{src.getAddress(), kept.getAddress()});
    if (!Objects.equals(src.getEntryDate(), kept.getEntryDate())) d.put("entryDate",
            new Object[]{src.getEntryDate(), kept.getEntryDate()});
    return d;
  }
}
