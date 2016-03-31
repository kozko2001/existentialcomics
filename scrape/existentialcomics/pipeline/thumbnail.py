from scrapy.conf import settings
from PIL import Image


class ThumbnailPipeline(object):
    """
    This pipeline creates the thumbnail image from the images collected during the
    crawling and store in the filesystem and fills the item['thumbnail'] with the
    file store
    """

    def process_item(self, item, spider):
        image = Image.open(item['image'])
        min_axis = min(image.height, image.width)

        thumbnail_size = 256.0
        p = thumbnail_size / min_axis

        size = (image.width * p, image.height * p)

        image.thumbnail(size)
        title = item['title'] or item["order"]

        image_path = '%s/thumbnail_%s_%s.png' % (settings['IMAGES_STORE'], item['comic'], title)
        image.save(image_path, "JPEG", quality=90, optimize=True, progressive=True)
        item["thumbnail"] = image_path

        return item
