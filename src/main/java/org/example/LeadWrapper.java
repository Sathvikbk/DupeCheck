package org.example;

import java.util.List;

/**
 * This wrapper class encapsulated a list of lead objects.
 */

public class LeadWrapper {
  private List<Lead> leads;
  public LeadWrapper() {}
  public LeadWrapper(List<Lead> leads) { this.leads = leads; }
  public List<Lead> getLeads() { return leads; }
  public void setLeads(List<Lead> leads) { this.leads = leads; }
}
