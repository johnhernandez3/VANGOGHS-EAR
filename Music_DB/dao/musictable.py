from config.dbconfig import pg_config
import psycopg2

class MusicDAO:
    def __init__(self):

        connect_url = "dbname=%s user=%s password=%s" % (pg_config['dbname'],
                                                         pg_config['user'],
                                                         pg_config['passwd'])
        self.conn = psycopg2._connect(connect_url)

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