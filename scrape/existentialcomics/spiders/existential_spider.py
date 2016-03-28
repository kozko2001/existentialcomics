import scrapy
import re
from existentialcomics.items import ExistentialcomicsItem
from base import BaseSpider


class ExistentialSpider(BaseSpider):
    name = "existential"
    allowed_domains = ["existentialcomics.com"]
    start_urls = [
        "http://existentialcomics.com/comic/1"
    ]

    def parse(self, response):
        ## going to next page
        last_page = response.xpath("//area[@alt='last']/@href").extract_first()

        if last_page:
            url = "http://existentialcomics.com%s" % last_page
            yield scrapy.Request(url, callback=self.parse_backwards)

    def parse_backwards(self, response):
        if not self.existsInDatabase(response.url):
            m = re.search('\/(\d+)', response.url)
            order = m.group(1)

            item = ExistentialcomicsItem()

            item['comic'] = 'existentialcomics'
            item['title'] = response.xpath("//h3/text()").extract()[0]
            item['image_urls'] = response.xpath("//img[@class='comicImg']/@src").extract()
            item['subtext'] = response.xpath("string(//div[@id='explainHidden'])").extract_first()
            item['url'] = response.url
            item['order'] = int(order)

            yield item

            ## going to prev page
            prev_page = response.xpath("//area[@alt='previous']/@href").extract_first()

            if prev_page:
                url = "http://existentialcomics.com%s" % prev_page
                yield scrapy.Request(url, callback=self.parse_backwards)
