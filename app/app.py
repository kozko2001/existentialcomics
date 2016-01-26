from flask import Flask, jsonify, send_file,  make_response
import pymongo
from bson import json_util, ObjectId
import json
import gridfs

app = Flask(__name__)

# mongo db connection
connection = pymongo.MongoClient('mongo')
db = connection['comics']
comics = db['comics']
fs = gridfs.GridFSBucket(db)


class JSONEncoder(json.JSONEncoder):
    def default(self, o):
        if isinstance(o, ObjectId):
            return str(o)
        return json.JSONEncoder.default(self, o)


@app.route("/comics")
def listComics():
    result = {
        'comics': comics.distinct('comic')
    }
    return jsonify(result)


@app.route("/comics/<comic>")
def get_comic(comic):
    projection = ['comic', 'title', 'text', 'url', 'order']
    result = comics.find({
        'comic': comic
    }, projection)

    data = json.loads(JSONEncoder().encode(list(result)))  # Ugly hack to remove objectid
    result = {
        'result': data
    }

    return jsonify(result)


@app.route('/comics/image/<document_id>')
def get_image(document_id):
    doc = comics.find_one({
        '_id': ObjectId(document_id)
    })

    if doc:
        grid_out = fs.open_download_stream(doc['file_id'])
        contents = grid_out.read()
        response = make_response(contents)

        response.headers['Content-Type'] = 'image/png'
        return response
    else:
        abort(404)


if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)
