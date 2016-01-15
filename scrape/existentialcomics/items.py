# Define here the models for your scraped items
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html

from scrapy.item import Item, Field

class ExistentialcomicsItem(Item):
    title = Field()
    url = Field()
    subtext = Field()
    image_urls = Field()
    images = Field()

    pass
