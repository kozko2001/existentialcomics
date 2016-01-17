from flask import Flask, jsonify, send_file
import pymongo
from bson import json_util, ObjectId
import json

app = Flask(__name__)

# mongo db connection
connection = pymongo.MongoClient('mongo')
db = connection['comics']
comics = db['comics']


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
    projection = ['comic', 'title', 'text', 'url']
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
        path = '/srv/project/scrape/%s' % (doc['image'])
        print path
        return send_file(path, mimetype='image/png')
    else:
        abort(404)


if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)
