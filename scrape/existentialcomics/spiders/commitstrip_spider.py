import scrapy
from existentialcomics.items import ExistentialcomicsItem
from datetime import datetime
from base import BaseSpider


class CommitStripSpider(BaseSpider):
    name = "commitstrip"
    allowed_domains = ["commitstrip.com"]
    start_urls = [
        "http://www.commitstrip.com/en/?"
    ]

    def parse(self, response):
        url = response.xpath("//div[@class='excerpt']/section/a/@href").extract_first()
        yield scrapy.Request(url, callback=self.parse_strip)

    def parse_strip(self, response):
        if not self.existsInDatabase(response.url):
            item = ExistentialcomicsItem()

            date = response.xpath("//article/header//time/@datetime").extract_first().split('+')[0]
            date = datetime.strptime(date, "%Y-%m-%dT%H:%M:%S")
            if date.year < 2014:
                return
            days_since_epoch = (date - datetime(1970, 1, 1)).days

            item['comic'] = 'commitstrip'
            item['title'] = response.xpath("//article/header/h1/text()").extract_first()
            item['image_urls'] = response.xpath("//div[@class='entry-content']//img/@src").extract()
            item['subtext'] = ""
            item['url'] = response.url
            item['createdAt'] = date
            item['order'] = days_since_epoch
            yield item

            ## going to next page
            url = response.xpath("//span[@class='nav-previous']//a/@href").extract_first()
            if url:
                yield scrapy.Request(url, callback=self.parse_strip)
