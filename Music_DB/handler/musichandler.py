from flask import jsonify
from dao.musictable import MusicDAO

dao = MusicDAO

class MusicHandler:
    def build_music_dict(self, row):
        result = {}
        result['mid'] = row[0]
        result['songname'] = row[1]
        result['songartist'] = row[2]
        return result
    
    def build_music_attributes(self, mid, songname, songartist):
        result = {}
        result['mid'] = mid
        result['songname'] = songname
        result['songartist'] = songartist
        return result
    
    def getAllMusic(self):
        music_list = dao.getAllMusic()
        result_list = []
        for row in music_list:
            result = self.build_music_dict(row)
            result_list.append(result)
        return jsonify(Music=result_list)

    def getMusicById(self, mid):
        row = dao.getMusicById(mid)
        if not row:
            return jsonify(Error="Music Not Found"), 404
        else:
            Music = self.build_music_dict(row)
        return jsonify(Music=Music)

    def searchMusic(self, args):
        dao = MusicDAO
        songname = args.get('songname')
        songartist = args.get('songartist')
        music_list = []
        if (len(args) == 2) and songname and songartist:
            music_list = dao.getMusicBySongnameandSongartist(songname, songartist)
        elif (len(args) == 1) and songname:
            music_list = dao.getMusicBySongname(songname)
        elif (len(args) == 1) and songartist:
            music_list = dao.getMusicBySongartist(songartist)
        else:
            return jsonify(Error="Malformed query string"), 400
        result_list = []
        for row in music_list:
            result = self.build_music_dict(row)
            result_list.append(result)
        return jsonify(Music=result_list)
    
    def insertMusicJson(self, json):
        songname = json['songname']
        songartist = json['songartist']
        if songname and songartist:
            mid = dao.insert(songname, songartist)
            result = self.build_music_attributes(mid, songname, songartist)
            return jsonify(Music=result), 201
        else:
            return jsonify(Error="Unexpected attributes in post request"), 400

    def updateMusic(self, mid, form):
        if not dao.getMusicById(mid):
            return jsonify(Error="Music not found."), 404
        else:
            if len(form) != 2:
                return jsonify(Error="Malformed update request"), 400
            else:
                songname = form['songname']
                songartist = form['songartist']
                if songname and songartist:
                    dao.update(mid, songname, songartist)
                    result = self.build_music_attributes(mid, songname, songartist)
                    return jsonify(Music=result), 200
                else:
                    return jsonify(Error="Unexpected attributes in update request"), 400

    def deleteMusic(self, mid):
        if not dao.getMusicById(mid):
            return jsonify(Error="Music not found."), 404
        else:
            dao.delete(mid)
            return jsonify(DeleteStatus="OK"), 200