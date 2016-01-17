# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: http://doc.scrapy.org/en/latest/topics/item-pipeline.html
from scrapy.conf import settings
from PIL import Image
from os.path import isfile
from scrapy.exceptions import DropItem
import pymongo


class ExistentialcomicsPipeline(object):
    def process_item(self, item, spider):
        return item


class MergeImagesPipeline(object):
    def process_item(self, item, spider):
        if len(item['images']) > 1:
            return self.process_item_merge(item)
        else:
            item['image'] = '%s/%s' % (settings['IMAGES_STORE'], item['images'][0]['path'])
            return item

    def process_item_merge(self, item):
        image_path = '%s/%s_%s.png' % (settings['IMAGES_STORE'], item['comic'], item['title'])

        ## if file already merge not do it again
        if not isfile(image_path):
            paths = map(lambda i: settings['IMAGES_STORE'] + '/' + i['path'], item['images'])
            images = map(lambda path: Image.open(path), paths)

            ## Create an image of the sum of height
            width = max(map(lambda i: i.width, images))
            height = sum(map(lambda i: i.height, images))
            merge_image = Image.new("RGB", (width, height))

            ## merge all the images in one
            offset = 0
            for image in images:
                merge_image.paste(image, (0, offset))
                offset = offset + image.height

            image_path = '%s/%s_%s.png' % (settings['IMAGES_STORE'], item['comic'], item['title'])
            merge_image.save(image_path)
        item['image'] = image_path

        return item


class MongoPipeline(object):

    def __init__(self):
        connection = pymongo.MongoClient(
            settings['MONGODB_SERVER'],
            settings['MONGODB_PORT']
        )
        db = connection[settings['MONGODB_DB']]
        self.collection = db[settings['MONGODB_COLLECTION']]

    def process_item(self, item, spider):
        print "START"
        print item
        title = item['title']
        comic = item['comic']
        image = item['image']
        subtext = item['subtext']
        url = item['url']

        mongodb_item = self.collection.find_one({
            'comic': comic,
            'title': title
        })

        if not mongodb_item:
            print "inserted?!"
            self.collection.insert({
                'comic': comic,
                'title': title,
                'image': image,
                'text': subtext,
                'url': url
            })

        return item
