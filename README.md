# exitentialcomics

if not docker-machine is created

```
docker-machine create dev
`docker-machine env dev`
```

```
docker build -t comicstrip --rm .

docker run --name mongodb -d mongo

docker run -p 80:5000 -v `pwd`:/srv/project --link mongodb:mongo --name comicstrip comicstrip
```

