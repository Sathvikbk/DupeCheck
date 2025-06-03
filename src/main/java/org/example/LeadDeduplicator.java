package org.example;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The LeadDuplicator class is where the main logic for deduplication lies, the class compares
 * two lead objects based on either "ID" or "email". It prefers the one which has the later
 * timestamp when duplicates are present. lead objects that have null or empty values are discarded.
 * Logs the changes and invalid entries it encounters.
 */
public class LeadDeduplicator {
  public static class Result {
    private final List<Lead> deduped;
    private final List<ChangeLogEntry> log;
    private final List<String> invalidLogs;

    /**
     * Constructs a object containing duplicate leads, logs and invalid leads.
     * @param deduped list of deduped leads
     * @param log list of the changes made
     * @param invalidLogs list of the errors for invalid leads
     */
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

  /**
   * Dedpues a list of leads based on "ID" and "email".
   * @param leads list of leads to dedupe
   * @return a Result containing the duplicated leads, logs and invalid leads.
   */
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

  /**
   * Trims the whitespaces for the leads "ID" and "email".
   * @param lead the lead to be normalized.
   */
  private static void normalize(Lead lead){
    if(lead.getId() != null) lead.setId(lead.getId().trim());
    if(lead.getEmail() != null) lead.setEmail(lead.getEmail().trim());
  }

  /**
   * Converts the email string into lowercase.
   * @param s the email string
   * @return the email in lowercase
   */
  private static String normalizeEmail(String s){
    return s.toLowerCase();
  }

  /**
   * Checks if the values of the lead are populated and not null.
   * @param l the lead to validate
   * @return true if the lead is valid else returns false.
   */
  private static boolean isValid(Lead l){
    return notBlank(l.getId()) && notBlank(l.getEmail()) && notBlank(l.getFirstName())
            && notBlank(l.getLastName()) && notBlank(l.getAddress()) && l.getEntryDate() != null;
  }

  /**
   * Checks to see if the value is not NULL and not blank.
   * @param s a string to check
   * @return true if its not blank else false
   */
  private static boolean notBlank(String s){
    return s != null && !s.trim().isEmpty();
  }

  /**
   * Determines if the current lead should replace the existing lead with duplicate ID/email.
   * @param current the current lead which is being evaluated
   * @param existing the existing lead with the duplicate
   * @param curIndex index of the current lead in the original list
   * @param existIndex index of the existing lead in the survivors list
   * @return true if current should replace existing else false
   */
  private static boolean isCurrentPreferred(Lead current, Lead existing, int curIndex, int existIndex) {
    Instant c = current.getEntryDate();
    Instant e = existing.getEntryDate();
    if (c.isAfter(e)) return true;
    if (c.equals(e)) return curIndex > existIndex;
    return false;
  }

  /**
   * Compares two leads and returns a map of the changes, maps to an array [oldValue, newValue].
   * @param src the discarded lead
   * @param kept the retained lead
   * @return a map of changes
   */
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
