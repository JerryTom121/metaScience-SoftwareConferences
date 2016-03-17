What is this project about?
===========================

This project describes a three-phased process to calculate metrics in co-authorship graphs populated from data coming from the [DBLP database](http://dblp.l3s.de). This implementation is part of [MetaScience](https://github.com/SOM-Research/metaScience), a tool service developed to help researchers analyze their research profile and that of the conferences/journals where they publish.

The current implementation has been adapted to calculate the metrics required in the research work presented in the paper titled *Analysis of co-authorship Graphs of CORE-Ranked Software Conferences*, currently under review. In our work, we calculate a set of metrics for a subset of conferences included in the [CORE ranking list](http://www.core.edu.au/). 

The **metric results used in our study** are presented in:
 - [Metric results](data/phase3-results.xlsx), containing the metric results for each conference considered in our study.
 - [Metric results grouped](data/phase3-results-groupedPerCORERank.xlsx), containing the results grouped by the CORE rank. 
 
Also, you can find the set of [co-authorship graphs](data/graphs) used in our study.

The process
===========

Phase 1
-------

This phase is in charge of selecting a set of conferences to be analyzed. Each conference will be represented as a property file including a set of properties describing the main features of the conference. You can find more information about the properties to use in the documentation of the class [Phase2Launcher](phase2/src/som/metascience/Phase2Launcher.java). Also, you will find some examples of these proerty files in [this folder](data/importData).
 
The current implementation has been adapted to digest the full set of CORE-ranked conference list and filter a subset of its entries to keep only international conferences in the software domain. In particular, the selection process takes all the conferences tagged as computer software in the field of research property in the CORE list and removes (1) workshops, (2) Australasian/Australian conferences, (3) conferences
not included in DBLP, (4) conferences with less than five editions including one in 2014, and (5) conferences not reporting the length of their papers.

The resulting list of property files will be used as input in the following phase.

More details regarding this phase can be found in [this folder](phase1)

Phase 2
-------

This phase generates the co-authorship graphs for a set of conferences. In this kind of graphs, authors who have published a paper in the conference are represented as nodes while co-authorship is represented as an edge between the involved author nodes. Furthermore, the weight of a node represents the number of papers accepted in the conference for an author while the weight of an edge indicates the number of times those author nodes have coauthored a paper in the conference.  

This phase takes as input the set of property files generated in the previous phase representing conferences and populates the co-authorship graph by accessing to the [DBLP database](http://dblp.l3s.de), which has to be deployed before somewhere. 

More details regarding this phase can be found in [this folder](phase2)

Phase 3
-------

This phase is in charge of calculating the metrics we have considered for the analysis of the conferences. It takes as input the set of co-authorship graphs generated previously and generates a CSV file with the set of metric results per conference.

More details regarding this phase can be found in [this folder](phase3)

What can you find in this repository
====================================

The project structure include a folder containing the source code for each phase of the process. Additionally, the folder [data](data) includes the results used in our research work titled *Analysis of co-authorship Graphs of CORE-Ranked Software Conferences*.

Note that the process relies on a MySQL database includeing the DBLP dataset provided [here](http://dblp.l3s.de).

Can I collaborate?
==================

Absolutely!. You can follow the typical GitHub contribution flow to ask/contribute:

 - For bugs or feature requests you can directly create an [issue](https://github.com/SOM-Research/metaScience-SoftwareConferences/issues). 
 - For implementing new features or directly address any issue, please fork the project, perform the patch and send us a [pull request](https://github.com/SOM-Research/metaScience-SoftwareConferences/pulls).

Whatever your contribution is, you will have an answer from us in less than 7 days.

You can find our (simple) governance rules in this [file](governance.md).

Who is behind this project?
===========================

* [Javier Canovas](http://github.com/jlcanovas/ "Javier Canovas")
* [Valerio Cosentino](https://github.com/valeriocos "Valerio Cosentino")
* [Jordi Cabot](http://github.com/jcabot/ "Jordi Cabot")

License
---
This project is licensed under the [EPL](http://www.eclipse.org/legal/epl-v10.html) license. Documentation is licensed under the under [CC BY 3.0](http://creativecommons.org/licenses/by/3.0/).