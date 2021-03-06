import scrapy
from existentialcomics.items import ExistentialcomicsItem
from datetime import datetime
from base import BaseSpider


class DilbertSpider(BaseSpider):
    name = "dilbert"
    allowed_domains = ["dilbert.com"]
    start_urls = [
        "http://dilbert.com"
    ]

    def parse(self, response):
        url = response.xpath("//a[@itemprop='image']/@href")[0].extract()
        yield scrapy.Request(url, callback=self.parse_strip)

    def parse_strip(self, response):
        if not self.existsInDatabase(response.url):
            item = ExistentialcomicsItem()

            date = " ".join(response.xpath("//date/span/text()").extract())
            date = datetime.strptime(date, "%A %B %d, %Y")
            if date.year < 2014:
                return
            days_since_epoch = (date - datetime(1970, 1, 1)).days

            item['comic'] = 'dilbert'
            item['title'] = response.xpath("//date/span/text()").extract_first()
            item['image_urls'] = response.xpath("//a[@itemprop='image']/img/@src").extract()
            item['subtext'] = ""
            item['url'] = response.url
            item['createdAt'] = date
            item['order'] = days_since_epoch
            yield item

            ## going to next page
            next_page = response.xpath("//div[contains(@class, 'nav-left')]/a/@href").extract_first()
            if next_page:
                url = "http://dilbert.com%s" % next_page
                yield scrapy.Request(url, callback=self.parse_strip)
