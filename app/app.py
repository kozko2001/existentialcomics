from flask import Flask, jsonify, send_file, make_response, request, abort
import pymongo
from bson import json_util, ObjectId
import json
import gridfs
import requests
from itertools import takewhile
from flask.ext.cors import CORS
import os


app = Flask(__name__)
CORS(app)


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
    collection = db['comics_info']
    cursor = collection.find({})
    all_comics = json.loads(JSONEncoder().encode(list(cursor)))  # Ugly hack to remove objectid

    result = {
        'comics': all_comics
    }
    return jsonify(result)


@app.route("/comics/<comic>")
def get_comic(comic):
    return get_comic_until_id(comic, None)


@app.route("/comics/<comic>/<last_id>")
def get_comic_until_id(comic, last_id):
    """
    Get the data from the mongodb, if client send us which is the last id they
    have received, we only send back the new data

    {
        result: [ {strip1}, {strip2}, ...]
    }
    """
    projection = ['comic', 'title', 'text', 'url', 'order']
    try:
        last_id = ObjectId(last_id)
    except:
        last_id = None

    cursor = comics.find({'comic': comic}, projection).sort('order', pymongo.DESCENDING)
    cursor = takewhile(lambda x: x['_id'] != last_id, cursor)

    data = json.loads(JSONEncoder().encode(list(cursor)))  # Ugly hack to remove objectid
    result = {
        'result': data
    }
    return jsonify(result)


@app.route('/comics/image/<document_id>')
def get_image(document_id):
    return _get_image(document_id, "file_id")


@app.route('/comics/thumbnail/<document_id>')
def get_thumbnail(document_id):
    return _get_image(document_id, "thumbnail")

@app.route('/comics/timeline', methods=['POST'])
def get_timeline():
    content = request.get_json()
    print content

    comics = content.get("comics", [])
    last_id = content.get("last_id", None)

    find_key = {}
    projection = ['comic', 'title', 'text', 'url', 'order']

    if comics:
        find_key["comic"] = {"$in": comics}

    collection = db.comics;
    cursor = collection.find(find_key, projection).sort([("createdAt", -1)]).limit(100)

    if last_id:
        last_id = ObjectId(last_id)

    cursor = takewhile(lambda x: x['_id'] != last_id, cursor)

    data = json.loads(JSONEncoder().encode(list(cursor)))  # Ugly hack to remove objectid
    result = {
        'result': data
    }
    return jsonify(result)



def _get_image(document_id, image_field):
    doc = comics.find_one({
        '_id': ObjectId(document_id)
    })

    if doc:
        grid_out = fs.open_download_stream(doc[image_field])
        contents = grid_out.read()
        response = make_response(contents)

        response.headers['Content-Type'] = 'image/png'
        return response
    else:
        return abort(404)


@app.route('/register', methods=['POST'])
def register():
    content = request.get_json()

    data = {
        'proto': content['proto'],
        'token': content['token']
    }

    print data

    r = requests.post("http://push:8081/subscribers", data=data)
    json = r.json()

    return jsonify({"id": json['id']})


@app.route("/ping/<user_id>")
def ping(user_id):
    data = {
        "badge": 0
    }
    r = requests.post("http://push:8081/subscriber/" + user_id, data=data)

    return jsonify({"result": "ok"})


@app.route("/subscribe/<user_id>/<topic>")
def subscribe(user_id, topic):
    r = requests.post("http://push:8081/subscriber/%s/subscriptions/%s" % (user_id, topic))
    print r

    return jsonify({"result": "ok"})


@app.route("/unsubscribe/<user_id>/<topic>")
def unsubscribe(user_id, topic):
    r = requests.delete("http://push:8081/subscriber/%s/subscriptions/%s" % (user_id, topic))
    print r

    return jsonify({"result": "ok"})


@app.route("/webpush/register", methods=['POST'])
def webpush_register():
    content = request.get_json()

    print request
    print content

    valid = "endpoint" in content and "topics" in content
    if valid:
        collection = db.webpush;

        key = {"token": content["endpoint"]}
        data = {"$set": {"topics": content["topics"], "type": "webpush-chrome"}}

        collection.update_one(key, data, upsert=True)

        return jsonify({"result": "ok"})
    else:
        return jsonify({"result": "ko"})


@app.route("/webpush/notify", methods=['POST'])
def webpush_notify():
    content = request.get_json()

    topic = content["topic"]
    key = os.environ['GCM_KEY']

    collection = db.webpush;
    cursor = collection.find({"topics": topic, "type": "webpush-chrome"})
    tokens = map(lambda i: i["token"].split("/")[-1], cursor)


    headers = {
        "Authorization": "key=" + key,
        "Content-Type": "application/json"
    }

    data = {
        "registration_ids": tokens
    }

    print headers
    print data

    r = requests.post("https://android.googleapis.com/gcm/send", json=data, headers=headers)
    _json = r.text

    print _json

    return jsonify({"result": "ok"})


if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True)
