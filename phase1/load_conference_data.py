__author__ = 'valerio cosentino'

#This script selects a sub-set of conference to analyze (according to two filtering rules [the number of editions and the last edition of the conference]).
#For each selected conference a configuration file is generated and added to the folder "output".
#A configuration file contains general information about the conference and SQL queries to retrieve the conference details from the MetaScience DB.

import mysql.connector
from mysql.connector import errorcode
import db_connection
import json
import re

cnx = mysql.connector.connect(**db_connection.CONFIG)

#input/output data
CONFERENCE_FILE = 'data/treated_output.json'
DESTINATION_FOLDER = '../data/importData'

#selection conference parameters
THRESHOLD_EDITIONS = 5
THRESHOLD_LAST_EDITION = 2015

#serialization settings
DEBUG = False
ACTIVATE_QUERY_ALL_EDITIONS = False
ACTIVATE_QUERY_LAST_EDITIONS = True

ACTIVATE_QUERIES_PER_EDITION = True
ACTIVATE_QUERIES_CONFERENCE_EVOLUTION = True

LAST_EDITIONS = True
FILTER_NUMBER_PAGES = 5


def create_file_name(title):
    title = re.sub('(\s|\W)+', '-', title)
    return title


def create_config_file(title, source, source_id, years, num_editions, url, rank):
    last_editions = years[:THRESHOLD_EDITIONS]
    f = open(DESTINATION_FOLDER + '/' + create_file_name(title) + '.properties', 'w')
    f.write('# Name of the conference (REQUIRED)\n')
    f.write('conferenceName=' + title + '\n')
    f.write('#CORE rank (REQUIRED)\n')
    f.write('rank=' + rank + '\n')
    f.write('#total number of editions\n')
    f.write('editions=' + str(num_editions) + '\n')
    f.write("#list of editions\n")
    f.write("editionQueries=" + ",".join(str(e) for e in years) + "\n")
    f.write('#dblp urls\n')
    f.write('urls=' + ','.join(url) + '\n')
    f.write('#acronym of the conference\n')
    f.write('sources=' + source + '\n')
    f.write('#acronym of the conference in dblp\n')
    f.write('source_ids=' + source_id + '\n')

    if ACTIVATE_QUERY_ALL_EDITIONS:
        f.write('# Queries to get the full graph\n')
        f.write("fullNodes=SELECT airn.author_id AS id, airn.author AS label, COUNT(pub.id) AS size FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id WHERE source IN (" + source.encode('utf-8') + ") AND source_id IN (" + source_id.encode('utf-8') + ") AND pub.type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " GROUP BY airn.author_id;\n")
        f.write("fullEdges=SELECT source_author_id AS source, target_author_id AS target, relation_strength AS weight FROM ( SELECT source_authors.author AS source_author_name, source_authors.author_id AS source_author_id, target_authors.author AS target_author_name, target_authors.author_id AS target_author_id, COUNT(*) AS relation_strength, CONCAT(GREATEST(source_authors.author_id, target_authors.author_id), '-', LEAST(source_authors.author_id, target_authors.author_id)) AS connection_id FROM  ( SELECT pub.id AS pub, author, author_id FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id WHERE source IN (" + source.encode('utf-8') + ") AND source_id IN (" + source_id.encode('utf-8') + ") AND pub.type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " ) AS source_authors JOIN ( SELECT pub.id AS pub, author, author_id FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id WHERE source IN (" + source.encode('utf-8') + ") AND source_id IN (" + source_id.encode('utf-8') + ") AND pub.type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + ") AS target_authors ON source_authors.pub = target_authors.pub AND source_authors.author_id <> target_authors.author_id GROUP BY source_authors.author_id, target_authors.author_id) AS x GROUP BY connection_id;\n")

    if ACTIVATE_QUERY_LAST_EDITIONS:
        f.write('# Queries to get the full graph for the last ' + str(THRESHOLD_EDITIONS) + ' editions\n')
        f.write("fullNodesLast" + str(THRESHOLD_EDITIONS) + "Editions=SELECT airn.author_id AS id, airn.author AS label, COUNT(pub.id) AS size FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id WHERE source IN (" + source.encode('utf-8') + ") AND source_id IN (" + source_id.encode('utf-8') + ") AND pub.type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " AND pub.year IN ( " + ','.join(str(e) for e in last_editions) + " ) GROUP BY airn.author_id;\n")
        f.write("fullEdgesLast" + str(THRESHOLD_EDITIONS) + "Editions=SELECT source_author_id AS source, target_author_id AS target, relation_strength AS weight FROM ( SELECT source_authors.author AS source_author_name, source_authors.author_id AS source_author_id, target_authors.author AS target_author_name, target_authors.author_id AS target_author_id, COUNT(*) AS relation_strength, CONCAT(GREATEST(source_authors.author_id, target_authors.author_id), '-', LEAST(source_authors.author_id, target_authors.author_id)) AS connection_id FROM  ( SELECT pub.id AS pub, author, author_id FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id WHERE source IN (" + source.encode('utf-8') + ") AND source_id IN (" + source_id.encode('utf-8') + ") AND pub.type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " AND pub.year IN ( " + ','.join(str(e) for e in last_editions) + " )) AS source_authors JOIN ( SELECT pub.id AS pub, author, author_id FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id WHERE source IN (" + source.encode('utf-8') + ") AND source_id IN (" + source_id.encode('utf-8') + ") AND pub.type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " AND pub.year IN ( " + ','.join(str(e) for e in last_editions) + " )) AS target_authors ON source_authors.pub = target_authors.pub AND source_authors.author_id <> target_authors.author_id GROUP BY source_authors.author_id, target_authors.author_id) AS x GROUP BY connection_id;\n")

    if ACTIVATE_QUERIES_PER_EDITION:
        f.write("#####-#####-#####-#####\n")
        f.write("# Queries to get the graph per edition\n")
        if LAST_EDITIONS:
            f.write("# Last " + str(THRESHOLD_EDITIONS) + " editions\n")

        for y in years:
            if LAST_EDITIONS:
                if y in last_editions:
                    f.write("#####-#####-#####-#####\n")
                    f.write("SingleEditionLast" + str(THRESHOLD_EDITIONS) + "Editions" + str(y) + "Nodes=SELECT airn.author_id AS id, airn.author AS label, COUNT(pub.id) AS size FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id WHERE source IN (" + source.encode('utf-8') + ") AND source_id IN (" + source_id.encode('utf-8') + ") AND pub.type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " AND pub.year = " + str(y) + " GROUP BY airn.author_id;\n")
                    f.write("SingleEditionLast" + str(THRESHOLD_EDITIONS) + "Editions" + str(y) + "Edges=SELECT source_author_id AS source, target_author_id AS target, relation_strength AS weight FROM ( SELECT source_authors.author AS source_author_name, source_authors.author_id AS source_author_id, target_authors.author AS target_author_name, target_authors.author_id AS target_author_id, COUNT(*) AS relation_strength, CONCAT(GREATEST(source_authors.author_id, target_authors.author_id), '-', LEAST(source_authors.author_id, target_authors.author_id)) AS connection_id FROM  ( SELECT pub.id AS pub, author, author_id FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id WHERE source IN (" + source.encode('utf-8') + ") AND source_id IN (" + source_id.encode('utf-8') + ") AND pub.type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " AND pub.year = " + str(y) + ") AS source_authors JOIN ( SELECT pub.id AS pub, author, author_id FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id WHERE source IN (" + source.encode('utf-8') + ") AND source_id IN (" + source_id.encode('utf-8') + ") AND pub.type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " AND pub.year = " + str(y) + ") AS target_authors ON source_authors.pub = target_authors.pub AND source_authors.author_id <> target_authors.author_id GROUP BY source_authors.author_id, target_authors.author_id) AS x GROUP BY connection_id;\n")
            else:
                f.write("#####-#####-#####-#####\n")
                f.write("SingleEdition" + str(y) + "Nodes=SELECT airn.author_id AS id, airn.author AS label, COUNT(pub.id) AS size FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id WHERE source IN (" + source.encode('utf-8') + ") AND source_id IN (" + source_id.encode('utf-8') + ") AND pub.type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " AND pub.year = " + str(y) + " GROUP BY airn.author_id;\n")
                f.write("SingleEdition" + str(y) + "Edges=SELECT source_author_id AS source, target_author_id AS target, relation_strength AS weight FROM ( SELECT source_authors.author AS source_author_name, source_authors.author_id AS source_author_id, target_authors.author AS target_author_name, target_authors.author_id AS target_author_id, COUNT(*) AS relation_strength, CONCAT(GREATEST(source_authors.author_id, target_authors.author_id), '-', LEAST(source_authors.author_id, target_authors.author_id)) AS connection_id FROM  ( SELECT pub.id AS pub, author, author_id FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id WHERE source IN (" + source.encode('utf-8') + ") AND source_id IN (" + source_id.encode('utf-8') + ") AND pub.type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " AND pub.year = " + str(y) + ") AS source_authors JOIN ( SELECT pub.id AS pub, author, author_id FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id WHERE source IN (" + source.encode('utf-8') + ") AND source_id IN (" + source_id.encode('utf-8') + ") AND pub.type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " AND pub.year = " + str(y) + ") AS target_authors ON source_authors.pub = target_authors.pub AND source_authors.author_id <> target_authors.author_id GROUP BY source_authors.author_id, target_authors.author_id) AS x GROUP BY connection_id;\n")
        f.write("#####-#####-#####-#####\n")

    if ACTIVATE_QUERIES_CONFERENCE_EVOLUTION:
        f.write("#####-#####-#####-#####\n")
        f.write("# Queries to get the evolution of the collaboration graph\n")
        if LAST_EDITIONS:
            f.write("# Last " + str(THRESHOLD_EDITIONS) + " editions\n")

        for y in years:
            if LAST_EDITIONS:
                if y in last_editions:
                    f.write("#####-#####-#####-#####\n")
                    f.write("UntilEditionLast" + str(THRESHOLD_EDITIONS) + "Editions" + str(y) + "Nodes=SELECT airn.author_id AS id, airn.author AS label, COUNT(pub.id) AS size FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id WHERE source IN (" + source.encode('utf-8') + ") AND source_id IN (" + source_id.encode('utf-8') + ") AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " AND pub.type = 'inproceedings' AND pub.year <= " + str(y) + " AND pub.year >= " + str(last_editions[-1]) + " GROUP BY airn.author_id;\n")
                    f.write("UntilEditionLast" + str(THRESHOLD_EDITIONS) + "Editions" + str(y) + "Edges=SELECT source_author_id AS source, target_author_id AS target, relation_strength AS weight FROM ( SELECT source_authors.author AS source_author_name, source_authors.author_id AS source_author_id, target_authors.author AS target_author_name, target_authors.author_id AS target_author_id, COUNT(*) AS relation_strength, CONCAT(GREATEST(source_authors.author_id, target_authors.author_id), '-', LEAST(source_authors.author_id, target_authors.author_id)) AS connection_id FROM  ( SELECT pub.id AS pub, author, author_id FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id WHERE source IN (" + source.encode('utf-8') + ") AND source_id IN (" + source_id.encode('utf-8') + ") AND pub.type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " AND pub.year <= " + str(y) + " AND pub.year >= " + str(last_editions[-1]) + ") AS source_authors JOIN ( SELECT pub.id AS pub, author, author_id FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id WHERE source IN (" + source.encode('utf-8') + ") AND source_id IN (" + source_id.encode('utf-8') + ") AND pub.type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " AND pub.year <= " + str(y) + " AND pub.year >= " + str(last_editions[-1]) + ") AS target_authors ON source_authors.pub = target_authors.pub AND source_authors.author_id <> target_authors.author_id GROUP BY source_authors.author_id, target_authors.author_id) AS x GROUP BY connection_id;\n")
            else:
                f.write("#####-#####-#####-#####\n")
                f.write("UntilEdition" + str(y) + "Nodes=SELECT airn.author_id AS id, airn.author AS label, COUNT(pub.id) AS size FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id WHERE source IN (" + source.encode('utf-8') + ") AND source_id IN (" + source_id.encode('utf-8') + ") AND pub.type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " AND pub.year <= " + str(y) + " GROUP BY airn.author_id;\n")
                f.write("UntilEdition" + str(y) + "Edges=SELECT source_author_id AS source, target_author_id AS target, relation_strength AS weight FROM ( SELECT source_authors.author AS source_author_name, source_authors.author_id AS source_author_id, target_authors.author AS target_author_name, target_authors.author_id AS target_author_id, COUNT(*) AS relation_strength, CONCAT(GREATEST(source_authors.author_id, target_authors.author_id), '-', LEAST(source_authors.author_id, target_authors.author_id)) AS connection_id FROM  ( SELECT pub.id AS pub, author, author_id FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id WHERE source IN (" + source.encode('utf-8') + ") AND source_id IN (" + source_id.encode('utf-8') + ") AND pub.type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " AND pub.year <= " + str(y) + ") AS source_authors JOIN ( SELECT pub.id AS pub, author, author_id FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id WHERE source IN (" + source.encode('utf-8') + ") AND source_id IN (" + source_id.encode('utf-8') + ") AND pub.type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " AND pub.year <= " + str(y) + ") AS target_authors ON source_authors.pub = target_authors.pub AND source_authors.author_id <> target_authors.author_id GROUP BY source_authors.author_id, target_authors.author_id) AS x GROUP BY connection_id;\n")
        f.write("#####-#####-#####-#####\n")

    f.close()


def to_string(list):
    if len(list) == 1:
        str = list[0]
    else:
        str = "','".join(list)

    return "'" + str + "'"


def get_editions_count(source, source_id):
    cursor = cnx.cursor()
    query = "SELECT COUNT(*) AS editions " \
            "FROM (" \
            "SELECT year FROM dblp_pub_new " \
            "WHERE source IN (" + source + ") AND source_id IN (" + source_id + ") AND type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " " + \
            "GROUP BY year) AS x"
    cursor.execute(query)
    row = cursor.fetchone()
    cursor.close()

    return row[0]


def get_year_editions(source, source_id):
    years = []
    cursor = cnx.cursor()
    query = "SELECT year " \
            "FROM dblp_pub_new " \
            "WHERE source IN (" + source + ") AND source_id IN (" + source_id + ") AND type = 'inproceedings' AND calculate_num_of_pages(pages) >= " + str(FILTER_NUMBER_PAGES) + " " + \
            "GROUP BY year " \
            "ORDER BY year DESC"
    cursor.execute(query)
    row = cursor.fetchone()

    while row:
        years.append(row[0])
        row = cursor.fetchone()

    cursor.close()

    return years


def get_last_edition(years):
    try:
        last_edition = max(years)
    except:
        last_edition = 0

    return last_edition


def serialize_conference_info(conferences):
    serialized = 0
    for c in conferences.keys():
        conference = conferences.get(c)
        title = c
        source = to_string(conference.get('source'))
        source_id = to_string(conference.get('source_id'))
        rank = conference.get('rank')
        url = conference.get('url')

        editions = get_editions_count(source, source_id)
        years = get_year_editions(source, source_id)
        last_edition = get_last_edition(years)
        if editions >= THRESHOLD_EDITIONS and last_edition >= THRESHOLD_LAST_EDITION:
            create_config_file(str(serialized) + '-' + title, source, source_id, years, editions, url, rank)
            serialized += 1
        else:
            if DEBUG:
                print "conference " + title + " has been filtered " \
                      "(" + "number of editions: " + str(editions) + " [>=" + str(THRESHOLD_EDITIONS) + "], " \
                            "last edition: " + str(last_edition) + " [>=" + str(THRESHOLD_LAST_EDITION) + "])"

    print str(serialized) + " out of " + str(len(conferences.keys())) + " conferences have been selected!"


def main():
    conferences = {}
    input = open(CONFERENCE_FILE, 'r')
    for line in input.readlines():
        info = json.loads(line)
        title = info.get('title')
        source = info.get('source')
        source_id = info.get('source_id')
        rank = info.get('rank')
        url = info.get('url')

        if conferences.get(title):
            c = conferences.get(title)
            new_source = c.get('source')
            new_source.append(source)
            new_source_id = c.get('source_id')
            new_source_id.append(source_id)
            new_url = c.get('url')
            new_url.append(url)
            conferences.update({title: {
                                        'source': new_source,
                                        'source_id': new_source_id,
                                        'url': new_url,
                                        'rank': c.get('rank')
                                      }})
        else:
            conferences.update({title: {'source': [source], 'source_id': [source_id], 'url': [url], 'rank': rank}})

    serialize_conference_info(conferences)


if __name__ == '__main__':
    main()
