# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: http://doc.scrapy.org/en/latest/topics/item-pipeline.html
from scrapy.conf import settings
from PIL import Image
from os.path import isfile
import pymongo
import gridfs
import requests


class ExistentialcomicsPipeline(object):
    def process_item(self, item, spider):
        return item


class MergeImagesPipeline(object):
    def process_item(self, item, spider):
        if len(item['images']) > 1:
            item = self.process_item_merge(item)
        else:
            item['image'] = '%s/%s' % (settings['IMAGES_STORE'], item['images'][0]['path'])

        return self.create_thumbnail(item)

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

    def create_thumbnail(self, item):
        image = Image.open(item['image'])
        print "image original size %i %i " % (image.width, image.height)
        min_axis = min(image.height, image.width)
        print "min axis %i" % min_axis

        thumbnail_size = 256.0
        p = thumbnail_size / min_axis
        print "mult: %f" % p

        size = (image.width * p, image.height * p)
        print size

        image.thumbnail(size)

        image_path = '%s/thumbnail_%s_%s.png' % (settings['IMAGES_STORE'], item['comic'], item['title'])
        image.save(image_path, "JPEG", quality=90, optimize=True, progressive=True)
        item["thumbnail"] = image_path

        return item


class MongoPipeline(object):

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
                'thumbnail': thumbnail_file_id
            })

        return item


class PushPipeline(object):

    def process_item(self, item, spider):
        key = item['comic']
        msg = "There is a new strip " + key
        data = {
            "msg": msg,
            "data.comic": key,
        }
        r = requests.post("http://push:8081/event/%s" % key, data=data)
        print r.status_code
