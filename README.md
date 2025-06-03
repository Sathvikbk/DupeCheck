# DEDUPE CHECKER

## Overview
This tool helps clean up messy lead data by identifying duplicates based on matching `_id` or `email` fields, and keeping only the most recent record using the `entryDate`. It takes care of tricky edge cases like missing values, formatting issues, and invalid dates. Along the way, it keeps a detailed log of everything it removes, giving you a clear view of what changed and why.

The end result is a clean, reliable dataset you can trust, along with a full audit trail.

Instead of transitive duplicate logic, we use direct duplicate logic only—records are only compared directly based on `_id` or `email`.

Note: The leads.json file path is hard-coded and references the resources folder. To use a different input file, you'll need to update the path in Main.java.

-----------------------

## Sample Input JSON
```
{
  "leads": [
    {
      "_id": "123",
      "email": "jane@example.com",
      "firstName": "Jane",
      "lastName": "Doe",
      "address": "123 Main St",
      "entryDate": "2023-04-01T10:00:00Z"
    }
  ]
}
```
-----------------------

## Deduplication Logic
  Duplicates are checked based on `_id` first then duplicates are checked based on `email`.
  If duplicates are found, the record with the latest `entryDate` is retained.
  If timestamps are equal, the lead appearing later in the list is preferred.

-----------------------

## Assumptions
1. There is clean data provided, meaning that `_id` and `email` are in proper format

2. Leads are only processed in the order they are given (as order matters)

3. Comparison between duplicates is made on `_id` first, and then `email`

4. JSON file is the only form of input

5. Any leads which have null or empty values for any property will be removed and logged under the section "removed leads" in the log output

6. Any lead with null or empty fields for `_id`, `email`, `firstName`,`lastName`,`address`,`entryDate` are removed, leads with invalid or unparsable `entryDate` are removed as well. 

-----------------------

## Performance
**Time Complexity:** O(n)

**Space Complexity:** O(n), where n is the number of leads

-----------------------

## Class	Description
- `Lead:`	Data model for each lead with fields like `_id`, `email`, `entryDate`, etc.
- `LeadWrapper:`	Wrapper for serializing/deserializing the input JSON list of leads.
- `ChangeLogEntry:`	Captures detailed logs of what changed between discarded and retained leads.
- `LeadDeduplicator:`	Core logic for identifying duplicates, validating records, and comparing entries.
- `Main:`	Loads input, invokes deduplication, prints results, logs changes.

-----------------------
## Handels
- Missing/null fields

- Invalid date formats

- Time zone offsets

- Unicode look-alikes, email casing, and formatting inconsistencies
