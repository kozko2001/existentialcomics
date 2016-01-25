import scrapy
from existentialcomics.items import ExistentialcomicsItem


class DilbertSpider(scrapy.Spider):
    name = "dilbert"
    allowed_domains = ["dilbert.com"]
    start_urls = [
        "http://dilbert.com/strip/2014-04-26"
    ]

    def parse(self, response):
        item = ExistentialcomicsItem()

        images = response.xpath("//div[@id='comic']//img/@src").extract()
        images = map(lambda url: "http://" + url[2:], images)

        item['comic'] = 'dilbert'
        item['title'] = response.xpath("//date/span/text()").extract_first()
        item['image_urls'] = response.xpath("//a[@itemprop='image']/img/@src").extract()
        item['subtext'] = ""
        item['url'] = response.url
        yield item

        ## going to next page
        next_page = response.xpath("//div[contains(@class, 'nav-right')]/a/@href").extract_first()
        print next_page
        if next_page:
            url = "http://dilbert.com%s" % next_page
            print url
            yield scrapy.Request(url, callback=self.parse)
