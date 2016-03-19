from scrapy.conf import settings
from PIL import Image
from os.path import isfile


class MergeImagesPipeline(object):
    """
    Pipeline to merge multiple images into on single image

    This is made specifically for the existential comics crawler
    where the comic is made from multiple images in vertical.

    If the item['images'] size from the pipeline is > 1 then we
    do the merge of the multiple images, if not this pipeline does
    nothing
    """

    def process_item(self, item, spider):
        if len(item['images']) > 1:
            item = self.process_item_merge(item)
        else:
            item['image'] = '%s/%s' % (settings['IMAGES_STORE'], item['images'][0]['path'])

        return item

    def process_item_merge(self, item):
        image_path = '%s/%s_%s.png' % (settings['IMAGES_STORE'], item['comic'], item['title'])

        # if file already merge not do it again
        if not isfile(image_path):
            paths = map(lambda i: settings['IMAGES_STORE'] + '/' + i['path'], item['images'])
            images = map(lambda path: Image.open(path), paths)

            # Create an image of the sum of height
            width = max(map(lambda i: i.width, images))
            height = sum(map(lambda i: i.height, images))
            merge_image = Image.new("RGB", (width, height))

            # merge all the images in one
            offset = 0
            for image in images:
                merge_image.paste(image, (0, offset))
                offset = offset + image.height

            image_path = '%s/%s_%s.png' % (settings['IMAGES_STORE'], item['comic'], item['title'])
            merge_image.save(image_path)
        item['image'] = image_path

        return item
