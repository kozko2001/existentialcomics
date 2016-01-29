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
docker run -p 80:5000 -v `pwd`:/srv/project --link mongodb:mongo --name comicstrip comicstrip
```

