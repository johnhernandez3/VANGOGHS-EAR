from config.dbconfig import pg_config
import psycopg2

class ChordDAO:
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
    def getAllChord(self):
        cursor = self.conn.cursor()
        query = "select * from chord;"
        cursor.execute(query)
        result = []
        for row in cursor:
            result.append(row)
        return result

################################################################################
#                    Individual  Categories                                    #
################################################################################

    def getChordById(self, cid):
        cursor = self.conn.cursor()
        query = "select * from chord where cid = %s;"
        cursor.execute(query, (mid,))
        result = cursor.fetchone()
        return result

    def getChordByChordname(self, chordname):
        cursor = self.conn.cursor()
        query = "select * from chord where chordname = %s;"
        cursor.execute(query, (chordname,))
        result = []
        for row in cursor:
            result.append(row)
        return result

################################################################################
#                  Insert, Update, and Delete                                  #
################################################################################

    def insert(self, chordname):
        cursor = self.conn.cursor()
        query = "insert into music(chordname) values (%s) returning cid;"
        cursor.execute(query, (songname, songartist))
        cid = cursor.fetchone()[0]
        self.conn.commit()
        return cid

    def update(self, cid, chordname):
        cursor = self.conn.cursor()
        query = "update music set chordname = %s where cid = %s;"
        cursor.execute(query, (chordname, cid,))
        self.conn.commit()
        return cid

    def delete(self, cid):
        cursor = self.conn.cursor()
        query = "delete from chord where cid = %s;"
        cursor.execute(query, (cid,))
        self.conn.commit()
        return cid