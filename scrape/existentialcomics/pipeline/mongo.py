from scrapy.conf import settings
import pymongo
import gridfs


class MongoPipeline(object):
    """
    Pipeline were we store the item in the database
    """

    def __init__(self):
        connection = pymongo.MongoClient(
            settings['MONGODB_SERVER'],
            settings['MONGODB_PORT']
        )
        self.db = connection[settings['MONGODB_DB']]
        self.collection = self.db[settings['MONGODB_COLLECTION']]

    def process_item(self, item, spider):
        title = item['title']
        comic = item['comic']
        image = item['image']
        thumbnail = item['thumbnail']
        subtext = item['subtext']
        url = item['url']
        order = item['order']
        datetime = item['createdAt'] if 'createdAt' in item else None

        mongodb_item = self.collection.find_one({
            'comic': comic,
            'title': title
        })

        if not mongodb_item:
            fs = gridfs.GridFSBucket(self.db)
            file_id = fs.upload_from_stream(image, open(image))
            thumbnail_file_id = fs.upload_from_stream(image, open(thumbnail))

            self.collection.insert({
                'comic': comic,
                'title': title,
                'image': image,
                'file_id': file_id,
                'text': subtext,
                'url': url,
                'order': order,
                'createdAt': datetime,
                'thumbnail': thumbnail_file_id
            })

        return item
