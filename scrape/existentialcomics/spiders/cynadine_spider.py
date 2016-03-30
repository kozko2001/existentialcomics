import scrapy
from existentialcomics.items import ExistentialcomicsItem
from datetime import datetime
from base import BaseSpider


class CynadineSpider(BaseSpider):
    name = "cynadine"
    allowed_domains = ["explosm.net"]
    start_urls = [
        "http://explosm.net/"
    ]

    def parse(self, response):
        if not self.existsInDatabase(response.url):
            item = ExistentialcomicsItem()

            date = response.xpath("//*[contains(@class, 'meta-data')]/div/h3/a/text()").extract_first()
            date = datetime.strptime(date, "%Y.%m.%d")
            if date.year < 2014:
                return

            comic_id = int(response.xpath("//*[contains(@class, 'meta-data')]//@data-id").extract_first())

            image_url = response.xpath("//img[@id='main-comic']/@src").extract_first()
            if not image_url:
                image_url = response.xpath("//img[@id='featured-comic']/@src").extract_first()

            item['comic'] = 'cynadine'
            item['title'] = str(comic_id)
            item['image_urls'] = ["http:" + image_url]
            item['subtext'] = ""
            item['url'] = response.url
            item['createdAt'] = date
            item['order'] = comic_id
            yield item

            ## going to next page
            prev = response.xpath("//a[contains(@class, 'previous-comic')]/@href").extract_first()
            next_page = "http://explosm.net/%s" % prev

            if next_page:
                yield scrapy.Request(next_page, callback=self.parse)
