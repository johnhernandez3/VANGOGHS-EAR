from flask import jsonify
from dao.chordtable import ChordDAO

dao = ChordDAO

class ChordHandler:
    def build_chord_dict(self, row):
        result = {}
        result['cid'] = row[0]
        result['chordname'] = row[1]
        return result
    
    def build_chord_attributes(self, cid, chordname):
        result = {}
        result['cid'] = cid
        result['chordname'] = chordname
        return result
    
    def getAllChord(self):
        chord_list = dao.getAllChord()
        result_list = []
        for row in chord_list:
            result = self.build_chord_dict(row)
            result_list.append(result)
        return jsonify(Chord=result_list)

    def getChordById(self, cid):
        row = dao.getChordById(cid)
        if not row:
            return jsonify(Error="Chord Not Found"), 404
        else:
            Chord = self.build_chord_dict(row)
        return jsonify(Chord=Chord)

    def searchMusic(self, args):
        dao = ChordDAO
        chordname = args.get('chordname')
        chord_list = []
        if (len(args) == 1) and chordname:
            chord_list = dao.getChordByChordname(chordname)
        else:
            return jsonify(Error="Malformed query string"), 400
        result_list = []
        for row in chord_list:
            result = self.build_chord_dict(row)
            result_list.append(result)
        return jsonify(Chord=result_list)
    
    def insertChordJson(self, json):
        chordname = json['chordname']
        if songname:
            cid = dao.insert(chordname)
            result = self.build_chord_attributes(cid, chordname)
            return jsonify(Chord=result), 201
        else:
            return jsonify(Error="Unexpected attributes in post request"), 400

    def updateChord(self, cid, form):
        if not dao.getChordById(cid):
            return jsonify(Error="Music not found."), 404
        else:
            if len(form) != 2:
                return jsonify(Error="Malformed update request"), 400
            else:
                chordname = form['chordname']
                if chordname:
                    dao.update(cid, chordname)
                    result = self.build_chord_attributes(cid, chordname)
                    return jsonify(Chord=result), 200
                else:
                    return jsonify(Error="Unexpected attributes in update request"), 400

    def deleteChord(self, cid):
        if not dao.getChordById(cid):
            return jsonify(Error="Music not found."), 404
        else:
            dao.delete(cid)
            return jsonify(DeleteStatus="OK"), 200