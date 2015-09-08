__author__ = 'valerio cosentino'

import mysql.connector
from mysql.connector import errorcode
import db_connection
import json

cnx = mysql.connector.connect(**db_connection.CONFIG)
MINIMUM_NUMBER_OF_EDITIONS = 5
CONFERENCE_FILE = 'data/treated_output.json'
DESTINATION_FOLDER = 'output'


def create_config_file(title, source, source_id, editions, url, rank):
    f = open(DESTINATION_FOLDER + '/' + title.replace('/', '-') + '.txt', 'w')
    f.write('# Name of the conference (REQUIRED)\n')
    f.write('conferenceName=' + title + '\n')
    f.write('#CORE rank (REQUIRED)\n')
    f.write('rank=' + rank + '\n')
    f.write('#total number of editions\n')
    f.write('editions=' + str(editions) + '\n')
    f.write('#dblp urls\n')
    f.write('urls=' + ','.join(url) + '\n')
    f.write('# Queries to get the full graph (REQUIRED)\n')
    f.write("fullNodes=\n"
            "SELECT airn.author_id AS id, airn.author AS label, COUNT(pub.id) AS size\n "
            "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id\n "
            "WHERE source IN (" + source + ") AND source_id IN (" + source_id + ") AND pub.type = 'inproceedings' \n"
            "GROUP BY airn.author_id;\n")
    f.write("fullEdges=\n"
        "SELECT source_author_id AS source, target_author_id AS target, relation_strength AS weight\n"
        "FROM ( SELECT source_authors.author AS source_author_name, source_authors.author_id AS source_author_id, target_authors.author AS target_author_name, target_authors.author_id AS target_author_id, COUNT(*) AS relation_strength, CONCAT(GREATEST(source_authors.author_id, target_authors.author_id), '-', LEAST(source_authors.author_id, target_authors.author_id)) AS connection_id\n"
        "FROM  ( SELECT pub.id AS pub, author, author_id FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id\n"
        "WHERE source IN (" + source + ") AND source_id IN (" + source_id + ") AND pub.type = 'inproceedings' ) AS source_authors\n"
        "JOIN ( SELECT pub.id AS pub, author, author_id\n"
        "FROM dblp_pub_new pub JOIN dblp_authorid_ref_new airn ON pub.id = airn.id\n"
        "WHERE source IN (" + source + ") AND source_id IN (" + source_id + ") AND pub.type = 'inproceedings') AS target_authors\n"
        "ON source_authors.pub = target_authors.pub AND source_authors.author_id <> target_authors.author_id\n"
        "GROUP BY source_authors.author_id, target_authors.author_id) AS x\n"
        "GROUP BY connection_id\n")
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
            "FROM dblp_pub_new " \
            "WHERE source IN (" + source + ") AND source_id IN (" + source_id + ") AND type = 'proceedings'"
    cursor.execute(query)
    row = cursor.fetchone()
    cursor.close()

    return row[0]


def serialize_conference_info(conferences):
    for c in conferences.keys():
        conference = conferences.get(c)
        title = c
        source = to_string(conference.get('source'))
        source_id = to_string(conference.get('source_id'))
        rank = conference.get('rank')
        url = conference.get('url')

        editions = get_editions_count(source, source_id)
        if editions >= MINIMUM_NUMBER_OF_EDITIONS:
            create_config_file(title, source, source_id, editions, url, rank)
        else:
            print "conference " + title + " has less than " + str(MINIMUM_NUMBER_OF_EDITIONS) + " editions!"


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
            conferences.update({title: {
                                        'source': c.get('source').append(source),
                                        'source_id': c.get('source_id').append(source_id),
                                        'url': c.get('url').append(url),
                                        'rank': c.get('rank')
                                      }})
        else:
            conferences.update({title: {'source': [source], 'source_id': [source_id], 'url': [url], 'rank': rank}})

    serialize_conference_info(conferences)


if __name__ == '__main__':
    main()
