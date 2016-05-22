import requests


class PushPipeline(object):
    """
    Notify to the push service that a new strip so pushd will notify to all the interested users
    """
    def process_item(self, item, spider):
        key = item['comic']
        msg = "There is a new strip " + key
        data = {
            "msg": msg,
            "data.comic": key,
        }
        r = requests.post("http://push:8081/event/%s" % key, data=data)

        ## Hack to notify also using webpush custom implementation
        r = requests.post("http://localhost:5000/webpush/notify", json = {"topic": key})
