from datetime import datetime


class CreatedPipeline(object):
    """
    Adds the current timestamp to the createdAt file
    """

    def process_item(self, item, spider):
        enable = spider.createdAt > 0
        if enable and not 'createdAt' in item:
            item['createdAt'] = datetime.now()
        return item
