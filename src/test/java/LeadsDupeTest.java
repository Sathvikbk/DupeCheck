import org.example.Lead;
import org.example.LeadDeduplicator;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LeadsDupeTest {
  private static Lead lead(String id, String email, String first, String last, String address, Instant entryTime) {
    return new Lead(id, email, first, last, address, entryTime);
  }

  private static LeadDeduplicator.Result run(Lead... leads) {
    return LeadDeduplicator.deduplicate(List.of(leads));
  }

  /**
   * Same Id, unique email -> one with later time wins
   */
  @Test
  void duplicateId() {
    Lead a = new Lead("abc", "abc1@email.com", "A", "X", "address1", Instant.parse("2024-05-07T17:30:20Z"));
    Lead b = new Lead("abc", "abc2@email.com", "B", "Y", "address2", Instant.parse("2024-05-07T17:32:20Z"));
    Lead c = new Lead("abc", "abc3@email.com", "C", "Z", "address3", Instant.parse("2024-05-07T17:28:20Z"));

    LeadDeduplicator.Result res = run(a, b, c);

    assertEquals(1, res.getDeduped().size(), 1);
    assertEquals(b, res.getDeduped().get(0));
    assertEquals(2, res.getLog().size());
  }

  /**
   * Same Email unique id -> one with later time wins
   */
  @Test
  void duplicateEmail_uniqueId(){
    Lead a = new Lead("abcc", "abc@email.com", "A", "X", "address1", Instant.parse("2024-05-07T17:30:20Z"));
    Lead b = new Lead("abcd", "abc@email.com", "B", "Y", "address2", Instant.parse("2024-05-07T17:32:20Z"));
    Lead c = new Lead("abca", "abc@email.com", "C", "Z", "address3", Instant.parse("2024-05-07T17:28:20Z"));

    LeadDeduplicator.Result res = run(a,b,c);

    assertEquals(1, res.getDeduped().size());
    assertEquals(b, res.getDeduped().get(0));
    assertEquals(2, res.getLog().size());
  }

  /**
   * Same Email -> one with later time wins
   */
  @Test
  void duplicateEmailChain(){
    Lead a = new Lead("abcc", "abc@email.com", "A", "X", "address1", Instant.parse("2024-05-07T17:30:20Z"));
    Lead b = new Lead("abcd", "abc@email.com", "B", "Y", "address2", Instant.parse("2024-05-07T17:32:20Z"));
    Lead c = new Lead("abca", "abc@email.com", "C", "Z", "address3", Instant.parse("2024-05-07T17:28:20Z"));
    Lead d = new Lead("abc", "abc@email.com", "D", "Z", "address3", Instant.parse("2024-05-07T17:37:20Z"));


    LeadDeduplicator.Result res = run(a,b,c,d);
    assertEquals(1, res.getDeduped().size());
    assertEquals(d, res.getDeduped().get(0));
    assertEquals(3, res.getLog().size());
  }

  /**
   * Same Id -> one with later time wins
   */
  @Test
  void duplicateIdChain(){
    Lead a = new Lead("abc", "abc1@email.com", "A", "X", "address1", Instant.parse("2024-05-07T17:30:20Z"));
    Lead b = new Lead("abc", "abc2@email.com", "B", "Y", "address2", Instant.parse("2024-05-07T17:32:20Z"));
    Lead c = new Lead("abc", "abc3@email.com", "C", "Z", "address3", Instant.parse("2024-05-07T17:28:20Z"));
    Lead d = new Lead("abc", "abc4@email.com", "D", "Z", "address3", Instant.parse("2024-05-07T17:29:20Z"));

    LeadDeduplicator.Result res = run(a,b,c,d);
    assertEquals(1, res.getDeduped().size());
    assertEquals(b, res.getDeduped().get(0));
    assertEquals(3, res.getLog().size()) ;
  }

  /**
   * Chain of duplicates with either Email or Id
   */
  @Test
  void duplicateChain(){
    Lead a = new Lead("abc", "abc1@email.com", "A", "X", "address1", Instant.parse("2024-05-07T17:30:20Z"));
    Lead b = new Lead("abc", "abc2@email.com", "B", "Y", "address2", Instant.parse("2024-05-07T17:32:20Z"));
    Lead c = new Lead("abcd", "abc2@email.com", "C", "Z", "address3", Instant.parse("2024-05-07T17:28:20Z"));
    Lead d = new Lead("abce", "abc4@email.com", "D", "Z", "address3", Instant.parse("2024-05-07T17:29:20Z"));

    LeadDeduplicator.Result res = run(a,b,c,d);
    assertEquals(2, res.getDeduped().size());
    assertEquals(2, res.getLog().size());
    assertEquals(true, res.getDeduped().contains(b));
    assertEquals(true, res.getDeduped().contains(d));
  }

  /**
   * all uniqe records
   */
  @Test
  void uniqueRecords(){
    Lead a = new Lead("a", "abc1@email.com", "A", "X", "address1", Instant.parse("2024-05-07T17:30:20Z"));
    Lead b = new Lead("ab", "abc2@email.com", "B", "Y", "address2", Instant.parse("2024-05-07T17:32:20Z"));
    Lead c = new Lead("abc", "abc3@email.com", "C", "Z", "address3", Instant.parse("2024-05-07T17:28:20Z"));
    Lead d = new Lead("abcd", "abc4@email.com", "D", "Z", "address4", Instant.parse("2024-05-07T17:29:20Z"));

    LeadDeduplicator.Result res = run(a,b,c,d);
    assertEquals(4, res.getDeduped().size());
    assertEquals(0, res.getLog().size());
    assertEquals(true, res.getDeduped().contains(a));
    assertEquals(true, res.getDeduped().contains(b));
    assertEquals(true, res.getDeduped().contains(c));
    assertEquals(true, res.getDeduped().contains(d));

  }

  /**
   * Time stamps change
   */
  @Test
  void timeStamps(){
    Lead a = new Lead("abc", "abc1@email.com", "A", "X", "address1", Instant.parse("2024-05-07T17:30:20Z"));
    Lead b = new Lead("abc", "abc2@email.com", "B", "Y", "address2", Instant.parse("2024-05-07T17:32:20Z"));
    Lead c = new Lead("abc", "abc3@email.com", "C", "Z", "address3", Instant.parse("2024-05-07T17:28:20Z"));

    LeadDeduplicator.Result res = run(a,b,c);
    assertEquals(1, res.getDeduped().size());
    assertEquals(b, res.getDeduped().get(0));
    assertEquals(2, res.getLog().size());

  }

  /**
   * Same date different time
   */
  @Test
  void sameDateDiffTime(){
    Lead a = new Lead("abc", "abc1@email.com", "A", "X", "address1", Instant.parse("2024-05-07T17:30:20Z"));
    Lead b = new Lead("abcd", "abc2@email.com", "B", "Y", "address2", Instant.parse("2024-05-07T17:32:20Z"));
    Lead c = new Lead("abc", "abc3@email.com", "C", "Z", "address3", Instant.parse("2024-05-07T17:28:20Z"));

    LeadDeduplicator.Result res = run(a,b,c);
    assertEquals(2, res.getDeduped().size());
    assertEquals(a, res.getDeduped().get(0));
    assertEquals(b, res.getDeduped().get(1));
    assertEquals(1, res.getLog().size());

  }




}
