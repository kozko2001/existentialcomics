#!/bin/bash

spiders=(existential xkcd dilbert commitstrip cynadine)

for spider in ${spiders[*]}
do
  echo "Start spider $spider"
  /usr/local/bin/scrapy crawl -L ERROR --logfile=/var/log/scrapping.log -a createdAt=1 $spider 

done
