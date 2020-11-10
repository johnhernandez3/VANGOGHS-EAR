from dbconfig import pg_config
import mysql.connector

#To make one: docker run  --name ms -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password mysql 

#bash into the container to run mysql, allows you to enter the bash
#docker exec -it ms bash

#run mysql, allows you to run the db
#mysql -u root -ppassword (no space between -p and password) 

#To use database:
#use musicdb

con = mysql.connector.connect(
            host = "127.0.0.1",
            user = "root",
            password = "imp",
            database = "musicdb",
            port = "3306"
        )


cur = con.cursor()
        
#cur.execute("insert into music (songname, songartist) values (%s, %s);", ('test4','test5',))
#cur.execute("select songname,songartist from music where songname = %s;", ('test2',))
cur.execute("")
cur.execute("select * from music")

rows = cur.fetchall()

for r in rows:
    print(f" id = {r[0]} songname = {r[1]} songartist = {r[2]}")

con.commit()

print("Connected")
cur.close()
con.close()


class MusicDAO:
    def __init__(self):

        con = mysql.connector.connect(
            host = "dbuser",
            user = "root",
            password = "imp",
            database = "musicdb",
            port = 3306
        )
        
        print("Connected")
        con.close()

################################################################################
#                           All  Categories                                    #
################################################################################
#TODO: this can be more performant using generators
# instead of returning a list you can use 'yield'
# inside the for loop and it will not waste as much memory as a list

    def getAllMusic(self):
        cursor = self.conn.cursor()
        query = "select * from music;"
        cursor.execute(query)
        result = []
        for row in cursor:
            result.append(row)
        return result

################################################################################
#                    Individual  Categories                                    #
################################################################################

    def getMusicById(self, mid):
        cursor = self.conn.cursor()
        query = "select * from music where mid = %s;"
        cursor.execute(query, (mid,))
        result = cursor.fetchone()
        return result

    def getMusicBySongname(self, songname):
        cursor = self.conn.cursor()
        query = "select * from music where songname = %s;"
        cursor.execute(query, (songname,))
        result = []
        for row in cursor:
            result.append(row)
        return result

    def getMusicBySongartist(self, songartist):
        cursor = self.conn.cursor()
        query = "select * from music where songartist = %s;"
        cursor.execute(query, (songartist,))
        result = []
        for row in cursor:
            result.append(row)
        return result

################################################################################
#                        Double  Categories                                    #
################################################################################

    def getMusicBySongnameandSongartist(self, songname, songartist):
        cursor = self.conn.cursor()
        query = "select * from music where songname = %s and songartist = %s;"
        cursor.execute(query, (songname, songartist))
        result = []
        for row in cursor:
            result.append(row)
        return result

################################################################################
#                  Insert, Update, and Delete                                  #
################################################################################

    def insert(self, songname, songartist):
        cursor = self.conn.cursor()
        query = "insert into music(songname, songartist) values (%s, %s) returning mid;"
        cursor.execute(query, (songname, songartist))
        mid = cursor.fetchone()[0]
        self.conn.commit()
        return mid

    def update(self, mid, songname, songartist):
        cursor = self.conn.cursor()
        query = "update music set songname = %s, songartist = %s where mid = %s;"
        cursor.execute(query, (songname, songartist, mid,))
        self.conn.commit()
        return mid

    def delete(self, mid):
        cursor = self.conn.cursor()
        query = "delete from music where mid = %s;"
        cursor.execute(query, (mid,))
        self.conn.commit()
        return mid