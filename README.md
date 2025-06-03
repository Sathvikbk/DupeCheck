# DEDUPE CHECKER

This tool helps clean up messy lead data by identifying duplicates based on matching id or email fields, and keeping only the most recent record using the entryDate. 
It takes care of tricky edge cases like missing values, formatting issues, and invalid dates. 
Along the way, it keeps a detailed log of everything it removes, giving you a clear view of what changed and why. 
The end result is a clean, reliable dataset you can trust, along with a full audit trail for peace of mind.
Instead of transitive duplicate logic we use direct duplicate logic only. 
the leads.json file path is hard coded ans references the resources file in the progam , in order to use this for other files the path needs to be changed.

-----------------------
There are a few assumptions made on the way, being:
1. There is clean data provided meaning that ID and Email are in proper format
2. Leads are only processed in the order they are given (as order matters)
3. comparision between duplicates is made on ID first and then email
4. JSON file is the only form of input
5. Any Leads which have null or empty values for any property will be removed and put in a colum called removed leads in the log file

------------------------

Deduplicates leads based on:
  - Matching `id`
  - Matching `email` (case/whitespace insensitive)
-  Compares using `entryDate` to retain the most recent record
- Handles:
  - Missing/null fields
  - Invalid date formats
  - Time zone offsets
  - Unicode look-alikes, email casing, and formatting inconsistencies
-  Generates:
  - Deduplicated JSON
  - Human-readable change log
- Includes a full JUnit test suite
- Time and Space complexity of the solution is O(n) where n is the number of leads
