import scrapy
from existentialcomics.items import ExistentialcomicsItem

class ExistentialSpider(scrapy.Spider):
    name = "existential"
    allowed_domains = ["existentialcomics.com"]
    start_urls = [
        "http://existentialcomics.com/comic/1"
    ]

    def parse(self, response):
    	item = ExistentialcomicsItem()

        item['title'] = response.xpath("//h3/text()").extract()[0]
        item['image_urls'] = response.xpath("//img[@class='comicImg']/@src").extract()
        item['subtext'] = response.xpath("string(//div[@id='explainHidden'])").extract_first()
        item['url'] = response.url
        yield item

        ## going to next page
        next_page = response.xpath("//area[@alt='next']/@href").extract_first()

        if next_page:
        	url = "http://existentialcomics.com%s" % next_page
        	yield scrapy.Request(url, callback=self.parse)