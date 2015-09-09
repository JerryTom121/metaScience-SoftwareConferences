__author__ = 'valerio cosentino'

#This script matches the acronyms of the Computer Software (code 0803) conferences listed at http://www.core.edu.au/index.php/ with the conferences in DBLP.
#However, due to many mismatches between the two web-sites, the output file (output.json) has to be manually checked and refined.

import csv
import mysql.connector
from mysql.connector import errorcode
import re
import db_connection
import json

#input/output data
CVS_PATH = "data/CORE.csv"
OUTPUT = 'data/output.json'

cnx = mysql.connector.connect(**db_connection.CONFIG)


def process_title(title):
    return re.sub('\(.*\)', '', title)


def find_match(acronym, title, rank, output):
    cursor = cnx.cursor()
    query = "SELECT SUBSTRING_INDEX(url, '/', 3) AS conference_page, source, source_id " \
            "FROM dblp_pub_new " \
            "WHERE source = %s AND type = 'proceedings' " \
            "GROUP BY conference_page"
    arguments = [acronym]
    cursor.execute(query, arguments)
    row = cursor.fetchone()

    if not row:
        print 'no match for ' + title + '(' + acronym + ') ' + rank
    else:
        while row:
            dblp_url = row[0]
            source = row[1]
            source_id = row[2]

            data = {'title': title, 'url': dblp_url, 'source': source, 'source_id': source_id, 'rank': rank}
            output.write(json.dumps(data) + '\n')

            row = cursor.fetchone()

    cursor.close()


def main():
    output = open(OUTPUT, 'w')

    input = open(CVS_PATH, 'rb')
    reader = csv.reader(input)

    for row in reader:
        title = process_title(row[1])
        acronym = row[2]
        rank = row[4]

        if rank != 'Australasian' and 'workshop' not in title.lower():
            find_match(acronym, title, rank, output)

    input.close()
    output.close()

if __name__ == "__main__":
    main()
