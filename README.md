# metaScience-metrics
Extension to metaScience to generate metrics

Process
---
* Phase 1. This phase takes the set of conferences and remove the ones that: (1) are a workshop, (2) are Australian, (3) are not in DBLP or (4) have less than 5 editions.
* Phase 2. This phase takes the set of conferences selected before and generate the corresponding collaboration graphs. In particular, a graph representing all the editions and five graphs for each five last editions are generated.
* Phase 3. This phase calculates a set of metrics for each conference. The results are stored in a CSV file.
