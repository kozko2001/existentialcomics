import scrapy

class ExistentialSpider(scrapy.Spider):
    name = "existential"
    allowed_domains = ["existentialcomics.com"]
    start_urls = [
        "http://existentialcomics.com/comic/1"
    ]

    def parse(self, response):

        title = response.xpath("//h3/text()").extract()
        images = response.xpath("//img[@class='comicImg']/@src")
        
        print title, images