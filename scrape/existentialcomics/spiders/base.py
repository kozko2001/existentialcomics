import scrapy
import pymongo
from scrapy.conf import settings


class BaseSpider(scrapy.Spider):

    def __init__(self, createdAt=0, *args, **kwargs):
        self.createdAt = int(createdAt)
        super(BaseSpider, self).__init__(*args, **kwargs)

    def existsInDatabase(self, url):
        """
        Detects if the current url we want to parse already was
        parsed and stored in the mongo database
        """
        connection = pymongo.MongoClient(
            settings['MONGODB_SERVER'],
            settings['MONGODB_PORT']
        )
        db = connection[settings['MONGODB_DB']]
        collection = db[settings['MONGODB_COLLECTION']]

        db_comic = collection.find_one({
            'url': url
        })
        return True if db_comic else False
