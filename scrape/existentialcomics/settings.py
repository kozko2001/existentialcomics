# Scrapy settings for existentialcomics project
#
# For simplicity, this file contains only the most important settings by
# default. All the other settings are documented here:
#
#     http://doc.scrapy.org/en/latest/topics/settings.html
#

BOT_NAME = 'existentialcomics'

SPIDER_MODULES = ['existentialcomics.spiders']
NEWSPIDER_MODULE = 'existentialcomics.spiders'

# image downloading configuration
ITEM_PIPELINES = {
    'scrapy.pipelines.images.ImagesPipeline': 1,
    'existentialcomics.pipeline.merge.MergeImagesPipeline': 2,
    'existentialcomics.pipeline.thumbnail.ThumbnailPipeline': 3,
    'existentialcomics.pipeline.created.CreatedPipeline': 4,
    'existentialcomics.pipeline.mongo.MongoPipeline':  10,
    'existentialcomics.pipeline.push.PushPipeline':  20,
}

IMAGES_STORE = './images'
IMAGES_EXPIRES = 90

MONGODB_SERVER = "mongo"
MONGODB_PORT = 27017
MONGODB_DB = "comics"
MONGODB_COLLECTION = "comics"

# Crawl responsibly by identifying yourself (and your website) on the user-agent
#USER_AGENT = 'existentialcomics (+http://www.yourdomain.com)'
