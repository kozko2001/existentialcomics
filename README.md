# exitentialcomics

if not docker-machine is created

```
docker-machine create dev
`docker-machine env dev`
```

```
docker build -t comicstrip --rm .

docker run --name mongodb -d mongo
docker run --name redis -d redis
docker run --name pushd  -d --link redis:res -e "REDIS_HOST=res" -e "GCM_KEY=XXX" -p 8080:8081  amsdard/pushd:latest
docker run -e VIRTUAL_HOST=comic.allocsoc.net --link mongodb:mongo --link pushd:push --name comicstrip -d comicstrip
```



For doing the first import just do the normal on the scrape folder :

```scrapy crawl existential```

but when doing for incremental, so the current date of when the data is crawled will be persisted
if not able to extract that information from the page

```scrapy crawl existential -a createdAt=1```
