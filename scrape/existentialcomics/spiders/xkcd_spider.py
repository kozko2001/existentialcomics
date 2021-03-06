import scrapy
from existentialcomics.items import ExistentialcomicsItem
import re
from base import BaseSpider


class XKCDSpider(BaseSpider):
    name = "xkcd"
    allowed_domains = ["xkcd.com"]
    start_urls = [
        "http://xkcd.com/"
    ]

    def parse(self, response):
        url = self.getPermanentUrl(response)

        if not self.existsInDatabase(url):
            item = ExistentialcomicsItem()

            images = response.xpath("//div[@id='comic']//img/@src").extract()
            images = map(lambda url: "http://" + url[2:], images)

            item['comic'] = 'xkcd'
            item['title'] = response.xpath("//div[@id='ctitle']/text()").extract_first()
            item['image_urls'] = images
            item['subtext'] = response.xpath("//div[@id='comic']//img/@title").extract_first()
            item['url'] = url
            item['order'] = self.getOrderFromUrl(url)
            yield item

            # going to prev page
            prev_page = response.xpath("//a[@rel='prev']/@href").extract_first()
            if prev_page:
                url = "http://xkcd.com%s" % prev_page
                yield scrapy.Request(url, callback=self.parse)

    def getPermanentUrl(self, response):
        text = "".join(response.xpath("//div[@class='box']/text()").extract())
        urls = re.findall('http[s]?://(?:[a-zA-Z]|[0-9]|[$-_@.&+]|[!*\(\),]|(?:%[0-9a-fA-F][0-9a-fA-F]))+', text)

        return urls[0]

    def getOrderFromUrl(self, url):
        m = re.search('\/(\d+)', url)
        return int(m.group(1))
