import scrapy
from existentialcomics.items import ExistentialcomicsItem


class XKCDSpider(scrapy.Spider):
    name = "xkcd"
    allowed_domains = ["xkcd.com"]
    start_urls = [
        "http://xkcd.com/1/"
    ]

    def parse(self, response):
        item = ExistentialcomicsItem()

        images = response.xpath("//div[@id='comic']//img/@src").extract()
        images = map(lambda url: "http://" + url[2:], images)

        item['comic'] = 'xkcd'
        item['title'] = response.xpath("//div[@id='ctitle']/text()").extract_first()
        item['image_urls'] = images
        item['subtext'] = ""
        item['url'] = response.url
        yield item

        ## going to next page
        next_page = response.xpath("//a[@rel='next']/@href").extract_first()
        print next_page
        if next_page:
            url = "http://xkcd.com%s" % next_page
            print url
            yield scrapy.Request(url, callback=self.parse)
