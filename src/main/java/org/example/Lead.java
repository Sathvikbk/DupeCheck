package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class Lead {
  @JsonProperty("_id")
  private String id;
  private String email;
  private String firstName;
  private String lastName;
  private String address;
  private Instant entryDate;
  public Lead() {}

  public Lead(String id, String email, String firstName, String lastName, String address, Instant entryDate) {
    this.id = id;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.address = address;
    this.entryDate = entryDate;
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }

  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }

  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }

  public Instant getEntryDate() { return entryDate; }
  public void setEntryDate(Instant entryDate) { this.entryDate = entryDate; }

  @Override
  public String toString() {
    return "Lead{" +
            "id='" + id + '\'' +
            ", email='" + email + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", address='" + address + '\'' +
            ", entryDate=" + entryDate +
            '}';
  }
}
